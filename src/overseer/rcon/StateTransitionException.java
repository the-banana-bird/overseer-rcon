package overseer.rcon;

public class StateTransitionException extends Exception {
	private static final long serialVersionUID = 1L;

	public StateTransitionException(RconSessionState currentState, RconSessionState nextState) {
		super(String.format("Invalid session state transition %s->%s", currentState.name(), nextState.name()));
	}
}
