
public class MenuEngines extends CduMenu {

	public MenuEngines() {
		// TODO Auto-generated constructor stub
	}

	// Pages
	static int page = 1;
	static int totalPages = 2;

	public static void showMenu() {
		newCduPage = getScreen(page);
		updatePage();
	}

	public static String[] getScreen(int page) {
		String[] screen = new String[cduLines];

		if (page == 1) {
			// Thrust
			int currentThrustVal = psx.getAvgThrust();
			String currentThrustLabel = String.format("<%d%%", (currentThrustVal >= 0 ? currentThrustVal : currentThrustVal + 1));
			String ThrustLineExtra = CduMenu.createStringLength("_", 6 - currentThrustLabel.length());
			String finalThrustLabel = currentThrustLabel + ThrustLineExtra;
			
			
			screen[0] = "________ENGINES_____" + page + "/" + totalPages + "_++++++++++++++++++++---+";
			screen[1] = "________________________";
			screen[2] = "<UP_____________________";
			screen[3] = "_SET_THRUST_____________";
			screen[4] = finalThrustLabel + "__________________";
			screen[5] = "_________________REVERSE";
			screen[6] = "<DOWN____________TOGGLE>";
			screen[7] = "________________________";
			screen[8] = "<FULL_THRUST_______IDLE>";
			screen[9] = "________________________";
			screen[10] = "<IDLE_THRUST_______FULL>";
			screen[13] = emptyLine;
		} else if (page == 2) {
			screen[0] = "________ENGINES_____" + page + "/" + totalPages + "_++++++++++++++++++++---+";
			screen[1] = "_____________FUEL_CUTOFF";
			screen[2] = "______________" + (psx.isEngCutoffSwRun(1) ? "RUNNING" : "_CUTOFF") + "_1>";
			screen[3] = emptyLine;
			screen[4] = "______________" + (psx.isEngCutoffSwRun(2) ? "RUNNING" : "_CUTOFF") + "_2>";
			screen[5] = emptyLine;
			screen[6] = "______________" + (psx.isEngCutoffSwRun(3) ? "RUNNING" : "_CUTOFF") + "_3>";
			screen[7] = emptyLine;
			screen[8] = "______________" + (psx.isEngCutoffSwRun(4) ? "RUNNING" : "_CUTOFF") + "_4>";
			screen[9] = emptyLine;
			screen[10] = emptyLine;
			screen[13] = emptyLine;
		}

		return screen;
	}
	
	static void setPage(int direction) {
		if (direction == 1) {
			if (page != totalPages)
				page++;
			else
				page = 1;
		} else if (direction == -1) {
			if (page != 1)
				page--;
			else
				page = totalPages;
		}
		showMenu();
	}	

	public static void skPushed(int id) {
		if(page == 1) {
			switch (id) {
			case 41:
				// UP Thrust
				Functions.setThrust(psx.getAvgThrust() + 20);
				break;
			case 42:
				// Set Thrust
				int newThrust = Integer.valueOf(CduMenu.scratchPad);
				Functions.setThrust(newThrust);
				break;
			case 43:
				// DOWN Thrust
				Functions.setThrust(psx.getAvgThrust() - 20);
				break;
			case 44:
				// Full Thrust
				Functions.setThrust(100);
				break;
			case 45:
				// Idle Thrust
				Functions.setThrust(0);
				break;
			case 51:
				// Empty
				break;
			case 52:
				// Empty
				break;
			case 53:
				// Toggle Reverse
				int thrust = psx.getAvgThrust();
				if(thrust >= 0) {
					Functions.setThrust(-1);
				} else {
					Functions.setThrust(0);
				}
				break;
			case 54:
				// Idle Reverse
				Functions.setThrust(-1);
				break;
			case 55:
				// Full Reverse
				Functions.setThrust(-101);
				break;
			}
		} else if(page == 2) {
			switch (id) {
			case 41:
				// Empty
				break;
			case 42:
				// Empty
				break;
			case 43:
				// Empty
				break;
			case 44:
				// Empty
				break;
			case 45:
				// Empty
				break;
			case 51:
				// Engine 1 Cutoff
				Functions.fuelCutoff(1);
				break;
			case 52:
				// Engine 2 Cutoff
				Functions.fuelCutoff(2);
				break;
			case 53:
				// Engine 3 Cutoff
				Functions.fuelCutoff(3);
				break;
			case 54:
				// Engine 4 Cutoff
				Functions.fuelCutoff(4);
				break;
			case 55:
				// Empty
				break;
			}
		}
		showMenu();
	}

}
