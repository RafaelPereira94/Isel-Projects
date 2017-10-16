using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Threading;

namespace Tests
{
    [TestClass]
    public class RetryLazyTests
    {

        List<Exception> exceptions = new List<Exception>();

        [TestMethod]
        public void Test_OneTry()
        {
            object obj = "test1";
            RetryLazy<object> sync = new RetryLazy<object>(() => obj, 1);
            Assert.AreSame(obj, sync.Value);
        }

        [TestMethod]
        public void Test_SeveralTries()
        {
            object obj = "test1";
            int aux = 0;
            RetryLazy<object> sync = new RetryLazy<object>(() =>
            {
                if(aux++ == 0)
                    return obj;
                return "";
            }, 4);

            Thread t = new Thread(() => {
                try
                {
                    Assert.AreSame(obj, sync.Value);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
            Thread t1 = new Thread(() => {
                try
                {
                    Assert.AreSame(obj, sync.Value);
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });

            Assert.AreSame(obj, sync.Value);

            t.Start();
            t1.Start();
            t.Join();

            Assert.AreEqual(0, exceptions.Count);
        }
    }
}