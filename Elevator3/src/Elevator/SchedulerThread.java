package Elevator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import static Elevator.ElevatorTest.initialTime;

public class SchedulerThread extends Thread{
	private ElevatorThread elevatorThread1;
	private ElevatorThread elevatorThread2;
	private ElevatorThread elevatorThread3;
	RequestQueue requestQueue;
	Elevator firstElevator;
	Elevator secondElevator;
	Elevator thirdElevator;
	FileWriter fileWriter;
	
	
	public SchedulerThread(RequestQueue requestQueue, Elevator firstElevator, Elevator secondElevator,
			Elevator thirdElevator, FileWriter fileWriter) {
		super();
		this.elevatorThread1 = new ElevatorThread(firstElevator, fileWriter);
		this.elevatorThread2 = new ElevatorThread(secondElevator, fileWriter);
		this.elevatorThread3 = new ElevatorThread(thirdElevator, fileWriter);
		this.requestQueue = requestQueue;
		this.firstElevator = firstElevator;
		this.firstElevator.setElevatorID(1);
		this.secondElevator = secondElevator;
		this.secondElevator.setElevatorID(2);
		this.thirdElevator = thirdElevator;
		this.thirdElevator.setElevatorID(3);
		this.fileWriter = fileWriter;
	}


	public void run() {
		elevatorThread1.start();
		elevatorThread2.start();
		elevatorThread3.start();
		this.scanRequestQueue();		
	}
	
