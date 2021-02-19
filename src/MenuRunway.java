
public class MenuRunway extends CduMenu {

	public MenuRunway() {
		// TODO Auto-generated constructor stub
	}

	// Pages
	static int page = 1;
	static int totalPages = 1;

	public static void showMenu() {
		newCduPage = getScreen(page);
		updatePage();
	}

	public static String[] getScreen(int page) {
		String[] screen = new String[cduLines];
		
		// Thrust
		int currentThrustVal = psx.getAvgThrust();
		String currentThrustLabel = String.format("<%d%%", (currentThrustVal >= 0 ? currentThrustVal : currentThrustVal + 1));
		String ThrustLineExtra = CduMenu.createStringLength("_", 6 - currentThrustLabel.length());
		String finalThrustLabel = currentThrustLabel + ThrustLineExtra;
		
		// Speedbrake label
		String speedbrakeStatus;
		int speedbrakeVar = 388;
		int currentSpeedbrakeVal = Integer.valueOf(psx.qhVariables[speedbrakeVar]);
		if(currentSpeedbrakeVal < 40) {
			speedbrakeStatus = "<RETRACTED";
		} else if(currentSpeedbrakeVal < 60) {
			speedbrakeStatus = "<ARMED____";
		} else {
			speedbrakeStatus = "<EXTENDED_";
		}
		
		// Autobrakes
		String currentAutobrakes = psx.getAutobrakes();
		String autobrakeLabel = String.format("%8.8s>", currentAutobrakes);
		autobrakeLabel = autobrakeLabel.replace(" ", "_");
				
		// Flaps
		int currentFlapDeg = psx.getFlapDeg();
		String flapLabelPre = String.format("%d", currentFlapDeg);
		String flapLabel = String.format("%3.3s", flapLabelPre);
		flapLabel = flapLabel.replace(" ", "_");

		// Gear
		//int currentGear;
		//String[] gearStatus = {"N/A","UP","OFF","DOWN"};
		String gearLabel = String.format("%4.4s>", psx.getGearLever());
		gearLabel = gearLabel.replace(" ", "_");
		
		
		if (page == 1) {
			screen[0] = "_________RUNWAY_________";
			screen[1] = "_LANDING_____TAKEOFF/G/A";
			screen[2] = "<A/P_DISCONNECT___TO/GA>";
			screen[3] = "_______________AUTOBRAKE";
			screen[4] = "<A/T_DISCONNECT" + autobrakeLabel;
			screen[5] = "_SET_THRUST_____________";
			screen[6] = finalThrustLabel + "_______________UP>";
			screen[7] = "_SPEEDBRAKE________FLAPS";
			screen[8] = speedbrakeStatus + "_________DOWN>";
			screen[9] = "_THRUST_____________GEAR";
			screen[10] = "<REVERSE___________" + gearLabel;
			screen[13] = emptyLine;
		}

		return screen;
	}

	public static void skPushed(int id) {
		if (page == 1) {
			switch (id) {
			case 41:
				// A/P Disconnect
				Functions.autoPilotDisc();
				break;
			case 42:
				// A/T Disconnect
				Functions.autoThrottleDisc();
				break;
			case 43:
				// Set Thrust
				int newThrust = Integer.valueOf(CduMenu.scratchPad);
				Functions.setThrust(newThrust);
				break;
			case 44:
				// Speedbrake
				Functions.cycleSpeedbrake();
				break;
			case 45:
				// Full Reverse
				Functions.setThrust(-100);
				break;
			case 51:
				// TO/GA
				Functions.togaPushed();
				break;
			case 52:
				// Autobrakes
				int newAutobrakes = Integer.valueOf(CduMenu.scratchPad);
				Functions.setAutobrakes(newAutobrakes);
				break;
			case 53:
				Functions.flapLever("UP");
				break;
			case 54:
				// Flaps
				//int newFlap = Integer.valueOf(CduMenu.scratchPad);
				Functions.flapLever("DOWN");
				break;
			case 55:
				// Gear
				Functions.gearCycle();
				break;
			}
		}

		showMenu();
	}

}
