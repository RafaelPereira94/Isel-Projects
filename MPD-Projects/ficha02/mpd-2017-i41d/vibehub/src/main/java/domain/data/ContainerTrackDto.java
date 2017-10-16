package domain.data;

public class ContainerTrackDto {

    private String name;

    private ContainerArtistDto artist;

    private ContainerAlbumDto album;

    private String url;

    private String duration;

    public ContainerTrackDto(String name, ContainerArtistDto artist, ContainerAlbumDto album, String url, String duration) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.url = url;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public ContainerArtistDto getArtist() {
        return artist;
    }

    public ContainerAlbumDto getAlbum() {
        return album;
    }

    public String getUrl() {
        return url;
    }

    public String getDuration() {
        return duration;
    }

}
