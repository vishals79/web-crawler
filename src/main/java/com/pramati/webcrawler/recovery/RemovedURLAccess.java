package com.pramati.webcrawler.recovery;

public class RemovedURLAccess extends FileAccess {

	private static final String RECOVERY_REMOVED_URL_FILE = "recovery.removed.url.queue.file";

	public RemovedURLAccess() {
		initialize(RECOVERY_REMOVED_URL_FILE);
	}
}
