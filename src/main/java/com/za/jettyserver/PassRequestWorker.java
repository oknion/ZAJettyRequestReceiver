package com.za.jettyserver;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.za.verify.VerifyRequestService;

public class PassRequestWorker implements Runnable {
	private LinkedBlockingQueue<String[]> strings;
	ThreadPoolExecutor executor;

	public PassRequestWorker(LinkedBlockingQueue<String[]> strings) {
		this.strings = strings;
		executor = new ThreadPoolExecutor(2, 4, 10L, TimeUnit.SECONDS, (BlockingQueue) new LinkedBlockingQueue<>());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

		while (true) {
			String[] rq;
			try {
				rq = strings.take();
				executor.execute(new SendRequest(rq));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}

class SendRequest implements Runnable {
	private String[] request;
	ZARequestReceiverConfig config = ZARequestReceiverConfig.getInstance();

	public SendRequest(String[] request) {
		this.request = request;

		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

		try {
			byte modulo = (byte) (System.currentTimeMillis() % config.getKeys().length);
			TTransport transport;
			String key = config.getKeys()[modulo];
			System.err.println(modulo + ";" + key + ";" + config.getMapProperties().get(key));
			transport = new TFramedTransport(new TSocket(key, config.getMapProperties().get(key)));
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			VerifyRequestService.Client client = new VerifyRequestService.Client(protocol);
			client.verifyAndSubmit(Arrays.toString(request));

			transport.close();

		} catch (TException x) {
			x.printStackTrace();
		}
	}

}
