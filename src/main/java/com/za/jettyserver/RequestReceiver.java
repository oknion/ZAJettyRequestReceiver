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
		String[] array = new String[23];

		request2array(array, req);
		strings.add(array);
	}

	private void request2array(String[] array, HttpServletRequest request) {
		array[0] = request.getParameter("_id");
		array[1] = request.getParameter("idsite");
		array[2] = request.getParameter("action_name");
		array[3] = request.getParameter("url");
		array[4] = request.getParameter("ref_type");
		array[5] = request.getParameter("urlref");
		array[6] = request.getParameter("_idvc");
		array[7] = request.getParameter("_viewts");
		array[8] = request.getParameter("_idts");
		array[9] = request.getParameter("idtscr");
		array[10] = request.getParameter("_id_visit");
		array[11] = request.getParameter("res");
		array[12] = request.getParameter("java");
		array[13] = request.getParameter("fla");
		array[14] = request.getParameter("new_visitor");
		array[15] = request.getParameter("ct_code");
		array[16] = request.getParameter("ct_city");
		array[17] = request.getParameter("us_lang");
		array[18] = request.getParameter("us_br");
		array[19] = request.getParameter("os");
		array[20] = request.getParameter("device");
		array[21] = request.getParameter("duration");
		array[22] = request.getParameter("path_duration");

	}
}
