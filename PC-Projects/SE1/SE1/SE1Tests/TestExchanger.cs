using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Threading;

namespace Tests
{
    [TestClass]
    public class ExchangerTests
    {
        private List<Exception> exceptions = new List<Exception>();

        [TestMethod]
        public void Test_SuccessfulTradeBetweenTwoThreads()
        {
            Exchanger<string> exch = new Exchanger<string>();
            Thread t1 = new Thread(() => {
                try
                {
                    Assert.AreEqual("t1", exch.Exchange("t2", Timeout.Infinite));
                }
                catch (Exception e)
                {
                    exceptions.Add(e);
                }
            });
            t1.Start();
            string exchangeRes = exch.Exchange("t1", Timeout.Infinite);
            t1.Join();

            Assert.AreEqual("t2", exchangeRes);
            Assert.AreEqual(0, exceptions.Count);
        }

    }
}