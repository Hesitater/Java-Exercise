package FirstTaxi;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import static java.lang.Math.abs;

/**
 * Created by ${USER} on ${DATE}.
 * OO doesn't allow me to tell u my great name.
 * I'm sorry about that.
 */

public class ScanServerThread extends Thread implements ScannerInterface{
    /* Overview: 扫描服务器线程，扫描所在地周围4x4范围内在时间窗内所有可相应的出租车，并能最终决定接客车
     */

    private int[] clientLocation;
    private int[] clientDestination;
    private final int DUATION = 3000; //单位ms
    private final int SCANWIDTH = 2;
    private Taxi[] taxis;
    private ArrayList<Taxi> probableTaxis;
    private FileWriter filewriter;
    private String content;
    private Request request;
    private GraphSearch graphSearcher;
    private CityMap map;
    private String probableTaxiContent;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        return true;
    }

    public ScanServerThread(int[] clientLocation, int[] clientDestination, Taxi[] taxis, Request request, FileWriter filewriter, CityMap map) {
        /* @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性;
        */

        this.clientLocation = clientLocation;
        this.clientDestination = clientDestination;
        this.taxis = taxis;
        this.probableTaxis = new ArrayList<Taxi>();
        this.filewriter = filewriter;
        this.request = request;
        this.content = System.getProperty("line.separator") + request + System.getProperty("line.separator");
        this.graphSearcher = new GraphSearch(map.getNewMap());
        this.map = map;
        this.probableTaxiContent = "";
    }

    public void run(){
        /* @ MODIFIES: this.fileWriter, this.probableTaxis, this.content;;
        @ EFFECTS: 扫描各个出租车状态并找到可相应请求的出租车; exceptional_behavior(InterruptedException) 提示错误并退出程序;
        */

        double startTime = System.currentTimeMillis();

        //输出请求时刻4x4内出租车信息
        this.scan();
        if (this.probableTaxis.size() != 0){
            this.content = this.content + "Taxis that can get order at beginning:" + System.getProperty("line.separator") + this.probableTaxiContent;
        }else{
            this.content = this.content + "No taxi available at beginning." + System.getProperty("line.separator");
        }

        //开始为期3s的扫描
        double currentTime = System.currentTimeMillis();
        while (currentTime - startTime < DUATION){
            this.scan();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("ScanServerThread sleep error");
                System.exit(0);
                //e.printStackTrace();
            }
            currentTime = System.currentTimeMillis();
        }

        //扫描结束，输出抢单时间窗内抢单出租车
        if (this.probableTaxis.size() != 0) {
            this.content = this.content + "Taxis that have scrambled the order:" + "\n" + this.probableTaxiContent;
        }

        //开始选择出租车接单
        while (true) {
            if (this.probableTaxis.size() != 0) { //统计到有出租车能响应(但现在可能无法响应)

                //删除当前无法响应的出租车
                for (int i = 0; i < probableTaxis.size(); i++) {
                    if (probableTaxis.get(i).getState() != 0) {
                        probableTaxis.remove(i);
                    }
                }

                if (probableTaxis.size() != 0) { //现在有出租车能响应(弱一定能响应)
                    Taxi priorTaxi = null;

                    for (int i = 0; i < probableTaxis.size(); i++) {
                        if (i == 0) {
                            priorTaxi = probableTaxis.get(i);
                        } else {
                            priorTaxi = checkPriority(priorTaxi, probableTaxis.get(i));
                        }
                    }

                    if (!priorTaxi.achieveOrder(request)) {
                        continue; //该出租车在寻找最优者时更改了状态
                    }

                    this.content = this.content + "Taxi to get order: " + System.getProperty("line.separator") +
                            priorTaxi + System.getProperty("line.separator"); //输出接单出租车
                } else {
                    this.content = this.content + "No taxi can get order now." + System.getProperty("line.separator"); //输出当前无可接单出租车
                }
            } else {
                this.content = this.content + "No taxi can get order now." + System.getProperty("line.separator"); //输出当前无可接单出租车
            }

            this.flushOutput();
            break;
        }
    }

    public void flushOutput(){
        /* @ MODIFIES: this.fileWriter, this.content;
        @ EFFECTS: 将this.content写入this.fileWriter并清空this.content; exceptional_behavior(IOException) 提示错误并退出程序;
        */

        try {
            this.filewriter.write(this.content);
            this.filewriter.flush();
            this.content = "";
        } catch (IOException e) {
            System.out.println("File writer error.");
            System.exit(0);
            //e.printStackTrace();
        }
    }

    public Taxi checkPriority(Taxi taxiA, Taxi taxiB){
        /* @ REQUIRES: (taxiA != null) & (taxiB != null);
        @ MODIFIES: None;
        @ EFFECTS: 选出taxiA和taxiB中优先级高的车并返回;
        */

        int clientMapIndex = this.map.getIndexByCoordinate(this.clientLocation[0], this.clientLocation[1]);
        int taxiAMapIndex = this.map.getIndexByCoordinate(taxiA.getLocation()[0], taxiA.getLocation()[1]);
        int taxiBMapIndex = this.map.getIndexByCoordinate(taxiB.getLocation()[0], taxiB.getLocation()[1]);

        if (taxiB.getCredit() > taxiA.getCredit()){ //信用高则优先
            return taxiB;
        }else if (taxiB.getCredit() == taxiA.getCredit()){ //信用相同
            int taxiBDistance = this.graphSearcher.findPath(clientMapIndex, taxiBMapIndex).size();
            int taxiADistance = this.graphSearcher.findPath(clientMapIndex, taxiAMapIndex).size();

            if ( taxiBDistance < taxiADistance){ //距离近则优先
                return taxiB;
            }else if (taxiBDistance == taxiADistance){ //距离相同则随机
            }
        }
        return taxiA;
    }

    @Override
    public void scan() {
        /* @ MODIFIES: this.probableTaxiContent, this.probableTaxi;
        @ EFFECTS: 扫描各个出租车状态并列出所有可相应请求的出租车;
        */

        for (int i = 0; i < taxis.length; i++) {
            if (abs(taxis[i].getLocation()[0] - this.clientLocation[0]) <= SCANWIDTH &&
                    abs(taxis[i].getLocation()[1] - this.clientLocation[1]) <= SCANWIDTH){
                if (!this.probableTaxis.contains(taxis[i])){
                    this.probableTaxiContent = this.probableTaxiContent + taxis[i] + System.getProperty("line.separator");
                    taxis[i].scrambleOrder();
                    this.probableTaxis.add(taxis[i]);
                }
            }
        }
    }
}
