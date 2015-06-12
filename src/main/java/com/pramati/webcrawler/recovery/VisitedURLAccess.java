package com.pramati.webcrawler.recovery;

public class VisitedURLAccess extends FileAccess {

	private static final String RECOVERY_VISITED_URL_FILE = "recovery.visited.url.queue.file";

	public VisitedURLAccess() {
		initialize(RECOVERY_VISITED_URL_FILE);
	}
}
