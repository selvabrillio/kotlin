/*
 * Copyright 2010-2013 JetBrains s.r.o.
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

package org.jetbrains.jet.lang.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.jet.lang.psi.stubs.elements.JetStubElementTypes;

import java.util.List;

public class JetTypeArgumentList extends JetElementImplStub<KotlinPlaceHolderStub<JetTypeArgumentList>> {
    public JetTypeArgumentList(@NotNull ASTNode node) {
        super(node);
    }

    public JetTypeArgumentList(@NotNull KotlinPlaceHolderStub<JetTypeArgumentList> stub) {
        super(stub, JetStubElementTypes.TYPE_ARGUMENT_LIST);
    }

    @Override
    public <R, D> R accept(@NotNull JetVisitor<R, D> visitor, D data) {
        return visitor.visitTypeArgumentList(this, data);
    }

    @NotNull
    public List<JetTypeProjection> getArguments() {
        return getStubOrPsiChildrenAsList(JetStubElementTypes.TYPE_PROJECTION);
    }
}
