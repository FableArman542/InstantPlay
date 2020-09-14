package code.dam_45414.instantplay.Model;

public class Post {

    private String description;
    private String postid;
    private String postimage;
    private String publisher;
    private Boolean visible;

    public Post (String description, String postid, String postimage, String publisher, Boolean visible) {
        this.description = description;
        this.postid = postid;
        this.postimage = postimage;
        this.publisher = publisher;
        this.visible = visible;
    }

    public Post () { }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
