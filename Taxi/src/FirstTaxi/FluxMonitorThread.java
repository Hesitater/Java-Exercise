package FirstTaxi;

import java.util.Iterator;
import static FirstTaxi.TaxiTest.MAXORDER;


/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class FluxMonitorThread extends Thread{
    /* Overview: 流量监控线程，控制流量的周期性清空
     */

    public CityMap map;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        if (map == null) {
            return false;
        }

        return true;
    }

    public FluxMonitorThread(CityMap map) {
        /* @ REQUIRES: map != null;
        @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性；
        */
        this.map = map;
    }

    public void run(){
        /* @ MODIFIES: this.map;
        @ EFFECTS: 周期性清空this.map.fluxes; exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("Flux monitor thread sleep error!");
                //e.printStackTrace();
            }

            /*
            Iterator<String> iterator = this.map.fluxes.keySet().iterator();
            int count = 0;
            for (int i = 0; i < this.map.fluxes.size(); i++) {
                count += this.map.fluxes.get(iterator.next()).value;
            }
            System.out.println(count);
            */

            this.map.initializeFluxes();
        }
    }
}
