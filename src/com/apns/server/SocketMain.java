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
 * 项目名称：JavaThread 
 * 程序名称：JabberServer 
 * 日期：2012-8-23 上午11:36:12 
 * 作者： 
 * 模块： 
 * 描述： 
 * 备注： 
 * ------------------------------------------------------------ 
 * 修改历史 
 * 序号               日期              修改人       修改原因 
 *  
 * 修改备注： 
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
        	//设定服务端的端口号  
            s = new ServerSocket(PORT);  
            System.out.println("ServerSocket Start:"+s);  
        	while(true) {
        		if (vflag.equals("0")){
        			break;
        		}	            
	            //等待请求,此方法会一直阻塞,直到获得请求才往下走  
	            socket = s.accept();  
	            System.out.println("Connection accept socket:"+socket);  
	            //用于接收客户端发来的请求  
	            br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));  
	            //用于发送返回信息,可以不需要装饰这么多io流使用缓冲流时发送数据要注意调用.flush()方法  
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
	                //处理推送业务------------------------------------
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
 * 通过实现接口的多线程  
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