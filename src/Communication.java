import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/* The thread that ask for status and print out others status*/

public class Communication implements Runnable {

	public static String msg = "";

	public boolean hasEntered = false;
	public String tmp = "";
	String pseudo, unikey, peer;
	String data[], user[];
	int size;
	long currentTime;
	long[] times;
	Profile p;

	public Communication(Profile p, int size) {
		this.p = p;
		this.size = size;
	}

	@Override
	public void run() {
		try {
			while (true) {

				/* ask for new status */
				if (!hasEntered) {
					System.out.print("Status: ");
					BufferedReader br = new BufferedReader(
							new InputStreamReader(System.in));
					tmp = br.readLine();
					while (tmp.length() == 0) {
						System.out.println("Status is empty. Retry.");
						tmp = new BufferedReader(new InputStreamReader(
								System.in)).readLine();
					}

					/* too long */
					while (tmp.length() > 140) {
						System.out
								.println("Status is too long, 140 characters max. Retry.");
						tmp = new BufferedReader(new InputStreamReader(
								System.in)).readLine();
					}

					msg = tmp;

					/* notify the client that the status has renewed */
					synchronized (P2PClient.waiting) {
						P2PClient.waiting.notify();
					}
					hasEntered = true;

				} else {

					/* print out all status */
					data = P2PServer.getData();
					user = P2PServer.getUser();
					System.out.println("### P2P tweets ###");

					System.out.println("# " + P2PTwitter.localPseudo
							+ " (myself): " + msg);
					for (int i = 0; i < size; i++) {
						if (i == (P2PTwitter.localPeer))
							continue;
						peer = P2PTwitter.peers[i];
						// System.out.println(peer);
						pseudo = (String) p.properties.get(peer + ".pseudo");
						if (data[i].length() == 0) {
							unikey = (String) p.properties
									.get(peer + ".unikey");
							// if (unikey.equals(P2PTwitter.localUnikey))
							// continue;
							System.out.println("# " + "[" + pseudo + " ("
									+ unikey + ")" + ": "
									+ "not yet initialized]");
						} else {
							currentTime = new Date().getTime();
							times = P2PServer.getTimes();
							if (currentTime - times[i] < 10000) {
								System.out.println("# " + pseudo + " ("
										+ user[i] + ")" + ": " + data[i]);
							} else if (currentTime - times[i] < 20000) {
								System.out.println("# " + "[" + pseudo + " ("
										+ user[i] + ")" + ": " + "idle]");
							} else {
								// do not print
							}
						}
					}
					System.out.println("### End tweets ###");
					hasEntered = false;
				}
			}
		} catch (IOException e) {
		}

	}

	public static synchronized String getMsg() {
		return msg;
	}

}
