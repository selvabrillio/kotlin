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

package org.jetbrains.jet.codegen

import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import org.jetbrains.org.objectweb.asm.Label

trait StackValueTrait {
    fun put(`type`: Type, v: InstructionAdapter);

    public fun store(topOfStackType: Type, v: InstructionAdapter)

    public fun dupReceiver(v: InstructionAdapter)

    public fun receiverSize(): Int

    public fun condJump(label: Label, jumpIfFalse: Boolean, v: InstructionAdapter)

}

public class CastValue(val value: StackValue, val castType: Type) : StackValue(castType), StackValueTrait by value {

}


public abstract class StackValueComplex(val value: StackValue, val receiver: StackValue) : StackValue(value.`type`) {

    override fun dupReceiver(v: InstructionAdapter) {
        throw UnsupportedOperationException()
    }

    override fun store(topOfStackType: Type, v: InstructionAdapter) {
        receiver.store(receiver.`type`, v)
        value.store(topOfStackType, v)
    }

    override fun put(`type`: Type, v: InstructionAdapter) {
        throw UnsupportedOperationException()
    }

    public fun put(`type`: Type, v: InstructionAdapter, what: StackValue) {
        receiver.put(receiver.`type`, v)
        what.put(what.`type`, v)
        value.store(`type`, v)
    }

    override fun receiverSize(): Int {
        val size = receiver.`type`.getSize()
        return size
    }
}