using System;
using System.Threading;


class Class2
{
    public static void Main()
    {
        new ThrottledRegionTests().test();
        Console.WriteLine("--> Test Michael-Scott concurrent queue: {0}",
                        (testMichaelScottQueue() ? "passed" : "failed"));
    }

    private static void sleepUninterruptibly(int milliseconds)
    {
        int expiresAt = Environment.TickCount + milliseconds;
        do
        {
            try
            {
                Thread.Sleep(milliseconds);
                break;
            }
            catch (ThreadInterruptedException ie) { }
            milliseconds = expiresAt - Environment.TickCount;
        } while (milliseconds > 0);
    }

    private static bool joinUninterruptibly(Thread toJoin, int timeout)
    {
        do
        {
            try
            {
                toJoin.Join(timeout);
                return !toJoin.IsAlive;
            }
            catch (ThreadInterruptedException) { }
        } while (true);
    }

    //
    // Test method.
    //

    public static bool testMichaelScottQueue()
    {

        int CONSUMER_THREADS = 2;
        int PRODUCER_THREADS = 1;
        int MAX_PRODUCE_INTERVAL = 100;
        int MAX_CONSUME_TIME = 25;
        int FAILURE_PERCENT = 5;
        int JOIN_TIMEOUT = 100;
        int RUN_TIME = 5 * 1000;
        int POLL_INTERVAL = 20;


        Thread[] consumers = new Thread[CONSUMER_THREADS];
        Thread[] producers = new Thread[PRODUCER_THREADS];
        ConcurrentQueue_<string> msqueue = new ConcurrentQueue_<String>();
        int[] productions = new int[PRODUCER_THREADS];
        int[] consumptions = new int[CONSUMER_THREADS];
        int[] failuresInjected = new int[PRODUCER_THREADS];
        int[] failuresDetected = new int[CONSUMER_THREADS];

        Console.WriteLine("--> Start test of Michael-Scott queue in producer/consumer context...");

        // create and start the consumer threads.		
        for (int i = 0; i < CONSUMER_THREADS; i++)
        {
            int tid = i;
            consumers[i] = new Thread(() =>
            {
                Random rnd = new Random(Thread.CurrentThread.ManagedThreadId);
                int count = 0;
                Console.WriteLine("-->c#{0} starts...", tid);
                do
                {
                    try
                    {
                        String data = msqueue.dequeue();
                        if (!data.Equals("hello"))
                        {
                            failuresDetected[tid]++;
                            Console.WriteLine("[f#{0}]", tid);
                        }

                        if (++count % 10 == 0)
                            Console.WriteLine("[c#{0}]", tid);

                        // Simulate the time needed to process the data.

                        if (MAX_CONSUME_TIME > 0)
                            Thread.Sleep(rnd.Next(MAX_CONSUME_TIME));
                    }
                    catch (ThreadInterruptedException ie)
                    {
                        break;
                    }
                } while (true);

                // display the consumer thread's results.	

                Console.WriteLine("<--c#{0} exits, consumed: {1}, failures: {2}", tid, count, failuresDetected[tid]);
                consumptions[tid] = count;
            });
            consumers[i].IsBackground = true;
            consumers[i].Start();
        }

        // create and start the producer threads.		
        for (int i = 0; i < PRODUCER_THREADS; i++)
        {
            int tid = i;
            producers[i] = new Thread(() =>
            {
                Random rnd = new Random(Thread.CurrentThread.ManagedThreadId);
                int count = 0;
                Console.WriteLine("-->p#{0} starts...", tid);
                do
                {
                    String data;

                    if (rnd.Next(100) >= FAILURE_PERCENT)
                    {
                        data = "hello";
                    }
                    else
                    {
                        data = "HELLO";
                        failuresInjected[tid]++;
                    }
                    // enqueue a data item
                    msqueue.enqueue(data);
                    // increment request count and periodically display the "alive" menssage.
                    if (++count % 10 == 0)
                        Console.WriteLine("[p#{0}]", tid);
                    // production interval.
                    try
                    {
                        Thread.Sleep(rnd.Next(MAX_PRODUCE_INTERVAL));
                    }
                    catch (ThreadInterruptedException ie)
                    {
                        //do {} while (tid == 0);
                        break;
                    }
                } while (true);
                // display the producer thread's results
                Console.WriteLine("<--p#{0} exits, produced: {1}, failures: {2}", tid, count, failuresInjected[tid]);
                productions[tid] = count;
            });
            producers[i].IsBackground = true;
            producers[i].Start();
        }
        // run the test RUN_TIME milliseconds.
        sleepUninterruptibly(RUN_TIME);
        // interrupt all producer threads and wait for for until each one finished. 
        int stillRunning = 0;
        for (int i = 0; i < PRODUCER_THREADS; i++)
        {
            producers[i].Interrupt();
            if (!joinUninterruptibly(producers[i], JOIN_TIMEOUT))
                stillRunning++;
        }

        // wait until the queue is empty 
        while (!msqueue.isEmpty())
        {
            sleepUninterruptibly(POLL_INTERVAL);
        }

        // interrupt each consumer thread and wait for a while until each one finished.
        for (int i = 0; i < CONSUMER_THREADS; i++)
        {
            consumers[i].Interrupt();
            if (!joinUninterruptibly(consumers[i], JOIN_TIMEOUT))
                stillRunning++;
        }

        // if any thread failed to fisnish, something is wrong.
        if (stillRunning > 0)
        {
            Console.WriteLine("*** failure: {0} thread(s) did answer to interrupt", stillRunning);
            return false;
        }

        // compute and display the results.

        long sumProductions = 0, sumFailuresInjected = 0;
        for (int i = 0; i < PRODUCER_THREADS; i++)
        {
            sumProductions += productions[i];
            sumFailuresInjected += failuresInjected[i];
        }
        long sumConsumptions = 0, sumFailuresDetected = 0;
        for (int i = 0; i < CONSUMER_THREADS; i++)
        {
            sumConsumptions += consumptions[i];
            sumFailuresDetected += failuresDetected[i];
        }
        Console.WriteLine("<-- successful: {0}/{1}, failed: {2}/{3}", sumProductions, sumConsumptions, sumFailuresInjected, sumFailuresDetected);
        return sumProductions == sumConsumptions && sumFailuresInjected == sumFailuresDetected;
    }
}
