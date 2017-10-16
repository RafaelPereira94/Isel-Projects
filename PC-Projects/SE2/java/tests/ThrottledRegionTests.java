import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ThrottledRegionTests {

    private final int MAX_INSIDE = 10;
    private final int MAX_WAITING = 100;
    private final int N_OF_THREADS = 100;

    private final ThrottledRegion_ region = new ThrottledRegion_(MAX_INSIDE, MAX_WAITING, 1 << 30);
    private final AtomicInteger counter = new AtomicInteger();
    private final ConcurrentLinkedQueue<Throwable> exceptions = new ConcurrentLinkedQueue<>();

    private void sleep(int millis){
        try{
            Thread.sleep(millis);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void test() throws Throwable {
        Thread[] ths = new Thread[N_OF_THREADS];
        int[] counters = new int[N_OF_THREADS];
        System.out.printf("Starting\n");
        for(int i = 0 ; i<N_OF_THREADS ; ++i){
            int th = i;
            ths[i] = new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(10);
                        while(!region.TryEnter(1)){
                            System.out.println("sleeping");
                            sleep(10);
                        }
                        counters[th] += 1;
                        int count = counter.incrementAndGet();
                        if(count > MAX_INSIDE) {
                            System.out.printf(">count = %d\n", count);
                        }
                        System.out.printf("-count = %d\n", count);
                        sleep(10);
                        count = counter.decrementAndGet();
                        if(count < 0) {
                            System.out.printf("<count = %d\n", count);
                        }
                        region.Leave(1);
                    }
                }catch(Throwable e){
                    if(e instanceof InterruptedException) {
                        return;
                    }
                    exceptions.add(e);
                }
            });
            ths[i].start();
        }
        Thread.sleep(10000);
        for(int i = 0 ; i<N_OF_THREADS ; ++i){
            ths[i].interrupt();
            ths[i].join();
            System.out.printf("Thread %d ended with %d\n", i, counters[i]);
        }
        for(Throwable t : exceptions){
            throw t;
        }
    }
}
