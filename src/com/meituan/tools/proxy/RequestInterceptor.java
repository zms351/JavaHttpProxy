package com.meituan.tools.proxy;

import java.io.Closeable;

public interface RequestInterceptor extends Closeable {

    public void on(ProxyThread thread) throws Exception;

}
