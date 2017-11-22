package FirstTaxi;

import java.util.ArrayList;
import static FirstTaxi.TaxiTest.MAXORDER;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class RequestQueue {
    /* Overview: 请求队列类，存储了一个请求队列，可通过方法进行修改
     */

    private ArrayList<Request> requests;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    public RequestQueue() {
        /* @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性;
        */

        this.requests = new ArrayList<Request>();
    }

    public synchronized void append(Request request){
        /* @ REQUIRES: request != null;
        @ MODIFIES: this.requests;
        @ EFFECTS: 增加request到this.requests队尾;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.requests);
        */

        this.requests.add(request);
    }

    public synchronized Request remove(int index){
        /* @REQUIRES: 0 <= index < this.requests.size();
        @ MODIFIES: this.requests;
        @ EFFECTS: 删除this.requests内下标为index的对象;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.requests);
        */

        return this.requests.remove(index);
    }

    public synchronized void insert(int index, Request request){
        /* @ REQUIRES: (0 <= index < this.requests.size) & (request != null);
        @ MODIFIES: this.requests;
        @ EFFECTS: 在this.requests中下标为index的位置增加request;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.requests);
        */

        this.requests.add(index, request);
    }

    public synchronized Request getRequestAtIndex(int index){
        /* @ REQUIRES: 0 <= index < this.requests.size();
        @ MODIFIES: None;
        @ EFFECTS: 返回this.requests中下标为index的对象;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.requests);
        */

        return this.requests.get(index);
    }

    public synchronized int getSize(){
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.requests的长度;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.requests);
        */

        return this.requests.size();
    }
}
