package web;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import service.*;
import web.controllers.VibeCtrl;
import web.util.HttpServer;

import static java.lang.ClassLoader.getSystemResource;

public class WebApp {

    public static void main(String[] args) throws Exception {

        try(HttpRequest http = new HttpRequest()) {
            VibeCacheService service = new VibeCacheService(new SetlistApi(http),new LastfmApi(http));
            VibeCtrl vibeCtrl = new VibeCtrl(service);
            ServletHolder holderHome = new ServletHolder("static-home", DefaultServlet.class);
            String resPath = getSystemResource("public").toString();
            holderHome.setInitParameter("resourceBase", resPath);
            holderHome.setInitParameter("dirAllowed", "true");
            holderHome.setInitParameter("pathInfoOnly", "true");

            new HttpServer(3000)
                    .addHandler("/home", vibeCtrl::home)
                    .addHandler("/search/venues",vibeCtrl::searchVenues)
                    .addHandler("/events/*",vibeCtrl::listEvents)
                    .addHandler("/events/detail/*",vibeCtrl::detailEvent)
                    .addHandler("/artist/*",vibeCtrl::showArtist)
                    .addHandler("/events/tracks/*",vibeCtrl::listingTracks)
                    .addServletHolder("/public/*", holderHome)
                    .run();
        }
    }
}