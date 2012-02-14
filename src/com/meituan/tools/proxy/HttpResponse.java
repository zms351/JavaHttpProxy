package com.meituan.tools.proxy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class HttpResponse implements Constants {
    
    private String versionTok="HTTP/1.1";
    private int statucCode;
    private String statusMessage;
    private List<NameValuePair> headers;
    private byte[] body;
    public static String HeaderEncoding=ProxyThread.HeaderEncoding;
    private String sendEncoding=HeaderEncoding;
    private InputStream input;
    MyByteArrayOutputStream baos;

    private HttpResponse() {
        headers=new ArrayList<>();
        headers.add(new NameValuePair(Server,ProxyServerName));
    }
    
    public void setHeaders(List<NameValuePair> _headers) {
        this.headers.clear();
        this.headers.addAll(_headers);
        String server=ProxyUtils.getHeaderValue(this.headers,Server);
        if(server==null || server.length()<1) {
            ProxyUtils.setHeader(this.headers,Server,ProxyServerName);
        }
    }
    
    public HttpResponse(int status,String message) {
        this();
        this.statucCode=status;
        this.statusMessage=message;
    }
    
    public HttpResponse(StatusCode status) {
        this();
        this.statucCode=status.getCode();
        this.statusMessage=status.name();
    }
    
    public HttpResponse(StatusCode status,byte[] body) {
        this(status);
        this.setBody(body);
    }
    
    public HttpResponse(StatusCode status,byte[] body,String contentType) {
        this(status,body);
        ProxyUtils.setHeader(this.getHeaders(),ContentType,contentType);
    }

    public String getVersionTok() {
        return versionTok;
    }

    public void setVersionTok(String versionTok) {
        this.versionTok = versionTok;
    }

    public int getStatucCode() {
        return statucCode;
    }

    public void setStatucCode(int statucCode) {
        this.statucCode = statucCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<NameValuePair> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getSendEncoding() {
        return sendEncoding;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public void setSendEncoding(String sendEncoding) {
        this.sendEncoding = sendEncoding;
    }

}
