/*
 * INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA
 * Licenciatura em Engenharia Informática e de Computadores
 *
 * Programação Concorrente - Inverno de 2009-2010
 * Paulo Pereira
 *
 * Código base para a 3ª Série de Exercícios.
 *
 */

using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;

namespace Tracker
{
    /// <summary>
    /// Handles client requests.
    /// </summary>
    public sealed class Handler
    {
        #region Message handlers

        /// <summary>
        /// Data structure that supports message processing dispatch.
        /// </summary>
        private static readonly Dictionary<string, Action<StreamReader, StreamWriter, Logger>> MESSAGE_HANDLERS;

        static Handler()
        {
            MESSAGE_HANDLERS = new Dictionary<string, Action<StreamReader, StreamWriter, Logger>>();
            MESSAGE_HANDLERS["REGISTER"] = ProcessRegisterMessage;
            MESSAGE_HANDLERS["UNREGISTER"] = ProcessUnregisterMessage;
            MESSAGE_HANDLERS["LIST_FILES"] = ProcessListFilesMessage;
            MESSAGE_HANDLERS["LIST_LOCATIONS"] = ProcessListLocationsMessage;
        }

        /// <summary>
        /// Handles REGISTER messages.
        /// </summary>
        private static void ProcessRegisterMessage(StreamReader input, StreamWriter output, Logger log)
        {
            // Read message payload, terminated by an empty line. 
            // Each payload line has the following format
            // <filename>:<ipAddress>:<portNumber>
            string line;
            while ((line = input.ReadLine()) != null && line != string.Empty)
            {
                string[] triple = line.Split(':');
                if (triple.Length != 3)
                {
                    log.LogMessage("Handler - Invalid REGISTER message.");
                    return;
                }
                IPAddress ipAddress = IPAddress.Parse(triple[1]);
                ushort port;
                if(!ushort.TryParse(triple[2], out port))
                {
                    log.LogMessage("Handler - Invalid REGISTER message.");
                    return;
                }
                Store.Instance.Register(triple[0], new IPEndPoint(ipAddress, port));
            }

            // This request message does not have a corresponding response message, hence, 
            // nothing is sent to the client.
        }

        /// <summary>
        /// Handles UNREGISTER messages.
        /// </summary>
        private static void ProcessUnregisterMessage(StreamReader input, StreamWriter output, Logger log)
        {
            // Read message payload, terminated by an empty line. 
            // Each payload line has the following format
            // <filename>:<ipAddress>:<portNumber>
            string line;
            while ((line = input.ReadLine()) != null && line != string.Empty)
            {
                string[] triple = line.Split(':');
                if (triple.Length != 3)
                {
                    log.LogMessage("Handler - Invalid UNREGISTER message.");
                    return;
                }
                IPAddress ipAddress = IPAddress.Parse(triple[1]);
                ushort port;
                if (!ushort.TryParse(triple[2], out port))
                {
                    log.LogMessage("Handler - Invalid UNREGISTER message.");
                    return;
                }
                Store.Instance.Unregister(triple[0], new IPEndPoint(ipAddress, port));
            }

            // This request message does not have a corresponding response message, hence, 
            // nothing is sent to the client.
        }

        /// <summary>
        /// Handles LIST_FILES messages.
        /// </summary>
        private static void ProcessListFilesMessage(StreamReader input, StreamWriter output, Logger log)
        {
            // Request message does not have a payload.
            // Read end message mark (empty line)
            input.ReadLine();

            string[] trackedFiles = Store.Instance.GetTrackedFiles();

            // Send response message. 
            // The message is composed of multiple lines and is terminated by an empty one.
            // Each line contains a name of a tracked file.
            foreach (string file in trackedFiles)
                output.WriteLine(file);

            // End response and flush it.
            output.WriteLine();
            output.Flush();
        }

        /// <summary>
        /// Handles LIST_LOCATIONS messages.
        /// </summary>
        private static void ProcessListLocationsMessage(StreamReader input, StreamWriter output, Logger log)
        {
            // Request message payload is composed of a single line containing the file name.
            // The end of the message's payload is marked with an empty line
            string line = input.ReadLine();
            input.ReadLine();

            IPEndPoint[] fileLocations = Store.Instance.GetFileLocations(line);

            // Send response message. 
            // The message is composed of multiple lines and is terminated by an empty one.
            // Each line has the following format
            // <ipAddress>:<portNumber>
            foreach (IPEndPoint endpoint in fileLocations)
                output.WriteLine(string.Format("{0}:{1}", endpoint.Address, endpoint.Port));

            // End response and flush it.
            output.WriteLine();
            output.Flush();
        }
        #endregion


        /// <summary>
        /// The handler's input (from the TCP connection)
        /// </summary>
        private readonly StreamReader input;

        /// <summary>
        /// The handler's output (to the TCP connection)
        /// </summary>
        private readonly StreamWriter output;

        /// <summary>
        /// The Logger instance to be used.
        /// </summary>
        private readonly Logger log;

