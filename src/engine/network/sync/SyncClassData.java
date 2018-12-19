package engine.network.sync;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SyncClassData {
	//public HashMap<Byte, Field> idToFieldMap;
	public ArrayList<SyncField> annotations;
	public ArrayList<Field> syncedFields;
	public int numFields;
	
	public SyncClassData(ArrayList<Field> synced, ArrayList<SyncField> annot) {//HashMap<Byte, Field> map
		//idToFieldMap = map;
		syncedFields = synced;
		annotations = annot;
		numFields = syncedFields.size();
	}
}
