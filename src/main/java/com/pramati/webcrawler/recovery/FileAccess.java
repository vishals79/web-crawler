package com.pramati.webcrawler.recovery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileAccess {

	private FileWriter fileWriter = null;
	private Properties configFile;
	private InputStream inputStream;
	private String recoveryFileName;
	private String recoveryDirName;
	private String recoveryFilePath;

	private boolean isStreamClosed = false;

	public FileAccess() {

	}

	public void initialize(String fileKey) {
		File file = null;
		File dir = null;
		String userCurrentDir = null;
		StringBuilder temp = new StringBuilder();

		inputStream = MainQueueAccess.class.getClassLoader()
				.getResourceAsStream("application.properties");
		if (inputStream != null) {
			try {
				configFile = new Properties();
				configFile.load(inputStream);

				recoveryDirName = configFile.getProperty("recovery.dir");
				recoveryFileName = configFile.getProperty(fileKey);

				userCurrentDir = System.getProperty("user.dir");

				temp = temp.append(userCurrentDir).append("/")
						.append(recoveryDirName);
				dir = new File(temp.toString());
				if (!dir.exists()) {
					dir.mkdir();
				}
				temp.setLength(0);
				temp = temp.append(userCurrentDir).append("/")
						.append(recoveryDirName).append("/")
						.append(recoveryFileName);

				recoveryFilePath = temp.toString();
				file = new File(recoveryFilePath);
				if (!file.exists()) {
					file.createNewFile();
					fileWriter = new FileWriter(recoveryFilePath);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void cleanUp() {
		if (fileWriter != null) {
			try {
				fileWriter.close();
				setStreamClosed(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the fileWriter
	 */
	public FileWriter getFileWriter() {
		if (fileWriter == null) {
			try {
				fileWriter = new FileWriter(recoveryFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileWriter;
	}

	/**
	 * @return the isStreamClosed
	 */
	public boolean isStreamClosed() {
		return isStreamClosed;
	}

	/**
	 * @param isStreamClosed
	 *            the isStreamClosed to set
	 */
	public void setStreamClosed(boolean isStreamClosed) {
		this.isStreamClosed = isStreamClosed;
	}
}
