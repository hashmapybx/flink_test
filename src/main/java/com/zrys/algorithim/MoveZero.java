package com.zrys.algorithim;

import java.util.Arrays;

/**
 * @author rocky
 * @create 2021 - 09 - 13 21:55
 */
public class MoveZero {

    //测试算法
    public static void main(String[] args) {
        int[] nums = new int[]{1,0,2,4,0,7};
        moveZero2(nums);
    }

    public static void moveZero2(int[] nums) {
        if (nums == null) return;
        int n = nums.length;
        int j =0;
        //记录数组中非零元素的个数
        for (int i = 0; i < n; i++) {
            if (nums[i] != 0) {
                nums[j++] = nums[i];
            }
        }
        //从非零元素个数的索引位置开始 后面的全部置为0
        for (int i = j; i < n; i++) {
            nums[i] = 0;
        }
//        System.out.println(j);
        System.out.println(Arrays.toString(nums));
    }



    public static void moveZeroes(int[] nums) {
        //利用快速排序的思想
        if (nums == null) return;
        int n = nums.length;
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (nums[i] != 0) {
                int tmp = nums[i];
                nums[i] = nums[j];
                nums[j++] = tmp;
            }
        }
        System.out.println(Arrays.toString(nums));
    }




}