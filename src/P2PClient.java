import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

/* The Client Thread that sends to all other peers with the encoded message*/

public class P2PClient implements Runnable {

	public static Object waiting = "wait";

	String msg = "", tmp = "";
	DatagramSocket ds;
	int buffer_size = 1024;
	int size, localPeer;
	byte buffer[];
	Profile p;

	public P2PClient(Profile p, int size) {
		this.p = p;
		this.size = size;
		buffer = new byte[buffer_size];
		localPeer = P2PTwitter.localPeer;
	}

	public void run() {
		try {
			while (true) {
				ds = new DatagramSocket();

				/* get message from local user */
				tmp = Communication.getMsg();
				while (tmp.length() == 0) {
					tmp = Communication.getMsg();
				}
				tmp = tmp.replace(":", "\\:");
				msg = P2PTwitter.localUnikey + ":" + tmp;
				buffer = msg.getBytes("ISO-8859-1");

				// System.out.println(msg);

				/* send to all other peers */
				int i = 0;
				while (i < size) {
					if (i != localPeer) {
						String addrIp = (String) p.properties
								.get(P2PTwitter.peers[i] + ".ip");
						InetAddress address = InetAddress.getByName(addrIp);
						ds.send(new DatagramPacket(buffer, msg.length(),
								address, 7014));

						/* testing code */
						// System.out.println(new Date().getTime);
					}
					i++;
				}

				/* wait for 1-3 seconds, or a new status has been entered */
				synchronized (waiting) {
					waiting.wait((new Random().nextInt(3000 - 1000) + 1000));
				}

				// Thread.sleep((new Random().nextInt(3000 - 1000) + 1000));
			}

		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}
}