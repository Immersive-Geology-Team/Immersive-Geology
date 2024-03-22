package com.igteam.immersivegeology.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
public class IGApi {
    public final static String MODID = "immersivegeology";
    public static Logger getNewLogger()
    {
        return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
    }
}
