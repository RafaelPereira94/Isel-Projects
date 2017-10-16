package domain.data;

public class ContainerAlbumDto {

    private String title;

    private String url;

    private ContainerAlbumImages[] image;

    public ContainerAlbumDto(String title, String url, ContainerAlbumImages[] image) {
        this.title = title;
        this.url = url;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public ContainerAlbumImages[] getImage() {
        return image;
    }

    public String[] getAlbumImages(){
        String[]s = new String[image.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = image[i].getText();
        }
        return s;
    }
}
