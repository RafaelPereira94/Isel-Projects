
package service;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HttpRequest implements IRequest {

    private final AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient();

    @Override
    public CompletableFuture<Stream<String>> getContent(String path) {
        return asyncHttpClient
                .prepareGet(path)
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    if(response.getStatusCode() == 404) throw new UncheckedIOException(new IOException("No page found"));
                    return response.getResponseBodyAsStream();
                })
                .thenApply(inputStream -> StreamSupport.stream(new SpliteratorFromReader(inputStream),false));
    }

    @Override
    public void close() {
        if(!asyncHttpClient.isClosed()){
            try{
                asyncHttpClient.close();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }

    }
}
