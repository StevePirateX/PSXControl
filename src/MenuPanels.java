
public class MenuPanels extends CduMenu {

	public MenuPanels() {
		// TODO Auto-generated constructor stub
	}

	// Pages
	static int page = 1;
	static int totalPages = 1;

	public static void showMenu() {
		newCduPage = getScreen(page);
		updatePage();
	}

	public static String[] getScreen(int page) {
		String[] screen = new String[cduLines];

		if (page == 1) {
			screen[0] = "_________PANELS_________";
			screen[1] = emptyLine;
			screen[2] = "<OVERHEAD_______________";
			screen[3] = emptyLine;
			screen[4] = "<MCP____________________";
			screen[5] = emptyLine;
			screen[6] = "<GLARESHIELD____________";
			screen[7] = emptyLine;
			screen[8] = "<THROTTLE QUAD__________";
			screen[9] = emptyLine;
			screen[10] = "<CTR_PEDASTOOL__________";
			screen[13] = emptyLine;
		}

		return screen;
	}		

	public static void skPushed(int id) {
		if (page == 1) {
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
				// Empty
				break;
			case 52:
				// Empty
				break;
			case 53:
				// Empty
				break;
			case 54:
				// Empty
				break;
			case 55:
				// Empty
				break;
			}
		}

		showMenu();
	}

}