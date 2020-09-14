package code.dam_45414.instantplay.Model;

public class AdItem {

    private String city;
    private String description;
    private String title;
    private String id;


    public AdItem (String city, String description, String title, String id) {
        this.city = city;
        this.description = description;
        this.title = title;
        this.id = id;
    }

    public AdItem () { }

    public String getTitle () { return title; }
    public String getDescription () { return description; }
    public String getCity () { return city; }
    public String getId () { return id; }

    public void SetTitle (String title) { this.title = title; }
    public void SetDescription (String description) { this.description = description; }
    public void SetCity (String city) { this.city = city; }
    public void SetId (String id) { this.id = id; }
}
