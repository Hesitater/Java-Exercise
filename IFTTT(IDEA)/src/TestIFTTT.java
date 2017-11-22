
import javax.xml.soap.Detail;
import java.util.ArrayList;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class TestIFTTT {
    public static void main(String args[]){
        try {

            //------------------------------------------------------------------------------------------------------
            //                              请输入你的Summary.txt与Detail.txt的文件路径
            Summary summary = new Summary("./Summary.txt"); //已输入的路径仅是示例,可按需求自行修改
            MyDetail detail = new MyDetail("./Detail.txt");  //已输入的路径仅是示例,可按需求自行修改
            //------------------------------------------------------------------------------------------------------

            InputHandler inputHandler = new InputHandler();
            inputHandler.handleInput();
            ArrayList<IFTTTContainer> containers = inputHandler.getContainers();
            int i;
            for (i = 0; i < containers.size(); i++) {
                if (containers.get(i).getTrigger() == 0){ //Rename thread
                    RenamedThread thread = new RenamedThread(summary, detail, containers.get(i));
                    thread.start();
                } else if (containers.get(i).getTrigger() == 1){ //modified thread
                    ModifiedThread thread = new ModifiedThread(summary, detail, containers.get(i));
                    thread.start();
                } else if (containers.get(i).getTrigger() == 2){ //path-changed thread
                    PathChangedThread thread = new PathChangedThread(summary, detail, containers.get(i));
                    thread.start();
                } else if (containers.get(i).getTrigger() == 3){ //size-changed thread
                    SizeChangedThread thread = new SizeChangedThread(summary, detail, containers.get(i));
                    thread.start();
                }
            }

            RecordWritingThread recordWritingThread = new RecordWritingThread(summary, detail);
            recordWritingThread.start();

            /*------------------------------------------------------------------------------------------------------
                                            请在此处放入你的测试线程，并启动它
             ------------------------------------------------------------------------------------------------------*/

        } catch (Exception e) {
            System.out.println("Oops. Something unexpected happened.");;
        }
    }
}
