package domain.data;

public class LfmArtistDto {

    private ArtistResultDto artist;

    public LfmArtistDto(ArtistResultDto artist) {
        this.artist = artist;
    }

    public ArtistResultDto getArtist() {
        return artist;
    }
}
