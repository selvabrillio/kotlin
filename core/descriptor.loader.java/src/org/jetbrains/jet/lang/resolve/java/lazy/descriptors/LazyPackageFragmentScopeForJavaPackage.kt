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

package org.jetbrains.jet.lang.resolve.java.lazy.descriptors

import org.jetbrains.jet.lang.descriptors.*
import org.jetbrains.jet.lang.resolve.name.*
import org.jetbrains.jet.lang.resolve.java.lazy.LazyJavaResolverContext
import org.jetbrains.jet.lang.resolve.java.structure.JavaPackage
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass
import org.jetbrains.jet.lang.resolve.java.lazy.findClassInJava
import org.jetbrains.jet.lang.resolve.java.PackageClassUtils
import org.jetbrains.jet.lang.resolve.scopes.JetScope
import org.jetbrains.jet.lang.resolve.kotlin.KotlinJvmBinaryClass
import org.jetbrains.jet.utils.addIfNotNull

public class LazyPackageFragmentScopeForJavaPackage(
        c: LazyJavaResolverContext,
        private val jPackage: JavaPackage,
        packageFragment: LazyJavaPackageFragment
) : LazyJavaStaticScope(c, packageFragment) {

    // TODO: Storing references is a temporary hack until modules infrastructure is implemented.
    // See JetTypeMapperWithOutDirectories for details
    public val kotlinBinaryClass: KotlinJvmBinaryClass?
            = c.kotlinClassFinder.findKotlinClass(PackageClassUtils.getPackageClassId(packageFragment.fqName))

    private val deserializedPackageScope = c.storageManager.createLazyValue {
        val kotlinBinaryClass = kotlinBinaryClass
        if (kotlinBinaryClass == null)
            JetScope.Empty
        else
            c.deserializedDescriptorResolver.createKotlinPackageScope(packageFragment, kotlinBinaryClass) ?: JetScope.Empty
    }

    private val classes = c.storageManager.createMemoizedFunctionWithNullableValues<Name, ClassDescriptor> { name ->
        val classId = ClassId(packageFragment.fqName, SpecialNames.safeIdentifier(name))
        val (jClass, kClass) = c.findClassInJava(classId)
        if (kClass != null)
            kClass
        else if (jClass == null)
            null
        else {
            val classDescriptor = c.javaClassResolver.resolveClass(jClass)
            assert(classDescriptor == null || classDescriptor.getContainingDeclaration() == packageFragment) {
                "Wrong package fragment for $classDescriptor, expected $packageFragment"
            }
            classDescriptor
        }
    }

    override fun getClassifier(name: Name): ClassifierDescriptor? = classes(name)

    override fun getProperties(name: Name) = deserializedPackageScope().getProperties(name)
    override fun getFunctions(name: Name) = deserializedPackageScope().getFunctions(name) + super.getFunctions(name)

    override fun addExtraDescriptors(result: MutableSet<DeclarationDescriptor>,
                                     kindFilterMask: Int,
                                     nameFilter: (Name) -> Boolean) {
        result.addAll(deserializedPackageScope().getDescriptors(kindFilterMask, nameFilter))
    }

    override fun computeMemberIndex(): MemberIndex = object : MemberIndex by EMPTY_MEMBER_INDEX {
        // For SAM-constructors
        override fun getMethodNames(nameFilter: (Name) -> Boolean): Collection<Name> = getClassNames(nameFilter)
    }

    override fun computeAdditionalFunctions(name: Name) = listOf<SimpleFunctionDescriptor>()

    override fun getClassNames(nameFilter: (Name) -> Boolean): Collection<Name> {
        return jPackage.getClasses(nameFilter).stream()
                .filter { c -> c.getOriginKind() != JavaClass.OriginKind.KOTLIN_LIGHT_CLASS }
                .map { c -> c.getName() }.toList()
    }

    private val _subPackages = c.storageManager.createRecursionTolerantLazyValue(
            {
                jPackage.getSubPackages().map { sp -> sp.getFqName() }
            },
            // This breaks infinite recursion between loading Java descriptors and building light classes
            onRecursiveCall = listOf()
    )

    override fun computeNonDeclaredFunctions(result: MutableCollection<SimpleFunctionDescriptor>, name: Name) {
        result.addIfNotNull(getClassifier(name)?.createSamConstructor())
    }

    override fun getSubPackages() = _subPackages()

    override fun getAllPropertyNames() = listOf<Name>()

    // we don't use implementation from super which caches all descriptors and does not use filters
    override fun getDescriptors(kindFilterMask: Int, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> {
        return computeDescriptors(kindFilterMask, nameFilter)
    }
}
