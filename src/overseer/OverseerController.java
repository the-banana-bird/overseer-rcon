package overseer;

import java.util.ArrayList;
import java.util.List;

import overseer.view.OverseerServerViewModel;
import overseer.view.OverseerViewModel;

public class OverseerController {

	public OverseerViewModel viewOverseer() {
		List<OverseerServerViewModel> servers = new ArrayList<OverseerServerViewModel>();

		for (int i = 0; i < 5; i++) {
			servers.add(viewMockServer());
		}

		return new OverseerViewModel("Overseer RCON", "1.0.0", servers);
	}

	public OverseerServerViewModel viewMockServer() {
		return new OverseerServerViewModel("Mock Server", "192.168.0.0", 27020, "password", null);
	}

}
