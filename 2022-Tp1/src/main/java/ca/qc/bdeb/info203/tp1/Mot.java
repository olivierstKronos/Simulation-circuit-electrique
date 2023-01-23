package ca.qc.bdeb.info203.tp1;

public class Mot {
    private final Position pos;
    private final boolean horizontal;
    private final String indice;
    private final String reponse;
    private boolean complete;

    public Mot(Position pos, boolean horizontal, String indice, String reponse) {
        this.pos = pos;
        this.horizontal = horizontal;
        this.indice = indice;
        this.reponse=reponse;
        this.complete=false;
    }

    public Position getPos() {
        return pos;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public String getIndice() {
        return indice;
    }

    public String getReponse() {
        return reponse;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
