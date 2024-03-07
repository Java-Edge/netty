package io.netty.example.cp8;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * 代码清单 8-7 使用属性值
 *
 * @author JavaEdge
 * @date 2023/5/20
 */
public class Demo87 {

    public static void main(String[] args) throws InterruptedException {
        // 创建一个 AttributeKey 以标识该属性
        final AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        // 创建一个 Bootstrap 类的实例以创建客户端 Channel 并连接它们
        Bootstrap bootstrap = new Bootstrap();
        // 设置 EventLoopGroup，其提供了用以处理 Channel 事件的 EventLoop
        bootstrap.group(new NioEventLoopGroup())
                // 指定Channel的实现
                .channel(NioSocketChannel.class)
                // 设置用以处理 Channel 的I/O 以及数据的 ChannelInboundHandler
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                             @Override
                             public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                 // 使用 AttributeKey 检索属性以及它的值
                                 Integer idValue = ctx.channel().attr(id).get();
                                 // do something with the idValue
                             }

                             @Override
                             protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf)
                                     throws Exception {
                                 System.out.println("Received data");
                             }
                         }
                );
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                // 设置 ChannelOption，其将在 connect()或者bind()方法被调用时被设置到已经创建的Channel 上
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        // 存储该 id 属性
        bootstrap.attr(id, 123456);
        // 使用配置好的 Bootstrap实例连接到远程主机
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
        future.syncUninterruptibly();
    }
}
