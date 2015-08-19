package com.za.jettyserver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
		executor = new ThreadPoolExecutor(2, 2, 10L, TimeUnit.SECONDS, (BlockingQueue) new LinkedBlockingQueue<>());
	}

	@Override
	public void run() {

		while (true) {
			try {
				strings.poll(2, TimeUnit.SECONDS);
				executor.execute(new SendRequest(strings.take()));
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}

class SendRequest implements Runnable {
	private String[] request;
	private static ZARequestReceiverConfig config = ZARequestReceiverConfig.getInstance();
	private static String[] keys = config.getKeys();
	private static Map<String, Integer> maps = config.getMapProperties();

	public SendRequest(String[] request) {
		this.request = request;
	}

	@Override
	public void run() {

		try {
			byte modulo = (byte) (System.currentTimeMillis() % keys.length);
			TTransport transport;
			String key = keys[modulo];
			transport = new TFramedTransport(new TSocket(key, maps.get(key)));
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			VerifyRequestService.Client client = new VerifyRequestService.Client(protocol);
			System.out.println("Pass request to " + key + maps.get(key));
			for (int i = 0; i < request.length; i++) {
				if (request[i] == null) {
					request[i] = "null";
				}
			}
			List<String> strings = Arrays.asList(request);
			client.verifyAndSubmit(strings);
			transport.close();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	/*
	 * convert Object to byte buffer private ByteBuffer toByteBuffer(Object s) {
	 * ByteBuffer byteBuffer; ByteArrayOutputStream byteout = new
	 * ByteArrayOutputStream(); try { ObjectOutputStream out = new
	 * ObjectOutputStream(byteout); out.writeObject(s); } catch (IOException e)
	 * { e.printStackTrace(); return null; } byteBuffer =
	 * ByteBuffer.wrap(byteout.toByteArray());
	 * byteBuffer.put(byteout.toByteArray());
	 * 
	 * return byteBuffer; }
	 */
}
