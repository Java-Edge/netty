package io.netty.example.helloworld;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * @author JavaEdge
 * @date 2021/5/17
 */
public class NioServer {
    public static void main(String[] args) throws Exception {

        // 创建一个ServerSocket
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8089));

        // 设置为非阻塞模式
        serverChannel.configureBlocking(false);

        // 创建一个事件查询器
        Selector selector = SelectorProvider.provider().openSelector();

        // 把 ServerSocketChannel 注册到事件查询器上，并且感兴趣 OP_ACCEPT  事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 创建【事件查询器】组
        EventLoopGroup eventLoopGroup = new EventLoopGroup();

        while (true) {
            // 阻塞方法，等待系统有I/O事件发生
            int eventNum = selector.select();
            System.out.println("系统发生IO事件 数量->" + eventNum);

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                // 拿到该 key
                SelectionKey key = keyIterator.next();
                // 拿到后就移除它，否则后面遍历还会重复拿到它
                keyIterator.remove();

                // 只需处理【连接事件】 a connection was accepted by a ServerSocketChannel.
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
                    // socketChannel.register(selector, SelectionKey.OP_READ);
                    eventLoopGroup.register(socketChannel, SelectionKey.OP_READ);
                }
            }
        }
    }
}
