package com.meituan.tools.proxy;

import com.meituan.tasks.Executors;
import com.meituan.tools.proxy.interceptors.ComboInterceptor;
import com.meituan.tools.proxy.interceptors.GZIPInterceptor;
import com.meituan.tools.proxy.interceptors.LogRequestInterceptor;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;

public class JavaHttpProxy implements Closeable {
    
    ServerSocket serverSocket;
    SSLServerSocket sslServerSocket;
    
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

    private SSLContext createSSLContext(){
        try{
            KeyStore keyStore = KeyStore.getInstance("JKS");
            String name=this.getClass().getName();
            name=name.substring(0,name.lastIndexOf('.')).replace('.','/')+"/key/test.keystore";
            try(InputStream input=this.getClass().getResourceAsStream(name)) {
                keyStore.load(input, "123456".toCharArray());
            }

            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "123456".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(km,  tm, null);

            return sslContext;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    protected void initHttps(String host, int port) throws Exception {
        SSLContext sslContext = this.createSSLContext();

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket;
        if(host==null || host.length()<1 || "null".equalsIgnoreCase(host)) {
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port+1);
        } else {
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port+1,0,Inet4Address.getByName(host));
        }
        this.sslServerSocket=sslServerSocket;
    }
    
    public void start() {
        Executors.getInstance().submitCommon(new AcceptThread(this));
        Executors.getInstance().submitCommon(new Accept2Thread(this));
    }

    @Override
    public void close() throws IOException {
        try {
            try {
                serverSocket.close();
            } finally {
                if (sslServerSocket != null) {
                    sslServerSocket.close();
                }
            }
        } finally {
            if(interceptor!=null) {
                interceptor.close();
            }
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
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        try (JavaHttpProxy proxy=new JavaHttpProxy(host, port,interceptor)) {
            proxy.initHttps(host,port);
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
