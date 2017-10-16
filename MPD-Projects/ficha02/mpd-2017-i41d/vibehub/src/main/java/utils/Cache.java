package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<T,R> {
    public static <T,R> Function<T,R> memoize(Function<T,R> src) {
        final Map<T, R> data = new HashMap<>();
        return key -> {
            R res = data.get(key);
            if(res == null) {
                res = src.apply(key);
                data.put(key, res);
            }
            return res;
        };
    }
}
