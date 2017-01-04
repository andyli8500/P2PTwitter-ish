public class P2PTwitter {

	public static String localUnikey, localPseudo;
	public static String peers[];
	public static int NUM_OF_PEERS;
	public static int localPeer;
	public static Profile profile;

	/* Main Method */
	public static void main(String[] args) {
		localUnikey = args[0];
		profile = new Profile();
		String tmp = (String) profile.properties.get("participants");
		peers = tmp.split(",");
		NUM_OF_PEERS = peers.length;

		localPeer = findPeer(localUnikey);
		localPseudo = (String) profile.properties.get(peers[localPeer]
				+ ".pseudo");

		/* start all the threads for P2P */
		Thread t1 = new Thread(new P2PClient(profile, NUM_OF_PEERS));
		Thread t2 = new Thread(new P2PServer(profile, NUM_OF_PEERS));
		Thread t3 = new Thread(new Communication(profile, NUM_OF_PEERS));
		t3.start();
		t1.start();
		t2.start();
	}

	/* Method for finding the peer index by given the unikey */
	public static int findPeer(String local) {
		String unikey;
		String localKey = local;
		for (int i = 0; i < NUM_OF_PEERS; i++) {
			unikey = (String) profile.properties.get(peers[i] + ".unikey");
			if (unikey.equals(localKey)) {
				return i;
			}
		}
		return 0;
	}
}
