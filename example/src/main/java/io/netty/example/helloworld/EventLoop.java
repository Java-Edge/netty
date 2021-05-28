package io.netty.example.helloworld;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author JavaEdge
 * @date 2021/5/25
 */
public class EventLoop implements Runnable {

    private Selector selector;

    private Thread thread;

    private Queue<Runnable> taskQueue = new LinkedBlockingDeque<>(32);

    public EventLoop() throws IOException {
        this.selector = SelectorProvider.provider().openSelector();
        this.thread = new Thread(this);
        this.thread.start();
    }

    /**
     * 把 channel 注册到 事件查询器
     */
    public void register(SocketChannel channel, int keyOps) {
        // 将注册的逻辑封装成一个任务，因为不能让主线程执行，必须由 eventloop 的线程执行
        taskQueue.add(() -> {
            try {
                MyChannel myChannel = new MyChannel(channel, this);
                SelectionKey selectionKey = channel.register(selector, keyOps);
                selectionKey.attach(myChannel);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        });
        // 但此时EventLoop的线程阻塞在 selector.select()，通过主线程唤醒它
        selector.wakeup();
    }

    /**
     * 改造后的 EventLoop 职责就更单一了，只负责转发事件即可
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                System.out.println(thread + "开始查询 I/O 事件...");
                // 阻塞方法，等待系统有 I/0 事件产生
                int eventNum = selector.select();
                System.out.println("系统发生IO事件 数量->" + eventNum);

                // 有事件则处理
                if (eventNum > 0) {
                    Set<SelectionKey> keySet = selector.selectedKeys();
                    Iterator<SelectionKey> iterable = keySet.iterator();
                    while (iterable.hasNext()) {
                        SelectionKey key = iterable.next();
                        iterable.remove();

                        // myChannel中已经封装了 channel 和 selector 对应关系
                        MyChannel myChannel = (MyChannel) key.attachment();

                        // 可读事件
                        if (key.isReadable()) {
                            myChannel.read(key);
                        }

                        // 可写事件
                        if (key.isWritable()) {
                            myChannel.write(key);
                        }
                    }
                }

                // 无事件则执行任务
                Runnable task;
                while ((task = taskQueue.poll()) != null) {
                    // EventLoop执行队列中的任务，即注册任务
                    task.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
