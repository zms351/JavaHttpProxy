package com.meituan.tools.proxy;

import com.meituan.service.GarUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;

public class ProxyThread implements Callable<Object>, Constants, Closeable {

    ProcessThread parent;
    DataInputStream requestInput;
    public InputStream responseInput;

    public final MyByteArrayOutputStream output;
    List<String> headerLines;

    public String[] requestFirstLine;
    public String[] responseFirstLine;
    public Object[] uriToks;
    public List<NameValuePair> requestHeaders;
    public List<NameValuePair> responseHeaders;
    public boolean keep;
    public byte[] mockBody;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String,Object> attributes=new HashMap<>();
    
    public synchronized Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    public synchronized Object setAttribute(String key,Object value) {
        return attributes.put(key,value);
    }

    public static String HeaderEncoding = "UTF-8";
    public static String DefaultSchema = "http://";
    public static String SchemaOther = "https://";

    public ProxyThread(ProcessThread parent) {
        this.parent = parent;
        this.requestInput = parent.getInput();
        output = new MyByteArrayOutputStream(1024 * 10);
        headerLines = new ArrayList<>();
        requestFirstLine = new String[3];
        responseFirstLine = new String[3];
        uriToks = new Object[4];
        requestHeaders = new LinkedList<>();
        responseHeaders = new LinkedList<>();
    }

    @Override
    public Object call() throws Exception {
        Socket socket = parent.socket;
        while (socket.isConnected()) {
            ProxyUtils.readHeaders(this.output, this.headerLines, this.requestInput, HeaderEncoding);
            parseRequestHeaders();
            logger.debug("received request %s %s", uriToks[1], uriToks[3]);
            if (Debug) {
                parent.reply(new HttpResponse(StatusCode.Forbidden, TestHtml.getBytes(HeaderEncoding), TextHtml));
                this.skipBody(this.requestHeaders, this.requestInput);
                continue;
            }
            if ("https://".equalsIgnoreCase(uriToks[0].toString())) {
                parent.reply(new HttpResponse(StatusCode.MethodNotAllowed, "https not supported".getBytes(HeaderEncoding), TextHtml));
                this.skipBody(this.requestHeaders, this.requestInput);
                continue;
            }
            ProxyUtils.setHeader(this.requestHeaders, Connection, "close");
            try {
                sendRequest();
            } catch (Throwable t) {
                this.close();
                StringBuilder buffer = new StringBuilder();
                String s = "error occurred when proxy: %s ";
                buffer.append(s);
                buffer.append(t.getMessage());
                if(Debug) {
                    logger.debug(t, s, t.getMessage());
                } else {
                    logger.debug(s,t.getMessage());
                }
                parent.reply(new HttpResponse(StatusCode.ServiceUnavailable, buffer.toString().getBytes(HeaderEncoding), TextHtml));
                continue;
            }
            logger.debug("received response %s %s %s", uriToks[1], uriToks[3], responseFirstLine[1]);
            this.mockBody = null;
            parent.parent.onOne(this);
            sendResponse();
        }
        return this;
    }

    boolean parseRequestHeaders() throws IOException {
        List<String> headers = headerLines;
        assert (headers.size() > 0);
        String first = headers.get(0);
        int index1 = first.indexOf(' ');
        int index2 = first.lastIndexOf(' ');
        if (index1 > 0 && index2 > index1) {
            String a = first.substring(0, index1).toUpperCase();
            String b = first.substring(index1 + 1, index2).trim();
            String c = first.substring(index2 + 1).toUpperCase();
            requestFirstLine[0] = a;
            requestFirstLine[1] = b;
            requestFirstLine[2] = c;
            if (!a.matches("^[A-Z]+$")) {
                throw new ParseException("bad request verb");
            }
            if (!c.matches("^HTTP/.+$")) {
                throw new ParseException("bad request version");
            }
            ProxyUtils.parseHeaders(requestHeaders, headers);
            if (b.startsWith("/")) {
                String host = ProxyUtils.getHeaderValue(this.requestHeaders, Host);
                if (host == null || host.length() < 1) {
                    throw new ParseException("need host");
                }
                uriToks[0] = DefaultSchema;
                uriToks[1] = host;
                uriToks[3] = b;
            } else {
                String s;
                if (b.length() > 8) {
                    s = b.substring(0, 8).toLowerCase();
                } else {
                    s = b.toLowerCase();
                }
                if (s.startsWith(DefaultSchema)) {
                    uriToks[0] = DefaultSchema;
                } else if (s.startsWith(SchemaOther)) {
                    uriToks[0] = SchemaOther;
                } else {
                    throw new ParseException("bad request path,bad schema");
                }
                b = b.substring(uriToks[0].toString().length());
                int index = b.indexOf('/');
                if (index < 1) {
                    throw new ParseException("bad request path,no host or path");
                }
                uriToks[1] = b.substring(0, index);
                uriToks[3] = b.substring(index);
                ProxyUtils.setHeader(requestHeaders, Host, uriToks[1]);
            }
            int port;
            String host = uriToks[1].toString();
            int index = host.lastIndexOf(':');
            if (index > 0) {
                port = Integer.parseInt(host.substring(index + 1).trim());
                host = host.substring(0, index);
            } else {
                port = DefaultPort;
            }
            uriToks[1] = host;
            uriToks[2] = port;
            String s1 = ProxyUtils.getHeaderValue(this.requestHeaders, Connection);
            String s2 = ProxyUtils.getHeaderValue(this.requestHeaders, ProxyConnection);
            keep = "keep-alive".equalsIgnoreCase(s1) || "keep-alive".equalsIgnoreCase(s2);
            ProxyUtils.removeHeader(this.requestHeaders, ProxyConnection);
            return true;
        } else {
            throw new ParseException("bad request first line");
        }
    }

