package FirstTaxi;

import java.awt.*;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import static FirstTaxi.TaxiTest.gui;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class TraceableTaxi extends Taxi{
    /* Overview: 可追踪出租车类，继承出租车类的属性与行为，为部分行为增加了添加记录操作；
        拥有迭代器生成器
     */

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */

        return true;
    }

    public ArrayList<ServiceRecord> serviceRecords;
    public int recordIndex;

    public TraceableTaxi(int direction, int x, int y, int taxiNo, CityMap map, FileWriter fileWriter) {
        /* @ REQUIRES: 0 <= x <= MAXORDER & 0 <= y <= MAXORDER & map != null & fileWriter != null;
        @ MODIFIES: this;
        @ EFFECTS: 初始化各类属性；
        */

        super(direction, x, y, taxiNo, map, fileWriter);
        this.type = 1;
        this.serviceRecords = new ArrayList<>();
        this.recordIndex = -1;
    }

//获取迭代器
    public ServiceRecordIterator generateSRIterator() {
        /* @ MODIFIES: None;
        @ EFFECTS: 获取迭代器对象；
        */

        return new ServiceRecordIterator(this);
    }

//静态迭代器类
    public static class ServiceRecordIterator implements Iterator {
        /* Overview: 服务记录迭代器类，静态类，可对指定出租车对象的服务记录进行迭代操作
        */

        private TraceableTaxi taxi;
        private int index;

        public boolean repOK() {
            /* @ Effects: \result==invariant(this).
            */
            if (taxi == null){
                return false;
            }

            return true;
        }

        public ServiceRecordIterator(TraceableTaxi taxi) {
        /* @ REQUIRES: taxi != null;
        @ MODIFIES: this;
        @ EFFECTS: 初始化所有属性;
        */
            this.taxi = taxi;
            this.index = -1;
        }

        public boolean hasNext(){
            /* @ MODIFIES: None;
            @ EFFECTS: 判断迭代器所处集合位置的下一个元素是否存在;
            */

            return this.index < (this.taxi.serviceRecords.size() - 1);
        }

        public ServiceRecord next(){
            /* @ MODIFIES: None;
            @ EFFECTS: 获得迭代器所处集合位置的下一个元素并返回;
            */

            if (hasNext()) {
                this.index++;
                return this.taxi.serviceRecords.get(this.index);
            }else{
                System.out.println("Can't iterator forward any more!");
            }

            return null;
        }

        public boolean hasPrevious(){
            /* @ MODIFIES: None;
            @ EFFECTS: 判断迭代器所处集合位置的上一个元素是否存在;
            */

            return this.index > 0;
        }

        public ServiceRecord previous(){
            /* @ MODIFIES: None;
            @ EFFECTS: 获得迭代器所处集合位置的上一个元素并返回;
            */

            if (hasPrevious()) {
                this.index--;
                return this.taxi.serviceRecords.get(this.index);
            }else{
                System.out.println("Can't iterator backward any more!");
            }

            return null;
        }
    }

//接到单并获取乘客的位置与目的地位置，并记录
    public synchronized boolean achieveOrder(Request request){
            /* @ REQUIRES: request != null;
            @ MODIFIES: this;
            @ EFFECTS: 存储请求所在位置与目的地到this.clientLocation和this.destination，并在this.serviceRecords中添加新记录;
            @ THREAD_REQUIRES: None;
            @ THREAD_EFFECTS: \locked(this.pathList);
            */

        if (this.state == 0) {
            this.state = 2;
            this.clientLocation = request.getLocation();
            this.destination = request.getDestination();

            //将新请求记录下来
            this.recordIndex++;
            int locationX = this.location[0];
            int locationY = this.location[1];
            ServiceRecord record = new ServiceRecord(request, locationX, locationY);
            this.serviceRecords.add(record);

            return true;
        }
        return false;
    }

