package domain.data;

import com.google.gson.annotations.SerializedName;

public class ContainerImageDto {

    @SerializedName("#text")
    private String text;

    public ContainerImageDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
