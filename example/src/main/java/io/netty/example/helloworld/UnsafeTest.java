package io.netty.example.helloworld;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
/**
 * @author JavaEdge
 * @date 2021/5/17
 */
public class UnsafeTest {

    public static void main(String[] args) {
        // 拿到堆外内存管理工具
        Unsafe unsafe = getUnsafe();

        // 分配10个字节的内存，返回值为内存基础地址
        long address = unsafe.allocateMemory(10);

        // 传入基础地址，长度10,  byte-0，初始化堆外内存
        unsafe.setMemory(address, 10L, (byte) 0);

        // 传入内存地址位置设置byte值
        unsafe.putByte(address, (byte) 1);
        unsafe.putByte(address+1, (byte) 2);
        unsafe.putByte(address+2, (byte) 3);

        // 根据内存地址获取byte值
        System.out.println(unsafe.getByte(address));
        System.out.println(unsafe.getByte(address+1));
        System.out.println(unsafe.getByte(address+2));
        // 未设置值，取出是 0
        System.out.println(unsafe.getByte(address+3));

        //释放内存
        unsafe.freeMemory(address);
    }

    /**
     * 反射拿到Unsafe对象
     *
     * @return
     */
    private static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