	public void scanRequestQueue() {
		while (true){
			Request tempRequest = null;
			
			for (int i = 0; i < this.requestQueue.getRequests().size(); ) {
				tempRequest = this.requestQueue.getRequest(i);
				
				if (tempRequest.getType() == "FR") { //请求为FR请求，需按电梯情况指派请求
					if (!this.assignFloorRequest(tempRequest, i)) {
						i++;
					}
				}else{ //请求为ER请求
					this.assignElevatorRequest(firstElevator, tempRequest, i, elevatorThread1);
					this.assignElevatorRequest(secondElevator, tempRequest, i, elevatorThread2);
					this.assignElevatorRequest(thirdElevator, tempRequest, i, elevatorThread3);
				}

			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//检查是否有指定电梯的电梯请求，有则按下相应按钮并向按钮存入请求(还需判断按钮是否被按下)
	public void assignElevatorRequest(Elevator elevator, Request currentRequest, int requestIndex, ElevatorThread elevatorThread) {
		int targetFloor = -1;
		double currentTime = (System.currentTimeMillis() - initialTime) / 1000;
		
		if (((ElevatorRequest)currentRequest).getElevatorNumber() != elevator.getElevatorID()) { //不是当前电梯的请求
			return;
		}else{ //是当前电梯的请求
			targetFloor = ((ElevatorRequest)currentRequest).getTargetFloor();
			
			if (elevator.pressButton(targetFloor)) { //按按钮并判断是否被按下
				elevatorThread.setElevatorButtonRequestList(targetFloor - 1, (ElevatorRequest)currentRequest); //将请求存入按钮
			}else{
				//System.out.println(System.currentTimeMillis() + ":SAME [" + currentRequest + "," + currentRequest.getRequestTime() + "]"); //输出因为重复而被忽略的请求
				try {
					this.fileWriter.write(System.currentTimeMillis() + ":SAME [" + currentRequest + "," + currentRequest.getRequestTime() + "]" + System.getProperty("line.separator"));
					this.fileWriter.flush();
				} catch (IOException e) {
					System.out.println("Oops. Something unexpected happened.");
				}
			}
			this.requestQueue.removeRequest(requestIndex); //将当前请求从队列中删除
		}
	}
	
	//楼层请求队列中有请求，判断当前时刻是否有电梯能响应请求队列中请求
	public boolean assignFloorRequest(Request currentRequest, int requestIndex) {
		ArrayList<Request> requestList = this.requestQueue.getRequests();
		boolean[] canAssign = {false, false, false}; //用以判断三个电梯是否能分配FR请求
		Elevator elevatorToGetFR = null;
		int indexOfElevator = -1;
		Elevator[] elevators = {this.firstElevator, this.secondElevator, this.thirdElevator};
		ElevatorThread[] elevatorThreads = {this.elevatorThread1, this.elevatorThread2, this.elevatorThread3};
			
		//检查各电梯是否能执行FR
		canAssign[0] = this.canGetFloorRequest(firstElevator, (FloorRequest)currentRequest);
		canAssign[1] = this.canGetFloorRequest(secondElevator, (FloorRequest)currentRequest);
		canAssign[2] = this.canGetFloorRequest(thirdElevator, (FloorRequest)currentRequest);

		//判断哪个电梯能抢到FR
		for (int j = 0; j < canAssign.length; j++) {
			if (canAssign[j] == true) {
				if (elevatorToGetFR == null) {
					elevatorToGetFR = elevators[j];
					indexOfElevator = j;
				}else{
					if (elevators[j].getMovingTrend() == 0) { //电梯长期不动，优先级较低
						if (elevatorToGetFR.getMovingTrend() == 0) {
							if (elevators[j].getWorkAmount() < elevatorToGetFR.getWorkAmount()) { //运动量较小则优先执行
								elevatorToGetFR = elevators[j];
								indexOfElevator = j;
							}
						}
					}else if (elevators[j].getUpOrDown() != 0) { //电梯仍然在运动，优先级较高
						if (elevatorToGetFR.getMovingTrend() == 0) {
							elevatorToGetFR = elevators[j];
							indexOfElevator = j;
						}else{
							if (elevators[j].getWorkAmount() < elevatorToGetFR.getWorkAmount()) { //运动量较小则优先执行
								elevatorToGetFR = elevators[j];
								indexOfElevator = j;
							}
						}
					}
				}
			}
		}	
		int targetFloor = -1;
		int tempArg = -1;
		
		if (elevatorToGetFR != null) {
			targetFloor = ((FloorRequest)currentRequest).getFloorNumber();
			
			//用tempArg代表请求为向上或者向下
			if (((FloorRequest)currentRequest).getUpOrDown() == 1) {
				tempArg = 0;
			}else{
				tempArg = 1;
			}
			
			if ((elevatorThreads[indexOfElevator].getFloor(targetFloor).getUpButtonIsPressed() && ((FloorRequest)currentRequest).getUpOrDown() == 1) || 
					(elevatorThreads[indexOfElevator].getFloor(targetFloor).getDownButtonIsPressed() && ((FloorRequest)currentRequest).getUpOrDown() == -1)) {
				//System.out.println(System.currentTimeMillis() + ":SAME [" + currentRequest + "," + currentRequest.getRequestTime() + "]"); //输出因为重复而被忽略的请求
				try {
					this.fileWriter.write(System.currentTimeMillis() + ":SAME [" + currentRequest + "," + currentRequest.getRequestTime() + "]" + System.getProperty("line.separator"));
					this.fileWriter.flush();
				} catch (IOException e) {
					System.out.println("Oops. Something unexpected happened.");
				}
			}else{
				if (tempArg == 0) { //请求为向上
					elevatorThreads[indexOfElevator].getFloor(targetFloor).pressUpButton(); //按下向上按钮
					if (elevators[indexOfElevator].getCurrentFloor() > targetFloor) { //运动方向与请求方向相反则设置为特殊请求
						((FloorRequest)currentRequest).setSpecialRequest();
					}
				}else{ //请求为向下
					elevatorThreads[indexOfElevator].getFloor(targetFloor).pressDownButton(); //按下向下按钮
					if (elevators[indexOfElevator].getCurrentFloor() < targetFloor) { //运动方向与请求方向相反则设置为特殊请求
						((FloorRequest)currentRequest).setSpecialRequest();
					}
				}
				
				elevatorThreads[indexOfElevator].setFloorButtonRequestList(2 * (targetFloor - 1) + tempArg, (FloorRequest)currentRequest); //将请求存入按钮
				requestList.remove(requestIndex); //将当前请求从队列中删除
				return true;
			}
		}
		return false;
	}
	
	//判断指定电梯是否有机会得到指定FR请求（可能与其他电梯产生竞争）
	public boolean canGetFloorRequest(Elevator elevator, FloorRequest request) {
		if (elevator.getCurrentFloor() == request.getFloorNumber() - 1 && elevator.getUpOrDown() == 1 && request.getUpOrDown() == 1) {
			return true;
		}else if (elevator.getCurrentFloor() == request.getFloorNumber() + 1 && elevator.getUpOrDown() == -1 && request.getUpOrDown() == -1) {
			return true;
		}else if (elevator.getMovingTrend() == 0) {
			return true;
		}
		
		return false;
	}
}
