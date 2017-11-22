import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class DirectoryTrigger {
    private ArrayList<FileInfo> renamedList; //存储名称改变元素的数组（前为旧元素，后为新元素）
    private ArrayList<FileInfo> pathChangedList; //存储路径改变元素的数组（前为旧元素，后为新元素）
    private ArrayList<FileInfo> sizeChangedList; //存储大小改变元素的数组（前为旧元素，后为新元素）
    private ArrayList<FileInfo> modifiedList; //存储修改时间改变元素的数组（前为旧元素，后为新元素）

    public DirectoryTrigger() {
        this.renamedList = new ArrayList<FileInfo>(); //存储名称改变元素的数组（前为旧元素，后为新元素）
        this.pathChangedList = new ArrayList<FileInfo>(); //存储路径改变元素的数组（前为旧元素，后为新元素）
        this.sizeChangedList = new ArrayList<FileInfo>(); //存储大小改变元素的数组（前为旧元素，后为新元素）
        this.modifiedList = new ArrayList<FileInfo>(); //存储修改时间改变元素的数组（前为旧元素，后为新元素）
    }

    public void monitorTrigger(FileMap oldFileMap, FileMap newFileMap) {
        HashMap<String, FileInfo> oldMap = oldFileMap.getMap();
        HashMap<String, FileInfo> newMap = newFileMap.getMap();

        Set<String> oldKeySet = oldMap.keySet();
        Set<String> newKeySet = newMap.keySet();
        Iterator<String> oldIter = oldKeySet.iterator();
        Iterator<String> newIter = newKeySet.iterator();

        ArrayList<FileInfo> renamedList = new ArrayList<FileInfo>(); //存储名称改变元素的数组（前为旧元素，后为新元素）
        ArrayList<FileInfo> pathChangedList = new ArrayList<FileInfo>(); //存储路径改变元素的数组（前为旧元素，后为新元素）
        ArrayList<FileInfo> sizeChangedList = new ArrayList<FileInfo>(); //存储大小改变元素的数组（前为旧元素，后为新元素）
        ArrayList<FileInfo> modifiedList = new ArrayList<FileInfo>(); //存储修改时间改变元素的数组（前为旧元素，后为新元素）
        ArrayList<FileInfo> occurringList = new ArrayList<FileInfo>(); //存储新出现元素的数组
        ArrayList<FileInfo> lostingList = new ArrayList<FileInfo>(); //存储缺失的旧元素的数组

        //用旧快照比较新快照，以确定更改时间的文件、被改变大小（不包含重命名和移动导致的删除、添加），找到可能的被移动、重命名、新建的文件
        while (newIter.hasNext()) {
            String currentKey = newIter.next();
            if (oldKeySet.contains(currentKey)) {
                FileInfo oldFileInfo = oldMap.get(currentKey);
                FileInfo newFileInfo = newMap.get(currentKey);
                if (oldFileInfo.getSize() != newFileInfo.getSize()) {
                    sizeChangedList.add(oldFileInfo);
                    sizeChangedList.add(newFileInfo);
                }
                if (newFileInfo.getLastModifiedTime() != oldFileInfo.getLastModifiedTime()) {
                    modifiedList.add(oldFileInfo);
                    modifiedList.add(newFileInfo);
                }
            } else if (newMap.get(currentKey).isFile()) {
                occurringList.add(newMap.get(currentKey));
            } else {
                System.out.println("New directory " + currentKey + " adding.");
            }
        }

        //用新快照比较旧快照，以确定被移动、重命名、新建的文件，并进一步更新改变大小的文件容器
        while (oldIter.hasNext()){
            String currentKey = oldIter.next();
            if (!newKeySet.contains(currentKey)) {
                if (oldMap.get(currentKey).isFile()) {
                    lostingList.add(oldMap.get(currentKey));
                } else if ((new MyFile(oldMap.get(currentKey).getPath()).exists()) && !oldMap.get(currentKey).isFile()) {
                    System.out.println("Directory " + currentKey + " has been deleted.");
                }
            }
        }

        //比较occurringList和lostingList，通过比较确定上一次扫描的文件是否被重命名或删除或新增或更改路径
        int i, j;
        for (i = 0; i < occurringList.size(); i++) {
            FileInfo occurringInfo = occurringList.get(i);
            for (j = 0; j < lostingList.size(); j++) {
                FileInfo lostingInfo = lostingList.get(j);
                if (occurringInfo.getParent() != null && lostingInfo.getParent() != null){
                    if (occurringInfo.getParent().equals(lostingInfo.getParent()) &&
                            occurringInfo.getLastModifiedTime() == lostingInfo.getLastModifiedTime() &&
                            occurringInfo.getSize() == lostingInfo.getSize()) { //文件被重命名
                        renamedList.add(lostingInfo);
                        renamedList.add(occurringInfo);
                        //重命名代表文件的一删一增
                        sizeChangedList.add(lostingInfo);
                        sizeChangedList.add(new FileInfo(""));
                        sizeChangedList.add(new FileInfo(""));
                        sizeChangedList.add(occurringInfo);
                        break;
                    }
                }

                if (occurringInfo.getName().equals(lostingInfo.getName()) &&
                        occurringInfo.getLastModifiedTime() == lostingInfo.getLastModifiedTime() &&
                        occurringInfo.getSize() == lostingInfo.getSize()) { //文件路径被移动
                    pathChangedList.add(lostingInfo);
                    pathChangedList.add(occurringInfo);
                    //文件路径移动代表原文件被删，并新增一个文件
                    sizeChangedList.add(lostingInfo);
                    sizeChangedList.add(new FileInfo(""));
                    sizeChangedList.add(new FileInfo(""));
                    sizeChangedList.add(occurringInfo);
                    break;
                }
            }

            if (j == lostingList.size()){ //该文件是新建的
                sizeChangedList.add(new FileInfo(""));
                sizeChangedList.add(occurringInfo);
            }
        }

        for (i = 0; i < lostingList.size(); i++) {
            FileInfo lostingInfo = lostingList.get(i);
            for (j = 0; j < occurringList.size(); j++) {
                FileInfo occurringInfo = occurringList.get(j);
                if (occurringInfo.getParent() != null && lostingInfo.getParent() != null){
                    if (occurringInfo.getParent().equals(lostingInfo.getParent()) &&
                            occurringInfo.getLastModifiedTime() == lostingInfo.getLastModifiedTime() &&
                            occurringInfo.getSize() == lostingInfo.getSize()) { //文件被重命名
                        break;
                    }
                }

                if (occurringInfo.getName().equals(lostingInfo.getName()) &&
                        occurringInfo.getLastModifiedTime() == lostingInfo.getLastModifiedTime() &&
                        occurringInfo.getSize() == lostingInfo.getSize()) { //文件路径被移动
                    break;
                }
            }

            if (j == occurringList.size()){ //该文件已被删除
                sizeChangedList.add(lostingInfo);
                sizeChangedList.add(new FileInfo(""));
            }
        }

        this.renamedList = renamedList;
        this.modifiedList = modifiedList;
        this.pathChangedList = pathChangedList;
        this.sizeChangedList = sizeChangedList;
    }

    //务必在trigger执行后再执行以下方法

    public ArrayList<FileInfo> getRenamedList() {
        return renamedList;
    }

    public ArrayList<FileInfo> getPathChangedList() {
        return pathChangedList;
    }

    public ArrayList<FileInfo> getSizeChangedList() {
        return sizeChangedList;
    }

    public ArrayList<FileInfo> getModifiedList() {
        return modifiedList;
    }
}
