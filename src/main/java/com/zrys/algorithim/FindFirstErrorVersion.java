package com.zrys.algorithim;

/**
 * 找到第一个错误版本信息
 *
 * @author rocky
 * @create 2021 - 09 - 09 16:48
 */
//
class VersionControl {
    boolean isBadVersion(int version) {
        return true;
    };
}


public class FindFirstErrorVersion extends VersionControl{
    //在系统开发的时候我们需要找到第一个错误的版本信息 其中需要尽量减少对检测api的调用
    // 其中当一个版本出现错误，那之后的版本都是错误的
    //如果当前版本是正确的 之前的版本也将是争取
    public int firstBadVersion(int n) {
        int left =1;
        int right =n;
        while (left < right) {
            int mid = left + (right - left)/2;
            if (isBadVersion(mid)) {
                right = mid; // 答案在区间 [left, mid] 中
            } else {
                left = mid + 1; // 答案在区间 [mid+1, right] 中
            }
        }
        return left;
    }
}
