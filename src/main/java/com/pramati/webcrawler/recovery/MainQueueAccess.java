package com.pramati.webcrawler.recovery;

public class MainQueueAccess extends FileAccess {

	private static final String RECOVERY_MAIN_QUEUE_FILE = "recovery.main.queue.file";

	public MainQueueAccess() {
		initialize(RECOVERY_MAIN_QUEUE_FILE);
	}
}
