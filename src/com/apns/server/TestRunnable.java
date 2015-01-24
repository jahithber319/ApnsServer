package com.apns.server;
 
  
/**  
 * 测试Runnable类实现的多线程程序   
 *   
 * @author ciding   
 * @createTime Dec 7, 2011 9:38:52 AM  
 *  
 */  
public class TestRunnable {    
    public static void main(String[] args) {    
        StrRunnable sr1 = new StrRunnable("chenfengbin");   
        StrRunnable sr2 = new StrRunnable("cidng");   

        Thread t1 = new Thread(sr1);   
        Thread t2 = new Thread(sr2);   
  
        t1.start();    
        t2.start();    
           
        StrThread st1 = new StrThread("chenfengbin");   
        StrThread st2 = new StrThread("cidng");   
  
        Thread t3 = new Thread(st1);   
        Thread t4 = new Thread(st2);   
  
        t3.start();    
        t4.start();    
    }   
}   
  
/**  
 * 通过实现接口的多线程  
 *   
 * @author ciding   
 * @createTime Dec 8, 2011 10:20:28 PM  
 *  
 */  
class StrRunnable  implements Runnable {    
    private String name;    
  
    public StrRunnable(String name) {    
        this.name = name;    
    }    
  
    public void run() {    
        for (int i = 0; i < 3; i++) {    
            for(long j=0;j<100000000;j++);   //用来模拟一个非常耗时的操作，方便显示多线程效果。   
            System.out.println("Runnable下的" + name + ": " + i);   
        }    
    }    
}   
  
/**  
 * 通过继承类的多线程  
 *   
 * @author ciding   
 * @createTime Dec 8, 2011 10:20:50 PM  
 *  
 */  
class StrThread extends Thread{   
    private String name;   
       
    public StrThread(String name){   
        this.name = name;   
    }   
       
    public void run() {    
        for (int i = 0; i < 3; i++) {    
            for(long j=0;j<100000000;j++);   //用来模拟一个非常耗时的操作，方便显示多线程效果。   
            System.out.println("Thread下的" + name + ": " + i);   
        }    
    }   
}  