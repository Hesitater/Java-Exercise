package Elevator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static Elevator.ElevatorTest.initialTime;

public class SmartSimulator extends ALSSimulator{
	private FileWriter fileWriter;
	
	public SmartSimulator(RequestQueue requestQueue, Elevator elevator, Floor[] floors,
			ArrayList<ElevatorRequest> elevatorButtonRequestList, ArrayList<FloorRequest> floorButtonRequestList,
			FileWriter fileWriter) {
		super(requestQueue);
		this.elevator = elevator;
		this.floors = floors;
		this.elevatorButtonRequestList = elevatorButtonRequestList;
		this.floorButtonRequestList = floorButtonRequestList;
		this.fileWriter = fileWriter;
	}
	
	//寻找时间最靠前按钮,设置电梯趋势，若有同层请求则立即执行同层请求,若无任何请求则电梯运动趋势为0
	public void findEarliestRequest() {
		int currentFloor = this.elevator.getCurrentFloor();
		Request firstRequest = this.findFirstRequest();
		Request tempRequest = null;
		int tempFloorNumber;
		
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
					
					tempRequest = (ElevatorRequest)this.elevatorButtonRequestList.get(currentFloor - 1);
					this.elevator.setTargetTime(System.currentTimeMillis() - initialTime);
					if (this.elevatorButtonRequestList.get(currentFloor - 1) != null) {
						//System.out.println(System.currentTimeMillis() + ":[" + 
					//(ElevatorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")"); //输出已执行完的请求
						try {
							this.fileWriter.write(System.currentTimeMillis() + ":[" + 
									(ElevatorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")" + 
									System.getProperty("line.separator"));
							this.fileWriter.flush();
						} catch (IOException e) {
							System.out.println("Oops. Something unexpected happened.");
						}
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
					
					this.elevator.setTargetTime(System.currentTimeMillis() - initialTime);
					if (this.floorButtonRequestList.get(2 * (currentFloor - 1)) != null) { //导致STILL的向上FR请求
						tempRequest = this.floorButtonRequestList.get(2 * (currentFloor - 1));
						if (tempRequest != null) {
							//System.out.println(System.currentTimeMillis() + ":[" + 
						//(FloorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")"); //输出已执行完的请求
							try {
								this.fileWriter.write(System.currentTimeMillis() + ":[" + 
										(FloorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")" +
										System.getProperty("line.separator"));
								this.fileWriter.flush();
							} catch (IOException e) {
								System.out.println("Oops. Something unexpected happened.");
							}
						}
							
						this.floorButtonRequestList.set(2 * (currentFloor - 1), null); //清除按钮存储的请求
					}else{ //导致STILL的向下FR请求
						tempRequest = this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1);
						if (tempRequest != null) {
							//System.out.println(System.currentTimeMillis() + ":[" + 
						//(FloorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")"); //输出已执行完的请求
							try {
								this.fileWriter.write(System.currentTimeMillis() + ":[" + 
										(FloorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")" +
										System.getProperty("line.separator"));
								this.fileWriter.flush();
							} catch (IOException e) {
								System.out.println("Oops. Something unexpected happened.");
							}
						}
						this.floorButtonRequestList.set(2 * (currentFloor - 1) + 1, null); //清除按钮存储的请求
					}
					
					
					
					this.elevator.setGateIsOperating(true);
					this.elevator.setGateOperatingTime(0);
				}
			}
		}else{ //无任何其他按钮被按下
			this.elevator.setMovingTrend(0);
		}
	}
	
	//开关门完毕后检查电梯的下一个运动状态,有同层请求则立即执行
	public void checkMovingState() {
		
		//根据电梯运行趋势设计电梯下一步工作
		if (this.elevator.getMovingTrend() == 0) {
			this.findEarliestRequest();
		}else{ //电梯运动趋势不为静止
			this.elevator.setUporDown(this.elevator.getMovingTrend());
		}
	}
	
