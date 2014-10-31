package org.jetbrains.jet.plugin.quickfix.createFromUsage.createClass

import org.jetbrains.jet.lang.diagnostics.Diagnostic
import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.jet.lang.psi.psiUtil.getParentByType
import org.jetbrains.jet.lang.psi.JetTypeReference
import org.jetbrains.jet.lang.psi.JetCallExpression
import org.jetbrains.jet.lang.psi.JetSimpleNameExpression
import org.jetbrains.jet.lang.psi.JetQualifiedExpression
import org.jetbrains.jet.lang.resolve.calls.callUtil.getCall
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.TypeInfo
import org.jetbrains.jet.lang.types.Variance
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.ParameterInfo
import org.jetbrains.jet.plugin.quickfix.JetSingleIntentionActionFactory
import org.jetbrains.jet.plugin.caches.resolve.getAnalysisResults
import org.jetbrains.jet.lang.psi.JetFile

public object CreateClassFromConstructorCallActionFactory: JetSingleIntentionActionFactory() {
    override fun createAction(diagnostic: Diagnostic): IntentionAction? {
        val diagElement = diagnostic.getPsiElement()
        if (diagElement.getParentByType(javaClass<JetTypeReference>()) != null) return null

        val callExpr = diagElement.getParent() as? JetCallExpression ?: return null
        if (callExpr.getCalleeExpression() != diagElement) return null

        val calleeExpr = callExpr.getCalleeExpression() as? JetSimpleNameExpression ?: return null

        val name = calleeExpr.getReferencedName()
        if (!name.checkClassName()) return null

        val callParent = callExpr.getParent()
        val fullCallExpr =
                if (callParent is JetQualifiedExpression && callParent.getSelectorExpression() == callExpr) callParent else callExpr

        val file = fullCallExpr.getContainingFile() as? JetFile ?: return null

        val exhaust = callExpr.getAnalysisResults()
        val context = exhaust.getBindingContext()

        val call = callExpr.getCall(context) ?: return null
        val targetParent = getTargetParentByCall(call, file) ?: return null
        val inner = isInnerClassExpected(call)

        val anyType = KotlinBuiltIns.getInstance().getNullableAnyType()
        val parameterInfos = callExpr.getValueArguments().map {
            ParameterInfo(
                    it.getArgumentExpression()?.let { TypeInfo(it, Variance.IN_VARIANCE) } ?: TypeInfo(anyType, Variance.IN_VARIANCE),
                    it.getArgumentName()?.getReferenceExpression()?.getReferencedName()
            )
        }

        val (expectedTypeInfo, filter) = fullCallExpr.getInheritableTypeInfo(context, exhaust.getModuleDescriptor(), targetParent)
        if (!filter(ClassKind.PLAIN_CLASS)) return null

        val classInfo = ClassInfo(
                kind = ClassKind.PLAIN_CLASS,
                name = name,
                targetParent = targetParent,
                expectedTypeInfo = expectedTypeInfo,
                inner = inner,
                typeArguments = callExpr.getTypeArguments().map { TypeInfo(it.getTypeReference(), Variance.INVARIANT) },
                parameterInfos = parameterInfos
        )
        return CreateClassFromUsageFix(callExpr, classInfo)
    }
}