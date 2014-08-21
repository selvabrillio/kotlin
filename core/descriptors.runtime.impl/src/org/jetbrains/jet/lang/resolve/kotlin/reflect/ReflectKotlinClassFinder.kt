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

package org.jetbrains.jet.lang.resolve.kotlin.reflect

import org.jetbrains.jet.lang.resolve.java.structure.JavaClass
import org.jetbrains.jet.lang.resolve.java.reflect.tryLoadClass
import org.jetbrains.jet.lang.resolve.kotlin.KotlinClassFinder
import org.jetbrains.jet.lang.resolve.kotlin.KotlinJvmBinaryClass
import org.jetbrains.jet.lang.resolve.name.ClassId

public class ReflectKotlinClassFinder(private val classLoader: ClassLoader) : KotlinClassFinder {
    private fun findKotlinClass(fqName: String): KotlinJvmBinaryClass? {
        val klass = classLoader.tryLoadClass(fqName)
        if (klass == null) return null
        return try {
            ReflectKotlinClass(klass)
        }
        catch (e: ReflectKotlinClass.NotAKotlinClass) {
            null
        }
    }

    override fun findKotlinClass(classId: ClassId) = findKotlinClass(classId.toRuntimeFqName())

    override fun findKotlinClass(javaClass: JavaClass): KotlinJvmBinaryClass? {
        // TODO: go through javaClass's class loader
        return findKotlinClass(javaClass.getFqName()?.asString() ?: return null)
    }
}

private fun ClassId.toRuntimeFqName(): String {
    val className = getRelativeClassName().toString().replace('.', '$')
    return if (getPackageFqName().isRoot()) className else "${getPackageFqName()}.$className"
}
