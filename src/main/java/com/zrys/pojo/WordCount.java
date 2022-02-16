package com.zrys.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rocky
 * @create 2021 - 09 - 02 8:52
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordCount {
    public String word;
    public long frequency;
}