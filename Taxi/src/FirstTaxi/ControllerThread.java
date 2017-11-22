package FirstTaxi;

import java.io.FileWriter;
import java.util.ArrayList;
import static FirstTaxi.TaxiTest.MAXORDER;


/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class ControllerThread extends Thread {
    /* Overview: 总控制器线程，分派请求给ScannerThread
     */

    private Taxi[] taxis;
    private RequestQueue requestQueue;
    private FileWriter fileWriter;
    private CityMap map;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */

        if (taxis == null || requestQueue == null || fileWriter == null || map == null){
            return false;
        }

        return true;
    }

    public ControllerThread(Taxi[] taxis, RequestQueue requestQueue, FileWriter fileWriter, CityMap map) {
        /* @ REQUIRES: (taxis != null) & (requestQueue != null) & (fileWriter != null) & (map != null);
        @ MODIFIES: 各类属性;
        @ EFFECTS: 初始化所有属性；
        */
        this.taxis = taxis;
        this.requestQueue = requestQueue;
        this.fileWriter = fileWriter;
        this.map = map;
    }

    public void run(){
        /* @ MODIFIES: this.requestQueue;
        @ EFFECTS: 控制线程开始工作，周期性从this.requestQueue中读取请求；exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        System.out.println("Controller entered.");

        while (true){
            if (this.requestQueue.getSize() != 0){
                //System.out.println(requestQueue.getSize()); 调试代码
                this.removeSameRequest();

                Request currentRequest;
                //System.out.println(requestQueue.getSize()); //调试代码
                for (int i = 0; i < this.requestQueue.getSize(); i++) { //得到新的请求，开始扫描周围车辆
                    //System.out.println(requestQueue.getRequestAtIndex(i));
                    currentRequest = this.requestQueue.getRequestAtIndex(i);
                    ScanServerThread thread = new ScanServerThread(currentRequest.getLocation(),
                            currentRequest.getDestination(), taxis, currentRequest, fileWriter, map);
                    thread.start();
                }

                //删除请求队列里的请求
                for (int i = 0; i < this.requestQueue.getSize();) {
                    this.requestQueue.remove(0);
                }
                //System.out.println(requestQueue.getSize());
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Controller thread sleep error!");
                System.exit(0);
                //e.printStackTrace();
            }
        }
    }
    
    public void removeSameRequest(){
         /* @ MODIFIES: this.requestQueue;
        @ EFFECTS: 将相同请求从this.requestQueue中去除；
        */

        Request currentRequest;
        ArrayList<Request> tempRequests = new ArrayList<Request>();
        //System.out.println(this.requestQueue.getSize());

        for (int i = 0; i < this.requestQueue.getSize();) {
            if (i == 0){
                tempRequests.add(this.requestQueue.getRequestAtIndex(i));
                i++;
            }else{
                currentRequest = this.requestQueue.getRequestAtIndex(i);
                int j;
                for (j = 0; j < tempRequests.size(); j++) {
                    if (tempRequests.get(j).getDestination()[0] == currentRequest.getDestination()[0] &&
                            tempRequests.get(j).getDestination()[1] == currentRequest.getDestination()[1] &&
                            tempRequests.get(j).getLocation()[0] == currentRequest.getLocation()[0] &&
                            tempRequests.get(j).getLocation()[1] == currentRequest.getLocation()[1]){
                        System.out.println("Same request: " + currentRequest);
                        this.requestQueue.remove(i);
                        break;
                    }
                }

                if (tempRequests.size() == j) {
                    //该请求第一次出现
                    tempRequests.add(currentRequest);
                    i++;
                }
            }
        }

    }
}
