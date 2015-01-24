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
    public static Boolean production = true;	//true��production false: sandbox
    public static final int numberOfThreads = 8;
	
//    public static void main(String[] args) throws Exception { 	
//    	JSONObject jsonObject=new JSONObject();
//    	jsonObject.put("TOKEN", "bbcc4d6a 30fc024f 01ba4759 758c9eae 6f23a844 7309599c e1bfe2ca 1df21e01");   	
//    	jsonObject.put("PAYLOAD", "��������"); 	  	  
//    	for (int i=0;i<10;i++){  
//    		jsonObject.put("ALERT", "���µ���Ϣ"+i);
//    		String pubstr = jsonObject.toString();   
//    		pushMain(pubstr);
//    	}   	
//    }
    
    public static void pushMain(String pushjson) throws Exception{
       	
 	   String keystore = "apns.p12";//֤��·����֤����  
        String password = "123456"; 			// ֤������  
        boolean production = false; // ����trueΪ��ʽ�����ַ��falseΪ�����ߵ�ַ  
        int threadThreads = 10; // �߳���    
        try {  
        	//����alerttext
        	 //��ʼ������������s
	        JSONObject jsonObject=new JSONObject();
	        jsonObject = JSONObject.fromObject(pushjson);

	        String alert = jsonObject.getString("ALERT");
	        String token = jsonObject.getString("TOKEN");
	        String payload = jsonObject.getString("PAYLOAD");
	        
	        token =  token.replace(" ", "");
            // ������Apple����������  
            AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production  );  
		   List<PayloadPerDevice> list = new ArrayList<PayloadPerDevice>();  
		  
		   PushNotificationPayload pushPayload = new PushNotificationPayload();  	       
		   pushPayload.addAlert(alert);  
		   pushPayload.addSound("default");// ����  
//		   pushPayload.addBadge(1);//ͼ��С��Ȧ����ֵ  
		   pushPayload.addCustomDictionary("payload",payload);// ����ֵ�   
		   PayloadPerDevice pay = new PayloadPerDevice(pushPayload,token);// ��Ҫ���͵���Ϣ���ֻ�Ψһ��ʶ��   	       
		   list.add(pay);  	            
 	       
 	       System.out.println("��ʼ������Ϣ:"+alert);  
 	       NotificationThreads work = new NotificationThreads(server,list,threadThreads);//   
 	       
 	       work.setListener(DEBUGGING_PROGRESS_LISTENER);// ���̵߳ļ�����һ��Ҫ�����������  
 	       work.start(); // �����߳�  
 	       work.waitForAllThreads();// �ȴ������߳��������  
 	     
 	    } catch (Exception e) {  
 	       e.printStackTrace();  
 	    } 
    }
 // �̼߳���  
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
     * ����һ������Ϣ
     * @param msg ��Ϣ
     * @param devices �豸
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushMsgNotification(String msg,Object devices) throws CommunicationException, KeystoreException{
        Push.alert(msg, keystore, password, production, devices);
    }
    /**
     * ����һ�����
     * @param badge ���
     * @param devices �豸
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushBadgeNotification(int badge,Object devices) throws CommunicationException, KeystoreException{
        Push.badge(badge, keystore, password, production, devices);
    }
    /**
     * ����һ������
     * @param sound ����
     * @param devices �豸
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushSoundNotification(String sound,Object devices) throws CommunicationException, KeystoreException{
        Push.sound(sound, keystore, password, production, devices);
    }
    /**
     * ����һ��alert+badge+sound֪ͨ
     * @param message ��Ϣ
     * @param badge ���
     * @param sound ����
     * @param devices �豸
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void pushCombinedNotification(String message,int badge,String sound,Object devices) 
    		throws CommunicationException, KeystoreException{       
    	Push.combined(message, badge, sound, keystore, password, production, devices);
    }
    /**
     * ֪ͨApple����־����
     * @param devices �豸
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void contentAvailable(Object devices) throws CommunicationException, KeystoreException{
        Push.contentAvailable(keystore, password, production, devices);
    }
    /**
     * �������õĵ�����Ϣ
     * @param devices �豸
     * @throws CommunicationException
     * @throws KeystoreException
     */
    public static void test(Object devices) throws CommunicationException, KeystoreException{
        Push.test(keystore, password, production, devices);
    }

}
