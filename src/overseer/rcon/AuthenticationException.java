package overseer.rcon;

public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 1L;

	public AuthenticationException(String ip, int port) {
		super(String.format("Failed to authenticate with RCON server %s:%d", ip, port));
	}
}
