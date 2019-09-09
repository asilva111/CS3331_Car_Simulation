/*CS3331 - Advanced Object-oriented programming - Dr.Roach - M,W 3:00pm - 4:20pm
 * This program simulates a 3 mile circuit where n number of cars can participate. Each mile has a specified
 * top speed (20mph, 60mph, and 30mph), and a position (distance) based on arbitrary 'time'. 
 * 
 * The timer will stop running only after all cars have finished the track.
 * The cars will stop running once they finish the track.
 * 
 * Cars have to start running 1 minute apart from each other and they have the same acceleration.
 * 
 * It is important that cars do not go over the speed limit before entering a new section, which is why a slow
 * down action is required.
 * 
 * Last updated: Sep 8th, 2019 by Andres Silva - 80588336
 **************************************************************************************************************/
import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Car {
	
	private String name;
	
	private double segmentSpeed;
	private double acceleration;
	private double velocity;
	private double distance;
	
	private boolean slowingDown;
	private boolean running;
	private boolean finished;
	
	private int currentSegment;
	
	private static double deltaTime = .01; //Specified change in time.

	public Car(String newName, double speed, double Acceleration) {
		name = newName;
		segmentSpeed = speed; 
		acceleration = Acceleration;

		velocity = 0;
		distance = 0;
		
		slowingDown = false;
		running = false;
		finished = false;
		
		currentSegment = 1;	
	}
	
	public static double round(double value, int places) { // Rounding method taken from: https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private void setAcceleration(double newAcceleration) {
		acceleration = newAcceleration;
	}
	
	private double getAcceleration() {
		return acceleration;
	}
	
	private void setVelocity() { 
		if((velocity + (acceleration * deltaTime) <= segmentSpeed) && velocity + (acceleration * deltaTime) >= 0) {
			velocity += acceleration * deltaTime; //Update velocity only when it has not reached the top speed and is greater than 0.
		}
		else { //Else cap speed at indicated value.
			velocity = segmentSpeed;
		}
	}
	
	private void setDistance() { 
		distance += velocity * deltaTime; 
		//Formula: distance = velocity * deltaTime
	}
	
	private static double getStoppingDistance(Car R, double FinalVelocity) { //Calculate distance to begin slow down.
		return round(Math.pow(FinalVelocity, 2) / (2 * R.acceleration ), 2); //Calculate distance needed to stop
		//Formula: FinalVel^2 / 2 * acceleration
	}
	
	private static boolean trackStatus(Car[] Cars, double time, double trackLength) { //Count how many cars have traveled the entire track.
		int doneCars = 0;
		
		for(int i = 0; i < Cars.length; i++) { 
			if (Cars[i].finished == true) { //If a runner traveled the track length, count it as done.
				doneCars++;
			}
		}
		
		if(doneCars < Cars.length){ //return false if not all runners are done.
			return false;
		}
		
		else { //Else, return true
			return true;
		}
	}
	
	private static void printLine(Car[] Cars, double time) { //Print distance of all runners at specified time.
		System.out.println("\n");
		System.out.print(time);
		
		for(int i = 0; i < Cars.length; i++) { //For all runners print position.
			System.out.print("\t" + round(Cars[i].velocity,2) + "\t" + round(Cars[i].distance / 5280, 2) + "\t");
		}
		
	}
	
	public static void main(String args[]) {
		double time = 0; //Clock.
		double stoppingValue; //Distance needed to decelerate to indicated velocity.
		
		int printInterval = 30; //How often to print data.
		
		double trackLength = 3 * 5280; // 3 miles in feet.
		
		double segment1TopSpeed = (20 * 1.467);//20 MPH to Feet per Second.
		double segment2TopSpeed = (60 * 1.467);
		double segment3TopSpeed = (30 * 1.467);
		
		double segment1Length = 5280; //All segments are 1 mile (5280 ft) in length.
		double segment2Length = segment1Length + 5280; //In feet, where does the segment ends.
		double segment3Length = segment2Length + 5280; 
	
		/**Car declaration********************************************/
		Car Cars[] = new Car[3];
		
		Cars[0] = new Car("A", segment1TopSpeed , 15); // A, 20 miles in feet per second, 15 feet per sec^2.
		Cars[1] = new Car("B", segment1TopSpeed , 15);
		Cars[2] = new Car("C", segment1TopSpeed , 15);
		/************************************************************/
		
		/**Formating*************************************************/
		for(int i = 0; i < Cars.length; i++) { //For all runners print name.
			System.out.print("\tCar " + Cars[i].name + "\t\t");
		}
		
		System.out.print("\nTime");
		
		for(int i = 0; i < Cars.length ;i++) {
			System.out.print("\tSpeed   Location");
		}
		/************************************************************/
		
		while(trackStatus(Cars, round(time,2), trackLength) == false) { //While there is still a car that has not finished driving,

			for(int i = 0; i < Cars.length; i++) { 

				if(round(time,2) * 60 == i * 60 * 60) { //Mark cars as running one minute apart from each other, starting with the first car.
					Cars[i].running = true;	
				}
				
				if(Cars[i].distance > 0 && Cars[i].distance < segment1Length) { //If a car is within a section of the circuit, mark it as such.
					Cars[i].currentSegment = 1;
				}
				if(Cars[i].distance > segment1Length && Cars[i].distance < segment2Length) { 
					Cars[i].currentSegment = 2;
				}
				if(Cars[i].distance > segment2Length && Cars[i].distance < segment3Length) {
					Cars[i].currentSegment = 3;
				}
				if(Cars[i].distance > trackLength ){ //If car has traveled the whole track, mark it as done.
					Cars[i].finished = true;
				}
				
				// If car is running and in the second segment,
				if( Cars[i].running == true && Cars[i].currentSegment == 2 ){
						Cars[i].segmentSpeed = segment2TopSpeed; //Change segment speed.	
				}
				
				//If car is running and about to enter the third segment, slow down.
				stoppingValue = getStoppingDistance(Cars[i], segment3TopSpeed); 				
				if( Cars[i].running == true &&
					Cars[i].slowingDown == false && 
					Cars[i].currentSegment == 2 &&
					Cars[i].distance >= (segment2Length - stoppingValue) - .5 &&  //Check if car is within range of where it should stop decelerating.
					Cars[i].distance <= (segment2Length - stoppingValue) + .5){ 
					
						Cars[i].setAcceleration(Cars[i].getAcceleration() * -1); //Set acceleration to negative to slow down.
						Cars[i].slowingDown = true;
				}
				
				//If desired velocity reached, change acceleration back to positive and keep going.
				if( Cars[i].running == true &&
					Cars[i].velocity < segment3TopSpeed -.5 &&	 //The .5 makes sure the velocity is not very close to the limit.
					Cars[i].distance > segment2Length ) {
						
						Cars[i].currentSegment = 3;
						Cars[i].segmentSpeed = segment3TopSpeed; //Set new segment top speed.
						Cars[i].setAcceleration(Cars[i].getAcceleration() * -1); //Set acceleration to positive.
				}
								
				if(Cars[i].running == true && Cars[i].finished == false) { //Only update when car is running and not finished.
					Cars[i].setVelocity(); //Update velocity and distance.
					Cars[i].setDistance();
				}
			}
		
			if(round(time,2) % printInterval == 0) { //Every time interval, print data.
				printLine(Cars, round(time,2));
			}
			
			time += deltaTime; //Increase time.
			
		}//Close while loop.

	}//Close main
}//Close class.
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

