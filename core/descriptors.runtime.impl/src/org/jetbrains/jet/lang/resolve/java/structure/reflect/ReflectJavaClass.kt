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

package org.jetbrains.jet.lang.resolve.java.structure.reflect

import java.lang.reflect.Modifier
import org.jetbrains.jet.lang.resolve.java.structure.*
import org.jetbrains.jet.lang.resolve.name.FqName
import org.jetbrains.jet.lang.resolve.name.Name
import org.jetbrains.jet.utils.emptyOrSingletonList

public class ReflectJavaClass(private val klass: Class<*>) : ReflectJavaElement(), JavaClass {
    override fun getInnerClasses(): Collection<JavaClass> {
        // TODO
        return listOf()
    }

    override fun getFqName(): FqName? {
        // TODO: can there be primitive types, arrays?
        return getOuterClass()?.getFqName()?.child(Name.identifier(klass.getSimpleName())) ?: FqName(klass.getName())
    }

    override fun getOuterClass(): JavaClass? {
        val container = klass.getDeclaringClass()
        return if (container != null) ReflectJavaClass(container) else null
    }

    override fun getSupertypes(): Collection<JavaClassifierType> {
        // TODO: also call getSuperclass() / getInterfaces() for classes without generic signature
        val supertypes = emptyOrSingletonList(klass.getGenericSuperclass()) + klass.getGenericInterfaces()
        return supertypes.map { supertype -> ReflectJavaClassifierType(supertype) }
    }

    override fun getMethods(): Collection<JavaMethod> {
        // TODO
        return listOf()
    }

    override fun getFields(): Collection<JavaField> {
        // TODO
        return listOf()
    }

    override fun getConstructors(): Collection<JavaConstructor> {
        // TODO
        return listOf()
    }

    override fun getDefaultType() = ReflectJavaClassifierType(klass)

    // TODO: drop OriginKind?
    override fun getOriginKind() = JavaClass.OriginKind.COMPILED

    override fun createImmediateType(substitutor: JavaTypeSubstitutor): JavaType {
        // TODO
        throw UnsupportedOperationException()
    }

    override fun getName(): Name {
        // TODO: can there be primitive types, arrays?
        return Name.identifier(klass.getSimpleName())
    }

    override fun getAnnotations(): Collection<JavaAnnotation> {
        // TODO
        return listOf()
    }

    override fun findAnnotation(fqName: FqName): JavaAnnotation? {
        // TODO
        return null
    }

    override fun getTypeParameters() = klass.getTypeParameters().map { ReflectJavaTypeParameter(it!!) }

    override fun isInterface() = klass.isInterface()
    override fun isAnnotationType() = klass.isAnnotation()
    override fun isEnum() = klass.isEnum()

    override fun isAbstract() = Modifier.isAbstract(klass.getModifiers())
    override fun isStatic() = Modifier.isStatic(klass.getModifiers())
    override fun isFinal() = Modifier.isFinal(klass.getModifiers())

    override fun getVisibility() = calculateVisibility(klass.getModifiers())
}
