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
import lombok.ast.Block;
import lombok.ast.ConstructorInvocation;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.MethodInvocation;
import lombok.ast.Node;
import lombok.ast.Try;

/**
 * Created by ulexzhong on 2018/3/19.
 * io/Cursor 检测是否正确使用close方法，避免内存泄露
 */
public class CloseStreamDetector extends Detector implements Detector.JavaScanner {
    public static final Issue ISSUE = Issue.create("CloseStreamUse",
            "请在finally中调用close方法",
            "请在finally中调用close方法，防止内存泄露",
            Category.SECURITY, 5, Severity.WARNING,
            new Implementation(CloseStreamDetector.class, Scope.JAVA_FILE_SCOPE));

    //调用方法的类
    private static final String[] sSupportSupperType = new String[]{
            "java.io.InputStream", "java.io.OutputStream", "android.database.Cursor"};

    private static final String CLOSE = "close";

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(ConstructorInvocation.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        return new ForwardingAstVisitor() {
            @Override
            public boolean visitConstructorInvocation(final ConstructorInvocation node) {
                JavaParser.ResolvedNode resolvedNode = context.resolve(node.astTypeReference());
//                System.out.println("closeStream.......");
                if (resolvedNode != null && resolvedNode instanceof JavaParser.ResolvedClass) {
                    JavaParser.ResolvedClass resolvedClass = (JavaParser.ResolvedClass) resolvedNode;
                    boolean isSupperClass = false;
                    for (String supperClass : sSupportSupperType) {
                        if (resolvedClass.isSubclassOf(supperClass, false)) {
                            isSupperClass = true;
                        }
                    }
                    if (!isSupperClass) {
//                        System.out.println("not supper class");
                        return super.visitConstructorInvocation(node);
                    }

                    //获取上一层的Try节点
                    Try tryBlock = JavaContext.getParentOfType(node, Try.class);
                    if (tryBlock == null) {
                        System.out.println("tryBlock null");
                        report(context, node);
                        return super.visitConstructorInvocation(node);
                    }
                    //寻找finally节点
                    Block finalBlock = tryBlock.astFinally();
                    if (finalBlock == null) {
                        System.out.println("finalBlock null");
                        report(context, node);
                        return super.visitConstructorInvocation(node);
                    }
                    //finally节点内容为空
                    if (finalBlock.astContents().size() == 0) {
                        report(context, node);
                        System.out.println("finalBlock empty");
                        return super.visitConstructorInvocation(node);
                    }
                    //检测finally块里面的内容是否包含close()方法
                    CloseFinder closeFinder = new CloseFinder();
                    finalBlock.accept(closeFinder);
                    if (!closeFinder.isCloseCalled()) {
                        System.out.println("not use close");
                        report(context, node);
                    }

                    //尝试使用遍历子节点的方式 寻找close，失败
                    //                    Iterable<JavaParser.ResolvedMethod> methodIt=  resolvedClass.getMethods("close",true);
                    //                    if(methodIt==null){
                    //                        System.out.println("dddddddddddd");
                    //                    }
                    //                   if(methodIt!=null&&methodIt.iterator().hasNext()){
                    //                       JavaParser.ResolvedMethod method = methodIt.iterator().next();
                    //                       System.out.println("method:"+method);
                    //                       MethodInvocation methodNode= (MethodInvocation) method.findAstNode();
                    //                       System.out.println("methodNode:"+methodNode);
                    ////                      int lineNum= context.getLocation(method.findAstNode()).getStart().getLine();
                    ////                       System.out.println("method.line:"+lineNum);
                    ////                       Try finalTryBlock=JavaContext.getParentOfType(method.findAstNode().,Try.class);
                    ////                       if(finalTryBlock!=null){
                    ////                           int lineNum=context.getLocation(finalTryBlock).getStart().getLine();
                    ////                           System.out.println("finalTryBlock.line:"+lineNum);
                    ////                       }
                    ////                     List<Node> finalChildNodeList=  finalBlock.getChildren();
                    ////                     if(finalChildNodeList==null||finalChildNodeList.size()==0){
                    ////                         System.out.println("finalNodelist null");
                    ////                         report(context,node);
                    ////                         return super.visitConstructorInvocation(node);
                    ////                     }
                    ////                     for(Node fianlChildNode:finalChildNodeList){
                    ////                         System.out.println("node:"+fianlChildNode);
                    ////                         if(fianlChildNode instanceof Try){
                    ////                             List<Node> tryChildNodes=fianlChildNode.getChildren();
                    ////                             for(Node tryChildNode:tryChildNodes){
                    ////                                 System.out.println("tryChilNode:"+tryChildNode);
                    ////                                 if(tryChildNode instanceof If){
                    ////                                     List<Node> ifChildNodes=tryChildNode.getChildren();
                    ////                                     for(Node ifChildNode:ifChildNodes){
                    ////                                         System.out.println("ifChildNode:"+ifChildNode);
                    ////                                     }
                    ////                                 }
                    ////                             }
                    ////                         }
                    ////
                    ////                     }
                    //                   }


                }
                return super.visitConstructorInvocation(node);
            }
        };
    }

    /**
     * 参考 @link http://grepcode.com/file/repo1.maven.org/maven2/com.android.tools.lint/lint-checks/24.3.0/com/android/tools/lint/checks/ToastDetector.java?av=f
     */
    private class CloseFinder extends ForwardingAstVisitor {
        private boolean mFound;

        @Override
        public boolean visitMethodInvocation(MethodInvocation node) {
            //            System.out.println("method:" + node.astName() + ",value:" + node.astName().astValue());
            if (CLOSE.equals(node.astName().astValue())) {
                mFound = true;
                System.out.println("has found close()");
            }
            return true;
        }

        boolean isCloseCalled() {
            return mFound;
        }
    }


    private void report(JavaContext context, ConstructorInvocation node) {
        context.report(ISSUE, context.getLocation(node.astTypeReference()), "please use close() method in finally");
    }
}
