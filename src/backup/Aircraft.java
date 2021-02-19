package backup;
public class Aircraft extends PSXControl {
	
	int psxNetThreadId = 0;
	String version = "10";
	int sVariables = 0;
	int iVariables = 0;
	int hVariables = 0;
	
	int variableArraySizes = 700;
	String[] qsVariables = new String[variableArraySizes];
	String[] qhVariables = new String[variableArraySizes];
	int [] qiVariables = new int[variableArraySizes];
	
	// PSX Control variables
	public boolean isActiveSubSystem = false;
	public boolean motionFreeze = false; // True if motion frozen
	public boolean terrOvrd = false; // True if TERR OVRD is on
	
	// Weather variables
	public String[] wxAloft = new String[3]; // Planet Weather Aloft
	public String[] wxBasic = new String[24]; // Planet Weather output local zones
	public String[] weatherArray; // Weather Array
	public String preMetar; // METAR all before clouds
	public String postMetar; // METAR all after winds
	public int wxZone; // Current active weather zone - used to calculate QNH
	
	

	protected void setPsxNetThreadId(int i) {
		psxNetThreadId = i;
	}
	
	public void setStringVariable(int id, String value){
		qsVariables[id] = value;
		if(id > sVariables){
			sVariables = id;
		}
		//System.out.println("ID: " + id + ": " + value + " inserted into: " + qsVariables.get(id));
	}
	
	public void setHumanVariable(int id, String value){
		qhVariables[id] = value;
		if(id > hVariables){
			hVariables = id;
		}
		//System.out.println("ID: " + id + ": " + value + " inserted into: " + qhVariables.get(id));
	}
	
	public void setIntegerVariable(int id, int value){
		qiVariables[id] = value;
		if(id > iVariables){
			iVariables = id;
		}
		//System.out.println("ID: " + id + ": " + value + " inserted into: " + qiVariables.get(id));
	}
	
	
	public void setPsxTime() {
		send("Qs123=" + System.currentTimeMillis());
		send("Qs124=" + System.currentTimeMillis());
		send("Qs125=" + System.currentTimeMillis());
	}
	
	
}
