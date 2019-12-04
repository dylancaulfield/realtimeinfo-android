package ie.dylancaulfield.realtimeinfo.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TLocation {

    @SerializedName("stopid")
    @Expose
    private String stopid;
    @SerializedName("displaystopid")
    @Expose
    private String displaystopid;
    @SerializedName("shortname")
    @Expose
    private String shortname;
    @SerializedName("shortnamelocalized")
    @Expose
    private String shortnamelocalized;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("fullnamelocalized")
    @Expose
    private String fullnamelocalized;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("lastupdated")
    @Expose
    private String lastupdated;
    @SerializedName("operators")
    @Expose
    private List<Operator> operators = null;

    public String getStopid() {
        return stopid;
    }

    public void setStopid(String stopid) {
        this.stopid = stopid;
    }

    public String getDisplaystopid() {
        return displaystopid;
    }

    public void setDisplaystopid(String displaystopid) {
        this.displaystopid = displaystopid;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getShortnamelocalized() {
        return shortnamelocalized;
    }

    public void setShortnamelocalized(String shortnamelocalized) {
        this.shortnamelocalized = shortnamelocalized;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullnamelocalized() {
        return fullnamelocalized;
    }

    public void setFullnamelocalized(String fullnamelocalized) {
        this.fullnamelocalized = fullnamelocalized;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    public Operator getOperator() {
        return operators.get(0);
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    public String getName() {

        switch (operators.get(0).getOperatortype()) {

            case 1:
                return fullname;

            case 2:
                return displaystopid;

            case 3:
                return fullname.substring(5);

            default:
                return "";
        }

    }

}
