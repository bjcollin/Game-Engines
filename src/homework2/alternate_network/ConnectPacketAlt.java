package homework2.alternate_network;

public class ConnectPacketAlt extends PacketAlt {

	private static final long serialVersionUID = 888560611226068860L;
	public String name;
	
	public ConnectPacketAlt() {
		super(PacketTypeAlt.CONNECT);
	}
	public ConnectPacketAlt(String name) {
		super(PacketTypeAlt.CONNECT);
		this.name = name;
	}
}
