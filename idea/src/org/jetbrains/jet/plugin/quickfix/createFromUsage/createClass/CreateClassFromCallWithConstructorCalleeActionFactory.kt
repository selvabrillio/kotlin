package org.jetbrains.jet.plugin.quickfix.createFromUsage.createClass

import org.jetbrains.jet.lang.diagnostics.Diagnostic
import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.jet.plugin.project.AnalyzerFacadeWithCache
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.TypeInfo
import org.jetbrains.jet.lang.resolve.BindingContext
import org.jetbrains.jet.lang.types.Variance
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns
import org.jetbrains.jet.plugin.quickfix.createFromUsage.callableBuilder.ParameterInfo
import org.jetbrains.jet.plugin.quickfix.JetSingleIntentionActionFactory
import org.jetbrains.jet.lang.psi.JetFile
import org.jetbrains.jet.lang.psi.JetAnnotationEntry
import org.jetbrains.jet.lang.psi.JetUserType
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.jet.lang.psi.JetDelegatorToSuperCall
import org.jetbrains.jet.lang.psi.JetDelegatorToThisCall
import org.jetbrains.jet.lang.psi.JetCallElement
import org.jetbrains.jet.lang.psi.psiUtil.isAncestor
import org.jetbrains.jet.lang.psi.JetConstructorCalleeExpression
import org.jetbrains.jet.lang.psi.JetSimpleNameExpression
import java.util.Collections

public object CreateClassFromCallWithConstructorCalleeActionFactory : JetSingleIntentionActionFactory() {
    override fun createAction(diagnostic: Diagnostic): IntentionAction? {
        val diagElement = diagnostic.getPsiElement()

        val callElement = PsiTreeUtil.getParentOfType(
                diagElement,
                javaClass<JetAnnotationEntry>(),
                javaClass<JetDelegatorToSuperCall>()
        ) as? JetCallElement ?: return null

        val isAnnotation = callElement is JetAnnotationEntry

        val callee = callElement.getCalleeExpression() as? JetConstructorCalleeExpression ?: return null
        val calleeRef = callee.getConstructorReferenceExpression() ?: return null

        if (!calleeRef.isAncestor(diagElement)) return null

        val file = callElement.getContainingFile() as? JetFile ?: return null
        val typeRef = callee.getTypeReference() ?: return null
        val userType = typeRef.getTypeElement() as? JetUserType ?: return null

        val context = AnalyzerFacadeWithCache.getContextForElement(userType)

        val qualifier = userType.getQualifier()?.getReferenceExpression()
        val qualifierDescriptor = qualifier?.let { context[BindingContext.REFERENCE_TARGET, it] }

        val targetParent = getTargetParentByQualifier(file, qualifier != null, qualifierDescriptor) ?: return null

        val anyType = KotlinBuiltIns.getInstance().getNullableAnyType()
        val parameterInfos = callElement.getValueArguments().map {
            ParameterInfo(
                    it.getArgumentExpression()?.let { TypeInfo(it, Variance.IN_VARIANCE) } ?: TypeInfo(anyType, Variance.IN_VARIANCE),
                    it.getArgumentName()?.getReferenceExpression()?.getReferencedName()
            )
        }

        val typeArgumentInfos = when {
            isAnnotation -> Collections.emptyList<TypeInfo>()
            else -> callElement.getTypeArguments().map { TypeInfo(it.getTypeReference(), Variance.INVARIANT) }
        }

        val classInfo = ClassInfo(
                kind = if (isAnnotation) ClassKind.ANNOTATION_CLASS else ClassKind.PLAIN_CLASS,
                name = calleeRef.getReferencedName(),
                targetParent = targetParent,
                expectedTypeInfo = TypeInfo.Empty,
                parameterInfos = parameterInfos,
                open = true,
                typeArguments = typeArgumentInfos
        )
        return CreateClassFromUsageFix(callElement, classInfo)
    }
}