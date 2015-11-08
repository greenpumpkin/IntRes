import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TCPClient {

	private Socket clientSocket;

	/**
	 * Constructeur
	 * 
	 * @param serveurIP
	 * @param port
	 */
	public TCPClient(byte[] serveurIP, int port) {

		try {
			InetAddress addr = Inet4Address.getByAddress(serveurIP);
			clientSocket = new Socket(addr, port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * Envoie n'importe quelle requête passée en paramètre au serveur.
	 * 
	 * @param request
	 * @return la réponse sur serveur
	 */
	public String sendRequest(String request) {

		String answer = "";
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

	/**
	 * Envoie une requête GetList au serveur.
	 * 
	 * @return la liste des tous les noms avec leurs surnoms.
	 */
	public HashMap<String, ArrayList<String>> sendGetListRequest() {
		String listeString = sendRequest("(getList)");
		// listeString du type "-nom: surnom; surnom; -nom:..."
		String[] tab = listeString.split("-");
		HashMap<String, ArrayList<String>> res = new HashMap<>();
		for (int i = 1; i < tab.length; i++) {
			ArrayList<String> surnoms = new ArrayList<>();
			String[] sep = tab[i].split(":");
			String[] surn = sep[1].split(";");
			for (String sz : surn) {
				surnoms.add(sz);
			}
			res.put(sep[0], surnoms);
		}
		return res;
	}

	/**
	 * Envoie une requête setNom au serveur.
	 * 
	 * @param nom
	 *            de la liste à mettre à jour (ou créer le nom s'il n'existe
	 *            pas)
	 * @param surnoms
	 *            à ajouter au param nom
	 * @return 1 si nom n'existait pas et a été créé avec ses surnoms et 2 s'il
	 *         existait et que le serveur lui a simplement ajouté ses surnoms
	 */
	public int sendSetNomRequest(String nom, ArrayList<String> surnoms) {
		String requete = "(setNom)" + nom + ":";
		for (String s : surnoms) {
			requete += s + ";";
		}
		/* Requête du type (setNom)nom : surnom ; surnom ; … */
		String res = sendRequest(requete);
		return Integer.valueOf(res);
	}

	public static void main(String argv[]) throws Exception {

		/* Adresse IP du serveur */
		byte[] serveurIP = new byte[] { 10, (byte) 212, (byte) 123, (byte) 224 };
		TCPClient client = new TCPClient(serveurIP, 1903);
		ArrayList<String> s = new ArrayList<>();
		s.add("jojo");
		client.sendSetNomRequest("Johana", s);
	}
}