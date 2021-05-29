package io.netty.example.helloworld;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @author JavaEdge
 * @date 2021/5/28
 */
@Slf4j
public class MyHandler1 implements Handler {

    @Override
    public void channelRead(HandlerContext ctx, Object msg) {
        log.debug(msg.toString());
        // 第一个处理器接收到的一定是ByteBuffer类型消息

        // 解码
        ByteBuffer buffer = (ByteBuffer) msg;
        int limit = buffer.limit();
        byte[] content = new byte[limit];
        buffer.get(content);
        // 解码:将底层 socket 的 byte[]转换为对象的过程
        String string = new String(content);
        // 向后传递
        ctx.fireChannelRead(string);

        // 如果是实例化的对象，就需要清理资源
        buffer.clear();
    }

    /**
     * @param msg 客户端传过来的对象，可以是 Bean类，可以是字符串。但这些东西底层socket可不认识
     *            所以要经历编码,比如转换成socket认识的字节流
     */
    @Override
    public void write(HandlerContext ctx, Object msg) {
        log.debug("msg=" + msg);
        // 因为最终要写给 socket，所以要把对象转换成二进制的字节，即【编码】
        ByteBuffer buffer = ByteBuffer.wrap(msg.toString().getBytes());
        // 传递给 pipeline
        ctx.write(buffer);
    }

    @Override
    public void flush(HandlerContext ctx) {
        // 向后传递
        log.debug("flush");
        ctx.flush();
    }
}
