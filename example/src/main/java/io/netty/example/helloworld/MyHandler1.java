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
//        byte[] bytes = new byte[readNum];
//        // 客户端发来的数据
//        buffer.get(bytes, 0, readNum);
//        String clientData = new String(bytes);
//        System.out.println(clientData);
//
//        // 加入写缓冲区
//        writeQueue.add(ByteBuffer.wrap("hello JavaEdge".getBytes()));
//
//        if ("flush".equals(clientData)) {
//            // 把 key 关注的事件切换为写
//            key.interestOps(SelectionKey.OP_WRITE);
//        }
        log.debug("MyHandler2:" + msg);
        // 第一个处理器接收到的一定是ByteBuffer类型消息

        // 解码
        ByteBuffer buffer = (ByteBuffer) msg;
        int limit = buffer.limit();
        byte[] content = new byte[limit];
        buffer.get(content);
        // 解码后的数据
        String string = new String(content);
        // 向后传递
        ctx.fireChannelRead(string);

        // 如果是实例化的对象，就需要清理资源
        buffer.clear();
    }

    @Override
    public void write(HandlerContext ctx, Object msg) {

    }

    @Override
    public void flush(HandlerContext ctx) {

    }
}
