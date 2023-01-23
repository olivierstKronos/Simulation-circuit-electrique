package ca.qc.bdeb.info203.tp1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MotsCroises {
    private HashMap<Position, Character> positionLettreReponse;
    private HashMap<Position, Character> positionLettreJoueur;
    private HashMap<Position, Integer> positionPremiereLettreMots;
    private int longueurLigne;
    private int longueurColonne;
    private HashMap<Integer, Mot> mots;

    MotsCroises() {
        longueurLigne = 0;
        longueurColonne = 0;
        mots = new HashMap<>();
        positionLettreReponse = new HashMap<>();
        positionLettreJoueur = new HashMap<>();
        positionPremiereLettreMots = new HashMap<>();
    }

    /**
     * Essaye de charger un nouveau mot croisé à partir d'un fichier. Si
     * une exception est lancée, les informations ne seront pas transféré à ce mot croisé
     *
     * @param stream le fichier de donné pour constituer la grille
     * @throws NullPointerException     Exception produite si le stream est null
     * @throws IllegalArgumentException Une ou plusieurs données du fichier sont invalides
     * @throws IOException              Une erreur dna le système a été produite
     */
    public void tryLoadGame(InputStream stream) throws IOException {
        MotsCroises croise = new MotsCroises();
        croise.loadMotCroisee(stream);
        this.mots = croise.mots;
        this.longueurLigne = croise.longueurLigne;
        this.longueurColonne = croise.longueurColonne;
        this.positionLettreReponse = croise.positionLettreReponse;
        this.positionLettreJoueur = croise.positionLettreJoueur;
        this.positionPremiereLettreMots = croise.positionPremiereLettreMots;
    }


    private void loadMotCroisee(InputStream stream) throws IOException {
        int iter = 0;
        String ligne;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            while ((ligne = reader.readLine()) != null) {

                // Sauter les commentaires (lignes qui débutent par #)
                if (ligne.startsWith("#") || ligne.isBlank()) {
                    continue;
                }

                String[] parsedLine = ligne.split(":");
                if (parsedLine.length != 5)
                    throw new IllegalArgumentException("Le fichier est invalide! " +
                            "\nLe format d'un fichier de mot croisé n'est pas respecter");
                String mot = parsedLine[0];
                int col = Integer.parseInt(parsedLine[1]);
                int line = Integer.parseInt(parsedLine[2]);
                String orientation = parsedLine[3].strip();


                if (!orientation.equalsIgnoreCase("h") && !orientation.equalsIgnoreCase("v"))
                    throw new IllegalArgumentException("Le fichier est invalide! " +
                            "\nL'information sur l'orientation d'un des mots est invalide");
                boolean horizontal = orientation.equalsIgnoreCase("h");
                for (int i = 0; i < mot.length(); i++) {
                    Position pos = orientation.equalsIgnoreCase("h") ? new Position(line, col + i) : new Position(line + i, col);
                    if (positionLettreReponse.containsKey(pos) && !positionLettreReponse.get(pos).equals(mot.charAt(i)))
                        throw new IllegalArgumentException("Le fichier est invalide! " +
                                "\nLes lettres des mots ne se croisent pas au bon endroit");
                    positionLettreReponse.putIfAbsent(pos, mot.charAt(i));
                    positionLettreJoueur.put(pos, ' ');

                }
                positionPremiereLettreMots.put(new Position(line, col), iter + 1);
                longueurColonne = horizontal ? Math.max(longueurColonne, col + mot.length()) : Math.max(longueurColonne, col);
                longueurLigne = horizontal ? Math.max(longueurLigne, line) : Math.max(longueurLigne, line + mot.length());
                mots.put(iter + 1, new Mot(new Position(line, col), horizontal, parsedLine[4], mot));
                iter++;
            }
        }
    }

    /**
     * Sert à obtenir l'indice pour un mot
     *
     * @param index numéro du mot
     * @return description du mot indiqué
     */
    public String getDescription(int index) {
        return getMot(index).getIndice();
    }

    /**
     * Sert à déterminer si une case est vide (impossibilité de mettre une lettre),
     * non tenté (aucune tentative n'a été faite), complete(la case contient une lettre exacte) ou
     * erroné (la case ne contient pas une lettre exacte)
     *
     * @param ligne   numéro de ligne dans le mot croisé
     * @param colonne numéro de colonne dans le mot croisé
     * @return Une constante représentant état de la case
     */
    public CellState etatCase(int ligne, int colonne) {
        Position pos = new Position(ligne, colonne);
        if (!positionLettreReponse.containsKey(pos))
            return CellState.VIDE;
        else if (Character.isWhitespace(getLettre(ligne, colonne)))
            return CellState.NON_TENTE;
        else if (Objects.equals(positionLettreJoueur.get(pos), positionLettreReponse.get(pos)))
            return CellState.COMPLETE;
        else
            return CellState.ERONNE;
    }

    /**
     * Retourne la lettre, s'il y en a une, correspondant à la position d'une case
     * dans le mots-croisé
     *
     * @param ligne   numéro de ligne dans le mot croisé
     * @param colonne numéro de colonne dans le mot croisé
     * @return la lettre situé à la case précisé
     */
    public char getLettre(int ligne, int colonne) {
        return positionLettreJoueur.getOrDefault(new Position(ligne, colonne), ' ');
    }

    /**
     * Transfère un mot entré par le joueur dans la grille. Si la longueur
     * de la chaîne de caractère dépasse le nombre permis, seulement la longueur permisse
     * sera transféré.
     *
     * @param index numéro du mot
     * @param mot   Mot à transférer à la grille
     */
    public void transfer(int index, String mot) {
        Mot unMot = getMot(index);
        Position pos = unMot.getPos();
        int line = pos.ligne();
        int colonne = pos.colonne();
        boolean horizontal = unMot.isHorizontal();
        String rep = unMot.getReponse();
        if (rep.equals(mot) || mot.startsWith(rep))
            unMot.setComplete(true);
        else if (unMot.getReponse().length() > mot.length()) {
            //cette partie est pour si on a déja remplis certaine case et que celle que l'on modifie
            //sont les cases de début.Code plus lourd mais efficace dans c'est quelque cas de figure
            //ex:on remplis le numéro 6 de grille 1 et que l'on remplis ensuite seulement 3 ppremière
            //lettre du numéro 8
            unMot.setComplete(true);
            for (int i = mot.length(); i < unMot.getReponse().length(); i++) {
                Position nextPos = horizontal ? new Position(line, colonne + i) : new Position(line + i, colonne);
                if (!positionLettreJoueur.get(nextPos).equals(positionLettreReponse.get(nextPos))) {
                    unMot.setComplete(false);
                    break;
                }
            }
        }

        for (int i = 0; i < mot.length(); i++) {
            if (!positionLettreJoueur.containsKey(pos))
                break;
            else if (horizontal) {
                positionLettreJoueur.replace(new Position(line, colonne + i), mot.charAt(i));
            } else {
                positionLettreJoueur.replace(new Position(line + i, colonne), mot.charAt(i));
            }
        }
    }

    /**
     * Permet de vérifier si le mot croisé à été complété dans son intégralité
     *
     * @return un boolean représentant si oui ou non le jeu est terminé
     */
    public boolean estComplet() {
        for (Map.Entry<Integer, Mot> mot : mots.entrySet()) {
            if (!isComplete(mot.getKey()))
                return false;
        }
        return true;
    }

    /**
     * Permet d'obtenir la liste des numéro et indice des mots non complété
     *
     * @return Une {@link List} d'élément descriptif pour chaque mot
     */
    public List<String> getDescriptions() {
        ArrayList<String> descriptions = new ArrayList<>();
        for (Map.Entry<Integer, Mot> mot : mots.entrySet()) {
            if (!isComplete(mot.getKey()))
                descriptions.add(mot.getKey() + "." + getDescription(mot.getKey()));
        }
        return descriptions;
    }

    /**
     * Vérifie si la cette case du tableau correspond à la première lettre d'un mot
     * et si c'est le cas, rtourne son numéro. Si ce n'est pas le cas, retourne -1
     *
     * @param ligne   numéro de ligne dans le mot croisé
     * @param colonne numéro de colonne dans le mot croisé
     * @return le numéro du mot ou -1 si ce pas la première lettre
     */
    public int getIndexIfFirstLetter(int ligne, int colonne) {
        return positionPremiereLettreMots.getOrDefault(new Position(ligne, colonne), -1);
    }

    public boolean isPositionInWord(int i, int j, int index) {
        if(index==-1)
            return false;
        Mot unMot = mots.get(index);
        Position pos = unMot.getPos();
        if (unMot.isHorizontal())
            return pos.colonne() <= j && j < pos.colonne() + unMot.getReponse().length() && pos.ligne() == i;
        else
            return pos.ligne() <= i && i < pos.ligne() + unMot.getReponse().length() && pos.colonne() == j;
    }

    /**
     * Vérifie si le numéro de mot entré par le joueur est valide
     *
     * @param number numéro du mot
     * @return un boolean qui représente la validité du numéro
     */
    public void checkIndex(int number) {
        if(!(number > 0 || number < mots.size()))
            throw new ArrayIndexOutOfBoundsException();
    }

    public String getReponse(int index) {
        return mots.get(index).getReponse();
    }

    public void revealAnwser(int index) {
        transfer(index, mots.get(index).getReponse());
    }

    public boolean isComplete(int index) {
        return mots.get(index).isComplete();
    }

    public int getLongueurLigne() {
        return longueurLigne;
    }

    public int getLongueurColonne() {
        return longueurColonne;
    }

    public Mot getMot(int index){
        checkIndex(index);
        return mots.get(index);
    }

    public Position getWordPos(int index){
        Mot mot=getMot(index);
        if(mot==null)
            return null;
        return mot.getPos();
    }
}
