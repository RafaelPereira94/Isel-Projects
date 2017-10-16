package service;

import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
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
    private Map<String,Event> cacheSingleEvent = new HashMap<>();

    public CompletableFuture<Event> getEvent(String id){
        CompletableFuture<Event> event = new CompletableFuture<>();

        if(cacheSingleEvent.containsKey(id)){
            event.complete(cacheSingleEvent.get(id));
            return event;
        }

        Supplier<Stream<Map<String,Event>>> supplier =()-> cacheEvent.values().stream();

        if(supplier.get().anyMatch(map -> map.containsKey(id))){
            Event evt = supplier.get().filter(map -> map.containsKey(id)).findFirst().get().get(id);
            cacheSingleEvent.put(id,evt);
            event.complete(evt);
        }
        return event;
    }

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
    public CompletableFuture<Track> getTrack(String trackName, String artistName){
        TrackInfo trackInfo = new TrackInfo(trackName,artistName);
        CompletableFuture<Track> tr = new CompletableFuture<>();
        if(cacheTrack.containsKey(trackInfo)){
            tr.complete(cacheTrack.get(trackInfo));
        }
        else{
            tr = super.getTrack(trackName,artistName);
            tr = tr.thenApply(track -> cacheTrack.put(trackInfo,track));
        }
        return tr;
    }

    @Override
    public CompletableFuture<Artist> getArtist(String mbid){
        CompletableFuture<Artist> ar = new CompletableFuture<>();
        if(cacheArtist.containsKey(mbid)){
            ar.complete(cacheArtist.get(mbid));
        }
        else{
            ar = super.getArtist(mbid);
            ar = ar.thenApply(artist -> {
                cacheArtist.put(mbid,artist);
                return artist;
            });
        }
        return ar;
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
