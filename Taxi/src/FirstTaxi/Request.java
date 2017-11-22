package FirstTaxi;

import java.math.BigDecimal;
import java.util.Arrays;
import static FirstTaxi.TaxiTest.MAXORDER;


/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class Request {
    /* Overview: 请求类，存储请求的具体信息，包括请求发出时间、请求所在地、请求目的地
     */

    private int[] location;
    private int[] destination;
    public double time;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    public Request() {
        /* @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性;
        */

        this.location = new int[2];
        this.destination = new int[2];
        this.time = System.currentTimeMillis();
    }
//Setters
    public void setLocation(int x, int y) {
        /* @ REQUIRES: （0 <= x < MAXORDER) & (0 <= y < MAXORDER);
        @ MODIFIES: this.location;
        @ EFFECTS: 设置this.location;
        */

        this.location[0] = x;
        this.location[1] = y;
    }

    public void setDestination(int x, int y) {
        /* @ REQUIRES: （0 <= x < MAXODER) & (0 <= y < MAXORDER);
        @ MODIFIES: this.destination;
        @ EFFECTS: 设置this.destination;
        */

        this.destination[0] = x;
        this.destination[1] = y;
    }

    public void setTime(double time){
        /* @ REQUIRES: （0 <= x <= 79) & (0 <= y <= 79);
        @ MODIFIES: this.time;
        @ EFFECTS: 设置this.time;
        */

        this.time = time;
    }

//Getters
    public int[] getLocation() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.location;
        */

        return location;
    }

    public int[] getDestination() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.destination;
        */

        return destination;
    }

    @Override
    public String toString() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回请求信息;
        */

        return "Request {" +
                "location=" + Arrays.toString(location) +
                ", destination=" + Arrays.toString(destination) +
                ", time=" + formatRequestTime(time) +
                '}';
    }

    //转化毫秒为秒
    public BigDecimal formatRequestTime(double requestTime) {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回格式化后的时间;
        */

        BigDecimal bigDecimal = new BigDecimal(requestTime);
        return bigDecimal;
    }
}
