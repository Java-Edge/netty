package io.netty.example.cp8;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

import java.net.InetSocketAddress;

/**
 * 代码清单 8-6 引导和使用 ChannelInitializer
 *
 * @author JavaEdge
 * @date 2023/5/20
 */
public class Demo86 {

    public static void main(String[] args) throws InterruptedException {
        // 创建 ServerBootstrap 以创建和绑定新的 Channel
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 设置 EventLoopGroup，其将提供用以处理 Channel 事件的 EventLoop
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                // 指定 Channel 的实现
                .channel(NioServerSocketChannel.class);
                // 注册一个 ChannelInitializerImpl 的实例来设置 ChannelPipeline
//                .childHandler(new ChannelInitializerImpl());
        // 绑定到地址
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.sync();
    }

    // 用以设置 ChannelPipeline 的自定义ChannelInitializerImpl 实现
    final class ChannelInitializerImpl extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            // 将所需的ChannelHandler添加到ChannelPipeline
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpClientCodec());
            pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        }
    }
}
