using System;
using System.Threading;

class TestUtils
{
    public static bool TestMethodBlocks(Action blockingMethod)
    {
        bool gotBlocked = true;
        Thread takeThread = new Thread(() =>
        {
            try
            {
                blockingMethod();
                gotBlocked = false;
            }
            catch (ThreadInterruptedException)
            {
            }
        });

        takeThread.Start();
        int LOCK_DETECTED_TIMEOUT = 5000;
        Thread.Sleep(LOCK_DETECTED_TIMEOUT);
        takeThread.Interrupt();
        takeThread.Join(LOCK_DETECTED_TIMEOUT);
        return gotBlocked && !takeThread.IsAlive;
    }
}

