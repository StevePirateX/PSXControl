import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class Functions extends CduMenu {
	public static Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

	public Functions() {
		// TODO Auto-generated constructor stub
	}

	static void alignIrs() {
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

	static void startEng() {
		send("Qh392=1\n" + "Qh393=1\n" + "Qh394=1\n" + "Qh395=1\n" + "Qs361=OcccOccc" + "Qh275=0\n" + "Qs361=OOccOOcc"
				+ "Qh276=0\n" + "Qs361=OOOcOOOc\n" + "Qh277=0\n" + "Qs361=OOOOOOOO\n" + "Qh278=0\n"
				+ "Qs358=290;259;259;259;548;548;548;548;LLLL;377;365;371;384;0;0;0;0;61;59;60;62;15;14;13;14;0;0;0;0\n"
				+ "Qs359=OOOO\n" + "Qh209=96\n" + "Qh210=96\n" + "Qh211=96\n" + "Qh212=96\n");

		psx.qhVariables[275] = "0";
		psx.qhVariables[276] = "0";
		psx.qhVariables[277] = "0";
		psx.qhVariables[278] = "0";
		psx.qhVariables[392] = "1";
		psx.qhVariables[393] = "1";
		psx.qhVariables[394] = "1";
		psx.qhVariables[395] = "1";
		psx.qsVariables[358] = "290;259;259;259;548;548;548;548;LLLL;377;365;371;384;0;0;0;0;61;59;60;62;15;14;13;14;0;0;0;0";
		psx.qsVariables[359] = "OOOO";
		psx.qsVariables[361] = "OOOOOOOO";
		psx.qhVariables[209] = "96";
		psx.qhVariables[210] = "96";
		psx.qhVariables[211] = "96";
		psx.qhVariables[212] = "96";
	}

	static void startApu() {

		send("Qh279=0\n" + "Qi135=2549\n" + "Qh193=1\n" + "Qi160=30000\n" + "Qi260=450\n" + "Qh197=128\n"
				+ "Qh198=128\n" + "Qh325=325=0");

		psx.qhVariables[279] = "0"; // Fire
		psx.qiVariables[135] = 2549; // APU Voltage
		psx.qhVariables[193] = "1"; // APU Switch
		psx.qiVariables[160] = 30000; // APU Phase
		psx.qiVariables[260] = 450; // APU Door
		psx.qhVariables[197] = "128"; // APU Gen 1
		psx.qhVariables[198] = "128"; // APU Gen 2
		psx.qhVariables[325] = "0"; // APU Valve

	}

	static void autoThrottleDisc() {
		try {
			send("Qi90=200");
			send("Qh386=3");
			Thread.sleep(100);
			send("Qh386=0");
			Thread.sleep(100);
			send("Qh386=3");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	static void autoPilotDisc() {
		try {
			send("Qi90=200");
			send("Qh400=1");
			Thread.sleep(100);
			send("Qh400=0");
			Thread.sleep(100);
			send("Qh400=1");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	static void togaPushed() {
		send("Qh387=3");
	}

	static void fuelCutoff(int eng) {

		int newSwitchState = psx.getCutoffSwState(eng) - (psx.isEngCutoffSwRun(eng) ? 1 : -1);
		psx.qhVariables[psx.getCutoffSw(eng)] = String.valueOf(newSwitchState);

		String s = "Qh" + psx.getCutoffSw(eng) + "=" + newSwitchState;
		System.out.println(s);
		send(s);
	}

	static void flapLever(String degrees) {
		// degrees can be "UP", "DOWN" or a "0", "5" etc to set specific flap setting
		int variable = Integer.parseInt(psx.qhVariables[psx.flapLever]);

		try {
			int setDegrees = Integer.parseInt(degrees);
			switch (setDegrees) {
			case 0:
				variable = 0;
				break;
			case 1:
				variable = 1;
				break;
			case 5:
				variable = 2;
				break;
			case 10:
				variable = 3;
				break;
			case 20:
				variable = 4;
				break;
			case 25:
				variable = 5;
				break;
			case 30:
				variable = 6;
				break;
			}
		} catch (NumberFormatException e) {
			if (degrees.equals("UP") && variable != 0) {
				variable--;
			} else if (degrees.equals("DOWN") && variable != 6) {
				variable++;
			}
		}

		psx.qhVariables[psx.flapLever] = String.valueOf(variable);
		CduMenu.clearScratchPad();
		send("Qh" + psx.flapLever + "=" + variable);
	}

	static void gearCycle() {
		int currPos = psx.currentGearLeverPos;
		if (currPos == 3 || currPos == 1)
			currPos = 2;
		else if (currPos == 2 && (psx.prevGearLeverPos == 1 || psx.qiVariables[257] == 1))
			currPos = 3;
		else if (currPos == 2 && psx.prevGearLeverPos == 3)
			currPos = 1;

		psx.prevGearLeverPos = Integer.valueOf(psx.qhVariables[psx.gearLever]);
		psx.currentGearLeverPos = currPos;
		psx.qhVariables[psx.gearLever] = String.valueOf(currPos);
		send("Qh" + psx.gearLever + "=" + currPos);
		CduMenu.displayMenu();
	}

	static void updateAltim() {
		send("Qi90=500");

		String stdAltim = "101325";
		String Qs448string;
		String[] Qs448 = psx.qsVariables[448].split(";");
		String Qs449string;
		String[] Qs449 = psx.qsVariables[449].split(";");
		int altitude = psx.getAltitude();
		int qnh = psx.getQnh();
		int trans = psx.getTransitionLevel();
		System.out.println("Alt = " + altitude + "; Transition = " + trans + "; QNH = " + qnh);

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
		System.out.println(psx.qsVariables[448]);
		System.out.println(Qs448string);
		send("Qs448=" + Qs448string);
		send("Qs449=" + Qs449string);
	}

	static void cycleSpeedbrake() {
		int speedbrakeVariable = 388;
		int speedbrakeValue = Integer.valueOf(psx.qhVariables[speedbrakeVariable]);
		if (speedbrakeValue < 40) {
			// Speedbrake is retracted
			speedbrakeValue = 50;
		} else if (speedbrakeValue < 60) {
			// Speedbrake is armed
			speedbrakeValue = 800;
		} else {
			// Speedbrake is extended
			speedbrakeValue = 0;
		}

		psx.qhVariables[speedbrakeVariable] = String.valueOf(speedbrakeValue);
		String s = "Qh" + speedbrakeVariable + "=" + speedbrakeValue;
		System.out.println(s);
		send(s);
		CduMenu.displayMenu();
	}

	static void setThrust(int i) {
		// Idle = 0, full = 100, idle reverse = -1, full reverse = -101
		// Thrust = (idle = 0; full = 5000)
		// Qs436 split into array (";")
		// Reverse - (idle = -4600; full = -8925

		i = Math.max(-101, Math.min(i, 100));

		int fullThrust = PSXControl.psx.fullThrust;
		int idleThrust = PSXControl.psx.idleThrust;
		int idleReverse = PSXControl.psx.idleReverse;
		int fullReverse = PSXControl.psx.fullReverse;

		int newThrustScaled = 0;

		if (i >= 0) {
			newThrustScaled = i * (fullThrust - idleThrust) / 100 + idleThrust;
		} else if (i >= -1) {
			newThrustScaled = idleReverse;
		} else if (i >= -101) {
			float scale = (fullReverse - idleReverse) / 100;
			System.out.println(scale);
			newThrustScaled = (int) (-(i - 1) * scale + idleReverse);
		}

		String thrustLevers = newThrustScaled + ";" + newThrustScaled + ";" + newThrustScaled + ";" + newThrustScaled;
		PSXControl.psx.qsVariables[PSXControl.psx.qsTla] = thrustLevers;
		String s = "Qs" + PSXControl.psx.qsTla + "=" + thrustLevers;
		CduMenu.clearScratchPad();
		System.out.println("New thrust = " + i + " Sending... " + s);
		send("Qs" + PSXControl.psx.qsTla + "=" + thrustLevers);
	}

	static void setAutobrakes(int i) {
		// RTO = -1, 0 = OFF, 1 = 1, 2 = 2 etc
		int output = 0;
		if (i == -1) {
			output = 0;
		} else if (i == 0) {
			output = 1;
		} else if (i == 1) {
			output = i + 2;
		} else if (i == 2) {
			output = i + 2;
		} else if (i == 3) {
			output = i + 2;
		} else if (i == 4) {
			output = i + 2;
		} else if (i == 5) {
			output = i + 2;
		}

		psx.qhVariables[psx.autoBrakeSw] = String.valueOf(output);
		CduMenu.clearScratchPad();
		send("Qh" + psx.autoBrakeSw + "=" + output);
		CduMenu.displayMenu();

	}

	static void setRudderTrim(int trim) {
		int rudderTrim = trim;
		rudderTrim = Math.max(-8000, Math.min(rudderTrim, 8000));

		psx.qiVariables[psx.varRudderTrim] = rudderTrim;
		send("Qi" + psx.varRudderTrim + "=" + rudderTrim);
		CduMenu.displayMenu();
	}

	static void setStabTrim(int trim) {
		int stabTrim = trim;
		stabTrim = Math.max(0, Math.min(stabTrim, 15000));

		psx.qiVariables[psx.varStabTrim] = stabTrim;
		send("Qi" + psx.varStabTrim + "=" + stabTrim);
		CduMenu.displayMenu();
	}

	static void groundProx() {
		boolean isGroundProxBypassed;
		boolean FlapOvrd = Integer.parseInt(psx.qhVariables[166]) % 2 == 1;
		boolean GearOvrd = Integer.parseInt(psx.qhVariables[167]) % 2 == 1;
		boolean TerrOvrd = Integer.parseInt(psx.qhVariables[168]) % 2 == 1;

		if (FlapOvrd || GearOvrd || TerrOvrd) {
			isGroundProxBypassed = true;
		} else {
			isGroundProxBypassed = false;
		}

		isGroundProxBypassed = !isGroundProxBypassed;

		for (int i = 166; i <= 168; i++) {
			if (Integer.parseInt(psx.qhVariables[i]) % 2 == 1 && isGroundProxBypassed) {
				isGroundProxBypassed = true;
			}
			int newVal = Integer.parseInt(psx.qhVariables[i])
					+ (isGroundProxBypassed ? 1 : (Integer.parseInt(psx.qhVariables[i]) % 2 == 1 ? -1 : 0));
			psx.qhVariables[i] = String.valueOf(newVal);
			send("Qh" + i + "=" + String.valueOf(newVal));
		}
	}

	static void setWind(String wind) {
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
			String miscFlightData[] = PSXControl.psx.qsVariables[483].split(";");
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

	static void setRealWx() {
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

	static void setVmc() {
		send("Qi139=0"); // Turn off real Wx

		// Settings
		int maxCloudCoverage = 4; // Out of 8
		int minCloudHeight = 5000; // In feet
		int maxCloudBase = 20; // In 1000s of feet
		int maxCloudThickness = 10; // in 1000s of feet

		// Defaults
		int selCloudLayersUsed = 0; // Between 0 & 3
		int selVisibility = 9999;
		// String cloudMetar = "CAVOK";

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
			// cloudMetar = "";
		}

		for (int i = 0; i < selCloudLayersUsed; i++) {
			int cloudCover = Math.max(0, random.nextInt(maxCloudCoverage - 1)) + 1;
			int cloudBase = random.nextInt(maxCloudBase) * 1000 + minCloudHeight;
			int cloudTop = random.nextInt(maxCloudThickness) * 1000 + cloudBase;

			psx.wxBasic[3 * i] = String.valueOf(cloudCover);
			psx.wxBasic[3 * i + 1] = String.valueOf(cloudTop);
			psx.wxBasic[3 * i + 2] = String.valueOf(cloudBase);

			if (selCloudLayersUsed == 1) {
				// cloudMetar = " 9999 " + cloudCoverWords[(int) Math.ceil(cloudCover / 2)] + ""
				// + String.format("%03d", (int) Math.floor(cloudBase / 100));
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
		Functions.updateAltim();
	}

	static void setImc() {

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
		} else if (selCloudLayersUsed == 2) {
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
		Functions.updateAltim();
	}

	static void setCavok() {

		send("Qi90=500");
		send("Qi139=0");

		PSXControl.psx.preMetar = "" + psx.weatherIcao + " " + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
				+ String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + String.format("%02d", cal.get(Calendar.MINUTE))
				+ "Z " + "00000KT";

		Integer currentTemp = 15;
		String minus = currentTemp < 3 ? "M" : "";
		String newMetar = "CAVOK ";
		String endOfMetar = String.format("%02d", currentTemp) + "/" + minus + String.format("%02d", currentTemp - 3)
				+ " Q1013";

		PSXControl.psx.postMetar = newMetar + endOfMetar;
		String metar = PSXControl.psx.preMetar + " " + PSXControl.psx.postMetar;

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
		qs328NewArray += PSXControl.psx.wxBasic[PSXControl.psx.wxBasic.length - 1];
		send("Qs328=" + qs328NewArray);
	}

	static void set20ktCrosswind() {
		int hdg = psx.getHdg();

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

		String newMetar = "" + psx.weatherIcao + " " + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
				+ String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + String.format("%02d", cal.get(Calendar.MINUTE))
				+ "Z " + String.format("%03d", wndDir) + wndStrength + "KT";

		PSXControl.psx.preMetar = newMetar;
		String metar = PSXControl.psx.preMetar + " " + PSXControl.psx.postMetar;
		send("Qs" + (342 + PSXControl.psx.wxZone) + "=" + metar);

		// Planet Weather
		// Wind aloft
		String qs327LowerCase = PSXControl.psx.wxAloft[0].toLowerCase();
		String newQs327 = PSXControl.psx.wxAloft[0].substring(0, qs327LowerCase.indexOf("w")) + "W000"
				+ String.format("%03d", wndDir) + wndStrength + ";" + wndStrength + ";" + PSXControl.psx.wxAloft[2];
		send("Qs327=" + newQs327);

		// Planet wind
		PSXControl.psx.wxBasic[6] = "0"; // Turbulence
		PSXControl.psx.wxBasic[12] = "0"; // Microburst
		PSXControl.psx.wxBasic[13] = "0"; // Microoburst %
		PSXControl.psx.wxBasic[18] = "000" + String.format("%03d", wndDir) + wndStrength; // Winds
		PSXControl.psx.wxBasic[19] = "0"; // Gust strenth

		String qs328NewArray = "";
		for (int i = 0; i < PSXControl.psx.wxBasic.length - 1; i++) {
			qs328NewArray += PSXControl.psx.wxBasic[i] + ";";
		}
		qs328NewArray += PSXControl.psx.wxBasic[PSXControl.psx.wxBasic.length - 1];
		send("Qs328=" + qs328NewArray);

	}

	static void setTiller(int tiller) {
		tiller = Math.max(-999, Math.min(tiller, 999));

		psx.qhVariables[psx.varTiller] = String.valueOf(tiller);
		send("Qh" + psx.varTiller + "=" + String.valueOf(tiller));
		CduMenu.displayMenu();
	}

	static void tglExtPower() {
		int powerBits = psx.qiVariables[132];
		boolean extPow1 = powerBits % 2 >= 1;
		boolean extPow2 = powerBits % 16 >= 8;

		boolean connectExtPow = (extPow1 || extPow2 ? false : true);

		extPow1 = connectExtPow;
		extPow2 = connectExtPow;

		if (powerBits % 16 >= 8 && !connectExtPow) {
			powerBits -= 8;
		} else if (powerBits % 2 < 8 && connectExtPow) {
			powerBits += 8;
		}

		if (powerBits % 2 >= 1 && !connectExtPow) {
			powerBits -= 1;
		} else if (powerBits % 2 < 1 && connectExtPow) {
			powerBits += 1;
		}

		psx.qiVariables[132] = powerBits;
		send("Qi132=" + powerBits);
	}

	static void tglBleedAir() {
		int bleedAirBits = psx.qiVariables[174];
		boolean bleedAir = bleedAirBits % 2 >= 1;

		boolean connectBleedAir = (bleedAir ? false : true);

		bleedAir = connectBleedAir;

		if (bleedAirBits % 2 >= 1 && !connectBleedAir) {
			bleedAirBits -= 1;
		} else if (bleedAirBits % 2 < 1 && connectBleedAir) {
			bleedAirBits += 1;
		}

		psx.qiVariables[174] = bleedAirBits;
		send("Qi174=" + bleedAirBits);
	}

	static void tglConditionedAir() {
		int conditionedAirBits = psx.qiVariables[174];
		boolean conditionedAir = conditionedAirBits % 4 >= 2;

		boolean connectConditionedAir = (conditionedAir ? false : true);

		conditionedAir = connectConditionedAir;

		if (conditionedAirBits % 4 >= 2 && !connectConditionedAir) {
			conditionedAirBits -= 2;
		} else if (conditionedAirBits % 4 < 2 && connectConditionedAir) {
			conditionedAirBits += 2;
		}

		psx.qiVariables[174] = conditionedAirBits;
		send("Qi174=" + conditionedAirBits);
	}

	static void setParkBrake() {
		int varBrakes = 357; // String
		int varParkBrake = 397;

		boolean isParkBrakeSet = psx.qhVariables[varParkBrake].equals("1");

		int newBrakePres = 0;
		String newBrakes = newBrakePres + ";" + newBrakePres;

		if (isParkBrakeSet) {
			newBrakePres = 1000;
			newBrakes = newBrakePres + ";" + newBrakePres;
			psx.qsVariables[varBrakes] = newBrakes;
			String sBrake = "Qs" + varBrakes + "=" + newBrakes;
			send(sBrake);

			// Turn off Park Brake
			psx.qhVariables[varParkBrake] = "0";
			String sPark = "Qh" + varParkBrake + "=0";
			send(sPark);

			// Set brakes off
			newBrakePres = 0;
			newBrakes = newBrakePres + ";" + newBrakePres;
			psx.qsVariables[varBrakes] = newBrakes;
			sBrake = "Qs" + varBrakes + "=" + newBrakes;
			send(sBrake);
		} else {
			newBrakePres = 1000;
			newBrakes = newBrakePres + ";" + newBrakePres;
			psx.qsVariables[varBrakes] = newBrakes;
			String sBrake = "Qs" + varBrakes + "=" + newBrakes;
			send(sBrake);

			// Set Park Brake
			psx.qhVariables[varParkBrake] = "1";
			String sPark = "Qh" + varParkBrake + "=1";
			send(sPark);

			// Set brakes off
			newBrakePres = 950;
			newBrakes = newBrakePres + ";" + newBrakePres;
			psx.qsVariables[varBrakes] = newBrakes;
			sBrake = "Qs" + varBrakes + "=" + newBrakes;
			send(sBrake);
		}
	}

	static void setFuel(float fuelInput) {
		int input = 0;
		try {
			// 1250 = 10000 / 8
			input = (int) fuelInput;
			System.out.println("Fuel input (kgs): " + fuelInput);
			input = (int) (Float.parseFloat(CduMenu.scratchPad) * 10000 * 2.2046226218);
			System.out.println("Fuel input (lbs): " + input);
		} catch (NumberFormatException nfe) {
			// Invalid entry
			send("Qi90=500");
			CduMenu.invalidEntry = true;
			send("Qs89=INVALID_ENTRY___________");
			// scratchPad += "_";
		}

		if (input >= 0 /* & input <= Fuel.fuelMax */ & CduMenu.scratchPad.length() > 0) {
			// send("Qi90=500");
			// CduMenu.fuel = Float.parseFloat(scratchPad);

			System.out.println("Fuel output = " + Fuel.fuelInput(input));
			send("Qi196=1");
			String fuelString = Fuel.fuelInput(input);
			psx.qsVariables[438] = fuelString;
			send("Qs438=" + fuelString);
			// Instant refuel

			send("Qs89=________________________");
			CduMenu.scratchPad = "";

		} else {
			// Invalid entry
			send("Qi90=500");
			CduMenu.invalidEntry = true;
			send("Qs89=INVALID_ENTRY___________");
			// scratchPad += "_";
		}
	}

	static void setCg(float cgInput) {
		float input = 0;
		try {
			input = cgInput * 10;
		} catch (NumberFormatException nfe) {
			// Invalid entry
			send("Qi90=500");
			CduMenu.invalidEntry = true;
			send("Qs89=INVALID_ENTRY___________");
			// scratchPad += "_";
		}

		if (input >= 0 & input < 1000 & String.valueOf(cgInput).length() > 0) {
			// send("Qi90=500");
			// CduMenu.cg = Float.parseFloat(scratchPad);

			if (input < 85) {
				input = 85;
			} else if (input > 330) {
				input = 330;
			}

			MenuSituation.cg = input / 10;

			int i = (int) input;
			String output = String.valueOf(i);
			psx.qiVariables[122] = i;
			send("Qi122=" + output);

			send("Qs89=________________________");
			CduMenu.scratchPad = "";
		} else {
			// Invalid entry
			send("Qi90=500");
			CduMenu.invalidEntry = true;
			send("Qs89=INVALID_ENTRY___________");
			// scratchPad += "_";
		}
	}

	static void tglDoorsOpen() {

	}

	static void allowBoardingLeft() {

	}

	static void allowBoardingRight() {

	}

	static void clearCb() {
		for (int i = 1; i < psx.cbs.length; i++) {
			String s = psx.cbs[i][0] + "=" + psx.cbs[i][1].toLowerCase();
			System.out.println(s);
			send(s);
		}
		send("Qi130=1"); // Clear extra CB
	}

	static void clearMalf() {
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

	static void cycleFuelFlow() {
		int currentFlow = psx.qiVariables[193];
		int newFlow = 1;

		switch (currentFlow) {
		case 0:
			newFlow = 1;
			break;
		case 1:
			newFlow = 100;
			break;
		case 100:
			newFlow = 0;
			break;
		}
		psx.qiVariables[193] = newFlow;
		send("Qi193=" + newFlow);
	}

	static void tglBoardingDoors() {
		int i = psx.getBoardingDoors();

		if (i == 1)
			i = 2;
		else if (i == 2)
			i = 4;
		else
			i = 1;

		i = i + (psx.getBoardingL() ? 8 : 0) + (psx.getBoardingR() ? 16 : 0);

		psx.qiVariables[179] = i;
		send("Qi179=" + i);
	}

	static void tglBoardingL() {
		boolean isAlreadyBoarding = psx.getBoardingL();
		int bitMask = 8;
		int newBoarding = psx.qiVariables[179]; // Boarding Variable

		if (isAlreadyBoarding) {
			newBoarding = newBoarding - bitMask;
		} else {
			newBoarding = newBoarding + bitMask;
		}

		psx.qiVariables[179] = newBoarding;
		send("Qi179=" + newBoarding);
	}

	static void tglBoardingR() {
		boolean isAlreadyBoarding = psx.getBoardingR();
		int bitMask = 16;
		int newBoarding = psx.qiVariables[179]; // Boarding Variable

		if (isAlreadyBoarding) {
			newBoarding = newBoarding - bitMask;
		} else {
			newBoarding = newBoarding + bitMask;
		}

		psx.qiVariables[179] = newBoarding;
		send("Qi179=" + newBoarding);
	}
}
