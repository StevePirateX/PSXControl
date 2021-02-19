
//AddonExampleNetThread.java is a class used by AddonExample.java

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

class PSXControlNetThread extends Thread {

	// Network thread variables
	boolean viewMessages = false;
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter ou = null;
	private boolean remoteExit;
	private String targetHost;
	private int targetPort;
	public int clientId = 0;
	public static String staticFltData;
	public static String rawPosition;
	public static int forceRouteUpdate = 0;
	public String rawRte;
	public static String otherCdus;
	// private static String cCdu;
	// public static String[] rteData;

	PSXControlNetThread(String h, int p) {
		targetHost = h;
		targetPort = p;
	}

	// protected List<String> eventSVariables = new ArrayList<String>();
	// protected List<Integer> eventIVariables = new ArrayList<Integer>();
	// protected List<String> eventHVariables = new ArrayList<String>();

	// CDU CONTROL
	// New load
	public static StringBuilder currentSituation = new StringBuilder(); // What to load if a reload is requested
	public static StringBuilder eventRcdSituation = new StringBuilder();
	public boolean isPsxLoading = true;
	public String startPos;
	public String timeEarth;
	// public String scratchPad = "";
	// public boolean scratchPadLock = false; // Locks entry such as when you press
	// DELETE
	// public boolean deleteActive = false;
	// public String[] cduKeyboard = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
	// "9", "A", "B", "C", "D", "E", "F", "G",
	// "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
	// "W", "X", "Y", "Z" };
	// public static String viewPSXTime = "----";
	// public Calendar psxTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	// boolean invalidEntry = false;

	// Fuel
	static boolean fuelAux = false;
	static boolean fuelStab = false;
	String[] tanks = new String[11];

	// Array for all variables
	// public ArrayList<String> psxVariables = new ArrayList<>();

	public static int activeMenu = 0; // Which menu is active - main menu = 1
	// public static boolean terrOvrd = false; // True if terrOvrd is turned on in
	// the sim

	// Position Calculations

