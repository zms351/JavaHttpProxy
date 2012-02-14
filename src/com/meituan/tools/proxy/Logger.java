package com.meituan.tools.proxy;

//import org.jetbrains.annotations.Nullable;

public class Logger implements Constants {

    Object logger;

    Class klass;

    public Logger(Object logger) {
        this.logger=logger;
    }
    
    public void debug(/*@Nullable*/ Throwable t,String format,Object... args) {        
        String s = String.format(format, args);
        if(UseSysout) {
            if(t!=null) {
                t.printStackTrace(System.err);
            }
            System.out.printf("%s - %s: %s\n",klass.getName(),"Debug",s);
        } else {
            //((org.slf4j.Logger)logger).debug(s,t);
        }
    }
    
    public void debug(String format,Object... args) {
        debug(null,format,args);
    }
    
}
