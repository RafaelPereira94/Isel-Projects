using System;
using System.Threading;

namespace AsyncUtils {
    // Generic IAsyncResult implementation 

    public class GenericAsyncResult<R> : IAsyncResult {

        private volatile bool isCompleted;
        private volatile ManualResetEvent waitEvent;
        private readonly AsyncCallback userCallback;
        private readonly object userState;
        private R result;
        private Exception error;


        public GenericAsyncResult(AsyncCallback userCallback, object userState) {
            this.userCallback = userCallback;
            this.userState = userState;
        }

        //
        // Complete the underlying asynchronous operation.
        //

        public void OnComplete(R result, Exception error) {
            this.result = result;
            this.error = error;
            isCompleted = true;

            Thread.MemoryBarrier();     // Prevent the release followed by acquire hazard! 

            if(waitEvent != null) {
                try {
                    waitEvent.Set();
                    // We can get ObjectDisposedExcption due a benign race, so ignore it!
                } catch(ObjectDisposedException) { }
            }
            if(userCallback != null)
                userCallback(this);
        }

        //
        // Return the asynchronous operation's result (called once by EndXxx())
        //

#pragma warning disable 420

        public R Result {
            get {
                if(!isCompleted) {
                    AsyncWaitHandle.WaitOne();
                }
                if(waitEvent != null)
                    waitEvent.Close();
                if(error != null)
                    throw error;
                return result;
            }
        }

        //
        // IAsyncResult interface's implementation.
        //

        public bool IsCompleted { get { return isCompleted; } }

        //
        // This implementation never completes synchronously.
        //

        public bool CompletedSynchronously { get { return false; } }

        public Object AsyncState { get { return userState; } }

        public WaitHandle AsyncWaitHandle {
            get {
                if(waitEvent == null) {
                    bool s = isCompleted;
                    ManualResetEvent done = new ManualResetEvent(s);
                    if(Interlocked.CompareExchange(ref waitEvent, done, null) == null) {
                        if(s != isCompleted) {
                            done.Set();
                        }
                    } else {
                        done.Dispose();
                    }
                }
                return waitEvent;
            }
        }
#pragma warning restore 420
    }
}
