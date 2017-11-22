
import java.util.LinkedList;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class FileNode extends FileInfo{
    private LinkedList<FileNode> childList;
    private FileNode parent;

    public FileNode(String path, LinkedList<FileNode> childList, FileNode parent) {
        super(path);
        this.childList = childList;
        this.parent = parent;
    }
}
