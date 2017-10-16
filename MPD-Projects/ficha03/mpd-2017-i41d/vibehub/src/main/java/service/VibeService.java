package service;

import domain.data.EventDto;
import domain.data.LfmArtistDto;
import domain.data.TrackDto;
import domain.data.VenueDto;
import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;
import utils.iterators.SpliteratorPage;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Responsable to instanciate and link Dtos with domain
 */
public class VibeService {

    private  SetlistApi sa;
    private  LastfmApi la;
    private  IRequest req;

    public VibeService(SetlistApi sa,LastfmApi la) {
        this.sa = sa;
        this.la = la;
    }

    public VibeService(IRequest req) {
        this.req = req;
    }

    //has to iterate over all pages.
    public Supplier<Stream<Venue>> searchVenues(String name){
        return () ->{
            SpliteratorPage<VenueDto, Venue> iter = new SpliteratorPage<>(this::dtoToVenue,sa::getVenues,name);
            return StreamSupport.stream(iter,false);
        };
    }

    public CompletableFuture<Artist> getArtist(String mbid){
        return artistDtoToArtist(la.getArtistInfo(mbid));
    }

    public Supplier<Stream<Event>> getEvents(String id){
       return () ->{
           SpliteratorPage<EventDto, Event> iter = new SpliteratorPage<>(this::dtoToEvent,sa::getEvents,id);
           return StreamSupport.stream(iter,false);
       } ;
    }


    public CompletableFuture<Track> getTrack(String trackName,String artistName){
        return trackDtoToTrack(la.getTrackInfo(trackName,artistName));
    }

    @SuppressWarnings("unchecked")
    private Event dtoToEvent(EventDto dto) {
        return new Event(getArtist(dto.getArtist().getMbid()),
                dto.getArtist().getName(),
                dto.getEventDate(),
                dto.getTour(),
                dto.getTracksPerformed() == null ? null : (String[]) dto.getTracksPerformed().toArray(new String[dto.getTracksPerformed().size()]),
                evtTrackToTrack(dto),//eventTrackToTrack(dto).map(cfDto -> cfDto.thenApply(trkDto -> trackDtoToTrack(cfDto))),
                dto.getId());
    }

    private Stream<CompletableFuture<TrackDto>> eventTrackToTrack(EventDto dto) {
        Stream<String> stream = dto.getTracksPerformed().stream();
        return stream.map(str -> la.getTrackInfo(str,dto.getArtist().getName()));
    }
    private CompletableFuture<Track>[] evtTrackToTrack(EventDto dto){
        Stream<String> stream = dto.getTracksPerformed().stream();
        CompletableFuture[] arr = new CompletableFuture[dto.getTracksPerformed().size()];
        final int count[] = {0};
        stream.forEach(str -> arr[count[0]++] = (trackDtoToTrack(la.getTrackInfo(str,dto.getArtist().getName()))));

        return arr;
    }

    private CompletableFuture<Artist> artistDtoToArtist(CompletableFuture<LfmArtistDto> dtoCompletableFuture){

        return dtoCompletableFuture.thenApply(dto->{
            if(dto==null){
                return new Artist("","","",new String[1],"");
            }
            return new Artist(
                    dto.getArtist().getName()!=null ?  dto.getArtist().getName() : "",
                    dto.getArtist().getBio().getContent()!=null ?  dto.getArtist().getBio().getContent() : "",
                    dto.getArtist().getUrl() != null ?   dto.getArtist().getUrl() : "",
                    dto.getArtist().getImageUrl() != null ?  dto.getArtist().getImageUrl():new String[1] ,
                    dto.getArtist().getMbid() != null ? dto.getArtist().getMbid() : ""
            );
        });
    }

    private Venue dtoToVenue(VenueDto dto) {
        return new Venue(
                dto.getName(),
                dto.getId(),
                this.getEvents(dto.getId())
        );
    }

    private CompletableFuture<Track> trackDtoToTrack(CompletableFuture<TrackDto> dtoCompletableFuture) {
        return dtoCompletableFuture.thenApply(dto -> {
            String albumName = null;
            String[]getAlbumImages = null;
            String albumUrl = null;
            if(dto.getTrack() == null){
                return null;
            }
            if(dto.getTrack().getAlbum() != null){
                albumName = dto.getTrack().getAlbum().getTitle();
                getAlbumImages = dto.getTrack().getAlbum().getAlbumImages();
                albumUrl = dto.getTrack().getAlbum().getUrl();
            }
            return new Track(dto.getTrack().getName(),
                    dto.getTrack().getArtist().getName(),
                    albumName,
                    dto.getTrack().getUrl(),
                    getAlbumImages,
                    albumUrl,
                    Integer.parseInt(dto.getTrack().getDuration()));
        });

    }
}