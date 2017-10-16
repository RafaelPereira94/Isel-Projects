package utils.iterators;

import java.io.UncheckedIOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpliteratorPage<D,R> extends Spliterators.AbstractSpliterator<R> {

    private Function<D,R> mapper; //maps to V the Dto
    private BiFunction<String,Integer,CompletableFuture<D[]>> request; //faz o pedido.
    private int currentPage;
    private final String param; //String passada ao apply do request que pode ser um id para event ou name para Venue
    private int idx = 0;
    private D[] aux;

    public SpliteratorPage(Function<D, R> mapper, BiFunction<String, Integer, CompletableFuture<D[]>> request, String param) {
        super(Long.MAX_VALUE, Spliterator.ORDERED);
        this.mapper = mapper;
        this.request = request;
        currentPage = 1;
        this.param = param;
        aux = request.apply(param,currentPage).join();
    }

    @Override
    public boolean tryAdvance(Consumer<? super R> action) {
        try {
            if(aux == null){
                aux = request.apply(param,currentPage).join();
            }
            if(idx == aux.length){
                ++currentPage;
                idx = 0;
                aux = request.apply(param,currentPage).join();
                return true;
            }
            action.accept(mapper.apply(aux[idx++]));

        }catch (UncheckedIOException | CompletionException ex){
            return false;
        }
        return true;
    }
}