package com.ulexzhong.lintrules.detector.standard;

import com.android.annotations.NonNull;
import com.android.resources.ResourceFolderType;
import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.Speed;

import java.util.Collections;
import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.Expression;
import lombok.ast.MethodInvocation;
import lombok.ast.Select;
import lombok.ast.StrictListAccessor;
import lombok.ast.StringLiteral;
import lombok.ast.VariableReference;

/**
 * Created by ulexzhong on 2018/3/9.
 * intent参数key必须以EXTRA_开头的宏定义
 */
public class IntentExtraKayDetector extends Detector implements Detector.JavaScanner {


    public static final Issue ISSUE=  Issue.create(
            "extraKey",
            "please avoid use hardcode defined intent extra key",
            "defined in another activity",
            Category.SECURITY,5, Severity.WARNING,
            new Implementation(IntentExtraKayDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    @Override
    public boolean appliesTo(ResourceFolderType folderType) {
        return true;
    }

    @Override
    public Speed getSpeed() {
        return Speed.FAST;
    }

    @Override
    public List<String> getApplicableMethodNames() {
        return Collections.singletonList("putExtra");
    }

    @Override
    public void visitMethod(JavaContext context, AstVisitor visitor, MethodInvocation node) {
       JavaParser.ResolvedNode resolvedNode=context.resolve(node);
        if(resolvedNode instanceof JavaParser.ResolvedMethod){
            JavaParser.ResolvedMethod method= (JavaParser.ResolvedMethod) resolvedNode;
            if (method.getContainingClass().isSubclassOf("android.content.Intent", false)
                    && method.getArgumentCount() == 2) {
                ensureExtraKey(context, node);
            }
        }
    }

    private static void ensureExtraKey(JavaContext context, @NonNull MethodInvocation node) {
        //获取method的参数值
        StrictListAccessor<Expression, MethodInvocation> accessor = node.astArguments();
        if (accessor.size() != 2) {
            return;
        }
        Expression expression = accessor.first();
        //当第一个参数值类型为String,这样是硬编码
        if (expression instanceof StringLiteral) {
            report(context,node,"please avoid use hardcode defined Intent.putExtra key");
            return;
        }
        //当第一个参数值类型为变量
        if (expression instanceof VariableReference) {
            //获取该变量的定义name
            String targetName = ((VariableReference) expression).astIdentifier().astValue();
            if (!targetName.startsWith("EXTRA_")) {
                report(context,node,"please defined intent extra key start with EXTRA_");
            }
        }
        //当第一个参数值是其他类的变量时
        if (expression instanceof Select) {
            String targetName = ((Select) expression).astIdentifier().astValue();
            if (!targetName.startsWith("EXTRA_")) {
                report(context,node,"please defined intent extra key start with EXTRA_");
            }
        }
    }

    private static void report(JavaContext context,MethodInvocation node,String msg){
        context.report(ISSUE,node,context.getLocation(node),msg);
    }
}
