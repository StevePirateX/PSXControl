package backup;
import java.util.Calendar;
import java.util.TimeZone;

class CduMenu extends PSXControl {

	// VARIABLES
	// Static Menu (Left Side)
	static String titlMenu = "______PSX_CONTROL_______";
	static String qs77Menu = "_MOTION_";
	static String qs78Menu = "<FREEZE_";
	static String qs79Menu = "________";
	static String qs80Menu = (qfa25 ? "<QFA25__" : "________");
	static String qs81Menu = "________";
	static String qs82Menu = "<ACTIONS";
	static String qs83Menu = "________";
	static String qs84Menu = "<WEATHER";
	static String qs85Menu = "________";
	static String qs86Menu = "<SITU___";
	static String qs87Menu = "________";
	static String qs88Menu = "<SERVICE";
	static String qs89Menu = "________";

	static String[] cduColours = new String[14];
	static String defaultColour = "w";
	
	// Standard Colours
	static int menuLength = 13;
	static String menuColour = "b"; // Grey Background
	static int cduLines = 14;
	static int cduLength = 24;
	static int qsStart = 514;
	
	static final int PAGE_MAINMENU = 0;
	static final int PAGE_ACTIONS = 1;
	static final int PAGE_WEATHER = 2;
	static final int PAGE_SITUATIONS = 3;
	static final int PAGE_SERVICE = 4;
	static final int PAGE_QFA25 = 5;

	// When <ACT> needs to be shown
	static String defaultLongRightMenu = "________________"; // When Right manages <ACT>
	static String defaultShortRightMenu = "___________"; // When Left manages <ACT>

	static int activePage = 0; // See "PAGE_*" constants for assignements
	
	// Cdu Handling
	static String scratchPad = ""; // This is the text in the scratchpad
	public static boolean scratchPadLock = false; // Locks entry such as when you press DELETE
	public static boolean deleteActive = false; // If the user presses delete, the user is unable to type
	public static String[] cduKeyboard = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	public static String viewPSXTime = "----";
	public static Calendar psxTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	public static boolean  invalidEntry = false;
	
	
	public CduMenu() {
				 
		for(int i = 0;i < cduLines; i++) {
			cduColours[i] = createStringLength(defaultColour, 24);
		}
	}
		
	// ACT / Non-ACT Menus
	static void motionFreeze(String s) { // String must be 10 characters long of what to display on the right
		String textColour = "";
		if (PSXControlNetThread.activeMenu == 1) {
			if (psx.motionFreeze) {
				textColour = "r";
				
				String newQs78Menu = qs78Menu + "<ACT>";
				send("Qs78=" + newQs78Menu + s);
				
				cduColours[1] = createStringLength(textColour, 7) + cduColours[1].substring(7,cduColours[1].length());
				send("Qs515=" + cduColours[1].toString());
				//System.out.println(qs515Colour);
				
				cduColours[2] = createStringLength(textColour, newQs78Menu.length()) + cduColours[2].substring(newQs78Menu.length(),cduColours[2].length());
				send("Qs516=" + cduColours[2].toString());
				//System.out.println(qs516Colour);
				
			} else if (!psx.motionFreeze) {
				textColour = "w";
				
				String newQs78Menu = qs78Menu + "_____";
				send("Qs78=" + newQs78Menu + s);
				
				cduColours[1] = createStringLength(textColour, 7) + cduColours[1].substring(7,cduColours[1].length());
				send("Qs515=" + cduColours[1].toString());
				//System.out.println(qs515Colour);
				
				cduColours[2] = createStringLength(textColour, newQs78Menu.length()) + cduColours[2].substring(newQs78Menu.length(),cduColours[2].length());
				send("Qs516=" + cduColours[2].toString());
				//System.out.println(qs516Colour);
			}
		}
	}

	static void terrOvrd(String s) { // String must be 10 characters long of what to display on the right
		if (PSXControlNetThread.activeMenu == 1) {
			if (psx.terrOvrd)
				send("Qs80=" + qs80Menu + "<ACT>_" + s);
			else if (!psx.terrOvrd)
				send("Qs80=" + qs80Menu + "______" + s);
		}
	}

	
	
	public static void updateMenu(int updateType) {
		// System.out.println(activePage);
		if (psx.isActiveSubSystem) {
			switch (activePage) {
			case PAGE_MAINMENU:
				showMainMenu();
				break;
				
			case PAGE_ACTIONS:
				menuActions();
				break;
				
			case PAGE_WEATHER:
				boolean windUpdate = false;
				if (updateType == 1) {
					windUpdate = true;
				}
				menuWeather(windUpdate);
				break;
			case PAGE_SITUATIONS:
				menuSitu();
				break;
			case PAGE_SERVICE:
				menuService();
				break;
			case PAGE_QFA25:
				menuQfa25();
				break;
			default:
				break;
			}
		}
	}

	// MAIN MENU
	public static void showMainMenu() {
		// activePage = 0;
		if (psx.isActiveSubSystem) {
			send("Qs76=" + titlMenu);
			send("Qs77=" + qs77Menu + defaultLongRightMenu);
			motionFreeze(defaultShortRightMenu);
			send("Qs79=" + qs79Menu + defaultLongRightMenu);
			send("Qs80=" + qs80Menu + defaultLongRightMenu);
			send("Qs81=" + qs81Menu + defaultLongRightMenu);
			send("Qs82=" + qs82Menu + defaultLongRightMenu);
			send("Qs83=" + qs83Menu + defaultLongRightMenu);
			send("Qs84=" + qs84Menu + defaultLongRightMenu);
			send("Qs85=" + qs85Menu + defaultLongRightMenu);
			send("Qs86=" + qs86Menu + defaultLongRightMenu);
			send("Qs87=" + qs87Menu + defaultLongRightMenu);
			send("Qs88=" + qs88Menu + defaultLongRightMenu);
			// send("Qs89=" + qs89Menu + defaultLongRightMenu);
		}
	}

