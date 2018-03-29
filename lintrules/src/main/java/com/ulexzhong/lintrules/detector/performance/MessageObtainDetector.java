package com.ulexzhong.lintrules.detector.performance;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.util.Collections;
import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.ConstructorInvocation;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.Node;

/**
 * Created by ulexzhong on 2018/3/7.
 * 避免使用new Message()
 */
public class MessageObtainDetector extends Detector implements Detector.JavaScanner {

    public static final Issue ISSUE = Issue.create("MessageUse",
            "You should not call 'new Message()' directly.",
            "You should not call 'new message()' directly.Instead,you should use 'handler.obtainMessage'or'Message.obtain()'.",
            Category.SECURITY,
            5,
            Severity.WARNING,
            new Implementation(MessageObtainDetector.class, Scope.JAVA_FILE_SCOPE))
            .addMoreInfo("这个能不能显示出来啊啊啊啊啊 啊，http://www.doubleencore.com/2013/05/layout-inflation-as-intended");

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(ConstructorInvocation.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        //        return new ForwardingAstVisitor() {
        //            @Override
        //            public boolean visitConstructorInvocation(ConstructorInvocation node) {
        //
        //                //                context.isEnabled(ISSUE);
        //                JavaParser.ResolvedNode resolvedNode = context.resolve(node.astTypeReference());
        //                if (resolvedNode != null && resolvedNode instanceof JavaParser.ResolvedClass) {
        //                    JavaParser.ResolvedClass resolvedClass = (JavaParser.ResolvedClass) resolvedNode;
        //                    if (resolvedClass.matches("android.os.Message")) {
        //                        context.report(ISSUE, node, context.getLocation(node), "You should not call 'new Message()' directly");
        //                        return true;
        //                    }
        //                }
        //
        //
        //                //                JavaParser.ResolvedNode resolvedNode=context.resolve(node.astTypeReference());
        //                //                if(resolvedNode!=null&&resolvedNode instanceof JavaParser.ResolvedClass) {
        //                //                    JavaParser.ResolvedClass resolvedClass = (JavaParser.ResolvedClass) resolvedNode;
        //                //                    if (resolvedClass.isSubclassOf("android.os.Message", false)) {
        //                //                        context.report(ISSUE, node, context.getLocation(node), "You should not call 'new Message()' directly");
        //                //                        return true;
        //                //                    }
        //                //                }
        //
        //                return super.visitConstructorInvocation(node);
        //            }
        //        };
        return new CheckVisitor(context);
    }

    private static class CheckVisitor extends ForwardingAstVisitor {
        private final boolean mCheck;
        private final JavaContext mContext;

        CheckVisitor(JavaContext context) {
            mContext = context;
            mCheck = context.isEnabled(ISSUE);
        }

        @Override
        public boolean visitConstructorInvocation(ConstructorInvocation node) {
            if (!mCheck) {
                return super.visitConstructorInvocation(node);
            }
            //                context.isEnabled(ISSUE);
            JavaParser.ResolvedNode resolvedNode = mContext.resolve(node.astTypeReference());
            if (resolvedNode != null && resolvedNode instanceof JavaParser.ResolvedClass) {
                JavaParser.ResolvedClass resolvedClass = (JavaParser.ResolvedClass) resolvedNode;
                if (resolvedClass.matches("android.os.Message")) {
                    mContext.report(ISSUE, node, mContext.getLocation(node), "You should not call 'new Message()' directly");
                    return true;
                }
            }


            //                JavaParser.ResolvedNode resolvedNode=context.resolve(node.astTypeReference());
            //                if(resolvedNode!=null&&resolvedNode instanceof JavaParser.ResolvedClass) {
            //                    JavaParser.ResolvedClass resolvedClass = (JavaParser.ResolvedClass) resolvedNode;
            //                    if (resolvedClass.isSubclassOf("android.os.Message", false)) {
            //                        context.report(ISSUE, node, context.getLocation(node), "You should not call 'new Message()' directly");
            //                        return true;
            //                    }
            //                }

            return super.visitConstructorInvocation(node);
        }
    }
}
