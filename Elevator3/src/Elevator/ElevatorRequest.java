package Elevator;

public class ElevatorRequest extends Request{
	private int targetFloor;
	private int elevatorID;
	
	public ElevatorRequest(String type, int targetFloor, int elevatorNumber, double time) {
		super(type, time);
		this.targetFloor = targetFloor;
		this.elevatorID = elevatorNumber;
	}
	
	public int getTargetFloor() {
		return this.targetFloor;
	}
	
	public String toString() {
		return "["+ this.getType() + ",#" + this.elevatorID + "," + this.targetFloor + "]";
	}
	
	public int getElevatorNumber() {
		return this.elevatorID;
	}
}
