package Elevator;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ALSSimulator extends Simulator{
	protected ArrayList<ElevatorRequest> elevatorButtonRequestList; //电梯内按钮存储请求所用数组
	protected ArrayList<FloorRequest> floorButtonRequestList; //楼层按钮存储请求所用数组，共20个，以2个为单位，前一个代表上按钮，后一个代表下按钮
	
	public ALSSimulator(RequestQueue requestQueue) {
		super(requestQueue);
		Request tempRequest;
		
		floorButtonRequestList = new ArrayList<>();
		for (int i = 0; i < 2 * floors.length; i++) { 
			tempRequest = new Request("FR", 0);
			floorButtonRequestList.add(null);
		}
		
		elevatorButtonRequestList = new ArrayList<>();
		for (int i = 0; i < floors.length; i++) {
			tempRequest = new Request("ER", 0);
			elevatorButtonRequestList.add(null);
		}
	}
	
	//遍历请求队列检查当前时间是否有请求,有则按下对应按钮，返回值为获取到的请求个数
	public int checkCurrentRequestCount(int lastRequestIndex) {
		int currentRequestIndex = lastRequestIndex + 1;
		
		//请求队列已经读完
		if (currentRequestIndex == this.requestList.size()) {
			return 0;
		}
		
		Request currentRequest = this.requestList.get(currentRequestIndex);
		double currentRequestTime = currentRequest.getRequestTime();
		int targetFloor;
		int loopCount = 0;
		int tempArg;
		while (currentRequestTime == this.time) {
			if (currentRequest.getType().equals("ER")) { //该请求为ER类请求
				targetFloor = ((ElevatorRequest)currentRequest).getTargetFloor();
				if (this.elevator.pressButton(targetFloor)) { //按按钮并判断是否被按下
					currentRequest.setRequestRank(loopCount); //设置同时间请求的先后次序
					this.elevatorButtonRequestList.set(targetFloor - 1, (ElevatorRequest)currentRequest); //将请求存入按钮
				}else{
					System.out.println("Request is ignored for duplication: " + currentRequest); //输出因为重复而被忽略的请求
				}
			}else{ //该请求为FR类请求
				targetFloor = ((FloorRequest)currentRequest).getFloorNumber();
				
				//用tempArg代表上或者下
				if (((FloorRequest)currentRequest).getUpOrDown() == 1) {
					tempArg = 0;
				}else{
					tempArg = 1;
				}
				
				if ((this.floors[targetFloor - 1].getUpButtonIsPressed() && ((FloorRequest)currentRequest).getUpOrDown() == 1) || 
						(this.floors[targetFloor - 1].getDownButtonIsPressed() && ((FloorRequest)currentRequest).getUpOrDown() == -1)) {
					System.out.println("Request is ignored for duplication: " + currentRequest); //输出因为重复而被忽略的请求
				}else{
					if (tempArg == 0) {
						this.floors[targetFloor - 1].pressUpButton(); //按下向上按钮
					}else{
						this.floors[targetFloor - 1].pressDownButton(); //按下向下按钮
					}
					currentRequest.setRequestRank(loopCount); //设置同时间请求的先后次序
					this.floorButtonRequestList.set(2 * (targetFloor - 1) + tempArg, (FloorRequest)currentRequest); //将请求存入按钮
				}
			}
			
			currentRequestIndex++;
			if (currentRequestIndex == this.requestList.size()) {
				break;
			}
			currentRequest = (Request)this.requestList.get(currentRequestIndex);
			currentRequestTime = currentRequest.getRequestTime();
			loopCount++;
		}
		
		return (currentRequestIndex - lastRequestIndex - 1);
	}
	
	//判断电梯内是否有同趋势请求
	public boolean haveSameTrendRequest(int currentFloor, int upOrDown) {
		if (upOrDown == 1) {
			for (int i = currentFloor + 1; i <= floors.length; i++) {
				if (this.elevator.getButtonState(i)) {
					return true;
				}
			}
		}else if (upOrDown == -1) {
			for (int i = currentFloor - 1; i >= 1; i--) {
				if (this.elevator.getButtonState(i)) {
					return true;
				}
			}
		}
		
		return false;
	}

	//寻找被按下按钮中存储的请求中时间最靠前的请求(时间相同则编号最小)
	public Request findFirstRequest(){
		Request firstRequest = null;
		Request tempRequest;
		Request tempUpRequest;
		Request tempDownRequest;
		
		//挨个判断电梯内请求
		for (int i = 0; i < this.floors.length; i++) {
			tempRequest = (Request)this.elevatorButtonRequestList.get(i);
			
			if (tempRequest == null) { //直接跳过空请求
				continue;
			}
			
			if (firstRequest == null) { //第一个请求为空，则当前请求必是当前最靠前（当前请求也可能为空）
				firstRequest = tempRequest;
				continue;
			}
			
			//某个电梯号码按钮被按下
			if (this.elevator.getButtonState(i + 1)) {
				if (tempRequest.getRequestTime() < firstRequest.getRequestTime()) { //判断时间先后
					firstRequest = this.elevatorButtonRequestList.get(i);
				}else if (tempRequest.getRequestTime() == firstRequest.getRequestTime()) {
					if (tempRequest.getRequestRank() < firstRequest.getRequestRank()) { //判断序号先后
						firstRequest = this.elevatorButtonRequestList.get(i);
					}
				}
			}
		}
		
		//挨个判断楼层请求
		for (int i = 0; i < this.floors.length; i++) {
			tempUpRequest = this.floorButtonRequestList.get(2 * i);
			tempDownRequest = this.floorButtonRequestList.get(2 * i + 1);
			
			if (firstRequest == null) { //第一个请求为空，则当前请求必是当前最靠前（当前请求也可能为空）
				if (tempUpRequest != null) {
					firstRequest = tempUpRequest;
					
					//tempDownRequest不为空，则拿它与firstRequest比较
					if (tempDownRequest != null){
						if (tempDownRequest.getRequestTime() < firstRequest.getRequestTime()) { //判断时间先后
							firstRequest = tempDownRequest;
						}else if (tempDownRequest.getRequestTime() == firstRequest.getRequestTime()) { //时间相同
							if (tempDownRequest.getRequestRank() < firstRequest.getRequestRank()) { //判断序号先后
								firstRequest = tempDownRequest;
							}
						}
					}
				}else{
					firstRequest = tempDownRequest;
				}
				
				continue;
			}
			
			//某个楼层向上按钮被按下
			if (this.floors[i].getUpButtonIsPressed()) {
				
				if (tempUpRequest == null) { //直接跳过空请求
					continue;
				}
				
				if (tempUpRequest.getRequestTime() < firstRequest.getRequestTime()) { //判断时间先后
					firstRequest = this.floorButtonRequestList.get(2 * i);
				}else if (tempUpRequest.getRequestTime() == firstRequest.getRequestTime()) {
					if (tempUpRequest.getRequestRank() < firstRequest.getRequestRank()) { //判断序号先后
						firstRequest = this.floorButtonRequestList.get(2 * i);
					}
				}
			}
			
			//某个楼层向下按钮被按下
			if (this.floors[i].getDownButtonIsPressed()){
				
				if (tempDownRequest == null) { //直接跳过空请求
					continue;
				}
				
				if (tempDownRequest.getRequestTime() < firstRequest.getRequestTime()) { //判断时间先后
					firstRequest = this.floorButtonRequestList.get(2 * i + 1);
				}else if (tempDownRequest.getRequestTime() == firstRequest.getRequestTime()) {
					if (tempDownRequest.getRequestRank() < firstRequest.getRequestRank()) { //判断序号先后
						firstRequest = this.floorButtonRequestList.get(2 * i + 1);
					}
				}
			}
		}
		
		return firstRequest;
	}
	
	//中级模拟器启动入口
	public void simulate(){
		int currentFloor;
		Request firstRequest;
		int lastRequestIndex = -1;
		int tempFloorNumber = 1;
		for (this.time = 0; ; this.time = this.time + 0.5) {
			if (this.elevator.getUpOrDown() != 0) { //电梯在上下楼
				
				if (this.elevator.getUpOrDown() == 1) {
					this.elevator.getUpFloor();
				}else{
					this.elevator.getDownFloor();
				}
				
				currentFloor = this.elevator.getCurrentFloor();
				if ((this.floors[currentFloor - 1].getUpButtonIsPressed() && this.elevator.getMovingTrend() == 1) ||
						(this.floorButtonRequestList.get(2 * (currentFloor - 1)) != null && this.floorButtonRequestList.get(2 * (currentFloor - 1)).getSpecialRequest()) ||
						(this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1) != null && this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1).getSpecialRequest()) ||
						(this.floors[currentFloor - 1].getDownButtonIsPressed() && this.elevator.getMovingTrend() == -1) ||
						this.elevator.getButtonState(currentFloor)) { //电梯内或外有按钮被按下（外满足方向与运动趋势相同或为特殊指令）
					
					//输出并清除当前楼层按钮内请求信息（楼层请求需要与电梯运动趋势相同或为特殊请求）
					if (this.floors[currentFloor - 1].getUpButtonIsPressed() && this.elevator.getMovingTrend() == 1
							||  this.floorButtonRequestList.get(2 * (currentFloor - 1)) != null && this.floorButtonRequestList.get(2 * (currentFloor - 1)).getSpecialRequest())
					{//楼层向上请求且与电梯运动方向相同或为特殊请求
						this.elevator.setTargetTime(this.time);
						System.out.print((FloorRequest)this.floorButtonRequestList.get(2 * (currentFloor - 1))); //输出已执行完的请求
						System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
						this.floorButtonRequestList.set(2 * (currentFloor - 1), null);//清除按钮内请求信息
					}
					
					if (this.floors[currentFloor - 1].getDownButtonIsPressed() && this.elevator.getMovingTrend() == -1
							|| this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1) != null && this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1).getSpecialRequest())
					 { //楼层向下请求且与电梯运动方向相同或为特殊请求
						this.elevator.setTargetTime(this.time);
						System.out.print((FloorRequest)this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1)); //输出已执行完的请求
						System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
						this.floorButtonRequestList.set(2 * (currentFloor - 1) + 1, null);//清除按钮内请求信息
					}
					
					if (this.elevator.getButtonState(currentFloor)) { //电梯内请求
						this.elevator.setTargetTime(this.time);
						System.out.print((ElevatorRequest)this.elevatorButtonRequestList.get(currentFloor - 1)); //输出已执行完的请求
						System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
						this.elevatorButtonRequestList.set(currentFloor - 1, null);//清除按钮内请求信息
					}
					
					//根据现有的其他请求状态确定电梯运动趋势
					if (this.haveSameTrendRequest(currentFloor, this.elevator.getUpOrDown())) { //电梯内有同趋势请求
						this.elevator.setMovingTrend(this.elevator.getUpOrDown());
					}else{ //电梯内无同趋势请求
						firstRequest = this.findFirstRequest();
						if (firstRequest != null) {
							if (firstRequest.getType().equals("ER")) { //ER类请求
								if (((ElevatorRequest)firstRequest).getTargetFloor() > currentFloor) {
									this.elevator.setMovingTrend(1);
								}else if (((ElevatorRequest)firstRequest).getTargetFloor() < currentFloor) {
									this.elevator.setMovingTrend(-1);
								}
							}else{ //FR类请求
								if (((FloorRequest)firstRequest).getFloorNumber() > currentFloor) {
									this.elevator.setMovingTrend(1);
									if (((FloorRequest)firstRequest).getUpOrDown() == -1) { //请求方向与电梯运动方向相反（向下），设置为特殊请求
										tempFloorNumber = ((FloorRequest)firstRequest).getFloorNumber();
										this.floorButtonRequestList.get(2 * (tempFloorNumber - 1) + 1).setSpecialRequest();
									}
								}else if (((FloorRequest)firstRequest).getFloorNumber() < currentFloor) {
									this.elevator.setMovingTrend(-1);
									if (((FloorRequest)firstRequest).getUpOrDown() == 1) { //请求方向与电梯运动方向相反（向上），设置为特殊请求
										tempFloorNumber = ((FloorRequest)firstRequest).getFloorNumber();
										this.floorButtonRequestList.get(2 * (tempFloorNumber - 1)).setSpecialRequest();
									}
								}else{
									this.elevator.setMovingTrend(0);
								}
							}
						}else{ //无任何其他按钮被按下
							this.elevator.setMovingTrend(0);
						}
					}
					
					this.elevator.setUporDown(0);
					this.elevator.setGateIsOperating(true);
					this.elevator.setGateOperatingTime(0);
				}
				
				lastRequestIndex = this.checkCurrentRequestCount(lastRequestIndex) + lastRequestIndex;
			}else{ //电梯暂时停在某楼
				lastRequestIndex = this.checkCurrentRequestCount(lastRequestIndex) + lastRequestIndex; //检查当前时间是否有新请求
				
				if (this.elevator.getGateIsOperating()) { //上一时刻电梯在开关门
					if (this.elevator.getGateOperatingTime() == 0) { //进入开关门的第0.5时刻
						this.elevator.setGateOperatingTime(0.5);
					}else if (this.elevator.getGateOperatingTime() == 0.5) { //完成开关门操作
						this.elevator.setGateOperatingTime(1);
						this.elevator.setGateIsOperating(false);
						
						
						//弹起已执行完请求的当前楼层按钮，并清除导致STILL的按钮内请求（电梯内外）
						currentFloor = this.elevator.getCurrentFloor();
						
							//同层电梯内按钮且所存请求已被删除
						if (this.elevator.getButtonState(currentFloor) && this.elevatorButtonRequestList.get(currentFloor - 1) == null) { 
							this.elevator.unpressButton(currentFloor); //弹起按钮
						}

							//同楼层向上按钮且所存请求已被删除
						if (this.floors[currentFloor - 1].getUpButtonIsPressed() && this.floorButtonRequestList.get(2 * (currentFloor - 1)) == null) {
							this.floors[currentFloor - 1].unpressUpButton(); //弹起按钮
						}
						
							//同楼层向下按钮且所存请求已被删除
						if (this.floors[currentFloor - 1].getDownButtonIsPressed() && this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1) == null) {
								this.floors[currentFloor - 1].unpressDownButton(); //弹起按钮
						}
					
						
						//根据电梯运行趋势设计电梯下一步工作
						if (this.elevator.getMovingTrend() == 0) {
							firstRequest = this.findFirstRequest();
							if (firstRequest != null) { //有未执行请求
								if (firstRequest.getType() == "ER") {
									if (((ElevatorRequest)firstRequest).getTargetFloor() > currentFloor) {
										this.elevator.setUporDown(1);
										this.elevator.setMovingTrend(1);
										this.elevator.setGateOperatingTime(0);
									}else if (((ElevatorRequest)firstRequest).getTargetFloor() < currentFloor) {
										this.elevator.setUporDown(-1);
										this.elevator.setMovingTrend(-1);
										this.elevator.setGateOperatingTime(0);
									}else{ //同层请求即刻执行，并直接输出并清除按钮存储请求
										this.elevator.setUporDown(0);
										this.elevator.setMovingTrend(0);
										
										this.elevator.setTargetTime(this.time);
										if (this.elevatorButtonRequestList.get(currentFloor - 1) != null) {
											System.out.print((ElevatorRequest)this.elevatorButtonRequestList.get(currentFloor - 1)); //输出已执行完的请求
											System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
										}
										this.elevatorButtonRequestList.set(currentFloor - 1, null); //清除按钮存储的请求
										
										this.elevator.setGateIsOperating(true);
										this.elevator.setGateOperatingTime(0);
									}
								}else{ //未执行请求为FR类
									if (((FloorRequest)firstRequest).getFloorNumber() > currentFloor) {
										this.elevator.setUporDown(1);
										this.elevator.setMovingTrend(1);
										
										if (((FloorRequest)firstRequest).getUpOrDown() == -1) { //请求方向与电梯运动方向相反（向下），设置为特殊请求
											tempFloorNumber = ((FloorRequest)firstRequest).getFloorNumber();
											this.floorButtonRequestList.get(2 * (tempFloorNumber - 1) + 1).setSpecialRequest();
										}
										
									}else if (((FloorRequest)firstRequest).getFloorNumber() < currentFloor) {
										this.elevator.setUporDown(-1);
										this.elevator.setMovingTrend(-1);
										
										if (((FloorRequest)firstRequest).getUpOrDown() == 1) { //请求方向与电梯运动方向相反（向上），设置为特殊请求
											tempFloorNumber = ((FloorRequest)firstRequest).getFloorNumber();
											this.floorButtonRequestList.get(2 * (tempFloorNumber - 1)).setSpecialRequest();
										}
										
									}else{ //同层请求即刻执行，并直接输出并清除按钮存储请求
										this.elevator.setUporDown(0);
										this.elevator.setMovingTrend(0);
										
										this.elevator.setTargetTime(this.time);
										if (this.floorButtonRequestList.get(2 * (currentFloor - 1)) != null) { //导致STILL的向上FR请求
											System.out.print((FloorRequest)this.floorButtonRequestList.get(2 * (currentFloor - 1))); //输出已执行完的请求
											System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
											this.floorButtonRequestList.set(2 * (currentFloor - 1), null); //清除按钮存储的请求
										}else{ //导致STILL的向下FR请求
											System.out.print((FloorRequest)this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1)); //输出已执行完的请求
											System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
											this.floorButtonRequestList.set(2 * (currentFloor - 1) + 1, null); //清除按钮存储的请求
										}
										
										
										
										this.elevator.setGateIsOperating(true);
										this.elevator.setGateOperatingTime(0);
									}
								}
							}else{ //无任何其他按钮被按下
								this.elevator.setMovingTrend(0);
							}
						}else{ //电梯运动趋势不为静止
							this.elevator.setUporDown(this.elevator.getMovingTrend());
						}
					}
				}else{ //电梯停在某层并且上一个时刻未在开关门(停了有段时间了)或开关门刚结束
					currentFloor = this.elevator.getCurrentFloor();
				
					//寻找时间最靠前按钮并执行,若无则电梯不执行任何操作
					firstRequest = this.findFirstRequest();
					if (firstRequest != null) {
						if (firstRequest.getType() == "ER") {
							if (((ElevatorRequest)firstRequest).getTargetFloor() > currentFloor) {
								this.elevator.setUporDown(1);
								this.elevator.setMovingTrend(1);
							}else if (((ElevatorRequest)firstRequest).getTargetFloor() < currentFloor) {
								this.elevator.setUporDown(-1);
								this.elevator.setMovingTrend(-1);
							}else{
								this.elevator.setUporDown(0);
								this.elevator.setMovingTrend(0);
								
								this.elevator.setTargetTime(this.time);
								if (this.elevatorButtonRequestList.get(currentFloor - 1) != null) {
									System.out.print((ElevatorRequest)this.elevatorButtonRequestList.get(currentFloor - 1)); //输出已执行完的请求
									System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
								}
								this.elevatorButtonRequestList.set(currentFloor - 1, null); //清除按钮存储的请求
								
								this.elevator.setGateIsOperating(true);
								this.elevator.setGateOperatingTime(0);
							}
						}else{ //找到的请求为FR类
							if (((FloorRequest)firstRequest).getFloorNumber() > currentFloor) {
								this.elevator.setUporDown(1);
								this.elevator.setMovingTrend(1);
								
								if (((FloorRequest)firstRequest).getUpOrDown() == -1) { //请求方向与电梯运动方向相反（向下），设置为特殊请求
									tempFloorNumber = ((FloorRequest)firstRequest).getFloorNumber();
									this.floorButtonRequestList.get(2 * (tempFloorNumber - 1) + 1).setSpecialRequest();
								}
								
							}else if (((FloorRequest)firstRequest).getFloorNumber() < currentFloor) {
								this.elevator.setUporDown(-1);
								this.elevator.setMovingTrend(-1);
								
								if (((FloorRequest)firstRequest).getUpOrDown() == 1) { //请求方向与电梯运动方向相反（向上），设置为特殊请求
									tempFloorNumber = ((FloorRequest)firstRequest).getFloorNumber();
									this.floorButtonRequestList.get(2 * (tempFloorNumber - 1)).setSpecialRequest();
								}
								
							}else{ //为当前楼层的FR指令，立即执行
								this.elevator.setUporDown(0);
								this.elevator.setMovingTrend(0);
								
								this.elevator.setTargetTime(this.time);
								if (this.floorButtonRequestList.get(2 * (currentFloor - 1)) != null) { //导致STILL的FR向上指令
									System.out.print((FloorRequest)this.floorButtonRequestList.get(2 * (currentFloor - 1))); //输出已执行完的请求
									System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
									this.floorButtonRequestList.set(2 * (currentFloor - 1), null); //清除按钮存储的请求
								}else{ //导致STILL的FR向下指令
									System.out.print((FloorRequest)this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1)); //输出已执行完的请求
									System.out.print("/" + this.elevator + "\n"); //输出电梯当前状态
									this.floorButtonRequestList.set(2 * (currentFloor - 1) + 1, null); //清除按钮存储的请求
								}
								
								
								this.elevator.setGateIsOperating(true);
								this.elevator.setGateOperatingTime(0);
							}
						}
					}else{ //电梯继续停止并无可处理请求
						
						//若此状态下，请求已读取完，则模拟器模拟结束
						if (lastRequestIndex == this.requestList.size() - 1) {
							break;
						}else{
							this.time = this.requestList.get(lastRequestIndex + 1).getRequestTime() - 0.5;
						}
					}
				}
			}
		}
	}
}
