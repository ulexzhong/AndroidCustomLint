package com.ulexzhong.lintrules.detector.standard;

import com.android.SdkConstants;
import com.android.resources.ResourceFolderType;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.ResourceXmlDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.Speed;
import com.android.tools.lint.detector.api.XmlContext;

import org.w3c.dom.Attr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by ulexzhong on 2018/3/14.
 * layout中id命名规范
 * RelativeLayout/LinearLayout/FrameLayout->layout_
 * TextView->txt
 * EditView->edt
 */
public class ViewIdNameDetector extends ResourceXmlDetector {

    public static final Issue ISSUE = Issue.create("ViewIdNameUse",
            "Check whether the name is conform to the specifications",
            "Check whether the name is conform to the specifications",
            Category.LINT, 5, Severity.WARNING,
            new Implementation(ViewIdNameDetector.class, Scope.RESOURCE_FILE_SCOPE));

    private static final String REPORT_STR_FORMAT = "Layout文件中 \"%s\" 不符合 %s 的id命名规范,前缀必须是:%s";

    private static final String ANDROID_ID = "android:id";

    /**
     * 组件对应前缀
     * key:Android组件名，value：期望前缀
     */
    private static final HashMap<String, String> prefixMap = new HashMap<String, String>() {{
        put("RelativeLayout", "layout_");
        put("LinearLayout", "layout_");
        put("FrameLayout", "layout_");
        put("TextView", "txt_");
        put("EditText", "edt_");
        put("Button", "btn_");
        put("RadioButton", "rbtn_");
        put("ImageButton", "ibtn_");
        put("ScrollView","sc_");
        put("ListView", "lv_");
        put("CheckBox", "cb_");
        put("ImageView", "imgv_");
        put("WebView", "webview_");
        put("ProgressBar", "progressbar_");
        put("SeekBar", "seekbar_");
    }};

    @Override
    public boolean appliesTo(ResourceFolderType folderType) {
        return ResourceFolderType.LAYOUT == folderType;
    }

    @Override
    public Speed getSpeed() {
        return Speed.FAST;
    }

    @Override
    public Collection<String> getApplicableAttributes() {
        return Collections.singletonList(SdkConstants.VALUE_ID);
    }

    @Override
    public void visitAttribute(XmlContext context, Attr attribute) {
        super.visitAttribute(context, attribute);

        String prnMain = context.getMainProject().getDir().getPath();
        String prnCur = context.getProject().getDir().getPath();

        //1.只关心id节点
        //2.只关心工程中的xml文件,build等等目录下的不关心
        if (attribute.getName().startsWith(ANDROID_ID) && prnMain.equals(prnCur)) {
            checkNameRule(context, attribute);
        }

    }

    private void checkNameRule(XmlContext context, Attr attribute) {
        String tagName = attribute.getOwnerElement().getTagName();
        int startIndex = 0;
        String idName = attribute.getValue().substring(5);//"@+id/value"
        String idPrefix = prefixMap.get(tagName);
        if (idPrefix != null) {
            startIndex = idName.indexOf(idPrefix);
        }
        if (startIndex != 0) {
            String reportStr = String.format(REPORT_STR_FORMAT, idName, tagName, idPrefix);
            context.report(ISSUE, attribute, context.getLocation(attribute), reportStr);
        }
    }
}
