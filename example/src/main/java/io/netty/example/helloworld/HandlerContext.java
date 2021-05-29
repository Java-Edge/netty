package io.netty.example.helloworld;

import lombok.Getter;

/**
 * 由于 mychannel 的 read/write 方法里有太多业务代码，作为框架，肯定是要暴露接口给应用开发人员使用
 * 在接口里给一个事件回调给 业务开发们使用
 *
 * 处理事件
 *
 * @author JavaEdge
 * @date 2021/5/28
 */
public class HandlerContext {

    private Handler handler;

    @Getter
    private MyChannel myChannel;

    /**
     * 双向链表
     */
    HandlerContext prev;
    HandlerContext next;

    public HandlerContext(Handler handler, MyChannel myChannel) {
        this.handler = handler;
        this.myChannel = myChannel;
    }

    /**
     * 当有消息从 mychannel 中读出来时，传到链中处理
     */
    public void fireChannelRead(Object msg) {
        // 找到下一个处理器
        HandlerContext next = this.next;
        // 没到最后一个处理器
        if (next != null) {
            next.handler.channelRead(next, msg);
        }
    }

    /**
     * 写数据时，从后往前传递
     * 注意，tailCtx 自己不会使用自己的处理器，而是找到它前面的 ctx 的处理器
     */
    public void write(Object msg) {
        HandlerContext prev = this.prev;
        if (prev != null) {
            prev.handler.write(prev, msg);
        }
    }

    /**
     * flush数据时，从后往前传递
     */
    public void flush() {
        HandlerContext prev = this.prev;
        if (prev != null) {
            prev.handler.flush(prev);
        }
    }
}
