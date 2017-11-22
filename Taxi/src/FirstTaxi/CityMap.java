package FirstTaxi;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

import static FirstTaxi.TaxiTest.gui;
import static FirstTaxi.TaxiTest.MAXORDER;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class CityMap {
    /* Overview: 存储与地图相关的信息，包括地图中边的连接状况，红绿灯状况
     */

    private int[][] oldMap;
    private MapNode[] newMap;
    public MapNode[] originalNewMap;
    private final int MAXNODECOUNT = MAXORDER * MAXORDER;
    private int connectedNodeCount;
    private boolean[] visit;
    public HashMap<String, MyInt> fluxes;
    private final int EDGECOUNT = 12640;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */

        if (oldMap == null || oldMap.length != MAXORDER){
            return false;
        }

        return true;
    }

    public CityMap(int[][] oldMap) {
        /* @ REQUIRES: (oldMap != null) & (oldMap.length == MAXORDER);
        @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性；
        */
        this.oldMap = oldMap;
        this.newMap = new MapNode[MAXNODECOUNT];
        this.originalNewMap = new MapNode[MAXNODECOUNT];
        this.visit = new boolean[MAXNODECOUNT];
        this.fluxes = new HashMap<>();
        this.initializeNewMap();
        this.initializeOriginalNewMap();
        this.initializeVisit();
        this.initializeFluxes();
    }

    public void initializeVisit() {
        /* @ MODIFIES: this.visit;
        @ EFFECTS: 初始化this.visit[i] (0<=i<=this.visit.length);
        */

        for (int i = 0; i < this.visit.length; i++) {
            this.visit[i] = false;
        }
    }

    public void initializeNewMap() {
        /* @ MODIFIES: this.newMap;
        @ EFFECTS: 初始化this.newMap;
        */

        int row, column;
        int newMapIndex = 0;
        int[] tempCoordinate;

        for (row = 0; row < MAXORDER; row++) {
            for (column = 0; column < MAXORDER; column++) {
                this.newMap[newMapIndex] = new MapNode(row, column);
                tempCoordinate = new int[2];
                tempCoordinate[0] = row;
                tempCoordinate[1] = column;
                newMapIndex++;
            }
        }
    }

    public void initializeOriginalNewMap() {
        /* @ MODIFIES: this.originalNewMap;
        @ EFFECTS: 初始化this.originalNewMap;
        */

        int row, column;
        int newMapIndex = 0;
        int[] tempCoordinate;

        for (row = 0; row < MAXORDER; row++) {
            for (column = 0; column < MAXORDER; column++) {
                this.originalNewMap[newMapIndex] = new MapNode(row, column);
                tempCoordinate = new int[2];
                tempCoordinate[0] = row;
                tempCoordinate[1] = column;
                newMapIndex++;
            }
        }
    }

    public void initializeFluxes(){
        /* @ MODIFIES: this.fluxes;
        @ EFFECTS: 初始化this.fluxes;
        */

        MapNode tempNode;
        int downNodeIndex;
        int rightNodeIndex;
        String downNodeString;
        String rightNodeString;

        for (int i = 0; i < MAXNODECOUNT; i++) {
            tempNode = getNodeByIndex(i);
            downNodeIndex = i + 80;
            rightNodeIndex = i + 1;
            downNodeString = i + "," + downNodeIndex;
            rightNodeString = i + "," + rightNodeIndex;
            if (tempNode.getCoordinate()[0] < MAXORDER - 1
                    && tempNode.getCoordinate()[1] < MAXORDER - 1){ //此点不处于边界
                fluxes.put(downNodeString, new MyInt(0));
                fluxes.put(rightNodeString, new MyInt(0));
            }else if (tempNode.getCoordinate()[0] == MAXORDER - 1
                    && tempNode.getCoordinate()[1] < MAXORDER - 1) { //此点处于下边界,不包含最后点
                fluxes.put(rightNodeString, new MyInt(0));
            }else if (tempNode.getCoordinate()[0] < MAXORDER - 1
                    && tempNode.getCoordinate()[1] == MAXORDER - 1) { //此点处于右边界,不包含最后点
                fluxes.put(downNodeString, new MyInt(0));
            }else{ //此点为最后点
                //Do nothing
            }
        }
    }

