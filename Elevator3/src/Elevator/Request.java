package Elevator;

public class Request{
	private String type;
	private double requestTime;
	private boolean isIssuing;
	private int requestRank;

	public Request(String type, double requestTime) {
		this.type = type;
		this.requestTime = requestTime;
		this.isIssuing = false;
		this.requestRank = 0;
	}
	
	public void setRequestRank(int requestRank) {
		this.requestRank = requestRank;
	}
	
	public int getRequestRank() {
		return this.requestRank;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setRequestTime(double requestTime){
		this.requestTime = requestTime;
	}
	
	public double getRequestTime() {
		return this.requestTime;
	}
	
	public void startIssuing() {
		this.isIssuing = true;
	}
	
	public void completeIssuing() {
		this.isIssuing = false;
	}
	
	public boolean getIsIssuing() {
		return this.isIssuing;
	}
}