package ca.qc.bdeb.info203.tp1;

import java.util.Objects;

/**
 * Sert à représenter une position dans le mot croisé.
 * Ceci facilite entre autre les comparaisons et la création d'un index de position
 * @param ligne numéro de ligne
 * @param colonne numéro de colonne
 */
public record Position(int ligne, int colonne) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return ligne == position.ligne && colonne == position.colonne;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ligne, colonne);
    }
}
