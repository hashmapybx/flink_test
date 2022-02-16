package com.zrys.netty;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RpcRequest
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/15 11:52
 * @Version
 */
@Data
public class RpcRequest  implements Serializable {
    private static final long serialVersionUID = -2577707401136472809L;

    private String id;
    private Object data;


    @Override
    public String toString() {
        return "RpcRequest{" + "id='" + id + '\'' + ", data=" + data + '}';
    }
}