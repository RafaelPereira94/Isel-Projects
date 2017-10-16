using System;
using System.Collections.Concurrent;
using System.Threading;

public class ThrottledRegionTests
{
    private static readonly int MAX_INSIDE = 10;
    private static readonly int MAX_WAITING = 100;
    private static readonly int N_OF_THREADS = 100;

    private readonly ThrottledRegion_ region = new ThrottledRegion_(MAX_INSIDE, MAX_WAITING, 1 << 30);
    private volatile int counter;
    private readonly ConcurrentQueue<Exception> exceptions = new ConcurrentQueue<Exception>();

    private void sleep(int millis)
    {
        try
        {
            Thread.Sleep(millis);
        }
        catch (ThreadInterruptedException)
        {
            Thread.CurrentThread.Interrupt();
        }
    }

    public void test()
    {
        Thread []
        ths = new Thread[N_OF_THREADS];
        int[] counters = new int[N_OF_THREADS];
        Console.WriteLine("Starting\n");
        for(int i = 0; i<N_OF_THREADS ; ++i){
            int th = i;
            ths[i] = new Thread(() => {
                try {
                    while (true) {
                        Thread.Sleep(10);
                        while(!region.TryEnter(1)){
                            Console.WriteLine("sleeping");
                            sleep(10);
                        }
                        counters[th] += 1;
                        int count = Interlocked.Increment(ref counter);
                        if(count > MAX_INSIDE) {
                            Console.WriteLine(">count = {0}\n", count);
                        }
                        Console.WriteLine("-count = {0}\n", count);
                        sleep(10);
                        count = Interlocked.Decrement(ref counter);
                        if (count< 0) {
                            Console.WriteLine("<count = {0}\n", count);
                        }
                        region.Leave(1);
                    }
                }catch(Exception e){
                    if(e.GetType() == typeof(ThreadInterruptedException)) {
                        return;
                    }
                    exceptions.Enqueue(e);
                }
            });
            ths[i].Start();
        }
        Thread.Sleep(10000);
        for(int i = 0; i<N_OF_THREADS ; ++i){
            ths[i].Interrupt();
            ths[i].Join();
            Console.WriteLine("Thread {0} ended with {1}\n", i, counters[i]);
        }
        foreach(Exception t in exceptions){
            throw t;
        }
    }

    /*public static void Main()
    {
        new ThrottledRegionTests().test();
    }*/
}

