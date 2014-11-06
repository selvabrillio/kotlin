package org.jetbrains.jet.plugin.quickfix.createFromUsage.createClass

import org.jetbrains.jet.plugin.quickfix.JetIntentionActionsFactory
import org.jetbrains.jet.lang.diagnostics.Diagnostic
import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.jet.plugin.quickfix.QuickFixUtil
import java.util.Collections
import org.jetbrains.jet.lang.psi.JetUserType
import org.jetbrains.jet.lang.psi.JetFile
import org.jetbrains.jet.lang.resolve.BindingContext
import org.jetbrains.jet.plugin.caches.resolve.getAnalysisResults
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.TypeInfo
import org.jetbrains.jet.lang.types.Variance
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor
import com.intellij.psi.PsiElement
import org.jetbrains.jet.lang.psi.JetDelegatorToSuperClass
import org.jetbrains.jet.lang.psi.JetDelegatorToSuperCall
import org.jetbrains.jet.lang.psi.JetConstructorCalleeExpression

public object CreateClassFromTypeReferenceActionFactory: JetIntentionActionsFactory() {
    override fun doCreateActions(diagnostic: Diagnostic): List<IntentionAction> {
        val userType = QuickFixUtil.getParentElementOfType(diagnostic, javaClass<JetUserType>()) ?: return Collections.emptyList()
        val typeArguments = userType.getTypeArgumentsAsTypes()

        val name = userType.getReferencedName() ?: return Collections.emptyList()

        val typeRefParent = userType.getParent()?.getParent()
        if (typeRefParent is JetConstructorCalleeExpression) return Collections.emptyList()

        val traitExpected = typeRefParent is JetDelegatorToSuperClass

        val exhaust = userType.getAnalysisResults()
        val context = exhaust.getBindingContext()

        val file = userType.getContainingFile() as? JetFile ?: return Collections.emptyList()

        val isQualifier = (userType.getParent() as? JetUserType)?.let { it.getQualifier() == userType } ?: false
        val qualifier = userType.getQualifier()?.getReferenceExpression()
        val qualifierDescriptor = qualifier?.let { context[BindingContext.REFERENCE_TARGET, it] }

        val targetParent = getTargetParentByQualifier(file, qualifier != null, qualifierDescriptor) ?: return Collections.emptyList()

        val possibleKinds = when {
            traitExpected -> Collections.singletonList(ClassKind.TRAIT)
            else -> ClassKind.values().filter {
                val noTypeArguments = typeArguments.isEmpty()
                when (it) {
                    ClassKind.OBJECT -> noTypeArguments && isQualifier
                    ClassKind.ANNOTATION_CLASS -> noTypeArguments && !isQualifier
                    ClassKind.ENUM_ENTRY -> false
                    ClassKind.ENUM_CLASS -> noTypeArguments
                    else -> true
                }
            }
        }

        return possibleKinds.map {
            val classInfo = ClassInfo(
                    kind = it,
                    name = name,
                    targetParent = targetParent,
                    expectedTypeInfo = TypeInfo.Empty,
                    typeArguments = typeArguments.map { TypeInfo(it, Variance.INVARIANT) }
            )
            CreateClassFromUsageFix(userType, classInfo)
        }
    }
}