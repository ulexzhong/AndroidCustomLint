package com.ulexzhong.lintrules.detector.performance;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.io.File;
import java.util.Collections;
import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.EnumDeclaration;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.Node;

/**
 * Created by ulexzhong on 2018/3/9.
 * 避免使用Enum
 */
public class EnumDetector extends Detector implements Detector.JavaScanner {

    public static final Issue ISSUE=Issue.create("EnumUse",
            "please avoid use enum",
            "please avoid use enum,instead of interface",
            Category.SECURITY,5, Severity.WARNING,
            new Implementation(EnumDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public boolean appliesTo(Context context, File file) {
        return true;
    }

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(EnumDeclaration.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        return new ForwardingAstVisitor() {
            @Override
            public boolean visitEnumDeclaration(EnumDeclaration node) {
                context.report(ISSUE,node,context.getLocation(node.astName()),"please avoid use enum");
                return super.visitEnumDeclaration(node);
            }
        };
    }
}
