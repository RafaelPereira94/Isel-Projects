
package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class IteratorFromReader implements Iterator<String> {

    final BufferedReader reader;
    final InputStream in;
    private String nextLine;

    public IteratorFromReader(InputStream in) {
        this.in = in;
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.nextLine = moveNext();
    }

    private String moveNext() {
        try {
            String line = reader.readLine();
            if(line == null) {
                in.close();
                reader.close();
            }
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    @Override
    public String next() {
        String curr = nextLine;
        nextLine = moveNext();
        return curr;
    }
}
