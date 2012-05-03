package com.nxcgroup.liferay.storage;

public class StorageItem {
	private String token;
	private String url;

	public StorageItem(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
