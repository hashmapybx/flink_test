package com.zrys.algorithim;

/**
 * 二分查找
 ** leetcode 704: public int search(int[] nums, int target) {
 *
 *     }
 * Description:
 * Date: 2021/9/9/0009
 * Author: xxxName
 * No such property: code for class: Script1
 *
  * @return null
 */

public class TwoSearch {

    public static void main(String[] args) {
        TwoSearch twoSearch = new TwoSearch();
        int[] nums = new int[]{1,2,3,4};
        System.out.println(twoSearch.search(nums, 3));
    }


    /**
     * 二分查找数据
     * @param nums
     * @param target
     * @return
     */
    public int search(int[] nums, int target) {
        //给定一个 n 个元素有序的（升序）整型数组 nums 和一个目标值 target  ，
        // 写一个函数搜索 nums 中的 target，如果目标值存在返回下标，否则返回 -1。
        int left = 0;
        int right = nums.length-1;
        while (left <= right) {
            int mid = left + (right - left) /2;
            int tmp = nums[mid];
            if (target == tmp) {
                return mid;
            }else if(target > tmp) {
                left = mid +1;
            }else {
                right = mid -1;
            }
        }
        return -1;
    }

}
