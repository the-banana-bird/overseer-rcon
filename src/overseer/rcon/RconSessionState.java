package overseer.rcon;

public enum RconSessionState {
	DISCONNECTED, CONNECTING, AUTHENTICATING, CONNECTED, EXECUTING;
}

/*
 * DISCONNECTED->CONNECTING User requests to connect
 * 
 * CONNECTING->AUTHENTICATING TCP socket success, session requests auth
 * 
 * CONNECTING->DISCONNECTED TCP socket failure
 * 
 * AUTHENTICATING->CONNECTED Server responds with password OK
 * 
 * AUTHENTICATING->DISCONNECTED Server responds with password invalid
 * 
 * CONNECTED->EXECUTING User requests to run a command
 * 
 * CONNECTED->DISCONNECTED Connection from either side closes
 * 
 * EXECUTING->CONNECTED Server responds to command
 * 
 * EXECUTING->DISCONNECTED Connection from either side closes
 */