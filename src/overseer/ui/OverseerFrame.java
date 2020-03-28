package overseer.ui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import overseer.OverseerController;

public class OverseerFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static OverseerController controller;

	public static void main(String[] args) {
		controller = new OverseerController();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OverseerFrame frame = new OverseerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public OverseerFrame() {
		setMinimumSize(new Dimension(600, 250));
		setTitle("OverseerRCON - 1.0.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		OverseerServerPanel overseerServerPanel = new OverseerServerPanel(controller.viewMockServer());
		sl_contentPane.putConstraint(SpringLayout.NORTH, overseerServerPanel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, overseerServerPanel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, overseerServerPanel, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, overseerServerPanel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(overseerServerPanel);

	}
}
