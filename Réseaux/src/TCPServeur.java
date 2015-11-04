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

	public void serveur() {
		String reqClient;
		String capitalizedSentence;
		ServerSocket welcomeSocket;
		try {
			welcomeSocket = new ServerSocket(1903);
			while (true) {
				Socket connectionSocket = welcomeSocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				reqClient = inFromClient.readLine();
				System.out.println("Received: " + reqClient);
				capitalizedSentence = reqClient.toUpperCase() + '\n';
				outToClient.writeBytes(capitalizedSentence);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		TCPServeur serv = new TCPServeur("data.txt");
		serv.serveur();
	}
}