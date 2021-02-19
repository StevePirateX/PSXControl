
class Situations{

	// Vertical Speed
	private static long previousAltTime = 0;
	private static int previousAltitude = -999;
	
	// Heading Yaw Rate
	private static long previousYawTime = 0;
	private static int previousYaw = -999;
	
	protected static int getVerticalSpeed(int currentAltitude) {
		long currentTime = System.currentTimeMillis();
		
		if(previousAltitude == -999) {
			previousAltitude = currentAltitude;
			return 0;
		}
		previousAltTime = (previousAltTime == currentTime) ? (currentTime - 1) : previousAltTime;
		int timeDiff = (int) (currentTime - previousAltTime);
		int verticalSpeed = (int) (((currentAltitude - previousAltitude))/timeDiff);
		
		//System.out.println("Alt: " + currentAltitude + "\tPrev Alt: " + previousAltitude + " = VERTICAL SPEED (ft/s)= " + verticalSpeed);
		//System.out.println("Cur Time: " + currentTime + "\tPrev Time: " + previousTime + " = " + timeDiff);
		//System.out.println("Vertical Speed = " + (verticalSpeed * 60));
		
		previousAltitude = currentAltitude;
		previousAltTime = currentTime;
		return verticalSpeed;
	}
	
	protected static int getYawRate(int currentYaw) {
		long currentTime = System.currentTimeMillis();
		int yawRate = 0;
		if(previousYaw == -999) {
			previousYaw = currentYaw;
			return yawRate;
		}
		previousYawTime = (previousYawTime == currentTime) ? (currentTime - 1) : previousYawTime;
		int timeDiff = (int) (currentTime - previousYawTime);
		yawRate = (int) (((currentYaw - previousYaw))/timeDiff);
		
		//System.out.println("Alt: " + currentAltitude + "\tPrev Alt: " + previousAltitude + " = VERTICAL SPEED (ft/s)= " + verticalSpeed);
		//System.out.println("Cur Time: " + currentTime + "\tPrev Time: " + previousTime + " = " + timeDiff);
		//System.out.println("Vertical Speed = " + (verticalSpeed * 60));
		
		previousYaw = currentYaw;
		previousYawTime = currentTime;
		return yawRate;
	}
	
}
