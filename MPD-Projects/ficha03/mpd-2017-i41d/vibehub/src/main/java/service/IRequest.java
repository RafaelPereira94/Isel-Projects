package service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface IRequest extends AutoCloseable{

    CompletableFuture<Stream<String>> getContent(String path);

    @Override
    default void close(){}
}
