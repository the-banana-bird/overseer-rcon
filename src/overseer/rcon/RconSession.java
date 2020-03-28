package overseer.rcon;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RconSession {
	private String ip;
	private int port;
	private String password;

	private RconSessionState _sessionState;
	private int _sessionId;
	private Random sessionRandom;
	private Socket sessionSocket = null;
	private List<ChangeListener> stateChangeListeners;
	private List<ChangeListener> outputChangeListeners;
	private StringBuilder sessionLog;

	public RconSession(String ip, int port, String password) {
		this.ip = ip;
		this.port = port;
		this.password = password;
		this.sessionRandom = new Random();
		this._sessionState = RconSessionState.DISCONNECTED;
		this.stateChangeListeners = new ArrayList<ChangeListener>();
		this.outputChangeListeners = new ArrayList<ChangeListener>();
		this.sessionLog = new StringBuilder();
	}

	public void connect() throws StateTransitionException {
		transitionState(RconSessionState.CONNECTING);
		logn(String.format("[OverseerRCON] Connecting to %s:%d", ip, port));

		try {
			// Open TCP socket
			sessionSocket = new Socket(ip, port);
			_sessionId = sessionRandom.nextInt();

			// Authenticate with server
			transitionState(RconSessionState.AUTHENTICATING);
			logn("[OverseerRCON] Authenticating");

			RconPacket authRequest = new RconPacket(_sessionId, RconPacket.TYPE_REQUEST_AUTH, password);
			RconPacket.send(authRequest, sessionSocket.getOutputStream());

			RconPacket authResponse = RconPacket.receive(sessionSocket.getInputStream());

			if (authResponse.getType() != RconPacket.TYPE_RESPONSE_AUTH || authResponse.getID() != _sessionId)
				throw new AuthenticationException(ip, port);

			// Success, session is connected
			transitionState(RconSessionState.CONNECTED);
			logn("[OverseerRCON] Connected");
		} catch (IOException | AuthenticationException e) {
			disconnect();
			e.printStackTrace();
		} catch (StateTransitionException e) {
			throw e;
		}
	}

	public String execute(String command) throws StateTransitionException {
		transitionState(RconSessionState.EXECUTING);
		logn(String.format(">>> %s <<<", command));

		String response = "<Error executing command>";

		try {
			// Generate a unique id for this transaction
			int _txid = sessionRandom.nextInt();

			// Send an RCON exec request
			RconPacket execRequest = new RconPacket(_txid, RconPacket.TYPE_REQUEST_EXECCOMMAND, command);
			RconPacket.send(execRequest, sessionSocket.getOutputStream());

			// Await response packets
			RconPacket execResponse = RconPacket.receive(sessionSocket.getInputStream());

			// Verify response is command output
			if (execResponse.getType() != RconPacket.TYPE_RESPONSE_VALUE || (execResponse.getID() != _txid))
				throw new RconPacketException(String.format("Bad RCON response for command: %s", command));

			response = execResponse.getBody();
		} catch (IOException | RconPacketException e) {
			e.printStackTrace();
		} finally {
			transitionState(RconSessionState.CONNECTED);
		}

		log(response);
		return response;
	}

	public void disconnect() throws StateTransitionException {
		transitionState(RconSessionState.DISCONNECTED);
		logn("[OverseerRCON] Disconnected");

		try {
			if (sessionSocket != null && !sessionSocket.isClosed())
				sessionSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sessionSocket = null;
		}
	}

	public RconSessionState getSessionState() {
		return _sessionState;
	}

	public String getOutput() {
		return sessionLog.toString();
	}

	public void addStateChangeListener(ChangeListener listener) {
		stateChangeListeners.add(listener);
	}

	public void addOutputChangeListener(ChangeListener listener) {
		outputChangeListeners.add(listener);
	}

	private void log(String message) {
		sessionLog.append(message);
		outputChangeListeners.forEach(listener -> listener.stateChanged(new ChangeEvent(this)));
	}

	private void logn(String message) {
		sessionLog.append(message);
		sessionLog.append("\n");
		outputChangeListeners.forEach(listener -> listener.stateChanged(new ChangeEvent(this)));
	}

	private void transitionState(RconSessionState nextState) throws StateTransitionException {
		System.out.println(String.format("%s->%s", _sessionState.name(), nextState.name()));

		if (!RconSessionState.isTransitionValid(_sessionState, nextState))
			throw new StateTransitionException(_sessionState, nextState);
		else {
			_sessionState = nextState;
			stateChangeListeners.forEach(listener -> listener.stateChanged(new ChangeEvent(this)));
		}
	}
}
