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
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.kdoc.psi.api.KDoc;
import org.jetbrains.jet.lang.psi.findDocComment.FindDocCommentPackage;

abstract class JetDeclarationStub<T extends StubElement> extends JetModifierListOwnerStub<T> implements JetDeclaration {
    public JetDeclarationStub(@NotNull T stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public JetDeclarationStub(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public KDoc getDocComment() {
        return FindDocCommentPackage.findDocComment(this);
    }
}
