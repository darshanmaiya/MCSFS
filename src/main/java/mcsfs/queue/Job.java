package mcsfs.queue;

import java.io.File;

public class Job {
	private QueueOperations operation;
	private File file;
	private String accessKey;
	private String provider;
	public Job(QueueOperations operation, File file, String accessKey, String provider, int retries) {
		this.operation = operation;
		this.file = file;
		this.accessKey = accessKey;
		this.provider = provider;
		this.retries = retries;
	}
	private int retries = 0;
	public QueueOperations getOperation() {
		return operation;
	}
	public void setOperation(QueueOperations operation) {
		this.operation = operation;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public int getRetries() {
		return retries;
	}
	public void setRetries(int retries) {
		this.retries = retries;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
}
