package domain.model;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Event {

    private final Supplier<Artist> artist;

    private final String artistName;
    private final String eventDate;
    private final String tour;
    private final String[] tracksName;
    private final Supplier<Stream<Track>> tracks;
    private final String listId;

    public Event(Supplier<Artist> artist, String artistName, String eventDate, String tour, String[] tracksName, Supplier<Stream<Track>> tracks, String listId) {
        this.artist = artist;
        this.artistName = artistName;
        this.eventDate = eventDate;
        this.tour = tour;
        this.tracksName = tracksName;
        this.tracks = tracks;
        this.listId = listId;
    }

    public Artist getArtist() {
        return artist.get();
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

    public Stream<Track> getTracks() {
        return tracks.get();
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
