
/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class RecordWritingThread extends Thread{
    private Summary summary;
    private MyDetail detail;

    public RecordWritingThread(Summary summary, MyDetail detail) {
        this.summary = summary;
        this.detail = detail;
    }

    public void run(){
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.summary.writeToSummaryFile();
            this.detail.writeToDetailFile();
        }
    }
}
