package FirstTaxi;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


import static FirstTaxi.TaxiTest.gui;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class Taxi implements CarInterface {
    /* Overview: 出租车类，存储出租车的状态，并能让出租车执行接客、闲逛、运行、休息指令
     */

    public int type; //0是普通车，1是可追踪车
    protected int state; //0是到处闲逛，1是服务中，2是接单中，3是停止
    protected int credit;
    protected int direction; //下一步的移动方向，上帝视角，0是上，1是右，2是下，3是左
    public int orientation; //出租车的当前朝向，上帝视角，0是上，1是右，2是下，3是左
    protected int[] location;
    protected int taxiNo;
    protected CityMap map;
    protected int wanderDistance; //闲逛距离，接到单或跑100次就清零
    protected int[] clientLocation;
    protected int[] destination;
    protected final int SERVECREDIT = 3;
    protected final int ORDERCREDIT = 1;
    protected GraphSearch graphSearcher;
    protected FileWriter fileWriter;
    protected String content;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    public Taxi(int direction, int x, int y, int taxiNo, CityMap map, FileWriter fileWriter) {
        /* @ REQUIRES: 0 <= x <= MAXORDER & 0 <= y <= MAXORDER
        @ MODIFIES: this.state, this.credit, this.direction, this.location, this.taxiNo,
        this.map, this.wanderDistance, this.graphSeacher, this.fileWriter, this.content;
        @ EFFECTS: 初始化各类属性；
        */

        Random random = new Random();
        this.type = 0;
        this.state = 0;
        this.credit = 0;
        this.direction = direction;
        this.orientation = random.nextInt(4);
        this.location = new int[2];
        this.location[0] = x;
        this.location[1] = y;
        this.taxiNo = taxiNo;
        this.map = map;
        this.wanderDistance = 0;
        this.graphSearcher = new GraphSearch(map.getNewMap());
        this.fileWriter = fileWriter;
        this.content = "";
    }

//重要属性的getter
    public int getState() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.state;
        */

        return state;
    }

    public int[] getLocation() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.location;
        */

        return location;
    }

    public int getTaxiNo() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.taxiNo;
        */

        return taxiNo;
    }

    public int getCredit() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.credit;
        */

        return credit;
    }

