package com.za.jettyserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class ZARequestReceiverConfig {
	private final static ZARequestReceiverConfig INSTANCE = new ZARequestReceiverConfig();
	private static Map<String, Integer> mapProps = new HashMap<>();
	private static String[] keys;
	private static Properties config = new Properties();
	private final static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
	private static final ReadLock rl = rwl.readLock();
	private static final WriteLock wl = rwl.writeLock();

	private ZARequestReceiverConfig() {

	}

	public static ZARequestReceiverConfig getInstance() {

		return INSTANCE;
	}

	public Properties getConfig() {
		rl.lock();

		try {
			return config;

		} finally {
			rl.unlock();
		}
	}

	public Map<String, Integer> getMapProperties() {
		rl.lock();
		try {
			return mapProps;
		} finally {
			rl.unlock();
		}
	}

	public String[] getKeys() {
		rl.lock();
		try {
			return keys;
		} finally {
			rl.unlock();
		}
	}

	public void init(String file) {
		wl.lock();
		try {
			FileInputStream in = new FileInputStream(file);
			config.load(in);
			mapProps.clear();
			for (Object key : config.keySet()) {
				try {
					mapProps.put(key.toString(), Integer.parseInt(config.getProperty(key.toString())));
				} catch (NumberFormatException e) {

				}
			}
			keys = new String[mapProps.size()];
			mapProps.keySet().toArray(keys);

		} catch (IOException e) {
			System.err.println("Could not load configuration!....");
			e.printStackTrace();
		} finally {
			wl.unlock();
		}

	}

}
