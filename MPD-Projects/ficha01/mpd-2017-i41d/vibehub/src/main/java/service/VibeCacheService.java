package service;

import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;

import java.util.HashMap;
import java.util.Map;

public class VibeCacheService extends VibeService {

    private SetlistApi sa ;
    private LastfmApi la;

    public VibeCacheService(SetlistApi sa, LastfmApi la) {
        super(sa, la);
        this.sa = sa;
        this.la = la;
    }

    public VibeCacheService(IRequest req) {
        super(req);
    }

    /*private Map<String,Iterable<Venue>> cacheVenue = new HashMap<>();
    private Map<String,Iterable<Event>> cacheEvent = new HashMap<>();*/


    private Map<TrackInfo,Track> cacheTrack = new HashMap<>();
    private Map<String,Artist> cacheArtist = new HashMap<>();

    private static <T> Iterable<T> getOrCreate(
            String name,
            Map<String, Iterable<T>> cache)
    {
        return cache.get(name);
    }
    @Override
    public Iterable<Venue> searchVenues(String name){
        /*Iterable<Venue>result = getOrCreate(name,cacheVenue);
        if(result!=null){
            return result;
        }*/
        return super.searchVenues(name);

        /*return ()->{
            Iterable<Venue> venues =super.searchVenues(name);
            cacheVenue.put(name,venues);
            return venues.iterator();
        };*/
    }

    @Override
    public Iterable<Event> getEvents(String id){
        /*Iterable<Event> result = getOrCreate(id,cacheEvent);
        if(result!=null){
            return result;
        }
        return ()->{
            Iterable<Event> events =super.getEvents(id);
            cacheEvent.put(id,events);
            return events.iterator();
        };*/
        return super.getEvents(id);
    }

    @Override
    public Track getTrack(String trackName,String artistName){
        TrackInfo trackInfo = new TrackInfo(trackName,artistName);
        Track result = cacheTrack.get(trackInfo);
        if(result == null){
            result = super.getTrack(trackName,artistName);
            cacheTrack.put(trackInfo,result);
        }
        return result;
    }

    @Override
    public Artist getArtist(String mbid){
        Artist artist = cacheArtist.get(mbid);
        if(artist==null){
            artist = super.getArtist(mbid);
            cacheArtist.put(mbid,artist);
        }
        return artist;
    }


    class TrackInfo {
        final String trackName;
        final String artistName;

        public TrackInfo(String trackName, String artistName) {
            this.trackName = trackName;
            this.artistName = artistName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TrackInfo tr = (TrackInfo) o;
            return tr.artistName.equals(artistName) && tr.trackName.equals(trackName);
        }

        @Override
       public int hashCode() {
            return trackName.hashCode() + artistName.hashCode();
       }

    }

}
