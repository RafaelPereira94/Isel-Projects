package domain.data;

public class TrackDto {

    private ContainerTrackDto track;

    public TrackDto(ContainerTrackDto track) {
        this.track = track;
    }

    public ContainerTrackDto getTrack() {
        return track;
    }
}
