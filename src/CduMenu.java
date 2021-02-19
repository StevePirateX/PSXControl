import java.util.Calendar;
import java.util.TimeZone;

class CduMenu extends PSXControl {

	// VARIABLES
	// Static Menu (Left Side)

	static int cduLines = 14;
	static String[] mainMenuPage = new String[cduLines];
	static String[] cduColours = new String[cduLines];
	static String defaultColour = "w";

	static String[] currentCduPage = new String[cduLines];
	static String[] newCduPage = new String[cduLines];

	static int startingLine = 76;
	static String emptyLine = "________________________";

	// Standard Colours
	static String textColourRed = "r";
	static String textColourWhite = "w";
	static String textColourAmber = "a";
	static String textColourCyan = "c";
	
	static int menuLength = 13;
	static String menuColour = "b"; // Grey Background
	static int cduLength = 24;
	static int qsStart = 514;

	static final int PAGE_MAINMENU = 0;
	static final int PAGE_GROUND = 1;
	static final int PAGE_RUNWAY = 2;
	static final int PAGE_AIRBORNE = 3;
	static final int PAGE_ENGINES = 4;
	static final int PAGE_SERVICE = 5;
	static final int PAGE_WEATHER = 6;
	static final int PAGE_LAYOUTS = 7;
	static final int PAGE_PANELS = 8;
	static final int PAGE_SITUATION = 9;
	static final int PAGE_OTHER = 10;

	// Default Page
	static int activePage = PAGE_MAINMENU; // See "PAGE_*" constants for assignements

	// Cdu Handling
	static String scratchPad = ""; // This is the text in the scratchpad
	public static boolean scratchPadLock = false; // Locks entry such as when you press DELETE
	public static boolean deleteActive = false; // If the user presses delete, the user is unable to type
	public static String[] cduKeyboard = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	public static String viewPSXTime = "----";
	public static Calendar psxTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	public static boolean invalidEntry = false;

	public CduMenu() {

		for (int i = 0; i < cduLines; i++) {
			cduColours[i] = createStringLength(defaultColour, 24);
		}

		mainMenuPage[0] = "____PSX_CONTROL_MENU____";
		mainMenuPage[1] = "____________" + "____________";
		mainMenuPage[2] = "<GROUND_____" + "____WEATHER>";
		mainMenuPage[3] = "____________" + "____________";
		mainMenuPage[4] = "<RUNWAY_____" + "____LAYOUTS>";
		mainMenuPage[5] = "____________" + "____________";
		mainMenuPage[6] = "<AIRBORNE___" + "_____PANELS>";
		mainMenuPage[7] = "____________" + "____________";
		mainMenuPage[8] = "<ENGINES____" + "__SITUATION>";
		mainMenuPage[9] = "____________" + "____________";
		mainMenuPage[10] = "<SERVICE____" + "______OTHER>";
		mainMenuPage[11] = "------------" + "------------";
		mainMenuPage[12] = "<INDEX_______MOTION_FRZ>";
		mainMenuPage[13] = "________________________";
	}

	// ACT / Non-ACT Menus
	static void staticBottomMenu() { // String must be 10 characters long of what to display on the right
		String textColourRed = "r";
		String textColourWhite = "w";
		int motionFreezePosition = 8;

		if (PSXControlNetThread.activeMenu == 1) {
			if (psx.motionFreeze) {
				String newQs88Menu = IndexLsk() + "<ACT>" + mainMenuPage[12].substring(motionFreezePosition + 5, 24);
				send("Qs88=" + newQs88Menu);

				cduColours[12] = createStringLength(textColourWhite, 8) + createStringLength(textColourRed, 16);
				send("Qs526=" + cduColours[12].toString());
				// System.out.println(qs516Colour);
			} else if (!psx.motionFreeze) {
				String newQs88Menu = IndexLsk() + "_____" + mainMenuPage[12].substring(motionFreezePosition + 5, 24);
				send("Qs88=" + newQs88Menu);

				cduColours[12] = createStringLength(textColourWhite, 24);
				send("Qs526=" + cduColours[12].toString());
				// System.out.println(qs515Colour);
			}
		}
	}

	static void terrOvrd(String s) { // String must be 10 characters long of what to display on the right
		if (PSXControlNetThread.activeMenu == 1) {
			// if (psx.terrOvrd)
			// send("Qs80=" + menuLine4 + "<ACT>_" + s);
			// else if (!psx.terrOvrd)
			// send("Qs80=" + menuLine4 + "______" + s);
		}
	}

