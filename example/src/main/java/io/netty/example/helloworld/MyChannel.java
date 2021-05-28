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
     * 写数据的缓冲区
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

    public void write(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer;
        while ((byteBuffer = writeQueue.poll()) != null) {
            channel.write(byteBuffer);
        }

        // 写完后，就不需要写了，就切换为读事件   如果不写该行代码就会死循环
        key.interestOps(SelectionKey.OP_READ);
    }
}
