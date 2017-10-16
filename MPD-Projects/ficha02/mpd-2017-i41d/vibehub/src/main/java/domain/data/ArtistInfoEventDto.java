package domain.data;

import com.google.gson.annotations.SerializedName;

public class ArtistInfoEventDto {

    @SerializedName("@name")
    private String name;

    @SerializedName("@mbid")
    private String mbid;

    @SerializedName("@sortName")
    private String sortName;

    private String url;

    public ArtistInfoEventDto(String name, String mbid, String sortName, String url) {
        this.name = name;
        this.mbid = mbid;
        this.sortName = sortName;
        this.url = url;
    }

    public ArtistInfoEventDto(String mbid,String name){
        this.mbid = mbid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMbid() {
        return mbid;
    }

    public String getSortName() {
        return sortName;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

