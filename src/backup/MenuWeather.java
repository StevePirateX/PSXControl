package backup;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

class MenuWeather extends CduMenu {

	public static int wndDirection = 0;
	public static int wndStrength = 0;
	static String miscFlightData[] = new String[6]; // Qs483
	static String cloudCoverWords[] = { "FEW", "SCT", "BKN", "OVC" };
	
	// Weather
		public static String[] weatherMetars = new String[8];
		public static boolean realWxChecked;
		public static String weather; // String before array
		public static String weatherIcao = ""; // Active weather ICAO
		public static Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		public static int hdg;

	public static void showMenu(boolean windUpdate) {

		if (!windUpdate) {
			send("Qs76=" + titlMenu);
			send("Qs77=" + qs77Menu + defaultLongRightMenu);
			motionFreeze("___REAL_WX>");
			send("Qs79=" + qs79Menu + defaultLongRightMenu);
			send("Qs80=" + qs80Menu + "____________VMC>");
			send("Qs81=" + qs81Menu + defaultLongRightMenu);
			send("Qs82=" + qs82Menu + "____________IMC>");
			send("Qs83=" + qs83Menu + defaultLongRightMenu);
			send("Qs84=" + qs84Menu + "<ACT>_____CAVOK>");
			send("Qs85=" + qs85Menu + "_____-----------");
			send("Qs86=" + qs86Menu + "_____20KT_XWIND>");
		}

		send("Qs87=" + qs87Menu + "_____" + MenuWeather.getWind() + "_");

		if (!windUpdate)
			send("Qs88=" + qs88Menu + "_____bbbo/bb_KT>");

		setMenuColours();
	}
	
	public static void rskPushed(int id) {
		// id = 51,52 etc of which key was pushed
		switch (id) {
		case 51: // Real weather
			setRealWx();
			break;
		case 52: // VMC
			setVmc();
			break;
		case 53: // IMC
			setImc();
			break;
		case 54:
			setCavok();
			break;
		case 55:
			set20ktCrosswind();
			break;
		case 56:
			setWind(scratchPad);
			break;
		}
	}

	public static void setRealWx() {
		send("Qi90=500");
		send("Qi139=1");
		// Reset wind aloft
		String qs3270LowerCase = psx.wxAloft[0].toLowerCase();
		String qs3271 = "0";
		String newQs327 = psx.wxAloft[0].substring(0, qs3270LowerCase.indexOf("w"))
				+ psx.wxAloft[0].substring(qs3270LowerCase.indexOf("w"), qs3270LowerCase.length()) + ";" + qs3271 + ";"
				+ psx.wxAloft[2];
		send("Qs327=" + newQs327);

		PSXControl.psx.setPsxTime();
	}

