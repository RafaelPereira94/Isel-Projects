package service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileRequest implements IRequest {

    public static Stream<String> getStream(String path) {
        String[] parts = path.split("/");
        path = parts[parts.length-1]
                .replace('?', '-')
                .replace('&', '-')
                .replace('=', '-')
                .replace(',', '-');
        System.out.println("");

        try {
            URL url = ClassLoader.getSystemResource(path);
            if(url == null) throw  new UncheckedIOException(new IOException());
            InputStream in = url.openStream();
            return StreamSupport.stream(new SpliteratorFromReader(in),false);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CompletableFuture<Stream<String>> getContent(String path) {
        return CompletableFuture.supplyAsync(() ->getStream(path));
    }

    @Override
    public void close() {}
}