//开始接送客,并记录
    public void pickUpClient(){
        /* @ MODIFIES: this;
        @ EFFECTS: 出租车按照最短路径去接乘客，并将路径添加到新记录中；
        */

        this.state = 2;
        this.wanderDistance = 0; //接到客则闲逛次数清零
        gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0],this.getLocation()[1]), 3);
        this.content = this.content + "\n" + "Taxi " + this.taxiNo + " picking up customer path:" + "\n"; //输出标题
        this.content = this.content + Arrays.toString(this.location) + "\n"; //输出起步位置
        this.updatePickUpPath(); //记录起步位置

        int clientMapIndex = this.map.getIndexByCoordinate(this.clientLocation[0], this.clientLocation[1]);
        int taxiMapIndex;
        //MapNode tempNode;
        LinkedList<MyInt> path;
        int loopCount = 0;

        while (clientMapIndex != this.map.getIndexByCoordinate(this.location[0], this.location[1])) {
            //确定当前接客的最短路径并根据最短路径移动一格
            taxiMapIndex = this.map.getIndexByCoordinate(this.location[0], this.location[1]);
            path = this.graphSearcher.findPath(taxiMapIndex, clientMapIndex);
            this.moveOneStepByPath(path);

            //记录并输出移动一格后的当前位置
            //tempNode = this.map.getNodeByIndex(path.get(1).getValue());
            this.updatePickUpPath();
            this.content = this.content + Arrays.toString(this.location) + "\n";

            loopCount++;
        }

        this.getOnAndOff(); //接到客人，客人上车
        this.transportClient();
    }

    public void transportClient() {
        /* @ MODIFIES: this.state, this.content, gui;
        @ EFFECTS: 接到乘客后，出租车按照最短路径送乘客到目的地，并将路径添加到新记录中；
        */

        this.state = 1;
        gui.SetTaxiStatus(this.getTaxiNo(), new Point(this.getLocation()[0], this.getLocation()[1]), 1);
        this.content = this.content + "Taxi " + this.taxiNo + " transporting customer path:" + "\n"; //输出标题
        this.content = this.content + Arrays.toString(this.location) + "\n"; //输出起步位置
        this.updateTransportPath(); //记录起步位置

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
            this.updateTransportPath();
            this.content = this.content + Arrays.toString(this.location) + "\n";
        }

        this.flushPath();
        this.getOnAndOff(); //客人下车
        this.increaseCredit(SERVECREDIT);
    }

//辅助方法
    public void checkAvailableDirection(boolean[] availableDirection){
        /* @ REQUIRES: availableDirection != null && availableDirection.length == 4
        @ MODIFIES: availableDirection;
        @ EFFECTS: 检查汽车所在点可以前往的方向(以初始图为准)，并设置对应方向的availableDirection[i]为true,
                   其中 i = 0 为方向向上，
                   i = 1 为向右,
                   i = 2 为向下,
                   i = 3 为向左;
        */

    MapNode node = this.map.getOriginalNodeByLocation(location[0], location[1]);
    MapNode nextNode;

    for (int i = 0; i < node.getDegree(); i++) {
        nextNode = this.map.getOriginalNodeByIndex(node.getConnectedNodeIndex(i));
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

    public void updatePickUpPath(){
        /* @ MODIFIES: this, gui;
        @ EFFECTS: 将新路过的点添加到新记录的接客路径当中去；
        */

        MyInt tempLocation = new MyInt(this.map.getIndexByCoordinate(this.location[0], this.location[1]));
        this.serviceRecords.get(this.recordIndex).pickUpPath.add(tempLocation);
    }

    public void updateTransportPath(){
        /* @ MODIFIES: this, gui;
        @ EFFECTS: 将新路过的点添加到新记录的送客路径当中去；
        */

        MyInt tempLocation = new MyInt(this.map.getIndexByCoordinate(this.location[0], this.location[1]));
        this.serviceRecords.get(this.recordIndex).transportPath.add(tempLocation);
    }
}

class ServiceRecord{
    /* Overview: 服务记录类，以一次完整服务为单位存储了此次服务的信息
    */

    public Request request;
    public int[] startLocation;
    public LinkedList<MyInt> pickUpPath;
    public LinkedList<MyInt> transportPath;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */

        return true;
    }

    public ServiceRecord(Request request, int startX, int startY) {
        /* @ REQUIRES: request != null & 0 <= startX <= MAXORDER & 0 <= startY <= MAXORDER;
        @ MODIFIES: this;
        @ EFFECTS: 初始化各类属性；
        */

        this.request = request;
        this.startLocation = new int[2];
        this.startLocation[0] = startX;
        this.startLocation[1] = startY;
        this.pickUpPath = new LinkedList<>();
        this.transportPath = new LinkedList<>();
    }
}