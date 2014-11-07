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

package org.jetbrains.jet.lang.resolve.scopes

import org.jetbrains.jet.lang.descriptors.*
import org.jetbrains.jet.lang.resolve.name.Name
import org.jetbrains.jet.utils.Printer

import java.util.*

import org.jetbrains.jet.lang.resolve.scopes.JetScopeSelectorUtil.*
import kotlin.Collection
import kotlin.List
import kotlin.Set

public class ChainedScope(private val containingDeclaration: DeclarationDescriptor?/* it's nullable as a hack for TypeUtils.intersect() */,
                          private val debugName: String,
                          vararg scopes: JetScope) : JetScope {
    private val scopeChain = scopes.clone()
    private var _allDescriptors: MutableCollection<DeclarationDescriptor>? = null
    private var implicitReceiverHierarchy: List<ReceiverParameterDescriptor>? = null

    override fun getClassifier(name: Name): ClassifierDescriptor?
            = getFirstMatch(scopeChain, name, CLASSIFIER_DESCRIPTOR_SCOPE_SELECTOR)

    override fun getPackage(name: Name): PackageViewDescriptor?
            = getFirstMatch(scopeChain, name, PACKAGE_SCOPE_SELECTOR)

    override fun getProperties(name: Name): Set<VariableDescriptor>
            = getFromAllScopes(scopeChain, name, NAMED_PROPERTIES_SCOPE_SELECTOR)

    override fun getLocalVariable(name: Name): VariableDescriptor?
            = getFirstMatch(scopeChain, name, VARIABLE_DESCRIPTOR_SCOPE_SELECTOR)

    override fun getFunctions(name: Name): Set<FunctionDescriptor>
            = getFromAllScopes(scopeChain, name, NAMED_FUNCTION_SCOPE_SELECTOR)

    override fun getImplicitReceiversHierarchy(): List<ReceiverParameterDescriptor> {
        if (implicitReceiverHierarchy == null) {
            val result = ArrayList<ReceiverParameterDescriptor>()
            for (jetScope in scopeChain) {
                result.addAll(jetScope.getImplicitReceiversHierarchy())
            }
            result.trimToSize()
            implicitReceiverHierarchy = result
        }
        return implicitReceiverHierarchy!!
    }

    override fun getContainingDeclaration(): DeclarationDescriptor = containingDeclaration!!

    override fun getDeclarationsByLabel(labelName: Name): Collection<DeclarationDescriptor> {
        val result = ArrayList<DeclarationDescriptor>()
        for (jetScope in scopeChain) {
            result.addAll(jetScope.getDeclarationsByLabel(labelName))
        }
        result.trimToSize()
        return result
    }

    override fun getDescriptors(kindFilter: (JetScope.DescriptorKind) -> Boolean,
                                nameFilter: (String) -> Boolean): Collection<DeclarationDescriptor> {
        if (_allDescriptors == null) {
            _allDescriptors = HashSet<DeclarationDescriptor>()
            for (scope in scopeChain) {
                _allDescriptors!!.addAll(scope.getDescriptors())
            }
        }
        return _allDescriptors!!
    }

    override fun getOwnDeclaredDescriptors(): Collection<DeclarationDescriptor> {
        throw UnsupportedOperationException()
    }

    override fun toString() = debugName

    override fun printScopeStructure(p: Printer) {
        p.println(javaClass.getSimpleName(), ": ", debugName, " {")
        p.pushIndent()

        for (scope in scopeChain) {
            scope.printScopeStructure(p)
        }

        p.popIndent()
        p.println("}")
    }
}
