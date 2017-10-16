using System.Collections.Concurrent;
using System.Threading;

public class ThrottledRegion_
{
    /*
     * In .Net the order of the volatiles can change. 
     * In this code we have two volatiles. 
     * So there may be a need to implement barriers.
     */

    public readonly int maxInside, maxWaiting, timeout;

    public ThrottledRegion_(int maxInside, int maxWaiting, int timeout)
    {
        this.maxInside = maxInside;
        this.maxWaiting = maxWaiting;
        this.timeout = timeout;
    }

    private class ThrottledRegionForKey
    {
        ThrottledRegion_ tr;
        public ThrottledRegionForKey(ThrottledRegion_ tr) { this.tr = tr; }

        private volatile int inside = 0, waiting = 0;
        private readonly object lockObj = new object();

        private bool TryAcquire()
        {
            while (true)
            {
                int observed = inside;
                if (observed >= tr.maxInside)
                    return false;
                if (Interlocked.CompareExchange(ref inside, observed + 1, observed) != observed)
                    return true;
            }
        }

        public bool TryEnter()
        {
            if(waiting == 0 && TryAcquire())
                return true;

            Monitor.Enter(lockObj);
            try{
                if (waiting == tr.maxWaiting)
                    return false;
                if (waiting == 1 && TryAcquire())
                    return true;

                Interlocked.Increment(ref waiting);

                bool hasTimedOut;
                while(true)
                {
                    try
                    {
                        hasTimedOut = Monitor.Wait(lockObj, tr.timeout/1000000);
                    }
                    catch (ThreadInterruptedException)
                    {
                        Interlocked.Decrement(ref waiting);
                        throw;
                    }
                    if (!hasTimedOut)
                    {
                        Interlocked.Decrement(ref waiting);
                        return false;
                    }
                }
            }finally
            {
                Monitor.Exit(lockObj);
            }
        }

        public void Leave()
        {
            bool hasDecremented = false;
            if (waiting == 0) //no pulse needed
            {
                Interlocked.Decrement(ref inside);
                Interlocked.MemoryBarrier();
                if (waiting == 0) return;
                hasDecremented = true;
            }
            Monitor.Enter(lockObj);
            try
            {
                if (waiting == 0) //no pulse needed
                {
                    Interlocked.Decrement(ref inside);
                    return;
                }
                if (hasDecremented && !TryAcquire())
                    return;
                Interlocked.Decrement(ref waiting);
                Monitor.Pulse(lockObj);
            }
            finally
            {
                Monitor.Exit(lockObj);
            }
        }

    }

    private readonly ConcurrentDictionary<int, ThrottledRegionForKey> map = new ConcurrentDictionary<int, ThrottledRegionForKey>();

    public bool TryEnter(int key) { return map.GetOrAdd(key, k => new ThrottledRegionForKey(this)).TryEnter(); }

    public void Leave(int key) { map[key].Leave(); }
}
