package com.example.push_lib;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

public class Config {

    long cvExpire = 1800000;
    long cuExpire = 63072000000L;
    long ccExpire = 63072000000L;
    long czExpire = 63072000000L;

    String uid = null;
    String vid = null;
    String preVisitDate = null;
    String frequency = "0";

    private static Map<String, String> config = new HashMap<>();
    private static Map<String, String> access = new HashMap<>();

    protected static void setConfig(String key, String value){ config.put(key, value); }

    protected static String getConfig(String key){
        if(config.get(key) == null) return "";

        return config.get(key);
    }

    protected static void setAccess(String key, String value){ access.put(key, value); }
    protected static String getAccess(String key){ return access.get(key); }
}
