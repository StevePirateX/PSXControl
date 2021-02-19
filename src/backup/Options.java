package backup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;

public class Options extends JDialog {

	
	private static final long serialVersionUID = -8578973490647968311L;
	static protected final JPanel contentPanel = new JPanel();
	
	protected static JCheckBox cbxAutoConnect;
	protected static JCheckBox cbxMinimise;
	protected static JCheckBox cbxAutoReconnect;
	protected static JComboBox<String> cbxSide;
	protected static JComboBox<String> cbxRow;
	
	private static JButton okButton;
	private static JPanel buttonPane;
	private static JButton cancelButton;
	

	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			Options dialog = new Options();
			dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			dialog.setVisible(true);
			dialog.getRootPane().setDefaultButton(okButton);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Options() {
		getContentPane().setPreferredSize(new Dimension(200, 200));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				setFields();
				dispose();
			}
		});
		//setType(Type.POPUP);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Throwable e) {
			e.printStackTrace();
		}
		setResizable(false);
		setModal(true);
		setAlwaysOnTop(true);
		URL url = Thread.currentThread().getContextClassLoader().getResource("resources/settings.png");
		if (url != null) {
			Image img = Toolkit.getDefaultToolkit().createImage(url);
			setIconImage(img);
		}
		setTitle("Options");
		setBounds(100, 100, 241, 265);
		getContentPane().setLayout(null);
		contentPanel.setBounds(10, 11, 219, 213);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		
		contentPanel.setLayout(null);
		{
			TitledBorder border = new TitledBorder("FTP Settings");
			border.setTitleJustification(TitledBorder.LEFT);
			border.setTitlePosition(TitledBorder.TOP);
		}
		{
			TitledBorder borderFileSettings = new TitledBorder("File Settings");
			borderFileSettings.setTitleJustification(TitledBorder.LEFT);
			borderFileSettings.setTitlePosition(TitledBorder.TOP);
			
			JPanel pnlGeneralSettings = new JPanel();
			pnlGeneralSettings.setBorder(new TitledBorder(null, "General Settings", TitledBorder.LEFT, TitledBorder.TOP, null, null));
			pnlGeneralSettings.setBounds(0, 0, 219, 97);
			contentPanel.add(pnlGeneralSettings);
			GridBagLayout gbl_pnlGeneralSettings = new GridBagLayout();
			gbl_pnlGeneralSettings.columnWidths = new int[] {95};
			gbl_pnlGeneralSettings.rowHeights = new int[] {0, 0, 0};
			gbl_pnlGeneralSettings.columnWeights = new double[]{1.0};
			gbl_pnlGeneralSettings.rowWeights = new double[]{0.0, 0.0, 0.0};
			pnlGeneralSettings.setLayout(gbl_pnlGeneralSettings);
			
			cbxAutoConnect = new JCheckBox("Connect to PSX on start up");
			cbxAutoConnect.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_cbxAutoConnect = new GridBagConstraints();
			gbc_cbxAutoConnect.insets = new Insets(0, 0, 5, 0);
			gbc_cbxAutoConnect.anchor = GridBagConstraints.NORTHWEST;
			gbc_cbxAutoConnect.gridx = 0;
			gbc_cbxAutoConnect.gridy = 0;
			pnlGeneralSettings.add(cbxAutoConnect, gbc_cbxAutoConnect);
			
			cbxMinimise = new JCheckBox("Minimise on connection");
			GridBagConstraints gbc_cbxMinimise = new GridBagConstraints();
			gbc_cbxMinimise.insets = new Insets(0, 0, 5, 0);
			gbc_cbxMinimise.anchor = GridBagConstraints.NORTHWEST;
			gbc_cbxMinimise.gridx = 0;
			gbc_cbxMinimise.gridy = 1;
			pnlGeneralSettings.add(cbxMinimise, gbc_cbxMinimise);
			
			cbxAutoReconnect = new JCheckBox("Auto reconnect on error");
			GridBagConstraints gbc_cbxAutoReconnect = new GridBagConstraints();
			gbc_cbxAutoReconnect.insets = new Insets(0, 0, 5, 0);
			gbc_cbxAutoReconnect.anchor = GridBagConstraints.NORTHWEST;
			gbc_cbxAutoReconnect.gridx = 0;
			gbc_cbxAutoReconnect.gridy = 2;
			pnlGeneralSettings.add(cbxAutoReconnect, gbc_cbxAutoReconnect);
		}
		
		JPanel pnlCduMenuPosition = new JPanel();
		pnlCduMenuPosition.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Centre CDU Menu Position", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlCduMenuPosition.setBounds(0, 108, 219, 74);
		contentPanel.add(pnlCduMenuPosition);
		GridBagLayout gbl_pnlCduMenuPosition = new GridBagLayout();
		gbl_pnlCduMenuPosition.columnWidths = new int[] {40, 60, 0};
		gbl_pnlCduMenuPosition.rowHeights = new int[]{0, 0, 0};
		gbl_pnlCduMenuPosition.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_pnlCduMenuPosition.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		pnlCduMenuPosition.setLayout(gbl_pnlCduMenuPosition);
		
		JLabel lblSide = new JLabel("Side:");
		lblSide.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblSide = new GridBagConstraints();
		gbc_lblSide.anchor = GridBagConstraints.EAST;
		gbc_lblSide.fill = GridBagConstraints.VERTICAL;
		gbc_lblSide.insets = new Insets(0, 0, 5, 5);
		gbc_lblSide.gridx = 0;
		gbc_lblSide.gridy = 0;
		pnlCduMenuPosition.add(lblSide, gbc_lblSide);
		
		cbxSide = new JComboBox<String>();
		cbxSide.setModel(new DefaultComboBoxModel<String>(new String[] {"LEFT", "RIGHT"}));
		GridBagConstraints gbc_cbxSide = new GridBagConstraints();
		gbc_cbxSide.insets = new Insets(0, 0, 5, 0);
		gbc_cbxSide.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbxSide.gridx = 1;
		gbc_cbxSide.gridy = 0;
		pnlCduMenuPosition.add(cbxSide, gbc_cbxSide);
		
		JLabel lblRow = new JLabel("Row:");
		GridBagConstraints gbc_lblRow = new GridBagConstraints();
		gbc_lblRow.anchor = GridBagConstraints.EAST;
		gbc_lblRow.insets = new Insets(0, 0, 0, 5);
		gbc_lblRow.gridx = 0;
		gbc_lblRow.gridy = 1;
		pnlCduMenuPosition.add(lblRow, gbc_lblRow);
		
		cbxRow = new JComboBox<String>();
		cbxRow.setModel(new DefaultComboBoxModel<String>(new String[] {"1", "2", "3", "4", "5", "6"}));
		GridBagConstraints gbc_cbxRow = new GridBagConstraints();
		gbc_cbxRow.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbxRow.gridx = 1;
		gbc_cbxRow.gridy = 1;
		pnlCduMenuPosition.add(cbxRow, gbc_cbxRow);
		
		
		
		
		{
			buttonPane = new JPanel();
			buttonPane.setBounds(0, 180, 219, 33);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {

						
						
						PSXControl.autoConnect = cbxAutoConnect.isSelected();
						PSXControl.autoMinimise = cbxMinimise.isSelected();
						PSXControl.autoReconnect = cbxAutoReconnect.isSelected();
						PSXControlConfig.setCduPosition(cbxSide.getSelectedItem().toString().substring(0,1) + cbxRow.getSelectedItem().toString());
						
						PSXControlConfig.saveProperties();
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						setFields();
						dispose();

					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{getContentPane(), contentPanel}));



		setFields();



	}

	// Set text fields
	public static void setFields() {
		//cbxAutoConnect.setSelected(PSXControl.autoConnect);
		//cbxMinimise.setSelected(PSXControl.autoMinimise);
		
	}
}
