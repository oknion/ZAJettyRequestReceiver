package com.za.jettyserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyServer {
	public static BlockingQueue<?> Request_Queue = new ArrayBlockingQueue<>(15000);

	public static void main(String[] args) throws Exception {
		// BasicConfigurator.configure();
		// Load config from properties
		Properties properties = loadConfig("properties/configPort.properties");
		// Log.setLog(new NoLogging());
		//
		PropertyConfigurator.configure("log4j.properties");
		if (("false").equals(properties.get("multiVerifyServer"))) {
			ZARequestReceiverConfig.getInstance().init("properties/verifyservers.properties");
			System.out.println("Start server with one verifyserverconfig");
		} else {
			System.out.println("Start server with multi verifyserverconfig");
			new Thread(new ConfigChangeListener("properties/verifyservers.properties")).start();
		}
		QueuedThreadPool threadPool = new QueuedThreadPool(4, 4, 10, (BlockingQueue<Runnable>) Request_Queue);

		Server server = new Server(threadPool);
		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory());
		http.setPort(Integer.parseInt(properties.getProperty("port")));
		http.setIdleTimeout(30000);
		server.addConnector(http);
		ServletContextHandler servletContext = new ServletContextHandler(server, "");
		servletContext.addServlet(RequestReceiver.class, "/requestreceiver");
		server.start();

	}

	public static Properties loadConfig(String path) {
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(path);
			prop.load(in);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prop;
	}

}
