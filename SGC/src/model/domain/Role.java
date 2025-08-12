package model.domain;

public enum Role {
    AMMINISTRATIVO,
    INSEGNANTE,
    ALTRO;


    @Override
    public String toString(){
        switch (this) {
            case AMMINISTRATIVO: return "Amministrativo";
            case INSEGNANTE: return "Insegnante";
            case ALTRO: return "Altro personale";
            default: return super.toString();
        }
    }
}
