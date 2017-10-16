package service;

import java.io.*;
import java.util.Spliterators;
import java.util.function.Consumer;

public class SpliteratorFromReader extends Spliterators.AbstractSpliterator<String> {

     BufferedReader reader;

    public SpliteratorFromReader(InputStream in) {
        super(Long.MAX_VALUE, ORDERED);
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    public boolean tryAdvance(Consumer<? super String> action) {
        try {
            if(reader == null) return false;
            String line = reader.readLine();
            if(line == null) {
                reader.close();
                reader = null;
                return false;
            }
            action.accept(line);
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
