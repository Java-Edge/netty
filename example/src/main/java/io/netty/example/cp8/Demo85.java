package io.netty.example.cp8;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 代码清单 8-5 引导服务器
 *
 * @author JavaEdge
 * @date 2023/5/20
 */
public class Demo85 {

    public static void main(String[] args) {
        // 创建 ServerBootstrap 以创建ServerSocketChannel，并绑定它
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 设置 EventLoopGroup，其将提供用以处理 Channel 事件的 EventLoop
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                // 指定要使用的 Channel 实现
                .channel(NioServerSocketChannel.class)
                // 设置用于处理已被接受的 子 Channel 的 I/O 和数据的ChannelInboundHandler
                .childHandler(
                        new SimpleChannelInboundHandler<ByteBuf>() {
                            ChannelFuture connFuture;

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                // 创建一个 Bootstrap 类的实例以连接到远程主机
                                Bootstrap bootstrap = new Bootstrap();
                                // 指定 Channel 的实现
                                bootstrap.channel(NioSocketChannel.class)
                                        // 为入站 I/O 设置 ChannelInboundHandler
                                        .handler(
                                                new SimpleChannelInboundHandler<ByteBuf>() {
                                                    @Override
                                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
                                                        System.out.println("Received data");
                                                    }
                                                });
                                // 使用与分配给已被接受的子Channel相同的EventLoop
                                bootstrap.group(ctx.channel().eventLoop());
                                // 连接到远程节点
                                connFuture = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                if (connFuture.isDone()) {
                                    // 当连接完成时，执行一些数据操作（如代理）
                                    // do something with the data
                                    System.out.println();
                                }
                            }
                        });
        // 通过配置好的ServerBootstrap绑定该 ServerSocketChannel
        ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(8080));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Server bound");
                } else {
                    System.err.println("Bind attempt failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
