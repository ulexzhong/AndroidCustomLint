package com.ulexzhong.lintrules.detector.standard;

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
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.MethodInvocation;
import lombok.ast.Node;

/**
 * Created by ulexzhong on 2018/3/5.
 * 避免使用System.out.println或者android.os.Log,提示用户使用团队内定制LogUtil
 */
public class LoggerDetector extends Detector implements Detector.JavaScanner {

    public static final Issue ISSUE=Issue.create("LogUse",
            "避免使用Log/System.out.println",
            "请使用LogUtils，避免在正式包中打印log",
            Category.CORRECTNESS,5, Severity.WARNING,
            new Implementation(LoggerDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(MethodInvocation.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        return new ForwardingAstVisitor() {
            @Override
            public boolean visitMethodInvocation(MethodInvocation node) {

                if (node.toString().startsWith("System.out.print")) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "请使用LogUtils，避免使用System.out.println");
                    return true;
                }

                JavaParser.ResolvedNode resolve = context.resolve(node);
                if (resolve instanceof JavaParser.ResolvedMethod) {
                    JavaParser.ResolvedMethod method = (JavaParser.ResolvedMethod) resolve;
                    // 方法所在的类校验
                    JavaParser.ResolvedClass containingClass = method.getContainingClass();
                    if (containingClass.matches("android.util.Log")) {
                        context.report(ISSUE, node, context.getLocation(node),
                                "请使用LogUtils，避免使用Log");
                        return true;
                    }
                }
                return super.visitMethodInvocation(node);
            }
        };
    }
}
