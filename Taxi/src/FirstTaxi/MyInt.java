package FirstTaxi;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class MyInt {
    /* Overview: 重新封装的整数类，包含一个整数类型值属性
     */

    public int value;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    public MyInt(int value) {
        /* @ MODIFIES: this.value;
        @ EFFECTS: 初始化this.value;
        */
        this.value = value;
    }

    public synchronized int getValue() {
        /* @ MODIFIES: None;
        @ EFFECTS: 返回this.value;
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.value);
        */

        return value;
    }
}
