
public class MenuGround extends CduMenu {

	public MenuGround() {
		// TODO Auto-generated constructor stub
	}

	// Pages
	static int page = 1;
	static int totalPages = 2;

	public static void showMenu() {
		newCduPage = getScreen(page);
		updatePage();
	}

	public static String[] getScreen(int page) {
		String[] screen = new String[cduLines];

		// Tiller indicator
		int tiller = psx.getTillerPos();
		float tillerRatio = ((float) (tiller) + 999) / 1998;
		float tillerCduChars = tillerRatio * 23 - 1;
		int tillerCharsRounded = Math.round(tillerCduChars);
		String tillerInd = CduMenu.createStringLength("-", Math.max(tillerCharsRounded, 0));
		tillerInd = tillerInd + "uu" + CduMenu.createStringLength("-", Math.max(22 - tillerInd.length(), 0));

		// External Power
		boolean extPower = (psx.qiVariables[132] % 2 >= 1 || psx.qiVariables[132] % 16 >= 8);
		String extPowerLabel = (extPower ? "<ON_" : "<OFF");

		// Bleed Air
		boolean bleedAir = (psx.qiVariables[174] % 2 >= 1);
		String bleedAirLabel = (bleedAir ? "<ON_" : "<OFF");

		// Conditioned Air
		boolean conditionedAir = (psx.qiVariables[174] % 4 >= 2);
		String conditionedAirLabel = (conditionedAir ? "<ON_" : "<OFF");

		// Autobrakes
		String currentAutobrakes = psx.getAutobrakes();
		String autobrakeLabel = String.format("%8.8s>", currentAutobrakes);
		autobrakeLabel = autobrakeLabel.replace(" ", "_");

		// Flaps
		int currentFlapDeg = psx.getFlapDeg();
		String flapLabelPre = String.format("%d>", currentFlapDeg);
		String flapLabel = String.format("%3.3s", flapLabelPre);
		flapLabel = flapLabel.replace(" ", "_");

		// Fuel
		float currentFuel = psx.getFuel(); // in x1000 kgs
		String currentFuelString = String.format("%.1f", currentFuel);
		String fuelLabel = String.format("%5.5s>", currentFuelString);
		fuelLabel = fuelLabel.replace(" ", "_");

		// CG
		float currentCg = psx.getCg(); // in x1000 kgs
		String currentCgString = String.format("%.1f", currentCg);
		String cgLabel = String.format("%4.4s>", currentCgString);
		cgLabel = cgLabel.replace(" ", "_");
		
		// Boarding
		int boardingDoors = psx.getBoardingDoors();
		String boardingDoorsString;
		if(boardingDoors == 1)
			boardingDoorsString = "CLOSED";
		else if (boardingDoors == 2)
			boardingDoorsString = "OPEN__";
		else
			boardingDoorsString = "ARMED_";
		boolean isBoardingLeftActive = psx.getBoardingL();
		boolean isBoardingRightActive = psx.getBoardingR();
		String boardingLeftString = (isBoardingLeftActive ? "<ACT>" : "_____");
		String boardingRightString = (isBoardingRightActive ? "<ACT>" : "_____");

		if (page == 1) {
			screen[0] = "_________GROUND_____" + page + "/" + totalPages + "_++++++++++++++++++++---+";
			screen[1] = "________________________";
			screen[2] = "<LEFT____TILLER___RIGHT>";
			screen[3] = tillerInd;
			screen[4] = "<CENTRE_______PARKBRAKE>";
			screen[5] = "_EXT_PWR_______AUTOBRAKE";
			screen[6] = extPowerLabel + "___________" + autobrakeLabel;
			screen[7] = "_BLEED_AIR_________FLAPS";
			screen[8] = bleedAirLabel + "_________________" + flapLabel;
			screen[9] = "_CONDITIONED AIR___ALTIM";
			screen[10] = conditionedAirLabel + "_____________UPDATE>";
			screen[13] = emptyLine;
		} else if (page == 2) {
			screen[0] = "_________GROUND_____" + page + "/" + totalPages + "_++++++++++++++++++++---+";
			screen[1] = "________________________";
			screen[2] = "<LEFT____TILLER___RIGHT>";
			screen[3] = tillerInd;
			screen[4] = "<CENTRE_______IRS_ALIGN>";
			screen[5] = "_DOORS__________SET_FUEL";
			screen[6] = "<" + boardingDoorsString + "___________" + fuelLabel;
			screen[7] = "_ALLOW_BOARDING___SET_CG";
			screen[8] = "<LEFT_" + boardingLeftString + "________" + cgLabel;
			screen[9] = "_________________SERVICE";
			screen[10] = "<RIGHT" + boardingRightString + "____AIRCRAFT>";
			screen[13] = emptyLine;
		}

		return screen;
	}

	static void setPage(int direction) {
		if (direction == 1) {
			if (page != totalPages)
				page++;
			else
				page = 1;
		} else if (direction == -1) {
			if (page != 1)
				page--;
			else
				page = totalPages;
		}
		showMenu();
	}

	public static void skPushed(int id) {
		if (page == 1) {
			switch (id) {
			case 41:
				// Left Tiller
				Functions.setTiller(Integer.parseInt(psx.qhVariables[psx.varTiller]) - 220);
				break;
			case 42:
				// Centre Tiller
				Functions.setTiller(0);
				break;
			case 43:
				// Ext Power
				Functions.tglExtPower();
				break;
			case 44:
				// Bleed Air
				Functions.tglBleedAir();
				break;
			case 45:
				// Conditioned Air
				Functions.tglConditionedAir();
				break;
			case 51:
				// Right Tiller
				Functions.setTiller(Integer.parseInt(psx.qhVariables[psx.varTiller]) + 220);
				break;
			case 52:
				// Parkbrake
				Functions.setParkBrake();
				break;
			case 53:
				// Autobrakes
				int newAutobrakes = Integer.valueOf(CduMenu.scratchPad);
				Functions.setAutobrakes(newAutobrakes);
				break;
			case 54:
				// Flaps
				String newFlap = CduMenu.scratchPad;
				Functions.flapLever(newFlap);
				break;
			case 55:
				// Update Altim
				Functions.updateAltim();
				break;
			}
		} else if (page == 2) {
			switch (id) {
			case 41:
				// Left Tiller
				Functions.setTiller(Integer.parseInt(psx.qhVariables[psx.varTiller]) - 220);
				break;
			case 42:
				// Centre Tiller
				Functions.setTiller(0);
				break;
			case 43:
				// Doors Open / Closed
				Functions.tglBoardingDoors();
				break;
			case 44:
				// Boarding Left
				Functions.tglBoardingL();
				break;
			case 45:
				// Boarding Right
				Functions.tglBoardingR();
				break;
			case 51:
				// Right Tiller
				Functions.setTiller(Integer.parseInt(psx.qhVariables[psx.varTiller]) + 220);
				break;
			case 52:
				// Align IRS
				Functions.alignIrs();
				break;
			case 53:
				// Set Fuel
				Functions.setFuel(Float.parseFloat(CduMenu.scratchPad));
				break;
			case 54:
				// Set CG
				float newCg = Float.valueOf(CduMenu.scratchPad);
				Functions.setCg(newCg);
				break;
			case 55:
				// Service Aircraft
				break;
			}
		}

		showMenu();
	}

}
