package overseer.view;

import java.util.List;

public class OverseerViewModel {
	public final String title;
	public final String version;
	public final List<OverseerServerViewModel> servers;

	public OverseerViewModel(String title, String version, List<OverseerServerViewModel> servers) {
		this.title = title;
		this.version = version;
		this.servers = servers;
	}
}