	public static void setVmc() {
		send("Qi139=0"); // Turn off real Wx

		// Settings
		int maxCloudCoverage = 4; // Out of 8
		int minCloudHeight = 5000; // In feet
		int maxCloudBase = 20; // In 1000s of feet
		int maxCloudThickness = 10; // in 1000s of feet

		// Defaults
		int selCloudLayersUsed = 0; // Between 0 & 3
		int selVisibility = 9999;
		//String cloudMetar = "CAVOK";

		// Probabillities must equal 100
		int[] cloudLayerProbability = { 20, 50, 30 }; // 0 clouds, 1 cloud layer, 2 cloud layers
		double visibilityProbability = 70; // 70% chance of 9999 visibility

		// Probability and random selection
		Random random = new Random();
		int randomPercent = random.nextInt(100);

		// Cloud layer calculation
		if (randomPercent < cloudLayerProbability[0]) {
			// No layers
			selCloudLayersUsed = 0;
		} else {
			if (randomPercent < cloudLayerProbability[0] + cloudLayerProbability[1]) {
				// 1 layer
				selCloudLayersUsed = 1;
			} else {
				// 2 layers
				selCloudLayersUsed = 2;
			}
			//cloudMetar = "";
		}

		for (int i = 0; i < selCloudLayersUsed; i++) {
			int cloudCover = Math.max(0, random.nextInt(maxCloudCoverage - 1)) + 1;
			int cloudBase = random.nextInt(maxCloudBase) * 1000 + minCloudHeight;
			int cloudTop = random.nextInt(maxCloudThickness) * 1000 + cloudBase;

			psx.wxBasic[3 * i] = String.valueOf(cloudCover);
			psx.wxBasic[3 * i + 1] = String.valueOf(cloudTop);
			psx.wxBasic[3 * i + 2] = String.valueOf(cloudBase);

			if (selCloudLayersUsed == 1) {
				//cloudMetar = " 9999 " + cloudCoverWords[(int) Math.ceil(cloudCover / 2)] + ""
				//		+ String.format("%03d", (int) Math.floor(cloudBase / 100));
			}
		}

		for (int i = selCloudLayersUsed; i <= 2; i++) {
			psx.wxBasic[3 * i] = "0";
			psx.wxBasic[3 * i + 1] = "0";
			psx.wxBasic[3 * i + 2] = "0";
		}

		// Turbulence + CBs
		for (int i = 2; i < 4; i++) {
			if (i != 2) {
				PSXControl.psx.wxBasic[3 * i] = "0";
				PSXControl.psx.wxBasic[3 * i + 1] = "0";
				PSXControl.psx.wxBasic[3 * i + 2] = "0";
			}
		}

		psx.wxBasic[20] = (random.nextInt(100) <= visibilityProbability ? String.valueOf(selVisibility)
				: String.valueOf(random.nextInt(7) * 500 + 6000));
		psx.wxBasic[21] = "0"; // No Precipitation

		// Send same weeather to each zone
		for (int i = 0; i < 8; i++) {
			int qsMetar = 328 + i;
			String weatherArrayReplace = Arrays.toString(psx.wxBasic).replace(", ", ";");
			String metarArrayToSend = weatherArrayReplace.substring(1, weatherArrayReplace.length() - 1);
			send("Qs" + qsMetar + "=" + metarArrayToSend);
		}

		send("Qi90=500"); // Blank CDU for 500ms

		Integer currentTemp = Integer.parseInt(psx.weatherArray[22]);
		String minus = currentTemp < 3 ? "M" : "";
		String newMetar = "";

		String endOfMetar = String.format("%02d", currentTemp) + "/" + minus + String.format("%02d", currentTemp - 3)
				+ " " + psx.postMetar.substring(psx.postMetar.indexOf("/") + 4);

		psx.postMetar = newMetar + endOfMetar;
		String metar = psx.preMetar + " " + psx.postMetar;

		String finalWeather = "Qs" + (342 + psx.wxZone) + "=" + metar;
		send(finalWeather);
		MenuService.updateAltim();
	}

