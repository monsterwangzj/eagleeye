package com.chengyi.eagleeye.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableUtil {

	public SerializableUtil() {
	}

	public static byte[] serialize(Object object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.close();
			byte data[] = bos.toByteArray();
			bos.close();
			return data;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object deSerialize(byte data[]) {
		Object object = null;
		if (data != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(bis);
				object = ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return object;
	}

}
