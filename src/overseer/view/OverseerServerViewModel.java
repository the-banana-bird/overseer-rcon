package overseer.view;

import overseer.rcon.RconSession;

public class OverseerServerViewModel {
	public final String name;
	public final RconSession session;

	public OverseerServerViewModel(String name, RconSession session) {
		this.name = name;
		this.session = session;
	}
}
