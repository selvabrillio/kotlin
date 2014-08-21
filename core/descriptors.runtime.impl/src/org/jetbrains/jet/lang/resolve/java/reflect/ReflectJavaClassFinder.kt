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

package org.jetbrains.jet.lang.resolve.java.reflect

import org.jetbrains.jet.lang.resolve.java.JavaClassFinder
import org.jetbrains.jet.lang.resolve.name.FqName
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass
import org.jetbrains.jet.lang.resolve.java.structure.JavaPackage
import org.jetbrains.jet.lang.resolve.java.structure.reflect.ReflectJavaClass
import org.jetbrains.jet.lang.resolve.java.structure.reflect.ReflectJavaPackage
import org.jetbrains.jet.lang.resolve.name.ClassId

public class ReflectJavaClassFinder(private val classLoader: ClassLoader) : JavaClassFinder {
    override fun findClass(classId: ClassId): JavaClass? {
        val klass = classLoader.tryLoadClass(classId.asSingleFqName().asString())
        return if (klass != null) ReflectJavaClass(klass) else null
    }

    override fun findPackage(fqName: FqName): JavaPackage? {
        // We don't know which packages our class loader has and has not, so we behave as if it contains any package in the world
        return ReflectJavaPackage(fqName)
    }
}

fun ClassLoader.tryLoadClass(fqName: String) =
        try {
            loadClass(fqName)
        }
        catch (e: ClassNotFoundException) {
            null
        }
