using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

namespace TAPTracker {
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
        public async Task RunAsync(Logger log) {
                TcpListener srv = null;
                try {
                    srv = new TcpListener(IPAddress.Loopback, portNumber);
                    srv.Start();
                    log.LogMessage("Listener - Waiting for connection requests.");
                    Program.ShowInfo(Store.Instance);
                    await AcceptAsync(srv, log);
                } finally {
                    log.LogMessage("Listener - Ending.");
                    srv.Stop();
                }
        }

        public async Task AcceptAsync(TcpListener srv, Logger log) {
                if(maxClients > currentClients && Interlocked.CompareExchange(ref waiting, 1, 0) == 0) {
                    using(TcpClient socket = await srv.AcceptTcpClientAsync()) {
                        Interlocked.Increment(ref currentClients);
                        socket.LingerState = new LingerOption(true, 10);
                        log.LogMessage(String.Format("Listener - Connection established with {0}.",
                            socket.Client.RemoteEndPoint));
                        // Instantiating protocol handler and associate it to the current TCP connection
                        Handler protocolHandler = new Handler(socket.GetStream(), log);
                        Task [] ops = new Task [2];
                        waiting = 0;
                        ops [0] = AcceptAsync(srv, log);
                        // Asynchronously process requests made through de current TCP connection
                        ops [1] = protocolHandler.RunAsync().ContinueWith((t) => {
                            Interlocked.Decrement(ref currentClients);
                            return AcceptAsync(srv, log);
                        });
                        await Task.WhenAll(ops);
                    }
                }
        }
    }

}
