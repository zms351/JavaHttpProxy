package com.meituan.tools.proxy;

public interface Constants {
    
    public static final String Host="Host";
    public static final String Server="Server";
    public static final String ContentLength="Content-Length";
    public static final String ContentType="Content-Type";
    public static final String ContentEncoding="Content-Encoding";
    public static final String TestHtml="this is <b>test</b> html!";
    public static final boolean Debug=false;
    public static final String TextHtml="text/html";
    public static final int DefaultPort=80;
    public static final String Connection="Connection";
    public static final String ProxyConnection="Proxy-Connection";
    public static final String ProxyServerName="zms-java-proxy";
    public static final boolean UseSysout=true;
    public static final byte[] LineBreak=new byte[]{'\r','\n'};

}
