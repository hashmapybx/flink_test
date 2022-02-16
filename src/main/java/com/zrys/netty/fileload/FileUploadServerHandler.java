package com.zrys.netty.fileload;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @ClassName FileUploadServerHandler 服务端数据处理类
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/16 22:29
 * @Version
 */
public class FileUploadServerHandler extends SimpleChannelInboundHandler<FileUploadEntity> {
    private int byteRead;//读取到的数据的长度
    private volatile int start = 0;//读取的起始位置
    private String file_dir = "D:";//服务器保存文件的路径
    private long startTime;//开始处理的时间
    private RandomAccessFile randomAccessFile;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        startTime = System.currentTimeMillis();
        System.out.println("channelActive....");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    /**
     * 开始处理client发送过来的数据
     * @param ctx
     * @param fileUploadEntity
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileUploadEntity fileUploadEntity) throws Exception {
        FileUploadEntity ef = fileUploadEntity;
        byte[] bytes = ef.getBytes();
        byteRead = ef.getDataLength(); //每一次接到数据长度
        System.out.println("byteRead=>" + byteRead);
        String md5 = ef.getFileName();//文件名
        String path = file_dir + File.separator + md5;//文件路径
        randomAccessFile = new RandomAccessFile(path, "rw");
        randomAccessFile.seek(start);
        randomAccessFile.write(bytes); //在对应的位置写入数据
        randomAccessFile.close();

        start = start + byteRead; //修改初始值，记录下来下次要从那个位置开始读取数据，并返回客户端，告诉客户端下次要从哪个位置开始读取数据。
        if (byteRead > 0) {
            System.out.println("返回给客户端的数据：" + start);
            ctx.writeAndFlush(start); //写会客户端
        } else {
            System.out.println("接收文件耗时：" + (System.currentTimeMillis() - startTime));
            ctx.close();
        }


    }
}