	public static void setImc() {

		send("Qi139=0"); // Turn off real Wx

		// Settings
		int maxCloudCoverage = 8; // Out of 8
		int minCloudCoverage = 5; // Out of 8
		int minCloudHeight = 0; // In feet
		int maxCloudBase = 20; // In 1000s of feet
		int maxCloudThickness = 15; // in 1000s of feet
		int maxHeightOfLowestCloud = 100; // in feet
		int rainProbability = 30; // Chance of rain
		int cbProbability = 50; // Chance of CBs (out of 100)
		int cbMinHeight = 500; // In feet

		// Defaults
		int selCloudLayersUsed = 3; // Between 0 & 3
		// int selVisibility = 2000;
		// String cloudMetar = "SCT020 OVC050 SCT150";

		// Probabillities must equal 100
		int[] cloudLayerProbability = { 5, 25, 70 }; // 0 clouds, 1 cloud layer, 2 cloud layers
		// double visibilityProbability = 50; // % chance of default visibility

		// Probability and random selection
		Random random = new Random();
		int randomPercent = random.nextInt(100);

		// Cloud layer calculation
		if (randomPercent < cloudLayerProbability[0]) {
			// No layers
			selCloudLayersUsed = 0;
		} else {
			if (randomPercent < cloudLayerProbability[0] + cloudLayerProbability[1]) {
				// 1 layer
				selCloudLayersUsed = 1;
			} else {
				// 2 layers
				selCloudLayersUsed = 2;
			}

			// cloudMetar = "";
			// System.out.println(cloudMetar);
		}

		int cloud0Cover = 0;
		int cloud0Top = 0;
		int cloud0Base = 0;

		int cloud1Cover = 0;
		int cloud1Top = 0;
		int cloud1Base = 0;

		// Calculate both clouds even if only 1 used cloud 1 must be lower than cloud 0
		cloud1Cover = Math.min(8, random.nextInt(3) + 7); // 2:1 chance of 8/8
		cloud1Base = random.nextInt(maxHeightOfLowestCloud / 5) * 5 + minCloudHeight;
		cloud1Top = random.nextInt(maxCloudThickness) * 1000 + cloud1Base; // Top calculated after base

		cloud0Cover = Math.max(minCloudCoverage, random.nextInt(maxCloudCoverage - 1)) + 1;
		cloud0Base = random.nextInt(maxCloudBase) * 1000 + minCloudHeight;
		cloud0Top = random.nextInt(maxCloudThickness) * 1000 + cloud0Base; // Top calculated after base

		if (selCloudLayersUsed == 0) {
			psx.wxBasic[0] = String.valueOf(0);
			psx.wxBasic[1] = String.valueOf(0);
			psx.wxBasic[2] = String.valueOf(0);
			psx.wxBasic[3] = String.valueOf(0);
			psx.wxBasic[4] = String.valueOf(0);
			psx.wxBasic[5] = String.valueOf(0);
		} else if (selCloudLayersUsed == 1) {
			psx.wxBasic[0] = String.valueOf(cloud1Cover);
			psx.wxBasic[1] = String.valueOf(cloud1Top);
			psx.wxBasic[2] = String.valueOf(cloud1Base);
			psx.wxBasic[3] = String.valueOf(0);
			psx.wxBasic[4] = String.valueOf(0);
			psx.wxBasic[5] = String.valueOf(0);
		} else if (selCloudLayersUsed == 2){
			psx.wxBasic[0] = String.valueOf(cloud0Cover);
			psx.wxBasic[1] = String.valueOf(cloud0Top);
			psx.wxBasic[2] = String.valueOf(cloud0Base);
			psx.wxBasic[3] = String.valueOf(cloud1Cover);
			psx.wxBasic[4] = String.valueOf(cloud1Top);
			psx.wxBasic[5] = String.valueOf(cloud1Base);
		}

		// Turbulence
		psx.wxBasic[6] = "0"; // Turbulence Strength
		psx.wxBasic[7] = "2000"; // Turbulence Top
		psx.wxBasic[8] = "100"; // Turbulence Base

		// CBs
		int cbSelProb = random.nextInt(100); // Selected probability of CBs
		int cbCover = (cbSelProb > cbProbability ? random.nextInt(maxCloudCoverage) : 0);
		int cbBase = random.nextInt(maxCloudBase * 2) * 500 + cbMinHeight;
		int cbTop = random.nextInt(maxCloudThickness * 2) * 500 + cbBase; // Top calculated after base
		
		PSXControl.psx.wxBasic[9] = String.valueOf(cbCover); // CB Coverage
		PSXControl.psx.wxBasic[10] = String.valueOf(cbTop); // CB Top
		PSXControl.psx.wxBasic[11] = String.valueOf(cbBase); // CB Bottom

		
		
		// psx.wxBasic[20] = (random.nextInt(100) <= visibilityProbability ?
		// String.valueOf(selVisibility) : String.valueOf(random.nextInt(7) * 250 +
		// 500));
		psx.wxBasic[20] = String.valueOf(random.nextInt(19) * 100 + 100); // Visibility
		psx.wxBasic[21] = (random.nextInt(100) <= rainProbability ? "1" : "0"); // Precipitation

		// Send same weeather to each zone
		for (int i = 0; i < 8; i++) {
			int qsMetar = 328 + i;
			String weatherArrayReplace = Arrays.toString(psx.wxBasic).replace(", ", ";");
			String metarArrayToSend = weatherArrayReplace.substring(1, weatherArrayReplace.length() - 1);
			// System.out.println("Weather = " + metarArrayToSend);
			send("Qs" + qsMetar + "=" + metarArrayToSend);
		}

		send("Qi90=500"); // Blank CDU for 500ms

		Integer currentTemp = Integer.parseInt(psx.weatherArray[22]);
		String minus = currentTemp < 3 ? "M" : "";
		String newMetar = "";

		String endOfMetar = String.format("%02d", currentTemp) + "/" + minus + String.format("%02d", currentTemp - 3)
				+ " " + psx.postMetar.substring(psx.postMetar.indexOf("/") + 4);

		psx.postMetar = newMetar + endOfMetar;
		String metar = psx.preMetar + " " + psx.postMetar;

		String finalWeather = "Qs" + (342 + psx.wxZone) + "=" + metar;
		send(finalWeather);
		MenuService.updateAltim();
	}

