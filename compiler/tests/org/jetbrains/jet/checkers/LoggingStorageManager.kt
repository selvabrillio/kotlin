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

package org.jetbrains.jet.checkers

import org.jetbrains.jet.checkers.LoggingStorageManager.CallData

public class LoggingStorageManager(
        private val delegate: org.jetbrains.jet.storage.StorageManager,
        private val callHandler: (lambda: Any, call: CallData?) -> Unit
) : org.jetbrains.jet.storage.StorageManager {

    public class CallData(
            val fieldOwner: Any?,
            val field: java.lang.reflect.Field?,
            val lambdaCreatedIn: java.lang.reflect.GenericDeclaration?,
            val arguments: List<Any?>,
            val result: Any?
    )

    // Creating objects here because we need a reference to it
    private val <T> (() -> T).logged: () -> T
        get() = object : () -> T {
            override fun invoke(): T {
                val result = this@logged()
                callHandler(this@logged, computeCallerData(this@logged, this, listOf(), result))
                return result
            }
        }

    // Creating objects here because we need a reference to it
    private val <K, V> ((K) -> V).logged: (K) -> V
        get() = object : (K) -> V {
            override fun invoke(p1: K): V {
                val result = this@logged(p1)
                callHandler(this@logged, computeCallerData(this@logged, this, listOf(p1), result))
                return result
            }
        }

    private fun computeCallerData(lambda: Any, wrapper: Any, arguments: List<Any?>, result: Any?): CallData {
        val jClass = lambda.javaClass

        val outerClass: Class<out Any?>? = jClass.getEnclosingClass()

        // fields named "this" or "this$0"
        val referenceToOuter = jClass.getAllDeclaredFields().firstOrNull {
            field ->
            field.getType() == outerClass && field.getName()!!.contains("this")
        }
        referenceToOuter?.setAccessible(true)

        val outerInstance = referenceToOuter?.get(lambda)

        val containingField = if (outerInstance == null) null
                              else outerClass?.getAllDeclaredFields()?.firstOrNull {
                                  (field): Boolean ->
                                  field.setAccessible(true)
                                  val value = field.get(outerInstance)
                                  if (value == null) return@firstOrNull false

                                  val valueClass = value.javaClass

                                  val functionField = valueClass.getAllDeclaredFields().firstOrNull {
                                      it.getType()?.getName()?.startsWith("kotlin.Function") ?: false
                                  }
                                  if (functionField == null) return@firstOrNull false

                                  functionField.setAccessible(true)
                                  val functionValue = functionField.get(value)
                                  functionValue == wrapper
                              }

        val enclosingEntity = jClass.getEnclosingConstructor()
                            ?: jClass.getEnclosingMethod()
                            ?: jClass.getEnclosingClass()

        return CallData(outerInstance, containingField, enclosingEntity as java.lang.reflect.GenericDeclaration?, arguments, result)
    }

    private fun Class<*>.getAllDeclaredFields(): List<java.lang.reflect.Field> {
        val result = arrayListOf<java.lang.reflect.Field>()

        var c = this
        while (true) {
            result.addAll(c.getDeclaredFields().toList())
            [suppress("UNCHECKED_CAST")]
            val superClass = (c as Class<Any>).getSuperclass() as Class<Any>?
            if (superClass == null) break
            if (c == superClass) break
            c = superClass
        }

        return result
    }

    override fun createMemoizedFunction<K, V: Any>(compute: (K) -> V): org.jetbrains.jet.storage.MemoizedFunctionToNotNull<K, V> {
        return delegate.createMemoizedFunction(compute.logged)
    }

    override fun createMemoizedFunctionWithNullableValues<K, V: Any>(compute: (K) -> V?): org.jetbrains.jet.storage.MemoizedFunctionToNullable<K, V> {
        return delegate.createMemoizedFunctionWithNullableValues(compute.logged)
    }

    override fun createLazyValue<T: Any>(computable: () -> T): org.jetbrains.jet.storage.NotNullLazyValue<T> {
        return delegate.createLazyValue(computable.logged)
    }

    override fun createRecursionTolerantLazyValue<T: Any>(computable: () -> T, onRecursiveCall: T): org.jetbrains.jet.storage.NotNullLazyValue<T> {
        return delegate.createRecursionTolerantLazyValue(computable.logged, onRecursiveCall)
    }

    override fun createLazyValueWithPostCompute<T: Any>(computable: () -> T, onRecursiveCall: ((Boolean) -> T)?, postCompute: (T) -> Unit): org.jetbrains.jet.storage.NotNullLazyValue<T> {
        return delegate.createLazyValueWithPostCompute(computable.logged, onRecursiveCall, postCompute)
    }

    override fun createNullableLazyValue<T: Any>(computable: () -> T?): org.jetbrains.jet.storage.NullableLazyValue<T> {
        return delegate.createNullableLazyValue(computable.logged)
    }

    override fun createRecursionTolerantNullableLazyValue<T: Any>(computable: () -> T?, onRecursiveCall: T?): org.jetbrains.jet.storage.NullableLazyValue<T> {
        return delegate.createRecursionTolerantNullableLazyValue(computable.logged, onRecursiveCall)
    }

    override fun createNullableLazyValueWithPostCompute<T: Any>(computable: () -> T?, postCompute: (T?) -> Unit): org.jetbrains.jet.storage.NullableLazyValue<T> {
        return delegate.createNullableLazyValueWithPostCompute(computable.logged, postCompute)
    }

    override fun compute<T>(computable: () -> T): T {
        return delegate.compute(computable.logged)
    }
}