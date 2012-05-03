package com.nxcgroup.liferay.storage;

import java.util.HashMap;
import java.util.Map;

final public class GoogleKeyStorage {
	final private static Map<String, StorageItem> storage = new HashMap<String, StorageItem>();

	public static Map<String, StorageItem> getStorage() {
		return storage;
	}
}
