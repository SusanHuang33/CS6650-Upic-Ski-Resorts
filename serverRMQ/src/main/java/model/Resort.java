package model;

import java.util.ArrayList;
import java.util.List;

public class Resort {
    private String resortName;
    private Integer resortID;
    private List<String> seasons;

    public Resort(String resortName, Integer resortID) {
        this.resortName = resortName;
        this.resortID = resortID;
        this.seasons = new ArrayList<>();

    }

    public Resort(String resortName, Integer resortID, List<String> seasons) {
        this.resortName = resortName;
        this.resortID = resortID;
        this.seasons = seasons;
    }

    public List<String> getSeasons() {
        return seasons;
    }
}
