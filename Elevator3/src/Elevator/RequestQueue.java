package Elevator;

import java.util.ArrayList;

public class RequestQueue{
	private ArrayList<Request> requests;
	private int completedRequestIndex;
	
	public RequestQueue() {
		this.requests = new ArrayList<Request>();
		this.completedRequestIndex = -1;
	}
	
	public synchronized void addRequest(Request request) {
		this.requests.add(request);
	}
	
	public synchronized ArrayList<Request> getRequests() {
		return this.requests;
	}
	
	public synchronized Request getRequest(int index) {
		return this.requests.get(index);
	}
	
	public void setCompletedRequestIndex(int index) {
		this.completedRequestIndex = index;
	}
	
	public int getCompletedRequestIndex() {
		return this.completedRequestIndex;
	}
	
	public synchronized Request removeRequest(int index) {
		Request removedRequest = this.requests.remove(index);
		return removedRequest;
	}
	
	public synchronized void removeAllRequests() {
		int requestListSize = requests.size();
		for (int i = 0; i < requestListSize; i++) {
			this.requests.remove(0);
		}
	}
}
