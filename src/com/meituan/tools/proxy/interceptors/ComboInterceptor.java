package com.meituan.tools.proxy.interceptors;

import com.meituan.tools.proxy.ProxyThread;
import com.meituan.tools.proxy.RequestInterceptor;

import java.io.IOException;

public class ComboInterceptor implements RequestInterceptor {
    
    private RequestInterceptor[] interceptors;

    public ComboInterceptor(RequestInterceptor... interceptors) {
        this.interceptors=interceptors;
    }

    @Override
    public void on(ProxyThread thread) throws Exception {
        for (RequestInterceptor interceptor : interceptors) {
            interceptor.on(thread);
        }
    }

    @Override
    public void close() throws IOException {
        for (RequestInterceptor interceptor : interceptors) {
            interceptor.close();
        }
    }

}
