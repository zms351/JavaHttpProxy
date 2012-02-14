package com.meituan.tools.proxy;

import com.meituan.service.GarUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.Callable;

class ProcessThread implements Callable<Object>, Closeable, Constants {

    JavaHttpProxy parent;
    Socket socket;
    private DataInputStream _input;
    DataOutputStream output;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public DataInputStream getInput() {
        return _input;
    }

    public ProcessThread(JavaHttpProxy parent, Socket socket) {
        this.parent = parent;
        this.socket = socket;
    }

    @Override
    public Object call() throws Exception {
        try {
            _input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            try (ProxyThread read = new ProxyThread(this)) {
                read.call();
            } catch (ParseException t) {
                logger.debug("Parse Request Error: %s", t.getMessage());
            } catch (SocketException t) {
                logger.debug("socket error");
            } catch (Throwable t) {
                logger.debug(t, "Parse Request other error");
            }
        } finally {
            this.close();
        }
        return this;
    }

    @Override
    public void close() throws IOException {
        logger.debug("connection at %s closed", socket.getRemoteSocketAddress());
        socket.close();
    }

    protected static final int BufferSize = 1024 * 1024 * 2;

    public boolean reply(HttpResponse response) throws IOException {
        String encoding = response.getSendEncoding();
        output.write(response.getVersionTok().getBytes(encoding));
        output.write(' ');
        output.write(String.valueOf(response.getStatucCode()).getBytes(encoding));
        output.write(' ');
        output.write(response.getStatusMessage().getBytes(encoding));
        output.write(LineBreak);
        List<NameValuePair> headers = response.getHeaders();
        byte[] body = response.getBody();

        MyByteArrayOutputStream baos = null;
        InputStream input = null;
        String _length = null;
        int size = 0;
        if (body == null || body.length < 1) {
            if ((input = response.getInput()) == null) {
                ProxyUtils.setHeader(headers, ContentLength, 0);
            } else {
                _length = ProxyUtils.getHeaderValue(response.getHeaders(), ContentLength);
                if (_length == null || _length.length() < 1) {
                    if ((baos = response.baos) != null) {
                        baos.reset();
                        size = GarUtils.transfer(input, baos, BufferSize);
                        if (size < BufferSize) {
                            ProxyUtils.setHeader(headers, ContentLength, size);
                        }
                    }
                }
            }
        } else {
            ProxyUtils.setHeader(headers, ContentLength, body.length);
        }

        ProxyUtils.sendHeaders(output, headers, encoding);
        output.write(LineBreak);

        try {
            if (body == null || body.length < 1) {
                if (input != null) {
                    if (_length == null || _length.length() < 1) {
                        if (baos != null) {
                            output.write(baos.getBuffer(),0,baos.size());
                            if (size < BufferSize) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        GarUtils.transfer(input, output, Integer.parseInt(_length));
                    }
                }
            } else {
                output.write(body);
            }
            return false;
        } finally {
            output.flush();
        }
    }

}
