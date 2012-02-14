package com.meituan.tools.proxy;

import com.meituan.tasks.Executors;
import com.meituan.tools.proxy.interceptors.ComboInterceptor;
import com.meituan.tools.proxy.interceptors.GZIPInterceptor;
import com.meituan.tools.proxy.interceptors.LogRequestInterceptor;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

public class JavaHttpProxy implements Closeable {
    
    ServerSocket serverSocket;    
    
    public JavaHttpProxy(String host, int port,RequestInterceptor interceptor) throws IOException {
        this.interceptor=interceptor;
        if(host==null || host.length()<1 || "null".equalsIgnoreCase(host)) {
            serverSocket=new ServerSocket(port);
        } else {
            serverSocket=new ServerSocket();
            SocketAddress address=new InetSocketAddress(host,port);
            serverSocket.bind(address);
        }
    }
    
    public void start() {
        Executors.getInstance().submitCommon(new AcceptThread(this));
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
        if(interceptor!=null) {
            interceptor.close();
        }
    }
    
    protected RequestInterceptor interceptor;

    public void onOne(ProxyThread thread) {
        if(interceptor!=null) {
            try {
                interceptor.on(thread);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length<3) {
            System.out.println("Usage: JavaHttpProxy host port root");
            System.out.println("host may be null,but must specified as 'null'");
            return;
        }
        RequestInterceptor interceptor=new ComboInterceptor(new GZIPInterceptor(),new LogRequestInterceptor(new File(args[2])));
        try (JavaHttpProxy proxy=new JavaHttpProxy(args[0],Integer.parseInt(args[1]),interceptor)) {
            proxy.start();
            if(args.length>3) {
                while(args[3]!=null) {
                    Thread.sleep(100);
                }
            } else {
                System.out.println("enter to exit");
                System.out.println(System.in.read());
            }
        } finally {
            System.out.println("finished");
        }
    }
    
}
