package com.developerspace.filestorageserver.domain.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author wangz
 * @Date 2021-04-06 0:13
 */
public class UUID {
    private static long lastId = 0;
    private static Object locked = new Object();
    public static long getId() {
        long currId = 0;
        //上锁
        synchronized (locked){
            //年、月、日、时、分、秒、毫秒
            currId = Long.parseLong(new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date()).toString()) * 100;
            if (lastId < currId) {
                lastId = currId;
            } else {
                lastId = lastId + 1;
                currId = lastId;
            }
            return currId;
        }
    }
    public static void main(String[] args) {
        Set set = new LinkedHashSet();
        Thread[] threads = new Thread[10];
        for(int i=0;i<threads.length;i++){
            threads[i] = new Thread(()->{
                for(int j=0;j<10;j++){
                    log.info(UUID.getId());
                }
            });
            threads[i].start();
        }
        log.info(set.size());
    }
}