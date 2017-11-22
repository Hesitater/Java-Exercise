package Elevator;

import java.math.BigDecimal;

public class Elevator implements ElevatorInterface{
	private int elevatorID;
	private boolean[] buttonsState; //表示各按钮状态的数组，数组下标为楼层按钮编号
	private int currentFloor;
	private int startFloor;
	private int targetFloor;
	private double departTime;
	private double targetTime;
	private boolean isRunning;
	private boolean gateIsOperating;
	private double gateOperatingTime;
	private int upOrDown; //1为向上走，-1为向下走，0为不走
	private int movingTrend; //运动趋势，表示方法与upOrDown相同
	private final int maxFloorNumber = 20;
	private int workAmount; //电梯运动量
	
	public Elevator(int currentFloor, int elevatorID) {
		this.buttonsState = new boolean[this.maxFloorNumber];
		this.initializeButtonsState();
		
		this.elevatorID = elevatorID;
		this.currentFloor = currentFloor;
		this.targetFloor = 1;
		this.isRunning = false;
		this.gateIsOperating = false;
		this.upOrDown = 0;
		this.movingTrend = 0;
		this.gateOperatingTime = 0;
		this.workAmount = 0;
	}
	
	public int getMaxFloorNumber(){
		return this.maxFloorNumber;
	}
	
	public void initializeButtonsState(){
		for (int i = 0; i < this.maxFloorNumber; i++) {
			this.buttonsState[i] = false;
		}
	}
	
	public boolean getButtonState(int floorNumber) {
		return this.buttonsState[floorNumber - 1];
	}
	
	public boolean pressButton(int buttonNumber) { //返回值代表本次按按钮是否成功，若已被按过，则按按钮失败，返回false
		if (this.buttonsState[buttonNumber - 1] == true) {
			return false;
		}
		
		buttonsState[buttonNumber - 1] = true;
		return true;
	}
	
	public void unpressButton(int buttonNumber) {
		buttonsState[buttonNumber - 1] = false;
	}
	
	public void depart(int targetFloor, double departTime) {
		this.departTime = departTime;
		this.startFloor = this.getCurrentFloor(departTime);
		this.targetFloor = targetFloor;
		this.getTargetTime();
		
		if (this.targetFloor < this.startFloor){
			this.upOrDown = -1;
			this.isRunning = true;
		}else if (this.targetFloor > this.startFloor){
			this.upOrDown = 1;
			this.isRunning = true;
		}else{
			this.upOrDown = 0;
			this.isRunning = false;
		}
	}
	
	public double getPlannedTargetTime(int plannedTargetFloor, double plannedDepartTime){
		double plannedTargetTime;
		int plannedUpOrDown;
		
		if (plannedTargetFloor > this.getCurrentFloor(plannedDepartTime)) {
			plannedUpOrDown = 1;
		}else if (plannedTargetFloor < this.getCurrentFloor(plannedDepartTime)) {
			plannedUpOrDown = -1;
		}else{
			plannedUpOrDown = 0;
		}
		plannedTargetTime = plannedDepartTime + (double)plannedUpOrDown * (double)(plannedTargetFloor - this.currentFloor) * 0.5;
		return plannedTargetTime;
	}
	
	public void getUpFloor(){
		if (this.currentFloor == this.maxFloorNumber) {
			System.out.println("Can't get higher.");
			return;
		}
		
		this.workAmount++;
		this.currentFloor = this.currentFloor + 1;
	}
	
	public void getDownFloor() {
		if (this.currentFloor == 1) {
			System.out.println("Can't get lower.");
			return;
		}
		
		this.workAmount++;
		this.currentFloor = this.currentFloor - 1;
	}
	
	public int getCurrentFloor() {
		return this.currentFloor;
	}
	
	public int getCurrentFloor(double currentTime) {
		this.getIsRunning(currentTime);
		if (this.isRunning){
			this.currentFloor = this.startFloor + (int)((currentTime - this.departTime) * 2 * (double)this.upOrDown);
		}else{
			this.currentFloor = this.targetFloor;
		}
		
		return this.currentFloor;
	}
	
	public double getTargetTime(){
		this.targetTime = this.departTime + (double)this.upOrDown * (double)(this.targetFloor - this.startFloor) * 0.5;
		return this.targetTime;
	}
	
	//检查电梯是否还在升降
	public boolean getIsRunning(double currentTime) { 
		if ((currentTime - this.departTime) * 2 >= this.upOrDown * (this.targetFloor - this.startFloor)) {
			this.isRunning = false;
		}
		
		return this.isRunning;
	}
	
	//设置电梯是否在开关门
	public void setGateIsOperating(boolean gateIsOperating) {
		this.gateIsOperating = gateIsOperating;
	}
	
	//检查电梯是否在开关门
	public boolean getGateIsOperating() {
		/*if ((currentTime - this.departTime) * 2 == this.upOrDown * (this.targetFloor - this.startFloor)) {
			this.gateIsOperating = true;
		}else{
			this.gateIsOperating = false;
		}*/
		
		return this.gateIsOperating;
	}
	
	public void setGateOperatingTime(double gateOperatingTime) {
		this.gateOperatingTime = gateOperatingTime;
	}
	
	
	public double getGateOperatingTime() {
		return this.gateOperatingTime;
	}
	
	//检查电梯的运动状态是上升、下降还是静止
	public synchronized void setUporDown(int upOrDown) {
		this.upOrDown = upOrDown;
	}
	
	//检查电梯的运动状态是上升、下降还是静止
	public synchronized int getUpOrDown() {
		return this.upOrDown;
	}
	
	public synchronized void setMovingTrend(int movingTrend) {
		this.movingTrend = movingTrend;
	}
	
	//检查电梯的运动趋势是上升、下降还是静止
	public synchronized int getMovingTrend() {
		return this.movingTrend;
	}
	
	public void setTargetTime(double targetTime) {
		this.formatTargetTime(targetTime);
	}
	
	public void formatTargetTime(double targetTime) {
		targetTime = targetTime / 1000;
		BigDecimal bigDecimal = new BigDecimal(targetTime);
		this.targetTime = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public int getWorkAmount() {
		return this.workAmount;
	}
	
	
	
	public int getElevatorID() {
		return this.elevatorID;
	}

	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}

	public String toString() {
		double currentTime = this.targetTime;
		if (this.getMovingTrend() == 1) {
			return "(#" + this.elevatorID + "," + this.currentFloor + "," + "UP" + "," + this.workAmount + "," +  currentTime  + ")";
		}else if (this.getMovingTrend() == -1) {
			return "(#" + this.elevatorID + "," + this.currentFloor + "," + "DOWN" + "," + this.workAmount + "," +  currentTime  + ")";
		}else{
			//this.targetTime++;
			currentTime = currentTime + 6;
			return "(#" + this.elevatorID + "," + this.currentFloor + "," + "STILL" + "," + this.workAmount + "," +  currentTime  + ")";
		}
	}
}
