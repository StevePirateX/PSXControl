
public class MenuQfa25 extends CduMenu {

	// Pages
		static int page = 1;
		static int totalPages = 2;

	// PSX Variables
	static String autobrakes = "0";
	static String fuelCutoff = "xxxx";
	static String n1 = "0";
	static boolean parkBrake = false;
	
	static int flapLever = 0;
	static String flaps[] = {"UP","1","5","10","20","25","30"};

	// Throttles
	static String reverse = "";

	public MenuQfa25() {
		// TODO Auto-generated constructor stub
	}

	public static void showMenu() {

		setMenuColours();

		// Menu
		//int titleLength = menuLine0.length() - String.valueOf(page).length() - String.valueOf(totalPages).length() - 2;
		//String titleBig = new String(new char[titleLength]).replace("\0", "+");
		//String titleSmall = new String(new char[24 - titleLength]).replace("\0", "-");

		//send("Qs76=" + menuLine0.substring(0, titleLength) + page + "/" + totalPages + "_" + titleBig + titleSmall);
		//send("Qs77=" + menuLine1 + getLine(1));
		//staticBottomMenu();
		//send("Qs79=" + menuLine3 + getLine(3));
		//send("Qs80=" + menuLine4 + getLine(4));
		//send("Qs81=" + menuLine5 + getLine(5));
		//send("Qs82=" + menuLine6 + getLine(6));
		//send("Qs83=" + qs83Menu + getLine(7));
		//send("Qs84=" + qs84Menu + getLine(8));
		//send("Qs85=" + qs85Menu + getLine(9));
		//send("Qs86=" + qs86Menu + getLine(10));
		//send("Qs87=" + qs87Menu + getLine(11));
		//send("Qs88=" + qs88Menu + getLine(12));

		//setMenuColours();
	}

	public static void rskPushed(int id) {
		if (page == 1) {
			switch (id) {
			case 51: // TO/GA
				send("Qh387=3");
				break;
			case 52: // AUTOBRAKES
				break;
			case 53: // SPEEDBRAKES
				break;
			case 54: // REVERSER
				break;
			case 55: // A/T DISCONNECT
				send("Qh386=1");
				break;
			case 56: // SET N1
				break;
			}
		} else if (page == 2) {
			switch (id) {
			case 51: // FUEL CUTOFF
				break;
			case 52:
				break;
			case 53:
				break;
			case 54:
				break;
			case 55: // FLAPS
				break;
			case 56: // PARK BRAKE
				break;
			}
		}
		showMenu();
	}

	static String getLine(int line) {
		if (page == 1) {
			switch (line) {
			case 1:
				return "________________";
			case 2:
				return "_____TO/GA>";

			case 3:
				return "______AUTOBRAKES";
			case 4:
				return "<ACT>_______bbb>";

			case 5:
				return "______SPEEDBRAKE";
			case 6:
				return "_________EXTEND>";

			case 7:
				return "_______THROTTLES";
			case 8:
				return "________REVERSE>";

			case 9:
				return "___AUTO_THROTTLE";
			case 10:
				return "_____DISCONNECT>";

			case 11:
				return "__________THRUST";
			case 12:
				String n1Formatted = "___";
				n1Formatted = n1Formatted.substring(0,n1Formatted.length() - n1.length()) + n1;
				//System.out.println(n1Formatted);
				return "____________" + n1Formatted + "%";
			}
			return "________________";
		} else if (page == 2) {
			switch (line) {
			case 1:
				return "_____FUEL_CUTOFF";
			case 2:
				return "_1_2_3_4_b>";

			case 3:
				return "________________";
			case 4:
				return "<ACT>___________";

			case 5:
				return "________________";
			case 6:
				return "________________";

			case 7:
				return "________________";
			case 8:
				return "________________";

			case 9:
				return "___________FLAPS";
			case 10:
				String flapFormatted = "";
				if(flapLever == 1 || flapLever == 2)
					flapFormatted = "_";
				String flapLine = "______________" + flapFormatted + flaps[flapLever];
				//System.out.println(flapLine);
				return flapLine;

			case 11:
				return "______PARK_BRAKE";
			case 12:
				return "____________SET>";
			}
			return "________________";
		} else {
			return "________________";
		}
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
