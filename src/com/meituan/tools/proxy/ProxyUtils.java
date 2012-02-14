package com.meituan.tools.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class ProxyUtils implements Constants {

    public static void sendHeaders(OutputStream output, List<NameValuePair> pairs, String encoding) throws IOException {
        for (NameValuePair pair : pairs) {
            output.write(pair.getName().getBytes(encoding));
            String value = pair.getValue();
            if (value != null) {
                output.write(':');
                output.write(' ');
                output.write(value.getBytes(encoding));
            }
            output.write(LineBreak);
        }
    }

    public static boolean setHeader(List<NameValuePair> headerList, String name, Object value) {
        boolean find = false;
        for (NameValuePair pair : headerList) {
            if (pair.getName().equalsIgnoreCase(name)) {
                pair.setValue(value.toString());
                find = true;
            }
        }
        if (!find) {
            headerList.add(new NameValuePair(name, value.toString()));
        }
        return find;
    }

    public static void parseHeaders(List<NameValuePair> headers, List<String> lines) {
        headers.clear();
        int size = lines.size();
        for (int i = 1; i < size; i++) {
            String line = lines.get(i);
            int index = line.indexOf(':');
            if (index > 0) {
                headers.add(new NameValuePair(line.substring(0, index), line.substring(index + 1).trim()));
            } else {
                headers.add(new NameValuePair(line, null));
            }
        }
    }

    public static int removeHeader(List<NameValuePair> headerList, String name) {
        int count=0;
        for(Iterator<NameValuePair> it = headerList.iterator();it.hasNext();) {
            if(it.next().getName().equalsIgnoreCase(name)) {
                it.remove();
                count++;
            }
        }
        return count;
    }
    
    public static String getHeaderValue(List<NameValuePair> headerList, String name) {
        for (NameValuePair pair : headerList) {
            if (pair.getName().equalsIgnoreCase(name)) {
                String value = pair.getValue();
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    public static void readHeaders(MyByteArrayOutputStream outputBuffer, List<String> lines, InputStream input, String encoding) throws IOException {
        outputBuffer.reset();
        lines.clear();
        int n;
        int last = -1;
        while ((n = input.read()) >= 0) {
            outputBuffer.write(n);
            if (n == '\n') {
                int index = outputBuffer.size() - 1;
                byte[] buffer = outputBuffer.getBuffer();
                String line = new String(buffer, last + 1, index - last - 1, encoding).trim();
                if (line.length() > 0) {
                    lines.add(line);
                    last = index;
                } else {
                    if (lines.size() < 1) {
                        throw new ParseException("blank hreaders");
                    }
                    assert (last > 0);
                    //处理边界
                    int gap = index - last;
                    if (gap == 1) {
                        //\n分隔，已到边界
                        return;
                    } else if (gap == 2) {
                        //  \r\n 或 \n\r
                        int other = buffer[last + 1];
                        if (other == buffer[last - 1]) {
                            //\r\n
                            return;
                        } else {
                            //\n\r
                            n = input.read();
                            if (n < 0) {
                                throw new ParseException("incomplete header part");
                            }
                            outputBuffer.write(n);
                            if (n == other) {
                                //ok
                                return;
                            } else {
                                throw new ParseException("different line break chars");
                            }
                        }
                    } else {
                        throw new ParseException("bad line break chars");
                    }
                }
            }
        }
        throw new ParseException("eof before header complete");
    }

}
