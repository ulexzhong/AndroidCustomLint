package com.ulexzhong.lintrules.detector.performance;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNewExpression;

import java.util.Collections;
import java.util.List;

/**
 * Created by ulexzhong on 2018/3/30.
 * 禁止使用new Thread()直接创建线程
 */
public class ThreadCreateDetector extends Detector implements Detector.JavaPsiScanner {
    private static final String THREAD_CLS = "java.lang.Thread";
    private static final String ISSUE_EXPLANATION = "避免直接调用new Thread()创建线程，建议使用AsyncTask或统一的线程管理工具类";

    public static final Issue ISSUE = Issue.create("ThreadCreate",
            "避免直接调用new Thread()创建线程",
            ISSUE_EXPLANATION,
            Category.PERFORMANCE, 5, Severity.WARNING,
            new Implementation(ThreadCreateDetector.class, Scope.JAVA_FILE_SCOPE));


    @Override
    public List<String> applicableSuperClasses() {
        return Collections.singletonList(THREAD_CLS);
    }

    @Override
    public void checkClass(JavaContext context, PsiClass declaration) {
        super.checkClass(context, declaration);
        PsiMethod[] constructors = declaration.getConstructors();
        if (constructors.length > 0) {
//            System.out.println("Thread:" + declaration);
            PsiElement locationNode = JavaContext.findNameElement(declaration);
            if (locationNode == null) {
                locationNode = declaration;
            }
            context.report(ISSUE, context.getLocation(locationNode), ISSUE_EXPLANATION);
        }
    }
        @Override
        public List<String> getApplicableConstructorTypes() {
            return Collections.singletonList(THREAD_CLS);

        }

        @Override
        public void visitConstructor(JavaContext context, JavaElementVisitor visitor, PsiNewExpression node, PsiMethod constructor) {
            if (context.isEnabled(ISSUE)) {
                context.report(ISSUE, context.getLocation(node), ISSUE_EXPLANATION);
            }
        }
}
