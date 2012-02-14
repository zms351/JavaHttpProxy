package com.meituan.tools.proxy.test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ProxyTests {

    public void test01() throws Exception {
        try (Socket socket=new Socket("v2.tudou.com",80)) {
            DataOutputStream output=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.write("GET /crossdomain.xml HTTP/1.1\r\n".getBytes());
            output.write("Host: v2.tudou.com\r\n".getBytes());
            output.write("Connection: close\r\n".getBytes());
            output.write('\r');
            output.write('\n');
            output.flush();
            BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while((line=reader.readLine())!=null) {
                System.out.println(line);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ProxyTests test=new ProxyTests();
        test.test01();
    }
    
}
