package io.netty.example.helloworld;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 类似 netty 的 channel
 *
 * @author JavaEdge
 *
 * @date 2021/5/27
 */
public class MyChannel {

    private SocketChannel channel;

    private EventLoop eventLoop;

    private PipeLine pipeLine;

    /**
     * 写数据的(临时)缓冲区
     */
    private Queue<ByteBuffer> writeQueue = new ArrayBlockingQueue<>(16);

    public MyChannel(SocketChannel channel,EventLoop eventLoop) {
        this.channel = channel;
        this.eventLoop = eventLoop;
        this.pipeLine = new PipeLine(this, eventLoop);
        this.pipeLine.addLast(new MyHandler1());
        this.pipeLine.addLast(new MyHandler2());
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int readNum = socketChannel.read(buffer);
            if (readNum == -1) {
                System.out.println("读取结束,关闭 socket");
                key.channel();
                socketChannel.close();
                return;
            }
            // 将Buffer从写模式切到读模式
            buffer.flip();
            this.pipeLine.headCtx.fireChannelRead(buffer);
        } catch (IOException e) {
            System.out.println("读取时发生异常,关闭 socket");
            // 取消 key
            key.channel();
            socketChannel.close();
        }
    }

    /**
     * EventLoop 里才能访问，不直接对外暴露,应该是被封装的，真正的往底层写数据的
     */
    public void write(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer;
        while ((byteBuffer = writeQueue.poll()) != null) {
            channel.write(byteBuffer);
        }

        // 写完后，就不需要写了，就切换为读事件   如果不写该行代码就会死循环
        key.interestOps(SelectionKey.OP_READ);
    }

    public void doWrite(Object msg) {
        this.pipeLine.tailCtx.write(msg);
    }

    public void addWriteQueue(ByteBuffer buffer) {
        writeQueue.add(buffer);
    }

    public void flush() {
        this.pipeLine.tailCtx.flush();
    }

    /**
     * 真正的写回客户端，由于 write 方法每次被调用后就切换为读事件了，
     * 所以此时要触发更改事件类型，将缓存区队列一次性写完
     *
     * 因为切换为写事件后，EventLoop 接到写请求后，会立即触发写完队列
     */
    public void doFlush() {
        this.channel.keyFor(eventLoop.getSelector()).interestOps(SelectionKey.OP_WRITE);
    }
}
