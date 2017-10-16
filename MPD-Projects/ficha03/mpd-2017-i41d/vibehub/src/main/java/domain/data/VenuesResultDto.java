package domain.data;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class VenuesResultDto {

    @SerializedName("@itemsPerPage")
    public final String itemsPerPage;

    @SerializedName("@page")
    public final String page;

    @SerializedName("@total")
    public final String total;

    public final VenueDto[] venue;

    public VenuesResultDto(String itemsPerPage, String page, String total, VenueDto[] venue) {
        this.itemsPerPage = itemsPerPage;
        this.page = page;
        this.total = total;
        this.venue = venue;
    }

    @Override
    public String toString() {
        return Arrays.toString(venue);
    }
}
