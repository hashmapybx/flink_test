package com.zrys.netty.fileload;

import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @ClassName FileUploadEntity 文件上传实体类
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/16 22:36
 * @Version
 */
@Data
public class FileUploadEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private File file; //文件
    private String fileName; //文件名
    private byte[] bytes; //文件字节数组
    private int dataLength; //文件数据长度

    @Override
    public String toString() {
        return "FileUploadEntity{" +
                "file=" + file +
                ", fileName='" + fileName + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                ", dataLength=" + dataLength +
                '}';
    }
}