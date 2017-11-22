package FirstTaxi;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public interface CarInterface {
    /* Overview: 所有类型车的接口类
     */

    public boolean repOK();
    /* @ Effects: \result==invariant(this).
    */

    public void move();
    /* @ MODIFIES: None;
    @ EFFECTS: 车子移动一个单位；
    */
}
