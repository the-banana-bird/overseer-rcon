package overseer.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import overseer.rcon.RconSession;
import overseer.rcon.RconSessionState;
import overseer.rcon.StateTransitionException;
import overseer.view.OverseerServerViewModel;

public class OverseerServerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private OverseerServerViewModel viewModel;
	private JButton btnConnectDisconnect;
	private JButton btnSave;
	private JButton btnSaveExit;
	private JButton btnExecute;
	private JTextField txtStatus;
	private JTextField txtCommand;
	private JTextArea txtOutput;
	private JTextArea txtPlayers;
	private final Action actionConnectDisconnect = new SwingActionConnectDisconnect();
	private final Action actionSave = new SwingActionSave();
	private final Action actionSaveExit = new SwingActionSaveExit();
	private final Action actionEdit = new SwingActionEdit();
	private final Action actionDelete = new SwingActionDelete();
	private final Action actionExecute = new SwingActionExecute();
	private final ChangeListener sessionStateChangeListener = new SessionStateChangeListener();
	private final ChangeListener sessionOutputChangeListener = new SessionOutputChangeListener();

	/**
	 * Create the panel.
	 */
	public OverseerServerPanel(OverseerServerViewModel viewModel) {
		this.viewModel = viewModel;

		this.viewModel.session.addStateChangeListener(sessionStateChangeListener);
		this.viewModel.session.addOutputChangeListener(sessionOutputChangeListener);

		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), this.viewModel.name,
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		JToolBar toolBar = new JToolBar();
		springLayout.putConstraint(SpringLayout.NORTH, toolBar, 2, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, toolBar, 2, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, toolBar, 2, SpringLayout.EAST, this);
		toolBar.setFloatable(false);
		add(toolBar);

		btnConnectDisconnect = new JButton("");
		btnConnectDisconnect.setToolTipText("");
		btnConnectDisconnect.setAction(actionConnectDisconnect);
		toolBar.add(btnConnectDisconnect);

		JSeparator separator1 = new JSeparator();
		separator1.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator1);

		btnSave = new JButton("");
		btnSave.setAction(actionSave);
		btnSave.setEnabled(false);
		btnSave.setToolTipText("");
		toolBar.add(btnSave);

		btnSaveExit = new JButton("");
		btnSaveExit.setAction(actionSaveExit);
		btnSaveExit.setEnabled(false);
		btnSaveExit.setToolTipText("");
		toolBar.add(btnSaveExit);

		txtOutput = new JTextArea();
		springLayout.putConstraint(SpringLayout.WEST, txtOutput, 2, SpringLayout.WEST, this);
		txtOutput.setBackground(UIManager.getColor("TextArea.foreground"));
		txtOutput.setForeground(UIManager.getColor("TextArea.background"));
		txtOutput.setEditable(false);
		springLayout.putConstraint(SpringLayout.NORTH, txtOutput, 2, SpringLayout.SOUTH, toolBar);

		JSeparator separator2 = new JSeparator();
		separator2.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator2);

		txtStatus = new JTextField();
		txtStatus.setEditable(false);
		txtStatus.setText("STATUS: DISCONNECTED");
		txtStatus.setForeground(Color.RED);
		txtStatus.setBackground(UIManager.getColor("TextField.foreground"));
		toolBar.add(txtStatus);
		txtStatus.setColumns(10);

		JButton btnEdit = new JButton("");
		btnEdit.setAction(actionEdit);
		btnEdit.setToolTipText("");
		btnEdit.setForeground(new Color(0, 0, 255));
		toolBar.add(btnEdit);

		JButton btnDelete = new JButton("");
		btnDelete.setAction(actionDelete);
		btnDelete.setToolTipText("");
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

		txtPlayers = new JTextArea();
		txtPlayers.setText("Players Online:");
		txtPlayers.setEditable(false);
		scrollPanePlayers.setViewportView(txtPlayers);

		txtCommand = new JTextField();
		txtCommand.setForeground(UIManager.getColor("TextField.background"));
		txtCommand.setBackground(UIManager.getColor("TextField.foreground"));
		springLayout.putConstraint(SpringLayout.NORTH, txtCommand, 2, SpringLayout.SOUTH, txtOutput);
		springLayout.putConstraint(SpringLayout.WEST, txtCommand, 2, SpringLayout.WEST, this);
		txtCommand.setToolTipText("Enter commands here");
		springLayout.putConstraint(SpringLayout.SOUTH, txtCommand, 2, SpringLayout.SOUTH, this);
		add(txtCommand);
		txtCommand.setColumns(10);

		btnExecute = new JButton("");
		btnExecute.setAction(actionExecute);
		btnExecute.setEnabled(false);
		springLayout.putConstraint(SpringLayout.EAST, txtCommand, -2, SpringLayout.WEST, btnExecute);
		springLayout.putConstraint(SpringLayout.SOUTH, btnExecute, 2, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnExecute, -2, SpringLayout.WEST, scrollPanePlayers);
		springLayout.putConstraint(SpringLayout.SOUTH, txtOutput, -2, SpringLayout.NORTH, btnExecute);
		btnExecute.setToolTipText("");
		add(btnExecute);
	}

	private class SwingActionConnectDisconnect extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionConnectDisconnect() {
			putValue(NAME, "Connnect");
			putValue(SHORT_DESCRIPTION, "Connect/Disconnect to Server");
		}

		public void actionPerformed(ActionEvent e) {
			RconSession session = viewModel.session;

			try {
				if (session.getSessionState() == RconSessionState.DISCONNECTED)
					session.connect();
				else
					session.disconnect();
			} catch (StateTransitionException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class SwingActionSave extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionSave() {
			putValue(NAME, "Save World");
			putValue(SHORT_DESCRIPTION, "Execute \"SaveWorld\"");
		}

		public void actionPerformed(ActionEvent e) {
			RconSession session = viewModel.session;
			try {
				// Execute command in RCON session
				session.execute("SaveWorld");
			} catch (StateTransitionException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class SwingActionSaveExit extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionSaveExit() {
			putValue(NAME, "Save World & Exit");
			putValue(SHORT_DESCRIPTION, "Execute \"SaveWorld | DoExit\"");
		}

		public void actionPerformed(ActionEvent e) {
			RconSession session = viewModel.session;
			try {
				// Execute command in RCON session
				session.execute("SaveWorld | DoExit");
			} catch (StateTransitionException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class SwingActionEdit extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionEdit() {
			putValue(NAME, "\uD83D\uDEE0");
			putValue(SHORT_DESCRIPTION, "Edit server config");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private class SwingActionDelete extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionDelete() {
			putValue(NAME, "\u274C");
			putValue(SHORT_DESCRIPTION, "Delete server config");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private class SwingActionExecute extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingActionExecute() {
			putValue(NAME, "Execute");
			putValue(SHORT_DESCRIPTION, "Send command to server");
		}

		public void actionPerformed(ActionEvent e) {
			// Do not execute if command is empty
			if (txtCommand.getText().isEmpty())
				return;

			RconSession session = viewModel.session;
			try {
				// Consume command string from command text box
				String command = txtCommand.getText();
				txtCommand.setText(new String());

				// Execute command in RCON session
				session.execute(command);
			} catch (StateTransitionException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class SessionStateChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			// Collect session state details relevant to the UI
			RconSession session = viewModel.session;

			boolean canExecute;
			boolean canConnect;
			Color statusColor;

			switch (session.getSessionState()) {
			default:
			case DISCONNECTED:
				canExecute = false;
				canConnect = true;
				statusColor = Color.RED;
				break;
			case CONNECTING:
				canExecute = false;
				canConnect = false;
				statusColor = Color.ORANGE;
				break;
			case AUTHENTICATING:
				canExecute = false;
				canConnect = false;
				statusColor = Color.YELLOW;
				break;
			case CONNECTED:
				canExecute = true;
				canConnect = false;
				statusColor = Color.GREEN;
				break;
			case EXECUTING:
				canExecute = false;
				canConnect = false;
				statusColor = Color.BLUE;
				break;
			}

			// Apply UI changes based on details
			btnSave.setEnabled(canExecute);
			btnSaveExit.setEnabled(canExecute);
			btnExecute.setEnabled(canExecute);

			btnConnectDisconnect.setText(canConnect ? "Connect" : "Disconnect");

			txtStatus.setForeground(statusColor);
			txtStatus.setText(String.format("STATUS: %s", session.getSessionState().name()));
		}
	}

	private class SessionOutputChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			RconSession session = viewModel.session;
			txtOutput.setText(session.getOutput());
		}
	}
}
