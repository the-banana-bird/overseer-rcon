package overseer.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import overseer.view.OverseerServerViewModel;
import overseer.view.OverseerViewModel;

public class OverseerFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private OverseerViewModel viewModel;

	public OverseerFrame(OverseerViewModel viewModel) {
		super(String.format("%s - %s", viewModel.title, viewModel.version));
		this.viewModel = viewModel;

		// Prepare frame layout for rows of servers
		BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		setLayout(layout);

		// Generate server panels for each server view
		for (OverseerServerViewModel serverViewModel : viewModel.servers) {
			OverseerServerPanel serverPanel = new OverseerServerPanel(serverViewModel);
			serverPanel.setPreferredSize(new Dimension(1000, 200));
			this.add(serverPanel);
		}

		// Any last JPanel operations go here
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}
}
