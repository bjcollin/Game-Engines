package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeyboardPacket extends Packet {
	public KeyboardPacket() {
		super(PacketType.KEYBOARD);
	}
	public KeyboardPacket(boolean released, int code) {
		super(PacketType.KEYBOARD);
		this.released = released;
		this.code = code;
	}

	public boolean released;
	public int code;

	@Override
	public void onPacketWrite(DataOutputStream ds) throws IOException {
		ds.writeBoolean(released);
		ds.writeInt(code);
	}

	@Override
	public void onPacketRead(DataInputStream ds) throws IOException {
		released = ds.readBoolean();
		code = ds.readInt();
	}

}
