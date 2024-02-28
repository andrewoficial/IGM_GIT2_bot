package services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
    public static final Logger logger2 = LogManager.getLogger(MyLogger.class);

    public static final void myError (String msg){
        System.out.println(msg);
        logger2.error(msg);
    }

    public static final void myInfo (String msg){
        System.out.println(msg);
        logger2.info(msg);
    }

}
