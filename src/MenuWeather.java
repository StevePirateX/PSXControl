class MenuWeather extends CduMenu {

	// Pages
	static int page = 1;
	static int totalPages = 1;

	public static int wndDirection = 0;
	public static int wndStrength = 0;
	static String miscFlightData[] = new String[6]; // Qs483
	static String cloudCoverWords[] = { "FEW", "SCT", "BKN", "OVC" };

	// Weather
	public static String[] weatherMetars = new String[8];
	public static boolean realWxChecked;
	public static String weather; // String before array
	//public static String weatherIcao = ""; // Active weather ICAO
	//public static Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	//public static int hdg;
	
	public static String currentWind = "bbbo/bb";

	public static void showMenu() {
		currentWind = getWind();
		newCduPage = getScreen(page);
		updatePage();

		// send("Qs76=" + menuLine0);
		// send("Qs77=" + menuLine1);
		// staticBottomMenu();
		// send("Qs79=" + menuLine3);
		// send("Qs80=" + menuLine4 + "____________VMC>");
		// send("Qs81=" + menuLine5);
		// send("Qs82=" + menuLine6 + "____________IMC>");
		// send("Qs83=" + qs83Menu);
		// send("Qs84=" + qs84Menu + "<ACT>_____CAVOK>");
		// send("Qs85=" + qs85Menu + "_____-----------");
		// send("Qs86=" + qs86Menu + "_____20KT_XWIND>");

		// send("Qs87=" + qs87Menu + "_____" + MenuWeather.getWind() + "_");

		// if (!windUpdate)
		// send("Qs88=" + qs88Menu + "_____bbbo/bb_KT>");

		// setMenuColours();
	}

	public static String[] getScreen(int page) {
		String[] screen = new String[cduLines];

		if (page == 1) {
			screen[0] = "________WEATHER_________";
			screen[1] = "_CURRENT_WIND___________";
			screen[2] = "<" + currentWind + "KT______REAL_WX>";
			screen[3] = emptyLine;
			screen[4] = emptyLine;
			screen[5] = "_SET_WIND_______________";
			screen[6] = "<bbbo/bbKT__________IMC>";
			screen[7] = emptyLine;
			screen[8] = "<20KT_XWIND_________VMC>";
			screen[9] = emptyLine;
			screen[10] = "<CLEAR_WIND_______CAVOK>";
			screen[13] = emptyLine;
		}

		return screen;
	}

	public static void skPushed(int id) {
		
		
		if (page == 1) {
			switch (id) {
			case 41:
				// Status of current wind - No action
				break;
			case 42:
				// Empty
				break;
			case 43:
				// Set Wind
				Functions.setWind(CduMenu.scratchPad);
				CduMenu.clearScratchPad();
				break;
			case 44:
				// 20 kt Croswind
				Functions.set20ktCrosswind();
				break;
			case 45:
				// Clear wind (set to 0)
				Functions.setWind("36000");
				break;
			case 51:
				// Real Wx
				Functions.setRealWx();
				break;
			case 52:
				// Empty
				break;
			case 53:
				// IMC
				Functions.setImc();
				break;
			case 54:
				// VMC
				Functions.setVmc();
				break;
			case 55:
				// CAVOK
				Functions.setCavok();
				break;
			}
		}

		showMenu();
	}






	



	public static String getWind() {
		// Input value is made of Qs483

		// Constantly Update winds
		miscFlightData = PSXControl.psx.qsVariables[483].split(";");
		/*
		 * 1 - Physical IAS x 10 (avionic independent) 2 - Physical OAT x 10 (avionic
		 * independent) 3 - Physical TAT x 10 (avionic independent) 4 - Computed wind
		 * direction (captain's selected ADC/IRU) 5 - Computed wind speed (captain's
		 * selected ADC/IRU) 6 - Magnetic variation x 10
		 * 
		 * Eg 1438;65;94;25;20;125;
		 */

		wndDirection = Integer.valueOf(miscFlightData[3]);
		wndStrength = Integer.valueOf(miscFlightData[4]);

		// Format winds
		String windDirectionFormatted = String.format("%03d", wndDirection);
		String windStrengthFormatted = String.format("%02d", wndStrength);

		String s = windDirectionFormatted + "o/" + windStrengthFormatted;
		//System.out.println(s);
		return s;
	}

}