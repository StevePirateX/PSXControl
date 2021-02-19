import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class About extends JDialog {

	private static final long serialVersionUID = -1294159563661850183L;
	private final JPanel contentPanel = new JPanel();
	
	Date today = Calendar.getInstance().getTime();
	String formattedDate = new SimpleDateFormat("dd/MMM/yyyy").format(today);
	

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// SwingUtilities.updateComponentTreeUI(contentPanel);

		try {
			About dialog = new About();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public About() {
		URL url = Thread.currentThread().getContextClassLoader().getResource("resources/about.png");
		if (url != null) {
			Image img = Toolkit.getDefaultToolkit().createImage(url);
			setIconImage(img);
		}
		//setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/resources/about.png")));
		setResizable(false);
		setModal(true);
		// setIconImage(Toolkit.getDefaultToolkit().getImage(About.class.getResource("/windowBuilder/resources/095_chart.png")));
		setTitle(" About");
		setAlwaysOnTop(true);
		setBounds(100, 100, 548, 479);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(contentPanel,
				GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(contentPanel, GroupLayout.PREFERRED_SIZE, 385, Short.MAX_VALUE)
						.addContainerGap()));

		JEditorPane dtrpnpsxinfoStevenBrown = new JEditorPane();
		dtrpnpsxinfoStevenBrown.setBackground(getBackground());
		dtrpnpsxinfoStevenBrown.setHighlighter(null);
		dtrpnpsxinfoStevenBrown.setEditable(false);
		dtrpnpsxinfoStevenBrown.setContentType("text/html");
		
		dtrpnpsxinfoStevenBrown.setText("<center><h1>PSXControl v1.3</h1>\r\n" + formattedDate + "<br>\r\nSteven Brown</br></center>\r\n<br>\r\n<p>You can now control PSX all controlled from the centre CDU without having to get out of your seat and go to the instructor station. It has now been implemented with the colour LCD screens. What a dream! When connected, a new menu will appear on the centre CDU so you can interact with the simulator without leaving the pilot seat. </p>\r\n<p>You can freeze the motion, set the time to land during a beautiful sunset, reset the situation, service the aircraft by clearing malfunctions and resetting the circuit breakers and fly in VMC/IMC or quickly load a 20kt crosswind or set the winds you desire. PSXControl also has it's own independant event record as well as a persistent ground proximity override. (It won't change when you reload)</p>\r\n<p>\r\n\r\nEnjoy! :)");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup().addContainerGap()
						.addComponent(dtrpnpsxinfoStevenBrown, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
						.addGap(13)));
		gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup().addContainerGap()
						.addComponent(dtrpnpsxinfoStevenBrown, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)));
		contentPanel.setLayout(gl_contentPanel);
		getContentPane().setLayout(groupLayout);
	}
}
