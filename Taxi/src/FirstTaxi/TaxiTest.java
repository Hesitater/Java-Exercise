package FirstTaxi;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

class mapInfo{
    int[][] map=new int[80][80];
    public void readmap(String path){//读入地图信息
        //Requires: path != null;
        //Modifies: System.out,map[][]
        //Effects: 从文件中读入地图信息，储存在map[][]中
        Scanner scan=null;
        File file=new File(path);
        if(file.exists()==false){
            System.out.println("地图文件不存在,程序退出");
            System.exit(1);
            return;
        }
        try {
            scan = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.exit(1);
        }
        for(int i=0;i<80;i++){
            String line = null;
            String[] strArray = null;
            try{
                line = scan.nextLine().replace(" ", "");
                strArray = line.split("");
            }catch(Exception e){
                System.out.println("地图文件信息有误，程序退出");
                System.exit(1);
            }
            for(int j=0;j<80;j++){
                try{
                    this.map[i][j]=Integer.parseInt(strArray[j]);
                }catch(Exception e){
                    System.out.println("地图文件信息有误，程序退出");
                    System.exit(1);
                }
            }
        }
        scan.close();
    }
}

public class TaxiTest {
    /* Overview: 工程测试类
     */

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    static TaxiGUI gui;
    static final int MAXORDER = 80;
    static final int MAXTAXICOUNT = 100;

    public static void main(String args[]) {
        /* @ MODIFIES: gui;
        @ EFFECTS: 主函数，程序入口；exceptional_behavior(Exception) 提示错误并退出程序;
        */

        try{
            gui=new TaxiGUI();
            mapInfo mi=new mapInfo();
            mi.readmap("map.txt");//在这里设置地图文件路径
            gui.LoadMap(mi.map, 80);

            Taxi[] taxis = new Taxi[MAXTAXICOUNT];
            RequestQueue requestQueue = new RequestQueue();

            //获取城市地图
            CityMap map;
            File mapFile = new File("map.txt");
            MapReader reader = new MapReader();
            map = reader.changeToMap(mapFile);

            //获取红绿灯分布图
            File lightFile = new File("LightDistribution.txt");
            LightReader lightReader = new LightReader();
            lightReader.arrangeLights(lightFile, map);

            //红绿灯控制线程开始运行
            LightControllerThread lightControllerThread = new LightControllerThread(map);
            lightControllerThread.start();


            try {
                FileWriter fileWriter = new FileWriter("Output.txt", true);
                FileWriter pathWriter = new FileWriter("Path.txt", true);

                //初始化100个出租车对象并让出租车线程运行
                initializeTaxis(taxis, map, pathWriter);
                //控制器线程开始运行
                ControllerThread controllerThread = new ControllerThread(taxis, requestQueue, fileWriter, map);
                controllerThread.start();
            } catch (IOException e) {
                System.out.println("File writer error. Program ends.");
                System.exit(0);
                //e.printStackTrace();
            }

            //流量监控线程开始运行
            FluxMonitorThread fluxMonitorThread = new FluxMonitorThread(map);
            fluxMonitorThread.start();

            //输入处理线程开始运行
            InputHandlerThread inputHandlerThread = new InputHandlerThread(requestQueue, map);
            inputHandlerThread.start();


            //建立TaxiAPI对象taxiAPI，供测试线程查询出租车信息使用
            TaxiAPI taxiAPI = new TaxiAPI(taxis);
            //--------------------------------------------------------------------------------------------------
            //                                      请在此处加入测试线程
            //--------------------------------------------------------------------------------------------------

        }catch(Exception e){
            System.out.println("Oops, something unexpected happened!");
            System.exit(0);
        }
    }

    public static Taxi[] initializeTaxis(Taxi[] taxis, CityMap map, FileWriter pathWriter){
        /* @ REQUIRES: taxis != null & map != null & pathWriter != null;
        @ MODIFIES: taxis, pathWriter, gui;
        @ EFFECTS: 初始化70辆普通出租车与30辆可追踪出租车，并使他们运行起来；
        */

        Random random = new Random();

        for (int i = 0; i < MAXTAXICOUNT; i++) {
            if (i < 70) {
                Taxi taxi = new Taxi(random.nextInt(4), random.nextInt(80),
                        random.nextInt(80), i + 1, map, pathWriter);
                taxis[i] = taxi;
                TaxiThread taxiThread = new TaxiThread(taxi);
                gui.SetTaxiType(i + 1, taxi.type);
                gui.SetTaxiStatus(taxi.getTaxiNo(), new Point(taxi.getLocation()[0], taxi.getLocation()[1]), 2);
                taxiThread.start();
            }else{
                TraceableTaxi taxi = new TraceableTaxi(random.nextInt(4), random.nextInt(80),
                        random.nextInt(80), i + 1, map, pathWriter);
                taxis[i] = taxi;
                TaxiThread taxiThread = new TaxiThread(taxi);
                gui.SetTaxiType(i + 1, taxi.type);
                gui.SetTaxiStatus(taxi.getTaxiNo(), new Point(taxi.getLocation()[0], taxi.getLocation()[1]), 2);
                taxiThread.start();
            }
        }

        return taxis;
    }
}
