package com.za.jettyserver;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class RequestReceiver extends HttpServlet {

	/**
	 * 
	 */
	private String[] keys = { "_id", "idsite", "action_name", "url", "ref_type", "urlref", "_idvc", "_viewts", "_idts",
			"idtscr", "_id_visit", "res", "java", "fla", "new_visitor", "ct_code", "ct_city", "us_lang", "us_br", "os",
			"device", "duration", "path_duration", "domain" };
	private static final long serialVersionUID = 1L;
	private static BlockingQueue<String[]> REQUESTS = new LinkedBlockingQueue<>(20000);
	private static final Logger LOGGER = Logger.getLogger(RequestReceiver.class.getName());
	static ThreadPoolExecutor executor;
	Thread[] threads;
	int workerThread = 2;
	Timer timer = new Timer();
	static AtomicInteger receiveCount = new AtomicInteger(0);

	public RequestReceiver() {
		// executor = new ThreadPoolExecutor(4, 4, 10L, TimeUnit.SECONDS,
		// (BlockingQueue) new LinkedBlockingQueue<>());
		// System.out.println("numworker:" + executor.getCorePoolSize());
		threads = new Thread[workerThread];
		for (int i = 0; i < workerThread; i++) {
			threads[i] = new Thread(new PassRequestWorker(REQUESTS));
			threads[i].start();
			// executor.execute(new PassRequestWorker(REQUESTS));
		}
		timer.schedule(new TrackRequest(), 1000, 1000);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String[] array = new String[keys.length];
		request2array(array, req);
		try {
			if (JettyServer.Request_Queue.size() > 10000) {
				LOGGER.warn("Jetty queue size is over 10000!");
			}
			if (REQUESTS.offer(array, 1L, TimeUnit.SECONDS)) {
			} else {
				LOGGER.warn("WORKER QUEUE FULL! Queue size:" + REQUESTS.size());
			}
		} catch (InterruptedException e) {
			LOGGER.warn("Could not add request to queue. Queue size: " + REQUESTS.size());
		}
		receiveCount.getAndIncrement();
		return;

	}

	private void request2array(String[] array, HttpServletRequest request) {
		for (int i = 0; i < keys.length; i++) {
			array[i] = request.getParameter(keys[i]);
		}
		// array[RequestParams.domain] = request.getServerName();
		array[RequestParams.ct_city] = request.getRemoteAddr();

	}

	class TrackRequest extends TimerTask {
		int lastValue = 0;
		int lastRc = 0;

		@Override
		public void run() {
			System.out.println("Total  request:" + receiveCount.get());
			System.out.println("Pending request:" + JettyServer.Request_Queue.size());
			System.out.println("Pending in worker queue:" + REQUESTS.size());
			System.out.println("Total request receive in last second:" + (receiveCount.get() - lastRc));
			System.out.println("Total request send in last second:" + (PassRequestWorker.count.get() - lastValue));
			lastValue = PassRequestWorker.count.get();
			lastRc = receiveCount.get();
		}
	}
}
