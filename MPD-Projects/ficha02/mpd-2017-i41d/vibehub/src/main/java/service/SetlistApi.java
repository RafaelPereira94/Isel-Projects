package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import domain.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetlistApi {

    private final String HOST = "https://api.setlist.fm/rest/0.1";
    private final String SEARCH_VENUES = "/search/venues.json";
    private final String SEARCH_EVENTS = "/venue/%s/setlists.json";
    private final String QUERY = "?cityName=%s";
    private final String PAGING_PARAM = "p=%s"; //paging
    private final Gson gson = new Gson();
    private final IRequest req;

    public SetlistApi(IRequest req) {
        this.req = req;
    }

    //gets the venus of first page
    public VenueDto[] getVenues(String cityName){
        return getVenues(cityName,1);
    }

    // gets the venues from specific page
    public VenueDto[] getVenues(String cityName,int page){
        String path = HOST+ SEARCH_VENUES+String.format(QUERY,cityName)+String.format("&"+PAGING_PARAM,page);
        Supplier<Stream<String>> content = () -> req.getContent(path);
        SearchVenueDto dto = gson.fromJson(join(content.get()),SearchVenueDto.class);
        if(dto==null){
            return new VenueDto[]{new VenueDto("","")};
        }
        return dto.venues.venue;
    }

    private String join(Stream<String> content) {
        StringBuilder sb = new StringBuilder();
        content.forEach(sb::append);
        return sb.toString();
    }

    //gets the events of the venue id the input id
    public EventDto[] getEvents(String id){
        return getEvents(id,1);
    }

    public EventDto[] getEvents(String id, int page){
        String path = HOST+String.format(SEARCH_EVENTS,id)+"?"+String.format(PAGING_PARAM,page);
        List<String> content = req.getContent(path).collect(Collectors.toList());

        SearchEventsDto dto = gson.fromJson(join(content.stream()),SearchEventsDto.class);
         if(dto == null){
            return new EventDto[]{new EventDto("","",new ArtistInfoEventDto("","","",""),"")};
        }

        JsonElement song = gson.fromJson(join(content.stream()),JsonElement.class);
        JsonObject set = song.getAsJsonObject().getAsJsonObject("setlists");

        JsonElement setListAux = set.get("setlist");

        JsonArray setlist = new JsonArray();

        if(setListAux.isJsonArray())
            setlist = set.getAsJsonArray("setlist").getAsJsonArray();
        else setlist.add(setListAux); //setlist é um objecto

        List<EventDto> list = new ArrayList<>();

        int idx = 0;
        for (JsonElement j : setlist) {
            idx = manualDeserializer(list, idx, j);
        }

        dto.setlists.setSetlist(list.toArray(new EventDto[list.size()]));
        return dto.setlists.setlist;
    }

    private int manualDeserializer(List<EventDto> list, int idx, JsonElement j) {
        String tour = "";
        JsonObject setAux = j.getAsJsonObject();

        if(setAux.get("@tour") != null)
            tour = setAux.get("@tour").getAsString();

        JsonElement mbid = setAux.get("artist").getAsJsonObject().get("@mbid");
        JsonElement name =  setAux.get("artist").getAsJsonObject().get("@name");

        list.add(new EventDto(setAux.get("@eventDate").getAsString(),
                setAux.get("@id").getAsString(),
                new ArtistInfoEventDto(mbid == null ? null : mbid.getAsString(),name == null ? null : name.getAsString()),
                tour));

        list.get(idx).createList();

        if(j.getAsJsonObject().get("sets").isJsonPrimitive()){ //se sets for sets:" " ->vazio adiciona na lista um event vazio e avança com o foreach de cima.
            list.get(idx++).setTrack("");
            return idx;
        }
        JsonElement aux = j.getAsJsonObject().get("sets").getAsJsonObject().get("set");
        //so tem song
        if(aux.isJsonObject()){
           JsonElement je = aux.getAsJsonObject().get("song");
            extractSongs(idx, list, je);
        }
        else if(aux.isJsonArray()){
            JsonElement je = aux.getAsJsonArray().get(0).getAsJsonObject().get("song");
            extractSongs(idx, list, je);
        }
        ++idx;
        return idx;
    }

    private void extractSongs(int idx, List<EventDto> list, JsonElement je) {
        if(je.isJsonArray()){
            JsonArray arr = je.getAsJsonArray();
            for (JsonElement json : arr){
                list.get(idx).setTrack(json.getAsJsonObject().get("@name").getAsString());
            }
        }else{
            JsonObject obj = je.getAsJsonObject();
            list.get(idx).setTrack( obj.get("@name").getAsString());
        }
    }
}
