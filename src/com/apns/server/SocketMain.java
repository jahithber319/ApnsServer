package com.apns.server;

import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.io.PrintWriter;  
import java.net.ServerSocket;  
import java.net.Socket;  
  
/** 
 ***************************************************************  
 * ��Ŀ���ƣ�JavaThread 
 * �������ƣ�JabberServer 
 * ���ڣ�2012-8-23 ����11:36:12 
 * ���ߣ� 
 * ģ�飺 
 * ������ 
 * ��ע�� 
 * ------------------------------------------------------------ 
 * �޸���ʷ 
 * ���               ����              �޸���       �޸�ԭ�� 
 *  
 * �޸ı�ע�� 
 * @version  
 *************************************************************** 
 */  
public class SocketMain {  
    public static int PORT = 8099;  
    public static void main(String[] agrs) {  
        ServerSocket s = null;  
        Socket socket = null;  
        BufferedReader br = null;  
        PrintWriter pw = null;  
        String vflag = "1";
        try {    
        	//�趨����˵Ķ˿ں�  
            s = new ServerSocket(PORT);  
            System.out.println("ServerSocket Start:"+s);  
        	while(true) {
        		if (vflag.equals("0")){
        			break;
        		}	            
	            //�ȴ�����,�˷�����һֱ����,ֱ����������������  
	            socket = s.accept();  
	            System.out.println("Connection accept socket:"+socket);  
	            //���ڽ��տͻ��˷���������  
	            br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));  
	            //���ڷ��ͷ�����Ϣ,���Բ���Ҫװ����ô��io��ʹ�û�����ʱ��������Ҫע�����.flush()����  
	            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);  
	            while(true){  
	                String str = br.readLine();  
	                if (str==null){
	                	break;
	                }
	                if(str.equals("END")){
	                	vflag="0";
	                    break;  
	                }  
	                System.out.println("Client Socket Message:"+str);  
	                Thread.sleep(1);  
	                //��������ҵ��------------------------------------
	                PushRunnable push = new PushRunnable(str);   
	                Thread threadPush = new Thread(push);   	          
	                threadPush.start();    	                
	                //-------end---------------
	                pw.println("Message Received:"+str);  
	                pw.flush();  
	            }
	            br.close();  
                pw.close();  
                socket.close();  

        	}
              
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }finally{  
            System.out.println("Close.....");  
            try {  
                br.close();  
                pw.close();  
                socket.close();  
                s.close();  
                
            } catch (Exception e2) {  
                  
            }  
        }  
    }  
}  

/**  
 * ͨ��ʵ�ֽӿڵĶ��߳�  
 *   
 * @author ciding   
 * @createTime Dec 8, 2011 10:20:28 PM  
 *  
 */  
class PushRunnable  implements Runnable {    
    private String para;    
  
    public PushRunnable(String para) {    
        this.para = para;    
    }    
  
    public void run() {    
    	try {
			IosPushMain.pushMain(this.para);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }    
} 