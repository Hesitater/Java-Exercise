
import java.util.HashMap;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class FileMap {
    private HashMap<String, FileInfo> map;

    public FileMap() {
        this.map = new HashMap<String, FileInfo>();
    }

    public void drawMap(MyFile file) {
        this.map.put(file.getPath(), new FileInfo(file.getPath()));

        if (file.isFile()) {
            return;
        }else if (file.isDirectory()) {
            MyFile[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                this.drawMap(fileList[i]);
            }
        }else{
            System.out.println("Invalid path " + file.getPath());
        }
    }

    public HashMap<String, FileInfo> getMap(){
        return this.map;
    }

}
