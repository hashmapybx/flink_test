package com.zrys.algorithim;

import java.util.Arrays;

/**
 * 有序数组的平方操作
 *
 * @author rocky
 * @create 2021 - 09 - 10 8:46
 *
 *
 * 给你一个按 非递减顺序 排序的整数数组 nums，返回 每个数字的平方 组成的新数组，要求也按 非递减顺序 排序。
 * 例如 这个题目的精华就是在数组中平方的最大值一定是出现在排序数组的最右边的
 * 利用左右指针来移动操作
 * 例外创建一个新的数组来保存结果
 * 定义一个新的指针来移动新数组所指向的位置
 */
public class ArraysortedSquares {
    public static void main(String[] args) {
        int[] nums = new int[]{-4,-1,0,3,10};
        System.out.println(Arrays.toString(sortedSquares(nums)));
    }



    /**
     * 常规的双指针操作
     * @param nums
     * @return
     */
    public static int[] sortedSquares(int[] nums) {
        int left = 0;
        int right = nums.length -1;
        int[] sorted = new int[nums.length];
        int write = nums.length -1;
        //边界条件
        while (left <= right) {
            //左右指针开始移动操作
            if (nums[left] * nums[left] >= nums[right] * nums[right]) {
                sorted[write] = nums[left] * nums[left];
                ++left;
                --write;
            }else {
                sorted[write] = nums[right] * nums[right];
                --right;
                --write;
            }
        }
        return sorted;
    }

}

