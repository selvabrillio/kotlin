package org.jetbrains.jet.plugin.quickfix.createFromUsage.createClass

import org.jetbrains.jet.lang.diagnostics.Diagnostic
import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.jet.lang.psi.JetSimpleNameExpression
import org.jetbrains.jet.lang.psi.JetExpression
import org.jetbrains.jet.lang.psi.psiUtil.getParentByType
import org.jetbrains.jet.lang.psi.JetTypeReference
import java.util.Collections
import org.jetbrains.jet.plugin.quickfix.JetIntentionActionsFactory
import org.jetbrains.jet.lang.psi.JetClass
import org.jetbrains.jet.lang.psi.JetQualifiedExpression
import org.jetbrains.jet.lang.psi.psiUtil.getAssignmentByLHS
import org.jetbrains.jet.lang.psi.JetCallExpression
import org.jetbrains.jet.lang.psi.JetImportDirective
import org.jetbrains.jet.plugin.caches.resolve.getAnalysisResults
import org.jetbrains.jet.lang.resolve.calls.callUtil.getCall
import org.jetbrains.jet.lang.psi.JetFile
import org.jetbrains.jet.lang.psi.psiUtil.getReceiverExpression
import org.jetbrains.jet.lang.resolve.BindingContext
import org.jetbrains.jet.lang.psi.psiUtil.getQualifiedElementSelector
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.TypeInfo
import org.jetbrains.jet.lang.psi.JetReferenceExpression
import org.jetbrains.jet.lang.resolve.DescriptorToSourceUtils
import java.util.Arrays

public object CreateClassFromReferenceExpressionActionFactory : JetIntentionActionsFactory() {
    override fun doCreateActions(diagnostic: Diagnostic): List<IntentionAction> {
        val refExpr = diagnostic.getPsiElement() as? JetSimpleNameExpression ?: return Collections.emptyList()
        if (refExpr.getParentByType(javaClass<JetTypeReference>()) != null) return Collections.emptyList()

        val file = refExpr.getContainingFile() as? JetFile ?: return Collections.emptyList()

        val name = refExpr.getReferencedName()
        if (!name.checkClassName()) return Collections.emptyList()

        val inImport = refExpr.getParentByType(javaClass<JetImportDirective>()) != null

        val exhaust = refExpr.getAnalysisResults()
        val context = exhaust.getBindingContext()

        val fullCallExpr = refExpr.getParent()?.let {
            when {
                it is JetCallExpression && it.getCalleeExpression() == refExpr -> return Collections.emptyList()
                it is JetQualifiedExpression && it.getSelectorExpression() == refExpr -> it
                else -> refExpr
            }
        } as? JetExpression ?: return Collections.emptyList()

        if (inImport) {
            val receiverSelector = (fullCallExpr as? JetQualifiedExpression)?.getReceiverExpression()?.getQualifiedElementSelector() as? JetReferenceExpression
            val qualifierDescriptor = receiverSelector?.let { context[BindingContext.REFERENCE_TARGET, it] }

            val targetParent =
                    getTargetParentByQualifier(refExpr.getContainingJetFile(), receiverSelector != null, qualifierDescriptor)
                    ?: return Collections.emptyList()

            return ClassKind.values()
                    .filter { it != ClassKind.ENUM_ENTRY || (targetParent is JetClass && targetParent.isEnum()) }
                    .map {
                        val classInfo = ClassInfo(
                                kind = it,
                                name = name,
                                targetParent = targetParent,
                                expectedTypeInfo = TypeInfo.Empty
                        )
                        CreateClassFromUsageFix(refExpr, classInfo)
                    }
        }

        if (fullCallExpr.getAssignmentByLHS() != null) return Collections.emptyList()

        val call = refExpr.getCall(context) ?: return Collections.emptyList()
        val targetParent = getTargetParentByCall(call, file) ?: return Collections.emptyList()
        if (isInnerClassExpected(call)) return Collections.emptyList()

        val (expectedTypeInfo, filter) = fullCallExpr.getInheritableTypeInfo(context, exhaust.getModuleDescriptor(), targetParent)

        return Arrays.asList(ClassKind.OBJECT, ClassKind.ENUM_ENTRY)
                .filter {
                    filter(it) && when (it) {
                        ClassKind.OBJECT -> true
                        ClassKind.ENUM_ENTRY -> targetParent is JetClass && targetParent.isEnum()
                        else -> false
                    }
                }
                .map {
                    val classInfo = ClassInfo(
                            kind = it,
                            name = name,
                            targetParent = targetParent,
                            expectedTypeInfo = expectedTypeInfo
                    )
                    CreateClassFromUsageFix(refExpr, classInfo)
                }
    }
}