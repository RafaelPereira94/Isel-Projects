package utils;

import utils.iterators.*;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class LazyQueries {

    public static <T> Iterable<T> filter(Iterable<T> data, Predicate<T> p) {
        // A lambda corresponde à implementação de Iterable::iterator()
        return () -> new IteratorFilter(data.iterator(), p);
    }

    public static <T, R> Iterable<R> map(Iterable<T> data, Function<T, R> mapper) {

        return () -> new IteratorMap(data.iterator(), mapper);
    }

    public static <T> Iterable<T> distinct(Iterable<T> data) {
        return () -> new IteratorDistinct(data.iterator());
    }

    public static <T> Iterable<T> skip(Iterable<T> data, int nr) {
        return () -> {
            Iterator<T> iter = data.iterator();
            for (int i = 0; i < nr && iter.hasNext(); i++) iter.next();
            return iter;
        };
    }

    public static <T> int count(Iterable<T> data) {
        int size = 0;
        for (T item: data) {
            size++;
        }
        return size;
    }


    public static <T> String join(Iterable<T> src) {
        String res = "";
        for (T item: src) {
            res += item.toString();
        }
        return res;
    }

}
