
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class Summary {
    private int[] triggerCount; //0 is "renamed", 1 is "modified", 2 is "path-changed", 3 is "size-changed"
    private String summaryFilePath;

    public Summary(String summaryFilePath) {
        this.summaryFilePath = summaryFilePath;
        this.triggerCount = new int[4];
        int i;
        for (i = 0; i < this.triggerCount.length; i++) {
            this.triggerCount[i] = 0;
        }
        this.writeToSummaryFile(); //每次新建summary对象时初始化summary.txt
    }

    public synchronized void increaseTriggerCount(int type){
        this.triggerCount[type]++;
    }

    public synchronized int getTriggerCount(int type) {
        return this.triggerCount[type];
    }

    public synchronized void writeToSummaryFile(){
        try {
            FileWriter fileWriter = new FileWriter(summaryFilePath, false);
            fileWriter.write("Trigger Count" + System.getProperty("line.separator")
            + "renamed: " + this.triggerCount[0] + System.getProperty("line.separator")
            + "modified: " + this.triggerCount[1] + System.getProperty("line.separator")
            + "path-changed: " + this.triggerCount[2] + System.getProperty("line.separator")
            + "size-changed: " + this.triggerCount[3] + System.getProperty("line.separator"));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Something unexpected when writing summary file.");
        }


    }
}
