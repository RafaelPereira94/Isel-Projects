package domain.data;

public class ArtistResultDto {

    private String name;

    private String mbid;

    private String url;

    private ContainerImageDto[] image;

    private ContainerBioDto bio;

    public ArtistResultDto(String name, String mbid, String url, ContainerImageDto[] image, ContainerBioDto bio) {
        this.name = name;
        this.mbid = mbid;
        this.url = url;
        this.image = image;
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public String getMbid() {
        return mbid;
    }

    public String getUrl() {
        return url;
    }

    public ContainerImageDto[] getImage() {
        return image;
    }

    public String[] getImageUrl(){
        String[]s = new String[image.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = image[i].getText();
        }
        return s;
    }

    public ContainerBioDto getBio() {
        return bio;
    }

}
