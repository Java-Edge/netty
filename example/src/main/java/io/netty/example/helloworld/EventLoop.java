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
                channel.register(selector, keyOps);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        });
        // 但此时EventLoop的线程阻塞在 selector.select()，通过主线程唤醒它
        selector.wakeup();
    }

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

                        // 可读事件
                        if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            try {
                                int readNum = socketChannel.read(buffer);
                                if (readNum == -1) {
                                    System.out.println("读取结束,关闭 socket");
                                    key.channel();
                                    socketChannel.close();
                                    break;
                                }
                                // 将Buffer从写模式切到读模式
                                buffer.flip();
                                byte[] bytes = new byte[readNum];
                                buffer.get(bytes, 0, readNum);
                                System.out.println(new String(bytes));

/*                        byte[] response = "client hello".getBytes();
                        // 清理了才可以重新使用
                        buffer.clear();
                        buffer.put(response);
                        buffer.flip();
                        // 该方法非阻塞的，如果此时无法写入也不会阻塞在此，而是直接返回 0 了
                        socketChannel.write(buffer);

                        */
                                // 在 key 上附加一个对象
                                key.attach("EventLoop says hello to client".getBytes());
                                // 把 key 关注的事件切换为写
                                key.interestOps(SelectionKey.OP_WRITE);

                            } catch (IOException e) {
                                System.out.println("读取时发生异常,关闭 socket");
                                // 取消 key
                                key.channel();
                            }
                        }

                        if (key.isWritable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            // 可写时再将那个对象拿出来
                            byte[] bytes = (byte[]) key.attachment();
                            key.attach(null);
                            System.out.println("可写事件发生 写入消息" + Arrays.toString(bytes));
                            if (bytes != null) {
                                socketChannel.write(ByteBuffer.wrap(bytes));
                            }

                            // 写完后，就不需要写了，就切换为读事件   如果不写该行代码就会死循环
                            key.interestOps(SelectionKey.OP_READ);
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
