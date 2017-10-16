
package service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.function.Function;

/**
 * @author Miguel Gamboa
 *         created on 08-03-2017
 */
public class FileRequest extends Request {
    public FileRequest() {
        super(FileRequest::getStream);
    }

    public static InputStream getStream(String path) {
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
            return url.openStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
