using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace APMTracker {
    /// <summary>
    /// This class instances are file tracking servers. They are responsible for accepting 
    /// and managing established TCP connections.
    /// </summary>
    public sealed class Listener {
        /// <summary>
        /// TCP port number in use.
        /// </summary>
        private readonly int portNumber;

        /// <summary>
        /// Max number of concurrent clients.
        /// </summary>
        private readonly int maxClients = 8;
        private volatile int currentClients = 0;
        private volatile int waiting = 0;

        /// <summary> Initiates a tracking server instance.</summary>
        /// <param name="_portNumber"> The TCP port number to be used.</param>
        public Listener(int _portNumber) { portNumber = _portNumber; }

        /// <summary>
        ///	Server's main loop implementation.
        /// </summary>
        /// <param name="log"> The Logger instance to be used.</param>
        public void Run(Logger log) {
            TcpListener srv = null;
            bool terminated = false;
            try {
                srv = new TcpListener(IPAddress.Loopback, portNumber);
                srv.Start();
                log.LogMessage("Listener - Waiting for connection requests.");

                AsyncCallback cb = null;
                currentClients++;
                waiting = 1;
                srv.BeginAcceptTcpClient(cb = ( (ares) => {
                    if(terminated) return;
                    waiting = 0;
                    TcpClient socket = srv.EndAcceptTcpClient(ares);
                    socket.LingerState = new LingerOption(true, 10);
                    log.LogMessage(String.Format("Listener - Connection established with {0}.",
                    socket.Client.RemoteEndPoint));
                    // Instantiating protocol handler and associate it to the current TCP connection
                    Handler protocolHandler = new Handler(socket.GetStream(), log);
                    //Verify if server reached max requests and if no thread is already waiting
                    if(currentClients < maxClients && Interlocked.CompareExchange(ref waiting,1,0) == 0) {
                        currentClients++;
                        log.LogMessage("Listener - Waiting for connection requests.");
                        srv.BeginAcceptTcpClient(cb, null);
                    }
                    // Asynchronously process requests made through de current TCP connection
                    protocolHandler.BeginRun("", GenerateListenerCallback(srv, protocolHandler, cb), null);
                    Program.ShowInfo(Store.Instance);
                } ), null);
                Console.Read();//Press Enter to Terminate
            } finally {
                log.LogMessage("Listener - Ending.");
                terminated = true;
                srv.Stop();
            }
        }

        private AsyncCallback GenerateListenerCallback(TcpListener srv, Handler protocolHandler, AsyncCallback cb) {
            return (res) => {
                protocolHandler.EndRun(res);
                if(Interlocked.CompareExchange(ref waiting, 1, 0) == 0)
                    srv.BeginAcceptTcpClient(cb, null);
                else
                    Interlocked.Decrement(ref currentClients);
            };
        }
    }
}
