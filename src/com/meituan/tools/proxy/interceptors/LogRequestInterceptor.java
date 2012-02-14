package com.meituan.tools.proxy.interceptors;

import com.meituan.tools.proxy.ProxyThread;
import com.meituan.tools.proxy.RequestInterceptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogRequestInterceptor implements RequestInterceptor {

    File root;
    
    public LogRequestInterceptor(File root) {
        this.root=root;
        if(!root.exists()) {
            boolean r=root.mkdirs();
            assert(r);
        }
    }
    @Override
    public void on(ProxyThread thread) {
        try {
            int code=Integer.parseInt(thread.responseFirstLine[1]);
            if(code==200) {
                File file=new File(root,thread.uriToks[1]+"_"+thread.uriToks[2]);
                if(!file.exists()) {
                    boolean r=file.mkdirs();
                    assert(r);
                }
                String path=thread.uriToks[3].toString();
                int index=path.indexOf('?');
                if(index>0) {
                    path=path.substring(0,index);
                }
                if(path.endsWith("/")) {
                    path+="root_index.html";
                }
                assert(path.startsWith("/"));
                path=replaceSpecialChars(path);
                file=new File(file,path.substring(1));
                File folder=file.getParentFile();
                if(!folder.exists()) {
                    boolean r=folder.mkdirs();
                    assert(r);
                }
                File meta=new File(folder,file.getName()+".logmeta");
                try (FileOutputStream output=new FileOutputStream(meta,true)) {
                    thread.sendRequestHeaders(output,ProxyThread.HeaderEncoding);
                    thread.sendResponseHeaders(output,ProxyThread.HeaderEncoding);
                    output.flush();
                }
                thread.responseInput=new LogInputStreamWrapper(thread.responseInput,false,new FileOutputStream(file),true);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    protected String replaceSpecialChars(String s) {
        char[] chars=s.toCharArray();
        for(int i=0;i<chars.length;i++) {
            char c=chars[i];
            if(c=='/' || c=='.' || c=='-' || c=='_' || (c>='0' && c<='9') || (c>='a' && c<='z') || (c>='A' && c<='Z')) {
            } else {
                c='_';
            }
            chars[i]=c;
        }
        return new String(chars);
    }

    @Override
    public void close() throws IOException {
    }

}
