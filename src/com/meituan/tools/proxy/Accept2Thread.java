package com.meituan.tools.proxy;

import java.net.ServerSocket;

/**
 * Created by 张小美 on 2021/12/11.
 * Copyright 2002-2016
 */
class Accept2Thread extends AcceptThread {

    public Accept2Thread(JavaHttpProxy parent) {
        super(parent);
    }

    @Override
    protected ServerSocket getServer() {
        return parent.sslServerSocket;
    }

}
