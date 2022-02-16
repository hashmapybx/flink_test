package com.zrys.netty;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RpcResponse
 * @Description TODO 作为server端返回给客户端的信息
 * @Author oyc
 * @Date 2022/2/15 11:58
 * @Version
 */
@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = -2577707401136472809L;

    private String id;
    private Object data;
    private int status;

    @Override
    public String toString() {
        return "RpcRequest{" + "id='" + id + '\'' + ", data=" + data + ", status= "+status+'}';
    }
}