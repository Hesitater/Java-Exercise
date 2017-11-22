package FirstTaxi;

import sun.text.resources.cldr.ha.FormatData_ha;

import java.util.ArrayList;
import java.util.Arrays;
import static FirstTaxi.TaxiTest.MAXORDER;


/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class TaxiAPI {
    /* Overview: 出租车查询类，能查询指定出租车状态或处于某一状态的一类车
     */

    private Taxi[] taxis;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */

        if (taxis == null){
            return false;
        }

        return true;
    }
    
    public TaxiAPI(Taxi[] taxis) {
        /* @ REQUIRES: this.taxis != null;
        @ MODIFIES: this.taxis;
        @ EFFECTS: 初始化this.taxis;
        */
        this.taxis = taxis;
    }
    
    //通过出租车号查找出租车信息
    public synchronized void getTaxiInfo(int taxiID){
        /* @ REQUIRES: taxiID >= 0 & taxiID <= 99；
        @ MODIFIES: None;
        @ EFFECTS: 通过出租车号查找出租车信息；
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.taxis);
        */
        if (taxiID < 1 || taxiID > 100){
            System.out.println("Taxi id out of range.");
            return;
        }

        Taxi taxi = taxis[taxiID - 1];
        System.out.println("Current time: " + System.currentTimeMillis() + "\n" +
        "Taxi coordinate: " + Arrays.toString(taxi.getLocation()) + "\n");
    }

    //参数0是到处闲逛，1是服务中，2是接单中，3是停止
    public synchronized ArrayList<Taxi> getTaxisInState(int state){
        /* @ REQUIRES: state >= 0 && state <= 3
        @ MODIFIES: None;
        @ EFFECTS: 得到所有符合state条件的出租车对象；
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.taxis);
        */

        if (state < 0 || state > 3){
            System.out.println("State out of range. Program ended.");
            System.exit(0);
        }

        ArrayList<Taxi> matchedTaxis = new ArrayList<Taxi>();

        for (int i = 0; i < taxis.length; i++) {
            if (taxis[i].getState() == state){
                matchedTaxis.add(taxis[i]);
            }
        }

        return matchedTaxis;
    }
}
