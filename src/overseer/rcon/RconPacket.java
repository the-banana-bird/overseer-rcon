package overseer.rcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RconPacket {
	protected static final int TYPE_REQUEST_AUTH = 3;
	protected static final int TYPE_REQUEST_EXECCOMMAND = 2;
	protected static final int TYPE_RESPONSE_AUTH = 2;
	protected static final int TYPE_RESPONSE_VALUE = 0;

	private static final int MAX_PACKET_SIZE = 4096;
	private static final int MAX_BODY_SIZE = getBodySizeFromPacketDataSize(MAX_PACKET_SIZE);

	private int id;
	private int type;
	private String body;

	protected RconPacket(int id, int type, String body) {
		this.id = id;
		this.type = type;
		this.body = (body.length() <= MAX_BODY_SIZE) ? body : body.substring(0, MAX_BODY_SIZE);
	}

	public int getID() {
		return this.id;
	}

	public int getType() {
		return this.type;
	}

	public String getBody() {
		return this.body;
	}

	private boolean isKeepAlive() {
		return id == 0 && type == 0 && body.equals("Keep Alive");
	}

	protected static void send(RconPacket packet, OutputStream stream) throws IOException {
		// Collect sizes, allocate buffer
		int packetSize = RconPacket.getPacketDataSize(packet.body);
		int bufferSize = RconPacket.getPacketBufferSize(packetSize);

		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		// Add Data: Size (beyond this int), id, type, body
		buffer.putInt(packetSize);
		buffer.putInt(packet.id);
		buffer.putInt(packet.type);
		buffer.put(packet.body.getBytes());

		// Add Data: NullTerm for body + NullTerm for entire packet
		buffer.put((byte) 0);
		buffer.put((byte) 0);

		// Write packet to TCP stream, flush to send
		stream.write(buffer.array());
		stream.flush();
	}

	protected static RconPacket receive(InputStream stream) throws IOException {
		RconPacket response;

		// Sometimes the server sends KeepAlive packets without client input.
		// Consume KeepAlive packets. Return the first non-KeepAlive packet.
		do {
			// Contents of response packet header
			// int Size +4
			// int ID +4
			// int Type +4
			byte[] header = new byte[4 * 3];

			// Read the header
			stream.read(header);

			// Parse the header with a byte buffer
			ByteBuffer buffer = ByteBuffer.wrap(header);
			buffer.order(ByteOrder.LITTLE_ENDIAN);

			int packetSize = buffer.getInt();
			int responseId = buffer.getInt();
			int responseType = buffer.getInt();

			// Parse the body with the header info
			int responseBodySize = getBodySizeFromPacketDataSize(packetSize);
			byte[] responseBody = new byte[responseBodySize];

			// Read body bytes
			int bytesRead = stream.read(responseBody);

			// Confirm body was read entirely
			if (bytesRead != responseBodySize)
				throw new IOException("Bytes read does not match body length for RCON response");

			// Read nullterm byte for body string and entire packet
			stream.read(new byte[2]);

			response = new RconPacket(responseId, responseType, new String(responseBody));
		} while (response.isKeepAlive());

		// Return the first non-KeepAlive packet.
		return response;
	}

	private static int getPacketDataSize(String body) {
		// Contents of packet without metadata
		// int ID +4
		// int Type +4
		// string Body +body.length()
		// (Body NullTerm) +1
		// (NullTerm) +1
		return 4 + 4 + body.length() + 1 + 1;
	}

	private static int getBodySizeFromPacketDataSize(int packetSize) {
		// Contents of packet without metadata
		// int ID +4
		// int Type +4
		// string Body +body.length()
		// (Body NullTerm) +1
		// (NullTerm) +1
		return packetSize - 4 - 4 - 1 - 1;
	}

	private static int getPacketBufferSize(int packetSize) {
		// Contents of packet with metadata
		// int Size +4
		// (Rest Of Packet) +packetSize()
		return 4 + packetSize;
	}
}
