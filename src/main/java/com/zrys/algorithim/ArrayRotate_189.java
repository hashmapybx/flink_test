package com.zrys.algorithim;

import java.util.Arrays;

/**
 * 旋转数组
 * 需求： 给定一个数组，将数组中的元素向右移动 k 个位置，其中 k 是非负数。
 * @author rocky
 * @create 2021 - 09 - 10 11:34
 */
public class ArrayRotate_189 {
    public static void main(String[] args) {
        int[] nums = new int[]{1,2,3,4,5};
//        rotate(nums, 2);
        startEndRotate(nums, 0, nums.length-1);
    }

    /**
     * 旋转数组 把数组中的元素向右移动k个位置
     * @param nums
     * @param k
     */
    public static void rotate(int[] nums, int k) {
        //先把k到num.length位置的元素放到一个新的数组里面 在把k之前的 0 ~ n-k位置的元素拼接
        int n = nums.length;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[(i+ k) % n] = nums[i];
        }

        System.arraycopy(arr,0, nums, 0, n);
    }

    /**
     * 数组调换位置
     * @param arr
     * @param start
     * @param end
     */
    public static void startEndRotate(int[] arr, int start, int end) {
        while (start < end) {
            int tmp = arr[start];
            arr[start] = arr[end];
            arr[end] = tmp;
            ++start;
            --end;
        }
        System.out.println(Arrays.toString(arr));
    }




}