        /// <summary>
        ///	Initiates an instance with the given parameters.
        /// </summary>
        /// <param name="connection">The TCP connection to be used.</param>
        /// <param name="log">the Logger instance to be used.</param>
        public Handler(Stream connection, Logger log)
        {
            this.log = log;
            output = new StreamWriter(connection);
            input = new StreamReader(connection);
        }

        /// <summary>
        /// Performs request servicing.
        /// </summary>
        public void Run()
        {
            try
            {
                string requestType;
                // Read request type (the request's first line)
                while ((requestType = input.ReadLine()) != null && requestType != string.Empty)
                {
                    requestType = requestType.ToUpper();
                    if (!MESSAGE_HANDLERS.ContainsKey(requestType))
                    {
                        log.LogMessage("Handler - Unknown message type. Servicing ending.");
                        return;
                    }
                    // Dispatch request processing
                    MESSAGE_HANDLERS[requestType](input, output, log);
                }
            }
            catch (IOException ioe)
            {
                // Connection closed by the client. Log it!
                log.LogMessage(String.Format("Handler - Connection closed by client {0}", ioe));
            }
            finally
            {
                input.Close();
                output.Close();
            }
        }
    }

    /// <summary>
    /// This class instances are file tracking servers. They are responsible for accepting 
    /// and managing established TCP connections.
    /// </summary>
    public sealed class Listener
    {
        /// <summary>
        /// TCP port number in use.
        /// </summary>
        private readonly int portNumber;

        /// <summary> Initiates a tracking server instance.</summary>
        /// <param name="_portNumber"> The TCP port number to be used.</param>
        public Listener(int _portNumber) { portNumber = _portNumber; }

        /// <summary>
        ///	Server's main loop implementation.
        /// </summary>
        /// <param name="log"> The Logger instance to be used.</param>
        public void Run(Logger log)
        {
            TcpListener srv = null;
            try
            {
                srv = new TcpListener(IPAddress.Loopback, portNumber);
                srv.Start();
                while (true)
                {
                    log.LogMessage("Listener - Waiting for connection requests.");
                    using (TcpClient socket = srv.AcceptTcpClient())
                    {
                        socket.LingerState = new LingerOption(true, 10);
                        log.LogMessage(String.Format("Listener - Connection established with {0}.",
                            socket.Client.RemoteEndPoint));
                        // Instantiating protocol handler and associate it to the current TCP connection
                        Handler protocolHandler = new Handler(socket.GetStream(), log);
                        // Synchronously process requests made through de current TCP connection
                        protocolHandler.Run();
                    }

                    Program.ShowInfo(Store.Instance);
                }
            }
            finally
            {
                log.LogMessage("Listener - Ending.");
                srv.Stop();
            }
        }

    }

    class Program
    {
        public static void ShowInfo(Store store)
        {
            foreach (string fileName in store.GetTrackedFiles())
            {
                Console.WriteLine(fileName);
                foreach (IPEndPoint endPoint in store.GetFileLocations(fileName))
                {
                    Console.Write(endPoint + " ; ");
                }
                Console.WriteLine();
            }
            Console.WriteLine();
        }

/*
        static void TestStore()
        {
            Store store = Store.Instance;

            store.Register("xpto", new IPEndPoint(IPAddress.Parse("193.1.2.3"), 1111));
            store.Register("xpto", new IPEndPoint(IPAddress.Parse("194.1.2.3"), 1111));
            store.Register("xpto", new IPEndPoint(IPAddress.Parse("195.1.2.3"), 1111));
            ShowInfo(store);
            Console.ReadLine();
            store.Register("ypto", new IPEndPoint(IPAddress.Parse("193.1.2.3"), 1111));
            store.Register("ypto", new IPEndPoint(IPAddress.Parse("194.1.2.3"), 1111));
            ShowInfo(store);
            Console.ReadLine();
            store.Unregister("xpto", new IPEndPoint(IPAddress.Parse("195.1.2.3"), 1111));
            ShowInfo(store);
            Console.ReadLine();

            store.Unregister("xpto", new IPEndPoint(IPAddress.Parse("193.1.2.3"), 1111));
            store.Unregister("xpto", new IPEndPoint(IPAddress.Parse("194.1.2.3"), 1111));
            ShowInfo(store);
            Console.ReadLine();
        }
*/


        /// <summary>
        ///	Application's starting point. Starts a tracking server that listens at the TCP port 
        ///	specified as a command line argument.
        /// </summary>
        public static void Main(string[] args)
        {
            // Checking command line arguments
            if (args.Length != 1)
            {
                Console.WriteLine("Utilização: {0} <numeroPortoTCP>", AppDomain.CurrentDomain.FriendlyName);
                Environment.Exit(1);
            }

            ushort port;
            if (!ushort.TryParse(args[0], out port))
            {
                Console.WriteLine("Usage: {0} <TCPPortNumber>", AppDomain.CurrentDomain.FriendlyName);
                return;
            }

            // Start servicing
            Logger log = new Logger();
            log.Start();
            try
            {
                new Listener(port).Run(log);
            }
            finally
            {
                log.Stop();
            }
        }
    }
}
