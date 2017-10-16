package domain.model;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Venue {

    private final String name;
    private final String id;
    private final Supplier<Stream<Event>> events;

    public Venue(String name, String id, Supplier<Stream<Event>> events) {
        this.name = name;
        this.id = id;
        this.events = events;
    }

    public String getName() {
        return name;
    }

    public Stream<Event> getEvents() {
        return events.get();
    }

    @Override
    public String toString() {
        String res =  " the events on " + this.name + "(id = " + this.id +")" + "are : \n ";

        String[] r = {""};
        events.get().forEach(event ->
            r[0] = r[0].concat(event.toString() + "\n")
         );

        return res + Arrays.toString(r);
    }

    public String getId() {
        return id;
    }
}
