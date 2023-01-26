package io.netty.example.discard;

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 处理服务端的channel.
 *
 * DiscardServerHandler继承ChannelInboundHandlerAdapter(ChannelInboundHandler的实现)
 * ChannelInboundHandler提供了可重写的各种事件处理器方法
 * 现在，仅扩展ChannelInboundHandlerAdapter即可，而不是自己实现处理器接口
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当有事件发生时，会调用
     * 这里重写channelRead事件处理器方法。每当从客户端接收到新数据时，就使用接收到的消息来调用此方法
     * 此示例中，接收到的消息的类型为ByteBuf
     * 该处理器相当于解码器
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        /**
         * ByteBuf是个引用计数对象，必须通过release显式释放。
         * 释放传递给处理程序的引用计数对象都是处理程序的责任！！！
         *
         * 一般channelRead处理器方法的实现如下：
         * @Override
         * public void channelRead(ChannelHandlerContext ctx, Object msg) {
         *     try {
         *         // 处理 msg
         *     } finally {
         *         ReferenceCountUtil.release(msg);
         *     }
         * }
         */
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            int count = byteBuf.readableBytes();
            byte[] content = new byte[count];
            byteBuf.readBytes(content);
            // 转成字符串
            String string = new String(content);
            // 向后传递
            ctx.fireChannelRead(string);
        } finally {
            byteBuf.release();
        }
    }

    /**
     * 当Netty由于I/O错误、处理器的实现类处理事件时抛异常
     * 将使用Throwable调用exceptionCaught事件处理器方法
     * 在大多数情况下，应该记录捕获的异常并在此处关闭其关联的通道，尽管此方法的实现可能会有所不同，具体取决于您要处理特殊情况时要采取的措施
     * 例如，您可能想在关闭连接之前发送带有错误代码的响应消息。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 引发异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
