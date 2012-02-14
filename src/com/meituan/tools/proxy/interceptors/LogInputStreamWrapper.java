package com.meituan.tools.proxy.interceptors;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LogInputStreamWrapper extends FilterInputStream {

    private OutputStream output;
    private boolean closeInput;
    private boolean closeOutput;
    
    protected LogInputStreamWrapper(InputStream in,boolean closeInput,OutputStream output,boolean closeOutput) {
        super(in);
        this.output=output;
        this.closeInput=closeInput;
        this.closeOutput=closeOutput;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            if(closeOutput) {
                this.output.close();
            }
            if(closeInput) {
                this.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public int read() throws IOException {
        int n = super.read();
        if(n>=0) {
            output.write(n);
        }
        return n;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b,0,b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int size = super.read(b, off, len);
        if(size>0) {
            output.write(b,off,size);
        }
        return size;
    }
    
}
