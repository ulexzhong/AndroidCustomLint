package com.ulexzhong.lintrules;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;
import com.ulexzhong.lintrules.detector.performance.CloseStreamDetector;
import com.ulexzhong.lintrules.detector.performance.EnumDetector;
import com.ulexzhong.lintrules.detector.performance.HashMapPerformanceDetector;
import com.ulexzhong.lintrules.detector.performance.InitialFieldDetector;
import com.ulexzhong.lintrules.detector.performance.MessageObtainDetector;
import com.ulexzhong.lintrules.detector.performance.ThreadCreateDetector;
import com.ulexzhong.lintrules.detector.standard.ActivityFragmentLayoutNameDetector;
import com.ulexzhong.lintrules.detector.standard.BaseActivityDetector;
import com.ulexzhong.lintrules.detector.standard.FieldNameDetector;
import com.ulexzhong.lintrules.detector.standard.IntentExtraKayDetector;
import com.ulexzhong.lintrules.detector.standard.LoggerDetector;
import com.ulexzhong.lintrules.detector.standard.ViewIdNameDetector;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ulexzhong on 2018/3/5.
 */

public class LintIssueRegistry extends IssueRegistry {

    @Override
    public List<Issue> getIssues() {
        System.out.println("=== lint issue start ===");
        return Arrays.asList(
                LoggerDetector.ISSUE
                , MessageObtainDetector.ISSUE
                , IntentExtraKayDetector.ISSUE
                , EnumDetector.ISSUE
                , BaseActivityDetector.ISSUE
                , ViewIdNameDetector.ISSUE
                , ActivityFragmentLayoutNameDetector.ISSUE
                , CloseStreamDetector.ISSUE
                , FieldNameDetector.ISSUE
                , HashMapPerformanceDetector.ISSUE
                , InitialFieldDetector.ISSUE
                , ThreadCreateDetector.ISSUE
        );
    }
}
