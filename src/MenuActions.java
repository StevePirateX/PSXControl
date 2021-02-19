
public class MenuActions extends CduMenu {

	// Pages
		static int page = 1;
		static int totalPages = 2;
	
	// Ground Proximity
	static boolean isGroundProxBypassed = false;
	static boolean FlapOvrd = false; // Qh166
	static boolean GearOvrd = false; // Qh167
	static boolean TerrOvrd = false; // Qh168
	static final int groundProxON = 63;
	static final int groundProxOFF = 62;

	// Flight Deck Door
	static boolean isFltDoorLocked = false;
	static String[] menuFltDeckDoor = { "UNLCKED", "_DENIED" };

	// Interphone
	static boolean isInterphoneActive = false;
	static String[] menuInterphone = { "__ACTIVE", "INACTIVE" };
	static int lAcpStatus = -1;

	// Wx Radar Test
	static boolean isWxTcasTesting = false;
	static String[] menuWxTcasTest = { "__ACTIVE", "INACTIVE" };
	static boolean isWxRadarTesting = false;
	static String wxPanel;
	static boolean isTcasTesting = false;
	static int tcasPanel;
	
	
	// Alternate Flap
	static boolean isAltFlapsArmed = false;
	static int altFlapsSwitch = 1; // 0 = Retract, 1 = Off, 2 = Extend
	static int altFlapArmButton = 4; // 4 = not armed, 5 = armed

	// Alternate Gear
	static boolean isAltGearArmed = false;
	static int gearExtended = 0;
	static String[] menuGear = { "___ARMED", "DISARMED" };

	public MenuActions() {
		// TODO Auto-generated constructor stub
	}

	public static void showMenu() {
		
		setMenuColours();
		
		
		//int titleLength = menuLine0.length() - String.valueOf(page).length() - String.valueOf(totalPages).length() - 2;
		//String titleBig = new String(new char[titleLength]).replace("\0", "+");
		//String titleSmall = new String(new char[24 - titleLength]).replace("\0", "-");

		//send("Qs76=" + menuLine0.substring(0, titleLength) + page + "/" + totalPages + "_" + titleBig + titleSmall);
		
		//send("Qs77=" + menuLine1 + "____________GND_");
		//staticBottomMenu();
		//send("Qs79=" + menuLine3 + "__FLT_DECK_DOOR_");
		//send("Qs80=" + menuLine4 + "________" + (isFltDoorLocked ? menuFltDeckDoor[0] : menuFltDeckDoor[1])
		//		+ ">");
		//send("Qs81=" + menuLine5 + "_____INTERPHONE_");
		//send("Qs82=" + menuLine6 + "<ACT>__" + (isInterphoneActive ? menuInterphone[0] : menuInterphone[1]) + ">");
		//send("Qs83=" + qs83Menu + "_WX_+_TCAS_TEST_");
		//send("Qs84=" + qs84Menu + "_______" + (isWxTcasTesting ? menuWxTcasTest[0] : menuWxTcasTest[1]) + ">");
		//send("Qs85=" + qs85Menu + "_____ALTN_FLAPS_");

		String altFlapsString = "_______________";

		if (isAltFlapsArmed) {
			if (altFlapsSwitch == 0) {
				altFlapsString = "_________DISARM";
			} else if (altFlapsSwitch == 2) {
				altFlapsString = "________RETRACT";
			}
		} else {
			altFlapsString = "_________EXTEND";
		}
		
		System.out.println(altFlapsString);

		//send("Qs86=" + qs86Menu + altFlapsString + ">");
		//send("Qs87=" + qs87Menu + "______ALTN_GEAR_");
		//send("Qs88=" + qs88Menu + "_______" + (isAltGearArmed ? menuGear[0] : menuGear[1]) + ">");
		
		// Set colours
		
		
		String gndProxColour = "a";
		for(int i = 1; i <= 2; i++) {
		cduColours[i] = cduColours[i].substring(0,13) + createStringLength((isGroundProxBypassed ? gndProxColour : "w"), 11);
		send("Qs" + (qsStart + i) + "=" + cduColours[i].toString());
		}
		
		String interphoneColour = "g";
		for(int i = 5; i <= 6; i++) {
			cduColours[i] = cduColours[i].substring(0,13) + createStringLength((isInterphoneActive ? interphoneColour : "w"), 11);
			send("Qs" + (qsStart + i) + "=" + cduColours[i].toString());
		}
		
		String wxRadarColour = "g";
		for(int i = 7; i <= 8; i++) {
			cduColours[i] = cduColours[i].substring(0,(i==7 ? 9 : 13)) + createStringLength((isWxTcasTesting ? wxRadarColour : "w"), (i==7 ? 15 : 11));
			send("Qs" + (qsStart + i) + "=" + cduColours[i].toString());
		}
		
		String altFlapsColour = "a";
		for(int i = 9; i <= 10; i++) {
			cduColours[i] = cduColours[i].substring(0,13) + createStringLength((isAltFlapsArmed ? altFlapsColour : "w"), 11);
			send("Qs" + (qsStart + i) + "=" + cduColours[i].toString());
		}
		
		String altGearColour = "a";
		for(int i = 11; i <= 12; i++) {
			cduColours[i] = cduColours[i].substring(0,13) + createStringLength((isAltGearArmed ? altGearColour : "w"), 11);
			send("Qs" + (qsStart + i) + "=" + cduColours[i].toString());
		}
		
		
		
	}

