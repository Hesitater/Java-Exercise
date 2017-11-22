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

public class MapReader {
    /* Overview: 地图文件读取类，可将存储地图信息的文件转化为可存储在对象里的地图信息
     */

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    public CityMap changeToMap(File file){
        /* @ REQUIRES: file != null;
        @ MODIFIES: None;
        @ EFFECTS: 提取文件信息得到CityMap对象; exceptional_behavior(Exception) 提示错误并退出程序; exceptional_behavior(Exception) 提示错误并退出程序;
        */

        if (file.exists()) {
            int[][] oldMap = new int[MAXORDER][MAXORDER];
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
                                    if (tempInt >= 0 && tempInt <= 3) {
                                        oldMap[i][j] = tempInt;
                                    } else {
                                        System.out.println("Number must between 0 and 3. Program ends.");
                                        System.exit(0);
                                    }
                                }catch (Exception e){
                                    System.out.println(line.charAt(j) + "isn't a integer. Program ends.");
                                    System.exit(0);
                                }
                            }
                        }else{ //列数不是80
                            System.out.println("Map need to have 80 column. Program ends.");
                            System.exit(0);
                        }

                    }catch (Exception e){
                        System.out.println("Map need to have 80 row. Program ends.");
                        System.exit(0);
                    }
                }

                CityMap map = new CityMap(oldMap);
                map.convertMap();
                if (map.checkValidity()){
                    return map;
                }else{
                    System.out.println("Map invalid. Program ends.");
                    System.exit(0);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Scanner error!");
                //e.printStackTrace();
            }
        }else{
            System.out.println("File doesn't exist. Program ends.");
            System.exit(0);
        }
        return null;
    }
}
