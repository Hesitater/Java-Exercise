package Elevator;

import java.io.FileWriter;
import java.util.ArrayList;

public class ElevatorThread extends Thread{
	private Elevator elevator;
	private RequestQueue requestQueue;
	private Floor[] floors;
	private final int MAXFLOOR = 20;
	private ArrayList<ElevatorRequest> elevatorButtonRequestList; //电梯内按钮存储请求所用数组
	private ArrayList<FloorRequest> floorButtonRequestList; //楼层按钮存储请求所用数组，共20个，以2个为单位，前一个代表上按钮，后一个代表下按钮
	private FileWriter fileWriter;
	
	public ElevatorThread(Elevator elevator, FileWriter fileWriter) { 
		this.elevator = elevator;
		this.requestQueue = new RequestQueue();
		this.floors = new Floor[MAXFLOOR];
		this.fileWriter = fileWriter;
		
		for (int i = 0; i < floors.length; i++) {
			this.floors[i] = new Floor(i + 1);
		}
		
		Request tempRequest = null;
		this.floorButtonRequestList = new ArrayList<>();
		for (int i = 0; i < 2 * MAXFLOOR; i++) { 
			tempRequest = new Request("FR", 0);
			this.floorButtonRequestList.add(null);
		}
		
		this.elevatorButtonRequestList = new ArrayList<>();
		for (int i = 0; i < MAXFLOOR; i++) {
			tempRequest = new Request("ER", 0);
			this.elevatorButtonRequestList.add(null);
		}
	}
	
	public void run() {
		SmartSimulator simulator = new SmartSimulator(requestQueue, elevator, floors, elevatorButtonRequestList, 
				floorButtonRequestList, fileWriter); //此模拟器Floor[]负责处理此电梯的FloorRequest,与真实Floor[]不同
		//此处requestQueue没有任何意义，只由于SmartSimulator的父类需要此参数
		simulator.simulate();
	}

	public ArrayList<ElevatorRequest> getElevatorButtonRequestList() {
		return elevatorButtonRequestList;
	}

	public void setElevatorButtonRequestList(int i, ElevatorRequest currentRequest) {
		this.elevatorButtonRequestList.set(i, currentRequest);
	}

	public ArrayList<FloorRequest> getFloorButtonRequestList() {
		return floorButtonRequestList;
	}

	public synchronized void setFloorButtonRequestList(int i, FloorRequest currentRequest) {
		if (this.floorButtonRequestList.get(i) == null) {
			this.floorButtonRequestList.remove(i);
			this.floorButtonRequestList.add(i, currentRequest);
		}else{
			this.floorButtonRequestList.set(i, currentRequest);
		}
	}

	public Floor getFloor(int floorNumber) {
		return this.floors[floorNumber - 1];
	}

	public void setFloors(Floor[] floors) {
		this.floors = floors;
	}
	
	
	
	
}
