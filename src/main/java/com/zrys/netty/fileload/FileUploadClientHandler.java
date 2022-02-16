package com.zrys.netty.fileload;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @ClassName FileUploadClientHandler
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/16 22:56
 * @Version
 */
public class FileUploadClientHandler extends SimpleChannelInboundHandler<FileUploadEntity> {
    private volatile int start = 0; //记录数据读取的起始位置
    private volatile int dataLength = 0; //需要读取的数据长度
    public RandomAccessFile randomAccessFile;
    private FileUploadEntity fileUploadEntity;
    private int byteRead; //上一次读取的数据长度
    private long startTime;//handler开始处理的起始时间
    private int ping_pong_times = 0;//客户端与服务端交互次数记录
    private final int dataGrameNum = 10;//数据段数
    private int dataGrameLength = 0;//数据段数长度


    public FileUploadClientHandler(FileUploadEntity ef) throws IOException {
        if (ef.getFile().exists()) {
            if (!ef.getFile().isFile()) {
                System.out.println("Not a file :" + ef.getFile());
                return;
            }
        }
        this.fileUploadEntity = ef;
        this.randomAccessFile = new RandomAccessFile(fileUploadEntity.getFile(), "r");
        dataGrameLength = (int) (randomAccessFile.length() / dataGrameNum);
    }

    /**
     * 向服务端发送数据
     * 实际上这个方法只执行一次，相当于抛砖引玉，客户端通过这个方法像服务端发送一次数据后
     * 客户端在channelRead方法受到服务端的响应 在这个方法里继续与服务端交互 接下来就向打乒乓球一样
     * 开始了ping-pong通信知道满足退出条件结束通信
     * 这里的结束条件就是文件上传完毕，程序读到文件末尾
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端发送消息=》channelActive");
        try {
            System.out.println("文件总大小=》" + randomAccessFile.length());
            randomAccessFile.seek(0);//设置读取的起始位置
            //lastLength = 11
            dataLength = dataGrameLength;//分段发送，分11段，115/10=11余数5 10段11 最后一段5
            startTime = System.currentTimeMillis();
            if (upload(ctx)) {
                System.out.println("channelActive=》第" + ping_pong_times + "次上传成功....");
                System.out.println("channelActive=》等待服务端返回响应....");
                System.out.println();
                System.out.println();
            } else {

                System.out.println("channelActive=》文件已经读完");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private boolean upload(ChannelHandlerContext ctx) throws IOException {
        //当最后一次发送完毕后 lastLength为0 bytes为空数组 这时继续给服务端传递
        // 服务端会判断拿到的数据长度为空时关闭连接和文件
        // 而客户端会在下面判断randomAccessFile.length() - start > 0不成立时返回false 从而关闭连接关闭文件
        byte[] bytes = new byte[dataLength];
        if ((byteRead = randomAccessFile.read(bytes)) != -1) {
            System.out.println();

            ping_pong_times++;
            fileUploadEntity.setDataLength(byteRead);
            fileUploadEntity.setBytes(bytes);
            ctx.writeAndFlush(fileUploadEntity);

            System.out.println("数据起始位置start=>" + start
                    + ",本次上传数据长度dataLength=>" + dataLength + ",本次读取长度=>" + byteRead);
            System.out.println(fileUploadEntity);

            if (randomAccessFile.length() - start > 0) {//判断是最后一次时返回false，以关闭客户端连接
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取服务端返回的数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Integer) {
            start = (Integer) msg;//读取到服务端修改后的初始值，第一次读取到start = 11
            System.out.println("服务端传回的指针=》" + start);
            if (start != -1) {
                randomAccessFile.seek(start);//设置文件指针
                int leaveDataLength = (int) (randomAccessFile.length() - start);//115 -11 =104
                //    a=33-0=33    b=33/10=3
                if (leaveDataLength < dataGrameLength) {//104<11 not
                    dataLength = leaveDataLength;
                    // dataLength = 11 第11次 dataLength =5，
                    // 当服务端拿到最后一次的数据后返回的start=115即文件的原始长度
                    // 此时leaveDataLength剩余长度为0 lastLength=0 下面又调upload方法 只不过传的data是空
                    // 这样就造成多发一次空数据
                }
                if (upload(ctx)) {
                    System.out.println("channelRead=》第" + ping_pong_times + "次上传成功....");
                } else {
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    System.out.println("channelRead=》上传文件耗时：" + (System.currentTimeMillis() - startTime));
                    randomAccessFile.close();
                    ctx.close();
                    System.out.println("channelRead=》文件已经读完--------" + byteRead);
                }
            }
        }
    }

    /**
     * 这个方法是 SimpleChannelInboundHandler 抽象类中的抽象方法
     * 它和上面的 channelRead 方法的区别是channelRead 是 ChannelInboundHandlerAdapter 的方法
     * ChannelInboundHandlerAdapter 是 SimpleChannelInboundHandler 的父类
     * 当我们的 handler 继承 SimpleChannelInboundHandler 时，如果同时重写了 channelRead 和 channelRead0 则默认执行前者
     * @param channelHandlerContext
     * @param fileUploadEntity
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileUploadEntity fileUploadEntity) throws Exception {
        System.out.println("channelRead0");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}