	public static void rskPushed(int id) {
		switch (id) {
		case 51:
			groundProx(!isGroundProxBypassed);
			break;
		case 52:
			fltDeckDoor(!isFltDoorLocked);
			break;
		case 53:
			tglInterphone(!isInterphoneActive);
			break;
		case 54:
			String wxRadar;
			int tcas;
			if(!isWxTcasTesting) {
				// Test will be active
				wxRadar = "e";
				tcas = 10;
				isWxRadarTesting = true;
				isTcasTesting = true;
			} else {
				wxRadar = "E";
				tcas = -1;
				isWxRadarTesting = false;
				isTcasTesting = false;
			}
			
			if(isWxRadarTesting || isTcasTesting) {
				isWxTcasTesting = true;
			} else {
				isWxTcasTesting = false;
			}
			
			wxPanel = wxPanel.substring(0, 7) + wxRadar + wxPanel.substring(8);
			tcasPanel = tcas;
			send("Qs104=" + wxPanel);
			send("Qh414=" + tcasPanel);
			showMenu();
			break;
		case 55:
			tglAltFlap();
			break;
		case 56:
			tglAltGear();
			break;
		}
		showMenu();
	}

	public static void groundProx(boolean b) {
		isGroundProxBypassed = b;
		if(isGroundProxBypassed) {
			FlapOvrd = true;
			GearOvrd = true;
			TerrOvrd = true;
		}else {
			FlapOvrd = false;
			GearOvrd = false;
			TerrOvrd = false;
		}
		
		for (int i = 0; i < 3; i++) {
			psx.qhVariables[i] = (isGroundProxBypassed ? "1" : "0");
			PSXControl.netThread.send("Qh" + (166 + i) + "=" + (isGroundProxBypassed ? groundProxON : groundProxOFF));
		}
	}

	public static void fltDeckDoor(boolean setDoorLocked) {
		try {
			if (setDoorLocked) {
				send("Qh419=0");
			} else {
				send("Qh419=2");
			}

			Thread.sleep(500);

			send("Qh419=1");
			isFltDoorLocked = setDoorLocked;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void tglInterphone(boolean setInterphoneActive) {
		if (isInterphoneActive) {
			send("Qh410=-1");
		} else {
			send("Qh410=27");
		}
		isInterphoneActive = setInterphoneActive;
	}

	public static void tglAltFlap() {

		if (isAltFlapsArmed) {
			if (altFlapsSwitch == 0) {
				isAltFlapsArmed = false;
				altFlapsSwitch = 1;
				altFlapArmButton = 4;
			} else if (altFlapsSwitch == 2) {
				altFlapsSwitch = 0;
			}
		} else {
			isAltFlapsArmed = true;
			altFlapsSwitch = 2;
			altFlapArmButton = 5;
		}

		send("Qh162=" + altFlapArmButton);
		send("Qh133=" + altFlapsSwitch);

		send("Qi90=300"); // Blank CDU for 500ms
		showMenu();
	}

	public static void tglAltGear() {
		if (isAltGearArmed) {
			isAltGearArmed = false;
		} else {
			isAltGearArmed = true;
		}

		for (int i = 0; i < 2; i++) {
			PSXControl.netThread.send("Qh" + (163 + i) + "=" + (isAltGearArmed ? 1 : 0));
		}
		send("Qi90=300"); // Blank CDU for 500ms
		showMenu();
	}

	
	static void setPage(int direction) {
		if (direction == 1) {
			if (page != totalPages) {
				page++;
			} else {
				page = 1;
			}
		} else if (direction == -1) {
			if (page != 1) {
				page--;
			} else {
				page = totalPages;
			}
		}

		showMenu();
	}
	
}
