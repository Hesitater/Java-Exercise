
import java.util.ArrayList;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class SizeChangedThread extends Thread{
    private Summary summary;
    private MyDetail detail;
    private IFTTTContainer container;
    private boolean[] actionList;
    private String path;
    private MyFile file;
    private FileInfo fileInfo;

    public SizeChangedThread(Summary summary, MyDetail detail, IFTTTContainer container) {
        this.summary = summary;
        this.detail = detail;
        this.container = container;
        this.actionList = container.getActionList();
        this.path = container.getPath();
        this.file = null;
        this.fileInfo = null;
    }

    public void run(){
        this.file = new MyFile(this.path);
        if (file.isFile()){
            boolean isDeleted = false;
            while (true){ //监测对象是文件
                if (this.fileInfo == null) { //首次读入监测文件信息
                    this.fileInfo = new FileInfo(this.path);
                    this.refreshFileInfo();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Size-changed sleep exception");
                    }
                }

                if (this.file.exists() &&
                        ((this.file.length() != this.fileInfo.getSize()) || isDeleted)) { //文件存在且文件大小改变
                    this.executeAction(isDeleted);
                    if (isDeleted){ //之前文件不存在
                        isDeleted = false; //现在文件存在
                    }

                    this.refreshFileInfo(); //更新文件状态
                }else if (!this.file.exists() && isDeleted == false){ //文件刚刚被删除
                    this.executeAction(isDeleted);
                    isDeleted = true;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Size-changed sleep exception");
                    //e.printStackTrace();
                }
            }
        }else if (file.isDirectory()) { //监测对线是目录
            FileMap oldMap = null;
            FileMap newMap = null;
            DirectoryTrigger trigger = new DirectoryTrigger();
            ArrayList<FileInfo> sizeChangedList = new ArrayList<FileInfo>();
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
                sizeChangedList = trigger.getSizeChangedList();

                for (i = 0; i < sizeChangedList.size(); i = i + 2) {
                    if (sizeChangedList.get(i).getPath() != "") {
                        this.executeAction(sizeChangedList.get(i).getPath(), sizeChangedList.get(i), sizeChangedList.get(i + 1)); //对监控到的每一次触发执行任务
                    }else{
                        this.executeAction(sizeChangedList.get(i + 1).getPath(), sizeChangedList.get(i), sizeChangedList.get(i + 1));
                    }
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

    public synchronized void executeAction(boolean isDeleted){
        int i;
        MyFile tempFile = new MyFile(this.path);
        for (i = 0; i < this.actionList.length; i++) {
            switch (i) {
                case 0: //record summary
                    if (this.actionList[i]) {
                        this.summary.increaseTriggerCount(3);
                    }
                    break;
                case 1: //record detail
                    if (this.actionList[i]) {
                        if (isDeleted) { //之前不存在此文件
                            this.detail.recordSizeChanged(this.path, 0, this.file.length());
                        }else{
                            this.detail.recordSizeChanged(this.path, this.fileInfo.getSize(), this.file.length());
                        }
                    }
                    break;
                case 2: //recover
                    if (this.actionList[i]){
                        System.out.println("Size-changed can't recover.");
                    }
                    break;
                default:
            }
        }
    }

    public synchronized void executeAction(String existsPath, FileInfo oldInfo, FileInfo newInfo){
        int i;
        for (i = 0; i < this.actionList.length; i++) {
            switch (i) {
                case 0: //record summary
                    if (this.actionList[i]) {
                        this.summary.increaseTriggerCount(3);
                    }
                    break;
                case 1: //record detail
                    if (this.actionList[i]) {
                        this.detail.recordSizeChanged(existsPath, oldInfo.getSize(), newInfo.getSize());
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