	public static void setCavok() {


		send("Qi90=500");
		send("Qi139=0");

		PSXControl.psx.preMetar = "" + weatherIcao + " "
				+ String.format("%02d",
						cal.get(Calendar.DAY_OF_MONTH))
				+ String.format("%02d",
						cal.get(Calendar.HOUR_OF_DAY))
				+ String.format("%02d", cal.get(Calendar.MINUTE))
				+ "Z " + "00000KT";

		Integer currentTemp = 15;
		String minus = currentTemp < 3 ? "M" : "";
		String newMetar = "CAVOK ";
		String endOfMetar = String.format("%02d", currentTemp) + "/"
				+ minus + String.format("%02d", currentTemp - 3)
				+ " Q1013";

		PSXControl.psx.postMetar = newMetar + endOfMetar;
		String metar = PSXControl.psx.preMetar + " "
				+ PSXControl.psx.postMetar;

		send("Qs" + (342 + PSXControl.psx.wxZone) + "=" + metar);

		// Planet Weather
		for (int i = 0; i < 12; i++) {
			PSXControl.psx.wxBasic[i] = "0"; // CLouds
		}
		PSXControl.psx.wxBasic[20] = "9999"; // Visibility
		PSXControl.psx.wxBasic[21] = "0"; // Precipitation
		PSXControl.psx.wxBasic[22] = "15"; // Temp at MSL
		PSXControl.psx.wxBasic[23] = "2992"; // QNH STD

		String qs328NewArray = "";
		for (int i = 0; i < PSXControl.psx.wxBasic.length - 1; i++) {
			qs328NewArray += PSXControl.psx.wxBasic[i] + ";";
		}
		qs328NewArray += PSXControl.psx.wxBasic[PSXControl.psx.wxBasic.length
				- 1];
		send("Qs328=" + qs328NewArray);
	}
	
