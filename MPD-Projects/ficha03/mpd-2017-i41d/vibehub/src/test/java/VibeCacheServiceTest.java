import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;
import org.junit.Test;
import service.FileRequest;
import service.LastfmApi;
import service.SetlistApi;
import service.VibeCacheService;
import utils.ICounter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static utils.Countify.of;

public class VibeCacheServiceTest {
    VibeCacheService service;
    @Test
    public void testTrackCache(){
        ICounter<String, CompletableFuture<Stream<String>>> req = of(new FileRequest()::getContent);
        service = new VibeCacheService(new SetlistApi(t -> req.apply(t)), new LastfmApi(t1 -> req.apply(t1)));
        assertEquals(0, req.getCount());
        Track track = service.getTrack("Cheap Honesty","Skunk Anansie").join();
        assertEquals(1,req.getCount());
        Track track2 = service.getTrack("Cheap Honesty","Skunk Anansie").join();
        assertEquals(1,req.getCount());
    }

    @Test
    public void testArtistCache(){
        ICounter<String, CompletableFuture<Stream<String>>> req = of(new FileRequest()::getContent);
        service = new VibeCacheService(new SetlistApi(t -> req.apply(t)), new LastfmApi(t1 -> req.apply(t1)));
        assertEquals(0, req.getCount());
        Artist artist = service.getArtist("9c9f1380-2516-4fc9-a3e6-f9f61941d090").join();
        assertEquals(1, req.getCount());
        Artist artist1 = service.getArtist("9c9f1380-2516-4fc9-a3e6-f9f61941d090").join();
        assertEquals(1,req.getCount());
    }

    @Test
    public void testEventsCache(){
        ICounter<String, CompletableFuture<Stream<String>>> req = of(new FileRequest()::getContent);

        service = new VibeCacheService(new SetlistApi(t -> req.apply(t)), new LastfmApi(t1 -> req.apply(t1)));
        assertEquals(0, req.getCount());
        Supplier<Stream<Event> >events = service.getEvents("33d4a8c9");
        Event event = events.get().findFirst().get();
        assertEquals(3, req.getCount());
        Supplier<Stream<Event>> events2 = service.getEvents("33d4a8c9");
        Event event2 = events2.get().findFirst().get();
        assertEquals(event.toString(), event2.toString());
        assertEquals(3, req.getCount());
    }

    @Test
    public void testVenuesCache(){
        ICounter<String, CompletableFuture<Stream<String>>> req = of(new FileRequest()::getContent);
        service = new VibeCacheService(new SetlistApi(t ->req.apply(t)),new LastfmApi(t1 ->req.apply(t1)));
        assertEquals(0, req.getCount());
        Supplier<Stream<Venue>> venues = service.searchVenues("Aljezur");
        assertEquals(0, req.getCount());
        Stream<Venue> stream = venues.get();
        Venue venue = stream.findFirst().get();
        assertEquals(1,req.getCount());
        Supplier<Stream<Venue>> venues2 = service.searchVenues("Aljezur");
        Stream<Venue> stream2 = venues2.get();
        Venue venue1 = stream2.findFirst().get();
        int count = req.getCount();
        assertEquals(1,count);
    }
}
