package ie.dylancaulfield.realtimeinfo.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Operator {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("operatortype")
    @Expose
    private Integer operatortype;
    @SerializedName("routes")
    @Expose
    private List<String> routes = null;

    public String getName() {

        switch (name) {

            case "bac":
                return "Dublin Bus";
            case "ir":
                return "Irishrail";
            case "BE":
                return "Bus Eireann";
            case "LUAS":
                return "Luas";
            default:
                return "";


        }

    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOperatortype() {
        return operatortype;
    }

    public void setOperatortype(Integer operatortype) {
        this.operatortype = operatortype;
    }

    public String getRoutes() {

        String s = routes.get(0);

        for (int i = 1; i < routes.size(); i++) {

            if (i == 6 && routes.size() > 7){

                s = s.concat(" + " + (routes.size() - i) + " more" );

                break;
            }

            s = s.concat(", " + routes.get(i));
        }

        return s;
    }

    public void setRoutes(List<String> routes) {
        this.routes = routes;
    }

}
