package Elevator;

public interface ElevatorInterface {
	public int getCurrentFloor(double currentTime);
	public String toString();
	public void depart(int targetFloor, double departTime);
}
