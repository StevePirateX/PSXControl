
public class MenuService extends CduMenu {

	// Pages
	static int page = 1;
	static int totalPages = 1;
			
			
	static float trim = 0;

	// DOORS
	static boolean isAircraftOnGround = false;
	static int comDoorBits = 0; // Bits = 0 = Close, 1 = Open, 2 = OpenParked, 3 = OpenBoardL, 4 = OpenBoardR
	static boolean areDoorsOpen = false;
	static String doorBoardingSide = "";

	// Circuit Breakers

	// Altimeters
	//public static int qnh; // QNH setting when altimeters are updated
	//public static int altim;
	///public static int trans;
	//public static String[] Qs448 = new String[8];
	//public static String[] Qs449 = new String[8];

	public MenuService() {
		// TODO Auto-generated constructor stub
	}

	public static String[] getScreen(int page) {
		String[] screen = new String[cduLines];
		
		
		if(page == 1) {
			// Fuel Flow
			String fuelFlow = psx.getFuelFlow();
			int fuelFlowLength = psx.getFuelFlow().length();
			fuelFlow = String.format("%-6s", fuelFlow.toUpperCase()).replace(" " , "_");
			System.out.println(fuelFlow);
			
			// Fuel
			float fuelQty = psx.getFuel();
			String fuelQtyFormatted = String.format("%5.1f", fuelQty);
			fuelQtyFormatted.replace(" ", "_");
			
			// ZFW
			float zfw = psx.getCg();
			String zfwFormatted = String.format("%5.1f", zfw);
			
			
			
			screen[0]  = "________SERVICE_________";
			screen[1]  = "_ENGINES________________";
			screen[2]  = "<START__________________";
			screen[3]  = "_APU_________________IRS";
			screen[4]  = "<START____________ALIGN>";
			screen[5]  = "_FUEL_FLOW______SET_FUEL";
			screen[6]  = "<" + fuelFlow + "___________" + fuelQtyFormatted + ">+" + (fuelFlowLength == 2 ? "+-+" : (fuelFlowLength == 4 ? "+++-+" : "+"));
			screen[7]  = "_________________SET_ZFW";
			screen[8]  = "<RESET_CB_________" + zfwFormatted + ">";
			screen[9]  = "___________________ALTIM";
			screen[10] = "<RESET_MALF______UPDATE>";
			screen[13] = emptyLine;
		}
		
		
		return screen;
	}
	
	
	
	
	public static void showMenu() {
		newCduPage = getScreen(page);
		updatePage();
		//send("Qs76=" + CduMenu.menuLine0);
		//send("Qs77=" + CduMenu.menuLine1 + "___________TRIM_");
		//CduMenu.staticBottomMenu();
		//send("Qs79=" + CduMenu.menuLine3 + "__________DOORS_");
		//send("Qs80=" + CduMenu.menuLine4 + "______" + (areDoorsOpen ? "_OPENED" : "_CLOSED") + "_"
		//		+ (doorBoardingSide.length() > 0 ? doorBoardingSide : "b") + (psx.qiVariables[257] == 1 ? ">" : "_"));
		//send("Qs81=" + CduMenu.menuLine5 + "__________ALIGN_");
		//send("Qs82=" + CduMenu.menuLine6 + "____________IRS>");
		//send("Qs83=" + CduMenu.qs83Menu + "__________START_");
		//send("Qs84=" + CduMenu.qs84Menu + "________ENGINES>");
		//send("Qs85=" + CduMenu.qs85Menu + "__________CLEAR_");
		//send("Qs86=" + CduMenu.qs86Menu + "_______CBS+MALF>");
		//send("Qs87=" + CduMenu.qs87Menu + "_________UPDATE_");
		//send("Qs88=" + CduMenu.qs88Menu + "<ACT>____ALTIMS>");

		//setMenuColours();
	}

	public static void skPushed(int id) {
		switch (id) {
		case 41:
			Functions.startEng();
			break;
		case 42:
			Functions.startApu();
			break;
		case 43:
			Functions.cycleFuelFlow();
			displayMenu();
			break;
		case 44:
			Functions.clearCb();
			break;
		case 45:
			Functions.clearMalf();
			break;
		case 52:
			Functions.alignIrs();
			break;
		case 53:
			float newFuel = Float.valueOf(CduMenu.scratchPad);
			Functions.setFuel(newFuel);
			displayMenu();
			break;
		case 54:
			// Set CG
			float newCg = Float.valueOf(CduMenu.scratchPad);
			Functions.setCg(newCg);
			displayMenu();
			break;
		case 55:
			Functions.updateAltim();
			break;
		}
		showMenu();
	}

	public static void setDoors() {
		System.out.println("Is Aircraft on Ground? " + psx.qiVariables[257]);
		isAircraftOnGround = (psx.qiVariables[257] == 1 ? true : false);
		if (isAircraftOnGround) {
			int boardLeft = 8;
			int boardRight = 16;

			System.out.println("Commbits: " + MenuService.comDoorBits + "\tAreDoorsOpen? " + MenuService.areDoorsOpen
					+ "\tBoarding Side: " + MenuService.doorBoardingSide);

			if (deleteActive || scratchPad.equals("B") || scratchPad.equals("L") || scratchPad.equals("R")) {
				MenuService.doorBoardingSide = scratchPad;
				if (deleteActive) {
					deleteActive = false;
					scratchPadLock = false;
				}
			} else {
				if (areDoorsOpen) {
					// Doors to close
					System.out.println("Closing Doors");
					comDoorBits = 1;
				} else if (!areDoorsOpen & isAircraftOnGround) {
					// Doors to open
					System.out.println("Opening Doors");
					comDoorBits = 2;
				}
			}

			if (doorBoardingSide.equals("B")) {
				send("Qi179=" + (comDoorBits + boardLeft + boardRight));
			} else if (doorBoardingSide.equals("L")) {
				send("Qi179=" + (comDoorBits + boardLeft));
			} else if (doorBoardingSide.equals("R")) {
				send("Qi179=" + (comDoorBits + boardRight));
			} else {
				send("Qi179=" + comDoorBits);
			}

			scratchPad = "";
			send("Qi90=500");
			send("Qs89=" + scratchPad);
		}

		// Reset cabin pressure
		System.out.println("Is Aircraft on Ground? " + isAircraftOnGround);
		if (areDoorsOpen & !isAircraftOnGround) {
			int calculatedCabinPressure = (int) ((int) 4 * Math.pow(10, -6) * Math.pow(psx.getAltitude(), 2) - 0.0443 * psx.getAltitude() + 1130.5);
			int cabinPressure = (int) (psx.getAltitude() > 19000 ? calculatedCabinPressure : 0);

			System.out.println("Altitude: " + psx.getAltitude() + ", cabin Pressure = " + calculatedCabinPressure);
			send("Qs431=9999;9999;" + cabinPressure);
		}

		menuService();
	}




	


}
