package com.za.jettyserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobReturn;
import org.gearman.GearmanServer;

interface Function<T, R> {
	public R apply(T t);
}

public class PassRequestWorker implements Runnable {
	private static final Gearman GEARMAN = Gearman.createGearman();
	private static final String VERIFY_FUNC_NAME = "VerifyFnc";
	private static final GearmanServer SERVER = GEARMAN.createGearmanServer("localhost", 5555);
	private static BlockingQueue<String[]> requests;
	private static ZARequestReceiverConfig config = ZARequestReceiverConfig.getInstance();

	// private static String[] keys = config.getKeys();
	// private static Map<String, Integer> maps = config.getMapProperties();
	// private static List<ServerInfo> serverInfos = new ArrayList<>();

	// static {
	//
	// serverInfos.add(new ServerInfo(config.getKeys()[0],
	// Integer.parseInt(config.getConfig().getProperty(config.getKeys()[0]))));
	// }

	static long start = 0;
	static long end = 0;
	public static AtomicInteger count = new AtomicInteger(1);
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(PassRequestWorker.class.getName());

	// static Function<TTransport, VerifyRequestService.Client> function = new
	// Function<TTransport, VerifyRequestService.Client>() {
	//
	// @Override
	// public Client apply(TTransport t) {
	// return new VerifyRequestService.Client(new TBinaryProtocol(t));
	// }
	// };
	// private static volatile ThriftClientPool<VerifyRequestService.Client>
	// clientPool = new ThriftClientPool<>(function,
	// serverInfos);

	public PassRequestWorker(BlockingQueue<String[]> requests) {
		this.requests = requests;
	}

	public byte[] objectToBytes(Object obj) throws IOException {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		ObjectOutput ois = new ObjectOutputStream(bais);
		ois.writeObject(obj);
		return bais.toByteArray();
	}

	@Override
	public void run() {
		// List<String> lists;
		// ThriftClient<VerifyRequestService.Client> thriftClient = null;
		// VerifyRequestService.Client client;
		String[] request;
		GearmanClient gmClient;
		GearmanJobReturn jobReturn;
		gmClient = GEARMAN.createGearmanClient();
		gmClient.addServer(SERVER);

		// byte[] bytes;
		while (true) {
			try {
				if ((request = requests.poll(1L, TimeUnit.SECONDS)) != null) {

					jobReturn = gmClient.submitBackgroundJob(VERIFY_FUNC_NAME, objectToBytes(Arrays.asList(request)));
					count.getAndIncrement();

					// // gmClient.removeAllServers();
					// // gmClient.shutdown();
					// // gmClient = null;
					// // gmClient = GEARMAN.createGearmanClient();
					// // gmClient.addServer(SERVER);
					// } else {
					// }
					// if (count.get() == 0) {
					// start = System.currentTimeMillis();
					// }
					// if (count.getAndIncrement() % 100000 == 0) {
					// end = System.currentTimeMillis();
					// LOGGER.warn("10000rs/" + (end - start));
					// System.out.println("time:" + (end - start));
					// start = System.currentTimeMillis();
					// }
					// for (int i = 0; i < request.length; i++) {
					// if (request[i] == null) {
					// request[i] = "null";
					// }
					// }
					//
					// lists = Arrays.asList(request);
					// try {
					// thriftClient = clientPool.getClient();
					// client = thriftClient.iFace();
					// client.verifyAndSubmit(lists);
					// thriftClient.finish();
					// } finally {
					// thriftClient.close();
					// }
					// System.out.println("take:" + count.get());
				}

			} catch (Exception e) {
				LOGGER.warn("Error send request to Verify Server. " + e.getMessage());
			}
		}
	}

}
