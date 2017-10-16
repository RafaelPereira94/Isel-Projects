package domain.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventDto {

    @SerializedName("@eventDate")
    private String eventDate;

    @SerializedName("@id")
    private String id;

    //@SerializedName("@versionid")
    //private String versionId;

    @SerializedName("@tour")
    private String tour;

    private ArtistInfoEventDto artist;

    public List<String> getTracksPerformed() {
        return tracksPerformed;
    }

    private List<String> tracksPerformed;

    public void createList(){
        tracksPerformed = new ArrayList<>();
    }

    public void setTrack(String track){
        tracksPerformed.add(track);
    }

    public EventDto(String eventDate, String id, ArtistInfoEventDto artist, String tour) {
        this.eventDate = eventDate;
        this.id = id;
        this.artist = artist;
        this.tour = tour;
    }

    /*public String getVersionId(){
            return versionId;
    }*/

    public String getEventDate() {
        return eventDate;
    }

    public String getId() {
        return id;
    }

    public String getTour() {
        return tour;
    }

    public ArtistInfoEventDto getArtist() {
        return artist;
    }

}
