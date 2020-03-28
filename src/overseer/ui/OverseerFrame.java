package overseer.ui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import overseer.view.OverseerServerViewModel;
import overseer.view.OverseerViewModel;

public class OverseerFrame extends JFrame {

	private OverseerViewModel viewModel;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public OverseerFrame(OverseerViewModel viewModel) {
		this.viewModel = viewModel;

		setMinimumSize(new Dimension(600, 1000));
		setTitle(String.format("%s - %s", this.viewModel.title, this.viewModel.version));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));

		for (OverseerServerViewModel serverViewModel : this.viewModel.servers) {
			OverseerServerPanel overseerServerPanel = new OverseerServerPanel(serverViewModel);
			contentPane.add(overseerServerPanel);
		}

	}
}
