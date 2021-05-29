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

        // 传给 handler2
        ctx.getMyChannel().doWrite("hello client");

        if ("flush".equals(string)) {
            /**
             * 这样调用，会跳过而不调用 handler2 的 flush 方法
             * 若还需要调用 handler2 的 flush 方法，应该通过 channel 调用:
             *          ctx.getMyChannel().flush();
             */
            ctx.flush();
        }
    }

    @Override
    public void write(HandlerContext ctx, Object msg) {
        log.debug("msg=" + msg);
        msg += "!!!";
        // 传递给 handler1
        ctx.write(msg);
    }

    @Override
    public void flush(HandlerContext ctx) {
        log.debug("flush");
        // 调用 handler1
        ctx.flush();
    }
}
