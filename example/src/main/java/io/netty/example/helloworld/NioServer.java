package io.netty.example.helloworld;

import io.netty.channel.EventLoopGroup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author JavaEdge
 * @date 2021/5/17
 */
public class NioServer {
    public static void main(String[] args) throws Exception {

        //创建一个ServerSocket
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8089));

        //设置为非阻塞模式
        serverChannel.configureBlocking(false);

        // 创建一个事件查询器
        Selector selector = SelectorProvider.provider().openSelector();

        // 把 ServerSocketChannel 注册到事件查询器上，并且感兴趣 OP_ACCEPT  事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
//
//        //创建一组事件查询器
//        EventLoopGroup eventLoopGroup = new EventLoopGroup();

        while (true) {
            // 阻塞方法，等待系统有I/O事件发生
            int eventNum = selector.select();
            System.out.println("系统发生IO事件 数量->" + eventNum);

            Set<SelectionKey> keySet = selector.selectedKeys();
            Iterator<SelectionKey> iterable = keySet.iterator();

            while (iterable.hasNext()) {
                // 拿到该 key
                SelectionKey key = iterable.next();
                // 拿到后就移除它，否则后面遍历还会重复拿到它
                iterable.remove();

                // 连接事件
                if (key.isAcceptable()) {
                    // 因为只有 ServerSocketChannel 有接收事件，所以可直接强转
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    // 接受客户端的连接,一个 SocketChannel 代表一个TCP连接
                    // 事件如果发生了，就肯定有新的连接
                    SocketChannel socketChannel = ssc.accept();
                    // 把SocketChannel设置为非阻塞模式
                    socketChannel.configureBlocking(false);
                    System.out.println("服务器接受了一个新的连接 " + socketChannel.getRemoteAddress());

                    // 把SocketChannel注册到Selector，并关注OP_READ事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
//                    eventLoopGroup.register(socketChannel, SelectionKey.OP_READ);
                }

                // 可读事件
                if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    try {
                        int readNum = socketChannel.read(buffer);
                        if (readNum == -1) {
                            System.out.println("读取结束,关闭 socket");
                            key.channel();
                            socketChannel.close();
                            break;
                        }
                        // 将Buffer从写模式切到读模式
                        buffer.flip();
                        byte[] bytes = new byte[readNum];
                        buffer.get(bytes, 0, readNum);
                        System.out.println(new String(bytes));

/*                        byte[] response = "client hello".getBytes();
                        // 清理了才可以重新使用
                        buffer.clear();
                        buffer.put(response);
                        buffer.flip();
                        // 该方法非阻塞的，如果此时无法写入也不会阻塞在此，而是直接返回 0 了
                        socketChannel.write(buffer);

                        */
                        // 在 key 上附加一个对象
                        key.attach("hello client".getBytes());
                        // 把 key 关注的事件切换为写
                        key.interestOps(SelectionKey.OP_WRITE);

                    } catch (IOException e) {
                        System.out.println("读取时发生异常,关闭 socket");
                        // 取消 key
                        key.channel();
                    }
                }

                if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    // 可写时再将那个对象拿出来
                    byte[] bytes = (byte[]) key.attachment();
                    key.attach(null);
                    System.out.println("可写事件发生 写入消息" + Arrays.toString(bytes));
                    if (bytes != null) {
                        socketChannel.write(ByteBuffer.wrap(bytes));
                    }

                    // 写完后，就不需要写了，就切换为读事件   如果不写该行代码就会死循环
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }
}
