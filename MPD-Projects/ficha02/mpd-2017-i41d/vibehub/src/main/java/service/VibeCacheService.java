package service;

import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Map<String,Map<String,Venue>> cacheVenue = new HashMap<>();
    private Map<String,Map<String,Event>> cacheEvent = new HashMap<>();
    private Map<TrackInfo,Track> cacheTrack = new HashMap<>();
    private Map<String,Artist> cacheArtist = new HashMap<>();

    @Override
    public Supplier<Stream<Venue>> searchVenues(String name){
        Map<String,Venue> container = getOrCreate(name,cacheVenue);

        if(container.keySet().isEmpty()){ //nao existia aquele valor em cache
            Supplier<Stream<Venue>> entry = super.searchVenues(name);
            Map<String,Venue> aux  = new HashMap<>();
            return ()-> entry
                        .get()
                        .peek(venue ->{
                                aux.put(venue.getId(),venue);
                                cacheVenue.put(name,aux);
                            }
                        );
        }
        return ()-> container.entrySet().stream().map(Map.Entry::getValue);
    }

    @Override
    public Supplier<Stream<Event>> getEvents(String id){
        Map<String,Event> container = getOrCreate(id,cacheEvent);

        if(container.keySet().isEmpty()){ //nao existia aquele valor em cache
            Supplier<Stream<Event>> entry = super.getEvents(id);
            Map<String,Event> aux  = new HashMap<>();
            return ()->entry
                      .get()
                      .peek(event ->{
                             aux.put(event.getListId(),event);
                            cacheEvent.put(id,aux);
                        }
                      );
        }
        return () -> container.entrySet().stream().map(Map.Entry::getValue);
    }

    private static <T> Map<String,T> getOrCreate(String id , Map<String,Map<String,T>> cache){
        return cache.computeIfAbsent(id, k -> new HashMap<>());
    }

    @Override
    public Track getTrack(String trackName,String artistName){
        TrackInfo trackInfo = new TrackInfo(trackName,artistName);
        return cacheTrack
                .computeIfAbsent(trackInfo, k -> super.getTrack(trackName, artistName));
    }

    @Override
    public Artist getArtist(String mbid){
        return cacheArtist
                .computeIfAbsent(mbid, super::getArtist);
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
