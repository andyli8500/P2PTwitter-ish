import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Date;

/*The Server Thread that recieves all the messages from all other peers and decode the messages*/

public class P2PServer implements Runnable {

	public static String data[], user[];
	public static long time[];

	int buffer_size = 1024;
	int size;
	byte buffer[];
	DatagramSocket ds;
	String addr, data2;
	String msg;
	Profile p;

	public P2PServer(Profile p, int size) {
		this.p = p;
		this.size = size;
		buffer = new byte[buffer_size];
		data = new String[size];
		user = new String[size];
		time = new long[size];
		Arrays.fill(data, "");
		Arrays.fill(user, "");
		Arrays.fill(time, -1);

		// Arrays.fill(data1, "");
		// data1 = new String[size];
		// Arrays.fill(data2, "");
		// data2 = new String[size];
	}

	@Override
	public void run() {
		try {
			ds = new DatagramSocket(7014);
			int x;
			while (true) {

				/* wait for connection */
				DatagramPacket pac = new DatagramPacket(buffer, buffer.length);
				ds.receive(pac);

				/*
				 * save/renew the data and time has been received from other
				 * peers
				 */
				addr = pac.getAddress().getHostAddress();
				int index = findPeer(addr);
				if (index < 0) {
					continue;
				}
				time[index] = new Date().getTime();
				data2 = new String(pac.getData(), 0, pac.getLength(),
						"ISO-8859-1");
				if (data2 == null || data2.isEmpty()) {
					continue;
				}
				// System.out.println(data2);
				data2 = data2.replace("\\:", ":");
				x = data2.indexOf(":");
				if (x < 1) {
					continue;
				}
				data[index] = data2.substring(x + 1, data2.length());
				user[index] = data2.substring(0, x);

				/* testing code */
				// System.out.println("receive from " + addr + ", the peer is "
				// + index + ", and data is " + data[index]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* get message */
	public static synchronized String[] getData() {
		return data;
	}

	/* get sender */
	public static synchronized String[] getUser() {
		return user;
	}

	/* get time of recieving */
	public static synchronized long[] getTimes() {
		return time;
	}

	/*
	 * method that finds the peer in the property file, and Return the peer
	 * index in the file
	 */
	public synchronized int findPeer(String addr) {
		for (int i = 0; i < size; i++) {
			if (p.properties.get(P2PTwitter.peers[i] + ".ip").equals(addr)) {
				return i;
			}
		}
		return -1;
	}

}
