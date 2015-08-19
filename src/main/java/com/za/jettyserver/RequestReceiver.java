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
	private String[] keys = { "_id", "idsite", "action_name", "url", "ref_type", "urlref", "_idvc", "_viewts", "_idts",
			"idtscr", "_id_visit", "res", "java", "fla", "new_visitor", "ct_code", "ct_city", "us_lang", "us_br", "os",
			"device", "duration", "path_duration", "domain" };
	private static final long serialVersionUID = 1L;
	private final LinkedBlockingQueue<String[]> strings = new LinkedBlockingQueue<>();

	public RequestReceiver() throws IOException {
		super();
		PassRequestWorker worker = new PassRequestWorker(strings);
		Thread thread = new Thread(worker);
		thread.start();

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if ("tracking".equals(req.getParameter("rec"))) {
			String[] array = new String[keys.length];
			request2array(array, req);
			strings.add(array);
			System.out.println("Add request....");
		}
	}

	private void request2array(String[] array, HttpServletRequest request) {
		for (int i = 0; i < keys.length; i++) {
			array[i] = request.getParameter(keys[i]);
		}
		array[16] = request.getRemoteAddr();
	}
}
