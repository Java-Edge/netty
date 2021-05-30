package io.netty.example.helloworld;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author JavaEdge
 * @date 2021/5/17
 */
@Slf4j
public class ByteBufTest {

    public static void main(String[] args) {
        // 会自动扩容的
        ByteBuf heapBuffer = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
        heapBuffer.writeByte(1);
        heapBuffer.writeByte(2);
        heapBuffer.writeByte(3);
        heapBuffer.writeByte(4);
        heapBuffer.writeByte(5);
        heapBuffer.readByte();
        heapBuffer.readByte();

        System.out.println(heapBuffer.readerIndex() + "|" + heapBuffer.writerIndex());

        ByteBuf sliceBuf = heapBuffer.slice(heapBuffer.readerIndex(), heapBuffer.readableBytes());
        System.out.println(sliceBuf.readerIndex() + "|" + sliceBuf.writerIndex());
        System.out.println(sliceBuf.readByte());
        System.out.println(sliceBuf.writerIndex(4));
//        heapBuffer.discardReadBytes();
//        System.out.println(heapBuffer.readerIndex() + "|" + heapBuffer.writerIndex());
//
//        ByteBuf directBuffer = UnpooledByteBufAllocator.DEFAULT.directBuffer(10);
//        System.out.println("refCnt:" + directBuffer.refCnt());
//        directBuffer.release();
//        System.out.println("refCnt:" + directBuffer.refCnt());
    }
}
