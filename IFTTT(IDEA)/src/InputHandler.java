
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class InputHandler {
    private ArrayList<IFTTTContainer> containers;

    public InputHandler() {
        this.containers = new ArrayList<IFTTTContainer>();
    }

    public void handleInput(){
        String inputLine;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                inputLine = scanner.nextLine();
                if (inputLine.equals("Go")) {
                    break;
                }
                String[] stringList = inputLine.split(" ");
                if (!this.combineContainer(stringList)) {
                    IFTTTContainer container = new IFTTTContainer();

                    if (new MyFile(stringList[1]).exists()) {
                        container.setPath(stringList[1]);
                    } else {
                        System.out.println("File/Directory input error!");
                        continue;
                    }

                    if (stringList[2].equals("renamed")) {
                        container.setTrigger(0);
                    } else if (stringList[2].equals("modified")) {
                        container.setTrigger(1);
                    } else if (stringList[2].equals("path-changed")) {
                        container.setTrigger(2);
                    } else if (stringList[2].equals("size-changed")) {
                        container.setTrigger(3);
                    } else {
                        System.out.println("Trigger input error!");
                        continue;
                    }

                    if (stringList[4].equals("record-summary")) {
                        container.setAction(0);
                    } else if (stringList[4].equals("record-detail")) {
                        container.setAction(1);
                    } else if (stringList[4].equals("recover")) {
                        if (container.getTrigger() == 0 || container.getTrigger() == 2) { //recover只能对应renamed与size-changed
                            container.setAction(2);
                        } else {
                            System.out.println("Recover can only match renamed & size-changed!");
                            continue;
                        }
                    } else {
                        System.out.println("Action input error!");
                        continue;
                    }

                    containers.add(container);
                }
            }catch (Exception e){
                System.out.println("Input exception.");
            }
        }
    }

    //若触发器与监测路径，则合并指令
    public boolean combineContainer(String[] stringList){
        if (this.containers.size() != 0) {
            for (int i = 0; i < this.containers.size(); i++) {
                if (stringList[1].equals(this.containers.get(i))) {
                    if ((stringList[2].equals("renamed") && this.containers.get(i).getTrigger() == 0) ||
                            (stringList[2].equals("modified") && this.containers.get(i).getTrigger() == 1) ||
                            (stringList[2].equals("path-changed") && this.containers.get(i).getTrigger() == 2) ||
                            (stringList[2].equals("size-changed") && this.containers.get(i).getTrigger() == 3)) {
                        if (stringList[4].equals("record-summary")) {
                            this.containers.get(i).setAction(0);
                        } else if (stringList[4].equals("record-detail")) {
                            this.containers.get(i).setAction(1);
                        } else if (stringList[4].equals("recover")) {
                            this.containers.get(i).setAction(2);
                        } else {
                            System.out.println("Action input error!");
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public ArrayList<IFTTTContainer> getContainers(){
        return this.containers;
    }
}