	/*
	 * public static void updateMenu(int updateType) { //
	 * System.out.println(activePage); if (psx.isActiveSubSystem) { switch
	 * (activePage) { case PAGE_MAINMENU: showMainMenu(); break; case PAGE_WEATHER:
	 * boolean windUpdate = false; if (updateType == 1) { windUpdate = true; }
	 * menuWeather(windUpdate); break; case PAGE_SITUATION: menuSitu(); break; case
	 * PAGE_SERVICE: menuService(); break; default: break; }
	 * 
	 * staticBottomMenu(); } }
	 */

	static String IndexLsk() {
		if (activePage != PAGE_MAINMENU) {
			return "<INDEX__";
		} else {
			return "________";
		}
	}

	// MAIN MENU
	public static void showMainMenu() {
		// activePage = 0;
		if (psx.isActiveSubSystem) {
			for (int i = 0; i < newCduPage.length; i++) {
				newCduPage[i] = mainMenuPage[i];
				if (newCduPage[i] != currentCduPage[i]) {
					send("Qs" + (i + startingLine) + "=" + newCduPage[i].toString());
					currentCduPage[i] = newCduPage[i];
				}

			}

			cduColours[2] = createStringLength(textColourWhite, 12) + createStringLength(textColourWhite, 12);
			cduColours[4] = createStringLength(textColourWhite, 12) + createStringLength(textColourWhite, 12);
			cduColours[6] = createStringLength(textColourWhite, 12) + createStringLength(textColourWhite, 12);
			cduColours[8] = createStringLength(textColourCyan, 12) + createStringLength(textColourWhite, 12);
			cduColours[10] = createStringLength(textColourWhite, 12) + createStringLength(textColourAmber, 12);
			setMenuColours();
			staticBottomMenu();
			// send("Qs88=" + qs88Menu);
			// send("Qs89=" + qs89Menu + defaultLongRightMenu);
		}
	}

	static void updatePage() {
		if (psx.isActiveSubSystem) {
			for (int i = 0; i < newCduPage.length; i++) {
				if (newCduPage[i] != currentCduPage[i]) {
					send("Qs" + (i + startingLine) + "=" + newCduPage[i]);
					currentCduPage[i] = newCduPage[i];
				}

			}

			staticBottomMenu();
		}
	}

	public static void menuQfa25() {
		int page = 1;
		if (activePage != page) {
			for (int i = 0; i < cduLines; i++) {
				cduColours[i] = cduColours[i].substring(0, menuLength)
						+ createStringLength(defaultColour, cduLength - menuLength);
			}
		}

		activePage = page;
		MenuQfa25.showMenu();
	}

	public static void menuActions() {
		int page = 1;
		if (activePage != page) {
			for (int i = 0; i < cduLines; i++) {
				cduColours[i] = cduColours[i].substring(0, menuLength)
						+ createStringLength(defaultColour, cduLength - menuLength);
			}
		}

		activePage = page;
		MenuActions.showMenu();
	}

	public static void menuWeather(boolean windUpdate) {
		int page = PAGE_WEATHER;
		if (activePage != page) {
			for (int i = 0; i < cduLines; i++) {
				cduColours[i] = cduColours[i].substring(0, menuLength)
						+ createStringLength(defaultColour, cduLength - menuLength);
			}
		}

		activePage = page;
		MenuWeather.showMenu();
	}

	public static void menuService() {
		if (psx.isActiveSubSystem) {
			int page = PAGE_SERVICE;
			if (activePage != page) {
				for (int i = 0; i < cduLines; i++) {
					cduColours[i] = cduColours[i].substring(0, menuLength)
							+ createStringLength(defaultColour, cduLength - menuLength);
				}
			}

			activePage = page;
			MenuService.showMenu();
		}
	}

	public static void setMenuColours() {

		for (int i = 0; i < cduLines; i++) {
			send("Qs" + (qsStart + i) + "=" + cduColours[i].toString());
		}
	}

	// KEYS PRESSED