//闲逛
    public void wander(){
        /* @ MODIFIES: this.location, this.wanderDistance, this.direction;
        @ EFFECTS: 根据流量选择当前所在点可走的路，并朝着这条路的方向移动一个单位；
        */

        gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0],this.getLocation()[1]), 2);
        boolean[] availableDirection = {false, false, false, false}; //上帝视角，0是上，1是右，2是下，3是左
        this.checkAvailableDirection(availableDirection);

        Random random = new Random();
        ArrayList<MyInt> tempDirection = new ArrayList<MyInt>();
        //将可走的方向存入tempDirection
        for (int i = 0; i < availableDirection.length; i++) {
            if (availableDirection[i]){
                tempDirection.add(new MyInt(i));
            }
        }

        tempDirection = this.findLeastFluxDirections(tempDirection);
        int index = random.nextInt(tempDirection.size());
        this.direction = tempDirection.get(index).getValue();
        this.move();
        this.wanderDistance++;
        if (this.wanderDistance == 100) { //闲逛100次休息1秒并清零闲逛次数
            this.restForOneSecond();
            this.wanderDistance = 0;
        }
    }

    public void checkAvailableDirection(boolean[] availableDirection){
        /* @ REQUIRES: availableDirection != null && availableDirection.length == 4
        @ MODIFIES: availableDirection;
        @ EFFECTS: 检查汽车所在点可以前往的方向（以当前图为准），并设置对应方向的availableDirection[i]为true,
                   其中 i = 0 为方向向上，
                   i = 1 为向右,
                   i = 2 为向下,
                   i = 3 为向左;
        */

        MapNode node = this.map.getNodeByLocation(location[0], location[1]);
        MapNode nextNode;

        for (int i = 0; i < node.getDegree(); i++) {
            nextNode = this.map.getNodeByIndex(node.getConnectedNodeIndex(i));
            if (nextNode.getCoordinate()[0] == location[0] - 1){
                availableDirection[0] = true; //可以向上
            }else if (nextNode.getCoordinate()[0] == location[0] + 1){
                availableDirection[2] = true; //可以向下
            }else if (nextNode.getCoordinate()[1] == location[1] - 1){
                availableDirection[3] = true; //可以向左
            }else if (nextNode.getCoordinate()[1] == location[1] + 1){
                availableDirection[1] = true; //可以向右
            }
        }
    }

    public ArrayList<MyInt> findLeastFluxDirections(ArrayList<MyInt> availableDirection){
        /* @ REQUIRES: availableDirection != null;
        @ MODIFIES: this;
        @ EFFECTS: 寻找所在点邻接最小流量边所对应的方向；
        */
        int leastFlux = 1000;
        ArrayList<MyInt> leastFluxDirections = new ArrayList<>();
        MapNode thisNode = this.map.getNodeByLocation(this.location[0], this.location[1]);
        MapNode nextNode = new MapNode(-1, -1);
        int currentFlux = 0;

        for (int i = 0; i < availableDirection.size(); i++) {
            switch (availableDirection.get(i).value){
                case 0: //向上
                    nextNode = new MapNode(this.location[0] - 1, this.location[1]);
                    currentFlux = this.map.getFluxes().get(this.map.getIndexByCoordinate(nextNode.coordinate[0], nextNode.coordinate[1])
                            + "," + this.map.getIndexByCoordinate(this.location[0], this.location[1])).getValue();
                    break;
                case 1: //向右
                    nextNode = new MapNode(this.location[0], this.location[1] + 1);
                    currentFlux = this.map.getFluxes().get(this.map.getIndexByCoordinate(this.location[0], this.location[1])
                            + "," + this.map.getIndexByCoordinate(nextNode.coordinate[0], nextNode.coordinate[1])).getValue();
                    break;
                case 2: //向下
                    nextNode = new MapNode(this.location[0] + 1, this.location[1]);
                    currentFlux = this.map.getFluxes().get(this.map.getIndexByCoordinate(this.location[0], this.location[1])
                            + "," + this.map.getIndexByCoordinate(nextNode.coordinate[0], nextNode.coordinate[1])).getValue();
                    break;
                case 3: //向左
                    nextNode = new MapNode(this.location[0], this.location[1] - 1);
                    currentFlux = this.map.getFluxes().get(this.map.getIndexByCoordinate(nextNode.coordinate[0], nextNode.coordinate[1])
                            + "," + this.map.getIndexByCoordinate(this.location[0], this.location[1])).getValue();
                    break;
            }


            //将流量最小的方向存入leastFluxDirections
            if (currentFlux <= leastFlux){
                if (currentFlux == leastFlux){
                    leastFluxDirections.add(availableDirection.get(i));
                }else{
                    if (leastFluxDirections.size() != 0){
                        leastFluxDirections.clear();
                    }
                    leastFluxDirections.add(availableDirection.get(i));
                }
                leastFlux = currentFlux;
            }
        }

        return leastFluxDirections;
    }

    public void restForOneSecond(){
        /* @ MODIFIES: this.state;
        @ EFFECTS: 让出租车休息1秒钟，先将this.state设置为3，再设置为0；exceptional_behavior(InterruptedException) 提示错误;
        */

        this.state = 3;
        gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0],this.getLocation()[1]), 0);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Wander sleep error.");
            //e.printStackTrace();
        }
        this.state = 0;
    }
//抢单
    public void scrambleOrder(){
        /* @ MODIFIES: this.credit;
        @ EFFECTS: 出租车抢到单，增加1信用；
        */

        this.increaseCredit(ORDERCREDIT);
    }

