package io.netty.example.helloworld;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

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

        /**
         * 因为写数据是从后往前处理，所以最终到该处理器，必须要调用 channel 执行底层的写数据到 socket
         */
        @Override
        public void write(HandlerContext ctx, Object msg) {
            log.debug(msg.toString());
            // 既然是写底层，那就必须是 ByteBuffer 类型
            if (!(msg instanceof ByteBuffer)) {
                throw new RuntimeException("error class type" + msg.getClass());
            }
            // 类型符合，则加入到 channel 的缓冲区队列
            PipeLine.this.myChannel.addWriteQueue((ByteBuffer) msg);
        }

        /**
         * 上边的 write 方法也只是将数据写到 channel 的临时缓冲区队列，并没有真正写进socket 输出
         * 当客户端调用了 flush 才真正的写数据出去。
         */
        @Override
        public void flush(HandlerContext ctx) {
            log.debug("flush");
            // 最后是由 pipeline 和 channel 交互写的数据
            PipeLine.this.myChannel.doFlush();
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
