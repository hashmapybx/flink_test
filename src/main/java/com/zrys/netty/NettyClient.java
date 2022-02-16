package com.zrys.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.UUID;

/**
 * @ClassName NettyClient
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/15 14:39
 * @Version
 */
public class NettyClient {
    private final String host;
    private final int port;
    private Channel channel;
    //连接服务端的端口号地址和端口号

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class) //制定客户端链接服务端的channel类型
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        System.out.println("正在链接中。。。。");
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new RpcEncoder(RpcRequest.class)); //编码request
                        pipeline.addLast(new RpcDecoder(RpcResponse.class)); //解码response

                        //业务处理类
                        pipeline.addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
                                System.out.println("接受到server的响应数据是：" + rpcResponse);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                ctx.close();
                            }
                        });

                    }
                });
        //发起异步连接请求，绑定连接端口和host信息
        ChannelFuture future = bootstrap.connect(host, port).sync(); //future 是服务器端的返回结果
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("链接服务前成功");
                } else {
                    System.out.println("链接服务前失败");
                    future.cause().printStackTrace();
                    group.shutdownGracefully();
                }
            }
        });

        this.channel = future.channel();

    }

    public Channel getChannel() {
        return channel;
    }

    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient("127.0.0.1", 8080);
        client.start();
        Channel channel = client.getChannel();
        RpcRequest request = new RpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setData("client.message");
        //channel对象可保存在map中，供其它地方发送消息
        channel.writeAndFlush(request);




    }
}