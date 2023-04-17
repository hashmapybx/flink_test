package com.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Person
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/7 16:32
 * @Version
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Comparable<Person> {
    private String name;
    private String create_time;

    @Override
    public int compareTo(Person o) {
        return
                (create_time.compareTo(o.getCreate_time()));
    }
}