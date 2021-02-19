package backup;

public class MenuService extends CduMenu {

	static float trim = 0;

	// DOORS
	static boolean isAircraftOnGround = false;
	static int comDoorBits = 0; // Bits = 0 = Close, 1 = Open, 2 = OpenParked, 3 = OpenBoardL, 4 = OpenBoardR
	static boolean areDoorsOpen = false;
	static String doorBoardingSide = "";

	// Circuit Breakers
	static String[][] cbs = new String[59][2];

	// Altimeters
	public static int qnh; // QNH setting when altimeters are updated
	public static int altim;
	public static int trans;
	public static int altitude;
	public static String[] Qs448 = new String[8];
	public static String[] Qs449 = new String[8];

	public MenuService() {
		// TODO Auto-generated constructor stub
	}

	public static void showMenu() {
		send("Qs76=" + CduMenu.titlMenu);
		send("Qs77=" + CduMenu.qs77Menu + "___________TRIM_");
		CduMenu.motionFreeze((trim <= 9.99 ? "______" : "_____") + String.format("%.2f", trim) + "_");
		send("Qs79=" + CduMenu.qs79Menu + "__________DOORS_");
		send("Qs80=" + CduMenu.qs80Menu + "______" + (areDoorsOpen ? "_OPENED" : "_CLOSED") + "_"
				+ (doorBoardingSide.length() > 0 ? doorBoardingSide : "b") + (psx.qiVariables[257] == 1 ? ">" : "_"));
		send("Qs81=" + CduMenu.qs81Menu + "__________ALIGN_");
		send("Qs82=" + CduMenu.qs82Menu + "____________IRS>");
		send("Qs83=" + CduMenu.qs83Menu + "__________START_");
		send("Qs84=" + CduMenu.qs84Menu + "________ENGINES>");
		send("Qs85=" + CduMenu.qs85Menu + "__________CLEAR_");
		send("Qs86=" + CduMenu.qs86Menu + "_______CBS+MALF>");
		send("Qs87=" + CduMenu.qs87Menu + "_________UPDATE_");
		send("Qs88=" + CduMenu.qs88Menu + "<ACT>____ALTIMS>");

		setMenuColours();
	}

	public static void rskPushed(int id) {
		switch (id) {
		case 51:
			break;
		case 52:
			setDoors();
			break;
		case 53:
			alignIrs();
			break;
		case 54:
			startEng();
			break;
		case 55:
			clearCbMf();
			break;
		case 56:
			updateAltim();
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
			int calculatedCabinPressure = (int) ((int) 4 * Math.pow(10, -6) * Math.pow(altitude, 2) - 0.0443 * altitude
					+ 1130.5);
			int cabinPressure = (int) (altitude > 19000 ? calculatedCabinPressure : 0);

			System.out.println("Altitude: " + altitude + ", cabin Pressure = " + calculatedCabinPressure);
			send("Qs431=9999;9999;" + cabinPressure);
		}

		menuService();
	}

	public static void alignIrs() {
		send("Qh213=2");
		send("Qh214=2");
		send("Qh215=2");
		send("Qi90=600");
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		send("Qs356=0;0;0;0;0;0");
		send("Qs369=-;-;-100;S33o11.4_W068o58.5S33o11.4_W068o58.5Egb-");
		send("Qs353=0.0;0.0;0.0;0.0;0.0;0.0");
		send("Qs355=102000;102000;102000");
	}

	public static void startEng() {
		send("Qi90=500");
		send("Qh392=1\n" + "Qh393=1\n" + "Qh394=1\n" + "Qh395=1\n" + "Qs361=OcccOccc" + "Qh275=0\n" + "Qs361=OOccOOcc"
				+ "Qh276=0\n" + "Qs361=OOOcOOOc\n" + "Qh277=0\n" + "Qs361=OOOOOOOO\n" + "Qh278=0\n"
				+ "Qs358=290;259;259;259;548;548;548;548;LLLL;377;365;371;384;0;0;0;0;61;59;60;62;15;14;13;14;0;0;0;0\n"
				+ "Qs359=OOOO\n" + "Qh209=96\n" + "Qh210=96\n" + "Qh211=96\n" + "Qh212=96\n");
	}

	public static void clearCbMf() {
		// Cear malfunctions
		send("Qi90=500");
		for (int i = 1; i < cbs.length; i++) {
			send(cbs[i][0] + "=" + cbs[i][1].toLowerCase());
		}
		for (int i = 137; i <= 320; i++) {
			send("Qs" + i + "=0;6;-99");
		}
		send("Qs486=0;6;-99"); // Extra malfunction
		send("Qs494=0;6;-99"); // Extra malfunction
		send("Qs495=0;6;-99"); // Extra malfunction
		send("Qs496=1;6;-99"); // Extra malfunction
		send("Qi130=1"); // Clear extra CB
		send("Qs433=R18584;18470"); // Refill Oxygen
	}

	public static void updateAltim() {
		send("Qi90=500");

		String stdAltim = "101325";
		String Qs448string;
		String Qs449string;

		if (altitude > (trans + 1000)) {
			send("Qi38=101325");

			// Captain
			Qs448[3] = stdAltim;
			Qs448[4] = "2";
			// First Officer
			Qs449[3] = stdAltim;
			Qs449[4] = "2";
		} else {
			send("Qi38=" + qnh);

			// Captain
			Qs448[3] = String.valueOf(qnh);
			Qs448[4] = "1";
			// First Officer
			Qs449[3] = String.valueOf(qnh);
			Qs449[4] = "1";
		}
		
		Qs448string = PSXControl.arrayToString(Qs448);
		Qs449string = PSXControl.arrayToString(Qs449);
		send("Qs448=" + Qs448string);
		send("Qs449=" + Qs449string);
	}
}
