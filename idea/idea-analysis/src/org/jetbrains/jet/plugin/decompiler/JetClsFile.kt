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

package org.jetbrains.jet.plugin.decompiler

import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.compiled.ClsFileImpl
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.TestOnly
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor
import org.jetbrains.jet.lang.psi.JetDeclaration
import com.intellij.psi.PsiElement
import org.jetbrains.jet.lang.psi.JetFile
import org.jetbrains.jet.utils.concurrent.block.BlockingNotNullVar
import org.jetbrains.jet.plugin.decompiler.textBuilder.DecompiledText
import org.jetbrains.jet.plugin.decompiler.textBuilder.buildDecompiledText
import org.jetbrains.jet.plugin.decompiler.textBuilder.descriptorToKey

public class JetClsFile(val provider: JetClassFileViewProvider) : ClsFileImpl(provider) {
    private val mirrorLock = Any()

    private var mirrorFile: JetFile? by BlockingNotNullVar(mirrorLock) {
        JetDummyClassFileViewProvider.createJetFile(
                provider.getManager(),
                getVirtualFile(),
                decompiledText!!.text)!!
    }

    private var decompiledText: DecompiledText? by BlockingNotNullVar(mirrorLock) {
        buildDecompiledText(getVirtualFile())
    }

    override fun getMirror(): PsiElement? = mirrorFile

    public fun getDeclarationForDescriptor(descriptor: DeclarationDescriptor): JetDeclaration? {
        val key = descriptorToKey(descriptor.getOriginal())

        return synchronized(mirrorLock) {
            val range = decompiledText!!.renderedDescriptorsToRange[key]
            if (range != null) {
                PsiTreeUtil.findElementOfClassAtRange(
                        mirrorFile, range.getStartOffset(), range.getEndOffset(), javaClass<JetDeclaration>())
            }
            else {
                null
            }
        }
    }

    override fun onContentReload() {
        super<ClsFileImpl>.onContentReload()

        synchronized (mirrorLock) {
            decompiledText = null
            mirrorFile = null
        }
    }

    TestOnly
    fun getRenderedDescriptorsToRange(): Map<String, TextRange> {
        return decompiledText!!.renderedDescriptorsToRange
    }
}
