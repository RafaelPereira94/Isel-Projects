
package service;

import java.io.InputStream;
import java.util.function.Function;


public class Request implements IRequest {

    final Function<String, InputStream> getStream;

    public Request(Function<String, InputStream> getStream) {
        this.getStream = getStream;
    }

    @Override
    public final Iterable<String> getContent(String path) {
        return () -> new IteratorFromReader(getStream.apply(path));
    }

}
