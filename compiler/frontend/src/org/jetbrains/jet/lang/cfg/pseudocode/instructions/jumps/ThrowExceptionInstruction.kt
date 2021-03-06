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

package org.jetbrains.jet.lang.cfg.pseudocode.instructions.jumps

import org.jetbrains.jet.lang.cfg.pseudocode.PseudoValue
import org.jetbrains.jet.lang.psi.JetThrowExpression
import org.jetbrains.jet.lang.cfg.Label
import java.util.Collections
import org.jetbrains.jet.lang.cfg.pseudocode.instructions.LexicalScope
import org.jetbrains.jet.lang.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.jet.lang.cfg.pseudocode.instructions.InstructionVisitorWithResult

public class ThrowExceptionInstruction(
        expression: JetThrowExpression,
        lexicalScope: LexicalScope,
        errorLabel: Label,
        public val thrownValue: PseudoValue
) : AbstractJumpInstruction(expression, errorLabel, lexicalScope) {
    override val inputValues: List<PseudoValue> get() = Collections.singletonList(thrownValue)

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitThrowExceptionInstruction(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R {
        return visitor.visitThrowExceptionInstruction(this)
    }

    override fun toString(): String {
        return "throw (${element.getText()}|$thrownValue)"
    }

    override fun createCopy(newLabel: Label, lexicalScope: LexicalScope): AbstractJumpInstruction {
        return ThrowExceptionInstruction((element as JetThrowExpression), lexicalScope, newLabel, thrownValue)
    }
}