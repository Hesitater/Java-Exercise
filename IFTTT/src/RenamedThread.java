
import java.util.ArrayList;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class RenamedThread extends Thread{
    private Summary summary;
    private MyDetail detail;
    private boolean[] actionList;
    private String path;
    private MyFile file;
    private FileInfo fileInfo;

    public RenamedThread(Summary summary, MyDetail detail, IFTTTContainer container) {
        this.summary = summary;
        this.detail = detail;
        this.actionList = container.getActionList();
        this.path = container.getPath();
        this.file = null;
        this.fileInfo = null;
    }

    public void run(){
        this.file = new MyFile(this.path);

        if (this.file.isFile()){ //监测对象是文件
            while (true) {
                this.file = new MyFile(this.path);
                if (this.file.exists()) { //文件未被重命名
                    if (this.fileInfo == null) {
                        this.fileInfo = new FileInfo(this.path);
                        this.refreshFileInfo();
                    }
                    //System.out.println("Not changed");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        System.out.println("Renamed sleep exception");
                        //e.printStackTrace();
                    }
                    continue;
                } else { //文件被重命名或删除或移动
                    //System.out.println("Changed");
                    MyFile parentDir = this.file.getParentFile();
                    if (parentDir != null && parentDir.exists()) {
                        MyFile[] fileList = parentDir.listFiles();
                        int i;
                        for (i = 0; i < fileList.length; i++) {
                            if (fileList[i].isFile()) {
                                //System.out.println("scan");
                                if (this.fileInfo.getLastModifiedTime() == fileList[i].lastModified() &&
                                        this.fileInfo.getSize() == fileList[i].length()) {
                                    this.path = fileList[i].getPath();
                                    //System.out.println("record or recover");
                                    this.executeAction();
                                    break;
                                }
                            }
                        }

                        if (i == fileList.length) { //文件被删除或移动
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                System.out.println("Renamed sleep exception");
                                //e.printStackTrace();
                            }
                            continue;
                        }
                    } else {
                        this.threadEndOutPut();
                        break;
                    }

                    this.refreshFileInfo();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Renamed sleep exception");
                        //e.printStackTrace();
                    }
                }
            }
        }else if (this.file.isDirectory()){ //监测对象是目录
            //System.out.println("is not file");
            FileMap oldMap = null;
            FileMap newMap = null;
            DirectoryTrigger trigger = new DirectoryTrigger();
            ArrayList<FileInfo> renamedList = new ArrayList<FileInfo>();
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
                renamedList = trigger.getRenamedList();

                for (i = 0; i < renamedList.size(); i = i + 2) {
                    this.executeAction(renamedList.get(i), renamedList.get(i + 1)); //对监控到的每一次触发执行任务
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
                        this.summary.increaseTriggerCount(0);
                    }
                    break;
                case 1: //record detail
                    if (this.actionList[i]) {
                        this.detail.recordNameChanged(this.fileInfo.getPath(), this.path);
                    }
                    break;
                case 2: //recover
                    if (this.actionList[i]) {
                        //System.out.println("befor recover: " + tempFile.getPath());
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
                        this.summary.increaseTriggerCount(0);
                    }
                    break;
                case 1: //record detail
                    if (this.actionList[i]) {
                        this.detail.recordNameChanged(oldInfo.getName(), newInfo.getName());
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
    public void threadEndOutPut(){
        System.out.println("Path " + this.file.getPath() + " no longer exists. Thread ends.");
    }
}
