package FirstTaxi;

import java.util.Arrays;
import static FirstTaxi.TaxiTest.MAXORDER;


/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class MapNode {
    /* Overview: 地图节点类，存储信息包括节点的度、坐标、相连节点的坐标信息、信号灯状态、信号灯切换时间
     */

    public int degree;
    public int[] connectedNodes;
    public int[] coordinate;
    public int lightState; //信号灯状态，0无信号灯,1东西为绿，2南北为绿
    public int lightWaitingTime;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    public MapNode(int x, int y) {
        /* @ REQUIRES: (0 <= x < MAXORDER) & (0 <= y < MAXORDER);
        @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性;
        */

        this.degree = 0;
        this.connectedNodes = new int[4];
        this.initializeConnectedNodes();
        this.coordinate = new int[2];
        this.coordinate[0] = x;
        this.coordinate[1] = y;
        this.lightState = 0;
    }

    public void initializeConnectedNodes(){
        /* @ MODIFIES: None;
        @ EFFECTS: 初始化this.connectedNodes;
        */

        int i;
        for (i = 0; i < connectedNodes.length; i++){
            this.connectedNodes[i] = 0;
        }
    }

    public void connectNode(int nodeIndex){
        /* @ REQUIRES: 0 <= nodeIndex <= 79;
        @ MODIFIES: this.connectedNodes;
        @ EFFECTS: 将nodeIndex存入this.connectedNodes;
        */

        if (this.degree == 4){
            System.out.println("Degree of " + "(" + coordinate[0] + "," + coordinate[1] + ") already 4 degrees!");
            return;
        }

        this.connectedNodes[degree] = nodeIndex;
        this.degree++;
    }

//Getters
    public int getDegree() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.degree;
        */

        return degree;
    }

    public int[] getCoordinate() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.coordinate;
        */

        return coordinate;
    }

    public int getConnectedNodeIndex(int i){
        /* @ REQUIRES: 0 <= i <= 3;
        @ MODIFIES: None;
        @ EFFECTS: 返回this.connectedNodes[i];
        */

        return this.connectedNodes[i];
    }

//Neighbor settings
    public synchronized void addConnectedNodes(int connectedNode) {
        /* @ REQUIRES: 0 <= connectedNode < 6400;
        @ MODIFIES: this.degree, this.connectedNodes;
        @ EFFECTS: 将connectedNode加入this.connectedNodes并增加this.degree;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.connectedNodes[]);
        */

        if (degree != 4) {
            //检查两个节点是否已经连接
            for (int i = 0; i < degree; i++) {
                if (this.connectedNodes[i] == connectedNode){
                    System.out.println("Nodes already connected with each other.");
                    return;
                }
            }

            this.connectedNodes[degree++] = connectedNode;
        }else{
            System.out.println("Can't get more neighbors!");
        }
    }

    public synchronized void cutConnectedNodes(int connectedNode){
        /* @ REQUIRES: 0 <= connectedNode < 6400;
        @ MODIFIES: this.degree, this.connectedNodes;
        @ EFFECTS: 将connectedNode从this.connectedNodes删除并减少this.degree;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.conndectedNodes);
        */

        if (degree != 0){
            for (int i = 0; i < degree; i++) {
                if (this.connectedNodes[i] == connectedNode){
                    for (int j = i; j < degree; j++) {
                        if (j < degree - 1) {
                            this.connectedNodes[j] = this.connectedNodes[j + 1];
                        }else{
                            this.connectedNodes[j] = 0;
                        }
                    }
                    degree--;
                    return;
                }
            }
            System.out.println("Can't find the input adjacent node.");
        }else{
            System.out.println("Can't get fewer neighbors!");
        }
    }

    @Override
    public String toString() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this的信息；
        */

        return "MapNode{" +
                "degree=" + degree +
                ", coordinate=" + Arrays.toString(coordinate) +
                '}';
    }
}
