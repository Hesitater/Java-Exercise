package Elevator;

import java.io.FileWriter;

public class InputThread extends Thread{
	private double initialTime;
	private RequestQueue requestQueue;
	private FileWriter fileWriter;
	
	public InputThread(double initialTime, RequestQueue requestQueue, FileWriter fileWriter) {
		super();
		this.initialTime = initialTime;
		this.requestQueue = requestQueue;
		this.fileWriter = fileWriter;
	}

	public void run() {
		InputHandler inputHandler = new InputHandler(this.fileWriter);
		inputHandler.handleInput(requestQueue);
	}
}
