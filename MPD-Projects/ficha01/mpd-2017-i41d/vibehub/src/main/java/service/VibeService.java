package service;

import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;
import domain.data.LfmArtistDto;
import domain.data.EventDto;
import domain.data.TrackDto;
import domain.data.VenueDto;
import utils.iterators.IteratorPage;

import static utils.LazyQueries.*;

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
    public Iterable<Venue> searchVenues(String name){
        return () ->{
            sa.getVenues(name); //primeiro pedido para saber numero de páginas
            return new IteratorPage<>(this::dtoToVenue,sa::getVenues,sa.getTotalPagesResult(),name);
        } ;

        //return () -> map(asList(sa.getVenues(name)), this::dtoToVenue).iterator();
        //return () -> iter;
    }

    protected Venue dtoToVenue(VenueDto dto) {
        return new Venue(
                dto.getName(),
                dto.getId(),
                this.getEvents(dto.getId())
        );
    }

    public Iterable<Event> getEvents(String id){
       return () ->{
           sa.getEvents(id);
           return new IteratorPage<>(this::dtoToEvent,sa::getEvents,sa.getTotalPagesResult(),id);
       } ;
    }

    protected Event dtoToEvent(EventDto dto) {
        return new Event(() -> getArtist(dto.getArtist().getMbid()),
                dto.getArtist().getName(),
                dto.getEventDate(),
                dto.getTour(),
                dto.getTracksPerformed() == null ? null :dto.getTracksPerformed().toArray(new String[dto.getTracksPerformed().size()]),
                map(eventTrackToTrack(dto),this::TrackDtoToTrack),
                dto.getId());
    }

    private Iterable<TrackDto> eventTrackToTrack(EventDto dto) {
        return map(dto.getTracksPerformed(),(str) -> la.getTrackInfo(str,dto.getArtist().getName())); //str equivale ao name do artist
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
        return TrackDtoToTrack(la.getTrackInfo(trackName,artistName));
    }

    private Track TrackDtoToTrack(TrackDto dto) {
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
