package com.meituan.tools.proxy.interceptors;

import com.meituan.service.GarUtils;
import com.meituan.tools.proxy.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class GZIPInterceptor implements RequestInterceptor,Constants {
    
    public static final int MaxSize=1024*1024*2;

    @Override
    public void on(ProxyThread thread) {
        try {
            int code=Integer.parseInt(thread.responseFirstLine[1]);
            if(code==200) {
                String _length= ProxyUtils.getHeaderValue(thread.responseHeaders,ContentLength);
                String _encoding=ProxyUtils.getHeaderValue(thread.responseHeaders,ContentEncoding);
                if(_length!=null && _length.length()>0 && "gzip".equalsIgnoreCase(_encoding) && thread.responseInput!=null) {
                    int length=Integer.parseInt(_length);
                    if(length<=MaxSize) {
                        MyByteArrayOutputStream buffer=thread.output;
                        buffer.reset();
                        int size;
                        try (GZIPInputStream input=new GZIPInputStream(thread.responseInput)) {
                            size=GarUtils.transfer(input,buffer);
                        }
                        assert(size==buffer.size());
                        thread.responseInput=new ByteArrayInputStream(buffer.getBuffer(),0,size);
                        ProxyUtils.removeHeader(thread.responseHeaders,ContentEncoding);
                        ProxyUtils.setHeader(thread.responseHeaders,ContentLength,size);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
    }

}
