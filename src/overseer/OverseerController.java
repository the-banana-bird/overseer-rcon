package overseer;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import overseer.rcon.RconSession;
import overseer.ui.OverseerFrame;
import overseer.view.OverseerServerViewModel;
import overseer.view.OverseerViewModel;

public class OverseerController {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Get main controller instance started
		OverseerController controller = new OverseerController();

		// Prepare view model for main window
		OverseerViewModel viewModel = controller.viewOverseer();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OverseerFrame frame = new OverseerFrame(viewModel);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public OverseerViewModel viewOverseer() {
		List<OverseerServerViewModel> servers = new ArrayList<OverseerServerViewModel>();

		return new OverseerViewModel("Overseer RCON", "1.0.0", servers);
	}

	public OverseerServerViewModel viewMockServer(String serverName, String ip, int port, String password) {
		return new OverseerServerViewModel(serverName, new RconSession(ip, port, password));
	}

}
