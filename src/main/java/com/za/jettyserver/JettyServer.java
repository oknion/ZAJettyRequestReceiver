package com.za.jettyserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.log.Logger;

public class JettyServer {

	public static void main(String[] args) throws Exception {
		// BasicConfigurator.configure();
		// Load config from properties
		Properties properties = loadConfig("properties/configPort.properties");
		// Log.setLog(new NoLogging());
		//
		if (("false").equals(properties.get("multiVerifyServer"))) {
			ZARequestReceiverConfig.getInstance().init("properties/verifyservers.properties");
		} else {
			new Thread(new ConfigChangeListener("properties/verifyservers.properties")).start();
		}
		Server server = new Server(Integer.parseInt(properties.getProperty("port")));
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		handler.addServletWithMapping(RequestReceiver.class, "/requestreceiver");

		server.start();
		server.join();
	}

	public static Properties loadConfig(String path) {
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(path);
			prop.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prop;
	}
}

class NoLogging implements Logger {
	@Override
	public String getName() {
		return "no";
	}

	@Override
	public void warn(String msg, Object... args) {
	}

	@Override
	public void warn(Throwable thrown) {
	}

	@Override
	public void warn(String msg, Throwable thrown) {
	}

	@Override
	public void info(String msg, Object... args) {
	}

	@Override
	public void info(Throwable thrown) {
	}

	@Override
	public void info(String msg, Throwable thrown) {
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void setDebugEnabled(boolean enabled) {
	}

	@Override
	public void debug(String msg, Object... args) {
	}

	@Override
	public void debug(Throwable thrown) {
	}

	@Override
	public void debug(String msg, Throwable thrown) {
	}

	@Override
	public Logger getLogger(String name) {
		return this;
	}

	@Override
	public void ignore(Throwable ignored) {
	}

	@Override
	public void debug(String arg0, long arg1) {
		// TODO Auto-generated method stub

	}
}