    protected void skipBody(List<NameValuePair> requestHeaders, InputStream input) throws IOException {
        String _length = ProxyUtils.getHeaderValue(requestHeaders, ContentLength);
        if (_length != null && _length.length() > 0) {
            long length = Long.parseLong(_length);
            long n = input.skip(length);
            assert (n == length);
        }
    }

    protected void transferBody(OutputStream output) throws IOException {
        String _length = ProxyUtils.getHeaderValue(this.requestHeaders, ContentLength);
        if (_length != null && _length.length() > 0) {
            long length = Long.parseLong(_length);
            GarUtils.transfer(this.requestInput, output, length);
        }
    }

    protected Socket socket;

    @Override
    public synchronized void close() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Throwable t) {
            logger.debug(t, "close socket error");
        }
        this.responseInput = null;
    }

    protected void sendRequest() throws IOException {
        boolean skipped = false;
        try {
            String encoding = HeaderEncoding;
            close();
            if(this.parent.socket instanceof SSLSocket) {
                SSLSocketFactory socketFactory = (SSLSocketFactory)
                        SSLSocketFactory.getDefault();
                socket=socketFactory.createSocket(uriToks[1].toString(), ((Number) uriToks[2]).intValue());
                ((SSLSocket)socket).setEnabledCipherSuites(socketFactory.getSupportedCipherSuites());
            } else {
                socket = new Socket(uriToks[1].toString(), ((Number) uriToks[2]).intValue());
            }

            DataOutputStream output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            sendRequestHeaders(output, encoding);
            transferBody(output);
            skipped = true;
            output.flush();

            responseInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            while (true) {
                ProxyUtils.readHeaders(this.output, this.headerLines, responseInput, encoding);
                parseResponseHeaders();
                int status = Integer.parseInt(responseFirstLine[1]);
                if (status >= 200) {
                    break;
                }
                assert (status >= 100);
                skipBody(this.responseHeaders, responseInput);
            }
        } finally {
            if (!skipped) {
                this.skipBody(this.requestHeaders, this.requestInput);
            }
        }
    }

    public void sendRequestHeaders(OutputStream output, String encoding) throws IOException {
        output.write(requestFirstLine[0].getBytes(encoding));
        output.write(' ');
        output.write(uriToks[3].toString().getBytes(encoding));
        output.write(' ');
        output.write(requestFirstLine[2].getBytes(encoding));
        output.write(LineBreak);
        ProxyUtils.sendHeaders(output, this.requestHeaders, encoding);
        output.write(LineBreak);
    }

    public void sendResponseHeaders(OutputStream output, String encoding) throws IOException {
        output.write(responseFirstLine[0].getBytes(encoding));
        output.write(' ');
        output.write(responseFirstLine[1].getBytes(encoding));
        output.write(' ');
        output.write(responseFirstLine[2].getBytes(encoding));
        output.write(LineBreak);
        ProxyUtils.sendHeaders(output, this.responseHeaders, encoding);
        output.write(LineBreak);
    }

    boolean parseResponseHeaders() throws IOException {
        List<String> headers = headerLines;
        int size = headers.size();
        assert (size > 0);
        String first = headers.get(0);
        int index1 = first.indexOf(' ');
        int index2 = first.indexOf(' ', index1 + 1);
        if (index1 > 0 && index2 > index1) {
            String a = first.substring(0, index1).toUpperCase();
            String b = first.substring(index1 + 1, index2).trim();
            String c = first.substring(index2 + 1).trim();
            responseFirstLine[0] = a;
            responseFirstLine[1] = b;
            responseFirstLine[2] = c;
            if (!a.matches("^HTTP/.+$")) {
                throw new ParseException("bad response version");
            }
            if (!b.matches("^\\d+$")) {
                throw new ParseException("bad response status code");
            }
            if (c.length() < 1) {
                throw new ParseException("no response status message");
            }
            ProxyUtils.parseHeaders(responseHeaders, headers);
            return true;
        } else {
            throw new ParseException("bad request first line");
        }
    }

    protected void sendResponse() throws IOException {
        HttpResponse response = new HttpResponse(Integer.parseInt(responseFirstLine[1]), responseFirstLine[2]);
        response.setVersionTok(responseFirstLine[0]);
        if (keep) {
            ProxyUtils.setHeader(this.responseHeaders, Connection, "keep-alive");
        }
        response.setHeaders(this.responseHeaders);
        response.setSendEncoding(HeaderEncoding);
        response.setInput(this.responseInput);
        response.setBody(this.mockBody);
        response.baos=this.output;
        if (parent.reply(response)) {
            try {
                GarUtils.transfer(this.responseInput, parent.output);
            } catch (Throwable t) {
                logger.debug(t, "transfer error");
            } finally {
                parent.output.flush();
            }
        }
    }

}
