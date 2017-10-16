/***
 *
 *  ISEL, LEIC, Programação Concorrente, Inverno 2016/17
 *
 *	Carlos Martins, Pedro Felix
 *
 *  Codigo anexo ao exercício 2 da SE#2
 * 
 ***/

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThrottledRegion_ {

    private final int maxInside, maxWaiting, timeout;

    ThrottledRegion_(int maxInside, int maxWaiting, int timeout){
        this.maxInside = maxInside;
        this.maxWaiting = maxWaiting;
        this.timeout = timeout;
    }

    private class ThrottledRegionForKey{
        private final AtomicInteger inside = new AtomicInteger(0);
        private volatile int waiting = 0;

        private final Lock lock = new ReentrantLock();
        final Condition leaving = lock.newCondition();

        private boolean TryAcquire(){
            int observed;
            while(true){
                if((observed = inside.get()) >= maxInside)
                    return false;
                if(inside.compareAndSet(observed, observed + 1))
                    return true;
            }
        }

        public boolean TryEnter() throws InterruptedException {
            if(waiting == 0 && TryAcquire())
                return true;
            lock.lock();
            try{
                if(waiting == maxWaiting)
                    return false;

                if(waiting == 1 && TryAcquire()) {
                    return true;
                }
                waiting += 1;
                long tm = timeout;
                while(true){
                    try{
                        tm = leaving.awaitNanos(tm);
                    }catch(InterruptedException e){
                        waiting -= 1;
                        throw e;
                    }
                    if(tm <= 0){
                        waiting -= 1;
                        return false;
                    }
                }
            }finally{
                lock.unlock();
            }
        }

        public void Leave(){
            boolean hasDecremented = false;
            if(waiting == 0){
                inside.decrementAndGet();
                if(waiting == 0) return;
                hasDecremented = true;
            }
            lock.lock();
            try{
                if(waiting == 0) {
                    inside.decrementAndGet();
                    return;
                }
                if(hasDecremented && !TryAcquire())
                    return;
                waiting -= 1;
                leaving.signal();
            }finally{
                lock.unlock();
            }
        }
    }

    private final ConcurrentMap<Integer, ThrottledRegionForKey> map = new ConcurrentHashMap<>();

    public boolean TryEnter(int key) throws InterruptedException { return map.computeIfAbsent(key, k -> new ThrottledRegionForKey()).TryEnter(); }
    public void Leave(int key){ map.get(key).Leave(); }
}
