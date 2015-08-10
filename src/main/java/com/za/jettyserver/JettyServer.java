package com.za.jettyserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class JettyServer {

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		Properties properties = loadConfig("properties/configPort.properties");
		System.out.println(properties.getProperty("port"));
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
