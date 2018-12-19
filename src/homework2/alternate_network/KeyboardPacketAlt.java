package homework2.alternate_network;

import engine.network.packet.KeyboardPacket;

public class KeyboardPacketAlt extends PacketAlt {
	
	private static final long serialVersionUID = -131679026748115176L;
	public KeyboardPacketAlt(KeyboardPacket p) {
		super(PacketTypeAlt.KEYBOARD);
		this.released = p.released;
		this.code = p.code;
	}
	public KeyboardPacketAlt(boolean released, int code) {
		super(PacketTypeAlt.KEYBOARD);
		this.released = released;
		this.code = code;
	}

	public boolean released;
	public int code;

}
