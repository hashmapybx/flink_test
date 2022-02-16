package com.zrys.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName NIOServer
 * @Description TODO
 * @Author oyc
 * @Date 2022/2/14 22:05
 * @Version
 */
public class NIOServer {

    public static void startServer(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port)); //绑定本地的一个端口

        //打开selector
        Selector selector = Selector.open();
        //表示只是关系accept事件和接受客户端的链接
        //当一个client链接到服务端完成后则就是accept事件完成。
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //select阻塞直到某一个注册的事件就绪就会更新SelectionKey的状态
            int readChannels = selector.select();
            if (readChannels <= 0) {
                continue;
            }
//            得到就绪的key集合，key中保存有就绪的事件以及对应的Channel通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            //创建buffer 开始便利key
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                //判断事件的状态
                //分别处理read write connect accept
                if (next.isAcceptable()) {
                    //处理accpet事件
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);// 配置为非阻塞
                    //此处是EventLoop只关心read事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (next.isReadable()) {
                    //处理read事件
                    SocketChannel channel = (SocketChannel) next.channel();
                    buffer.clear();
                    channel.read(buffer);
                    buffer.flip(); //用于读写翻转
                    channel.write(buffer);
                }
                //移除当前事件
                iterator.remove();
            }
        }


    }
}