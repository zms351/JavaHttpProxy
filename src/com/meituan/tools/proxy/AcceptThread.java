package com.meituan.tools.proxy;

import com.meituan.tasks.Executors;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

class AcceptThread implements Callable<Object> {

    JavaHttpProxy parent;
    Logger logger= LoggerFactory.getLogger(this.getClass());

    public AcceptThread(JavaHttpProxy parent) {
        this.parent=parent;
    }

    protected ServerSocket getServer() {
        return parent.serverSocket;
    }

    @Override
    public Object call() throws Exception {
        ServerSocket serverSocket=getServer();
        while(serverSocket.isBound()) {
            Socket socket=serverSocket.accept();
            logger.debug("connection from %s (%d)",socket.getRemoteSocketAddress(),socket.getLocalPort());
            Executors.getInstance().submitCommon(new ProcessThread(parent,socket));
        }
        return this;
    }
    
}
