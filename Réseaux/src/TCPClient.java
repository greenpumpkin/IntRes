import java.io.*;
import java.net.*;

public class TCPClient {
	private Socket clientSocket;
	public static final byte[] SERVERIP = new byte[] {127, 0, 0, 1};

	public TCPClient() {
		String sentence, modifiedSentence;
		try { 
			InetAddress addr = Inet4Address.getByAddress(SERVERIP);
			
			clientSocket = new Socket(addr, 1903);
			BufferedReader inFromUser = new BufferedReader(
					new InputStreamReader(System.in));

			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());

			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));

			sentence = inFromUser.readLine();
			outToServer.writeBytes(sentence + '\n');

			modifiedSentence = inFromServer.readLine();
			System.out.println("FROM SERVER: " + modifiedSentence);
			clientSocket.close();

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void main(String argv[]) throws Exception {
		TCPClient client = new TCPClient();
	}
}