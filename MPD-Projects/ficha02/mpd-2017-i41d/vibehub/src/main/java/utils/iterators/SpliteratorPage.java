package utils.iterators;

import java.io.UncheckedIOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpliteratorPage<D,R> extends Spliterators.AbstractSpliterator<R> {

    private Function<D,R> mapper; //maps to V the Dto
    private BiFunction<String,Integer,D[]> request;
    private int currentPage;
    private final String param; //String passada ao apply do request que pode ser um id para event ou name para Venue
    private int idx = 0;
    private D[] aux;

    public SpliteratorPage(Function<D, R> mapper, BiFunction<String, Integer, D[]> request,String param) {
        super(Long.MAX_VALUE, Spliterator.ORDERED);
        this.mapper = mapper;
        this.request = request;
        currentPage = 1;
        this.param = param;
    }

    @Override
    public boolean tryAdvance(Consumer<? super R> action) {
        try {
            if(aux == null){
                aux = request.apply(param,currentPage);
            }
            if(idx == aux.length){
                ++currentPage;
                idx = 0;
                aux = request.apply(param,currentPage);
                return true;
            }
            action.accept(mapper.apply(aux[idx++]));

        }catch (UncheckedIOException e){
            return false;
        }
        return true;
    }
}