package com.tool;

import lombok.Data;

import java.util.*;

/**
 * @ClassName FixSizedPriorityQueue
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/7 16:07
 * @Version
 */
@Data
public class FixSizedPriorityQueue<E extends Comparable>  {
    private PriorityQueue<E> queue;
    private int maxSize; //最大容量

    public FixSizedPriorityQueue(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.maxSize = maxSize;
        this.queue = new PriorityQueue<>(maxSize, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                // 生成最大堆使用o2-o1,生成最小堆使用o1-o2, 并修改 e.compareTo(peek) 比较规则
                return (o1.compareTo(o2));
            }
        });
    }

    public void add(E e) {
        if (queue.size() < maxSize) {
            queue.add(e);
        } else {
            //表示队列已满
            E peek =  queue.peek();
            if (e.compareTo(peek) > 0) {
                queue.poll();
                queue.add(e);
            }
        }
    }

    public List<E> sortedList() {
        List<E> list = new ArrayList<E>(queue);
        Collections.sort(list); // PriorityQueue本身的遍历是无序的，最终需要对队列中的元素进行排序
        return list;
    }




}