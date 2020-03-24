package overseer.rcon;

public enum RconSessionState {
	DISCONNECTED, CONNECTING, AUTHENTICATING, CONNECTED, EXECUTING;

	// State/transition diagram available in doc folder: RCONSession.png
	static boolean isTransitionValid(RconSessionState currentState, RconSessionState nextState) {
		switch (currentState) {
		case DISCONNECTED:
			return (nextState == RconSessionState.CONNECTING);
		case CONNECTING:
			return (nextState == RconSessionState.AUTHENTICATING || nextState == RconSessionState.DISCONNECTED);
		case AUTHENTICATING:
			return (nextState == RconSessionState.CONNECTED || nextState == RconSessionState.DISCONNECTED);
		case CONNECTED:
			return (nextState == RconSessionState.EXECUTING || nextState == RconSessionState.DISCONNECTED);
		case EXECUTING:
			return (nextState == RconSessionState.CONNECTED || nextState == RconSessionState.DISCONNECTED);
		default:
			return false;
		}
	}
}