	public static void skPushed(int keyPushed) {
		// Left Side Key Pushed 41-46, Right side 51-56
		if (psx.isActiveSubSystem) {
			// Index Button
			if (keyPushed == 46 && activePage != PAGE_MAINMENU) {
				activePage = PAGE_MAINMENU;
			}

			// Motion Freeze
			if (keyPushed == 56) {
				if (psx.motionFreeze == false) {
					send("Qi129=2");
					psx.motionFreeze = true;
				} else if (PSXControl.psx.motionFreeze == true) {
					send("Qi129=0");
					psx.motionFreeze = false;
				}
				if (PSXControl.active == true) {
					staticBottomMenu();
				}
			}
			
			switch (activePage) {
			case PAGE_MAINMENU:
				break;
			case PAGE_GROUND:
				MenuGround.skPushed(keyPushed);
				break;
			case PAGE_RUNWAY:
				MenuRunway.skPushed(keyPushed);
				break;
			case PAGE_AIRBORNE:
				MenuAirborne.skPushed(keyPushed);
				break;
			case PAGE_ENGINES:
				MenuEngines.skPushed(keyPushed);
				break;
			case PAGE_SERVICE:
				MenuService.skPushed(keyPushed);
				break;
			case PAGE_WEATHER:
				MenuWeather.skPushed(keyPushed);
				break;
			case PAGE_LAYOUTS:
				MenuLayouts.skPushed(keyPushed);
				break;
			case PAGE_PANELS:
				MenuPanels.skPushed(keyPushed);
				break;
			case PAGE_SITUATION:
				MenuSituation.skPushed(keyPushed);
				break;
			case PAGE_OTHER:
				MenuOther.skPushed(keyPushed);
				break;
			}

			if (activePage == PAGE_MAINMENU) {
				switch (keyPushed) {
				case 41:
					activePage = PAGE_GROUND;
					break;
				case 42:
					activePage = PAGE_RUNWAY;
					break;
				case 43:
					activePage = PAGE_AIRBORNE;
					break;
				case 44:
					activePage = PAGE_ENGINES;
					break;
				case 45:
					activePage = PAGE_SERVICE;
					break;
				case 51:
					activePage = PAGE_WEATHER;
					break;
				case 52:
					activePage = PAGE_LAYOUTS;
					break;
				case 53:
					activePage = PAGE_PANELS;
					break;
				case 54:
					activePage = PAGE_SITUATION;
					break;
				case 55:
					activePage = PAGE_OTHER;
					break;
				}

				displayMenu();
			}
		}
	}

	public static void rskPushed(int keyPushed) {

		if (psx.isActiveSubSystem) {
			if (keyPushed == 56) {
				if (psx.motionFreeze == false) {
					send("Qi129=2");
					psx.motionFreeze = true;
				} else if (PSXControl.psx.motionFreeze == true) {
					send("Qi129=0");
					psx.motionFreeze = false;

				}
				if (PSXControl.active == true) {
					staticBottomMenu();
				}
			} else {
				switch (activePage) {
				case PAGE_WEATHER:
					MenuWeather.rskPushed(keyPushed);
					break;
				case PAGE_SITUATION:
					MenuSituation.skPushed(keyPushed);
					break;
				case PAGE_SERVICE:
					MenuService.rskPushed(keyPushed);
					break;
				}
			}
		}
	}

	public static void clearScratchPad() {
		scratchPad = "";
		send("Qs89=" + scratchPad);
	}

	public static void clearPushed() {
		if (scratchPadLock) {
			send("Qs89=");
			scratchPadLock = false;
			if (deleteActive)
				deleteActive = false;
		} else if (scratchPad.length() > 0) {
			scratchPad = scratchPad.substring(0, scratchPad.length() - 1);
			send("Qs89=" + CduMenu.scratchPad);
		}
	}

	public static void deletePushed() {
		if (scratchPad.length() == 0) {
			//send("Qi90=300");
			send("Qs89=DELETE");
			deleteActive = true;
			scratchPadLock = true;
		}
	}

	static void blankScreen(int blankTime) {
		send("Qi90=" + blankTime);
	}

	public static String createStringLength(String c, int stringLength) {
		StringBuilder sbString = new StringBuilder(stringLength);

		for (int i = 0; i < stringLength; i++) {
			sbString.append(c);
		}

		String s = String.valueOf(sbString);
		sbString = null;
		return s;

	}

	public static void displayMenu() {
		if(activePage != PAGE_MAINMENU) {
			for(int i = 0; i < 11; i++) {
				cduColours[i] = createStringLength(textColourWhite, 24);
			}
			setMenuColours();
		}

		
		switch (activePage) {
		case PAGE_MAINMENU:
			showMainMenu();
			break;
		case PAGE_RUNWAY:
			MenuRunway.showMenu();
			break;
		case PAGE_AIRBORNE:
			MenuAirborne.showMenu();
			break;
		case PAGE_GROUND:
			MenuGround.showMenu();
			break;
		case PAGE_SITUATION:
			MenuSituation.showMenu();
			break;
		case PAGE_SERVICE:
			MenuService.showMenu();
			break;
		case PAGE_WEATHER:
			MenuWeather.showMenu();
			break;
		case PAGE_LAYOUTS:
			MenuLayouts.showMenu();
			break;
		case PAGE_PANELS:
			MenuPanels.showMenu();
			break;
		case PAGE_ENGINES:
			MenuEngines.showMenu();
			break;
		case PAGE_OTHER:
			MenuOther.showMenu();
			break;
		}

		staticBottomMenu();
	}

	static void setPage(int direction) {
		switch (activePage) {
		case PAGE_GROUND:
			MenuGround.setPage(direction);
			break;
		case PAGE_ENGINES:
			MenuEngines.setPage(direction);
			break;
		}

	}

}