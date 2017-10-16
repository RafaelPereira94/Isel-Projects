package pt.isel.pc.test;

import org.junit.Test;
import pt.isel.pc.main.SynchronousThreadPoolExecutor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class SynchronousThreadPoolExecutorTests {

    private static final int KEEP_ALIVE_TIME = 1_000_000_000;
    private List<Exception> exceptions = new LinkedList<>();

    @Test
    public void testExecute() throws Exception {
        SynchronousThreadPoolExecutor<String> tpl = new SynchronousThreadPoolExecutor<>(2, KEEP_ALIVE_TIME);
        assertEquals("teste1", tpl.execute(() -> "teste1"));
        assertEquals("teste2", tpl.execute(() -> "teste2"));
        assertEquals(0, exceptions.size());
    }

    @Test
    public void testMaxThreadsInside() throws Exception {
        SynchronousThreadPoolExecutor<String> tpl = new SynchronousThreadPoolExecutor<>(2, KEEP_ALIVE_TIME);
        AtomicLong time1 = new AtomicLong(), time2 = new AtomicLong(), time3 = new AtomicLong();
        new Thread(() -> {
            try {
                assertEquals("t1",
                tpl.execute(() -> {
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(KEEP_ALIVE_TIME));
                    time1.set(System.nanoTime());
                    return "t1";
                }));
            } catch (Exception e) {
                exceptions.add(e);
            }
        }).start();

        new Thread(() -> {
            try {
                assertEquals("t2",
                tpl.execute(() -> {
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(KEEP_ALIVE_TIME));
                    time2.set(System.nanoTime());
                    return "t2";
                }));
            } catch (Exception e) {
                exceptions.add(e);
            }
        }).start();

        assertEquals("t3",
        tpl.execute(() -> {
            time3.set(System.nanoTime());
            return "t3";
        }));

        assertTrue(time3.get() > time1.get() || time3.get() > time2.get());
        assertEquals(0, exceptions.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testShutdown() throws Exception {
        SynchronousThreadPoolExecutor<String> tpl = new SynchronousThreadPoolExecutor<>(2, KEEP_ALIVE_TIME);

        final AtomicLong time1 = new AtomicLong(), time2 = new AtomicLong(), time3 = new AtomicLong();

        tpl.execute(() -> "teste1");
        tpl.shutdown();
        tpl.execute(() -> "teste2");

        new Thread(() -> {
            try {
                assertEquals("t1",
                tpl.execute(() -> {
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(KEEP_ALIVE_TIME));
                    time1.set(System.nanoTime());
                    return "t1";
                }));

            } catch (Exception e) {
                exceptions.add(e);
            }
        }).start();

        new Thread(() -> {
            try {
                assertEquals("t2",
                tpl.execute(() -> {
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(KEEP_ALIVE_TIME));
                    time2.set(System.nanoTime());
                    return "t2";
                }));
            } catch (Exception e) {
                exceptions.add(e);
            }
        }).start();

        tpl.shutdown();
        time3.set(System.nanoTime());

        assertTrue(time3.get() > time1.get() && time3.get() > time2.get());
        assertEquals(0, exceptions.size());
    }
}