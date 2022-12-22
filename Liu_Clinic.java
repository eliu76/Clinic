/* Evan Liu
 * Period 3
 * Urgent Care Clinic
 * This class runs two different threads at the same time simulating 
 * a doctor operating on patients with a max capacity
 */
import java.util.*;

public class Liu_Clinic implements Runnable {

	//upper bound for patient arrival and treatment time
	private static final int patientArrival = 500;
	private static final int treatmentTime = 500;
	
	private Liu_LimitedPQ<Patient> waiting;
	private int masterID;
	private boolean sleep = false;

	public Liu_Clinic(int n) {

		waiting = new Liu_LimitedPQ<Patient>(n);
		//creates unique IDs starting from zero
		masterID = 0;
		//starts the clinic thread
		Thread t1 = new Thread(this);
		t1.start();
	}

	public void run() {
		
		//starts the doctor thread
		Thread t1 = new Thread(new Doctor());
		t1.start();

		while(true) {

			Patient toAdd = new Patient();
			Patient temp;
			
			//patient arrival time
			try {
				Thread.sleep((int)(Math.random() * patientArrival));
			}catch(InterruptedException ex){
				ex.printStackTrace();
			}
			
			if(sleep) {
				System.out.println("Doctor is awake");
			}
			
			//ensures only one thread can access the waiting room at a time
			synchronized(waiting) {

				temp = waiting.add(toAdd);
				//wakes doctor up
				waiting.notify();
				sleep = false;
			}
			
			if(temp!=null) {
				
				//full waiting room, and current patient has the lowest priority
				if(temp.id == toAdd.id) {
					System.out.println(toAdd + " Status: Going to hospital");
				}
				
				//the patient that got kicked out goes to hospital
				else {
					System.out.println(temp + " Status: Going to hospital");
				}
			}
			
			else {
				System.out.println(toAdd + " Status: In waiting room");
			}
		}
	}

	//this class runs a thread that simulates a doctor taking patients out of the waiting room
	public class Doctor implements Runnable {
		
		public void run() {
			
			while(true) {
				
				//if no patients doctor "takes a nap", calls wait
				if(waiting.isEmpty()) {

					synchronized(waiting) {
						
						System.out.println("Doctor is asleep");
						sleep = true;
						try {
							waiting.wait();
						} catch(InterruptedException ex) {
							ex.printStackTrace();
						}
					}
				}

				else {
					
					Patient current;
					
					synchronized(waiting) {

						current = waiting.remove();
						System.out.println("Current patient treated: " + current);
					}

					//amount of time it takes to treat the patient
					try {
						Thread.sleep((int)(Math.random() * treatmentTime));
					}catch(InterruptedException ex){
						ex.printStackTrace();
					}
				}
			}
		}
	}

	//this class represents a patient with a unique id and priority in the waiting room
	public class Patient implements Comparable<Patient> {

		private int priority;
		private int id;

		public Patient() {

			id = masterID;
			masterID++;
			priority = (int)(Math.random() * 20 + 1);
		}

		//negative if calling object higher priority
		public int compareTo(Patient temp) {
			
			if(priority == temp.priority) {
				
				return id - temp.id;
			}

			return priority - temp.priority;
		}
		
		public String toString() {

			String toReturn = "Patient id: " + id;
			toReturn = toReturn +  " Priority: " + priority;
			return toReturn;
		}
	}
	
	public static void main(String[] args) {

		new Liu_Clinic(20);
	}
}