
import java.io.IOException;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public interface FileHandlerInterface {
    public boolean getFileInfo(String path);
    public boolean renameFile(String oldPath, String newPath);
    public boolean changeDirectory(String oldPath, String newPath, boolean cover);
    public boolean createFile(String path);
    public boolean createDirectory(String path);
    public boolean removeFile(String path);
    public boolean removeDirectory(String path);
    public boolean appendFile(String path, String content);
}
