package ca.qc.bdeb.info203.tp1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainCmd {
    private static final String path = "/mots-croises1.txt";
    //reader pour lire les inputs sur la console
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public static void main(String[] args) {
        MotsCroises partie = new MotsCroises();
        println("\u001b[34mBienvenu sur version beta de\u001b[1m Super Mots-Croisés Master 3000\u001b[0m");
        choisirFichier(partie);
        while (!partie.estComplet()) {
            dessinerGrille(partie);
            afficherDescriptions(partie);
            int option = demanderChoixAction(partie);
            if (option == -1)
                return;
            if(!faireUneTentative(partie,option)){
                return;
            }
        }
        println("\u001b[32mFélicitation! Vous avez compléter la grille avec succès" +
                "\nSi vous voulez augmenter votre expérience de jeu, " +
                "allez essayer la version app\u001b[0m");
    }

    /**
     * Donne l'option entre prendre le fichier par défaut ou prendre son propre fichier.
     * @param partie le mot croisé utiliser
     */
    public static void choisirFichier(MotsCroises partie){
        int option = demanderChoixGrille();
        String reponse;
        while (true) {
            try {
                if (option == 2) {
                    println("Veuillez entrez le chemin absolu du fichier" +
                            " ou retourner au choix précédent en tapant 'b'");
                    reponse = input();
                    if (reponse.strip().equals("b")) {
                        option = demanderChoixGrille();
                        continue;
                    } else
                        partie.tryLoadGame(new FileInputStream(reponse));
                } else {
                    partie.tryLoadGame(MainCmd.class.getResourceAsStream(path));
                }
                break;
            } catch (FileNotFoundException e) {
                println("\u001b[31mVotre fichier est introuvable." +
                        " Veuillez vérifier l'ortographe\u001b[0m");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                println("\u001b[31m" + e.getMessage() + "\u001b[0m");
                option = demanderChoixGrille();
            } catch (NullPointerException e) {
                println("""
                        \u001b[31m
                        Le programme est corrompus.
                        Vous pouvez soit jouez avec votre " +
                        "propre grille ou essayer de télécharger une nouvelle version du jeu.\u001b[0m
                        """);
                System.exit(-1);
            }
        }
    }

    /**
     * Demander si on veut ne pas prendre l'option le fichier par défaut et plutôt prendre son
     * propre fichier.
     * L'option 1 est pour le défaut. L'option 2 est pour utiliser sa propre grille
     * @return option numérique qui est soit 1 ou 2
     */
    public static int demanderChoixGrille() {
        int option = 1;
        boolean verification = false;
        println("Voulez vous: \n\t1: Jouez avec notre grille offerte\n\t2: jouez avec votre propre grille");
        print("Votre réponse: ");
        while (!verification) {
            try {
                option = readInt();
            } catch (NumberFormatException e) {
                error("Votre entrée n'est pas un chiffre");
                print("Votre réponse: ");
                continue;
            }
            if (option == 1 || option == 2) {
                verification = true;
            } else {
                error("Votre réponse n'entre pas dans les choix \u001b[34m" +
                        "1\u001b[0m \u001b[31met\u001b[34m 2");
                print("Votre réponse: ");
            }
        }
        return option;
    }

    /**
     * Faire une tentative pour le mot que l'on a sélectionné dans la méthode demanderChoixAction
     * @param partie le mot croisé utiliser
     * @param numeroMot le numéro du mot que l'on veut tenter de résoudre
     * @return valeur boolean si on veut oui ou non continuer la partie
     */
    public static boolean faireUneTentative(MotsCroises partie,int numeroMot){
        print("Vous pouvez entrez s pour accéder la la solution\nTentative:");
        String essais = input();
        if (essais.strip().equals("q"))
            return false;
        else if (essais.strip().equals("s")) {
            partie.revealAnwser(numeroMot);
            String reponse = partie.getReponse(numeroMot);
            println("La réponse pour le numéro: " + numeroMot + " est " + reponse);
        } else {
            partie.transfer(numeroMot, essais);
        }
        return true;
    }

    /**
     * Demande au joueur quel mot pour lequel il veut faire une tentative ou s'il
     * veut quitter la partie
     * @param partie le mot croisé utiliser
     * @return Valeur numérique du choix de mot que l'on veut. Si on veut quitter, retourne -1
     */
    public static int demanderChoixAction(MotsCroises partie) {
        boolean verification;
        int optionNumerique = 0;
        do {
            println("Vous pouvez quitter le jeux en tout moment en entrant q");
            println("Quel mot voulez-vous deviner?");
            verification = true;
            String numero = input();
            //Cette option quitter ici ne se rend pas jusqu'a la prochaine iteration
            if (numero.strip().equals("q"))
                return -1;
            try {
                optionNumerique = Integer.parseInt(numero);
                partie.checkIndex(optionNumerique);
            } catch (NumberFormatException e) {
                error("Le chiffre que vous avez entrez n'est pas correct");
                verification = false;
            }catch (IndexOutOfBoundsException e){
                error("Le numéro de mot que vous avez entrez n'est pas valide");
                verification = false;
            }

        } while (!verification);
        return optionNumerique;
    }

    /**
     * Dessiner sur la console le mot croisé selon les spécifications
     * @param partie le mot croisé utiliser
     */
    public static void dessinerGrille(MotsCroises partie) {
        System.out.println();
        for (int i = 0; i < partie.getLongueurLigne(); i++) {
            for (int j = 0; j < partie.getLongueurColonne(); j++) {
                if (partie.etatCase(i, j).equals(CellState.VIDE))
                    print(". ");
                else if (partie.etatCase(i, j).equals(CellState.NON_TENTE)) {
                    int index = partie.getIndexIfFirstLetter(i, j);
                    if (index != -1)
                        print("\u001b[33m" + index + "\u001b[0m ");
                    else
                        print("? ");
                } else if (partie.etatCase(i, j).equals(CellState.ERONNE))
                    print("\u001b[31m" + partie.getLettre(i, j) + "\u001b[0m ");
                else
                    print(partie.getLettre(i, j) + " ");
            }
            System.out.println();
        }

    }

    /**
     * Affiche la description des éléments restant à compléter
     * @param partie le mot croisé utiliser
     */
    public static void afficherDescriptions(MotsCroises partie) {
        List<String> descriptions = partie.getDescriptions();
        for (String ligne : descriptions) {
            println(ligne);
        }
    }

    //Les méthodes qui suivent me permettent juste d'alléger mon code
    //-----------------------------------------------------------------

    /**
     * @param object élément que l'on veut écrire à la console
     */
    public static void println(Object object) {
        String value = new String(object.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println(value);
    }

    /**
     * @param object élément que l'on veut écrire à la console
     */
    public static void print(Object object) {
        String value = new String(object.toString().getBytes(StandardCharsets.UTF_8));
        System.out.print(value);
    }
    public static void error(Object object) {
        String value = new String(object.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("\u001b[31m"+value+"\u001b[0m");
    }

    /**
     * @return Un nombre entier entré par l'utilisateur sur la console
     * @throws NumberFormatException Exception produite si la valeur lue sur la console
     *                               ne correspond pas à un nombre entier
     */
    public static int readInt() {
        try {
            return Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Permet de faire une méthode simple, facile et qui produit moins d'erreur que la
     * classe {@link java.util.Scanner Scanner}
     *
     * @return Une chaîne de caractère entré par l'utilisateur sur la console
     */
    public static String input() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
