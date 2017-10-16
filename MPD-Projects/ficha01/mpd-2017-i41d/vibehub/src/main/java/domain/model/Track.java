package domain.model;

public class Track {

    private final String name;
    private final String artistName;
    private final String albumName;
    private final String trackUrl;
    private final String[] imagesUrl;
    private final String albumUrl;
    private final int duration;

    public Track(String name, String artistName, String albumName, String trackUrl, String[] imagesUrl, String albumUrl, int duration) {
        this.name = name;
        this.artistName = artistName;
        this.albumName = albumName;
        this.trackUrl = trackUrl;
        this.imagesUrl = imagesUrl;
        this.albumUrl = albumUrl;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public String[] getImagesUrl() {
        return imagesUrl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return this.name + "("+ this.duration+") by " + this.artistName + " on the album " + this.albumName
                + ". The album url is " + this.albumUrl + " and the track url is " + this.trackUrl;
    }
}
