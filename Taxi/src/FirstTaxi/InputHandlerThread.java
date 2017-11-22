package FirstTaxi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static FirstTaxi.TaxiTest.MAXORDER;


/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class InputHandlerThread extends Thread {
    /* Overview: 输入控制线程，不断监测新的输入，并将输入分类且检查输入的合法性
     */

    private RequestQueue requestQueue;
    private CityMap map;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */

        if (requestQueue == null || map == null) {
            return false;
        }

        return true;
    }

    public InputHandlerThread(RequestQueue requestQueue, CityMap map) {
        /* @ REQUIRES: (requestQueue != null) & (map != null);
        @ MODIFIES: this;
        @ EFFECTS: 初始化所有属性;
        */

        this.requestQueue = requestQueue;
        this.map = map;
    }

    public void run() {
        /* @ MODIFIES: None;
        @ EFFECTS: 获取输入并执行输入监测操作; exceptional_behavior(Exception) 提示错误并退出程序;
        */

        try {
        System.out.println("Waiting for inputs...");
        this.handleInputAndInsertRequest();

        //scanner.close();
        } catch (Exception e) {
            System.out.println("Input error.");
            System.exit(0);
        }

    }

    public void handleInputAndInsertRequest(){
        /* @ MODIFIES: this.requestQueue, this.map;
        @ EFFECTS: 判断输入类型，
                   若为乘客请求则周期性地将请求放入this.requestQueue；
                   若为开关路请求则执行开关路操作；
                   exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        Scanner scanner = new Scanner(System.in);
        String inputLine;

        Pattern requestPattern = Pattern.compile("\\[CR\\,\\(\\+?\\d+\\,\\+?\\d+\\)\\,\\(\\+?\\d+\\,\\+?\\d+\\)\\]");
        Matcher requestMatcher;
        Pattern mapOperatePattern = Pattern.compile("(OPEN|CLOSE),\\(\\+?\\d+,\\+?\\d+\\),\\(\\+?\\d+,\\+?\\d+\\)");
        Matcher mapOperateMatcher;

        Request tempRequest;
        String[] tempStrings;

        while (true){ //一行行检查输入
            inputLine = scanner.nextLine();
            inputLine = inputLine.replaceAll(" ", "");
            requestMatcher = requestPattern.matcher(inputLine);
            mapOperateMatcher = mapOperatePattern.matcher(inputLine);
            if (requestMatcher.matches()){
                tempRequest = this.changeToRequest(inputLine); //将输入转换成Request
                if (this.checkCoordinates(tempRequest)) { //检查坐标范围
                    this.requestQueue.append(tempRequest); //将新Request加入请求队列
                }
            }else if (mapOperateMatcher.matches()){
                inputLine = inputLine.replace("(", "");
                inputLine = inputLine.replace(")", "");
                tempStrings = inputLine.split(",");
                int x1 = Integer.parseInt(tempStrings[1]);
                int y1 = Integer.parseInt(tempStrings[2]);
                int x2 = Integer.parseInt(tempStrings[3]);
                int y2 = Integer.parseInt(tempStrings[4]);

                if (tempStrings[0].equals("OPEN")){
                    this.map.openRoad(x1, y1, x2, y2);
                }else if (tempStrings[0].equals("CLOSE")){
                    this.map.closeRoad(x1, y1, x2, y2);
                }
            }else{
                System.out.println("Input have mistakes!");
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("InputHandlerThread sleep error.");
                System.exit(0);
                //e.printStackTrace();
            }

            //每隔100ms存入临时队列到requestQueue中,并清空tempRequests
            /*if (System.currentTimeMillis() - lastTimeNode >= 100){
                lastTimeNode = System.currentTimeMillis();
                this.flushRequests(tempRequests);
            }
            */
        }
    }

//    public void flushRequests(ArrayList<Request> requests){
//        if (requests.size() != 0){
//            for (int i = 0; i < requests.size(); i++) {
//                this.requestQueue.append(requests.get(i));
//            }
//        }
//
//        while (requests.size() != 0){
//            requests.remove(0);
//        }
//    }

    public boolean checkCoordinates(Request request){
        /* @ REQUIRES: reqeust != null;
        @ MODIFIES: None;
        @ EFFECTS: 检查乘客请求的坐标是否超过地图边界；
        */

        if (request.getLocation()[0] < 0 || request.getLocation()[0] >= MAXORDER ||
                request.getLocation()[1] < 0 || request.getLocation()[1] >= MAXORDER ){
            System.out.println("Request location out of range!");
            return false;
        }

        if (request.getDestination()[0] < 0 || request.getDestination()[0] >= MAXORDER ||
                request.getDestination()[1] < 0 || request.getDestination()[1] >= MAXORDER){
            System.out.println("Destination out of range!");
            return false;
        }

        if (request.getDestination()[0] == request.getLocation()[0] &&
                request.getDestination()[1] == request.getLocation()[1]){
            System.out.println("Your destination is at your position. Please walk there.");
            return false;
        }

        return true;
    }

    public Request changeToRequest(String inputLine){
        /* @ REQUIRES: inputLine != null;
        @ MODIFIES: inputLine;
        @ EFFECTS: 提取inputLine中的信息，得到相应的请求对象
        @ */

        String[] tempStringList;
        Request request = new Request();

        inputLine = inputLine.replace("[", "");
        inputLine = inputLine.replace("(", "");
        inputLine = inputLine.replace(")", "");
        inputLine = inputLine.replace("]", "");
        tempStringList = inputLine.split(",");

        request.setLocation(Integer.parseInt(tempStringList[1]), Integer.parseInt(tempStringList[2]));
        request.setDestination(Integer.parseInt(tempStringList[3]), Integer.parseInt(tempStringList[4]));
        request.setTime(System.currentTimeMillis());

        return request;
    }

}
