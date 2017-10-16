/***
 *
 *  ISEL, LEIC, Programação Concorrente, Inverno 2016/17
 *
 *	Carlos Martins, Pedro Félix
 *
 *  Codigo anexo ao exercício 3 da SE#2
 *
 ***/

import java.util.Random;
import java.util.concurrent.atomic.*;

/*
 * Lock-free dualqueue
 * William N. Scherer III and Michael L. Scott
 */

public class LockFreeDualQueue_<T> {

	private enum Type { DATUM, REQUEST }

	private static class QNode<T> {
		Type type;
		final T data;
		final AtomicReference<QNode<T>> request, next;

		QNode(T data, Type type) {
			this.type = type;
			this.data = data;
			request = new AtomicReference<>(null);
			next = new AtomicReference<>(null);
		}
	}

	private final AtomicReference<QNode<T>> QHead, QTail;

	public LockFreeDualQueue_() {
		QNode<T> sentinel = new QNode<>(null, Type.DATUM);
		QHead = new AtomicReference<>(sentinel);
		QTail = new AtomicReference<>(sentinel);
	}

    public void enqueue(T value) {
        QNode<T> head, tail, request, tailNext, headNext, node = new QNode<>(value, Type.DATUM);
        while(true){
            head = QHead.get();
            tail = QTail.get();
            if ((tail == head || tail.type != Type.REQUEST)){
                tailNext = tail.next.get();
                if(tail == QTail.get()) {
                    if (tailNext != null)
                        QTail.compareAndSet(tail, tailNext);
                    else {
                        if (tail.next.compareAndSet(tailNext, node)) {
                            QTail.compareAndSet(tail, node);
                            return;
                        }
                    }
                }
            }else {
                headNext = head.next.get();
                if (tail == QTail.get()) {
                    request = head.request.get();
                    if (head == QHead.get()) {
                        boolean success = (request == null && head.request.compareAndSet(request, node));
                        QHead.compareAndSet(head, headNext);
                        if (success) return;
                    }
                }
            }
        }
    }

    public T dequeue() throws InterruptedException {
        QNode<T> head, headNext, tail, tailNext, next = null, node = new QNode<>(null, Type.REQUEST);
        while(true){
            head = QHead.get();
            tail = QTail.get();
            if ((tail == head || tail.type == Type.REQUEST)){
                tailNext = tail.next.get();
                if(tail == QTail.get()) {
                    if (tailNext != null)
                        QTail.compareAndSet(tail, tailNext);
                    else {
                        if (tail.next.compareAndSet(tailNext, node)) {
                            QTail.compareAndSet(tail, node);
                            if (head == QHead.get() && head.request.get() != null)
                                QHead.compareAndSet(head, head.next.get());
                            while (tail.request.get() == null){
                                Thread.sleep(0);
                            }
                            head = QHead.get();
                            if (head == tail)
                                QHead.compareAndSet(head, node);
                            return tail.request.get().data;
                        }
                    }
                }
            } else {
                headNext = head.next.get();
                if (tail == QTail.get()) {
                    T result = headNext.data;
                    if (QHead.compareAndSet(head, headNext))
                        return result;
                }
            }
        }
    }

	public boolean isEmpty() {
        QNode<T> tail, tailNext;
        while(true){
            tail = QTail.get();
            if (QHead.get() == tail) {
                if ((tailNext = tail.next.get()) == null && tail == QTail.get())
                    return true;
                QTail.compareAndSet(tail, tailNext);
            }else{
                tail = QTail.get();
                if (tail == QTail.get() && tail.type == Type.REQUEST)
                    return true;
            }
        }
	}

	/*
	 + Test code
	 */

    // Auxiliary methods

    private static void sleepUninterruptibly(long milliseconds) {
        long expiresAt = System.currentTimeMillis() + milliseconds;
        do {
            try {
                Thread.sleep(milliseconds);
                break;
            } catch (InterruptedException ie) {}
            milliseconds = expiresAt - System.currentTimeMillis();
        } while (milliseconds > 0);
    }

    private static boolean joinUninterruptibly(Thread toJoin, long timeout) {
        do {
            try {
                toJoin.join(timeout);
                return !toJoin.isAlive();
            } catch (InterruptedException ie) {}
        } while (true);
    }

    //
    // Test dual queue to drive in a producer/consumer context.
    //

