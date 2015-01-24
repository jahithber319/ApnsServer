package com.apns.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotifications;
import javapns.notification.transmission.NotificationProgressListener;
import javapns.notification.transmission.NotificationThread;
import javapns.notification.transmission.NotificationThreads;
import javapns.notification.transmission.PushQueue;


import net.sf.json.JSONObject;

import org.json.JSONException;

public class IosPushMain {
    public static String keystore = null;
    public static String password = null;
    public static String host = null;
    public static Boolean production = true;	//true：production false: sandbox
    public static final int numberOfThreads = 8;
	
//    public static void main(String[] args) throws Exception { 	
//    	JSONObject jsonObject=new JSONObject();
//    	jsonObject.put("TOKEN", "bbcc4d6a 30fc024f 01ba4759 758c9eae 6f23a844 7309599c e1bfe2ca 1df21e01");   	
//    	jsonObject.put("PAYLOAD", "哈哈哈哈"); 	  	  
//    	for (int i=0;i<10;i++){  
//    		jsonObject.put("ALERT", "有新的消息"+i);
//    		String pubstr = jsonObject.toString();   
//    		pushMain(pubstr);
//    	}   	
//    }
    
    public static void pushMain(String pushjson) throws Exception{
       	
 	   String keystore = "apns.p12";//证书路径和证书名  
        String password = "123456"; 			// 证书密码  
        boolean production = false; // 设置true为正式服务地址，false为开发者地址  
        int threadThreads = 10; // 线程数    
        try {  
        	//解析alerttext
        	 //开始解析报文内容s
	        JSONObject jsonObject=new JSONObject();
	        jsonObject = JSONObject.fromObject(pushjson);

	        String alert = jsonObject.getString("ALERT");
	        String token = jsonObject.getString("TOKEN");
	        String payload = jsonObject.getString("PAYLOAD");
	        
	        token =  token.replace(" ", "");
            // 建立与Apple服务器连接  
            AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production  );  
		   List<PayloadPerDevice> list = new ArrayList<PayloadPerDevice>();  
		  
		   PushNotificationPayload pushPayload = new PushNotificationPayload();  	       
		   pushPayload.addAlert(alert);  
		   pushPayload.addSound("default");// 声音  
//		   pushPayload.addBadge(1);//图标小红圈的数值  
		   pushPayload.addCustomDictionary("payload",payload);// 添加字典   
		   PayloadPerDevice pay = new PayloadPerDevice(pushPayload,token);// 将要推送的消息和手机唯一标识绑定   	       
		   list.add(pay);  	            
 	       
 	       System.out.println("开始推送消息:"+alert);  
 	       NotificationThreads work = new NotificationThreads(server,list,threadThreads);//   
 	       
 	       work.setListener(DEBUGGING_PROGRESS_LISTENER);// 对线程的监听，一定要加上这个监听  
 	       work.start(); // 启动线程  
 	       work.waitForAllThreads();// 等待所有线程启动完成  
 	     
 	    } catch (Exception e) {  
 	       e.printStackTrace();  
 	    } 
    }
 // 线程监听  
    public static final NotificationProgressListener DEBUGGING_PROGRESS_LISTENER = new NotificationProgressListener() {  
            public void eventThreadStarted(NotificationThread notificationThread) {  
                System.out.println("   [EVENT]: thread #" + notificationThread.getThreadNumber() + " started with " + " devices beginning at message id #" + notificationThread.getFirstMessageIdentifier());  
            }  
            public void eventThreadFinished(NotificationThread thread) {  
                System.out.println("   [EVENT]: thread #" + thread.getThreadNumber() + " finished: pushed messages #" + thread.getFirstMessageIdentifier() + " to " + thread.getLastMessageIdentifier() + " toward "+ " devices");  
            }  
            public void eventConnectionRestarted(NotificationThread thread) {  
                System.out.println("   [EVENT]: connection restarted in thread #" + thread.getThreadNumber() + " because it reached " + thread.getMaxNotificationsPerConnection() + " notifications per connection");  
            }  
            public void eventAllThreadsStarted(NotificationThreads notificationThreads) {  
                System.out.println("   [EVENT]: all threads started: " + notificationThreads.getThreads().size());  
            }  
            public void eventAllThreadsFinished(NotificationThreads notificationThreads) {  
                System.out.println("   [EVENT]: all threads finished: " + notificationThreads.getThreads().size());  
            }  
            public void eventCriticalException(NotificationThread notificationThread, Exception exception) {  
                System.out.println("   [EVENT]: critical exception occurred: " + exception);  
            }  
         };  

    /**
     * 推送一个简单消息
     * @param msg 消息
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushMsgNotification(String msg,Object devices) throws CommunicationException, KeystoreException{
        Push.alert(msg, keystore, password, production, devices);
    }
    /**
     * 推送一个标记
     * @param badge 标记
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushBadgeNotification(int badge,Object devices) throws CommunicationException, KeystoreException{
        Push.badge(badge, keystore, password, production, devices);
    }
    /**
     * 推送一个语音
     * @param sound 语音
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushSoundNotification(String sound,Object devices) throws CommunicationException, KeystoreException{
        Push.sound(sound, keystore, password, production, devices);
    }
    /**
     * 推送一个alert+badge+sound通知
     * @param message 消息
     * @param badge 标记
     * @param sound 声音
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushCombinedNotification(String message,int badge,String sound,Object devices) 
    		throws CommunicationException, KeystoreException{       
    	Push.combined(message, badge, sound, keystore, password, production, devices);
    }
    /**
     * 通知Apple的杂志内容
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void contentAvailable(Object devices) throws CommunicationException, KeystoreException{
        Push.contentAvailable(keystore, password, production, devices);
    }
    /**
     * 推送有用的调试信息
     * @param devices 设备
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void test(Object devices) throws CommunicationException, KeystoreException{
        Push.test(keystore, password, production, devices);
    }

}
