package domain.data;

public class SearchVenueDto {

    public final VenuesResultDto venues;

    public SearchVenueDto(VenuesResultDto venues) {
        this.venues = venues;
    }

    @Override
    public String toString() {
       return   "{" +
                "venues=" + venues +
                '}';
    }
}
