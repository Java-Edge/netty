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
        srcBuf.writeByte(11);
        srcBuf.writeByte(22);
        srcBuf.writeByte(33);
        srcBuf.writeByte(44);
        srcBuf.writeByte(55);
        System.out.println(srcBuf.readByte());
        System.out.println(srcBuf.readByte());
        System.out.println(srcBuf.readByte());
        ByteBuf dupBuf = srcBuf.duplicate();

        srcBuf.writeByte(66);

        System.out.println(dupBuf.readerIndex() + "|" + dupBuf.writerIndex());
        dupBuf.writeByte(77);

        System.out.println(srcBuf.readByte());
        System.out.println(srcBuf.readByte());
        System.out.println(srcBuf.readByte());
    }
}
