package web.controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import domain.model.Event;
import domain.model.Track;
import domain.model.Venue;
import service.VibeCacheService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.lang.ClassLoader.getSystemResource;
import static java.nio.file.Files.lines;

public class VibeCtrl {

    private static final Template layoutView,homeView,searchVenuesView,listEventsView,detailEventView,showArtistView,setListView;

    private static final String root;

    private final VibeCacheService service;

    private Map<String,String> eventHashMap = new HashMap<>();

    private Map<String,String> artistHashMap = new HashMap<>();

    private Map<String,String> tracksHashMap= new HashMap<>();

    public VibeCtrl(VibeCacheService service) {
        this.service = service;
    }

    static {
        try{
            Handlebars hbs = new Handlebars();
            root = getSystemResource(".").toURI().getPath();
            layoutView = hbs.compile("/base");
            homeView = hbs.compile("/views/home");
            searchVenuesView = hbs.compile("/views/searchVenues");
            listEventsView = hbs.compile("/views/listEvents");
            showArtistView = hbs.compile("/views/showArtist");
            setListView = hbs.compile("/views/setList");
            detailEventView = hbs.compile("/views/eventDetail");
        }catch (IOException | URISyntaxException e ){
            throw new RuntimeException(e);
        }
    }

    public String home(HttpServletRequest req) {
        try {
            return homeView.apply(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String searchVenues(HttpServletRequest req) {
        Map<String, String[]> queryString = req.getParameterMap();
        String loc = queryString.get("location")[0];
        try {
            List<Venue> venues = service.searchVenues(loc)
                            .get()
                            .collect(Collectors.toList());
            return searchVenuesView.apply(venues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String listEvents(HttpServletRequest req) {
        String[] id = req.getPathInfo().split("/");
        List<Event> events = service.getEvents(id[1])
                            .get()
                            .collect(Collectors.toList());
        try{
            return listEventsView.apply(events);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public String detailEvent(HttpServletRequest req){
        String[] params = req.getPathInfo().split("/");
        Event evt = service.getEvent(params[1]).join();
        String id = evt.getListId();
        if(eventHashMap.get(id)!=null){
            return eventHashMap.get(id);
        }
        String pageWritten = readFile("eventID" + id.replace(" ",""));
        if(pageWritten!= null){
            return pageWritten;
        }
        evt.getArtist().thenApply(artist -> {
            String name = artist.getName();
            try {
                String page = showArtistView.apply(artist);

                String pageName = new StringBuilder()
                .append(name)
                .append("Artist").toString();

                //Se já existe
                if(artistHashMap.putIfAbsent(name, page) != null){ //nao existe no hash
                    writeFile(page,pageName);
                    return artistHashMap.get(name);
                }
                writeFile(page,pageName);
                return page;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> allArtistsDone = CompletableFuture.allOf(evt.getTrack());

        CompletableFuture<List<Track>> listTrack = allArtistsDone.thenApply(v ->
            Arrays.stream(evt.getTrack()).map(CompletableFuture::join).collect(Collectors.toList())
        );
        listTrack.thenApply(tracks -> {
            try {
                String page = setListView.apply(tracks);

                String pageName = new StringBuilder()
                        .append("tracks" + id)
                        .toString();

                //Se já existe
                if(tracksHashMap.putIfAbsent(id, page) != null){ //nao existe no hash
                    writeFile(page,pageName);
                    return tracksHashMap.get(id);
                }
                writeFile(page,pageName);
                return page;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            String page = detailEventView.apply(evt);
            eventHashMap.put(id,page);
            writeFile(page,"eventID" + id);
            return eventHashMap.get(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String showArtist(HttpServletRequest req) {
        String [] params = req.getPathInfo().split("/");
        String artistPage = artistHashMap.get(params[1]);
        if(artistPage!=null){
            return artistPage;
        }
        return readFile(params[1]+"Artist".replace(" ",""));
    }

    public String listingTracks(HttpServletRequest req) {
        String[] id = req.getPathInfo().split("/");
        String trackPage = tracksHashMap.get(id[1]);
        if(trackPage!=null){
            return trackPage;
        }
        return readFile("tracks"+id[1].replace(" ",""));
    }

    private static void writeFile(String content,String fileName){
        String name = root+fileName.replace(" ","") + ".txt";
        try(FileWriter wrt = new FileWriter((new File(name)))){
            wrt.write(content);
            wrt.flush();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private static String readFile (String filePath){
        String name = root+filePath.replace(" ","") + ".txt";
        File file = new File(name);
        URI uri = !file.exists() ? null : file.toURI();
        return uri == null ? null : load(uri);
    }

    private static String load(URI uri) {
        try {
            Path path = Paths.get(uri);
            return lines(path).collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
