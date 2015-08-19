package com.za.jettyserver;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestReceiver extends HttpServlet {

	/**
	 * 
	 */
	private static final int MAX_QUEUE_SIZE = 1000000;
	private String[] keys = { "_id", "idsite", "action_name", "url", "ref_type", "urlref", "_idvc", "_viewts", "_idts",
			"idtscr", "_id_visit", "res", "java", "fla", "new_visitor", "ct_code", "ct_city", "us_lang", "us_br", "os",
			"device", "duration", "path_duration", "domain" };
	private static final long serialVersionUID = 1L;
	private static final LinkedBlockingQueue<String[]> REQUESTS = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

	public RequestReceiver() throws IOException {
		super();
		PassRequestWorker worker = new PassRequestWorker(REQUESTS);
		Thread thread = new Thread(worker);
		thread.start();

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if ("tracking".equals(req.getParameter("rec"))) {
			String[] array = new String[keys.length];
			request2array(array, req);
			try {
				REQUESTS.offer(array, 1L, TimeUnit.SECONDS);
				System.out.println("Add request....");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void request2array(String[] array, HttpServletRequest request) {
		for (int i = 0; i < keys.length; i++) {
			array[i] = request.getParameter(keys[i]);
		}
		array[16] = request.getRemoteAddr();
	}
}
