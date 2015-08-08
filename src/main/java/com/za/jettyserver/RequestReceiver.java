package com.za.jettyserver;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestReceiver extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final LinkedBlockingQueue<String[]> strings = new LinkedBlockingQueue<>();

	public RequestReceiver() throws IOException {
		super();
		new Thread(new ConfigChangeListener("properties/verifyservers.properties")).start();
		PassRequestWorker worker = new PassRequestWorker(strings);
		Thread thread = new Thread(worker);
		thread.start();

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] array = new String[10];
		request2array(array, req);
		strings.add(array);
	}

	private void request2array(String[] array, HttpServletRequest request) {
		array[0] = request.getParameter("_id");
		array[1] = request.getParameter("idsite");
	}
}
