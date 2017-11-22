
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class FileHandler implements FileHandlerInterface{

    public void alertFilePathError(){
        System.out.println("Please check your file path.");
    }

    @Override
    public synchronized boolean getFileInfo(String path){
        MyFile file = new MyFile(path);
        if (file.exists() && file.isFile()) {
            System.out.println("File name: " + file.getName());
            System.out.println("File size: " + file.length() + "KB");
            System.out.println("File was last modified at: " + file.lastModified());
            return true;
        }else{
            this.alertFilePathError();
        }

        return false;
    }

    @Override
    public synchronized boolean renameFile(String oldPath, String newPath) {
        if (!oldPath.equals(newPath)){ //新的文件名和以前文件名不同时,才有必要进行重命名
            MyFile oldFile = new MyFile(oldPath);
            MyFile newFile = new MyFile(newPath);
            if(newFile.exists()) //若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                System.out.println(newPath + "File already exits!");
            else{
                return oldFile.renameTo(newFile); //newFile是有效路径才会rename成功
            }
        }else{
            System.out.println("Name of file " + oldPath + " doesn't change!");
        }

        return false;
    }

    @Override
    //cover参数用于控制是否允许覆盖同名目录，true为允许
    public synchronized boolean changeDirectory(String oldPath, String newPath, boolean cover){
        MyFile oldFile = new MyFile(oldPath);
        MyFile newFile = new MyFile(newPath);

        if (!oldPath.equals(newPath)){
            if (newFile.exists()){ //若在待转移目录下，已经存在待转移文件
                if(cover){ //覆盖
                    return oldFile.renameTo(newFile);
                }else{
                    System.out.println("Directory " + newFile.getParent() + " already has file "+ newFile.getName() + "!");
                }
            }else{
                return oldFile.renameTo(newFile);
            }
        }else{
            System.out.println("Location of file " + oldFile.getName() + " doesn't change!");
        }

        return false;
    }

    @Override
    //创建文件绝对地址为path的空文件，若其父目录不存在则创建失败
    public synchronized boolean createFile(String path){
        MyFile file= new MyFile(path);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Path " + path + " is invalid!");
                return false;
            }
            return true;
        }else{
            System.out.println("Directory " + file.getParent() + " already has file "+ file.getName() + "!");
        }

        return false;
    }

    @Override
    //创建全路径为path的空目录，不支持创建嵌套的新目录
    public synchronized boolean createDirectory(String path){
        MyFile directory = new MyFile(path);

        if (!directory.exists()) {
            return directory.mkdir();
        }else{
            System.out.println("Directory " + path + " already has directory " + directory + "!");
        }

        return false;
    }

    @Override
    public synchronized boolean removeFile(String path){
        MyFile file = new MyFile(path);

        if (file.exists() && file.isFile()){
            return file.delete();
        }else{
            System.out.println("File doesn't exist!");
        }

        return false;
    }

    @Override
    //以递归的方式删除文件夹
    public synchronized boolean removeDirectory(String path){
        MyFile directory = new MyFile(path);
        int i;

        if (directory.exists()){
            MyFile[] tempFiles = directory.listFiles();
            for (i = 0; i < tempFiles.length; i++){
                if (tempFiles[i].isDirectory()){
                    removeDirectory(path + "/" + tempFiles[i].getName());
                }else{
                    tempFiles[i].delete();
                }
            }
            directory.delete();
            return true;
        }else{
            System.out.println("Directory doesn't exist!");
        }

        return false;
    }

    @Override
    //在文件名为fileName的文件后面添加content，若不存在文件则在path路径下创建一个新文件
    public synchronized boolean appendFile(String path, String content) {
        MyFile directory =  new MyFile(path);

        if (directory.exists()){
            try {
                FileWriter fileWriter = new FileWriter(path, true);
                fileWriter.write(content);
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                System.out.println("Something unexpected happened when writing file " + path + "!");
                return false;
                //e.printStackTrace();
            }

            return true;
        }else{
            System.out.println("Directory where file locates doesn't exist!");
        }

        return false;
    }

}
