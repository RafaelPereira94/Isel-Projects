package service;

import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;
import domain.data.LfmArtistDto;
import domain.data.EventDto;
import domain.data.TrackDto;
import domain.data.VenueDto;
import utils.iterators.SpliteratorPage;

import java.util.Spliterator;
import java.util.Spliterators;
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
           // sa.getVenues(name); //primeiro pedido para saber numero de páginas
            SpliteratorPage<VenueDto, Venue> iter = new SpliteratorPage<>(this::dtoToVenue,sa::getVenues,name);
            return StreamSupport.stream(iter,false);
        };
        //return () -> map(asList(sa.getVenues(name)), this::dtoToVenue).iterator();
        //return () -> iter;

    }

    private Venue dtoToVenue(VenueDto dto) {
        return new Venue(
                dto.getName(),
                dto.getId(),
                this.getEvents(dto.getId())
        );
    }

    public  Supplier<Stream<Event>> getEvents(String id){
       return () ->{
           SpliteratorPage<EventDto, Event> iter = new SpliteratorPage<>(this::dtoToEvent,sa::getEvents,id);
           return StreamSupport.stream(iter,false);
       } ;
    }

    private Event dtoToEvent(EventDto dto) {
        return new Event(() -> getArtist(dto.getArtist().getMbid()),
                dto.getArtist().getName(),
                dto.getEventDate(),
                dto.getTour(),
                dto.getTracksPerformed() == null ? null :dto.getTracksPerformed().toArray(new String[dto.getTracksPerformed().size()]),
                () -> eventTrackToTrack(dto).map(dtoTrack -> trackDtoToTrack(dtoTrack)),
                dto.getId());
    }

    private Stream<TrackDto> eventTrackToTrack(EventDto dto) {
        Stream<String> stream = dto.getTracksPerformed().stream();
        return stream.map(str -> la.getTrackInfo(str,dto.getArtist().getName()));
    }

    private Artist artistDtoToArtist(LfmArtistDto dto){
        return new Artist(
                dto.getArtist().getName(),
                dto.getArtist().getBio().getContent(),
                dto.getArtist().getUrl(),
                dto.getArtist().getImageUrl(),
                dto.getArtist().getMbid()
        );
    }

    public Artist getArtist(String mbid){
       return artistDtoToArtist(la.getArtistInfo(mbid));
    }

    public Track getTrack(String trackName,String artistName){
        return trackDtoToTrack(la.getTrackInfo(trackName,artistName));
    }

    private Track trackDtoToTrack(TrackDto dto) {
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
    }
}
