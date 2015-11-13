package main;

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
	
	/**
	 * Envoie n'importe quelle requette passée en parametre au serveur
	 * @param request
	 * @return la réponse sur serveur
	 */
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

	/**
	 * Envoie une requete GetList au serveur
	 * @return la liste des tous les noms avec leurs surnoms 
	 */
	public HashMap<String,ArrayList<String>> sendGetListRequest(){
		String listeString=sendRequest("(getList)");
		System.out.println(listeString);
		//listeString du type "-nom: surnom; surnom; -nom:..."
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
	
	/**
	 * Envoie une requete GetListSurnoms au serveur
	 * @return la liste de tous les surnoms 
	 */
	public ArrayList<String> sendGetListSurnomsRequest(){
		String listeString=sendRequest("(getListSurnoms)");
		//listeString du type "surnom; surnom;"
		ArrayList<String> surnoms= new ArrayList<>();
		String[] surn= listeString.split(";");
		for (int i=1; i<surn.length; i++){
			surnoms.add(surn[i]);
		}
		return surnoms;
	}
	
	/**
	 * Envoie une requête getListNoms au serveur.
	 * @return la liste de tous les noms enregistrés sur le serveur.
	 */
	public ArrayList<String> sendGetListeNomsRequest() {
		String listeString = sendRequest("(getListeNoms)");
		//listeString du type "nom; nom;"
		ArrayList<String> noms = new ArrayList<>();
		String[] names = listeString.split(";");
	
		for (int i = 1; i < names.length; i++) {
			noms.add(names[i]);
		}
		
		return noms;
	}
	
	/**
	 * Envoie une requete setNom au serveur
	 * @param nom de la liste à mettre à jour (ou créer s'il n'existe pas)
	 * @param surnoms à ajouter à nom
	 * @return 1 si nom n'existait pas et a été créé avec ses surnoms et 2
	 *  s'il existait et que le serveur lui a simplement ajouté ses surnoms 
	 */
	public int sendSetNomRequest(String nom, ArrayList<String> surnoms){
		String requete="(setNom)"+nom+":";
		for (String s: surnoms){
			requete+=s+";";
		}
		//requete du type (setNom)nom : surnom ; surnom ; …
		System.out.println(requete);
		String res=sendRequest(requete);
		return Integer.valueOf(res);
	}
	
	public static void main(String argv[]) throws Exception {
		byte[] serveurIP = new byte[] {10, (byte) 212, (byte) 116, (byte) 160};
		TCPClient client = new TCPClient(serveurIP, 1903);
		ArrayList<String> s= new ArrayList<>();
		s.add("jojo");
		client.sendGetListeNomsRequest();
		//client.sendSetNomRequest("Johana", s);
		//client.sendGetListRequest();
		//client.sendRequest("oiuhoh");
	}
}
