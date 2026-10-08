package service;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Authentification {

    /*
    public static String hasherMdp(String mdpClair){
        String hasher = null;

        if(mdpClair == null || mdpClair.isEmpty()) {
            return null;
        }
        hasher = BCrypt.withDefaults().hashToString(12, mdpClair.toCharArray());
        //Affichage pour débugage
        System.out.println(hasher);
        return hasher;
        // return BCrypt.withDefaults().hashToString(12, mdpClair.toCharArray());
    }
    */

    public static String recupMdpAdmin() {
        String mdp =null;
        //BufferedReader lit ligne par ligne
        try (BufferedReader br = new BufferedReader(new FileReader("secret/mdp.txt"))) {
            mdp = br.readLine();
            //affiche le mdp pour le test/débugage
            //System.out.println(mdp);
        } catch (IOException e) {
            System.err.println("Erreur dans la lecture du fichier des mdp : " + e.getMessage());
        }
        return mdp;
    }

    public static boolean connexion(String mdp) {
        String hashAdmin = recupMdpAdmin();
        BCrypt.Result result = BCrypt.verifyer().verify(mdp.toCharArray(), hashAdmin.toCharArray());
        return result.verified;
    }

}
