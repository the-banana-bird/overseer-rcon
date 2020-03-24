package overseer.rcon;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class RconSession {
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

	public String execute(String command) throws StateTransitionException {
		transitionState(RconSessionState.EXECUTING);

		StringBuilder responseBuilder = new StringBuilder();

		try {
			// Generate a unique id for this transaction
			int _txid = sessionRandom.nextInt();
			int _tailid = sessionRandom.nextInt();

			// Send an RCON exec request
			RconPacket execRequest = new RconPacket(_txid, RconPacket.TYPE_REQUEST_EXECCOMMAND, command);
			RconPacket.send(execRequest, sessionSocket.getOutputStream());

			// Send an RCON exec tail
			// The exec response can be multiple packets. The tail response is delivered
			// last, marking the "tail".
			RconPacket execTail = new RconPacket(_tailid, RconPacket.TYPE_RESPONSE_VALUE, "");
			RconPacket.send(execTail, sessionSocket.getOutputStream());

			// Await response packets
			boolean tailRead = false;
			do {
				RconPacket execResponse = RconPacket.receive(sessionSocket.getInputStream());

				// Verify response is either command output or tail
				if (execResponse.getType() != RconPacket.TYPE_RESPONSE_VALUE
						|| (execResponse.getID() != _txid && execResponse.getID() != _tailid))
					throw new RconPacketException(String.format("Bad RCON response for command: %s", command));

				if (execResponse.getID() == _tailid) {
					// TailID means the original response was exhausted, so we stop reading
					// responses.
					tailRead = true;
				} else {
					// TXID response gets appended to the overall response.
					responseBuilder.append(execResponse.getBody());
				}
			} while (!tailRead);

			// Success, execution completed
			transitionState(RconSessionState.CONNECTED);
		} catch (IOException | RconPacketException e) {
			e.printStackTrace();
		} catch (StateTransitionException e) {
			throw e;
		}

		return responseBuilder.toString();
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
		if (!RconSessionState.isTransitionValid(_sessionState, nextState))
			throw new StateTransitionException(_sessionState, nextState);
		else
			_sessionState = nextState;
	}
}
