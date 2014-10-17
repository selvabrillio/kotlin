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

package org.jetbrains.jet.utils.concurrent.block

import kotlin.properties.ReadWriteProperty

public class BlockingNotNullVar<T : Any>(lock: Any, val init: () -> T) : ReadWriteProperty<Any?, T?> {
    private val lock = lock
    private volatile var value: T? = null

    public override fun get(thisRef: Any?, desc: PropertyMetadata): T {
        val _v1 = value
        if (_v1 != null) {
            return _v1
        }

        return synchronized(lock) {
            val _v2 = value
            // Suppress because of https://youtrack.jetbrains.com/issue/KT-6176
            [suppress("UNNECESSARY_NOT_NULL_ASSERTION")]
            if (_v2 != null) {
                _v2!!
            }
            else {
                val _v3 = init()
                this.value = _v3
                _v3
            }
        }
    }

    override fun set(thisRef: Any?, desc: kotlin.PropertyMetadata, value: T?) {
        synchronized(lock) {
            this.value = value
        }
    }
}

