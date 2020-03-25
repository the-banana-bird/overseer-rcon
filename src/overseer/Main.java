package overseer;

import overseer.ui.OverseerFrame;
import overseer.view.OverseerViewModel;

public class Main {
	private static OverseerController controller;

	public static void main(String[] args) {
		// Create the controller instance
		controller = new OverseerController();

		// Get main view model from controller
		OverseerViewModel viewModel = controller.viewOverseer();

		// Create the UI on a separate thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create overseer frame and display it
				OverseerFrame frame = new OverseerFrame(viewModel);
				frame.setVisible(true);
			}
		});
	}
}
