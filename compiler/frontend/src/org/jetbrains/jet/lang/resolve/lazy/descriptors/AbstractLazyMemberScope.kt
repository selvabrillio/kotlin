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

package org.jetbrains.jet.lang.resolve.lazy.descriptors

import com.google.common.collect.Sets
import org.jetbrains.jet.lang.descriptors.*
import org.jetbrains.jet.lang.psi.*
import org.jetbrains.jet.lang.resolve.AnnotationResolver
import org.jetbrains.jet.lang.resolve.BindingTrace
import org.jetbrains.jet.lang.resolve.ScriptNameUtil
import org.jetbrains.jet.lang.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.jet.lang.resolve.lazy.ResolveSession
import org.jetbrains.jet.lang.resolve.lazy.data.JetScriptInfo
import org.jetbrains.jet.lang.resolve.lazy.declarations.DeclarationProvider
import org.jetbrains.jet.lang.resolve.name.Name
import org.jetbrains.jet.lang.resolve.scopes.JetScope
import org.jetbrains.jet.storage.MemoizedFunctionToNotNull
import org.jetbrains.jet.storage.NotNullLazyValue
import org.jetbrains.jet.utils.Printer

import java.util.*
import org.jetbrains.jet.utils.toReadOnlyList

public abstract class AbstractLazyMemberScope<D : DeclarationDescriptor, DP : DeclarationProvider> protected(
        protected val resolveSession: ResolveSession,
        protected val declarationProvider: DP,
        protected val thisDescriptor: D,
        protected val trace: BindingTrace) : JetScope {

    private val storageManager = resolveSession.getStorageManager()
    private val classDescriptors: MemoizedFunctionToNotNull<Name, List<ClassDescriptor>> = storageManager.createMemoizedFunction { resolveClassDescriptor(it) }
    private val functionDescriptors: MemoizedFunctionToNotNull<Name, Collection<FunctionDescriptor>> = storageManager.createMemoizedFunction { doGetFunctions(it) }
    private val propertyDescriptors: MemoizedFunctionToNotNull<Name, Collection<VariableDescriptor>> = storageManager.createMemoizedFunction { doGetProperties(it) }
    private val descriptorsFromDeclaredElements: NotNullLazyValue<Collection<DeclarationDescriptor>> = storageManager.createLazyValue { computeDescriptorsFromDeclaredElements() }
    private val extraDescriptors: NotNullLazyValue<Collection<DeclarationDescriptor>> = storageManager.createLazyValue { computeExtraDescriptors() }

    private fun resolveClassDescriptor(name: Name): List<ClassDescriptor> {
        return declarationProvider.getClassOrObjectDeclarations(name).map {
            // SCRIPT: Creating a script class
            if (it is JetScriptInfo)
                LazyScriptClassDescriptor(resolveSession, thisDescriptor, name, it)
            else
                LazyClassDescriptor(resolveSession, thisDescriptor, name, it)
        }
    }

    override fun getContainingDeclaration() = thisDescriptor

    override fun getClassifier(name: Name): ClassDescriptor? = classDescriptors(name).firstOrNull()

    override fun getFunctions(name: Name): Collection<FunctionDescriptor> = functionDescriptors(name)

    private fun doGetFunctions(name: Name): Collection<FunctionDescriptor> {
        val result = Sets.newLinkedHashSet<FunctionDescriptor>()

        val declarations = declarationProvider.getFunctionDeclarations(name)
        for (functionDeclaration in declarations) {
            val resolutionScope = getScopeForMemberDeclarationResolution(functionDeclaration)
            result.add(resolveSession.getDescriptorResolver().resolveFunctionDescriptorWithAnnotationArguments(
                    thisDescriptor,
                    resolutionScope,
                    functionDeclaration,
                    trace,
                    // this relies on the assumption that a lazily resolved declaration is not a local one,
                    // thus doesn't have a surrounding data flow
                    DataFlowInfo.EMPTY))
        }

        getNonDeclaredFunctions(name, result)

        return result.toReadOnlyList()
    }

    protected abstract fun getScopeForMemberDeclarationResolution(declaration: JetDeclaration): JetScope

    protected abstract fun getNonDeclaredFunctions(name: Name, result: MutableSet<FunctionDescriptor>)

    override fun getProperties(name: Name): Collection<VariableDescriptor> = propertyDescriptors(name)

    public fun doGetProperties(name: Name): Collection<VariableDescriptor> {
        val result = LinkedHashSet<VariableDescriptor>()

        val declarations = declarationProvider.getPropertyDeclarations(name)
        for (propertyDeclaration in declarations) {
            val resolutionScope = getScopeForMemberDeclarationResolution(propertyDeclaration)
            val propertyDescriptor = resolveSession.getDescriptorResolver().resolvePropertyDescriptor(
                    thisDescriptor,
                    resolutionScope,
                    propertyDeclaration,
                    trace,
                    // this relies on the assumption that a lazily resolved declaration is not a local one,
                    // thus doesn't have a surrounding data flow
                    DataFlowInfo.EMPTY)
            result.add(propertyDescriptor)
            AnnotationResolver.resolveAnnotationsArguments(propertyDescriptor.getAnnotations(), trace)
        }

        getNonDeclaredProperties(name, result)

        return result.toReadOnlyList()
    }

    protected abstract fun getNonDeclaredProperties(name: Name, result: MutableSet<VariableDescriptor>)

    override fun getLocalVariable(name: Name): VariableDescriptor? = null

    override fun getDeclarationsByLabel(labelName: Name) = setOf<DeclarationDescriptor>()

    override fun getDescriptors(kindFilter: (JetScope.DescriptorKind) -> Boolean,
                                nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> {
        val result = LinkedHashSet(descriptorsFromDeclaredElements())
        result.addAll(extraDescriptors())
        return result
    }

    private fun computeDescriptorsFromDeclaredElements(): Collection<DeclarationDescriptor> {
        val declarations = declarationProvider.getAllDeclarations()
        val result = ArrayList<DeclarationDescriptor>(declarations.size())
        for (declaration in declarations) {
            if (declaration is JetClassOrObject) {
                result.addAll(classDescriptors(declaration.getNameAsSafeName()))
            }
            else if (declaration is JetFunction) {
                result.addAll(getFunctions(declaration.getNameAsSafeName()))
            }
            else if (declaration is JetProperty) {
                result.addAll(getProperties(declaration.getNameAsSafeName()))
            }
            else if (declaration is JetParameter) {
                result.addAll(getProperties(declaration.getNameAsSafeName()))
            }
            else if (declaration is JetScript) {
                result.addAll(classDescriptors(ScriptNameUtil.classNameForScript(declaration).shortName()))
            }
            else if (declaration is JetTypedef || declaration is JetMultiDeclaration) {
                // Do nothing for typedefs as they are not supported.
                // MultiDeclarations are not supported on global level too.
            }
            else {
                throw IllegalArgumentException("Unsupported declaration kind: " + declaration)
            }
        }
        return result.toReadOnlyList()
    }

    protected abstract fun computeExtraDescriptors(): Collection<DeclarationDescriptor>

    override fun getImplicitReceiversHierarchy() = listOf<ReceiverParameterDescriptor>()

    // Do not change this, override in concrete subclasses:
    // it is very easy to compromise laziness of this class, and fail all the debugging
    // a generic implementation can't do this properly
    abstract override fun toString(): String

    override fun getOwnDeclaredDescriptors() = getDescriptors()

    override fun printScopeStructure(p: Printer) {
        p.println(javaClass.getSimpleName(), " {")
        p.pushIndent()

        p.println("thisDescriptor = ", thisDescriptor)

        p.popIndent()
        p.println("}")
    }
}
