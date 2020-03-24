package overseer.rcon;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class RconSession {
	public static void main(String[] args) {
		RconSession session = new RconSession("", 0, "");

		try {
			session.connect();
			session.execute("saveworld");
			session.execute("doexit");
			session.disconnect();
		} catch (StateTransitionException e) {
			e.printStackTrace();
		}
	}

	private String ip;
	private int port;
	private String password;

	private RconSessionState _sessionState;
	private int _sessionId;

	private Random sessionRandom;
	private Socket sessionSocket = null;

	public RconSession(String ip, int port, String password) {
		this.ip = ip;
		this.port = port;
		this.password = password;
		this.sessionRandom = new Random();
		this._sessionState = RconSessionState.DISCONNECTED;
	}

	public void connect() throws StateTransitionException {
		transitionState(RconSessionState.CONNECTING);

		try {
			// Open TCP socket
			sessionSocket = new Socket(ip, port);
			_sessionId = sessionRandom.nextInt();

			// Authenticate with server
			transitionState(RconSessionState.AUTHENTICATING);

			RconPacket authRequest = new RconPacket(_sessionId, RconPacket.TYPE_REQUEST_AUTH, password);
			RconPacket.send(authRequest, sessionSocket.getOutputStream());

			RconPacket authResponse = RconPacket.receive(sessionSocket.getInputStream());

			if (authResponse.getType() != RconPacket.TYPE_RESPONSE_AUTH || authResponse.getID() != _sessionId)
				throw new AuthenticationException(ip, port);

			// Success, session is connected
			transitionState(RconSessionState.CONNECTED);
		} catch (IOException | AuthenticationException e) {
			disconnect();
			e.printStackTrace();
		} catch (StateTransitionException e) {
			throw e;
		}
	}

	public void execute(String command) throws StateTransitionException {
		transitionState(RconSessionState.EXECUTING);

		try {
			// Generate a unique id for this transaction
			int _txid = sessionRandom.nextInt();

			// Send an RCON exec request
			RconPacket execRequest = new RconPacket(_txid, RconPacket.TYPE_REQUEST_EXECCOMMAND, command);
			RconPacket.send(execRequest, sessionSocket.getOutputStream());

			// Await an RCON value response
			RconPacket execResponse = RconPacket.receive(sessionSocket.getInputStream());

			// Verify response is correct
			if (execResponse.getType() != RconPacket.TYPE_RESPONSE_VALUE || execResponse.getID() != _txid)
				throw new RconPacketException(String.format("Bad RCON response for command: %s", command));

			// Display response text
			System.out.print(execResponse.getBody());

			// Success, execution completed
			transitionState(RconSessionState.CONNECTED);
		} catch (IOException | RconPacketException e) {
			e.printStackTrace();
		} catch (StateTransitionException e) {
			throw e;
		}
	}

	public void disconnect() throws StateTransitionException {
		transitionState(RconSessionState.DISCONNECTED);

		try {
			if (sessionSocket != null && !sessionSocket.isClosed())
				sessionSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sessionSocket = null;
		}
	}

	private void transitionState(RconSessionState nextState) throws StateTransitionException {
		boolean valid = false;

		switch (_sessionState) {
		case DISCONNECTED:
			valid = (nextState == RconSessionState.CONNECTING);
			break;
		case CONNECTING:
			valid = (nextState == RconSessionState.AUTHENTICATING || nextState == RconSessionState.DISCONNECTED);
			break;
		case AUTHENTICATING:
			valid = (nextState == RconSessionState.CONNECTED || nextState == RconSessionState.DISCONNECTED);
			break;
		case CONNECTED:
			valid = (nextState == RconSessionState.EXECUTING || nextState == RconSessionState.DISCONNECTED);
			break;
		case EXECUTING:
			valid = (nextState == RconSessionState.CONNECTED || nextState == RconSessionState.DISCONNECTED);
			break;
		}

		if (!valid)
			throw new StateTransitionException(_sessionState, nextState);
		else
			_sessionState = nextState;
	}
}
