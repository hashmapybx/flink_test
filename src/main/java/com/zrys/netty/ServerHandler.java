package com.zrys.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @ClassName ServerHandler
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/15 11:48
 * @Version
 */
// 节约内存 表示共享
@ChannelHandler.Sharable
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 接受客户端发送过来的数据进行处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        System.out.println("接受到的客户端的数据是：" + request.toString());
        //定义需要返回的数据类
        RpcResponse response = new RpcResponse();
        response.setId(UUID.randomUUID().toString());
        response.setData("server响应的数据");
        response.setStatus(1); //1表示响应成功 -1表示失败
        ctx.writeAndFlush(response);
    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端接受数据完毕");
        ctx.flush();
        ctx.close();
    }


    /**
     * todo 客户端去和服务端连接成功时触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}