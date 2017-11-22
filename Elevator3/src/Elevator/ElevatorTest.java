package Elevator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ElevatorTest {
	static double initialTime;
	private final static int MAXFLOOR = 20;
	public static void main(String args[]) {
		initialTime = System.currentTimeMillis(); //得到初始系统时间
		
		try {
			FileWriter fileWriter = new FileWriter("./result.txt");
			RequestQueue requestQueue = new RequestQueue();
			
			Elevator firstElevator = new Elevator(1, 1);
			Elevator secondElevator = new Elevator(1, 2);
			Elevator thirdElevator = new Elevator(1, 3);
			
			InputThread inputThread = new InputThread(initialTime, requestQueue, fileWriter);
			SchedulerThread schedulerThread = new SchedulerThread(requestQueue, 
					firstElevator, secondElevator, thirdElevator, fileWriter);
			inputThread.start();
			schedulerThread.start();
		} catch (Exception e) {
			System.out.println("Oops. Something unexpected happened.");;
		}
	}
}
