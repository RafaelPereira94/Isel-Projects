package utils.iterators;

import java.util.Iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;


public class IteratorMap<T,R> implements Iterator<R> {

    private Iterator <T>src;
    private Function <T,R> mapper;

    public IteratorMap(Iterator<T> src, Function<T, R> mapper) {
        this.src = src;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return src.hasNext();
    }

    @Override
    public R next() {
        return mapper.apply(src.next());
    }
}

