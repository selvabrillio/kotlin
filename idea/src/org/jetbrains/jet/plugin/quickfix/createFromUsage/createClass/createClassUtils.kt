package org.jetbrains.jet.plugin.quickfix.createFromUsage.createClass

import org.jetbrains.jet.lang.psi.JetFile
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor
import com.intellij.psi.PsiElement
import org.jetbrains.jet.lang.descriptors.ClassDescriptor
import org.jetbrains.jet.lang.descriptors.PackageViewDescriptor
import org.jetbrains.jet.plugin.codeInsight.DescriptorToDeclarationUtil
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiDirectory
import org.jetbrains.jet.lang.psi.JetElement
import org.jetbrains.jet.lang.psi.JetExpression
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.guessTypes
import org.jetbrains.jet.lang.resolve.BindingContext
import org.jetbrains.jet.lang.descriptors.ModuleDescriptor
import org.jetbrains.jet.lang.types.TypeUtils
import org.jetbrains.jet.lang.types.checker.JetTypeChecker
import org.jetbrains.jet.lang.descriptors.TypeParameterDescriptor
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.TypeInfo
import org.jetbrains.jet.lang.types.Variance
import org.jetbrains.jet.lang.psi.Call
import org.jetbrains.jet.lang.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.jet.lang.resolve.scopes.receivers.Qualifier
import org.jetbrains.jet.plugin.util.ProjectRootsUtil
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.substitutionFree
import org.jetbrains.jet.lang.resolve.DescriptorUtils
import org.jetbrains.jet.lang.resolve.DescriptorToSourceUtils

private fun String.checkClassName(): Boolean = isNotEmpty() && Character.isUpperCase(first())

private fun getTargetParentByQualifier(
        file: JetFile,
        isQualified: Boolean,
        qualifierDescriptor: DeclarationDescriptor?): PsiElement? {
    val project = file.getProject()

    val targetParent = when {
        !isQualified -> file

        qualifierDescriptor is ClassDescriptor -> {
            DescriptorToDeclarationUtil.getDeclaration(project, qualifierDescriptor)
        }

        qualifierDescriptor is PackageViewDescriptor -> {
            val currentModule = ModuleUtilCore.findModuleForPsiElement(file)
            val targetFqName = qualifierDescriptor.getFqName()
            if (targetFqName != file.getPackageFqName()) {
                JavaPsiFacade.getInstance(project)
                        .findPackage(targetFqName.asString())
                        ?.getDirectories()
                        ?.firstOrNull { ModuleUtilCore.findModuleForPsiElement(it) == currentModule }
            }
            else file
        }

        else -> null
    } ?: return null
    return if (targetParent.isWritable()
               && ProjectRootsUtil.isInProjectOrLibSource(targetParent)
               && (targetParent is PsiDirectory || targetParent is JetElement)) return targetParent else null
}

private fun getTargetParentByCall(call: Call, file: JetFile): PsiElement? {
    val receiver = call.getExplicitReceiver()
    return when (receiver) {
        null, ReceiverValue.NO_RECEIVER -> getTargetParentByQualifier(file, false, null)
        is Qualifier -> getTargetParentByQualifier(file, true, receiver.resultingDescriptor)
        else -> getTargetParentByQualifier(file, true, receiver.getType().getConstructor().getDeclarationDescriptor())
    }
}

private fun isInnerClassExpected(call: Call): Boolean {
    val receiver = call.getExplicitReceiver()
    return receiver != null && receiver != ReceiverValue.NO_RECEIVER && receiver !is Qualifier
}

private fun JetExpression.getInheritableTypeInfo(
        context: BindingContext,
        moduleDescriptor: ModuleDescriptor,
        containingDeclaration: PsiElement): Pair<TypeInfo, (ClassKind) -> Boolean> {
    val types = guessTypes(context, moduleDescriptor, false)
    if (types.size != 1) return TypeInfo.Empty to { classKind -> true }

    val type = types.first()
    val descriptor = type.getConstructor().getDeclarationDescriptor()

    val canHaveSubtypes = TypeUtils.canHaveSubtypes(JetTypeChecker.DEFAULT, type)
    val isEnum = DescriptorUtils.isEnumClass(descriptor)

    if (!(canHaveSubtypes || isEnum)
        || descriptor is TypeParameterDescriptor) return TypeInfo.Empty to { classKind -> false }

    return TypeInfo.ByType(type, Variance.OUT_VARIANCE) to { classKind ->
        when (classKind) {
            ClassKind.ENUM_ENTRY -> isEnum && containingDeclaration == DescriptorToSourceUtils.descriptorToDeclaration(descriptor)
            else -> canHaveSubtypes
        }
    }
}