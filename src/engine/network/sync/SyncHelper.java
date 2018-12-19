package engine.network.sync;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import engine.component.Component;

import engine.exception.InvalidAnnotationException;

@SuppressWarnings("rawtypes")
public class SyncHelper {
	
	private static HashMap<Class, SyncClassData> CACHE = new HashMap<Class, SyncClassData>();

	
	private static boolean isValidTypeForSync(Class c) {
		if (c == int.class || c == boolean.class || c == double.class || c == byte.class ||
				c == java.awt.Color.class || Component.class.isAssignableFrom(c)) {
			return true;
		}
		return false;
	}
	
	public static SyncClassData getSyncData(Object obj) {
		if (CACHE.containsKey(obj.getClass())) {
			return CACHE.get(obj.getClass());
		} else {
			Field[] fields = obj.getClass().getFields();
			ArrayList<Field> marked = new ArrayList<Field>();
			ArrayList<SyncField> annots = new ArrayList<SyncField>();
			for (Field f : fields) {
				if (f.isAnnotationPresent(SyncField.class)) {
					SyncField annotation = f.getAnnotation(SyncField.class);
					if (isValidTypeForSync(f.getType())) {
						marked.add(f);
						annots.add(annotation);
					} else {
						throw new InvalidAnnotationException("Illegal type for @SyncField: "+f.getType());
					}
				}
			}
			SyncClassData data = new SyncClassData(marked, annots);
			CACHE.put(obj.getClass(), data);
			return data;
		}
	}
	
	public static void writeAllSyncFields(Object obj, DataOutputStream ds) throws IOException {
		SyncClassData syncdata = SyncHelper.getSyncData(obj);
		for (int i=0; i<syncdata.numFields; i++) {
			SyncHelper.writeSyncField(obj, syncdata.syncedFields.get(i), syncdata.annotations.get(i), ds);
		}
	}
	
	public static void readAllSyncFields(Object obj, DataInputStream ds) throws IOException {
		SyncClassData syncdata = SyncHelper.getSyncData(obj);
		for (int i=0; i<syncdata.numFields; i++) {
			SyncHelper.readSyncField(obj, syncdata.syncedFields.get(i), syncdata.annotations.get(i), ds);
		}
	}
	
	private static void writeSyncField(Object obj, Field field, SyncField sf, DataOutputStream ds) throws IOException {
		try {
			if (field.getType() == int.class) {
				ds.writeInt(field.getInt(obj));
			} else if (field.getType() == boolean.class) {
				ds.writeBoolean(field.getBoolean(obj));
			} else if (field.getType() == double.class) {
				ds.writeDouble(field.getDouble(obj));
			} else if (field.getType() == byte.class) {
				ds.writeByte(field.getByte(obj));
			} else if (field.getType() == java.awt.Color.class) {
				Color c = (Color)field.get(obj);
				ds.writeInt(c.getRGB());
			} else if (Component.class.isAssignableFrom(field.getType())) { //field instanceof Component
				Component c = (Component) field.get(obj);
				c.writeToStream(ds);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void readSyncField(Object obj, Field field, SyncField sf, DataInputStream ds) throws IOException {
		try {
			if (field.getType() == int.class) {
				field.setInt(obj, ds.readInt());
			} else if (field.getType() == boolean.class) {
				field.setBoolean(obj, ds.readBoolean());
			} else if (field.getType() == double.class) {
				field.setDouble(obj, ds.readDouble());
			} else if (field.getType() == byte.class) {
				field.setByte(obj, ds.readByte());
			} else if (field.getType() == Color.class) {
				field.set(obj, new Color(ds.readInt()));
			} else if (Component.class.isAssignableFrom(field.getType())) { //field instanceof Component
				Component c = (Component) field.get(obj);
				c.readFromStream(ds);
			}
		} catch (IOException e) {
			throw e; //Throw this back up, so that it will handle it gracefully
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
