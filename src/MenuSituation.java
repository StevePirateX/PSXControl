import java.util.Calendar;

public class MenuSituation extends CduMenu {

	// Pages
	static int page = 1;
	static int totalPages = 1;

	// PSX Variables for situation tracking and recalling
	protected static String[] situSVariables;
	protected static String[] situHVariables;
	protected static int[] situIVariables;

	protected static String[] eventSVariables;
	protected static String[] eventHVariables;
	protected static int[] eventIVariables;

	public static boolean didAddonCauseReload = false; // if true - the addon caused the reload
	public static boolean situReload = false; // If the Situ button is pressed
	public static boolean eventReload = false; // If event reload button is pressed

	public static boolean loadOverwrite = true; // To get the initial values
	public static int eventRcd = 0; // If an event has been recorded

	public static int verticalSpeed = 0; // Feet per second
	public static int yawRate = 0;

	// FUEL & LOADING
	static float cg = 0;
	//static float fuel = 40;

	public MenuSituation() {
		// TODO Auto-generated constructor stub
	}

	public static void showMenu() {
		newCduPage = getScreen(page);
		updatePage();
		// send("Qs76=" + CduMenu.menuLine0);
		// send("Qs77=" + CduMenu.menuLine1 + "_________RELOAD_");
		// CduMenu.staticBottomMenu();
		// send("Qs79=" + CduMenu.menuLine3 + "_________" + (eventRcd > 0 ? "RELOAD" :
		// "______") + "_");
		// send("Qs80=" + CduMenu.menuLine4 + "__________" + (eventRcd > 0 ? "EVENT>" :
		// "______"));
		// send("Qs81=" + CduMenu.menuLine5 + "_______SET_FUEL_");
		// send("Qs82=" + CduMenu.menuLine6 + "________" + (fuel < 9.95 ? "_" : "") +
		// (fuel < 99.95 ? "_" : "")
		// + (fuel < 999.95 ? "_" : "") + (fuel < 9999.95 ? "_" : "") +
		// String.format("%.1f>", fuel));
		// send("Qs83=" + CduMenu.qs83Menu + "_________SET_CG_");
		/// send("Qs84=" + CduMenu.qs84Menu + (cg < 10 ? "_" : "") +
		// String.format("___________%.1f>", cg));
		// send("Qs85=" + CduMenu.qs85Menu + "_________RECORD_");
		// send("Qs86=" + CduMenu.qs86Menu + "<ACT>_____EVENT>");
		// send("Qs87=" + CduMenu.qs87Menu + "_______PSX_TIME_");
		// send("Qs88=" + CduMenu.qs88Menu + "__________" + CduMenu.viewPSXTime
		// + "Z>++++++++++++++++++++++-+");
		// send("Qs89=" + CduMenu.qs89Menu + defaultLongRightMenu);

		// CduMenu.setMenuColours();
	}

	public static String[] getScreen(int page) {
		String[] screen = new String[cduLines];

		if (page == 1) {
			screen[0] = "_______SITUATION________";
			screen[1] = "_SITUATION_________EVENT";
			screen[2] = "<RELOAD__________RELOAD>";
			screen[3] = emptyLine;
			screen[4] = emptyLine;
			screen[5] = "_PSX_TIME__________EVENT";
			screen[6] = "<" + CduMenu.viewPSXTime + "Z___________RECORD>+++++-++++++++++++++++++";
			screen[7] = emptyLine;
			screen[8] = emptyLine;
			screen[9] = emptyLine;
			screen[10] = emptyLine;
			screen[13] = emptyLine;
		}

		return screen;
	}

	public static void skPushed(int id) {
		switch (id) {
		case 41:
			reloadSitu();
			break;
		case 51:
			reloadEvent();
			break;
		case 53:
			recordEvent();
			//setFuel();
			break;
		case 54:
			//setCg();
			break;
		case 43:
			setPsxTime();
			//recordEvent();
			break;
		case 56:
			
			break;
		}
		showMenu();
	}

