package com.ulexzhong.lintrules.detector.performance;

import com.android.SdkConstants;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.ast.AstVisitor;
import lombok.ast.BinaryExpression;
import lombok.ast.ConstructorInvocation;
import lombok.ast.Expression;
import lombok.ast.ExpressionStatement;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.Node;
import lombok.ast.StrictListAccessor;
import lombok.ast.TypeReference;
import lombok.ast.VariableDefinition;

/**
 * Created by ulexzhong on 2018/3/21.
 */

public class HashMapPerformanceDetector extends Detector implements Detector.JavaScanner {

    /**
     * Using HashMaps where SparseArray would be better
     */
    public static final Issue ISSUE = Issue.create(
            "UseSparseArrays",
            "HashMap can be replaced with SparseArray",
            "For maps where the keys are of type integer, it's typically more efficient to " +
                    "use the Android `SparseArray` API. This check identifies scenarios where you might " +
                    "want to consider using `SparseArray` instead of `HashMap` for better performance.\n" +
                    "\n" +
                    "This is *particularly* useful when the value types are primitives like ints, " +
                    "where you can use `SparseIntArray` and avoid auto-boxing the values from `int` to " +
                    "`Integer`.\n" +
                    "\n" +
                    "If you need to construct a `HashMap` because you need to call an API outside of " +
                    "your control which requires a `Map`, you can suppress this warning using for " +
                    "example the `@SuppressLint` annotation.",
            Category.PERFORMANCE,
            4,
            Severity.WARNING,
            new Implementation(HashMapPerformanceDetector.class, Scope.JAVA_FILE_SCOPE));


    private static final String INTEGER = "Integer";                        //$NON-NLS-1$
    private static final String BOOLEAN = "Boolean";                        //$NON-NLS-1$
    private static final String BYTE = "Byte";                              //$NON-NLS-1$
    private static final String LONG = "Long";                              //$NON-NLS-1$
    private static final String HASH_MAP = "HashMap";

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(ConstructorInvocation.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        return new ForwardingAstVisitor() {
            @Override
            public boolean visitConstructorInvocation(ConstructorInvocation node) {
                TypeReference reference = node.astTypeReference();
                String typeName = reference.astParts().last().astIdentifier().astValue();
                System.out.println("node:" + node + ",reference:" + reference + ",typeName:" + typeName);
                if (typeName.equals(HASH_MAP)) {
                    checkHashMap(context, node, reference);
                }
                return super.visitConstructorInvocation(node);
            }
        };
    }

    /**
     * Checks whether the given constructor call and type reference refers
     * to a HashMap constructor call that is eligible for replacement by a
     * SparseArray call instead
     */
    private void checkHashMap(JavaContext context, ConstructorInvocation node, TypeReference reference) {
        StrictListAccessor<TypeReference, TypeReference> types = reference.getTypeArguments();
        System.out.println("checkHashMap.node:"+node+",node.getParent:"+node.getParent()+",PPP:"+node.getParent().getParent());
        if (types == null || types.size() != 2) {
            /*
            JDK7 新写法
            HashMap<Integer, String> map2 = new HashMap<>();
            map2.put(1, "name");
            Map<Integer, String> map3 = new HashMap<>();
            map3.put(1, "name");
             */

            Node result = node.getParent().getParent();
//            System.out.println("checkHashMap.node:"+node+",node.getParent:"+node.getParent()+",PPP:"+result);
            if (result instanceof VariableDefinition) {
                TypeReference typeReference = ((VariableDefinition) result).astTypeReference();
                System.out.println("check.typeReference:"+typeReference);
                checkCore(context, result, typeReference);
                return;
            }

            if (result instanceof ExpressionStatement) {
                Expression expression = ((ExpressionStatement) result).astExpression();
                System.out.println("check.expression:"+expression);
                if (expression instanceof BinaryExpression) {
                    Expression left = ((BinaryExpression) expression).astLeft();
                    String fullTypeName = context.getType(left).getName();
                    System.out.println("check.BinaryExpression:"+left+",fullTypeName:"+fullTypeName);
                    checkCore2(context, result, fullTypeName);
                }
            }
        }
        // else --> lint本身已经检测
    }

    private void checkCore2(JavaContext context, Node node, String fullTypeName) {
        final Pattern p = Pattern.compile(".*<(.*),(.*)>");
        Matcher m = p.matcher(fullTypeName);
        if (m.find()) {
            String typeName = m.group(1).trim();
            String valueType = m.group(2).trim();
            int minSdk = context.getMainProject().getMinSdk();
            if (typeName.equals(INTEGER) || typeName.equals(BYTE)) {
                if (valueType.equals(INTEGER)) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "Use new SparseIntArray(...) instead for better performance");
                } else if (valueType.equals(LONG) && minSdk >= 18) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "Use new SparseLongArray(...) instead for better performance");
                } else if (valueType.equals(BOOLEAN)) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "Use new SparseBooleanArray(...) instead for better performance");
                } else {
                    context.report(ISSUE, node, context.getLocation(node),
                            String.format(
                                    "Use new SparseArray<%1$s>(...) instead for better performance",
                                    valueType));
                }
            } else if (typeName.equals(LONG) && (minSdk >= 16 ||
                    Boolean.TRUE == context.getMainProject().dependsOn(
                            SdkConstants.SUPPORT_LIB_ARTIFACT))) {
                boolean useBuiltin = minSdk >= 16;
                String message = useBuiltin ?
                        "Use new LongSparseArray(...) instead for better performance" :
                        "Use new android.support.v4.util.LongSparseArray(...) instead for better performance";
                context.report(ISSUE, node, context.getLocation(node),
                        message);
            }
        }
    }

    /**
     * copy from lint source code
     */
    private void checkCore(JavaContext context, Node node, TypeReference reference) {
        // reference.hasTypeArguments returns false where it should not
        StrictListAccessor<TypeReference, TypeReference> types = reference.getTypeArguments();
        if (types != null && types.size() == 2) {
            TypeReference first = types.first();
            String typeName = first.getTypeName();
            int minSdk = context.getMainProject().getMinSdk();
            if (typeName.equals(INTEGER) || typeName.equals(BYTE)) {
                String valueType = types.last().getTypeName();
                if (valueType.equals(INTEGER)) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "Use new SparseIntArray(...) instead for better performance");
                } else if (valueType.equals(LONG) && minSdk >= 18) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "Use new SparseLongArray(...) instead for better performance");
                } else if (valueType.equals(BOOLEAN)) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "Use new SparseBooleanArray(...) instead for better performance");
                } else {
                    context.report(ISSUE, node, context.getLocation(node),
                            String.format(
                                    "Use new SparseArray<%1$s>(...) instead for better performance",
                                    valueType));
                }
            } else if (typeName.equals(LONG) && (minSdk >= 16 ||
                    Boolean.TRUE == context.getMainProject().dependsOn(
                            SdkConstants.SUPPORT_LIB_ARTIFACT))) {
                boolean useBuiltin = minSdk >= 16;
                String message = useBuiltin ?
                        "Use new LongSparseArray(...) instead for better performance" :
                        "Use new android.support.v4.util.LongSparseArray(...) instead for better performance";
                context.report(ISSUE, node, context.getLocation(node),
                        message);
            }
        }

    }
}
