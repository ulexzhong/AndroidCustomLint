package com.ulexzhong.lintrules.detector.standard;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.Speed;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.ClassDeclaration;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.Node;

/**
 * Created by ulexzhong on 2018/3/12.
 * Activity必须继承BaseActivity
 */
public class BaseActivityDetector extends Detector implements Detector.JavaScanner {



    private static final String OBJECT = "java.lang.Object";

    private static final List<String> INVALID_ACTIVITY_PARENT_LIST = Arrays.asList("android.app.Activity",
            "android.app.AppCompatActivity");
    private static final List<String> VALID_ACTIVITY_PARENT_LIST = Arrays.asList("BaseActivity");
    private static final String VALID_ACTIVITY_TIPS = getValidParentTips();

    public static final Issue ISSUE = Issue.create("BaseActivityUse",
            "please extends BaseActivity instead",
            "please avoid extends Activity/AppCompatActivity,instead of "+VALID_ACTIVITY_TIPS,
            Category.SECURITY, 5, Severity.WARNING,
            new Implementation(BaseActivityDetector.class, Scope.JAVA_FILE_SCOPE));


    private JavaContext mContext;

    @Override
    public Speed getSpeed() {
        return Speed.FAST;
    }

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(ClassDeclaration.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        mContext = context;
        return new ForwardingAstVisitor() {
            @Override
            public boolean visitClassDeclaration(ClassDeclaration node) {
                JavaParser.ResolvedNode resolvedNode = context.resolve(node);
                if (resolvedNode instanceof JavaParser.ResolvedClass) {

                    for (String validName : VALID_ACTIVITY_PARENT_LIST) {
                        if (resolvedNode.getName().contains(validName)) {
                            return super.visitClassDeclaration(node);
                        }
                    }

                    JavaParser.ResolvedClass resolvedClass = ((JavaParser.ResolvedClass) resolvedNode).getSuperClass();

                    recursiveSuperClass(resolvedClass, node);
                }

                return super.visitClassDeclaration(node);
            }

            private JavaParser.ResolvedClass recursiveSuperClass(JavaParser.ResolvedClass curClass, ClassDeclaration node) {
                //到最顶层object
                if (curClass.getName().equals(OBJECT)) {
                    return curClass;
                }
                if (checkActivityRules(curClass, node)) {
                    return curClass;
                }
                return recursiveSuperClass(curClass.getSuperClass(), node);
            }

            private boolean checkActivityRules(JavaParser.ResolvedClass curClass, ClassDeclaration node) {
                //符合要求的基类
                for (String validName : VALID_ACTIVITY_PARENT_LIST) {
                    if (curClass.getName().contains(validName)) {
                        return true;
                    }
                }
                //没有继承相关基类，提示
                for (String invalidName : INVALID_ACTIVITY_PARENT_LIST) {
                    if (curClass.matches(invalidName)) {
                        mContext.report(ISSUE, node, mContext.getLocation(node.astName()), "avoid extends " + invalidName + ",please extends "+VALID_ACTIVITY_TIPS+" instead");
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private static String getValidParentTips() {
        StringBuilder sb = new StringBuilder();
        for (String validName : VALID_ACTIVITY_PARENT_LIST) {
            if (!sb.toString().isEmpty()) {
                sb.append("/");
            }
            sb.append(validName);
        }
        return sb.toString();
    }
}
