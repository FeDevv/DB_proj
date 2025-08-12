package model.domain;

public enum LevelName {
    ELEMENTARY,
    INTERMEDIATE,
    FIRST,
    ADVANCED,
    PROFICIENCY;

    @Override
    public String toString(){
        switch (this) {
            case ELEMENTARY: return "ELEMENTARY";
            case INTERMEDIATE: return "INTERMEDIATE";
            case FIRST: return "FIRST";
            case ADVANCED: return "ADVANCED";
            case PROFICIENCY: return "PROFICIENCY";
            default: return super.toString();
        }
    }
}


