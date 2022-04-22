package model;

import java.util.ArrayList;
import java.util.List;

public class Resorts {
    private List<Resort> resorts = new ArrayList<>();

    public Resorts(List<Resort> resorts) {
        this.resorts = resorts;
    }

    public Resorts(Resort resort) {
        this.resorts.add(resort);
    }

}
