using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Threading;

namespace Tests
{
    [TestClass()]
    public class TestRwSemaphore
    {
        List<Exception> exceptions = new List<Exception>();

        [TestMethod]
        public void Test_RwFlow()
        {
            RwSemaphore s = new RwSemaphore();

            Thread t4 = new Thread(() => {
                try
                {
                    s.DownRead();
                    Assert.IsTrue(s.getWaitingCount() == 1);
                    bool exception = false;
                    try
                    {
                        s.UpWrite();
                    }
                    catch (InvalidOperationException)
                    {
                        exception = true;
                    }
                    Assert.IsTrue(exception);
                    s.UpRead();
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
            Thread t3 = new Thread(() => {
                try
                {
                    s.DownWrite();
                    Assert.IsTrue(s.getWaitingCount() == 1);
                    t4.Start();
                    s.DownWrite();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
            Thread t2 = new Thread(() => {
                try
                {
                    s.DownRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                    t3.Start();
                    s.UpRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
            Thread t1 = new Thread(() => {
                try
                {
                    s.DownRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                    t2.Start();
                    s.UpRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
           
            t1.Start();

            Assert.AreEqual(0, exceptions.Count);
        }

        [TestMethod]
        public void Test_RwFlowWithDowngradeWriter()
        {
            RwSemaphore s = new RwSemaphore();

            Thread t4 = new Thread(() => {
                try
                {
                    s.DownRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                    s.UpRead();
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
            Thread t3 = new Thread(() => {
                try
                {
                    s.DownWrite();
                    Assert.IsTrue(s.getWaitingCount() == 1);
                    t4.Start();
                    s.DowngradeWriter();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
            Thread t2 = new Thread(() => {
                try
                {
                    s.DownRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                    t3.Start();
                    s.UpRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
            Thread t1 = new Thread(() => {
                try
                {
                    s.DownRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                    t2.Start();
                    s.UpRead();
                    Assert.IsTrue(s.getWaitingCount() == 0);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });

            t1.Start();

            Assert.AreEqual(0, exceptions.Count);
        }
    }
}