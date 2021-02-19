package backup;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PSXControlConfig extends PSXControl{

	
	// Window Position
	public static int getXPosition() {
		Point p = frmPsxControl.getLocationOnScreen();
		return (int) p.getX();
	}
	public static int getYPosition() {
		Point p = frmPsxControl.getLocationOnScreen();
		return (int) p.getY();
	}
	
	
	
	// CDU Position
	public static void setCduPosition(String s) {	// Convert from properties to PSX format
		int col = 1;
		int row = Integer.parseInt(s.substring(1));
		if(s.substring(0, 1).matches("L")) {
			col = 1;
		}
		else if(s.substring(0, 1).matches("R")) {
			col = 2;
		}
		PSXControl.CDUPosition = col * 10 + row;
	}
	public static String getCduPosition(int i) {	// Convert from PSX format to properties
		String a;
		String b;
		if(i < 20) 
			a = "L";
		else
			a = "R";
		b = String.valueOf(i % 10);
		return a + b;
	}
	public static int getCduSide(String s) { 		// Selecting the saved value for the menu side
		int col = 0;
		if(s.substring(0, 1).matches("L")) {
			col = 0;
		}
		else if(s.substring(0, 1).matches("R")) {
			col = 1;
		}
		return col;
	}
	
	//Save and load to the properties file
	
	public static void loadProperties() throws IOException {
		Properties defaultProps = new Properties();
		defaultProps.setProperty("psxhost", "localhost");
		defaultProps.setProperty("psxport", "10747");
		defaultProps.setProperty("psxAutoConnect", "false");
		defaultProps.setProperty("autoMinimise", "false");
		defaultProps.setProperty("autoReconnect", "true");
		defaultProps.setProperty("cduPosition", "R6");
		defaultProps.setProperty("xPosition", "300");
		defaultProps.setProperty("yPosition", "200");
		
		configProps = new Properties(defaultProps);
		InputStream inputStream = new FileInputStream(configFile);
		configProps.load(inputStream);
		inputStream.close();
	}

	public static void saveProperties() {
		try {
			configProps.setProperty("psxhost", txt_HostIP.getText());
			configProps.setProperty("psxport", txt_HostPort.getText());
			configProps.setProperty("psxAutoConnect", String.valueOf(Options.cbxAutoConnect.isSelected()));
			configProps.setProperty("autoMinimise", String.valueOf(Options.cbxMinimise.isSelected()));
			configProps.setProperty("autoReconnect", String.valueOf(Options.cbxAutoReconnect.isSelected()));
			configProps.setProperty("cduPosition", getCduPosition(PSXControl.CDUPosition));
			configProps.setProperty("xPosition", String.valueOf(getXPosition()));
			configProps.setProperty("yPosition", String.valueOf(getYPosition()));

			File f = new File("config.cfg");
			OutputStream out = new FileOutputStream(f);
			configProps.store(out, "This is an optional header comment string");
			out.close();
			System.out.println("Config saved");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getProperties(){
		
	}
	
}