	public static void reloadSitu() {

		situReload = true;
		didAddonCauseReload = true;
		boolean isTerrOvrd = PSXControl.psx.terrOvrd;

		System.out.println("Number of i variables = " + situIVariables.length);
		send("load1");
		/*
		 * System.out.println("------LOAD1 SENT------"); for(int i=0;i<=31;i++) { int j
		 * = situIVariables[i]; String s = "Qi" + i + "=" + j; send(s);
		 * //System.out.println(s); } for(int i=0;i<=3;i++) { String s = "Qs" + i + "="
		 * + situSVariables[i].toString(); send(s); //System.out.println(s); }
		 */

		send("load2");
		System.out.println("------LOAD2 SENT------");

		for (int i = 32; i <= PSXControl.psx.iVariables; i++) {
			// System.out.println("INTEGER" + situIVariables[i]);
			// if(i!=(257)){
			Integer j = situIVariables[i];
			if (j.equals(null)) {
				j = 0;
			}
			String s = "Qi" + i + "=" + j;
			if (i == 257) {
				// System.out.println(s);
				s = "Qi257=0";
			}
			send(s);
			// System.out.println(s);
			// }

		}

		for (int i = 0; i <= PSXControl.psx.hVariables; i++) {
			String s = "Qh" + i + "=" + situHVariables[i];
			if (situHVariables[i] == null) {
				s = "Qh" + i + "=";
			}
			send(s);
			// System.out.println(s);
		}

		for (int i = 4; i <= PSXControl.psx.sVariables; i++) {

			String s = "Qs" + i + "=" + situSVariables[i];
			if (i == 122) {
				s = "Qs" + i + "=1;" + situSVariables[i].substring(2, situSVariables[i].length());
			}
			if (situSVariables[i] == null) {
				s = "Qs" + i + "=";
			}

			if (i == 406 || i == 351 || i == 76 || i == 77 || i == 78 || i == 79 || i == 80 || i == 81 || i == 82
					|| i == 83 || i == 84 || i == 85 || i == 86 || i == 87 || i == 88 || i == 89) {
			} else {
				// if(i<352) {
				send(s);
				// System.out.println(s);
			}
			if (i == 121 || i == 122) {
				// System.out.println(s);
			}
		}

		PSXControl.psx.motionFreeze = true;
		send("Qi129=2");

		if (isTerrOvrd) {
			// send("Qh168=1");
			MenuActions.groundProx(true);
			send("Qs132=279;");
			PSXControl.psx.terrOvrd = true;
		} else {
			// send("Qh168=0");
			MenuActions.groundProx(false);
			send("Qs132=");
			PSXControl.psx.terrOvrd = false;
		}

		send("load3");
		System.out.println("------LOAD3 SENT------");
		PSXControl.psx.setPsxTime();
		send("Qs406=" + situSVariables[406].substring(0, 4) + PSXControl.CDUPosition);
		System.out.println("Qs406=" + situSVariables[406].substring(0, 4) + PSXControl.CDUPosition);
		showMenu();
		Functions.updateAltim();
	}

	public static void reloadEvent() {
		System.out.println("Event Reload");
		situReload = true;
		didAddonCauseReload = true;
		boolean isTerrOvrd = PSXControl.psx.terrOvrd;

		System.out.println("Number of variables = " + eventIVariables.length);
		send("load1");
		System.out.println("------LOAD1 SENT------");
		/*
		 * for(int i=0;i<=31;i++) { int j = eventIVariables[i]; String s = "Qi" + i +
		 * "=" + j; send(s); //System.out.println(s); } for(int i=0;i<=3;i++) { String s
		 * = "Qs" + i + "=" + eventSVariables[i].toString(); send(s);
		 * //System.out.println(s); }
		 */
		send("load2");
		System.out.println("------LOAD2 SENT------");

		for (int i = 32; i <= PSXControl.psx.iVariables; i++) {
			// System.out.println("INTEGER" + eventIVariables[i]);
			// if(i!=(257)){
			Integer j = eventIVariables[i];
			if (j.equals(null)) {
				j = 0;
			}
			String s = "Qi" + i + "=" + j;
			if (i == 257) {
				System.out.println(s);
				s = "Qi257=0";
			}
			send(s);
			// System.out.println(s);
			// }

		}

		for (int i = 0; i <= PSXControl.psx.hVariables; i++) {
			String s = "Qh" + i + "=" + eventHVariables[i];
			if (eventHVariables[i] == null) {
				s = "Qh" + i + "=";
			}
			send(s);
			// System.out.println(s);
		}

		for (int i = 4; i <= PSXControl.psx.sVariables; i++) {

			String s = "Qs" + i + "=" + eventSVariables[i];
			if (i == 122) {
				s = "Qs" + i + "=1;" + eventSVariables[i].substring(2, eventSVariables[i].length());
			}
			if (eventSVariables[i] == null) {
				s = "Qs" + i + "=";
			}

			if (i == 406 || i == 351 || i == 76 || i == 77 || i == 78 || i == 79 || i == 80 || i == 81 || i == 82
					|| i == 83 || i == 84 || i == 85 || i == 86 || i == 87 || i == 88 || i == 89) {
			} else {
				// if(i<352) {
				send(s);
				// System.out.println(s);
			}
			if (i == 121 || i == 122) {
				// System.out.println(s);
			}
		}

		PSXControl.psx.motionFreeze = true;
		send("Qi129=2");

		if (isTerrOvrd) {
			// send("Qh168=1");
			MenuActions.groundProx(true);
			send("Qs132=279;");
			PSXControl.psx.terrOvrd = true;
		} else {
			// send("Qh168=0");
			MenuActions.groundProx(false);
			send("Qs132=");
			PSXControl.psx.terrOvrd = false;
		}

		send("load3");
		System.out.println("------LOAD3 SENT------");
		PSXControl.psx.setPsxTime();
		send("Qs406=" + eventSVariables[406].substring(0, 4) + "11");
		System.out.println("Qs406=" + eventSVariables[406].substring(0, 4) + "11");
		showMenu();
		Functions.updateAltim();


		showMenu();

	}

	

	

