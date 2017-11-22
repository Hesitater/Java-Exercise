
/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class FileInfo {
    private String path;
    private long lastModifiedTime;
    private long size;
    private boolean isFile;

    public FileInfo(String path) {
        this.path = path;
        this.lastModifiedTime = 0;
        this.refreshLastModifiedTime();
        this.size = 0;
        this.calculateSize();
        this.isFile = false;
        this.checkIsFile();
    }

    public synchronized void changePath(String newPath){
        if (this.isFile == true){
            this.path = newPath;
        }
    }

    public synchronized String getPath(){
        return this.path;
    }

    public synchronized long getLastModifiedTime() {
        return this.lastModifiedTime;
    }

    public synchronized void refreshLastModifiedTime(){
        MyFile file = new MyFile(this.path);
        this.lastModifiedTime = file.lastModified();
    }

    public synchronized long getSize() {
        return size;
    }

    public synchronized void calculateSize() {
        MyFile file = new MyFile(this.path);
        if (file.isDirectory()){ //是目录则进行大小计算
            MyFile[] fileList = file.listFiles();
            int i;
            for (i = 0; i < fileList.length; i++){
                if (fileList[i].isFile()){ //是文件则加上文件大小
                    this.size += fileList[i].length();
                }
            }
        }else{
            this.size = file.length();
        }
    }

    public synchronized boolean isFile() {
        return this.isFile;
    }

    public synchronized void checkIsFile(){
        MyFile file = new MyFile(this.path);
        this.isFile = file.isFile();
    }

    public synchronized String getName(){
        MyFile file = new MyFile(path);
        return file.getName();
    }

    public synchronized String getParent(){
        MyFile file = new MyFile(this.path);
        return file.getParent();
    }

}
