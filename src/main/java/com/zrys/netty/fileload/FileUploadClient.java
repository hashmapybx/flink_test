package com.zrys.netty.fileload;

import com.google.inject.internal.util.$Function;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;

/**
 * @ClassName FileUploadClient
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/16 22:51
 * @Version
 */
public class FileUploadClient {
    private static String file_name="D:\\日常工作文件\\能源云数据目录20211013-整合字段版.xlsx";
    public void connect(int port, String host, final FileUploadEntity fileUploadEntity) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                            ch.pipeline().addLast(new FileUploadClientHandler(fileUploadEntity));
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("客户端启动...");
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        try {
            FileUploadEntity entity = new FileUploadEntity();
            File file = new File(file_name);

            String name = file.getName();
            entity.setFileName(name);
            entity.setFile(file);

            //链接服务器 并且上传文件
            new FileUploadClient().connect(port, "127.0.0.1", entity);


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}