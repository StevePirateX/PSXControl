
public class Fuel {
	
	//All weights in lbs
	static String output = "";
	static String preString = "P";
	static int[] newFuel = {20,-20,-20,20,0,0,0,0,0};
	
	//Fuel totals
	static int maxMainsSml	= 292928;
	static int maxMainsLrg 	= 840578;
	static int maxRes		= 88296;
	static int maxCtr 		= 1149998;
	static int maxStab 		= 221102;
	static int maxAux 		= 214951;
	
	static int fuelMax = 2 * maxMainsSml + 2 *  + 2 * maxMainsLrg + 2 * maxRes + maxCtr + maxStab + maxAux;
	
	
	
	
	public static String fuelInput(int input) {
		output = "";
		newFuel[0] = 20;
		newFuel[1] = -20;
		newFuel[2] = -20;
		newFuel[3] = 20;
		newFuel[4] = 0;
		newFuel[5] = 0;
		newFuel[6] = 0;
		newFuel[7] = 0;
		newFuel[8] = 0;
		
		
		if (input == 0){
			
		} else if(input <= 1171632) {
			// Up to 53.1 tonnes
			System.out.println("Tier 1");
			for(int i=0;i<4;i++) {
				newFuel[i] = (int) (i == 0 || i == 3 ? input / 4 + 20 : input / 4 - 20);
			}
		} else if(input <= 1493190) {
			// Up to 67.1 tonnes
			System.out.println("Tier 2");
			newFuel[0] = maxMainsSml;
			newFuel[3] = maxMainsSml;
			int extraFuelReq = input - (2 * maxMainsSml);
			newFuel[1] = (int) extraFuelReq/2;
			newFuel[2] = (int) extraFuelReq/2;
			
		} else if(input <= 1669782) {
			// Up to 75.8 tonnes
			System.out.println("Tier 3");
			newFuel[0] = maxMainsSml;
			newFuel[1] = 453667;
			newFuel[2] = 453667;
			newFuel[3] = maxMainsSml;

			int extraFuelReq = input - (newFuel[0] + newFuel[1] + newFuel[2] + newFuel[3]);
			newFuel[4] = (int) extraFuelReq/2;
			newFuel[5] = (int) extraFuelReq/2;
		} else if(input <= 2443610) {
			System.out.println("Tier 4");
			newFuel[0] = maxMainsSml;
			newFuel[3] = maxMainsSml;
			newFuel[4] = maxRes;
			newFuel[5] = maxRes;
			
			int extraFuelReq = input - (newFuel[0] + newFuel[3] + newFuel[4] + newFuel[5]);
			newFuel[1] = (int) extraFuelReq/2;
			newFuel[2] = (int) extraFuelReq/2;
		} else if(!PSXControlNetThread.fuelAux && !PSXControlNetThread.fuelStab) {
			// NEITHER AUX NOR STAB TANKS
			if(input <= 3593540) {
				System.out.println("Tier 5");
				newFuel[0] = maxMainsSml;
				newFuel[1] = maxMainsLrg;
				newFuel[2] = maxMainsLrg;
				newFuel[3] = maxMainsSml;
				newFuel[4] = maxRes;
				newFuel[5] = maxRes;
				
				int extraFuelReq = input - (newFuel[0] + newFuel[1] + newFuel[2]  + newFuel[3] + newFuel[4] + newFuel[5]);
				newFuel[6] = (int) extraFuelReq;
			}  else {
				System.out.println("Tier 7");
				newFuel[0] = maxMainsSml;
				newFuel[1] = maxMainsLrg;
				newFuel[2] = maxMainsLrg;
				newFuel[3] = maxMainsSml;
				newFuel[4] = maxRes;
				newFuel[5] = maxRes;
				newFuel[6] = maxCtr;
			}
		} else if(PSXControlNetThread.fuelStab && !PSXControlNetThread.fuelAux) {
			// Stab but no Aux tanks			
			if(input <= 2773604) {
				System.out.println("Tier 5");
				newFuel[0] = maxMainsSml;
				newFuel[1] = maxMainsLrg;
				newFuel[2] = maxMainsLrg;
				newFuel[3] = maxMainsSml;
				newFuel[4] = maxRes;
				newFuel[5] = maxRes;
				
				int extraFuelReq = input - (newFuel[0] + newFuel[1] + newFuel[2]  + newFuel[3] + newFuel[4] + newFuel[5]);
				newFuel[6] = (int) extraFuelReq;
			} else if(input <= 3813230) {
				System.out.println("Tier 6");
				newFuel[0] = maxMainsSml;
				newFuel[1] = maxMainsLrg;
				newFuel[2] = maxMainsLrg;
				newFuel[3] = maxMainsSml;
				newFuel[4] = maxRes;
				newFuel[5] = maxRes;
				newFuel[8] = 0; // No Aux Tank
		
				int extraFuelReq = input - 2773604;
				// int extraFuelReq = input - (newFuel[0] + newFuel[1] + newFuel[2]  + newFuel[3] + newFuel[4] + newFuel[5] + newFuel[6]);
				System.out.println("Extra Fuel: " + extraFuelReq);
				newFuel[6] = (int) (extraFuelReq * 0.79 + 330000);
				newFuel[7] = (int) (extraFuelReq * 0.21);
			} else {
				System.out.println("Tier 7");
				newFuel[0] = maxMainsSml;
				newFuel[1] = maxMainsLrg;
				newFuel[2] = maxMainsLrg;
				newFuel[3] = maxMainsSml;
				newFuel[4] = maxRes;
				newFuel[5] = maxRes;
				newFuel[6] = maxCtr;
				newFuel[7] = maxStab;
				newFuel[8] = 0; // No Aux Tank
			}
	}else if(PSXControlNetThread.fuelAux && !PSXControlNetThread.fuelStab) {
		// AUX BUT NO STAB TANKS			
		if(input <= 2773610) {
			System.out.println("Tier 5");
			newFuel[0] = maxMainsSml;
			newFuel[1] = maxMainsLrg;
			newFuel[2] = maxMainsLrg;
			newFuel[3] = maxMainsSml;
			newFuel[4] = maxRes;
			newFuel[5] = maxRes;
			
			int extraFuelReq = input - (newFuel[0] + newFuel[1] + newFuel[2]  + newFuel[3] + newFuel[4] + newFuel[5]);
			newFuel[6] = (int) extraFuelReq;
		} else if(input <= 3808550) {
			System.out.println("Tier 6");
			newFuel[0] = maxMainsSml;
			newFuel[1] = maxMainsLrg;
			newFuel[2] = maxMainsLrg;
			newFuel[3] = maxMainsSml;
			newFuel[4] = maxRes;
			newFuel[5] = maxRes;
			newFuel[7] = 0; // No Stab Tank
	
			int extraFuelReq = input - 2773610;
			// int extraFuelReq = input - (newFuel[0] + newFuel[1] + newFuel[2]  + newFuel[3] + newFuel[4] + newFuel[5] + newFuel[6]);
			System.out.println("Extra Fuel: " + extraFuelReq);
			newFuel[6] = (int) (extraFuelReq * 0.7877 + 330000);
			newFuel[8] = (int) (extraFuelReq * 0.2123);
			if(newFuel[8] > 214940) {
				newFuel[6] += (int) (extraFuelReq * 0.2123) - 214940;
				System.out.println("Extra");
			}
		} else {
			System.out.println("Tier 7");
			newFuel[0] = maxMainsSml;
			newFuel[1] = maxMainsLrg;
			newFuel[2] = maxMainsLrg;
			newFuel[3] = maxMainsSml;
			newFuel[4] = maxRes;
			newFuel[5] = maxRes;
			newFuel[6] = maxCtr;
			newFuel[7] = 0; // No Stab Tank
			newFuel[8] = maxAux;
		}
} else {
			// Stab and aux tanks
			if(input <= 2505210) {
				newFuel[0] = maxMainsSml;
				newFuel[1] = maxMainsLrg;
				newFuel[2] = maxMainsLrg;
				newFuel[3] = maxMainsSml;
				newFuel[4] = maxRes;
				newFuel[5] = maxRes;
				
				int extraFuelReq = input - (newFuel[0] + newFuel[1] + newFuel[2]  + newFuel[3] + newFuel[4] + newFuel[5]);
				newFuel[6] = (int) extraFuelReq;
			} else if(input <= 4029650) {
				newFuel[0] = maxMainsSml;
				newFuel[1] = maxMainsLrg;
				newFuel[2] = maxMainsLrg;
				newFuel[3] = maxMainsSml;
				newFuel[4] = maxRes;
				newFuel[5] = maxRes;
		
				int extraFuelReq = input - 2505210;
				newFuel[6] = (int) (extraFuelReq * 0.715) + 61600;
				newFuel[7] = (int) (extraFuelReq * 0.145);
				newFuel[8] = (int) (extraFuelReq * 0.14);
			} else {
				newFuel[0] = maxMainsSml;
				newFuel[1] = maxMainsLrg;
				newFuel[2] = maxMainsLrg;
				newFuel[3] = maxMainsSml;
				newFuel[4] = maxRes;
				newFuel[5] = maxRes;
				newFuel[6] = maxCtr;
				newFuel[7] = maxStab;
				newFuel[8] = maxAux;
			}
		}
		
		
		
		for(int i=0;i<newFuel.length;i++) {
			output += newFuel[i];
			output += ";";
		}
		
		return preString + output;
	}

}
