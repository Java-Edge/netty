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
        ByteBuf srcBuf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
        srcBuf.writeByte(1);
        srcBuf.writeByte(2);
        srcBuf.writeByte(3);
        srcBuf.writeByte(4);
        srcBuf.writeByte(5);
        srcBuf.readByte();
        srcBuf.readByte();
        System.out.println(srcBuf.readerIndex() + "|" + srcBuf.writerIndex());

        ByteBuf slice = srcBuf.slice(srcBuf.readerIndex(), srcBuf.readableBytes());
        System.out.println(slice.readerIndex() + "|" + slice.writerIndex());
        System.out.println(slice.readByte());
        slice.setByte(1, 10);
        System.out.println(slice.readByte());
        System.out.println(slice.readByte());
    }
}
