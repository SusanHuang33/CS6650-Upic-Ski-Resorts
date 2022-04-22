package model;


public class SeasonVertical {

    private String seasonID;
    private Integer totalVertical;



    public SeasonVertical(String season, Integer vertical) {
        this.totalVertical = vertical;
        this.seasonID = season;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public Integer getTotalVertical() {
        return totalVertical;
    }

//    @Override
//    public String toString() {
//        return gson.toJson(this, SeasonVertical.class);
//
//    }


//    @Override
//    public String toString() {
//        return "seasonID:" + seasonID +
//                ", \ntotalVertical=" + totalVertical +
//                '}';
//    }
}
