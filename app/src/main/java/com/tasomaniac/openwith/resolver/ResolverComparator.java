package com.tasomaniac.openwith.resolver;

import android.app.usage.UsageStats;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.tasomaniac.openwith.extension.IntentExtensionKt;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import fe.linksheet.data.AppSelectionHistory;

class ResolverComparator implements Comparator<ResolveInfo> {

    private final PackageManager packageManager;
    private final Map<String, UsageStats> usageStatsMap;
    private final Map<String, AppSelectionHistory> historyMap;
    private final Collator collator;
    private final boolean isHttp;

    ResolverComparator(PackageManager packageManager,
                       Map<String, UsageStats> usageStatsMap,
                       Map<String, AppSelectionHistory> historyMap,
                       Intent sourceIntent) {
        this.packageManager = packageManager;
        this.usageStatsMap = usageStatsMap;
        this.historyMap = historyMap;
        this.collator = Collator.getInstance(Locale.getDefault());
        this.isHttp = IntentExtensionKt.isHttp(sourceIntent);
    }

    @Override
    public int compare(ResolveInfo lhs, ResolveInfo rhs) {

        if (isHttp) {
            // Special case: we want filters that match URI paths/schemes to be
            // ordered before others.  This is for the case when opening URIs,
            // to make native apps go above browsers.
            final boolean lhsSpecific = isSpecificUriMatch(lhs.match);
            final boolean rhsSpecific = isSpecificUriMatch(rhs.match);
            if (lhsSpecific != rhsSpecific) {
                return lhsSpecific ? -1 : 1;
            }
        }

        if(historyMap != null){
            AppSelectionHistory left = historyMap.get(lhs.activityInfo.packageName);
            AppSelectionHistory right = historyMap.get(rhs.activityInfo.packageName);

            long leftCount = 0;
            if(left != null){
                leftCount = left.getLastUsed();
            }

            long rightCount = 0;
            if(right != null){
                rightCount = right.getLastUsed();
            }

            if (leftCount != rightCount) {
                return Long.compare(rightCount, leftCount);
            }
        }

//        if (history != null) {
//            int leftCount = history.get(lhs.activityInfo.packageName);
//            int rightCount = history.get(rhs.activityInfo.packageName);
//            if (leftCount != rightCount) {
//                return Integer.compare(rightCount, leftCount);
//            }
//        }

//        if (priorityPackages != null) {
//            boolean leftPriority = isPriority(lhs);
//            boolean rightPriority = isPriority(rhs);
//            if (leftPriority != rightPriority) {
//                return Boolean.compare(rightPriority, leftPriority);
//            }
//        }

        if (usageStatsMap != null) {
            final long timeDiff =
                    getPackageTimeSpent(rhs.activityInfo.packageName) -
                            getPackageTimeSpent(lhs.activityInfo.packageName);

            if (timeDiff != 0) {
                return timeDiff > 0 ? 1 : -1;
            }
        }

        CharSequence sa = lhs.loadLabel(packageManager);
        if (sa == null) {
            sa = lhs.activityInfo.name;
        }
        CharSequence sb = rhs.loadLabel(packageManager);
        if (sb == null) {
            sb = rhs.activityInfo.name;
        }

        return collator.compare(sa.toString(), sb.toString());
    }

    private long getPackageTimeSpent(String packageName) {
        if (usageStatsMap != null) {
            final UsageStats stats = usageStatsMap.get(packageName);
            if (stats != null) {
                return stats.getTotalTimeInForeground();
            }

        }
        return 0;
    }

//    private boolean isPriority(ResolveInfo lhs) {
//        return priorityPackages.contains(lhs.activityInfo.packageName);
//    }

    private static boolean isSpecificUriMatch(int match) {
        match = match & IntentFilter.MATCH_CATEGORY_MASK;
        return match >= IntentFilter.MATCH_CATEGORY_HOST
                && match <= IntentFilter.MATCH_CATEGORY_PATH;
    }
}