//获取新旧图
    public synchronized HashMap<String, MyInt> getFluxes(){
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.fluxes;
        */

        return this.fluxes;
    }

    public int[][] getOldMap() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.oldMap;
        */

        return oldMap;
    }

    public MapNode[] getNewMap() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.newMap;
        */

        return newMap;
    }

    //将旧图转化为邻接表(新图)
    public void convertMap() {
        /* @ MODIFIES: this.newMap, this.originalNewMap;
        @ EFFECTS: 将this.oldMap映射到this.newMap和this.originalNewMap;
        */

        int i, x, y;
        int[] coordinate;

        for (i = 0; i < MAXNODECOUNT; i++) {
            coordinate = this.newMap[i].getCoordinate();
            x = coordinate[0];
            y = coordinate[1];
            this.connectNewMapNode(x, y, i);
            this.connectOriginalNewMapNode(x, y , i);
        }
    }

    public void connectNewMapNode(int x, int y, int nodeIndex) {
        /* @ REQUIRES: (0 <= x <= 79) & (0 <= y <= 79) & (0 <= nodeIndex <= 6400);
        @ MODIFIES: this.newMap;
        @ EFFECTS: 将this.newMap中坐标为(x,y)点与下标为nodeIndex的点相连；
        */

        int rightIndex = this.getIndexByCoordinate(x, y + 1);
        int downIndex = this.getIndexByCoordinate(x + 1, y);

        switch (this.oldMap[x][y]) {
            case 0:
                break;
            case 1:
                this.newMap[nodeIndex].connectNode(rightIndex);
                this.newMap[rightIndex].connectNode(nodeIndex);
                break;
            case 2:
                this.newMap[nodeIndex].connectNode(downIndex);
                this.newMap[downIndex].connectNode(nodeIndex);
                break;
            case 3:
                this.newMap[nodeIndex].connectNode(rightIndex);
                this.newMap[rightIndex].connectNode(nodeIndex);
                this.newMap[nodeIndex].connectNode(downIndex);
                this.newMap[downIndex].connectNode(nodeIndex);
                break;
            default:
                break;
        }
    }

    public void connectOriginalNewMapNode(int x, int y, int nodeIndex) {
        /* @ REQUIRES: (0 <= x <= 79) & (0 <= y <= 79) & (0 <= nodeIndex <= 6400);
        @ MODIFIES: this.originalNewMap;
        @ EFFECTS: 将this.originalNewMap中坐标为(x,y)点与下标为nodeIndex的点相连；
        */

        int rightIndex = this.getIndexByCoordinate(x, y + 1);
        int downIndex = this.getIndexByCoordinate(x + 1, y);

        switch (this.oldMap[x][y]) {
            case 0:
                break;
            case 1:
                this.originalNewMap[nodeIndex].connectNode(rightIndex);
                this.originalNewMap[rightIndex].connectNode(nodeIndex);
                break;
            case 2:
                this.originalNewMap[nodeIndex].connectNode(downIndex);
                this.originalNewMap[downIndex].connectNode(nodeIndex);
                break;
            case 3:
                this.originalNewMap[nodeIndex].connectNode(rightIndex);
                this.originalNewMap[rightIndex].connectNode(nodeIndex);
                this.originalNewMap[nodeIndex].connectNode(downIndex);
                this.originalNewMap[downIndex].connectNode(nodeIndex);
                break;
            default:
                break;
        }
    }


    //检查图的合法性
    public boolean checkValidity() {
        /* @ MODIFIES: None;
        @ EFFECTS: 检查this.newMap与this.oldMap是否合法；
        */

        if (checkOldMap()) {
            if (checkNewMap()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkOldMap() {
        /* @ MODIFIES: None;
        @ EFFECTS: 检查this.oldMap是否合法；
        */

        if (checkSize()) {
            if (checkBorder()) {
                return true;
            }
        }

        return false;
    }

    public boolean checkNewMap() {
        /* @ MODIFIES: None;
        @ EFFECTS: 检查this.newMap是否合法；
        */

        if (this.checkMapConnection()) {
            return true;
        }

        return false;
    }

    public boolean checkSize() {
        /* @ MODIFIES: None;
        @ EFFECTS: 检查this.oldMap大小是否为MAXORDER*MAXORDER;
        */

        if (oldMap.length == MAXORDER) {
            if (oldMap[0].length == MAXORDER) {
                return true;
            }
        }

        System.out.println("Size of oldMap must be 80x80.");
        return false;
    }

    public boolean checkBorder() {
        /* @ MODIFIES: None;
        @ EFFECTS: 检查this.oldMap中是否最后一列为1或3，
                   是否最后一行为2或3；
        */

        int row, column;

        //检查最后一行是否合法
        for (row = MAXORDER - 1, column = 0; column < this.oldMap.length; column++) {
            if (this.oldMap[row][column] == 2 || this.oldMap[row][column] == 3) {
                System.out.println("Can't get out of row border.");
                return false;
            }
        }

        //检查最后一列是否合法
        for (column = MAXORDER - 1, row = 0; row < this.oldMap.length; row++) {
            if (this.oldMap[row][column] == 1 || this.oldMap[row][column] == 3) {
                System.out.println("Can't get out of column border.");
                return false;
            }
        }

        return true;
    }

    public boolean checkMapConnection() {
        /* @ MODIFIES: None;
        @ EFFECTS: 检查this.newMap的连通性；
        */

        this.connectedNodeCount = 0;
        this.checkNodeConnection(0);
        if (this.connectedNodeCount == MAXNODECOUNT) {
            return true;
        }

        return false;
    }

    public void checkNodeConnection(int index) {
        /* @ REQUIRES: 0 <= index < 6400;
        @ MODIFIES: this.visit, this.connectedNodeCount;
        @ EFFECTS: 统计最大连通图的节点数this.connectedNodeCount；
        */

        MapNode mapNode = this.newMap[index];
        this.connectedNodeCount++;
        this.visit[index] = true;

        int tempIndex;
        for (int i = 0; i < mapNode.getDegree(); i++) {
            tempIndex = mapNode.getConnectedNodeIndex(i);
            if (!this.visit[tempIndex]) {
                this.checkNodeConnection(tempIndex);
            }
        }
    }

//设置红绿灯初始状态
    public void initializeTrafficLights(int[][] lightsDistribution){
        /* @ REQUIRES: lightsDistribution != null;
        @ MODIFIES: this.newMap;
        @ EFFECTS: 根据lightsDistribution设置所有存在红绿灯的节点的红绿灯状态lightState；
        */

        Random random = new Random();
        for (int i = 0; i < MAXORDER; i++) {
            for (int j = 0; j < MAXORDER; j++) {
                if (lightsDistribution[i][j] == 1){
                    this.getNodeByLocation(i, j).lightState = random.nextInt(2) + 1;
                    gui.SetLightStatus(new Point(i, j), this.getNodeByLocation(i, j).lightState);
                }
            }
        }
    }

//得到当前图节点的两种方式
    public MapNode getNodeByLocation(int x, int y) {
        /* @ REQUIRES: (0 <= x <= 79) & (0 <= y <= 79);
        @ MODIFIES: None;
        @ EFFECTS: 根据坐标得到this.newMap中相应MapNode对象；
        */

        int newMapIndex;
        MapNode node;
        newMapIndex = getIndexByCoordinate(x, y);
        node = this.newMap[newMapIndex];
        return node;
    }

    public MapNode getNodeByIndex(int index) {
        /* @ REQUIRES: 0 <= index <= 6400;
        @ MODIFIES: None;
        @ EFFECTS: 根据下标得到this.newMap中相应MapNode对象；
        */

        return this.newMap[index];
    }
//得到原图节点的两种方式
    public MapNode getOriginalNodeByLocation(int x, int y) {
        /* @ REQUIRES: (0 <= x <= 79) & (0 <= y <= 79);
        @ MODIFIES: None;
        @ EFFECTS: 根据坐标得到this.newMap中相应MapNode对象；
        */

    int newMapIndex;
    MapNode node;
    newMapIndex = getIndexByCoordinate(x, y);
    node = this.originalNewMap[newMapIndex];
    return node;
}

    public MapNode getOriginalNodeByIndex(int index) {
        /* @ REQUIRES: 0 <= index <= 6400;
        @ MODIFIES: None;
        @ EFFECTS: 根据下标得到this.newMap中相应MapNode对象；
        */

        return this.originalNewMap[index];
    }

//通过坐标获取邻接表对应下标
    public int getIndexByCoordinate(int x, int y) {
        /* @ REQUIRES: (0 <= x <= 79) & (0 <= y <= 79);
        @ MODIFIES: None;
        @ EFFECTS: 根据坐标得到this.newMap的相应下标;
        */

        int index;
        index = x * MAXORDER + y;
        return index;
    }

//切换道路状态
    public void openRoad(int x1, int y1, int x2, int y2) {
        /* @ REQUIRES: (0 <= x1 <= 79) & (0 <= y1 <= 79) & (0 <= x2 <= 79) & (0 <= y2 <= 79);
        @ MODIFIES: this.newMap;
        @ EFFECTS: 将两个坐标之间的道路打开；
        */

        if (checkCoordinateRange(x1, y1) && checkCoordinateRange(x2, y2)) {
            if (checkAdjacency(x1, y1, x2, y2)) {
                int i;
                for (i = 0; i < this.originalNewMap[getIndexByCoordinate(x1, y1)].degree; i++) {
                    if (this.originalNewMap[getIndexByCoordinate(x1, y1)].connectedNodes[i]
                            == getIndexByCoordinate(x2, y2)){
                        break;
                    }
                }

                if (i != this.originalNewMap[getIndexByCoordinate(x1, y1)].degree) { //两点连线在原图中存在
                    MapNode node1 = getNodeByLocation(x1, y1);
                    MapNode node2 = getNodeByLocation(x2, y2);
                    node1.addConnectedNodes(getIndexByCoordinate(x2, y2));
                    node2.addConnectedNodes(getIndexByCoordinate(x1, y1));
                    gui.SetRoadStatus(new Point(x1, y1), new Point(x2, y2), 1);
                    System.out.println("Road from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ") is open.");
                }else{
                    System.out.println("Road doesn't exist at beginning!");
                }
            }
        }
    }

    public void closeRoad(int x1, int y1, int x2, int y2) {
        /* @ REQUIRES: (0 <= x1 <= 79) & (0 <= y1 <= 79) & (0 <= x2 <= 79) & (0 <= y2 <= 79);
        @ MODIFIES: this.newMap;
        @ EFFECTS: 将两个坐标之间的道路关闭；
        */

        if (checkCoordinateRange(x1, y1) && checkCoordinateRange(x2, y2)) {
            if (checkAdjacency(x1, y1, x2, y2)) {
                MapNode node1 = getNodeByLocation(x1, y1);
                MapNode node2 = getNodeByLocation(x2, y2);
                node1.cutConnectedNodes(getIndexByCoordinate(x2, y2));
                node2.cutConnectedNodes(getIndexByCoordinate(x1, y1));
                gui.SetRoadStatus(new Point(x1, y1), new Point(x2, y2), 0);
                System.out.println("Road from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ") is close.");
            }
        }
    }

    public boolean checkAdjacency(int x1, int y1, int x2, int y2) {
        /* @ REQUIRES: (0 <= x1 <= 79) & (0 <= y1 <= 79) & (0 <= x2 <= 79) & (0 <= y2 <= 79)；
        @ MODIFIES: None;
        @ EFFECTS: 检查两个坐标是否邻接；
        */

        if (Math.abs(x1 - x2) == 1 && y1 - y2 == 0) {
            return true;
        } else if (Math.abs(y1 - y2) == 1 && x1 - x2 == 0) {
            return true;
        }

        System.out.println("(" + x1 + "," + y1 + ")" + " is not adjacent of (" + x2 + "," + y2 + ")");
        return false;
    }

    public boolean checkCoordinateRange(int x, int y) {
        /* @ REQUIRES: (0 <= x <= 79) & (0 <= y <= 79);
        @ MODIFIES: None;
        @ EFFECTS: 检查x和y的边界是否在0~(MAXORDER-1)之内；
        */

        if (x >= 0 && x < MAXORDER) {
            if (y >= 0 && y < MAXORDER) {
                return true;
            }
        }

        System.out.println("Coordinate (" + x + "," + y + ") is out of range!");
        return false;
    }
}