	public boolean getRandomBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}

	void functionInactive() {
		send("Qs89=_FUNCTION_INACTIVE");
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// send("Qs89=" + CduMenu.qs89Menu);
	}

	@Override
	public void run() {

		try {

			PSXControl.connectingUI(2); // Set UI for Connecting
			socket = new Socket(targetHost, targetPort);
			ou = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PSXControl.connectingUI(3); // Set UI to be isConnected
		} catch (UnknownHostException e) {
			// System.out.println(e);
			PSXControl.connectingUI(1); // Set UI to be disconnected
			if (!PSXControl.autoConnecting) {
				JOptionPane.showMessageDialog(null,
						"Unable to connect. Please check the Server IP / Port and try again", "ERROR: Unknown Address",
						JOptionPane.ERROR_MESSAGE);
			}
			return;
		} catch (IOException e) {
			// System.out.println(e);
			PSXControl.connectingUI(1); // Set UI to be disconnected
			if (!PSXControl.autoConnecting) {
				JOptionPane.showMessageDialog(null,
						"Unable to connect. Please check the Server IP / Port and try again", "ERROR: PSX Not Running",
						JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		// *********************************************************************
		// Reader:

		try {
			// PSXControl.sendToServer("demand=Qs483");

			String message;
			char qCategory;
			int qIndex;
			// int val;
			int parseMark;

			PSXControl.psx = new Aircraft();

			while (in != null) {

				if ((message = in.readLine()) != null) {

					if(PSXControl.debugReceiving)
						PSXControl.debugMessage("RECEIVING:   " + message);
					// System.out.println(message);
					// if(viewMessages) { System.out.println(message);}
					if (message.startsWith("version")) {
						PSXControl.psx.version = message.substring(message.indexOf('=') + 1).trim();
						PSXControl.send("demand=Qs483");
					}

					try {
						if (message.charAt(0) == 'Q') {
							parseMark = message.indexOf('=');
							try {
								qIndex = Integer.parseInt(message.substring(2, parseMark));
								qCategory = message.charAt(1);

								parseMark++;

								if (qCategory == 's') {
									PSXControl.psx.setStringVariable(qIndex, message.substring(parseMark).trim());

									// CIRCUIT BREAKERS
									if (qIndex >= 4 && qIndex <= 61) {
										String raw = message.substring(parseMark).trim();
										PSXControl.psx.cbs[qIndex - 3][0] = "Qs" + qIndex;
										PSXControl.psx.cbs[qIndex - 3][1] = raw;
									}

									// Wx Radar
									if (qIndex == 104) {
										String raw = message.substring(parseMark).trim();

										MenuActions.wxPanel = raw;

										if (raw.substring(7, 8).equals("e")) {
											MenuActions.isWxRadarTesting = true;
										} else {
											MenuActions.isWxRadarTesting = false;
										}

										if (MenuActions.isWxRadarTesting || MenuActions.isTcasTesting) {
											MenuActions.isWxTcasTesting = true;
										} else {
											MenuActions.isWxTcasTesting = false;
										}

										// if (CduMenu.activePage == CduMenu.PAGE_ACTIONS) {
										// MenuActions.showMenu();
										// }
									}

									// ALTITUDE + HEADING
									if (qIndex == 121) {
										String rawPosition = message.substring(parseMark).trim();
										String[] pos = rawPosition.split(";");

										//MenuWeather.hdg = (int) (Float.parseFloat(pos[2]) * 180 / Math.PI);
										MenuSituation.verticalSpeed = Situations
												.getVerticalSpeed(Integer.parseInt(pos[3]));
										MenuSituation.yawRate = Situations.getVerticalSpeed(CduMenu.psx.getHdg());

									}

									if (qIndex == 123) {
										long raw = Long.parseLong(message.substring(parseMark).trim());
										// System.out.println("TIME: " + raw);

										CduMenu.psxTime.setTimeInMillis(raw);

										SimpleDateFormat hm = new SimpleDateFormat("HHmm");
										hm.setTimeZone(TimeZone.getTimeZone("GMT"));

										if (!CduMenu.viewPSXTime.equals(hm.format(CduMenu.psxTime.getTime()))) {
											CduMenu.viewPSXTime = hm.format(CduMenu.psxTime.getTime());

											if (PSXControl.active && CduMenu.activePage == CduMenu.PAGE_SITUATION
													&& !isPsxLoading) {
												CduMenu.displayMenu();
											}
										}
									}

									// CUSTOM EICAS MESSAGE
									/*
									 * if (qIndex >= 130 & qIndex <= 134 ) { String raw =
									 * message.substring(parseMark).trim(); System.out.println("Qs" + qIndex + " = "
									 * + raw); send("Qs421=HELLO"); Thread.sleep(2000); send("Qs421="); }
									 */

									// WEATHER
									if (qIndex == 327) {
										String raw = message.substring(parseMark).trim();
										PSXControl.psx.wxAloft = raw.split(";");
									}

									if (qIndex == 328) {
										String raw = message.substring(parseMark).trim();
										// System.out.println("Weather (Qs328): " + raw);
										PSXControl.psx.weatherArray = raw.split(";");
										for (int i = 0; i < PSXControl.psx.weatherArray.length; i++) {
											PSXControl.psx.wxBasic[i] = PSXControl.psx.weatherArray[i];
										}
									}

									if (qIndex == (328 + PSXControl.psx.wxZone)) {
										String raw = message.substring(parseMark).trim();
										MenuWeather.weather = raw;
										PSXControl.psx.weatherArray = raw.split(";");
										//String strAltim = raw.substring(raw.lastIndexOf(';') + 1).trim();
										//MenuService.altim = Integer.parseInt(strAltim) * 10;
										//MenuService.qnh = (int) Math.round(MenuService.altim * 3.38639);
									}

									// ACTIVE WEATHER ZONE
									if (qIndex == (335 + PSXControl.psx.wxZone)) {
										// String raw = message.substring(parseMark).trim();
										// System.out.println(raw);
										// weatherIcao = raw.substring(raw.lastIndexOf(';') + 1).trim().substring(0, 4);
									}

									// Saving Weather Details
									if (qIndex >= 343 & qIndex <= 349) {
										String raw = message.substring(parseMark).trim();
										int i = qIndex - 343;
										MenuWeather.weatherMetars[i] = raw;

										if (PSXControl.psx.wxZone == i + 1) {
											if (MenuWeather.weatherMetars[i].substring(0, 1).equals("i")) {
												PSXControl.psx.preMetar = MenuWeather.weatherMetars[i].substring(1,
														MenuWeather.weatherMetars[i].indexOf("KT") + 2);
											} else {
												PSXControl.psx.preMetar = MenuWeather.weatherMetars[i].substring(0,
														MenuWeather.weatherMetars[i].indexOf("KT") + 2);
											}
											PSXControl.psx.postMetar = MenuWeather.weatherMetars[i]
													.substring(MenuWeather.weatherMetars[i].indexOf("KT") + 3);

											if (MenuWeather.weatherMetars[i].substring(0, 1).equals("i")) {
												CduMenu.psx.weatherIcao = MenuWeather.weatherMetars[i].substring(1, 5);
											} else {
												CduMenu.psx.weatherIcao = MenuWeather.weatherMetars[i].substring(0, 4);
											}
											// System.out.println("ICAO: " + weatherIcao + ", METAR:" +
											// weatherMetars[i]);
										}
										// System.out.println(preMetar + " " + postMetar);
									}

									if (qIndex == 406) {
										String raw = message.substring(parseMark).trim();
										// System.out.println("CDU: " + raw);
										otherCdus = raw.substring(0, 4);

										int CCduActive = Integer.parseInt(raw.substring(4));

										if (PSXControl.CDUPosition == CCduActive) {
											PSXControl.psx.isActiveSubSystem = true;
											if (PSXControl.active == false) {

												PSXControl.active = true;
												activeMenu = 1;
												CduMenu.showMainMenu();
											}
										} else {
											PSXControl.active = false;
											PSXControl.psx.isActiveSubSystem = false;
										}
										// System.out.println(PSXControl.active);

									}
									
									if(qIndex == PSXControl.psx.qsTla && (CduMenu.activePage == CduMenu.PAGE_RUNWAY || CduMenu.activePage == CduMenu.PAGE_ENGINES)) { // Thrust
										//String raw = message.substring(parseMark).trim();
										PSXControl.psx.getAvgThrust();
										CduMenu.displayMenu();
									}

									if (qIndex == 438 && PSXControl.active && (CduMenu.activePage == CduMenu.PAGE_SERVICE || (CduMenu.activePage == CduMenu.PAGE_GROUND && MenuGround.page == 2))) {
											CduMenu.displayMenu();
									}

									if (qIndex == 483 && CduMenu.activePage == CduMenu.PAGE_WEATHER) {
										CduMenu.displayMenu();
									}

								} else if (qCategory == 'i') {
									PSXControl.psx.setIntegerVariable(qIndex,
											Integer.parseInt(message.substring(parseMark).trim()));

									// Aux Tank
									if (qIndex == 23) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										if (raw == 1) {
											fuelAux = true;
										} else {
											fuelAux = false;
										}
									}

									// Stab Tank
									if (qIndex == 24) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										if (raw == 1) {
											fuelStab = true;
										} else {
											fuelStab = false;
										}
									}

									// TRIM
									if (qIndex == 119) {
										float raw = Float.parseFloat(message.substring(parseMark).trim()) / 1000;
										MenuService.trim = (float) (0.0019 * Math.pow(raw, 3) - 0.045 * Math.pow(raw, 2)
												+ 1.2544 * raw);
										// System.out.println(String.format("%.2f", CduMenu.trim));
										if (PSXControl.active && CduMenu.activePage == CduMenu.PAGE_SERVICE)
											CduMenu.displayMenu();
									}

									// Stab Trim
									if(qIndex == 119 && CduMenu.activePage == CduMenu.PAGE_AIRBORNE) {
										CduMenu.displayMenu();
									}
									
									// Rudder Trim
									if(qIndex == 121 && CduMenu.activePage == CduMenu.PAGE_AIRBORNE) {
										CduMenu.displayMenu();
									}
									
									// CG
									if (qIndex == 122 &&PSXControl.active && (CduMenu.activePage == CduMenu.PAGE_SERVICE || (CduMenu.activePage == CduMenu.PAGE_GROUND && MenuGround.page == 2))) {
											CduMenu.displayMenu();
									}

									// MOTION FREEZE
									if (qIndex == 129) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										// System.out.println(raw);
										if (raw == 0)
											PSXControl.psx.motionFreeze = false;
										else {
											PSXControl.psx.motionFreeze = true;
										}
										if (activeMenu == 1 && !MenuSituation.didAddonCauseReload && PSXControl.active)
											CduMenu.displayMenu();
									}

									if(qIndex == 132 && CduMenu.activePage == CduMenu.PAGE_GROUND) {
										CduMenu.displayMenu();
									}
									
									// REAL WEATHER
									if (qIndex == 139) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										if (raw == 1) {
											MenuWeather.realWxChecked = true;
										} else {
											MenuWeather.realWxChecked = false;
										}
									}
									
									if(qIndex == 174 && CduMenu.activePage == CduMenu.PAGE_GROUND) {
										CduMenu.displayMenu();
									}

									// DOOR OPEN BITS
									if (qIndex == 179) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										// System.out.println("Command Door Bits = " + raw);
										MenuService.comDoorBits = raw;
										if (MenuService.comDoorBits >= 8 && MenuService.comDoorBits < 16) {
											MenuService.doorBoardingSide = "L";
										} else if (MenuService.comDoorBits >= 16 && MenuService.comDoorBits < 24) {
											MenuService.doorBoardingSide = "R";
										} else if (MenuService.comDoorBits >= 24) {
											MenuService.doorBoardingSide = "B";
										} else {
											MenuService.doorBoardingSide = "";
										}
										if (PSXControl.active)
											CduMenu.displayMenu();
									}

									// DOOR OPEN BITS
									if (qIndex == 180) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										// System.out.println("Open Bits = " + raw);
										if (raw > 0) {
											MenuService.areDoorsOpen = true;
										} else {
											MenuService.areDoorsOpen = false;
										}
										if (PSXControl.active)
											CduMenu.displayMenu();
									}

									// So QNH can refer to current weather zone
									if (qIndex == 240) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										PSXControl.psx.wxZone = raw;
										int i = PSXControl.psx.wxZone - 1;
										// System.out.println(raw + " - " + weatherMetars[i]);

										try {
											if (MenuWeather.weatherMetars[i].substring(0, 1).equals("i")) {
												PSXControl.psx.preMetar = MenuWeather.weatherMetars[i].substring(1,
														MenuWeather.weatherMetars[i].indexOf("KT") + 2);
											} else {
												PSXControl.psx.preMetar = MenuWeather.weatherMetars[i].substring(0,
														MenuWeather.weatherMetars[i].indexOf("KT") + 2);
											}
											PSXControl.psx.postMetar = MenuWeather.weatherMetars[i]
													.substring(MenuWeather.weatherMetars[i].indexOf("KT") + 3);
											CduMenu.psx.weatherIcao = MenuWeather.weatherMetars[i].substring(0, 4);
											// System.out.println("ICAO: " + weatherIcao + ", METAR:" +
											// weatherMetars[i]);
										} catch (Exception e) {

										}

									}

								} else if (qCategory == 'h') {
									PSXControl.psx.setHumanVariable(qIndex, message.substring(parseMark).trim());

									if(qIndex == PSXControl.psx.autoBrakeSw && CduMenu.activePage == CduMenu.PAGE_RUNWAY) {
										CduMenu.displayMenu();
									}
									
									if (qIndex == 163 || qIndex == 164) {
										// int raw = Integer.parseInt(message.substring(parseMark).trim());

										boolean isAnyGearExtended = false;
										for (int i = 0; i < 2; i++) {
											int varValue = Integer.valueOf(PSXControl.psx.qhVariables[163 + i]);
											if (varValue == 1) {
												isAnyGearExtended = true;
											}
										}

										MenuActions.isAltGearArmed = isAnyGearExtended;
									}

									if (qIndex >= 166 && qIndex <= 168) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());

										if (raw % 2 == 1) {
											switch (qIndex) {
											case 166:
												MenuActions.FlapOvrd = true;
												break;
											case 167:
												MenuActions.GearOvrd = true;
												break;
											case 168:
												MenuActions.TerrOvrd = true;
												break;
											}
										} else if (raw % 2 == 0) {
											switch (qIndex) {
											case 166:
												MenuActions.FlapOvrd = false;
												break;
											case 167:
												MenuActions.GearOvrd = false;
												break;
											case 168:
												MenuActions.TerrOvrd = false;
												break;
											}
										}

										// System.out.println("Prox: " + MenuActions.FlapOvrd + " " +
										// MenuActions.GearOvrd + " " + MenuActions.TerrOvrd);
										if (MenuActions.FlapOvrd || MenuActions.GearOvrd || MenuActions.TerrOvrd) {
											MenuActions.isGroundProxBypassed = true;
											// if (CduMenu.activePage == CduMenu.PAGE_ACTIONS)
											// MenuActions.showMenu();
										} else {
											MenuActions.isGroundProxBypassed = false;
											// if (CduMenu.activePage == CduMenu.PAGE_ACTIONS)
											// MenuActions.showMenu();
										}
									}

									if (qIndex == 168) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										if (raw == 0 || raw == 32) {
											PSXControl.psx.terrOvrd = false;
											// System.out.println("Terrain is off: " + raw);
										} else if (raw > 0 && raw != 32) {
											PSXControl.psx.terrOvrd = true;
											// System.out.println("Terrain is on: " + raw);
										}

										if (PSXControl.active)
											CduMenu.displayMenu();
									}
									
									if(qIndex == PSXControl.psx.gearLever) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										PSXControl.psx.prevGearLeverPos = PSXControl.psx.currentGearLeverPos;
										PSXControl.psx.currentGearLeverPos = raw;
										if(CduMenu.activePage == CduMenu.PAGE_RUNWAY)
											CduMenu.displayMenu();
									}

									if(qIndex == 388 && CduMenu.activePage == CduMenu.PAGE_RUNWAY) {
										CduMenu.displayMenu();
									}
									
									if (qIndex >= PSXControl.psx.engineSwitchStart
											&& qIndex <= PSXControl.psx.engineSwitchStart + 4
											&& CduMenu.activePage == CduMenu.PAGE_ENGINES) {
										CduMenu.displayMenu();
									}
									
									if(qIndex == PSXControl.psx.varTiller && CduMenu.activePage == CduMenu.PAGE_GROUND) {
										CduMenu.displayMenu();
									}

									if (PSXControl.active) {
										// Centre CDU Button
										if (qIndex == 402) {
											int raw = Integer.parseInt(message.substring(parseMark).trim());
											// System.out.println(raw);

											if (CduMenu.invalidEntry) {
												send("Qs89=" + CduMenu.scratchPad);
												CduMenu.invalidEntry = false;
											} else {

												// Character pushed
												if (!CduMenu.scratchPadLock && !CduMenu.scratchPad.startsWith("B")
														&& !CduMenu.scratchPad.startsWith("L")
														&& !CduMenu.scratchPad.startsWith("R")) {
													if (raw >= 0 & raw <= 9 || ((raw == 11 || raw == 21 || raw == 27)
															&& CduMenu.scratchPad.length() == 0)) {
														CduMenu.scratchPad += CduMenu.cduKeyboard[raw];
														send("Qs89=" + CduMenu.scratchPad);
													}
												}

												// DELETE pushed
												if (raw == 37) {
													CduMenu.deletePushed();
												}

												// CLR pushed
												if (raw == 39) {
													CduMenu.clearPushed();
												}

												if (raw == 49 || raw == 50) {
													int direction = (raw == 49 ? -1 : 1);
													CduMenu.setPage(direction);
												}

												if (raw == 67) {
													CduMenu.scratchPad += ".";
													send("Qs89=" + CduMenu.scratchPad);
												}

												if (raw == 68) {
													if (CduMenu.scratchPad.length() == 0) {
														CduMenu.scratchPad += "-";
													} else if (CduMenu.scratchPad
															.substring(CduMenu.scratchPad.length() - 1).equals("-")) {
														CduMenu.scratchPad = CduMenu.scratchPad.substring(0,
																CduMenu.scratchPad.length() - 1) + "+";
													} else if (CduMenu.scratchPad
															.substring(CduMenu.scratchPad.length() - 1).equals("+")) {
														CduMenu.scratchPad = CduMenu.scratchPad.substring(0,
																CduMenu.scratchPad.length() - 1) + "-";
													} else {
														CduMenu.scratchPad += "-";
													}
													send("Qs89=" + CduMenu.scratchPad);
												}

												if (activeMenu == 1) {
													// Motion Freeze
													if (raw >= 41 && raw <= 46)
														CduMenu.skPushed(raw);

													// MENU BUTTON
													if (raw == 47) {
														CduMenu.scratchPad = "";
													}

													if (raw >= 51 & raw <= 56) {
														CduMenu.skPushed(raw);
													}

												}
											}
										}
									}
									
									if (qIndex == PSXControl.psx.flapLever && CduMenu.activePage == CduMenu.PAGE_RUNWAY) {
										CduMenu.displayMenu();
									}

									if (qIndex == 410) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());
										MenuActions.lAcpStatus = raw;

										if (raw == 27) {
											MenuActions.isInterphoneActive = true;
										} else if (raw == -1 || raw == 26) {
											MenuActions.isInterphoneActive = false;
										}

										// if (CduMenu.activePage == CduMenu.PAGE_ACTIONS)
										// MenuActions.showMenu();
									}

									if (qIndex == 414) {
										int raw = Integer.parseInt(message.substring(parseMark).trim());

										MenuActions.tcasPanel = raw;

										if (raw == 10) {
											MenuActions.isTcasTesting = true;
										} else {
											MenuActions.isTcasTesting = false;
										}

										if (MenuActions.isWxRadarTesting || MenuActions.isTcasTesting) {
											MenuActions.isWxTcasTesting = true;
										} else {
											MenuActions.isWxTcasTesting = false;
										}

										// if (CduMenu.activePage == CduMenu.PAGE_ACTIONS)
										// MenuActions.showMenu();
									}
								}

							} catch (NumberFormatException nfe) {
								// nfe.printStackTrace();
							}
						} else if (message.charAt(0) == 'L') {

							// Lexicon at net connect - Ignore

						} else if (message.substring(0, 3).equals("id=")) {

							try {
								clientId = Integer.parseInt(message.substring(3));
								PSXControl.setBtnConnectText("Disconnect - Client: " + clientId);
								PSXControl.debugMessage("Connection OK. CLIENT ID: " + clientId);
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
							}

						} else if (message.length() > 8 && message.substring(0, 8).equals("version=")) {
							// Check version agreement if required
						} else if (message.equals("load1")) {
							// Situation psxLoading phase 1 (paused and reading variables)
						} else if (message.equals("load2")) {
							// Situation psxLoading phase 2 (reading model options)
						} else if (message.equals("load3")) {
							// Situation psxLoading phase 3 (unpaused)
							viewMessages = false;
							if (!MenuSituation.didAddonCauseReload) {
								MenuSituation.situIVariables = PSXControl.psx.qiVariables.clone();
								MenuSituation.situHVariables = PSXControl.psx.qhVariables.clone();
								MenuSituation.situSVariables = PSXControl.psx.qsVariables.clone();
							}
							MenuSituation.didAddonCauseReload = false;
							if (PSXControl.isConnected) {
								String s = "cduC=" + PSXControl.CDUPosition;
								if (PSXControl.CDUPosition < 20) {
									s += "<PSXCTRL";
								} else if (PSXControl.CDUPosition >= 20) {
									s += "PSXCTRL>";
								}
								send(s);
							}
						} else if (message.equals("exit")) {
							remoteExit = true;
							break;
						} else if (message.startsWith("metar=")) {
							// METAR feeder status message
						}

					} catch (StringIndexOutOfBoundsException sioobe) {
						sioobe.printStackTrace();
					}
				}
			}

			try {
				notifyAll();
			} catch (Exception e) {

			}
		} catch (

		IOException e) {
		}
		// System.out.println("Is connected?: " + PSXControl.isConnected);

		// If connection lost - AUTO RECONNECT
		if (PSXControl.isConnected & PSXControl.autoReconnect) {
			finalJobs();
			PSXControl.autoConnecting = true;
			PSXControl.txt_HostIP.setEnabled(false);
			PSXControl.txt_HostPort.setEnabled(false);
			PSXControl.lab_Conn.setText("CONNECTING");
			PSXControl.setBtnConnectText("Reconnecting");
			PSXControl.lab_Conn.setForeground(Color.decode("#FF9900"));

			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (!PSXControl.isConnected & PSXControl.autoConnecting) {
				PSXControl.swingWorker = null;
				run();

				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} else {
			finalJobs();
		}

	}

	void finalJobs() {
		try {
			System.out.println("Final jobs!!");
			if (PSXControl.isConnected) {
				System.out.println("Final CDU Position: " + PSXControl.CDUPosition);
				send("cduC=" + PSXControl.CDUPosition);
			} else {
				if (PSXControl.psx != null)
					PSXControl.psx.isActiveSubSystem = false;

			}

			PSXControl.connectingUI(1);
			PSXControlConfig.saveProperties();

			if (!remoteExit && ou != null) {

				ou.println("exit");
				try {
					sleep(15);
				} catch (InterruptedException e) {
				}
				ou.close();
			}
			if (in != null)
				in.close();
			if (socket != null)
				socket.close();
			if (ou != null)
				ou.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	void send(String message) {
		if (ou != null) {
			if(PSXControl.debugSending)
				PSXControl.debugMessage("SENDING:     " + message);
			ou.println(message);
			if (ou.checkError())
				finalJobs();
		}
	}
	
}
