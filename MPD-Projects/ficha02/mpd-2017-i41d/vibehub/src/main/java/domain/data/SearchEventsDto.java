package domain.data;

public class SearchEventsDto {

    public SetListsEventDto setlists;

    public SearchEventsDto(SetListsEventDto setlists) {
        this.setlists = setlists;
    }

    public void setSetlists(SetListsEventDto setlists) {
        this.setlists = setlists;
    }

    @Override
    public String toString() {
        return "SearchEventsDto{" +
                "setlists=" + setlists +
                '}';
    }
}
