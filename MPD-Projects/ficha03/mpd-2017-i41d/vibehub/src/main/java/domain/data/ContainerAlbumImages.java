package domain.data;

import com.google.gson.annotations.SerializedName;

public class ContainerAlbumImages {

    @SerializedName("#text")
    private String text;

    public ContainerAlbumImages(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
