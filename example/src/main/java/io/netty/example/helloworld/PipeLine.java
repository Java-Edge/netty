package io.netty.example.helloworld;

import lombok.extern.slf4j.Slf4j;

/**
 * @author JavaEdge
 * @date 2021/5/28
 */
@Slf4j
public class PipeLine {

    private MyChannel myChannel;

    private EventLoop eventLoop;

    HandlerContext headCtx;
    HandlerContext tailCtx;

    public PipeLine(MyChannel myChannel, EventLoop eventLoop) {
        this.myChannel = myChannel;
        this.eventLoop = eventLoop;
        PileLineHandler pileLineHandler = new PileLineHandler();
        this.headCtx = new HandlerContext(pileLineHandler, myChannel);
        this.tailCtx = new HandlerContext(pileLineHandler, myChannel);

        // 构建初始化的链表
        this.headCtx.next = this.tailCtx;
        this.tailCtx.prev = this.headCtx;
    }

    class PileLineHandler implements Handler {

        @Override
        public void channelRead(HandlerContext ctx, Object msg) {
            log.debug(msg.toString());
            log.info("tail handler" + msg);
        }

        @Override
        public void write(HandlerContext ctx, Object msg) {
            log.debug(msg.toString());
        }

        @Override
        public void flush(HandlerContext ctx) {
            log.debug("flush");
        }
    }

    /**
     * 仅演示添加到链尾
     *
     * @param handler
     */
    public void addLast(Handler handler) {
        HandlerContext ctx = new HandlerContext(handler, myChannel);

        HandlerContext prev = this.tailCtx.prev;
        prev.next = ctx;
        ctx.prev = prev;
        ctx.next = this.tailCtx;
        tailCtx.prev = ctx;
    }
}
