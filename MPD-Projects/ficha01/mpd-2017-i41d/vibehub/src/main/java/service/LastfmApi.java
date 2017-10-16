package service;

import com.google.gson.Gson;
import domain.data.LfmArtistDto;
import domain.data.TrackDto;

import java.io.*;
import java.net.URL;

import static utils.LazyQueries.join;

public class LastfmApi {

    //http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&api_key=91e9b1f89cabb3aee5c8f371b8f12b3f&format=json&mbid=e0140a67-e4d1-4f13-8a01-364355bee46e
    private IRequest req;
    private static final String LASTFM_TOKEN;
    private static String HOST = "http://ws.audioscrobbler.com/2.0/";
    private static String ARTIST_METHOD = "?method=artist.getinfo";
    private static String TRACK_METHOD = "method=track.getInfo";
    private static String TRACK_NAME = "track=%s";
    private static String TRACK_ARTIST = "artist=%s";
    private static String API_KEY = "&api_key=%s";
    private static final String FORMAT = "&format=json";
    private static final String MBID = "&mbid=%s";
    private final Gson gson = new Gson();


    public LastfmApi(IRequest req) {
        this.req = req;
    }

    public LastfmApi() {}

    static {
        try {
            URL keyFile = ClassLoader.getSystemResource("lastfm-api-key.txt");
            if(keyFile == null) {
                throw new IllegalStateException(
                        "YOU MUST GOT a KEY in http://www.last.fm/pt/api and place it in src/main/resources/lastfm-api-key");
            } else {
                InputStream keyStream = keyFile.openStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(keyStream))) {
                    LASTFM_TOKEN = reader.readLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public LfmArtistDto getArtistInfo(String mbid){
        String path = HOST+ARTIST_METHOD+String.format(API_KEY,LASTFM_TOKEN)+String.format(MBID,mbid)+FORMAT;
        Iterable<String> content = () -> req.getContent(path).iterator();
        return gson.fromJson(join(content),LfmArtistDto.class);
    }

    public TrackDto getTrackInfo(String musicName,String artistName){
        String path = HOST + "?"+
                TRACK_METHOD+String.format(API_KEY,LASTFM_TOKEN)+
                "&"+String.format(TRACK_NAME,musicName.replace(' ','+'))+
                "&"+String.format(TRACK_ARTIST,artistName.replace(' ','+'))+FORMAT;
        Iterable<String> content = () -> req.getContent(path).iterator();
        return gson.fromJson(join(content),TrackDto.class);
    }
}
