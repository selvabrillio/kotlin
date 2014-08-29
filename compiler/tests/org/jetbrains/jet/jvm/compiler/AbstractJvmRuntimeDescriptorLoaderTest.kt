/*
 * Copyright 2010-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.jvm.compiler

import org.jetbrains.jet.codegen.GenerationUtils
import org.jetbrains.jet.JetTestUtils
import java.io.File
import org.jetbrains.jet.ConfigurationKind
import org.jetbrains.jet.test.util.RecursiveDescriptorComparator
import org.jetbrains.jet.test.TestCaseWithTmpdir
import org.jetbrains.jet.cli.common.output.outputUtils.writeAllTo
import org.jetbrains.kotlin.util.sure
import org.jetbrains.jet.lang.descriptors.*
import org.jetbrains.jet.lang.resolve.scopes.JetScope
import org.jetbrains.jet.lang.types.TypeSubstitutor
import org.jetbrains.jet.lang.resolve.java.JvmAbi
import org.jetbrains.jet.lang.resolve.kotlin.reflect.ReflectKotlinClass
import org.jetbrains.jet.lang.resolve.kotlin.header.KotlinClassHeader
import org.jetbrains.jet.lang.resolve.scopes.WritableScopeImpl
import org.jetbrains.jet.lang.resolve.scopes.RedeclarationHandler
import org.jetbrains.jet.lang.resolve.scopes.WritableScope.LockLevel
import org.jetbrains.jet.renderer.DescriptorRendererBuilder
import org.jetbrains.jet.lang.resolve.name.FqName
import org.jetbrains.jet.renderer.DescriptorRenderer
import org.jetbrains.jet.lang.resolve.resolveTopLevelClass
import org.jetbrains.jet.lang.resolve.kotlin.reflect.createModule
import java.net.URLClassLoader
import com.intellij.openapi.util.io.FileUtil
import java.util.regex.Pattern
import org.jetbrains.jet.lang.resolve.name.Name
import org.jetbrains.jet.JetTestUtils.TestFileFactoryNoModules
import org.jetbrains.jet.test.util.RecursiveDescriptorComparator.Configuration
import org.jetbrains.jet.test.util.DescriptorValidator.ValidationVisitor.FORBID_ERROR_TYPES
import org.jetbrains.jet.lang.resolve.DescriptorUtils
import org.jetbrains.jet.codegen.forTestCompile.ForTestCompileRuntime

public abstract class AbstractJvmRuntimeDescriptorLoaderTest : TestCaseWithTmpdir() {
    class object {
        private val renderer = DescriptorRendererBuilder()
                .setWithDefinedIn(false)
                .setExcludedAnnotationClasses(listOf(
                        ExpectedLoadErrorsUtil.ANNOTATION_CLASS_NAME,
                        "kotlin.deprecated",
                        "kotlin.data",
                        "org.jetbrains.annotations.NotNull",
                        "org.jetbrains.annotations.Nullable",
                        "org.jetbrains.annotations.Mutable",
                        "org.jetbrains.annotations.ReadOnly"
                ).map { FqName(it) })
                .setOverrideRenderingPolicy(DescriptorRenderer.OverrideRenderingPolicy.RENDER_OPEN_OVERRIDE)
                .setIncludeSynthesizedParameterNames(false)
                .setIncludePropertyConstant(false)
                .setVerbose(true)
                .build()
    }

    // NOTE: this test does a dirty hack of text substitution to make all annotations defined in source code retain at runtime.
    // Specifically each "annotation class" in Kotlin sources is replaced by "Retention(RUNTIME) annotation class"
    protected fun doTest(fileName: String) {
        val file = File(fileName)
        if (fileName.endsWith(".java")) {
            val sources = JetTestUtils.createTestFiles(file.getName(), file.readText(), object : TestFileFactoryNoModules<File>() {
                override fun create(fileName: String, text: String, directives: Map<String, String>): File {
                    val targetFile = File(tmpdir, fileName)
                    targetFile.writeText(addRuntimeRetentionToJavaSource(text))
                    return targetFile
                }
            })
            LoadDescriptorUtil.compileJavaWithAnnotationsJar(sources, tmpdir)
        }
        else if (fileName.endsWith(".kt")) {
            val environment = JetTestUtils.createEnvironmentWithMockJdkAndIdeaAnnotations(myTestRootDisposable, ConfigurationKind.ALL)
            val jetFile = JetTestUtils.createFile(fileName, addRuntimeRetentionToKotlinSource(file.readText()), environment.getProject())
            GenerationUtils.compileFileGetClassFileFactoryForTest(jetFile).writeAllTo(tmpdir)
        }

        val classLoader = URLClassLoader(array(tmpdir.toURI().toURL(), ForTestCompileRuntime.runtimeJarForTests().toURI().toURL()))
        val module = createModule(classLoader)

        // Since runtime package view descriptor doesn't support getAllDescriptors(), we construct a synthetic package view here.
        // It has in its scope descriptors for all the classes and top level members generated by the compiler
        val actual = object : PackageViewDescriptor {
            val scope = WritableScopeImpl(JetScope.Empty, this, RedeclarationHandler.THROW_EXCEPTION, "runtime descriptor loader test")

            override fun getFqName() = LoadDescriptorUtil.TEST_PACKAGE_FQNAME
            override fun getMemberScope() = scope
            override fun getModule() = module
            override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R =
                    visitor.visitPackageViewDescriptor(this, data)

            override fun getContainingDeclaration() = throw UnsupportedOperationException()
            override fun getOriginal() = throw UnsupportedOperationException()
            override fun substitute(substitutor: TypeSubstitutor) = throw UnsupportedOperationException()
            override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) = throw UnsupportedOperationException()
            override fun getAnnotations() = throw UnsupportedOperationException()
            override fun getName() = throw UnsupportedOperationException()
        }

        val scope = actual.getMemberScope()
        scope.changeLockLevel(LockLevel.BOTH)

        val generatedPackageDir = File(tmpdir, LoadDescriptorUtil.TEST_PACKAGE_FQNAME.pathSegments().single().asString())
        val allClassFiles = FileUtil.findFilesByMask(Pattern.compile(".*\\.class"), generatedPackageDir)

        for (classFile in allClassFiles) {
            val className = tmpdir.relativePath(classFile).substringBeforeLast(".class").replace('/', '.').replace('\\', '.')
            if (className.endsWith(JvmAbi.CLASS_OBJECT_SUFFIX)) continue

            val klass = classLoader.loadClass(className).sure("Couldn't load class $className")
            val kind = try { ReflectKotlinClass(klass).getClassHeader().kind } catch (e: ReflectKotlinClass.NotAKotlinClass) { null }

            if (kind == KotlinClassHeader.Kind.PACKAGE_FACADE) {
                val packageView = module.getPackage(actual.getFqName()) ?: error("Couldn't resolve package ${actual.getFqName()}")
                scope.importScope(packageView.getMemberScope())
            }
            else if (kind != KotlinClassHeader.Kind.SYNTHETIC_CLASS) {
                // Either a normal Kotlin class or a Java class
                val classDescriptor = resolveClassInModule(module, klass).sure("Couldn't resolve class $className")
                if (classDescriptor.getContainingDeclaration() is PackageFragmentDescriptor) {
                    scope.addClassifierDescriptor(classDescriptor)
                }
            }
        }

        val expected = LoadDescriptorUtil.loadTestPackageAndBindingContextFromJavaRoot(tmpdir, getTestRootDisposable(),
                                                                                       ConfigurationKind.ALL)
        val comparatorConfiguration = Configuration(
                /* checkPrimaryConstructors = */ fileName.endsWith(".kt"),
                /* checkPropertyAccessors = */ true,
                /* includeMethodsOfKotlinAny = */ false,
                { descriptor ->
                    // Parameter order of annotation parameters is not retained at runtime
                    !(descriptor is ConstructorDescriptor && DescriptorUtils.isAnnotationClass(descriptor.getContainingDeclaration()))
                    // Some symbols in AbstractList are present in the class file but not visible via reflection, e.g. "outOfBoundsMsg"
                    // TODO: investigate why this happens. Uncomment and run testModalityOfFakeOverrides() for details
                    && (descriptor as? DeclarationDescriptorWithVisibility)?.getVisibility() != Visibilities.INVISIBLE_FAKE
                },
                FORBID_ERROR_TYPES, renderer
        )
        RecursiveDescriptorComparator.validateAndCompareDescriptors(expected.first, actual, comparatorConfiguration, null)
    }

    private fun addRuntimeRetentionToKotlinSource(text: String): String {
        return text.replace(
                "annotation class",
                "[java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)] annotation class"
        )
    }

    private fun addRuntimeRetentionToJavaSource(text: String): String {
        return text.replace(
                "@interface",
                "@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) @interface"
        )
    }

    private fun resolveClassInModule(module: ModuleDescriptor, klass: Class<*>): ClassDescriptor? {
        val outerClass = klass.getDeclaringClass()
        if (outerClass == null) return module.resolveTopLevelClass(FqName(klass.getName()))

        val outerDescriptor = resolveClassInModule(module, outerClass) ?: return null

        val simpleName = klass.getSimpleName()
        if (simpleName == JvmAbi.CLASS_OBJECT_CLASS_NAME) return outerDescriptor.getClassObjectDescriptor()

        return outerDescriptor.getUnsubstitutedInnerClassesScope().getClassifier(Name.identifier(simpleName)) as? ClassDescriptor
    }
}
