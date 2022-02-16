package com.zrys.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName RpcDecoder 解码器
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/15 14:26
 * @Version
 */

public class RpcDecoder extends ByteToMessageDecoder {
    //目标对象进行解码
    private Class<?> target;

    public RpcDecoder(Class target) {
        this.target = target;
    }



    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() <4) {
            return; //长度小于4 丢弃
        }
        in.markReaderIndex(); //标记一下当前readIndex的位置
        int dataLength = in.readInt(); //读取传送过来 数据长度
        if (in.readableBytes() < dataLength) { //读到的消息体长度如果小于我们传送过来的消息长度，
            // 则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = JSON.parseObject(data, target); //将byte数据转化为我们需要的对象
        out.add(obj);
    }
}