package domain.model;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Event {

    private final CompletableFuture<Artist> artist;

    private final String artistName;
    private final String eventDate;
    private final String tour;
    private final String[] tracksName;
    private final CompletableFuture<Track>[] tracks;
    private final String listId;

    public Event(CompletableFuture<Artist> artist, String artistName, String eventDate, String tour, String[] tracksName, CompletableFuture<Track>[] tracks, String listId) {
        this.artist = artist;
        this.artistName = artistName;
        this.eventDate = eventDate;
        this.tour = tour;
        this.tracksName = tracksName;
        this.tracks = tracks;
        this.listId = listId;
    }

    public CompletableFuture<Artist> getArtist() {
        return artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getTour() {
        return tour;
    }

    public String[] getTracksName() {
        return tracksName;
    }

    public CompletableFuture<Track>[] getTrack(){
        return tracks;
    }

    public Track[] getTracks() {
        List<Track>list = Arrays.stream(tracks)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return list.toArray(new Track[list.size()]);
    }

    public String getListId() {
        return listId;
    }



    @Override
    public String toString() {
        int res = 0;
        if(this.tracksName != null) res = this.tracksName.length;
        return this.artistName + " played " + res + " songs at a show on " +
                this.eventDate + " during the " + this.tour  + " and the songs are : " + Arrays.toString(tracksName);
    }
}
