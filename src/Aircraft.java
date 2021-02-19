public class Aircraft extends PSXControl {

	int psxNetThreadId = 0;
	String version = "10";
	int sVariables = 0;
	int iVariables = 0;
	int hVariables = 0;

	int variableArraySizes = 700;
	String[] qsVariables = new String[variableArraySizes];
	String[] qhVariables = new String[variableArraySizes];
	int[] qiVariables = new int[variableArraySizes];

	// Malfunctions
	String[][] cbs = new String[59][2];

	// Gear
	int gearLever = 170;
	int currentGearLeverPos;
	int prevGearLeverPos = 1;

	int autoBrakeSw = 134;
	int flapLever = 389;
	int engineSwitchStart = 392;
	int varStabTrim = 119;
	int varRudderTrim = 121;
	int varTiller = 426;

	// Thrust
	int qsTla = 436;
	int fullThrust = 5000;
	int idleThrust = 0;
	int idleReverse = -4600;
	int fullReverse = -8925;

	// PSX Control variables
	public boolean isActiveSubSystem = false;
	public boolean motionFreeze = false; // True if motion frozen
	public boolean terrOvrd = false; // True if TERR OVRD is on

	// Weather variables
	public String weatherIcao = "";
	public String[] wxAloft = new String[3]; // Planet Weather Aloft
	public String[] wxBasic = new String[24]; // Planet Weather output local zones
	public String[] weatherArray; // Weather Array
	public String preMetar; // METAR all before clouds
	public String postMetar; // METAR all after winds
	public int wxZone; // Current active weather zone - used to calculate QNH

	protected void setPsxNetThreadId(int i) {
		psxNetThreadId = i;
	}

	public void setStringVariable(int id, String value) {
		qsVariables[id] = value;
		if (id > sVariables) {
			sVariables = id;
		}
		// System.out.println("ID: " + id + ": " + value + " inserted into: " +
		// qsVariables.get(id));
	}

	public void setHumanVariable(int id, String value) {
		qhVariables[id] = value;
		if (id > hVariables) {
			hVariables = id;
		}
		// System.out.println("ID: " + id + ": " + value + " inserted into: " +
		// qhVariables.get(id));
	}

	public void setIntegerVariable(int id, int value) {
		qiVariables[id] = value;
		if (id > iVariables) {
			iVariables = id;
		}
		// System.out.println("ID: " + id + ": " + value + " inserted into: " +
		// qiVariables.get(id));
	}

	public void setPsxTime() {
		send("Qs123=" + System.currentTimeMillis());
		send("Qs124=" + System.currentTimeMillis());
		send("Qs125=" + System.currentTimeMillis());
	}

	int getAltitude() {
		String[] position = qsVariables[121].split(";");
		int altitude = Integer.parseInt(position[3]) / 1000;
		return altitude;
	}

	int getQnh() {
		int wxZone = qiVariables[240];
		String wx = qsVariables[328 + wxZone];
		String strAltim = wx.substring(wx.lastIndexOf(";") + 1).trim();
		int altim = Integer.parseInt(strAltim) * 10;
		int qnh = (int) Math.round(altim * 3.38639);
		return qnh;
	}

	int getTransitionLevel() {
		int transition = Integer.parseInt(qsVariables[392].substring(qsVariables[392].indexOf(";") + 1,
				qsVariables[392].indexOf(";", qsVariables[392].indexOf(";") + 1)));
		return transition;
	}

	int getCutoffSw(int eng) {
		eng = eng - 1;
		int cutoffSwitch = engineSwitchStart + eng;
		return cutoffSwitch;
	}

	boolean isEngCutoffSwRun(int eng) {
		boolean isEngRunning = Integer.valueOf(psx.qhVariables[getCutoffSw(eng)]) % 2 == 1;
		return isEngRunning;
	}

	int getAvgThrust() {
		int numEngines = 4;
		String[] sEngThrust = qsVariables[qsTla].split(";");
		int[] iengThrust = new int[numEngines];

		for (int i = 0; i < numEngines; i++) {
			iengThrust[i] = Integer.valueOf(sEngThrust[i]);
		}

		int[] scaledThrust = new int[numEngines];

		int totalScaledThrust = 0;

		for (int i = 0; i < numEngines; i++) {
			if (iengThrust[i] >= idleThrust) {
				scaledThrust[i] = (iengThrust[i] - idleThrust) / (fullThrust / 100);
			} else if (iengThrust[i] > idleReverse) {
				scaledThrust[i] = -1;
			} else {
				float scale = (float) (iengThrust[i] - idleReverse) / (fullReverse - idleReverse);
				scaledThrust[i] = (int) (-100 * scale - 1);
			}
			totalScaledThrust += scaledThrust[i];
		}
		int avgThrust = (int) totalScaledThrust / numEngines;

		System.out.println("Raw thrust = " + iengThrust[0] + "\tScaled = " + scaledThrust[0] + "TotalScaled = "
				+ totalScaledThrust + "\tAverage Thrust = " + avgThrust);
		return avgThrust;
	}

	int getCutoffSwState(int eng) {
		int currentSwitchState = Integer.valueOf(psx.qhVariables[getCutoffSw(eng)]);
		return currentSwitchState;
	}

	int getFlapDeg() {
		int flapId = Integer.valueOf(qhVariables[flapLever]);
		int flapPositions[] = { 0, 1, 5, 10, 20, 25, 30 };
		int currentFlaps = flapPositions[flapId];
		return currentFlaps;
	}

	String getGearLever() {
		String gearLeverLabel;
		int gearLeverPos = Integer.valueOf(qhVariables[gearLever]);
		if (gearLeverPos == 1)
			gearLeverLabel = "UP";
		else if (gearLeverPos == 2)
			gearLeverLabel = "OFF";
		else
			gearLeverLabel = "DOWN";

		return gearLeverLabel;
	}

	String getAutobrakes() {
		String autobrakeLabel = "";
		int autobrakeSetting = Integer.valueOf(qhVariables[autoBrakeSw]);
		if (autobrakeSetting == 0) {
			autobrakeLabel = "RTO";
		} else if (autobrakeSetting == 1) {
			autobrakeLabel = "OFF";
		} else if (autobrakeSetting == 2) {
			autobrakeLabel = "DISARM";
		} else if (autobrakeSetting == 3) {
			autobrakeLabel = "1";
		} else if (autobrakeSetting == 4) {
			autobrakeLabel = "2";
		} else if (autobrakeSetting == 5) {
			autobrakeLabel = "3";
		} else if (autobrakeSetting == 6) {
			autobrakeLabel = "4";
		} else if (autobrakeSetting == 7) {
			autobrakeLabel = "MAX/AUTO";
		}
		return autobrakeLabel;
	}

	int getRudderTrim() {
		int rudderTrim = qiVariables[121];
		// System.out.println("Actual Rudder Trim = " + rudderTrim);
		return rudderTrim;
	}

	int getStabTrim() {
		int stabTrim = qiVariables[119];
		// System.out.println("Actual Stab Trim = " + stabTrim);
		return stabTrim;

	}

	int getHdg() {
		String[] pos = qsVariables[121].split(";");
		int hdg = (int) (Float.parseFloat(pos[2]) * 180 / Math.PI);

		return hdg;
	}

	int getTillerPos() {
		int tiller = Integer.parseInt(qhVariables[426]);
		return tiller;
	}

	float getFuel() {
		float fuel = 0;
		String[] tanks = qsVariables[438].split(";");
		int fuelOnBoard = 0;

		for (int i = 0; i < 9; i++) {
			tanks[i].replaceAll("[^\\d.]", "");
			if (i == 0) {
				tanks[i] = tanks[i].substring(1);
				fuel = 0;
				fuelOnBoard = 0;
			}
			fuelOnBoard += Integer.valueOf(tanks[i]);
		}

		fuel = (float) (fuelOnBoard / 2.2046226218 / 10000);
		return fuel;
	}

	float getCg() {
		return (float) (qiVariables[122]) / 10;
	}

	String getFuelFlow() {
		int fuelFlowVariable = 193;

		String fuelFlow = "";
		switch (psx.qiVariables[fuelFlowVariable]) {
		case 0:
			fuelFlow = "0x";
			break;
		case 1:
			fuelFlow = "NORMAL";
			break;
		case 100:
			fuelFlow = "100x";
			break;
		}

		return fuelFlow;

	}
	
	int getBoardingDoors() {
		int psxBoarding = psx.qiVariables[179];
		int boardingLeft = 8;
		int boardingRight = 16;
		int result;
		
		if(psxBoarding <= 4) {
			return psxBoarding;
		} else {
			result = boardingLeft & psxBoarding;
			if(result == boardingLeft) 
				psxBoarding = psxBoarding - boardingLeft;
			
			result = boardingRight & psxBoarding;
			if(result == boardingRight) 
				psxBoarding = psxBoarding - boardingRight;
			
			return psxBoarding;
		}
	}

	boolean getBoardingL() {
		int boardingLeft = 8;
		int psxBoarding = psx.qiVariables[179];
		
		int result = boardingLeft & psxBoarding;
		
		boolean isBoardingLeft = (result == boardingLeft ? true : false);
		System.out.println(boardingLeft + " : " + psxBoarding + " = " + result + " = " + isBoardingLeft);
		return isBoardingLeft;
	}
	
	boolean getBoardingR() {
		int boardingRight = 16;
		int psxBoarding = psx.qiVariables[179];
		
		int result = boardingRight & psxBoarding;
		
		boolean isBoardingRight = (result == boardingRight ? true : false);
		System.out.println(boardingRight + " : " + psxBoarding + " = " + result + " = " + isBoardingRight);
		return isBoardingRight;
	}

}
