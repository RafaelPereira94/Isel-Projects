using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Threading;

namespace Tests
{
    [TestClass]
    public class ThrottledRegionTests
    {

        List<Exception> exceptions = new List<Exception>();

        [TestMethod]
        public void Test_MultipleThreadsInside()
        {
            ThrottledRegion tr = new ThrottledRegion(2, 1, Timeout.Infinite);

            Thread t1 = new Thread(() => {
                try
                {
                    Assert.IsTrue(tr.TryEnter(1));
                    Thread.Sleep(150);
                    tr.Leave(1);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });

            t1.Start();
            Assert.IsTrue(tr.TryEnter(1));
            tr.Leave(1);
            t1.Join();

            Assert.AreEqual(0, exceptions.Count);
        }

        [TestMethod]
        public void Test_RegionFull()
        {
            ThrottledRegion tr = new ThrottledRegion(1, 1, Timeout.Infinite);
            bool waiting = false;
            new Thread(() => {
                try
                {
                    Assert.IsTrue(tr.TryEnter(1));
                    waiting = true;
                    tr.Leave(1);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            }).Start();
            while(!waiting);
            int start = Environment.TickCount;
            Assert.IsTrue(tr.TryEnter(1));
            int elapsed = Environment.TickCount - start;
            tr.Leave(1);
            Assert.AreEqual(0, exceptions.Count);
        }

        [TestMethod]
        public void Test_InterruptedThread()
        {
            ThrottledRegion tr = new ThrottledRegion(1, 1, Timeout.Infinite);
            Assert.IsTrue(tr.TryEnter(1));

            Thread t1 = new Thread(() => {
                try
                {
                    tr.TryEnter(1);
                }
                catch (ThreadInterruptedException e)
                {
                    exceptions.Add(e);
                }
            });

            t1.Start();
            t1.Interrupt();
            t1.Join();

            Assert.AreEqual(1, exceptions.Count);
        }
    }
}