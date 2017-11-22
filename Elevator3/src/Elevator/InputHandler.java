package Elevator;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static Elevator.ElevatorTest.initialTime;

public class InputHandler {
	private FileWriter fileWriter;
	
	public InputHandler(FileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}
	
	public void handleInput(RequestQueue requestQueue) {
		//try {
		Scanner scanner = new Scanner(System.in);
		String inputLine = scanner.nextLine();
		inputLine = inputLine.replaceAll(" ", "");
		
		Pattern floorRequestPattern = Pattern.compile("\\(FR\\,\\+?\\d+\\,(UP|DOWN)\\)");
		Matcher floorRequestMatcher;
		Pattern elevatorRequestPattern = Pattern.compile("\\(ER\\,\\#\\+?\\d+\\,\\+?\\d+\\)");
		Matcher elevatorRequestMatcher;
		String[] requestStrings;
		String[] tempStringList;
		
		//输入"Finish"则结束程序
		Request tempRequest;
		double currentRequestTime;
		double lastRequestTime = 0;
		int lastRequestRank = 0;
		while (true){ //一行行检查输入
			currentRequestTime = System.currentTimeMillis() - initialTime;
			currentRequestTime = formatRequestTime(currentRequestTime);
			requestStrings = inputLine.split(";");
			
			//逐个检查一行中的请求
			for (int i = 0, requestAmount = 0; i < requestStrings.length; i++) {
				
				if (requestAmount == 10) { //同一时刻的请求数大于10
					//System.out.println("Amount of requests at the same time can't be more than 10!");
					try {
						this.fileWriter.write("Amount of requests in one line can't be more than 10!" + System.getProperty("line.separator"));
						this.fileWriter.flush();
					} catch (IOException e) {
						System.out.println("Oops. Something unexpected happened.");
					}
					break;
				}
				
				floorRequestMatcher = floorRequestPattern.matcher(requestStrings[i]);
				elevatorRequestMatcher = elevatorRequestPattern.matcher(requestStrings[i]);
				
				if (floorRequestMatcher.matches()) { //FR型输入(未确认楼层数是否合法)
					tempStringList = floorRequestMatcher.group().split(",");
					tempStringList[0] = tempStringList[0].replace("(", "");
					tempStringList[2] = tempStringList[2].replace(")", "");
					if (Double.parseDouble(tempStringList[1]) > 20 || Double.parseDouble(tempStringList[1]) < 1) { //判断楼层数是否越界
						printInputWarning(requestStrings[i], currentRequestTime);
					}else{
						if (tempStringList[2].equals("UP")) {
							tempRequest = new FloorRequest("FR", Integer.parseInt(tempStringList[1]), 1, formatRequestTime(currentRequestTime));
						}else{
							tempRequest = new FloorRequest("FR", Integer.parseInt(tempStringList[1]), -1, formatRequestTime(currentRequestTime));
						}
						
						//限制1楼不能下，20楼不能上
						if (((FloorRequest)tempRequest).getFloorNumber() == 1 && ((FloorRequest)tempRequest).getUpOrDown() == -1) {
							printInputWarning(requestStrings[i], currentRequestTime);
							inputLine = scanner.nextLine();
							inputLine = inputLine.replaceAll(" ", "");
							continue;
						}else if (((FloorRequest)tempRequest).getFloorNumber() == 20 && ((FloorRequest)tempRequest).getUpOrDown() == 1){
							printInputWarning(requestStrings[i], currentRequestTime);
							inputLine = scanner.nextLine();
							inputLine = inputLine.replaceAll(" ", "");
							continue;
						}
						
						lastRequestRank = setCurrentRequestRank(tempRequest, lastRequestRank, lastRequestTime); //处理同时刻请求排序（不同行的输入也可能同时刻）
						lastRequestTime = tempRequest.getRequestTime();
						tempRequest.setRequestTime(currentRequestTime);
						requestQueue.addRequest(tempRequest);
						requestAmount++;
					}
				}else if (elevatorRequestMatcher.matches()) { //ER型输入(未确认楼层数是否合法)
					tempStringList = elevatorRequestMatcher.group().split(",");
					tempStringList[0] = tempStringList[0].replace("(", "");
					tempStringList[2] = tempStringList[2].replace(")", "");
					tempStringList[1] = tempStringList[1].replace("#", "");
					if (Double.parseDouble(tempStringList[2]) > 20 || Double.parseDouble(tempStringList[2]) < 1) { //判断楼层数是否越界
						printInputWarning(requestStrings[i], currentRequestTime);
					}else if (Double.parseDouble(tempStringList[1]) > 3 || Double.parseDouble(tempStringList[1]) < 1) { //判断电梯号是否越界
						printInputWarning(requestStrings[i], currentRequestTime);
					}else{
						tempRequest = new ElevatorRequest("ER", Integer.parseInt(tempStringList[2]), Integer.parseInt(tempStringList[1]),formatRequestTime(currentRequestTime));
						lastRequestRank = setCurrentRequestRank(tempRequest, lastRequestRank, lastRequestTime); //处理同时刻请求排序（不同行的输入也可能同时刻）
						lastRequestTime = tempRequest.getRequestTime();
						tempRequest.setRequestTime(currentRequestTime);
						requestQueue.addRequest(tempRequest);
						requestAmount++;
					}
				}else{
					printInputWarning(requestStrings[i], currentRequestTime);
				}
			}
			
			inputLine = scanner.nextLine();
			inputLine = inputLine.replaceAll(" ", "");
		}
		
		//scanner.close();
	//} catch (Exception e) {
		//System.out.println("Oops. Something unexpected happened.");
	//}
		//System.exit(1);
	}
	
	//处理同时刻请求排序,返回当前排序位
	public int setCurrentRequestRank(Request request, int lastRequestRank, double lastRequestTime) {
		if (request.getRequestTime() == lastRequestTime) {
			request.setRequestRank(lastRequestRank + 1);
			lastRequestRank++;
		}else{
			request.setRequestRank(0);
			lastRequestRank = 0;
		}
		return lastRequestRank;
	}
	
	//输出错误请求的提示
	public String printInputWarning(String invalidRequest, double requestTime) {
		double systemTime = System.currentTimeMillis();
		BigDecimal bigDecimal = new BigDecimal(systemTime);
		String systemTimeString = bigDecimal.toPlainString();
		//System.out.println(systemTimeString + ":INVALID [" + invalidRequest + "," + requestTime + "]");
		try {
			this.fileWriter.write(systemTimeString + ":INVALID [" + invalidRequest + "," + requestTime + "]" + System.getProperty("line.separator"));
			this.fileWriter.flush();
		} catch (IOException e) {
			System.out.println("Oops. Something unexpected happened.");
		}
		
		return systemTimeString;
	}
	
	//转化毫秒为秒并保留一位小数
	public double formatRequestTime(double requestTime) {
		requestTime = requestTime / 1000;
		BigDecimal bigDecimal = new BigDecimal(requestTime);
		requestTime = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		return requestTime;
	}
}