	public static void set20ktCrosswind() {

		// 20kt crosswind
		send("Qi90=500");
		send("Qi139=0");
		// System.out.println(weather);
		int wndDir;
		Random random = new Random();
		int wndStrength = 15 + random.nextInt(7);

		if (Math.random() < 0.5) { // Wind from the left
			if (hdg < 90) {
				wndDir = hdg + 270;
			} else {
				wndDir = hdg - 90;
			}
		} else {
			if (hdg > 270) {
				wndDir = hdg - 270;
			} else {
				wndDir = hdg + 90;
			}
		}

		String newMetar = "" + weatherIcao + " "
				+ String.format("%02d",
						cal.get(Calendar.DAY_OF_MONTH))
				+ String.format("%02d",
						cal.get(Calendar.HOUR_OF_DAY))
				+ String.format("%02d", cal.get(Calendar.MINUTE))
				+ "Z " + String.format("%03d", wndDir) + wndStrength
				+ "KT";

		PSXControl.psx.preMetar = newMetar;
		String metar = PSXControl.psx.preMetar + " "
				+ PSXControl.psx.postMetar;
		send("Qs" + (342 + PSXControl.psx.wxZone) + "=" + metar);

		// Planet Weather
		// Wind aloft
		String qs327LowerCase = PSXControl.psx.wxAloft[0]
				.toLowerCase();
		String newQs327 = PSXControl.psx.wxAloft[0].substring(0,
				qs327LowerCase.indexOf("w")) + "W000"
				+ String.format("%03d", wndDir) + wndStrength + ";"
				+ wndStrength + ";" + PSXControl.psx.wxAloft[2];
		send("Qs327=" + newQs327);

		// Planet wind
		PSXControl.psx.wxBasic[6] = "0"; // Turbulence
		PSXControl.psx.wxBasic[12] = "0"; // Microburst
		PSXControl.psx.wxBasic[13] = "0"; // Microoburst %
		PSXControl.psx.wxBasic[18] = "000"
				+ String.format("%03d", wndDir) + wndStrength; // Winds
		PSXControl.psx.wxBasic[19] = "0"; // Gust strenth

		String qs328NewArray = "";
		for (int i = 0; i < PSXControl.psx.wxBasic.length - 1; i++) {
			qs328NewArray += PSXControl.psx.wxBasic[i] + ";";
		}
		qs328NewArray += PSXControl.psx.wxBasic[PSXControl.psx.wxBasic.length
				- 1];
		send("Qs328=" + qs328NewArray);

	}

	
	public static void setWind(String wind) {
		int windDirection = 0;
		int windSpeed = 0;

		// Make sure of valid entry
		boolean numeric = true;
		try {
			Integer.parseInt(wind);
		} catch (NumberFormatException e) {
			numeric = false;
		}

		if (numeric && wind.length() == 5) {
			System.out.println("Wind = " + wind + " (Valid Entry)");

			windDirection = Integer.parseInt(wind.substring(0, 3));
			windSpeed = Integer.parseInt(wind.substring(3));

			// System.out.println("Direction = " + windDirection + " & Speed = " +
			// windSpeed);

			// Apply magnetic variation
			miscFlightData = PSXControl.psx.qsVariables[483].split(";");
			double magVar = Double.parseDouble(miscFlightData[5]) / 10;
			System.out.println("Direction = " + windDirection + ", variation = " + magVar);
			windDirection = (int) Math.round(windDirection + magVar);
			System.out.println(
					"New Direction = " + windDirection + ", variation = " + magVar + " & Speed = " + windSpeed);

			String windDirectionString = String.format("%06d", windDirection);
			String windSpeedString = String.format("%02d", windSpeed);

			// Turn off real weather
			send("Qi139=0");
			netThread.send("Qi243=20"); // Transition the weather

			String weatherMetars[][] = new String[8][24];

			for (int i = 0; i < 8; i++) {
				int qsMetar = 328 + i;
				// System.out.println(psx.qsVariables[qsMetar].toString());

				String metar[] = psx.qsVariables[qsMetar].split(";");
				for (int j = 0; j < 24; j++) {
					weatherMetars[i][j] = metar[j];
					// System.out.println("Weather METAR " + j + " = " + metar[j].toString());
				}

				weatherMetars[i][18] = windDirectionString + "" + windSpeedString;
				weatherMetars[i][19] = windSpeedString;

				String metarArrayPre = Arrays.toString(weatherMetars[i]).replace(", ", ";").substring(1);
				String metarArrayToSend = metarArrayPre.substring(0, metarArrayPre.length() - 1);

				// String fullSendToServer = "Qs" + qsMetar + "=" + metarArrayToSend;
				// System.out.println("Sending: " + fullSendToServer);
				netThread.send("Qs" + qsMetar + "=" + metarArrayToSend);
			}

			// Clean up CDU
			blankScreen(600);
			clearScratchPad();
		} else {
			System.out.println("Invalid Entry");
		}
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

		String s = windDirectionFormatted + "o/" + windStrengthFormatted + "_KT";

		return s;
	}

}