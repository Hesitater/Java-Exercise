
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class MyDetail {
    private String detailFilePath;
    private FileWriter fileWriter;
    private String tempContent;

    public MyDetail(String detailFilePath) {
        this.detailFilePath = detailFilePath;
        this.tempContent = null;
        try {
            this.fileWriter = new FileWriter(detailFilePath, true);
        } catch (IOException e) {
            System.out.println("Something unexpected when creating detail file.");;
        }
    }

    public synchronized void recordSizeChanged(String path, long oldSize, long newSize){
        if (tempContent != null) {
            this.tempContent = this.tempContent + "File " + path + " change size from " + oldSize + " to "
                    + newSize + System.getProperty("line.separator");
        }else{
            this.tempContent = "File " + path + " change size from " + oldSize + " to "
                    + newSize + System.getProperty("line.separator");
        }
    }

    public synchronized void recordNameChanged(String oldName, String newName){
        if (this.tempContent != null) {
            this.tempContent = this.tempContent + "File " + oldName + " change name to " + newName + System.getProperty("line.separator");
        }else{
            this.tempContent = "File " + oldName + " change name to " + newName + System.getProperty("line.separator");
        }
    }

    public synchronized void recordPathChanged(String oldPath, String newPath){
        if (this.tempContent != null) {
            this.tempContent = this.tempContent + oldPath + " change path to " + newPath + System.getProperty("line.separator");
        }else{
            this.tempContent = oldPath + " change path to " + newPath + System.getProperty("line.separator");
        }
    }

    public synchronized void recordModifiedTimeChanged(String path, long oldTime, long newTime){
        if (this.tempContent != null) {
            this.tempContent = this.tempContent + "File " + path + " change last modified time from " + oldTime + " to "
                    + newTime + System.getProperty("line.separator");
        }else{
            this.tempContent = "File " + path + " change last modified time from " + oldTime + " to "
                    + newTime + System.getProperty("line.separator");
        }
    }

    public synchronized void writeToDetailFile(){
        try {
            if (this.tempContent != null){
                this.fileWriter.write(this.tempContent);
            }
            this.fileWriter.flush();
            this.tempContent = null;
        } catch (IOException e) {
            System.out.println("Something unexpected when writing detail file.");
        }
    }
}
