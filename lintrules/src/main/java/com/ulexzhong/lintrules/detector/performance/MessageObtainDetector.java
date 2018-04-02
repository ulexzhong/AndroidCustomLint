package com.ulexzhong.lintrules.detector.performance;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNewExpression;

import java.util.Collections;
import java.util.List;

/**
 * Created by ulexzhong on 2018/3/7.
 * 避免使用new Message()
 */
public class MessageObtainDetector extends Detector implements Detector.JavaPsiScanner {
    private static final String MESSAGE = "android.os.Message";
    private static final String ISSUE_EXPLANATION = "You should not call 'new message()' directly.Instead,you should use 'handler.obtainMessage'or'Message.obtain()'.";

    public static final Issue ISSUE = Issue.create("MessageUse",
            "You should not call 'new Message()' directly.",
            ISSUE_EXPLANATION,
            Category.PERFORMANCE,
            5,
            Severity.WARNING,
            new Implementation(MessageObtainDetector.class, Scope.JAVA_FILE_SCOPE));


    @Override
    public List<String> getApplicableConstructorTypes() {
        return Collections.singletonList(MESSAGE);
    }

    @Override
    public void visitConstructor(JavaContext context, JavaElementVisitor visitor, PsiNewExpression node, PsiMethod constructor) {
        if (context.isEnabled(ISSUE)) {
            context.report(ISSUE, context.getLocation(node), ISSUE_EXPLANATION);
        }
    }
}
//public class MessageObtainDetector extends Detector implements Detector.JavaScanner {
//
//    public static final Issue ISSUE = Issue.create("MessageUse",
//            "You should not call 'new Message()' directly.",
//            "You should not call 'new message()' directly.Instead,you should use 'handler.obtainMessage'or'Message.obtain()'.",
//            Category.SECURITY,
//            5,
//            Severity.WARNING,
//            new Implementation(MessageObtainDetector.class, Scope.JAVA_FILE_SCOPE));
//
//    @Override
//    public List<Class<? extends Node>> getApplicableNodeTypes() {
//        return Collections.<Class<? extends Node>>singletonList(ConstructorInvocation.class);
//    }
//
//    @Override
//    public AstVisitor createJavaVisitor(final JavaContext context) {
//        return new CheckVisitor(context);
//    }
//
//    private static class CheckVisitor extends ForwardingAstVisitor {
//        private final JavaContext mContext;
//
//        CheckVisitor(JavaContext context) {
//            mContext = context;
//        }
//
//        @Override
//        public boolean visitConstructorInvocation(ConstructorInvocation node) {
//            if (!mContext.isEnabled(ISSUE)) {
//                return super.visitConstructorInvocation(node);
//            }
//            JavaParser.ResolvedNode resolvedNode = mContext.resolve(node.astTypeReference());
//            if (resolvedNode != null && resolvedNode instanceof JavaParser.ResolvedClass) {
//                JavaParser.ResolvedClass resolvedClass = (JavaParser.ResolvedClass) resolvedNode;
//                if (resolvedClass.matches("android.os.Message")) {
//                    mContext.report(ISSUE, node, mContext.getLocation(node), "You should not call 'new Message()' directly");
//                    return true;
//                }
//            }
//
//
//            //                JavaParser.ResolvedNode resolvedNode=context.resolve(node.astTypeReference());
//            //                if(resolvedNode!=null&&resolvedNode instanceof JavaParser.ResolvedClass) {
//            //                    JavaParser.ResolvedClass resolvedClass = (JavaParser.ResolvedClass) resolvedNode;
//            //                    if (resolvedClass.isSubclassOf("android.os.Message", false)) {
//            //                        context.report(ISSUE, node, context.getLocation(node), "You should not call 'new Message()' directly");
//            //                        return true;
//            //                    }
//            //                }
//
//            return super.visitConstructorInvocation(node);
//        }
//    }
//}
