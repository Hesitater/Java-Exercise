package Elevator;

import java.util.ArrayList;

//根据输入序列，对电梯进行模拟调度
public class Simulator{
	protected double time;
	protected Elevator elevator;
	protected Floor[] floors;
	protected RequestQueue requestQueue;
	protected ArrayList<Request> requestList;	
	
	public Simulator(RequestQueue requestQueue) {
		this.time = 0;
		this.elevator = new Elevator(1,1);
		this.floors = new Floor[20];
		this.requestQueue = requestQueue;
		this.requestList = requestQueue.getRequests();
		
		//初始化楼层信息
		for (int i = 0; i < this.floors.length; i++) {
			if (i == 0) {
				this.floors[i] = new Floor(i + 1);
			}else{
				this.floors[i] = new Floor(i + 1);
			}
		}
	}
	
	//模拟器启动入口
	public void simulate() {
		while (!this.requestList.isEmpty()){
			if (((Request)this.requestList.get(0)).getRequestTime() > this.time) { //电梯长久未动则直接将模拟时间调为当前指令时间
				this.time = ((Request)this.requestList.get(0)).getRequestTime();
			}
			this.executeRequest((Request)this.requestList.get(0));
			this.requestList.remove(0);
		}
	}
	
	//执行当前请求
	public void executeRequest(Request request) {
		this.elevator.initializeButtonsState(); //初始化电梯的所有按钮
		
		//初始化所有楼层按钮
		for (int i = 0; i < this.floors.length; i++) {
			this.floors[i].initializeButtons();
		}
		
		if (request.getType().equals("FR")) {
			//该请求执行时，其指定的向上或向下按钮被按下
			if (((FloorRequest)request).getUpOrDown() == 1) {
				this.floors[((FloorRequest)request).getFloorNumber() - 1].pressUpButton();
			}else{
				this.floors[((FloorRequest)request).getFloorNumber() - 1].pressDownButton();
			}
			
			this.elevator.depart(((FloorRequest) request).getFloorNumber(), this.time); //电梯离开当前楼层或保持不动仅开关门
		}else{
			this.elevator.pressButton(((ElevatorRequest) request).getTargetFloor()); //该请求执行时，其指定的楼层号按钮被按下
			this.elevator.depart(((ElevatorRequest) request).getTargetFloor(), this.time); //电梯离开当前楼层或保持不动仅开关门
		}
		
		this.time = this.elevator.getTargetTime() + 1; //得到电梯到达并开关门完毕的时刻
	
		
		//输出结果
		double tempTime;
		if (request.getType().equals("ER")) { //ER类请求的输出
			if (this.elevator.getUpOrDown() == 0) { //电梯保持不动
				System.out.println("(" + this.elevator.getCurrentFloor(this.time) + "," + "STILL" + "," + this.time + ")");
			}else if (this.elevator.getUpOrDown() == 1) { //电梯向上走
				tempTime = this.time - 1;
				System.out.println("(" + this.elevator.getCurrentFloor(this.time) + "," + "UP" + "," + tempTime + ")");
			}else{ //电梯向下走
				tempTime = this.time - 1;
				System.out.println("(" + this.elevator.getCurrentFloor(this.time) + "," + "DOWN" + "," + tempTime + ")");
			}
		}else{ //FR类请求的输出
			if (((FloorRequest)request).getFloorNumber() == this.elevator.getCurrentFloor(this.time - 1 - this.elevator.getTargetTime())) { //电梯保持不动
				System.out.println("(" + this.elevator.getCurrentFloor(this.time) + "," + "STILL" + "," + this.time + ")");
			}else if (this.elevator.getUpOrDown() == 1) { //电梯向上走
				tempTime = this.time - 1;
				System.out.println("(" + this.elevator.getCurrentFloor(this.time) + "," + "UP" + "," + tempTime + ")");
			}else{ //电梯向下走
				tempTime = this.time - 1;
				System.out.println("(" + this.elevator.getCurrentFloor(this.time) + "," + "DOWN" + "," + tempTime + ")");
			}
		}

		Request tempRequest;
		int tempFloorNumber;
		//去除开关门完毕前重复的请求
		if (this.requestList.size() > 1) {
			for (int i = 1; i < this.requestList.size() && ((Request)this.requestList.get(i)).getRequestTime() <= this.time;) { 
				tempRequest = (Request)this.requestList.get(i);
				
				if (tempRequest.getType().equals("FR")){ //去除开关门完毕前，楼层重复请求
					tempFloorNumber = ((FloorRequest)tempRequest).getFloorNumber();
					if (this.floors[tempFloorNumber - 1].getUpButtonIsPressed() && ((FloorRequest)tempRequest).getUpOrDown() == 1) {
						System.out.println("Request is ignored for duplication: " + this.requestList.get(i)); //输出因为重复而被忽略的请求
						this.requestList.remove(i);
						continue;
					}else if (this.floors[tempFloorNumber - 1].getDownButtonIsPressed() && ((FloorRequest)tempRequest).getUpOrDown() == -1) {
						this.requestList.remove(i);
						continue;
					}else if (((FloorRequest)tempRequest).getUpOrDown() == 1) {
						this.floors[((FloorRequest)tempRequest).getFloorNumber() - 1].pressUpButton();
					}else{
						this.floors[((FloorRequest)tempRequest).getFloorNumber() - 1].pressDownButton();
					}
				}else{ //去除开关门完毕前，电梯内重复请求
					tempFloorNumber = ((ElevatorRequest)tempRequest).getTargetFloor();
					if (!this.elevator.pressButton(tempFloorNumber)) {
						System.out.println("Request is ignored for duplication: " + this.requestList.get(i)); //输出因为重复而被忽略的请求
						this.requestList.remove(i);
						continue;
					}
				}
				i++;
			}
		}
	}
}
