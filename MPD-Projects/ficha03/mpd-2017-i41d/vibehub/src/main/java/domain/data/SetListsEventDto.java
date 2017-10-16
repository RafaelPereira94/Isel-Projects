package domain.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SetListsEventDto {

    @SerializedName("@itemsPerPage")
    public String itemsPerPage;

    @SerializedName("@page")
    public String page;

    @SerializedName("@total")
    public String total;

    public transient EventDto[] setlist;

    public SetListsEventDto(String itemsPerPage, String page, String total) {
        this.itemsPerPage = itemsPerPage;
        this.page = page;
        this.total = total;
    }

    public void setSetlist(EventDto[] setlist) {
        this.setlist = setlist;
    }
}


