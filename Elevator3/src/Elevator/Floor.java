package Elevator;

public class Floor{
	private int floorNumber;
	private boolean carriageArrived;
	private boolean upButtonIsPressed;
	private boolean downButtonIsPressed;
	
	public Floor(int floorNumber, boolean carriageArrived) {
		this.floorNumber = floorNumber;
		this.carriageArrived = carriageArrived;
		this.initializeButtons();
		this.upButtonIsPressed = false;
		this.downButtonIsPressed = false;
	}
	
	public Floor(int floorNumber) {
		this.floorNumber = floorNumber;
		this.initializeButtons();
		this.upButtonIsPressed = false;
		this.downButtonIsPressed = false;
	}
	
	public void initializeButtons() {
		this.upButtonIsPressed = false;
		this.downButtonIsPressed = false;
	}
	
	//返回值代表本次按按钮是否成功，若已被按过，则按按钮失败，返回false
	public boolean pressUpButton() { 
		if (upButtonIsPressed) {
			return false;
		}
		
		this.upButtonIsPressed = true;
		return true;
	}
	
	//返回值代表本次按按钮是否成功，若已被按过，则按按钮失败，返回false
	public boolean pressDownButton() { 
		if (downButtonIsPressed) {
			return false;
		}
		
		this.downButtonIsPressed = true;
		return true;
	}
	
	//向上按钮弹起
	public void unpressUpButton() {
		this.upButtonIsPressed = false;
	}
	
	//向下按钮弹起
	public void unpressDownButton() {
		this.downButtonIsPressed = false;
	}
	
	public void catchElevator(Elevator elevator, double currentTime){
		if (elevator.getCurrentFloor(currentTime) == this.floorNumber) {
			this.carriageArrived = true;
			if (this.upButtonIsPressed) {
				
			}
		}else{
			this.carriageArrived = false;
		}
	}
	
	public boolean getDownButtonIsPressed() {
		return this.downButtonIsPressed;
	}
	
	public boolean getUpButtonIsPressed() {
		return this.upButtonIsPressed;
	}
	
	//判断是否有上下键被按下，返回值为按下的按钮数
	public int haveButtonsPressed(){ 
		int buttonCount = 0;
		
		if (this.upButtonIsPressed) {
			buttonCount ++;
		}
		
		if (this.downButtonIsPressed) {
			buttonCount++;
		}
		
		return buttonCount;
	}
}
