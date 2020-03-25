package overseer.view;

import overseer.rcon.RconSession;

public class OverseerServerViewModel {
	public final String name;
	public final String ip;
	public final int port;
	public final String password;
	public final RconSession session;

	public OverseerServerViewModel(String name, String ip, int port, String password, RconSession session) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.password = password;
		this.session = session;
	}
}
