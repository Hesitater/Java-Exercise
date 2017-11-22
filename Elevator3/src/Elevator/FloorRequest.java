package Elevator;

public class FloorRequest extends Request{
	private int floorNumber;
	private int upOrDown; //数字1代表向上，数字-1代表向下
	private boolean isSpecialRequest; //用于处理电梯运动方向与楼层请求方向不同的情况
	
	public FloorRequest(String type, int currentFloor, int upOrDown, double time) {
		super(type, time);
		this.floorNumber = currentFloor;
		this.upOrDown = upOrDown;
	}
	
	public int getFloorNumber() {
		return this.floorNumber;
	}
	
	public int getUpOrDown() {
		return this.upOrDown;
	}
	
	public void setSpecialRequest() {
		this.isSpecialRequest = true;
	}
	
	public void cancelSpecialRequest() {
		this.isSpecialRequest = false;
	}
	
	public boolean getSpecialRequest() {
		return this.isSpecialRequest;
	}
	
	public String toString() {
		if (this.upOrDown == 1) {
			return "["+ this.getType() + "," + this.getFloorNumber() + "," + "UP" + "]";
		}
		return "["+ this.getType() + "," + this.getFloorNumber() + "," + "DOWN" + "]";
	}
}
