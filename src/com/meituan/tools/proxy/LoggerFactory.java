package com.meituan.tools.proxy;

public class LoggerFactory implements Constants {

    static Logger getLogger(Class klass) {
        Logger logger;
        if (UseSysout) {
            logger = new Logger(null);
        } else {
            //logger = new Logger(org.slf4j.LoggerFactory.getLogger(klass));
            logger=null;
        }
        logger.klass = klass;
        return logger;
    }

}
