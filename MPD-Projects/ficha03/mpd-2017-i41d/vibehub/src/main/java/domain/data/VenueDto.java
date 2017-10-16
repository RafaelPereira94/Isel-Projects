package domain.data;

import com.google.gson.annotations.SerializedName;

public class VenueDto {

    @SerializedName("@id")
    private final String id;
    @SerializedName("@name")
    private final String name;

    public VenueDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "VenueDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}