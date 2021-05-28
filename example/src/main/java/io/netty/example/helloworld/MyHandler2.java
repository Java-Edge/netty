package io.netty.example.helloworld;

import lombok.extern.slf4j.Slf4j;

/**
 * @author JavaEdge
 * @date 2021/5/28
 */
@Slf4j
public class MyHandler2 implements Handler {
    @Override
    public void channelRead(HandlerContext ctx, Object msg) {
        // 上一个处理器解码成 String 了，所以这里直接转型处理 String
        String string = (String) msg;
        // 处理业务
        log.debug(string);
        // 向后传递
        ctx.fireChannelRead(string);
    }

    @Override
    public void write(HandlerContext ctx, Object msg) {

    }

    @Override
    public void flush(HandlerContext ctx) {

    }
}