	//根据现有请求检查电梯的下一个运动趋势
	public void checkMovingTrend() {
		Request firstRequest;
		int currentFloor = this.elevator.getCurrentFloor();
		int tempFloorNumber;
		
		if (this.haveSameTrendRequest(currentFloor, this.elevator.getUpOrDown())) { //电梯内有同趋势请求
			this.elevator.setMovingTrend(this.elevator.getUpOrDown());
		}else{ //电梯内无同趋势请求,找时间最靠前的请求
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
	}
	
	//弹起已执行完请求的当前楼层按钮
	public void recoverButtons() {
		int currentFloor = this.elevator.getCurrentFloor();
		
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
	}
	
	//执行开关门
	public void openGate() {
		this.elevator.setGateIsOperating(true);
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.elevator.setGateIsOperating(false);
	}
	
	//检查当前楼层是否有需要执行的请求,有则设定运动趋势->执行开关门->弹起按钮->检查电梯下一个运动状态
	public void checkButtonState() {
		int currentFloor;
		Request tempRequest;
		
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
				this.elevator.setTargetTime(System.currentTimeMillis() - initialTime);
				tempRequest = this.floorButtonRequestList.get(2 * (currentFloor - 1));
				if (tempRequest != null) {
					//System.out.println(System.currentTimeMillis() + ":[" + 
				//(FloorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")"); //输出已执行完的请求
					try {
						this.fileWriter.write(System.currentTimeMillis() + ":[" + 
								(FloorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")" +
								System.getProperty("line.separator"));
						this.fileWriter.flush();
					} catch (IOException e) {
						System.out.println("Oops. Something unexpected happened.");
					}
				}
				this.floorButtonRequestList.set(2 * (currentFloor - 1), null);//清除按钮内请求信息
			}
			
			if (this.floors[currentFloor - 1].getDownButtonIsPressed() && this.elevator.getMovingTrend() == -1
					|| this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1) != null && this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1).getSpecialRequest())
			 { //楼层向下请求且与电梯运动方向相同或为特殊请求
				this.elevator.setTargetTime(System.currentTimeMillis() - initialTime);
				tempRequest = this.floorButtonRequestList.get(2 * (currentFloor - 1) + 1);
				if (tempRequest != null) {
					//System.out.println(System.currentTimeMillis() + ":[" + 
				//(FloorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")"); //输出已执行完的请求
					try {
						this.fileWriter.write(System.currentTimeMillis() + ":[" + 
								(FloorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")" +
								System.getProperty("line.separator"));
						this.fileWriter.flush();
					} catch (IOException e) {
						System.out.println("Oops. Something unexpected happened.");
					}
				}
				this.floorButtonRequestList.set(2 * (currentFloor - 1) + 1, null);//清除按钮内请求信息
			}
			
			if (this.elevator.getButtonState(currentFloor)) { //电梯内请求
				this.elevator.setTargetTime(System.currentTimeMillis() - initialTime);
				tempRequest = this.elevatorButtonRequestList.get(currentFloor - 1);
				if (tempRequest != null) {
					//System.out.println(System.currentTimeMillis() + ":[" + 
				//(ElevatorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")"); //输出已执行完的请求
					try {
						this.fileWriter.write(System.currentTimeMillis() + ":[" + 
								(ElevatorRequest)tempRequest + "," + tempRequest.getRequestTime() + "]/(" + this.elevator + ")" +
								System.getProperty("line.separator"));
						this.fileWriter.flush();
					} catch (IOException e) {
						System.out.println("Oops. Something unexpected happened.");
					}
				}
				this.elevatorButtonRequestList.set(currentFloor - 1, null);//清除按钮内请求信息
			}
			
			this.elevator.setUporDown(0);
			this.checkMovingTrend();
			this.openGate();
			this.recoverButtons();
			this.checkMovingState();
		}
	}
	
	public synchronized void simulate() {
		while (true) {
			if (this.elevator.getUpOrDown() != 0) { //电梯在上下楼
				if (this.elevator.getUpOrDown() == 1) {
					this.elevator.getUpFloor();
				}else{
					this.elevator.getDownFloor();
				}
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.checkButtonState(); //检查是否需要开关门,若需要则执行到开关门结束后的后续处理
				continue;
			}else{ //电梯停在某层有一段时间了
				findEarliestRequest(); //寻找时间最靠前按钮并执行,若无则电梯不执行任何操作
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Oops. Something unexpected happened.");
				}
			}
		}
	}
}
