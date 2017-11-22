
/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class IFTTTContainer {
    private int trigger; //0 is "renamed", 1 is "modified", 2 is "path-changed", 3 is "size-changed"
    private String path;
    private boolean[] actionList; //0 is "record-summary", 1 is "record-detail", 2 is "recover"

    public IFTTTContainer() {
        this.trigger = -1;
        this.path = null;
        this.actionList = new boolean[3];
        for (int i = 0; i < actionList.length; i++) {
            this.actionList[i] = false;
        }
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAction(int type) {
        this.actionList[type] = true;
    }

    public int getTrigger() {
        return this.trigger;
    }

    public String getPath() {
        return this.path;
    }

    public boolean[] getActionList(){
        return this.actionList;
    }
}
