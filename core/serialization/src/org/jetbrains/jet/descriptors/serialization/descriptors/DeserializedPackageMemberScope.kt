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

package org.jetbrains.jet.descriptors.serialization.descriptors

import org.jetbrains.jet.descriptors.serialization.PackageData
import org.jetbrains.jet.descriptors.serialization.ProtoBuf
import org.jetbrains.jet.descriptors.serialization.context.*
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor
import org.jetbrains.jet.lang.descriptors.PackageFragmentDescriptor
import org.jetbrains.jet.lang.descriptors.ReceiverParameterDescriptor
import org.jetbrains.jet.lang.resolve.name.ClassId
import org.jetbrains.jet.lang.resolve.name.Name
import org.jetbrains.jet.lang.resolve.scopes.JetScope
import org.jetbrains.jet.utils.addIfNotNull


public fun DeserializedPackageMemberScope(packageDescriptor: PackageFragmentDescriptor,
                                          packageData: PackageData,
                                          context: DeserializationGlobalContext,
                                          classNames: () -> Collection<Name>): DeserializedPackageMemberScope
        = DeserializedPackageMemberScope(packageDescriptor, packageData.getPackageProto(), context.withNameResolver(packageData.getNameResolver()), classNames)

public open class DeserializedPackageMemberScope(
        packageDescriptor: PackageFragmentDescriptor,
        proto: ProtoBuf.Package,
        private val context: DeserializationContext,
        classNames: () -> Collection<Name>)
: DeserializedMemberScope(context.withTypes(packageDescriptor), proto.getMemberList()) {

    private val packageFqName = packageDescriptor.fqName
    private val classNames = context.storageManager.createLazyValue(classNames)

    override fun getDescriptors(kindFilterMask: Int, nameFilter: (Name) -> Boolean)
            = computeDescriptors(kindFilterMask, nameFilter)

    override fun getClassDescriptor(name: Name) = context.deserializeClass(ClassId(packageFqName, name))

    override fun addClassDescriptors(result: MutableCollection<DeclarationDescriptor>, nameFilter: (Name) -> Boolean) {
        for (className in classNames()) {
            if (nameFilter(className)) {
                result.addIfNotNull(getClassDescriptor(className))
            }
        }
    }

    override fun addNonDeclaredDescriptors(result: MutableCollection<DeclarationDescriptor>) {
        // Do nothing
    }

    override fun getImplicitReceiver(): ReceiverParameterDescriptor? = null
}
