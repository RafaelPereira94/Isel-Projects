import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;
import org.junit.Before;
import org.junit.Test;
import service.*;
import utils.Cache;
import utils.Countify;
import utils.ICounter;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class VibeCacheServiceTest {
    VibeCacheService service;

    @Test
    public void testTrackCache(){
        ICounter<String, Iterable<String>> req = Countify.of(new FileRequest()::getContent);
        service = new VibeCacheService(new SetlistApi(req::apply), new LastfmApi(req::apply));
        assertEquals(0, req.getCount());
        Track track = service.getTrack("Cheap Honesty","Skunk Anansie");
        assertEquals(1,req.getCount());
        Track track2 = service.getTrack("Cheap Honesty","Skunk Anansie");
        assertEquals(1,req.getCount());

    }
    @Test
    public void testArtistCache(){
        ICounter<String, Iterable<String>> req = Countify.of(new FileRequest()::getContent);
        service = new VibeCacheService(new SetlistApi(req::apply), new LastfmApi(req::apply));
        assertEquals(0, req.getCount());
        Artist artist = service.getArtist("9c9f1380-2516-4fc9-a3e6-f9f61941d090");
        assertEquals(1, req.getCount());
        Artist artist1 = service.getArtist("9c9f1380-2516-4fc9-a3e6-f9f61941d090");
        assertEquals(1,req.getCount());

    }
    @Test
    public void testEventsCache(){
        ICounter<String, Iterable<String>> req = Countify.of(new FileRequest()::getContent);
        Function<String,Iterable<String>> cache = Cache.memoize(req);

        service = new VibeCacheService(new SetlistApi(cache::apply), new LastfmApi(cache::apply));
        assertEquals(0, req.getCount());
        Iterable<Event> events = service.getEvents("33d4a8c9");
         Event event = events.iterator().next();
        assertEquals(1, req.getCount());
        Iterable<Event> events2 = this.service.getEvents("33d4a8c9");
        Event event2 = events.iterator().next();
        assertEquals(event.toString(), event2.toString());
        assertEquals(1, req.getCount());
    }

    @Test
    public void testVenuesCache(){
        ICounter<String, Iterable<String>> req = Countify.of(new FileRequest()::getContent);
        Function<String,Iterable<String>> cache = Cache.memoize(req);

        service = new VibeCacheService(new SetlistApi(cache::apply), new LastfmApi(cache::apply));
        assertEquals(0, req.getCount());
        Iterable<Venue> venues = service.searchVenues("Lisbon");
        Venue venue = venues.iterator().next();
        assertEquals(1, req.getCount());
        Iterable<Venue> venues2 = service.searchVenues("Lisbon");
        Venue venue2 = venues2.iterator().next();
        assertEquals(1, req.getCount());
    }

}
