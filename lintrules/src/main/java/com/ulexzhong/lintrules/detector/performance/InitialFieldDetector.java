package com.ulexzhong.lintrules.detector.performance;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by ulexzhong on 2018/3/28.
 * 初始化变量时赋默认值提示
 */
public class InitialFieldDetector extends Detector implements Detector.JavaPsiScanner {

    public static final Issue ISSUE = Issue.create("InitialField",
            "初始化变量时不需要赋默认值",
            "初始化变量时不需要赋默认值，避免init<>上增加无用字节码",
            Category.PERFORMANCE, 5, Severity.WARNING,
            new Implementation(InitialFieldDetector.class, Scope.JAVA_FILE_SCOPE));


    private static final String DEFAULT_VALUE_FALSE = "false";
    private static final String DEFAULT_VALUE_0 = "0";
    private static final String DEFAULT_VALUE_CHAR = "'\\u0000'";
    private static final String DEFAULT_VALUE_NULL = "null";

    @Override
    public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
        return Collections.<Class<? extends PsiElement>>singletonList(PsiField.class);
    }

    @Override
    public JavaElementVisitor createPsiVisitor(JavaContext context) {
        return new InitialChecker(context);
    }

    private static class InitialChecker extends JavaElementVisitor {
        private JavaContext mContext;

        InitialChecker(JavaContext context) {
            mContext = context;
        }

        @Override
        public void visitField(PsiField field) {
            if (field == null) {
                return;
            }
            if (isConstant(field)) {//常量不提示
                super.visitField(field);
                return;
            }

//            System.out.println("InitialChecker.field:" + field
//                    + ",hasInitializer:" + field.hasInitializer());
            if (field.hasInitializer()) {
                PsiExpression initializer = field.getInitializer();
                //暂时只处理了该类型，其他后期处理
                if (initializer instanceof PsiLiteralExpression) {
                    PsiLiteralExpression literalExpression = (PsiLiteralExpression) initializer;

                    String typeName = field.getType().getCanonicalText();
                    String value = literalExpression.getText();
//                    System.out.println("InitialChecker.field:" + field + ".value：" + value + ".type:" + typeName + ".initializer:" + initializer);
                    if (value == null || value.length() == 0) {
                        super.visitField(field);
                        return;
                    }
                    if (hasSetDefaultValue(typeName, value)) {
                        mContext.report(ISSUE, mContext.getLocation(initializer)
                                , String.format(Locale.getDefault(), "%s类型为%s不需要赋值%s", field.getName(), typeName, value));
                    }
                }
            }
            super.visitField(field);
        }

        private boolean hasSetDefaultValue(String typeName, String value) {
            if (JavaParser.TYPE_INT.equals(typeName)
                    || JavaParser.TYPE_SHORT.equals(typeName)
                    || JavaParser.TYPE_BYTE.equals(typeName)) {//int|short|byte
                return DEFAULT_VALUE_0.equals(value);
            }
            if (JavaParser.TYPE_LONG.equals(typeName)) {//long
                return Long.compare(Long.valueOf(value), 0L) == 0;
            }
            if (JavaParser.TYPE_CHAR.equals(typeName)) {//char
                return DEFAULT_VALUE_CHAR.equals(value);
            }
            if (JavaParser.TYPE_FLOAT.equals(typeName)) {//float
                return Float.compare(Float.valueOf(value), 0.0f) == 0;
            }
            if (JavaParser.TYPE_DOUBLE.equals(typeName)) {//double
                return Double.compare(Double.valueOf(value), 0.0d) == 0;
            }
            if (JavaParser.TYPE_BOOLEAN.equals(typeName)) {//boolean
                return DEFAULT_VALUE_FALSE.equals(value);
            }

            return DEFAULT_VALUE_NULL.equals(value); //null
        }

        private boolean isConstant(PsiField field) {
            PsiModifierList modifierList = field.getModifierList();
            return modifierList != null
                    && modifierList.hasModifierProperty(PsiModifier.STATIC)
                    && modifierList.hasModifierProperty(PsiModifier.FINAL);
        }
    }
}
