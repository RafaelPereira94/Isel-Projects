package utils.iterators;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IteratorPage<D,R> implements Iterator<R> {

    private Function<D,R> mapper; //maps to V the Dto
    private BiFunction<String,Integer,D[]> request;
    private int currentPage;
    private double totalPage;
    private final String param; //String passada ao apply do request que pode ser um id para event ou name para Venue
    private int idx = 0;
    private D[] aux = null;

    public IteratorPage(Function<D, R> mapper, BiFunction<String, Integer, D[]> request, double totalPage, String param) {
        this.mapper = mapper;
        this.request = request;
        currentPage = 1;
        this.param = param;
        this.totalPage = totalPage;
    }

    @Override
    public boolean hasNext() {
        return currentPage <= totalPage;
    }

    @Override
    public R next() {
        if(hasNext()){ //mais paginas
            if(aux == null)
                aux = request.apply(param,currentPage);

            if(idx == aux.length){
                ++currentPage;
                idx=0;
                if(hasNext())
                    aux = request.apply(param,currentPage);
            }

            return mapper.apply(aux[idx++]);
        }

        return null;
    }
}