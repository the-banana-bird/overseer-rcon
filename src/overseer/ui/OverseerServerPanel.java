package overseer.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class OverseerServerPanel extends JPanel {
	private JTextField txtStatus;
	private JTextField txtCommand;

	/**
	 * Create the panel.
	 */
	public OverseerServerPanel() {
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Test", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		JToolBar toolBar = new JToolBar();
		springLayout.putConstraint(SpringLayout.NORTH, toolBar, 2, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, toolBar, 2, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, toolBar, 2, SpringLayout.EAST, this);
		toolBar.setFloatable(false);
		add(toolBar);

		JButton btnConnectDisconnect = new JButton("Disconnect");
		btnConnectDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		toolBar.add(btnConnectDisconnect);

		JSeparator separator1 = new JSeparator();
		separator1.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator1);

		JButton btnSave = new JButton("Save World");
		btnSave.setToolTipText("Execute \"SaveWorld\"");
		toolBar.add(btnSave);

		JButton btnSaveExit = new JButton("Save World & Exit");
		btnSaveExit.setToolTipText("Execute \"SaveWorld | DoExit\"");
		toolBar.add(btnSaveExit);

		JTextArea txtOutput = new JTextArea();
		springLayout.putConstraint(SpringLayout.WEST, txtOutput, 2, SpringLayout.WEST, this);
		txtOutput.setText("RCON");
		txtOutput.setBackground(UIManager.getColor("TextArea.foreground"));
		txtOutput.setForeground(UIManager.getColor("TextArea.background"));
		txtOutput.setEditable(false);
		springLayout.putConstraint(SpringLayout.NORTH, txtOutput, 2, SpringLayout.SOUTH, toolBar);

		JSeparator separator2 = new JSeparator();
		separator2.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator2);

		txtStatus = new JTextField();
		txtStatus.setEditable(false);
		txtStatus.setText("STATUS: AUTHENTICATING");
		txtStatus.setForeground(Color.YELLOW);
		txtStatus.setBackground(UIManager.getColor("TextField.foreground"));
		toolBar.add(txtStatus);
		txtStatus.setColumns(10);

		JButton btnEdit = new JButton("\uD83D\uDEE0");
		btnEdit.setToolTipText("Edit server config");
		btnEdit.setForeground(new Color(0, 0, 255));
		toolBar.add(btnEdit);

		JButton btnDelete = new JButton("\u274C");
		btnDelete.setToolTipText("Delete server config");
		btnDelete.setForeground(new Color(128, 0, 0));
		toolBar.add(btnDelete);
		add(txtOutput);

		JScrollPane scrollPanePlayers = new JScrollPane();
		springLayout.putConstraint(SpringLayout.EAST, txtOutput, -2, SpringLayout.WEST, scrollPanePlayers);
		scrollPanePlayers.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPanePlayers, 2, SpringLayout.SOUTH, toolBar);
		springLayout.putConstraint(SpringLayout.WEST, scrollPanePlayers, -162, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPanePlayers, 2, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, scrollPanePlayers, 2, SpringLayout.EAST, this);
		add(scrollPanePlayers);

		JTextArea txtPlayers = new JTextArea();
		txtPlayers.setText("Players Online:");
		txtPlayers.setEditable(false);
		scrollPanePlayers.setViewportView(txtPlayers);

		txtCommand = new JTextField();
		txtCommand.setForeground(UIManager.getColor("TextField.background"));
		txtCommand.setBackground(UIManager.getColor("TextField.foreground"));
		springLayout.putConstraint(SpringLayout.NORTH, txtCommand, 2, SpringLayout.SOUTH, txtOutput);
		springLayout.putConstraint(SpringLayout.WEST, txtCommand, 2, SpringLayout.WEST, this);
		txtCommand.setToolTipText("Enter commands here");
		txtCommand.setText("ListPlayers");
		springLayout.putConstraint(SpringLayout.SOUTH, txtCommand, 2, SpringLayout.SOUTH, this);
		add(txtCommand);
		txtCommand.setColumns(10);

		JButton btnExecute = new JButton("Execute");
		springLayout.putConstraint(SpringLayout.EAST, txtCommand, -2, SpringLayout.WEST, btnExecute);
		springLayout.putConstraint(SpringLayout.SOUTH, btnExecute, 2, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnExecute, -2, SpringLayout.WEST, scrollPanePlayers);
		springLayout.putConstraint(SpringLayout.SOUTH, txtOutput, -2, SpringLayout.NORTH, btnExecute);
		btnExecute.setToolTipText("Send command to server");
		add(btnExecute);
	}
}
