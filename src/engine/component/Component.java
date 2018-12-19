package engine.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.network.sync.SyncHelper;

/**
 * 
 * Base unit for Components on GameObjects. Generally used for convenience to hold multiple values.<br>
 * <br>
 * Subclasses can make use of SyncField annotation behavior for automatic synchronization of data.
 *
 */
public class Component {
	
	public Component() {} //Should really only be used as a superclass, this is just here for JavaDoc
	
	public void writeToStream(DataOutputStream ds) throws IOException {
		SyncHelper.writeAllSyncFields(this, ds);
	}
	
	public void readFromStream(DataInputStream ds) throws IOException {
		SyncHelper.readAllSyncFields(this, ds);
	}
}
