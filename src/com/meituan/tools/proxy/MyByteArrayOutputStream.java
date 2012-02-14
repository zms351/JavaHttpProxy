package com.meituan.tools.proxy;

import java.io.ByteArrayOutputStream;

public class MyByteArrayOutputStream extends ByteArrayOutputStream {

    public MyByteArrayOutputStream() {
        super();
    }

    public MyByteArrayOutputStream(int size) {
        super(size);
    }
    
    public byte[] getBuffer() {
        return super.buf;
    }

    @Override
    public byte[] toByteArray() {
        return super.toByteArray();
    }

}
