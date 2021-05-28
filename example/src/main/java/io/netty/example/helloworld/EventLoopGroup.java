package io.netty.example.helloworld;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author JavaEdge
 * @date 2021/5/25
 */
public class EventLoopGroup {

    private EventLoop[] eventLoops = new EventLoop[2];

    private final AtomicInteger idx = new AtomicInteger(0);

    public EventLoop next() {
        // 轮询算法
        return eventLoops[idx.getAndIncrement() & eventLoops.length - 1];
    }

    public EventLoopGroup() throws IOException {
        for (int i = 0; i < eventLoops.length; i++) {
            eventLoops[i] = new EventLoop();
        }
    }

    /**
     * 其实啥也不干，直接找到一个EventLoop，丢给他干
     */
    public void register(SocketChannel channel, int keyOps) {
        next().register(channel, keyOps);
    }
}
