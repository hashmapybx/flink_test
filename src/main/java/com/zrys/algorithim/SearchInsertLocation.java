package com.zrys.algorithim;

/**
 * leetcode 35: 搜索插入数据的位置
 * @author rocky
 * @create 2021 - 09 - 09 17:06
 */
public class SearchInsertLocation {

    public int searchInsert(int[] nums, int target) {
//     给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。

        //todo 1 先判断target在不在数组里面
        int left = 0;
        int right = nums.length -1;

        while (left <= right) {
            int mid = left + (right - left)/2;
            if (nums[mid] < target) {
                left = mid +1;

            }else {
                right = mid -1;
            }
        }
        //todo 要是不在的话找到合适的插入位置
        return left;

    }

    //搜索最佳的数据插入空间

}