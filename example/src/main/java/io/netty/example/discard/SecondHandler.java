package io.netty.example.discard;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecondHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当有事件发生时，会调用
     * 这里重写channelRead事件处理器方法。每当从客户端接收到新数据时，就使用接收到的消息来调用此方法
     * 此示例中，接收到的消息的类型为ByteBuf
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info(msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 引发异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
