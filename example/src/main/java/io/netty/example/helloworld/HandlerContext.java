package io.netty.example.helloworld;

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
     * @param msg
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
     *
     * @param msg
     */
    public void write(Object msg) {
        HandlerContext prev = this.prev;
        if (prev != null) {
            prev.handler.write(prev, msg);
        }
    }

    /**
     * flush数据时，从后往前传递
     *
     * @param msg
     */
    public void flush(Object msg) {
        HandlerContext prev = this.prev;
        if (prev != null) {
            prev.handler.write(prev, msg);
        }
    }
}
