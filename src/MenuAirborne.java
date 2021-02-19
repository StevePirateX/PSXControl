
public class MenuAirborne extends CduMenu {

	public MenuAirborne() {
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

		// Rudder Trim indicator
		int rudderTrim = psx.getRudderTrim();
		float rudderTrimRatio = ((float) (rudderTrim) + 8000) / 16000;
		float rudderCduChars = rudderTrimRatio * 23 - 1;
		int rudderCharsRounded = Math.round(rudderCduChars);
		String rudderTrimInd = CduMenu.createStringLength("-", Math.max(rudderCharsRounded, 0));
		rudderTrimInd = rudderTrimInd + "uu"
				+ CduMenu.createStringLength("-", Math.max(22 - rudderTrimInd.length(), 0));

		// Stab Trim Indictor
		int stabTrim = psx.getStabTrim();
		String stabTrimLabel = String.format("%.2f", (float) (stabTrim) / 1000);
		stabTrimLabel = String.format("%-5.5s", stabTrimLabel);
		stabTrimLabel = stabTrimLabel.replace(" ", "_");

		// Autobrakes
		String currentAutobrakes = psx.getAutobrakes();
		String autobrakeLabel = String.format("%8.8s>", currentAutobrakes);
		autobrakeLabel = autobrakeLabel.replace(" ", "_");

		// Gear
		// int currentGear;
		// String[] gearStatus = {"N/A","UP","OFF","DOWN"};
		String gearLabel = String.format("%4.4s>", psx.getGearLever());
		gearLabel = gearLabel.replace(" ", "_");

		if (page == 1) {
			screen[0] = "________AIRBORNE________";
			screen[1] = "_________RUDDER_________";
			screen[2] = "<LEFT_____TRIM____RIGHT>";
			screen[3] = rudderTrimInd;
			screen[4] = "<RESET_ALTIM___" + autobrakeLabel;
			screen[5] = "_______________AUTOBRAKE";
			screen[6] = "<NOSE_DOWN___________UP>";
			screen[7] = "_STAB_TRIM_" + stabTrimLabel + "___FLAPS";
			screen[8] = "<NOSE_UP___________DOWN>";
			screen[9] = "_GROUND_____________GEAR";
			screen[10] = "<PROX______________" + gearLabel;
			screen[13] = emptyLine;
		}

		return screen;
	}

	public static void skPushed(int id) {

		if (page == 1) {
			switch (id) {
			case 41:
				// Left Rudder Trim
				if (CduMenu.deleteActive) {
					Functions.setRudderTrim(0);
					clearPushed();
				} else
					Functions.setRudderTrim(psx.qiVariables[psx.varRudderTrim] - 200);
				break;
			case 42:
				// Update Altim
				Functions.updateAltim();
				break;
			case 43:
				// Stab Trim Nose Down
				Functions.setStabTrim(psx.qiVariables[psx.varStabTrim] - 200);
				break;
			case 44:
				// Stab Trim Nose Up
				Functions.setStabTrim(psx.qiVariables[psx.varStabTrim] + 200);
				break;
			case 45:
				// Ground Prox
				Functions.groundProx();
				break;
			case 51:
				// Right Rudder Trim
				if (CduMenu.deleteActive) {
					Functions.setRudderTrim(0);
					clearPushed();
				} else
					Functions.setRudderTrim(psx.qiVariables[psx.varRudderTrim] + 200);
				break;
			case 52:
				// Autobrakes
				int newAutobrakes = Integer.valueOf(CduMenu.scratchPad);
				Functions.setAutobrakes(newAutobrakes);
				break;
			case 53:
				// Set Stab Trim
				// Functions.setStabTrim((int) (Float.parseFloat(CduMenu.scratchPad) * 1000));
				// CduMenu.clearScratchPad();

				// Flaps Up one increment
				Functions.flapLever("UP");
				break;
			case 54:
				// Flaps
				// int newFlap = Integer.valueOf(CduMenu.scratchPad);
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
