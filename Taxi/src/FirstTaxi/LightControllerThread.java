package FirstTaxi;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.awt.*;
import java.util.Random;

import static FirstTaxi.TaxiTest.MAXORDER;
import static FirstTaxi.TaxiTest.gui;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class LightControllerThread extends Thread{
    /* Overview: 红绿灯控制线程，控制所有红绿灯状态的更新
     */

    private CityMap map;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        if (map == null){
            return false;
        }

        return true;
    }

    public LightControllerThread(CityMap map) {
        /* @ REQUIRES: map != null;
        @ MODIFIES: this;
        @ EFFECTS: 初始化所有属性;
        */

        Random random = new Random();
        this.map = map;
        int sleepTime;

        //给予每个红绿灯一个随机的切换间隔时间
        for (int i = 0; i < MAXORDER * MAXORDER; i++) {
            sleepTime = random.nextInt(301) + 200;
            this.map.getNewMap()[i].lightWaitingTime = sleepTime;
        }
    }

    public void run(){
        /* @ MODIFIES: this;
        @ EFFECTS: 随时监测所有路口红绿灯，到了其自身转换周期则转换红绿灯状态; exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        int[] sleepTime = new int[MAXORDER * MAXORDER];
        MapNode tempNode = null;

        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Light controller thread sleep error.");
                //e.printStackTrace();
            }

            for (int i = 0; i < MAXORDER * MAXORDER; i++) {
                tempNode = this.map.getNewMap()[i];

                if (sleepTime[i] == tempNode.lightWaitingTime) {
                    if (tempNode.lightState == 1) {
                        tempNode.lightState = 2;
                        gui.SetLightStatus(new Point(tempNode.coordinate[0], tempNode.coordinate[1]), 2);
                    }else if (this.map.getNewMap()[i].lightState == 2) {
                        this.map.getNewMap()[i].lightState = 1;
                        gui.SetLightStatus(new Point(tempNode.coordinate[0], tempNode.coordinate[1]), 1);
                    }
                    sleepTime[i] = 0;
                }else{
                    sleepTime[i]++;
                }
            }
        }
    }
}
