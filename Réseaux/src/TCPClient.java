import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TCPClient {
	private Socket clientSocket;

	public TCPClient(byte[] serveurIP, int port) {
		try {
			InetAddress addr = Inet4Address.getByAddress(serveurIP);
			clientSocket = new Socket(addr, port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public String sendRequest(String request){
		String answer="";
		try { 
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());

			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));

			outToServer.writeBytes(request + '\n');

			answer = inFromServer.readLine();
			System.out.println(answer);
			clientSocket.close();

		} catch (IOException e) {
			System.out.println(e);
		}
		return answer;
	}

	public HashMap<String,ArrayList<String>> sendGetListRequest(){
		String listeString=sendRequest("(getList)");
		String[] tab= listeString.split("-");
		HashMap<String,ArrayList<String>> res= new HashMap<>();
		for (int i=1; i<tab.length; i++){
			ArrayList<String> surnoms= new ArrayList<>();
			String[] sep= tab[i].split(":");
			String[] surn= sep[1].split(";");
			for (String sz: surn){
				surnoms.add(sz);
			}
			res.put(sep[0],surnoms);
		}
		return res;
	}
	
	public int sendSetRequest(String nom, ArrayList<String> surnoms){
		//(setNom)nom : surnom ; surnom ; …
		String requete="(setNom)"+nom+":";
		for (String s: surnoms){
			requete+=s+";";
		}
		String res=sendRequest(requete);
		System.out.println(res);
		return Integer.valueOf(res);
	}
	
	public static void main(String argv[]) throws Exception {
		byte[] serveurIP = new byte[] {10, (byte) 212, (byte) 123, (byte) 224};
		TCPClient client = new TCPClient(serveurIP, 1903);
		ArrayList<String> s= new ArrayList<>();
		s.add("titi");
		client.sendSetRequest("kolossal", s);
		System.out.println("send getlist");
	}
}
