package com.ulexzhong.lintrules.detector.standard;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import lombok.ast.AstVisitor;
import lombok.ast.Expression;
import lombok.ast.MethodInvocation;
import lombok.ast.Node;
import lombok.ast.Select;
import lombok.ast.StrictListAccessor;

/**
 * Created by ulexzhong on 2018/3/15.
 * Activity/Fragment layout命名规范，要求前缀layout_
 */
public class ActivityFragmentLayoutNameDetector extends Detector implements Detector.JavaScanner {

    public static final Issue ISSUE = Issue.create("ActivityFragmentLayoutNameUse",
            "Activity/Fragment layout 请用前缀activity_/fragment_",
            "Activity/Fragment layout 请用前缀activity_/fragment_",
            Category.SECURITY, 5, Severity.WARNING,
            new Implementation(ActivityFragmentLayoutNameDetector.class, Scope.JAVA_FILE_SCOPE));

    private static final String ACTIVITY = "android.app.Activity";
    private static final String FRAGMENT_APP = "android.app.Fragment";
    private static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    private static final String PREFIX_ACTIVITY = "activity_";
    private static final String PREFIX_FRAGMENT = "fragment_";
    private static final String METHOD_SETCONTENTVIEW = "setContentView";
    private static final String METHOD_INFLATE = "inflate";

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList(METHOD_SETCONTENTVIEW, METHOD_INFLATE);
    }

    @Override
    public void visitMethod(JavaContext context, AstVisitor visitor, MethodInvocation node) {
        super.visitMethod(context, visitor, node);
        JavaParser.ResolvedNode resolvedNode = context.resolve(node);
        if (resolvedNode instanceof JavaParser.ResolvedMethod) {
            JavaParser.ResolvedMethod method = (JavaParser.ResolvedMethod) resolvedNode;
            JavaParser.ResolvedClass resolvedClass = method.getContainingClass();//该方法本属于类

            String methodName = method.getName();
            StrictListAccessor<Expression, MethodInvocation> accessor = node.astArguments();
            if (accessor == null || accessor.size() < 1) {
                return;
            }
            Expression expression = accessor.first();
            if (expression instanceof Select) {
                String targetName = ((Select) expression).astIdentifier().astValue();
                String prefix = null;
                String className = null;
                if (METHOD_SETCONTENTVIEW.equals(methodName)) {//setContentView
                    if (resolvedClass.isSubclassOf(ACTIVITY, false)&&!targetName.startsWith(PREFIX_ACTIVITY)) {//activity
                        prefix = PREFIX_ACTIVITY;
                        className = resolvedClass.getName();
                    }
                }
                else if (METHOD_INFLATE.equals(methodName)) {//inflate
                    if (method.getContainingClass().isSubclassOf("android.view.LayoutInflater", false)) {
                        Node surroundingClass = JavaContext.findSurroundingClass(node);//使用inflate的类
                        JavaParser.ResolvedNode surroundingNode = context.resolve(surroundingClass);
                        if (surroundingNode instanceof JavaParser.ResolvedClass) {
                            JavaParser.ResolvedClass sResolvedClass = (JavaParser.ResolvedClass) surroundingNode;
                            if (sResolvedClass.isSubclassOf(FRAGMENT_V4, false) || sResolvedClass.isSubclassOf(FRAGMENT_APP, false)) {//fragment
                                if (!targetName.startsWith(PREFIX_FRAGMENT)) {
                                    prefix = PREFIX_FRAGMENT;
                                    className = sResolvedClass.getSimpleName();
                                }
                            }
                        }
                    }
                }
                if (prefix != null) {
                    context.report(ISSUE, context.getLocation(node), String.format(Locale.getDefault(), "%s的layout命名需用前缀：%s", className, prefix));
                }
            }
        }
    }
}