    public static boolean testLockFreeDualQueue() {

        final int CONSUMER_THREADS = 2;
        final int PRODUCER_THREADS = 1;
        final int MAX_PRODUCE_INTERVAL = 25;
        final int MAX_CONSUME_TIME = 25;
        final int FAILURE_PERCENT = 5;
        final int JOIN_TIMEOUT = 100;
        final int RUN_TIME = 10 * 1000;
        final int POLL_INTERVAL = 20;


        Thread[] consumers = new Thread[CONSUMER_THREADS];
        Thread[] producers = new Thread[PRODUCER_THREADS];
        final LockFreeDualQueue_<String> dualQueue = new LockFreeDualQueue_<>();
        final int[] productions = new int[PRODUCER_THREADS];
        final int[] consumptions = new int[CONSUMER_THREADS];
        final int[] failuresInjected = new int[PRODUCER_THREADS];
        final int[] failuresDetected = new int[CONSUMER_THREADS];

        // create and start the consumer threads.
        for (int i = 0; i < CONSUMER_THREADS; i++) {
            final int tid = i;
            consumers[i] = new Thread(() -> {
                Random rnd = new Random(Thread.currentThread().getId());
                int count = 0;

                System.out.printf("-->c#%02d starts...%n", tid);
                do {
                    try {
                        String data = dualQueue.dequeue();
                        if (!data.equals("hello")) {
                            failuresDetected[tid]++;
                            System.out.printf("[f#%d]", tid);
                        }

                        if (++count % 100 == 0)
                            System.out.printf("[c#%02d]", tid);

                        // simulate the time needed to process the data.
                        Thread.sleep(rnd.nextInt(MAX_CONSUME_TIME + 1));

                    } catch (InterruptedException ie) {
                        //do {} while (tid == 0);
                        break;
                    }
                } while (true);

                // display consumer thread's results.
                System.out.printf("%n<--c#%02d exits, consumed: %d, failures: %d",
                        tid, count, failuresDetected[tid]);
                consumptions[tid] = count;
            });
            consumers[i].setDaemon(true);
            consumers[i].start();
        }

        // create and start the producer threads.
        for (int i = 0; i < PRODUCER_THREADS; i++) {
            final int tid = i;
            producers[i] = new Thread( () -> {
                Random rnd = new Random(Thread.currentThread().getId());
                int count = 0;

                System.out.printf("-->p#%02d starts...%n", tid);
                do {
                    String data;

                    if (rnd.nextInt(100) >= FAILURE_PERCENT) {
                        data = "hello";
                    } else {
                        data = "HELLO";
                        failuresInjected[tid]++;
                    }

                    // enqueue a data item
                    dualQueue.enqueue(data);

                    // Increment request count and periodically display the "alive" menssage.
                    if (++count % 100 == 0)
                        System.out.printf("[p#%02d]", tid);

                    // production interval.
                    try {
                        Thread.sleep(rnd.nextInt(MAX_PRODUCE_INTERVAL));
                    } catch (InterruptedException ie) {
                        //do {} while (tid == 0);
                        break;
                    }
                } while (true);
                System.out.printf("%n<--p#%02d exits, produced: %d, failures: %d",
                        tid, count, failuresInjected[tid]);
                productions[tid] = count;
            });
            producers[i].setDaemon(true);
            producers[i].start();
        }

        // run the test RUN_TIME milliseconds
        sleepUninterruptibly(RUN_TIME);

        // interrupt all producer threads and wait for until each one finished.
        int stillRunning = 0;
        for (int i = 0; i < PRODUCER_THREADS; i++) {
            producers[i].interrupt();
            if (!joinUninterruptibly(producers[i], JOIN_TIMEOUT))
                stillRunning++;
        }

        // wait until the queue is empty
        while (!dualQueue.isEmpty()) {
            sleepUninterruptibly(POLL_INTERVAL);
        }

        // Interrupt each consumer thread and wait for a while until each one finished.
        for (int i = 0; i < CONSUMER_THREADS; i++) {
            consumers[i].interrupt();
            if (!joinUninterruptibly(consumers[i], JOIN_TIMEOUT))
                stillRunning++;
        }

        // If any thread failed to fisnish, something is wrong.
        if (stillRunning > 0) {
            System.out.printf("%n<--*** failure: %d thread(s) did answer to interrupt%n", stillRunning);
            return false;
        }

        // Compute and display the results.

        long sumProductions = 0, sumFailuresInjected = 0;
        for (int i = 0; i < PRODUCER_THREADS; i++) {
            sumProductions += productions[i];
            sumFailuresInjected += failuresInjected[i];
        }
        long sumConsumptions = 0, sumFailuresDetected = 0;
        for (int i = 0; i < CONSUMER_THREADS; i++) {
            sumConsumptions += consumptions[i];
            sumFailuresDetected += failuresDetected[i];
        }
        System.out.printf("%n<-- successful: %d/%d, failed: %d/%d%n",
                sumProductions, sumConsumptions, sumFailuresInjected, sumFailuresDetected);

        return sumProductions == sumConsumptions && sumFailuresInjected == sumFailuresDetected;
    }

    public static void main(String[] args) {
        System.out.printf("%n--> Test lock free dual queue: %s%n",
                (testLockFreeDualQueue() ? "passed" : "failed"));
    }
}



