
public class MenuLayouts extends CduMenu {

	public MenuLayouts() {
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
			screen[0] = "________LAYOUT__________";
			screen[1] = emptyLine;
			screen[2] = "<1____________________6>";
			screen[3] = emptyLine;
			screen[4] = "<2____________________7>";
			screen[5] = emptyLine;
			screen[6] = "<3____________________8>";
			screen[7] = emptyLine;
			screen[8] = "<4____________________9>";
			screen[9] = emptyLine;
			screen[10] = "<5______________________";
			screen[13] = emptyLine;
		}

		return screen;
	}	

	public static void skPushed(int id) {
		if (page == 1) {
			switch (id) {
			case 41:
				setLayout(1);
				break;
			case 42:
				setLayout(2);
				break;
			case 43:
				setLayout(3);
				break;
			case 44:
				setLayout(4);
				break;
			case 45:
				setLayout(5);
				break;
			case 51:
				setLayout(6);
				break;
			case 52:
				setLayout(7);
				break;
			case 53:
				setLayout(8);
				break;
			case 54:
				setLayout(9);
				break;
			case 55:
				// Empty
				break;
			}
		}

		showMenu();
	}

	static void setLayout(int i) {
		send("layout=" + i);
	}
}
