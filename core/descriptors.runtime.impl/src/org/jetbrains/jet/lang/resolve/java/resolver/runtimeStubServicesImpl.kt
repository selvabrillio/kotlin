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

package org.jetbrains.jet.lang.resolve.java.resolver

import org.jetbrains.jet.lang.descriptors.*
import org.jetbrains.jet.lang.resolve.java.structure.*
import org.jetbrains.jet.lang.resolve.name.FqName
import org.jetbrains.jet.lang.types.JetType
import org.jetbrains.jet.lang.resolve.kotlin.KotlinJvmBinaryClass
import org.jetbrains.jet.lang.resolve.java.JvmAbi
import org.jetbrains.jet.lang.resolve.constants.CompileTimeConstant
import org.jetbrains.jet.lang.resolve.java.sources.JavaSourceElementFactory
import org.jetbrains.jet.lang.resolve.java.sources.JavaSourceElement
import org.jetbrains.jet.lang.resolve.java.structure.reflect.ReflectJavaElement
import org.jetbrains.jet.lang.resolve.name.Name
import org.jetbrains.jet.lang.resolve.scopes.JetScope
import org.jetbrains.jet.lang.resolve.java.descriptor.SamConstructorDescriptor

public object RuntimeExternalSignatureResolver : ExternalSignatureResolver {
    override fun resolvePropagatedSignature(
            method: JavaMethod,
            owner: ClassDescriptor,
            returnType: JetType,
            receiverType: JetType?,
            valueParameters: List<ValueParameterDescriptor>,
            typeParameters: List<TypeParameterDescriptor>
    ): ExternalSignatureResolver.PropagatedMethodSignature {
        return ExternalSignatureResolver.PropagatedMethodSignature(
                returnType, receiverType, valueParameters, typeParameters, listOf(), false, listOf() /* TODO */
        )
    }

    override fun resolveAlternativeMethodSignature(
            methodOrConstructor: JavaMember,
            hasSuperMethods: Boolean,
            returnType: JetType?,
            receiverType: JetType?,
            valueParameters: List<ValueParameterDescriptor>,
            typeParameters: List<TypeParameterDescriptor>,
            hasStableParameterNames: Boolean
    ): ExternalSignatureResolver.AlternativeMethodSignature {
        return ExternalSignatureResolver.AlternativeMethodSignature(
                returnType, receiverType, valueParameters, typeParameters, listOf(), hasStableParameterNames
        )
    }

    override fun resolveAlternativeFieldSignature(
            field: JavaField, returnType: JetType, isVar: Boolean
    ): ExternalSignatureResolver.AlternativeFieldSignature {
        return ExternalSignatureResolver.AlternativeFieldSignature(returnType, null)
    }

    override fun reportSignatureErrors(descriptor: CallableMemberDescriptor, signatureErrors: List<String>) {
        throw UnsupportedOperationException()
    }
}


// TODO: ?
public object RuntimeJavaResolverCache : JavaResolverCache {
    override fun getClassResolvedFromSource(fqName: FqName) = null
    override fun recordMethod(method: JavaMethod, descriptor: SimpleFunctionDescriptor) { }
    override fun recordConstructor(element: JavaElement, descriptor: ConstructorDescriptor) { }
    override fun recordField(field: JavaField, descriptor: PropertyDescriptor) { }
    override fun recordClass(javaClass: JavaClass, descriptor: ClassDescriptor) { }
}


public object RuntimeErrorReporter : ErrorReporter {
    // TODO: specialized exceptions
    override fun reportIncompatibleAbiVersion(kotlinClass: KotlinJvmBinaryClass, actualVersion: Int) {
        throw IllegalStateException("Incompatible ABI version of ${kotlinClass.getClassId()}: $actualVersion " +
                                    "(expected version is ${JvmAbi.VERSION}")
    }
    override fun reportCannotInferVisibility(descriptor: CallableMemberDescriptor) {
        // TODO: use DescriptorRenderer
        throw IllegalStateException("Cannot infer visibility for $descriptor")
    }
    override fun reportLoadingError(message: String, exception: Exception?) {
        throw IllegalStateException(message, exception)
    }
}


public object RuntimeMethodSignatureChecker : MethodSignatureChecker {
    override fun checkSignature(
            method: JavaMethod,
            reportSignatureErrors: Boolean,
            descriptor: SimpleFunctionDescriptor,
            signatureErrors: List<String>,
            superFunctions: List<FunctionDescriptor>
    ) {
        // Do nothing
    }
}


public object RuntimeExternalAnnotationResolver : ExternalAnnotationResolver {
    override fun findExternalAnnotation(owner: JavaAnnotationOwner, fqName: FqName) = null
    override fun findExternalAnnotations(owner: JavaAnnotationOwner) = listOf<JavaAnnotation>()
}


public object RuntimePropertyInitializerEvaluator : JavaPropertyInitializerEvaluator {
    override fun isNotNullCompileTimeConstant(field: JavaField): Boolean {
        // TODO
        return false
    }

    // TODO: ?
    override fun getInitializerConstant(field: JavaField, descriptor: PropertyDescriptor): CompileTimeConstant<*>? = null
}


public object RuntimeSamConversionResolver : SamConversionResolver {
    override fun <D : FunctionDescriptor> resolveSamAdapter(original: D) = null
    override fun resolveSamConstructor(name: Name, scope: JetScope) = null
}


public object RuntimeSourceElementFactory : JavaSourceElementFactory {
    private class RuntimeSourceElement(override val javaElement: ReflectJavaElement) : JavaSourceElement

    override fun source(javaElement: JavaElement) = RuntimeSourceElement(javaElement as ReflectJavaElement)
}
