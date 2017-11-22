
import java.io.File;
import java.io.IOException;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class MyFile extends File {

    public MyFile(String pathname) {
        super(pathname);
    }

    @Override
    public synchronized String getParent() {
        return super.getParent();
    }

    public synchronized MyFile getParentFile() {
        MyFile myFile = null;
        myFile = new MyFile(super.getParentFile().getPath());
        return myFile;
    }

    @Override
    public synchronized String getPath() {
        return super.getPath();
    }

    @Override
    public synchronized boolean exists() {
        return super.exists();
    }

    @Override
    public synchronized boolean isDirectory() {
        return super.isDirectory();
    }

    @Override
    public synchronized boolean isFile() {
        return super.isFile();
    }

    @Override
    public synchronized long length() {
        return super.length();
    }

    @Override
    public synchronized boolean createNewFile() throws IOException {
        return super.createNewFile();
    }

    @Override
    public synchronized boolean delete() {
        return super.delete();
    }

    @Override
    public synchronized MyFile[] listFiles() {
        File[] fileList = super.listFiles();
        MyFile[] myFileList = new MyFile[fileList.length];

        for (int i = 0; i < fileList.length; i++) {
            myFileList[i] = new MyFile(fileList[i].getPath());
        }

        return myFileList;
    }

    @Override
    public synchronized boolean mkdir() {
        return super.mkdir();
    }

    @Override
    public synchronized boolean renameTo(File dest) {
        return super.renameTo(dest);
    }

    @Override
    public synchronized long lastModified() {
        return super.lastModified();
    }

    @Override
    public synchronized String getName() {
        return super.getName();
    }
}
