package com.ulexzhong.lintrules.detector.standard;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;

import java.util.Collections;
import java.util.List;

/**
 * Created by ulexzhong on 2018/3/28.
 * 变量规范
 * static final 常量大写
 * static 变量 以s开头
 * 普通变量以m开头
 */
public class FieldNameDetector extends Detector implements Detector.JavaPsiScanner {

    public static final Issue ISSUE = Issue.create("FieldName",
            "Static final field should be all uppercase",
            "Static final field should be all uppercase as our specification",
            Category.CORRECTNESS,
            9,
            Severity.WARNING,
            new Implementation(FieldNameDetector.class,
                    Scope.JAVA_FILE_SCOPE));

    private static final String FIELD_NAME_PREFIX_STATIC = "s";
    private static final String FIELD_NAME_PREFIX_NOMAL = "m";

    @Override
    public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
        return Collections.<Class<? extends PsiElement>>singletonList(PsiField.class);//声明为检查成员变量类型节点
    }

    @Override
    public JavaElementVisitor createPsiVisitor(JavaContext context) {
        return new FieldChecker(context);//返回本检查器真正的检查执行者
    }

    private static class FieldChecker extends JavaElementVisitor {
        private JavaContext mContext;

        FieldChecker(JavaContext context) {
            this.mContext = context;
        }

        @Override
        public void visitField(PsiField field) {
            if (field == null) {
                return;
            }
//            System.out.println("field.name:" + field.getName());
            PsiModifierList modifierList = field.getModifierList();
            if (modifierList == null) {
                super.visitField(field);
                return;
            }
            String fieldName = field.getName();
            if (fieldName == null) {
                super.visitField(field);
                return;
            }
            if (modifierList.hasModifierProperty(PsiModifier.STATIC)) {
                if (modifierList.hasModifierProperty(PsiModifier.FINAL)) {//常量 static final
                    if (!isAllUpper(fieldName)) {
                        mContext.report(ISSUE, mContext.getLocation(field.getNameIdentifier()), "field name with static final should be all uppercase");
                    }
                } else {
                    if (!fieldName.startsWith(FIELD_NAME_PREFIX_STATIC)) {
                        mContext.report(ISSUE, mContext.getLocation(field.getNameIdentifier()), "field name with static should be started with s");
                    }
                }
            }else{
                if(!fieldName.startsWith(FIELD_NAME_PREFIX_NOMAL)){
                    mContext.report(ISSUE, mContext.getLocation(field.getNameIdentifier()), "field name with static should be started with m");
                }
            }
            super.visitField(field);
        }
    }

    private static boolean isAllUpper(String str) {
        if (str == null) {
            return true;
        }
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            if (Character.isLowerCase(aChar)) {//只判断有没有小写字母，不限定其它特殊字符
                return false;
            }
        }
        return true;
    }
}