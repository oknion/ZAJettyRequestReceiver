package com.za.jettyserver;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class ConfigChangeListener implements Runnable {

	private String fullFilePath;

	public ConfigChangeListener(String fullFilePath) {
		this.fullFilePath = fullFilePath;
		ZARequestReceiverConfig.getInstance().init(fullFilePath);

	}

	@Override
	public void run() {
		register(fullFilePath);
	}

	void register(String fullFilePath) {

		int index = fullFilePath.lastIndexOf("/");
		String fileName = fullFilePath.substring(index + 1, fullFilePath.length());
		String dirPath = fullFilePath.substring(0, index + 1);
		try {
			startWatcher(dirPath, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void startWatcher(String dirPath, String fileName) throws IOException {

		final WatchService watchService = FileSystems.getDefault().newWatchService();

		Path path = Paths.get(dirPath);
		path.register(watchService, ENTRY_MODIFY);
		path.register(watchService, ENTRY_CREATE);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					watchService.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		WatchKey key = null;
		while (true) {
			try {
				key = watchService.take();
				for (WatchEvent<?> event : key.pollEvents()) {

					if (event.context().toString().equals(fileName)) {
						ZARequestReceiverConfig.getInstance().init(dirPath + fileName);
					}
				}
				boolean reset = key.reset();
				if (!reset) {
					System.out.println("Could not reset the watch key.");
					break;
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
