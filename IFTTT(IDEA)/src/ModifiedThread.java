
import java.io.File;
import java.util.ArrayList;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class ModifiedThread extends Thread{
    private Summary summary;
    private MyDetail detail;
    private boolean[] actionList;
    private String path;
    private MyFile file;
    private FileInfo fileInfo;

    public ModifiedThread(Summary summary, MyDetail detail, IFTTTContainer container) {
        this.summary = summary;
        this.detail = detail;
        this.actionList = container.getActionList();
        this.path = container.getPath();
        this.file = null;
        this.fileInfo = null;
    }

    public void run(){
        this.file = new MyFile(this.path);
        if (file.isFile()){
            while (true){
                if (this.fileInfo == null) { //首次读入监测文件信息
                    this.fileInfo = new FileInfo(this.path);
                    this.refreshFileInfo();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Modified sleep exception");
                    }
                }

                if (this.file.exists() &&
                        (this.file.lastModified() != this.fileInfo.getLastModifiedTime())) { //文件最后修改时间改变
                    this.executeAction(); //执行任务
                    this.refreshFileInfo(); //更新文件状态
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Modified sleep exception");
                    //e.printStackTrace();
                }
            }
        }else if (file.isDirectory()) {
            FileMap oldMap = null;
            FileMap newMap = null;
            DirectoryTrigger trigger = new DirectoryTrigger();
            ArrayList<FileInfo> modifiedList = new ArrayList<FileInfo>();
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
                modifiedList = trigger.getModifiedList();

                for (i = 0; i < modifiedList.size(); i = i + 2) {
                    this.executeAction(modifiedList.get(i), modifiedList.get(i + 1)); //对监控到的每一次触发执行任务
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

    public synchronized void executeAction(){
        int i;
        MyFile tempFile = new MyFile(this.path);
        for (i = 0; i < this.actionList.length; i++) {
            switch (i) {
                case 0: //record summary
                    if (this.actionList[i]) {
                        this.summary.increaseTriggerCount(1);
                    }
                    break;
                case 1: //record detail
                    if (this.actionList[i]) {
                        this.detail.recordModifiedTimeChanged(this.path, this.fileInfo.getLastModifiedTime(),
                                this.file.lastModified());
                    }
                    break;
                case 2: //recover
                    if (this.actionList[i]){
                        System.out.println("Modified can't recover.");
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
                        this.summary.increaseTriggerCount(1);
                    }
                    break;
                case 1: //record detail
                    if (this.actionList[i]) {
                        this.detail.recordModifiedTimeChanged(oldInfo.getPath(), oldInfo.getLastModifiedTime(),
                                newInfo.getLastModifiedTime());
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