//接到单并获取乘客的位置与目的地位置
    public synchronized boolean achieveOrder(Request request){
        /* @ REQUIRES: request != null;
        @ MODIFIES: this;
        @ EFFECTS: 存储请求所在位置与目的地到this.clientLocation和this.destination;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked();
        */

        if (this.state == 0) {
            this.state = 2;
            this.clientLocation = request.getLocation();
            this.destination = request.getDestination();
            return true;
        }
        return false;
    }

//开始接送客
    public void pickUpClient(){
        /* @ MODIFIES: this.state, this.wanderDistance, this.content. this.location, gui;
        @ EFFECTS: 出租车按照最短路径去接乘客；
        */

        this.state = 2;
        this.wanderDistance = 0; //接到客则闲逛次数清零
        gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0],this.getLocation()[1]), 3);
        this.content = this.content + "\n" + "Taxi " + this.taxiNo + " picking up customer path:" + "\n"; //输出标题
        this.content = this.content + Arrays.toString(this.location) + "\n"; //输出起步位置

        int clientMapIndex = this.map.getIndexByCoordinate(this.clientLocation[0], this.clientLocation[1]);
        int taxiMapIndex;
        //MapNode tempNode;
        LinkedList<MyInt> path;

        while (clientMapIndex != this.map.getIndexByCoordinate(this.location[0], this.location[1])) {
            //确定当前接客的最短路径并根据最短路径移动一格
            taxiMapIndex = this.map.getIndexByCoordinate(this.location[0], this.location[1]);
            path = this.graphSearcher.findPath(taxiMapIndex, clientMapIndex);
            this.moveOneStepByPath(path);

            //输出移动一格后的当前位置
            //tempNode = this.map.getNodeByIndex(path.get(1).getValue());
            this.content = this.content + Arrays.toString(this.location) + "\n";
        }

        this.getOnAndOff(); //接到客人，客人上车
        this.transportClient();
    }

    public void transportClient() {
        /* @ MODIFIES: this.state, this.content, gui;
        @ EFFECTS: 接到乘客后，出租车按照最短路径送乘客到目的地；
        */

        this.state = 1;
        gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0], this.getLocation()[1]), 1);
        this.content = this.content + "Taxi " + this.taxiNo + " transporting customer path:" + "\n"; //输出标题
        this.content = this.content + Arrays.toString(this.location) + "\n"; //输出起步位置

        int destinationMapIndex = this.map.getIndexByCoordinate(this.destination[0], this.destination[1]);
        int taxiMapIndex;
        //MapNode tempNode;
        LinkedList<MyInt> path;

        while (this.map.getIndexByCoordinate(this.location[0], this.location[1]) != destinationMapIndex){
            //确定当前送客的最短路径并根据最短路径移动一格
            taxiMapIndex = this.map.getIndexByCoordinate(this.location[0], this.location[1]);
            path = this.graphSearcher.findPath(taxiMapIndex, destinationMapIndex);
            this.moveOneStepByPath(path);

            //输出移动一格后的当前位置
            //tempNode = this.map.getNodeByIndex(this.location);
            this.content = this.content + Arrays.toString(this.location) + "\n";
        }

        this.flushPath();
        this.getOnAndOff(); //客人下车
        this.increaseCredit(SERVECREDIT);
    }

    public void getOnAndOff(){
        /* @ MODIFIES: this.state, gui;
        @ EFFECTS: 出租车上下客，出租车状态改变到停止，再改变到下一个状态；exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        int lastState = this.state;
        this.state = 3;
        gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0],this.getLocation()[1]), 0);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Get on / off sleep error.");
            System.exit(0);
            //e.printStackTrace();
        }

        switch (lastState){
            case 2: //之前接单中
                this.state = 1;
                break;
            case 1: //之前服务中
                this.state = 0;
                break;
            default:
                break;
        }
    }

//辅助方法
    public void increaseCredit(int amount){
        /* @REQUIRES: amount >= 0;
        @ MODIFIES: this.credit;
        @ EFFECTS: this.credit += amount;
        */

        this.credit += amount;
    }

    @Override
    public void move(){
        /*@ MODIFIES: this;
        @ EFFECTS: 出租车朝着this.direction移动一格，并更新流量；exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println("TaxiThread move sleep error!");
            System.exit(0);
            //e.printStackTrace();
        }

        String edgeFluxString;
        int thisIndex = this.map.getIndexByCoordinate(this.location[0], this.location[1]);
        int upIndex;
        int rightIndex;
        int downIndex;
        int leftIndex;

        //穿过十字路口
        this.crossRoad();

        //更新流量并移动位置
        switch (this.direction){
            case 0:
                upIndex = this.map.getIndexByCoordinate(this.location[0] - 1, this.location[1]);
                edgeFluxString = upIndex + "," + thisIndex;
                this.map.getFluxes().get(edgeFluxString).value++;
                this.location[0]--;
                break;
            case 1:
                rightIndex = this.map.getIndexByCoordinate(this.location[0], this.location[1] + 1);
                edgeFluxString = thisIndex + "," + rightIndex;
                this.map.getFluxes().get(edgeFluxString).value++;
                this.location[1]++;
                break;
            case 2:
                downIndex = this.map.getIndexByCoordinate(this.location[0] + 1, this.location[1]);
                edgeFluxString = thisIndex + "," + downIndex;
                this.map.getFluxes().get(edgeFluxString).value++;
                this.location[0]++;
                break;
            case 3:
                leftIndex = this.map.getIndexByCoordinate(this.location[0], this.location[1] - 1);
                edgeFluxString = leftIndex + "," + thisIndex;
                this.map.getFluxes().get(edgeFluxString).value++;
                this.location[1]--;
                break;
        }
    }

    public void crossRoad(){
        /*@ MODIFIES: this.orientation;
        @ EFFECTS: 根据出租车运动方向与红绿灯状态选择是否等待红绿灯；
        */

        switch (orientation){
            case 0:
                if (direction == 1){ //右拐
                }
                if (direction == 2){ //掉头
                }
                if (direction == 0){ //直行
                    if (map.getNodeByLocation(location[0], location[1]).lightState == 1){
                        this.waitForLight();
                    }
                }
                if (direction == 3){ //左拐
                    if (map.getNodeByLocation(location[0], location[1]).lightState == 2){
                        this.waitForLight();
                    }
                }
                break;
            case 1:
                if (direction == 2){ //右拐
                }
                if (direction == 3){ //掉头
                }
                if (direction == 1){ //直行
                    if (map.getNodeByLocation(location[0], location[1]).lightState == 2){
                        this.waitForLight();
                    }
                }
                if (direction == 0){ //左拐
                    if (map.getNodeByLocation(location[0], location[1]).lightState == 1){
                        this.waitForLight();
                    }
                }
                break;
            case 2:
                if (direction == 3){ //右拐
                }
                if (direction == 0){ //掉头
                }
                if (direction == 2){ //直行
                    if (map.getNodeByLocation(location[0], location[1]).lightState == 1){
                        this.waitForLight();
                    }
                }
                if (direction == 1){ //左拐
                    if (map.getNodeByLocation(location[0], location[1]).lightState == 2){
                        this.waitForLight();
                    }
                }
                break;
            case 3:
                if (direction == 0){ //右拐
                }
                if (direction == 1){ //掉头
                }
                if (direction == 3){ //直行
                    if (map.getNodeByLocation(location[0], location[1]).lightState == 2){
                        this.waitForLight();
                    }
                }
                if (direction == 2){ //左拐
                    if (map.getNodeByLocation(location[0], location[1]).lightState == 1){
                        this.waitForLight();
                    }
                }
                break;
        }

        this.orientation = this.direction;
    }

    public void waitForLight(){
        /*@ MODIFIES: None;
        @ EFFECTS: 出租车等待红绿灯；exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        try {
            Thread.sleep(map.getNodeByLocation(location[0], location[1]).lightWaitingTime);
        } catch (InterruptedException e) {
            System.out.println("Waiting for light sleep error.");
            System.exit(0);
            //e.printStackTrace();
        }
    }

    public void moveByPath(LinkedList<MyInt> path){
        /* @ REQUIRES: path != null;
        @ MODIFIES: gui, this.direction, this.location;
        @ EFFECTS: 按照path路径进行移动；
        */

        int pathIndex;
        int currentX;
        int currentY;
        int nextX;
        int nextY;
        int guiStatus;
        int i;

        for (i = 1; i < path.size(); i++) {
            if (this.state == 1){ //出租车服务中
                guiStatus = 1;
            }else{ //出租车接单中
                guiStatus = 3;
            }
            gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0],this.getLocation()[1]), guiStatus);

            pathIndex = path.get(i).getValue();
            currentX = this.location[0];
            currentY = this.location[1];
            nextX = this.map.getNodeByIndex(pathIndex).getCoordinate()[0];
            nextY = this.map.getNodeByIndex(pathIndex).getCoordinate()[1];

            //确定运动方向
            if (nextX == currentX + 1){
                this.direction = 2;
            }else if (nextX == currentX - 1){
                this.direction = 0;
            }else if (nextY == currentY + 1){
                this.direction = 1;
            }else if (nextY == currentY - 1){
                this.direction = 3;
            }

            //运动一格
            this.move();
        }
        //System.out.println(this.location[0] + "," + this.location[1] + ",," + this.map.getNodeByIndex(path.get(i-1).getValue()));
    }

    public void moveOneStepByPath(LinkedList<MyInt> path){
        /* @ REQUIRES: path != null
        @ MODIFIES: gui, this.direction, this.location;
        @ EFFECTS: 按照path路径移动一格；
        */

        int pathIndex;
        int currentX;
        int currentY;
        int nextX;
        int nextY;
        int guiStatus;

        pathIndex = path.get(1).getValue();
        currentX = this.location[0];
        currentY = this.location[1];
        nextX = this.map.getNodeByIndex(pathIndex).getCoordinate()[0];
        nextY = this.map.getNodeByIndex(pathIndex).getCoordinate()[1];

        //确定运动方向
        if (nextX == currentX + 1){
            this.direction = 2;
        }else if (nextX == currentX - 1){
            this.direction = 0;
        }else if (nextY == currentY + 1){
            this.direction = 1;
        }else if (nextY == currentY - 1){
            this.direction = 3;
        }

        //运动一格
        this.move();

        if (this.state == 1){ //出租车服务中
            guiStatus = 1;
        }else{ //出租车接单中
            guiStatus = 3;
        }
        gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0],this.getLocation()[1]), guiStatus);
        //System.out.println(this.location[0] + "," + this.location[1]);
    }

    public void flushPath(){
        /*@ MODIFIES: None;
        @ EFFECTS: 将path信息写入Path.txt文件；exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        try {
            this.fileWriter.write(this.content);
            this.fileWriter.flush();
        } catch (IOException e) {
            System.out.println("Taxi path write error.");
            System.exit(0);
            //e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        /*@ MODIFIES: None;
        @ EFFECTS: 返回表示出租车信息的字符串；
        */

        String stateString = null;

        switch (state){
            case 0:
                stateString = "wandering";
                break;
            case 1:
                stateString = "serving";
                break;
            case 2:
                stateString = "picking up customer";
                break;
            case 3:
                stateString = "stop";
                break;
            default:
                break;
        }

        return "Taxi No." + taxiNo + " {" +
                "state = " + stateString +
                ", credit = " + credit +
                ", location = " + Arrays.toString(location) +
                '}';
    }
}
