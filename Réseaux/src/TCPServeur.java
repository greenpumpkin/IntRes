import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class TCPServeur {

	private HashMap<String, ArrayList<String>> liste;
	private String dataFile;
	private static final int PORT = 1903;

	/**
	 * Constructeur
	 * 
	 * @param data
	 */
	public TCPServeur(String data) {

		String nom = "";
		ArrayList<String> surnoms = new ArrayList<String>();
		this.liste = new HashMap<String, ArrayList<String>>();

		this.dataFile = data;

		// lecture du fichier texte
		try {
			InputStream ips = new FileInputStream(data);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;

			while ((ligne = br.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(ligne, ":,;");
				nom = tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()) {
					surnoms.add(tokenizer.nextToken());
				}
				liste.put(nom, new ArrayList<String>(surnoms));
				surnoms.clear();
			}

			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * Requête permettant de récupérer tous les noms et surnoms des personnes
	 * enregistrées dans le serveur
	 * 
	 * @return String : -nom: surnom ; surnom ;… -nom: surnom ; surnom ;…
	 */
	public String getListe() {

		/* Chaîne de caractères à retourner */
		String result = "";

		for (Entry<String, ArrayList<String>> entry : liste.entrySet()) {
			String cle = entry.getKey();
			ArrayList<String> valeur = entry.getValue();

			/* Stockage du nom dans result */
			result += "-" + cle + ": ";

			/* Stockage des surnoms dans result */
			for (String surnom : valeur) {
				result += surnom + " ; ";
			}
		}
		return result;
	}

	/**
	 * Requête permettant de récupérer tous les surnoms des personnes
	 * enregistrées dans le serveur
	 * 
	 * @return String : surnom; surnom; surnom;...
	 */
	public String getListeSurnoms() {

		/* Chaîne de caractères à retourner */
		String result = "";

		for (Entry<String, ArrayList<String>> entry : liste.entrySet()) {
			ArrayList<String> valeur = entry.getValue();

			/* Stockage des surnoms dans result */
			for (String surnom : valeur) {
				result += surnom + " ; ";
			}
		}
		return result;
	}
	
	/**
	 * Requête permettant de récupérer tous les noms des personnes enregistrées
	 * dans le serveur.
	 * 
	 * @return String contenant les noms enregistrés dans le serveur.
	 */
	public String getListeNoms() {

		/* Chaîne de caractères à retourner */
		String result = "";

		for (Entry<String, ArrayList<String>> entry : liste.entrySet()) {
			String cle = entry.getKey();
			ArrayList<String> valeur = entry.getValue();

			result += "[ " + cle + " ;";
		}

		result += " ]";
		return result;
	}
	
	
	/**
	 * Requête permettant d'ajouter un nouveau nom avec les surnoms passés en
	 * paramètre. Si le nom existe déjà, les surnoms sont ajoutés à la liste des
	 * surnoms pour cette personne.
	 * 
	 * @param nom
	 * @param surnoms
	 * @return String : "1" si un nouveau a été créé, "2" si le nom existait
	 *         déjà et a reçu les nouveaux surnoms
	 */
	public String setNom(String nom, ArrayList<String> surnoms) {

		/*
		 * Si le nom existe déjà, on ajoute les surnoms à la suite de ses autres
		 * surnoms
		 */
		if (this.liste.containsKey(nom)) {
			liste.get(nom).addAll(surnoms);
			return "2";
		}

		/* Sinon, on ajoute le nom dans la liste avec les surnoms associés */
		else {
			this.liste.put(nom, surnoms);
			return "1";
		}
	}

	/**
	 * Méthode permettant de traiter une requête envoyée par le client
	 */
	public void serveur() {

		String reqClient;
		ServerSocket welcomeSocket;

		try {
			welcomeSocket = new ServerSocket(this.PORT);
			while (true) {
				Socket connectionSocket = welcomeSocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				reqClient = inFromClient.readLine();
				System.out.println("Received: " + reqClient);

				if (reqClient.contains("(getList)")) {
					outToClient.writeBytes(this.getListe());
				}
				
				else if (reqClient.contains("(getListeSurnoms)")) {
					outToClient.writeBytes(this.getListeSurnoms());
				}
				
				else if (reqClient.contains("(getListeNoms)")) {
					outToClient.writeBytes(this.getListeNoms());
				}

				else if (reqClient.contains("(setNom)")) {

					if (!reqClient.contains(";") || !reqClient.contains(":")) {
						outToClient.writeBytes("#SYNTAX ERROR#");
						break;
					}

					String[] result = reqClient.split(":");
					String nom = result[0].replace("(setNom", "");
					ArrayList<String> surnoms = new ArrayList<String>();
					String[] tabSurnoms = result[1].split(";");
					for (String s : tabSurnoms) {
						surnoms.add(s);
					}
					outToClient.writeBytes(this.setNom(nom, surnoms));
				}

				else {
					outToClient.writeBytes("#SYNTAX ERROR#");
				}
				connectionSocket.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String args[]) {
		TCPServeur serv = new TCPServeur("data.txt");
		serv.serveur();
	}
}
