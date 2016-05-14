package servlets;


/**
 * @author trierra
 * @date 5/14/16.
 */
public class Person {

    private  Integer id;
    private String title;
    private String locationUrl;
    private String coordinateLong;
    private String coordinateLat;
    private Boolean assistanceProvides;
    private Boolean assistanceRequires;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getCoordinateLong() {
        return coordinateLong;
    }

    public void setCoordinateLong(String coordinateLong) {
        this.coordinateLong = coordinateLong;
    }

    public String getCoordinateLat() {
        return coordinateLat;
    }

    public void setCoordinateLat(String coordinateLat) {
        this.coordinateLat = coordinateLat;
    }

    public Boolean getAssistanceProvides() {
        return assistanceProvides;
    }

    public void setAssistanceProvides(Boolean assistanceProvides) {
        this.assistanceProvides = assistanceProvides;
    }

    public Boolean getAssistanceRequires() {
        return assistanceRequires;
    }

    public void setAssistanceRequires(Boolean assistanceRequires) {
        this.assistanceRequires = assistanceRequires;
    }
}
