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

package org.jetbrains.jet.lang.psi.stubs.elements;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.JetPropertyAccessor;
import org.jetbrains.jet.lang.psi.stubs.KotlinPropertyAccessorStub;
import org.jetbrains.jet.lang.psi.stubs.impl.KotlinPropertyAccessorStubImpl;

import java.io.IOException;

public class JetPropertyAccessorElementType extends JetStubElementType<KotlinPropertyAccessorStub, JetPropertyAccessor> {
    public JetPropertyAccessorElementType(@NotNull @NonNls String debugName) {
        super(debugName, JetPropertyAccessor.class, KotlinPropertyAccessorStub.class);
    }

    @Override
    public KotlinPropertyAccessorStub createStub(@NotNull JetPropertyAccessor psi, StubElement parentStub) {
        return new KotlinPropertyAccessorStubImpl(parentStub, psi.isGetter(), psi.hasBody(), psi.hasBlockBody());
    }

    @Override
    public void serialize(@NotNull KotlinPropertyAccessorStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(stub.isGetter());
        dataStream.writeBoolean(stub.hasBody());
        dataStream.writeBoolean(stub.hasBlockBody());
    }

    @NotNull
    @Override
    public KotlinPropertyAccessorStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        boolean isGetter = dataStream.readBoolean();
        boolean hasBody = dataStream.readBoolean();
        boolean hasBlockBody = dataStream.readBoolean();
        return new KotlinPropertyAccessorStubImpl(parentStub, isGetter, hasBody, hasBlockBody);
    }
}
