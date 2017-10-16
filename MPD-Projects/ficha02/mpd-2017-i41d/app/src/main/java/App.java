import domain.model.Artist;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;
import service.*;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.System.out;


public class App {

    public static void main(String[] args) {
        VibeCacheService serv = new VibeCacheService(new SetlistApi(new HttpRequest()),new LastfmApi(new HttpRequest()));
        /*Iterable<Event> it = serv.getEvents("33d4a8c9");
        for (Event evt : it) {
            Track tr = evt.getTracks().iterator().next();
            int x = 1;
        }*/



        Supplier<Stream<Venue>> venues = serv.searchVenues("Lisbon");
        venues.get().forEach(out::println);
        /*Track tr = serv.getTrack("Uprising","Muse");
       // Artist art = serv.getArtist("bfcc6d75-a6a5-4bc6-8282-47aec8531818");
        //Artist sec = serv.getArtist("bfcc6d75-a6a5-4bc6-8282-47aec8531818");*/
        //iterable.forEach(out::println);
       // serv.getEvents("33d4a8c9");

        //serv.getTrack("Cheap Honesty","Skunk Anansie");
    }

}