	public static void menuQfa25() {
		int page = PAGE_QFA25;
		if(activePage != page) {
			for(int i = 0; i < cduLines; i++) {
				cduColours[i] = cduColours[i].substring(0,menuLength) + createStringLength(defaultColour, cduLength - menuLength);
			}
		}
		
		activePage = page;
		MenuQfa25.showMenu();
	}
	
	public static void menuActions() {
		int page = PAGE_ACTIONS;
		if(activePage != page) {
			for(int i = 0; i < cduLines; i++) {
				cduColours[i] = cduColours[i].substring(0,menuLength) + createStringLength(defaultColour, cduLength - menuLength);
			}
		}
		
		activePage = page;
		MenuActions.showMenu();
	}
	
 	public static void menuWeather(boolean windUpdate) {
 		int page = PAGE_WEATHER;
		if(activePage != page) {
			for(int i = 0; i < cduLines; i++) {
				cduColours[i] = cduColours[i].substring(0,menuLength) + createStringLength(defaultColour, cduLength - menuLength);
			}
		}
		
		activePage = page;
		MenuWeather.showMenu(windUpdate);
	}

	public static void menuSitu() {
		int page = PAGE_SITUATIONS;
		if(activePage != page) {
			for(int i = 0; i < cduLines; i++) {
				cduColours[i] = cduColours[i].substring(0,menuLength) + createStringLength(defaultColour, cduLength - menuLength);
			}
		}
		
		activePage = page;
		MenuSituations.showMenu();
	}

	public static void menuService() {
		if (psx.isActiveSubSystem) {
			int page = PAGE_SERVICE;
			if(activePage != page) {
				for(int i = 0; i < cduLines; i++) {
					cduColours[i] = cduColours[i].substring(0,menuLength) + createStringLength(defaultColour, cduLength - menuLength);
				}
			}
			
			activePage = page;
			MenuService.showMenu();
		}
	}

	public static void setMenuColours() {
		
		//for(int i = 0; i < cduLines; i++) {
		//	cduColours[i] = createStringLength(defaultColour, cduLength);
		//}
		
		// Menu Colours
		cduColours[4] = createStringLength((activePage == PAGE_QFA25 ? menuColour : defaultColour), menuLength) + cduColours[4].substring(menuLength,cduColours[4].length());
		cduColours[6] = createStringLength((activePage == PAGE_ACTIONS ? menuColour : defaultColour), menuLength) + cduColours[6].substring(menuLength,cduColours[6].length());
		cduColours[8] = createStringLength((activePage == PAGE_WEATHER ? menuColour : defaultColour), menuLength) + cduColours[8].substring(menuLength,cduColours[8].length());
		cduColours[10] = createStringLength((activePage == PAGE_SITUATIONS ? menuColour : defaultColour), menuLength) + cduColours[10].substring(menuLength,cduColours[10].length());
		cduColours[12] = createStringLength((activePage == PAGE_SERVICE ? menuColour : defaultColour), menuLength) + cduColours[12].substring(menuLength,cduColours[12].length());
	
		for(int i=0;i < cduLines;i++) {
			send("Qs" + (qsStart + i) + "=" + cduColours[i].toString());
		}
	}
	
	
	// KEYS PRESSED
	
	public static void lskPushed(int keyPushed) {
		// Left Side Key Pushed 41-46
		if (psx.isActiveSubSystem) {
			switch (keyPushed) {
			case 41:
				if (psx.motionFreeze == false) {
					send("Qi129=2");
					psx.motionFreeze = true;
				} else if (PSXControl.psx.motionFreeze == true) {
					send("Qi129=0");
					psx.motionFreeze = false;

				}
				if (PSXControl.active == true) {
					CduMenu.updateMenu(0);
				}
				break;
			case 42:
				if(qfa25)
					menuQfa25();
				break;
			case 43:
				menuActions();
				break;
			case 44:
				menuWeather(false);
				break;
			case 45:
				menuSitu();
				break;
			case 46:
				menuService();
				break;
			}
		}
	}

	public static void rskPushed(int keyPushed) {

		if (psx.isActiveSubSystem) {
			switch (activePage) {
			case PAGE_ACTIONS:
				MenuActions.rskPushed(keyPushed);
				break;
			case PAGE_WEATHER:
				MenuWeather.rskPushed(keyPushed);
				break;
			case PAGE_SITUATIONS:
				MenuSituations.rskPushed(keyPushed);
				break;
			case PAGE_SERVICE:
				MenuService.rskPushed(keyPushed);
				break;
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
		if(scratchPad.length() == 0) {
			send("Qi90=300");
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
		
		for(int i = 0; i < stringLength; i++) {
			sbString.append(c);
		}
		
		String s = String.valueOf(sbString);
		sbString = null;
		return s;
		
	}

	static void setPage(int direction) {
		switch(activePage) {
		case PAGE_ACTIONS:
			MenuActions.setPage(direction);
			break;
		case PAGE_QFA25:
			MenuQfa25.setPage(direction);
			break;
		}
	}
}