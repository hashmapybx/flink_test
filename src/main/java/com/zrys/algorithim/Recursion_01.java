package com.zrys.algorithim;

import java.util.HashMap;

/**
 * 递归操作
 *
 * @author rocky
 * @create 2021 - 09 - 04 18:14
 *
 * 1.关于递归的操作流程 可以是分为递 和 归两个过程
 * 2.需要注意的点是边界条件的确定。
 * 3.同时递归的堆栈溢出和重复计算的问题是很重要的，这个对于系统的稳定性影响很重要的
 * 4.避免递归过程中环的存在。
 *
 */
public class Recursion_01 {

    int depth = 0;

    // 需求是解决用户训话调用的场景
    public int recursion(int num) {
        ++depth;
        //防治递归的深度 我们加入了异常判断，当超过这个深度的话 我们就是要退出的
        if (num == 1) return num;
        if (num == 2) return num;
        HashMap<Integer, Integer> resultState = new HashMap<>();

        if (resultState.containsKey(num)) {
            return resultState.get(num);
        }

        int res = recursion(num - 1) + recursion(num - 2);
        resultState.put(num, res);

        return res;

    }

    public static void main(String[] args) {
        Recursion_01 res = new Recursion_01();
        int result = res.recursion(12);
        System.out.println(result);
    }



}