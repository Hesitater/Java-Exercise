
import java.io.File;
import java.util.ArrayList;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class PathChangedThread extends Thread{
    private Summary summary;
    private MyDetail detail;
    private IFTTTContainer container;
    private boolean[] actionList;
    private String path;
    private MyFile file;
    private FileInfo fileInfo;
    private boolean pathChanged;

    public PathChangedThread(Summary summary, MyDetail detail, IFTTTContainer container) {
        this.summary = summary;
        this.detail = detail;
        this.container = container;
        this.actionList = container.getActionList();
        this.path = container.getPath();
        this.file = null;
        this.fileInfo = null;
        this.pathChanged = false;
    }

    public void run(){
        this.file = new MyFile(this.path);
        if (file.isFile()){
            while (true){
                this.file = new MyFile(this.path);
               //System.out.println(this.path);
                if (this.fileInfo == null) { //首次读入监测文件信息
                    this.fileInfo = new FileInfo(this.path);
                    this.refreshFileInfo();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Path-changed sleep exception");
                    }
                }

                if (!this.file.exists()){ //找不到文件则不断递归其父目录的子目录以找到相同文件
                    //System.out.println("file not exist");
                    this.findSameFile(this.file.getParentFile());
                    if (this.pathChanged) { //找到文件的新路径
                        //System.out.println(this.path);
                        //System.out.println("find new path");
                        this.pathChanged = false;
                        this.executeAction(); //执行任务
                        this.refreshFileInfo();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Path-changed sleep exception");
                    //e.printStackTrace();
                }
            }
        }else if (file.isDirectory()) {
            FileMap oldMap = null;
            FileMap newMap = null;
            DirectoryTrigger trigger = new DirectoryTrigger();
            ArrayList<FileInfo> pathChangedList = new ArrayList<FileInfo>();
            int i;

            while (true){
                newMap = new FileMap();
                newMap.drawMap(this.file);

                if (oldMap == null){ //第一次进行监测
                    oldMap = newMap;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Renamed sleep exception");
                        //e.printStackTrace();
                    }
                    continue;
                }

                trigger.monitorTrigger(oldMap, newMap); //进行一次完全监测
                pathChangedList = trigger.getPathChangedList();

                for (i = 0; i < pathChangedList.size(); i = i + 2) {
                    this.executeAction(pathChangedList.get(i), pathChangedList.get(i + 1)); //对监控到的每一次触发执行任务
                }

                oldMap = newMap; //更新当前快照
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Renamed sleep exception");
                    //e.printStackTrace();
                }
            }
        }else{
            System.out.println(this.file.getPath() + " doesn't exist. Thread ends.");
        }
    }

    public void findSameFile(MyFile file){
        if(file.isDirectory()){
            MyFile[] fileList = file.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    this.findSameFile(fileList[i]); //递归调用
                }
            }
        }else{
            //System.out.println("File name " + file.getName() + " fileinfo name ");
            //System.out.println("");
            if (file.getName().equals(fileInfo.getName()) && file.lastModified() == fileInfo.getLastModifiedTime()
                    && file.length() == fileInfo.getSize()){ //找到被移动的文件
                this.path = file.getPath(); //更改监控的文件路径
                this.pathChanged = true;
            }
        }
    }

    public synchronized void executeAction(){
        int i;
        MyFile tempFile = new MyFile(this.path);
        for (i = 0; i < this.actionList.length; i++) {
            switch (i) {
                case 0: //record summary
                    if (this.actionList[i]) {
                        this.summary.increaseTriggerCount(2);
                    }
                    break;
                case 1: //record detail
                    if (this.actionList[i]) {
                        this.detail.recordPathChanged(this.fileInfo.getPath(), this.path);
                    }
                    break;
                case 2: //recover
                    if (this.actionList[i]){
                        tempFile.renameTo(this.file);
                        //System.out.println("after recover: " + tempFile.getPath());
                        this.path = this.file.getPath();
                    }
                    break;
                default:
            }
        }
    }

    public synchronized void executeAction(FileInfo oldInfo, FileInfo newInfo){
        int i;
        for (i = 0; i < this.actionList.length; i++) {
            switch (i) {
                case 0: //record summary
                    if (this.actionList[i]) {
                        this.summary.increaseTriggerCount(2);
                    }
                    break;
                case 1: //record detail
                    if (this.actionList[i]) {
                        this.detail.recordPathChanged(oldInfo.getPath(), newInfo.getPath());
                    }
                    break;
                case 2: //recover
                    if (this.actionList[i]) {
                        MyFile newFile = new MyFile(newInfo.getPath());
                        MyFile oldFile = new MyFile(oldInfo.getPath());
                        newFile.renameTo(oldFile);
                    }
                    break;
                default:
            }
        }
    }

    public void refreshFileInfo(){
        this.fileInfo.changePath(this.path);
        this.fileInfo.calculateSize();
        this.fileInfo.refreshLastModifiedTime();
    }
}