	public static void recordEvent() {

		// Event Rcd
		send("Qi90=500");
		eventRcd = 1;
		showMenu();

		System.out.println("------EVENT RECORD ---------");
		netThread.viewMessages = true;
		// send("start");
		// send("bang");
		System.out.println("Updated Event variables");
		eventIVariables = PSXControl.psx.qiVariables.clone();
		eventHVariables = PSXControl.psx.qhVariables.clone();
		eventSVariables = PSXControl.psx.qsVariables.clone();

		// Create custom Qs122 relocation
		System.out.println("Customizing QS122");
		String qs122 = "";
		System.out.println("Creating Array");
		// Turn Qs121 into an integer array
		String[] qs121StringArray = eventSVariables[121].split(";");
		float[] qs121 = new float[qs121StringArray.length];
		for (int i = 0; i < qs121.length; i++) {
			System.out.println("Creating: " + i);
			qs121[i] = Float.parseFloat(qs121StringArray[i]);
		}
		// End of Qs121 into to array
		System.out.println("Array Created");
		int updateMode = 1;
		int pitch = (int) (qs121[0] / 100); // Pitch
		int bank = (int) (qs121[1] / 100); // Bank
		int heading = (int) (qs121[2] * 1000); // Heading
		int altitude = (int) (qs121[3] / 1000); // Altitude
		int vs = verticalSpeed; // Vertical Speed (ft/s)
		int tas = (int) (qs121[4] / 1000); // True Airspeed
		int yaw = 0; // Yaw Momentum (heading change)
		float lat = qs121[5]; // Latitude
		float longitude = qs121[6]; // Longitude
		int elev = 50; // Elevation
		System.out.println("QS122 Generated");
		qs122 = updateMode + ";" + pitch + ";" + bank + ";" + heading + ";" + altitude + ";" + vs + ";" + tas + ";"
				+ yaw + ";" + lat + ";" + longitude + ";" + elev;
		System.out.println(qs122);
		eventSVariables[122] = qs122;
		System.out.println("QS122 Saved");
	}

	public static void setPsxTime() {

		// Set Time
		// SITU PAGE
		if (CduMenu.scratchPad.length() == 4) {
			if (Integer.parseInt(CduMenu.scratchPad.substring(0, 2)) <= 23
					& Integer.parseInt(CduMenu.scratchPad.substring(2, 4)) <= 59) {

				send("Qi90=500");
				String input = CduMenu.scratchPad;

				Calendar now = Calendar.getInstance();
				now.setTimeInMillis(System.currentTimeMillis());
				CduMenu.psxTime.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE),
						Integer.parseInt(input.substring(0, 2)), Integer.parseInt(input.substring(2, 4)), 0);

				send("Qs123=" + CduMenu.psxTime.getTimeInMillis());
				send("Qs124=" + CduMenu.psxTime.getTimeInMillis());
				send("Qs125=" + CduMenu.psxTime.getTimeInMillis());

				send("Qs89=________________________");
				CduMenu.scratchPad = "";
			}
		} else if (CduMenu.scratchPad.length() == 0) {
			send("Qi90=500");
			PSXControl.psx.setPsxTime();
			// send("Qs89=" + PSXControlNetThread.viewPSXTime +
			// "____________________");
			// scratchPad = PSXControlNetThread.viewPSXTime;

			new Thread(new Runnable() {
				public void run() {
					send("Qs89=REAL_TIME_INSERTED______");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					send("Qs89=________________________");
				}
			}).start();

		} else {
			// Invalid entry
			send("Qi90=500");
			CduMenu.invalidEntry = true;
			send("Qs89=INVALID_ENTRY___________");
			// scratchPad += "_";
		}
	}
}
