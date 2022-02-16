package com.zrys.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName RpcEncoder 编码器
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/15 14:34
 * @Version
 */
public class RpcEncoder extends MessageToByteEncoder {
    private  Class<?> target;
    public RpcEncoder(Class target) {
        this.target = target;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (target.isInstance(msg)) {
            byte[] bytes = JSON.toJSONBytes(msg);
            out.writeInt(bytes.length); //消息头
            out.writeBytes(bytes); //消息中包含数据

        }
    }
}