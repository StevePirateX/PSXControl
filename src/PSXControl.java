import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class PSXControl {

	// VARIABLES
	static boolean qfa25 = true;
	static boolean debugSending = true;
	static boolean debugReceiving = false;
	static SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss.SSSS");

	// Main Application variables
	static String version = "1.2";
	public static PSXControlNetThread netThread;
	public static int clientId = 0;
	static Options options = new Options();
	static About about = new About();
	static File configFile = new File("config.cfg");
	static int defaultXPosition = 300;
	static int defaultYPosition = 100;
	static int xPosition = defaultXPosition;
	static int yPosition = defaultYPosition;

	// Properties
	static Properties configProps;
	static boolean autoConnect;
	static boolean autoMinimise;
	static boolean autoReconnect;
	static boolean autoConnecting = false;

	// UI & PSX Connection Variables
	private static JMenuItem menuOptions;
	protected static JTextField txt_HostIP; // Text field to define PSX host IP
	protected static JTextField txt_HostPort; // Text field to define PSX host port
	static boolean isConnected = false;
	static JButton btnConnect = new JButton("Connect");
	static JLabel lab_Conn = new JLabel("DISCONNECTED");
	public static SwingWorker<Object, String> swingWorker = null;

	// Server Settings
	static final boolean TO_SERVER = true, FROM_SERVER = !TO_SERVER;
	public static Aircraft psx;
	static String psxhost;
	static int psxport;

	// CDU Variables
	static CduMenu cduMenu = new CduMenu();
	static boolean active = false;
	static int CDUPosition = 11; // Default Position

	// Final frame
	protected static JFrame frmPsxControl;

	// --- END OF VARIABLES --- //

	public static void connect() {

		swingWorker = new SwingWorker<Object, String>() {
			protected Object doInBackground() throws Exception {
				try {
					// System.out.println("Button Pressed, autoConnecting: " + autoConnecting);
					psxhost = txt_HostIP.getText();
					psxport = Integer.parseInt(txt_HostPort.getText());

					if (!isConnected) {
						netThread = new PSXControlNetThread(psxhost, psxport);
						netThread.start();
						send("demand=Qs483");
						TimeUnit.MILLISECONDS.sleep(250);
					} else if (netThread != null) {
						netThread.finalJobs();
					}
				} catch (Exception e) {
					if (!autoConnecting) {
						e.printStackTrace();
						System.out.println("ERROR, autoConnecting: " + autoConnecting);
						JOptionPane.showMessageDialog(null, "Please enter a valid IP Address & Port", "ERROR",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				return null;
			}

		};
		swingWorker.execute(); // Run the code to connect to PSX
	}

	// MAIN METHOD

	public static void main(String[] args) throws InstantiationException {

		// CREATE MAIN WINDOW
		Options.setFields();
		JPanel panel = new JPanel();
		panel.setBounds(new Rectangle(0, 0, 100, 100));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

		// Creating the main application frame
		frmPsxControl = new JFrame("Precision Simulator CDU Controller 1.0");
		frmPsxControl.setResizable(false);
		frmPsxControl.setMinimumSize(new Dimension(250, 250));
		frmPsxControl.setMaximumSize(new Dimension(350, 350));
		frmPsxControl.setName("PSXControl");
		frmPsxControl.setSize(new Dimension(250, 250));
		frmPsxControl.setIconImage(
				Toolkit.getDefaultToolkit().getImage(PSXControl.class.getResource("/resources/mainApplication.png")));
		frmPsxControl.setPreferredSize(new Dimension(250, 250));
		frmPsxControl.getContentPane().setEnabled(false);
		frmPsxControl.getContentPane().setBounds(0, 0, 200, 200);
		frmPsxControl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		URL url = Thread.currentThread().getContextClassLoader().getResource("resources/mainApplication.png");
		if (url != null) {
			frmPsxControl.setIconImage(Toolkit.getDefaultToolkit().createImage(url));
		}

		frmPsxControl.setTitle("PSXControl");
		panel.setLayout(null);
		panel.add(btnConnect);

		JLabel lblHostIp = new JLabel("Host IP:");
		lblHostIp.setBounds(10, 0, 60, 25);
		panel.add(lblHostIp);

		txt_HostIP = new JTextField();
		txt_HostIP.setBounds(76, 0, 138, 25);
		txt_HostIP.setText(psxhost);
		panel.add(txt_HostIP);
		txt_HostIP.setColumns(10);

		JLabel lblHostPort = new JLabel("Host Port:");
		lblHostPort.setBounds(10, 31, 60, 25);
		panel.add(lblHostPort);
		txt_HostPort = new JTextField();
		txt_HostPort.setBounds(76, 31, 138, 25);

		panel.add(txt_HostPort);
		txt_HostPort.setColumns(10);

		lab_Conn.setBounds(10, 120, 204, 27);
		panel.add(lab_Conn);
		lab_Conn.setHorizontalAlignment(SwingConstants.CENTER);
		lab_Conn.setForeground(Color.RED);
		lab_Conn.setFont(new Font("Tahoma", Font.BOLD, 18));

		btnConnect.setBounds(10, 70, 204, 30);
		btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 15));

		// PRESS CONNECT BUTTON
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (autoConnecting) {
					autoConnecting = false;
					connectingUI(1);
				} else {
					if (!isConnected) {
						PSXControl.connectingUI(2); // Set UI for when connecting to PSX
					}
					connect();
				}

			}
		});
		// END OF PRESSING THE CONNECT BUTTON

		frmPsxControl.pack();

		// Menus Bar
		JMenuBar menuBar = new JMenuBar();
		frmPsxControl.setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		menuOptions = new JMenuItem("Options");

		url = Thread.currentThread().getContextClassLoader().getResource("resources/settings.png");
		if (url != null) {
			Image img = Toolkit.getDefaultToolkit().createImage(url);
			menuOptions.setIcon(new ImageIcon(img));
		}

		// menuOptions.setIcon(new
		// ImageIcon(PSXControl.class.getResource("/resources/setting.png")));
		menuFile.add(menuOptions);

		JMenuItem menuExit = new JMenuItem("Exit");
		url = Thread.currentThread().getContextClassLoader().getResource("resources/power.png");
		if (url != null) {
			Image img = Toolkit.getDefaultToolkit().createImage(url);
			menuExit.setIcon(new ImageIcon(img));
		}

		// menuExit.setIcon(new
		// ImageIcon(PSXControl.class.getResource("/resources/power.png")));
		menuFile.add(menuExit);

		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		JMenuItem menuAbout = new JMenuItem("About");
		url = Thread.currentThread().getContextClassLoader().getResource("resources/about.png");
		if (url != null) {
			Image img = Toolkit.getDefaultToolkit().createImage(url);
			menuAbout.setIcon(new ImageIcon(img));
		}
		// menuAbout.setIcon(new
		// ImageIcon(PSXControl.class.getResource("/resources/about.png")));

		menuHelp.add(menuAbout);

		// LOADING PROPERTIES- if no file, loads default values
		try {
			PSXControlConfig.loadProperties();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "The config.cfg file does not exist, default properties loaded.");
		}

		// Sets values based on loaded properties
		psxhost = configProps.getProperty("psxhost");
		psxport = Integer.parseInt(configProps.getProperty("psxport"));
		autoConnect = Boolean.parseBoolean(configProps.getProperty("psxAutoConnect"));
		autoMinimise = Boolean.parseBoolean(configProps.getProperty("autoMinimise"));
		autoReconnect = Boolean.parseBoolean(configProps.getProperty("autoReconnect"));
		PSXControlConfig.setCduPosition(configProps.getProperty("cduPosition"));
		txt_HostIP.setText(configProps.getProperty("psxhost"));
		txt_HostPort.setText(configProps.getProperty("psxport"));
		Options.cbxAutoConnect.setSelected(Boolean.parseBoolean(configProps.getProperty("psxAutoConnect")));
		Options.cbxMinimise.setSelected(Boolean.parseBoolean(configProps.getProperty("autoMinimise")));
		Options.cbxAutoReconnect.setSelected(Boolean.parseBoolean(configProps.getProperty("autoReconnect")));
		Options.cbxSide.setSelectedIndex(PSXControlConfig.getCduSide(configProps.getProperty("cduPosition")));
		// System.out.println("Row: " +
		// Integer.parseInt(configProps.getProperty("cduPosition").substring(1))%10);
		Options.cbxRow.setSelectedIndex(Integer.parseInt(configProps.getProperty("cduPosition").substring(1)) % 10 - 1);

		// Screen width
		xPosition = Integer.parseInt(configProps.getProperty("xPosition"));
		yPosition = Integer.parseInt(configProps.getProperty("yPosition"));

		// Checks if the saved dimensions are bigger than the main window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (xPosition < 0 || xPosition >= screenSize.getWidth() || yPosition < 0
				|| yPosition > screenSize.getHeight()) {
			xPosition = defaultXPosition;
			yPosition = defaultYPosition;
		}

		frmPsxControl.setLocation(xPosition, yPosition);
		// END OF LOADING PROPERTIES

		GroupLayout groupLayout = new GroupLayout(frmPsxControl.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE).addContainerGap()));
		frmPsxControl.getContentPane().setLayout(groupLayout);

		// MENU BUTTON ACTIONS
		menuOptions.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					protected Void doInBackground() throws Exception {
						if (isConnected) {
							Options.cbxSide.setEnabled(false);
							Options.cbxRow.setEnabled(false);
						} else {
							Options.cbxSide.setEnabled(true);
							Options.cbxRow.setEnabled(true);
						}

						options.setVisible(true);
						return null;
					}
				};

				worker.execute();
			}
		});
		menuExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				systemExit();
			}
		});
		menuAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				about.setVisible(true);
			}
		});
		// END OF MENU BUTTON ACTIONS

		// For when the application is closed
		frmPsxControl.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				systemExit();
			}
		});

		// Now the window is ready to be viewed
		frmPsxControl.setVisible(true);

		if (autoConnect) {
			// btnConnect.doClick();

			autoConnecting = true;
			PSXControl.txt_HostIP.setEnabled(false);
			PSXControl.txt_HostPort.setEnabled(false);
			PSXControl.lab_Conn.setText("CONNECTING");
			PSXControl.setBtnConnectText("Connecting");
			PSXControl.lab_Conn.setForeground(Color.decode("#FF9900"));

			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean isFirstTry = true;
			while (!isConnected & autoConnecting) {
				// System.out.println("First try connecting? " + isFirstTry);
				if (autoConnecting) {
					if (isFirstTry) {
						connect();
						isFirstTry = false;
					} else {
						netThread.run();
						if (isConnected)
							isFirstTry = true;
					}
				}

				try {
					TimeUnit.MILLISECONDS.sleep(2000);
				} catch (InterruptedException e) {
				}

			}

		}

		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (isConnected & autoMinimise)
			frmPsxControl.setState(Frame.ICONIFIED);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (netThread != null)
					netThread.finalJobs();

			}
		});

	}

	static void setBtnConnectText(String text) {
		btnConnect.setText(text);
	}

	// THINGS THAT NEED TO HAPPEN WHEN CONNECTING/DISCONNECTING FROM PSX
	static void connectingUI(int i) {
		// When isConnected to PSX

		if (i == 3) {
			lab_Conn.setText("CONNECTED");
			lab_Conn.setForeground(Color.decode("#00AA00"));
			setBtnConnectText("Disconnect");
			isConnected = true;
			autoConnecting = false;
			try {
				TimeUnit.MILLISECONDS.sleep(777);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (autoMinimise)
				frmPsxControl.setState(Frame.ICONIFIED);

			if (CDUPosition < 20) {
				send("cduC=" + CDUPosition + "<PSXCTRL");
			} else if (CDUPosition >= 20) {
				send("cduC=" + CDUPosition + "PSXCTRL>");
			}
		}
		// When connectING to PSX
		else if (i == 2) {
			isConnected = false;
			PSXControlNetThread.activeMenu = 0;
			lab_Conn.setText("CONNECTING");
			lab_Conn.setForeground(Color.decode("#FF9900"));
			txt_HostIP.setEnabled(false);
			txt_HostPort.setEnabled(false);
		}
		// When disconnected from PSX
		else {
			isConnected = false;
			PSXControlNetThread.activeMenu = 0;
			PSXControl.active = false;
			if (autoConnecting) {
				lab_Conn.setText("CONNECTING");
				setBtnConnectText("Connecting");
				lab_Conn.setForeground(Color.decode("#FF9900"));
				txt_HostIP.setEnabled(false);
				txt_HostPort.setEnabled(false);
			} else {
				lab_Conn.setText("DISCONNECTED");
				setBtnConnectText("Connect");
				lab_Conn.setForeground(Color.red);
				txt_HostIP.setEnabled(true);
				txt_HostPort.setEnabled(true);
			}
		}
	}

	synchronized static void send(String s) {
		if (netThread != null && s != null)
			// System.out.println(s);
			netThread.send(s);
	}

	static void setScratchPad(int input) {

	}

	static void systemExit() {
		if (PSXControl.psx != null) {
			String closeCduActSys = "Qs406=" + PSXControl.psx.qsVariables[406].substring(0, 4) + "-1";
			send(closeCduActSys);
		}
		PSXControlConfig.saveProperties();
		if (netThread != null) {
			if (isConnected) {
				while (netThread.isAlive()) {
					netThread.finalJobs();
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} else {
				netThread = null;
				PSXControl.connectingUI(1);
				PSXControlConfig.saveProperties();
			}
		}
		System.exit(0);
	}

	static String arrayToString(String[] array) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (String particle : array) {
			if (!first)
				sb.append(';');

			sb.append(particle);
			first = false;
		}

		return sb.toString();
	}

	static void debugMessage(String message) {
		Date timestamp = new Date(System.currentTimeMillis());
		System.out.println(PSXControl.time.format(timestamp) + "\t" + message);
	}
}