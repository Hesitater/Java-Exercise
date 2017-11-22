package FirstTaxi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static FirstTaxi.TaxiTest.MAXORDER;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class LightReader {
    /* Overview: 红绿灯文件读取类，可将存储红绿灯信息的文件转化为可存储在对象里的红绿灯信息
     */

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    public void arrangeLights(File file, CityMap map){
        /* @ REQUIRES: (file != null) & (map != null);
        @ MODIFIES: map;
        @ EFFECTS: 提取文件信息得到红绿灯状态图,并利用状态图初始化所有红绿灯状态; exceptional_behavior(Exception) 提示错误并退出程序;
        */

        if (file.exists()) {
            int[][] lightDistribution = new int[MAXORDER][MAXORDER];
            try {
                Scanner scanner = new Scanner(file);
                String line;
                String[] strings;
                for (int i = 0; i < MAXORDER; i++) {
                    try{
                        line = scanner.nextLine();
                        line = line.replaceAll("\\s", "");
                        if (line.length() == MAXORDER){
                            for (int j = 0; j < line.length(); j++) {
                                try {
                                    int tempInt = Integer.parseInt(String.valueOf(line.charAt(j)));
                                    if (tempInt == 0 || tempInt == 1) {
                                        lightDistribution[i][j] = tempInt;
                                    }else{
                                        System.out.println("Number must be 0 or 1. Program ends.");
                                        System.exit(0);
                                    }
                                }catch (Exception e){
                                    System.out.println(line.charAt(j) + "isn't a integer. Program ends.");
                                    System.exit(0);
                                }
                            }
                        }else{ //每列元素个数必须达到80
                            System.out.println("One line must have 80 elements. Program ends.");
                            System.exit(0);
                        }

                    }catch (Exception e){
                        System.out.println("Input need to have 80 row. Program ends.");
                        System.exit(0);
                    }
                }

                map.initializeTrafficLights(lightDistribution);
            } catch (FileNotFoundException e) {
                System.out.println("Scanner error!");
                //e.printStackTrace();
            }
        }else{
            System.out.println("File doesn't exist. Program ends.");
            System.exit(0);
        }
    }
}
