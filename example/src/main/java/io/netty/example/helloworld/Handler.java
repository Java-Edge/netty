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
public interface Handler {

    void channelRead(HandlerContext ctx, Object msg);

    void write(HandlerContext ctx, Object msg);

    void flush(HandlerContext ctx);
}
