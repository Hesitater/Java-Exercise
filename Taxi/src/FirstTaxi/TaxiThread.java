package FirstTaxi;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class TaxiThread extends Thread{
    /* Overview: 出租车线程类，根据出租车状态给出租车发指令
     */

    private Taxi taxi;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */

        if (taxi == null){
            return false;
        }

        return true;
    }

    public TaxiThread(Taxi taxi) {
        /* @ REQUIRES: taxi != null;
        @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性;
        */

        this.taxi = taxi;
    }

    public void run(){
        /* @ MODIFIES: this.taxi;
        @ EFFECTS: 根据this.taxi的状态给出this.taxi相应指令;
        */

        while (true) {
            /*if (this.taxi.type == 1){
                TraceableTaxi.ServiceRecordIterator iterator = ((TraceableTaxi)this.taxi).generateSRIterator();
                if (iterator.hasNext()){
                    iterator.next();
                    iterator.previous();
                }
            }*/

            if (this.taxi.getState() == 0) { //出租车在闲逛
                this.taxi.wander();
            } else if (this.taxi.getState() == 2) { //出租车在接客
                this.taxi.pickUpClient();
            } else if (this.taxi.getState() == 1) { //出租车在送客
                //接客之后，出租车会自动送客
            } else if (this.taxi.getState() == 3) { //出租车停止
                //出租车停止
            }
        }
    }
}
