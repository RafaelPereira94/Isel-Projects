package domain.model;

public class Venue {

    private final String name;
    private final String id;
    private final Iterable<Event> events;

    public Venue(String name, String id, Iterable<Event> events) {
        this.name = name;
        this.id = id;
        this.events = events;
    }

    public String getName() {
        return name;
    }

    public Iterable<Event> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        String res =  " the events on " + this.name + "(id = " + this.id +")" + "are : \n ";

        for (Event event : events) {
            res = res.concat(event.toString() + "\n");
        }
        return res;
    }

    public String getId() {
        return id;
    }
}
