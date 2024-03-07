package io.netty.example.cp8;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

/**
 * 代码清单 8-9 优雅关闭
 *
 * @author JavaEdge
 * @date 2023/5/20
 */
public class Demo89 {

    public static void main(String[] args) throws InterruptedException {
        // 创建处理 I/O 的EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        // 创建一个 Bootstrap类的实例并配置它
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
        // ...
        // shutdownGracefully()方法将释放所有的资源，并且关闭所有的当前正在使用中的 Channel
        Future<?> future = group.shutdownGracefully();
        // block until the group has shutdown
        future.syncUninterruptibly();
    }
}
