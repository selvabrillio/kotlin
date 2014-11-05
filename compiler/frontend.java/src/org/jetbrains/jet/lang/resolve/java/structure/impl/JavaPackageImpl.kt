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

package org.jetbrains.jet.lang.resolve.java.structure.impl

import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiPackage
import com.intellij.util.containers.ContainerUtil
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass
import org.jetbrains.jet.lang.resolve.java.structure.JavaPackage
import org.jetbrains.jet.lang.resolve.name.FqName

import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaElementCollectionFromPsiArrayUtil.classes
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaElementCollectionFromPsiArrayUtil.packages

public class JavaPackageImpl(psiPackage: PsiPackage) : JavaElementImpl<PsiPackage>(psiPackage), JavaPackage {

    override fun getClasses() = classes(getPsi().getClasses().filter { it.getName() != null })

    override fun getSubPackages() = packages(getPsi().getSubPackages())

    override fun getFqName() = FqName(getPsi().getQualifiedName())
}
