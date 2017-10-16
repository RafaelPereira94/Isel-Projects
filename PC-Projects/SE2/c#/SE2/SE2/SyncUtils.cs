using System;
using System.Threading;

public class SyncUtils
{
    public static int AdjustTimeout(ref int lastTime, ref int timeout)
    {
        if (timeout != Timeout.Infinite)
        {
            int now = Environment.TickCount;
            int elapsed = (now == lastTime) ? 1 : now - lastTime;
            if (elapsed >= timeout)
            {
                timeout = 0;
            }
            else
            {
                timeout -= elapsed;
                lastTime = now;
            }
        }
        return timeout;
    }

    public static bool EnterWithoutTIE(object mlock)
    {
        bool tie = false;
        while (true)
        {
            try
            {
                Monitor.Enter(mlock);
                return tie;
            }
            catch (ThreadInterruptedException)
            {
                tie = true;
            }
        }
    }

    public static void Notify(object mlock, object mwait)
    {
        if (mlock == mwait)
        {
            Monitor.Pulse(mlock);
            return;
        }
        bool interrupted;
        EnterUninterruptibly(mwait, out interrupted);
        Monitor.Pulse(mwait);
        Monitor.Exit(mwait);
        if (interrupted)
            Thread.CurrentThread.Interrupt();
    }

    public static void Wait(object mlock, object mwait, int timeOut)
    {
        if (mlock == mwait)
        {
            Monitor.Wait(mlock, timeOut);
            return;
        }
        Monitor.Enter(mwait);
        Monitor.Exit(mlock);
        try
        {
            Monitor.Wait(mwait, timeOut);
        }
        finally
        {
            Monitor.Exit(mwait);
            bool interrupted;
            EnterUninterruptibly(mlock, out interrupted);
            if (interrupted)
                throw new ThreadInterruptedException();
        }
    }

    public static void Wait(object mLock, object condition)
    {
        Wait(mLock, condition, Timeout.Infinite);
    }

    public static void EnterUninterruptibly(object mLock, out bool wasInterrupted)
    {
        wasInterrupted = false;

        do
        {
            try
            {
                Monitor.Enter(mLock);
                break;
            }
            catch (ThreadInterruptedException)
            {
                wasInterrupted = true;
            }
        } while (true);
    }
}

