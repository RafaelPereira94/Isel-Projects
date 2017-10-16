using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace TAPTracker {
    /// <summary>
    /// Handles client requests.
    /// </summary>
    public sealed class Handler {
        #region Message handlers

        /// <summary>
        /// Data structure that supports message processing dispatch.
        /// </summary>
        private static readonly Dictionary<string, Func<StreamReader, StreamWriter, Logger,Task>> MESSAGE_HANDLERS;

        static Handler() {
            MESSAGE_HANDLERS = new Dictionary<string, Func<StreamReader, StreamWriter, Logger, Task>>();
            MESSAGE_HANDLERS ["REGISTER"] = ProcessRegisterMessageAsync;
            MESSAGE_HANDLERS ["UNREGISTER"] = ProcessUnregisterMessageAsync;
            MESSAGE_HANDLERS ["LIST_FILES"] = ProcessListFilesMessageAsync;
            MESSAGE_HANDLERS ["LIST_LOCATIONS"] = ProcessListLocationsMessageAsync;
        }

        /// <summary>
        /// Handles REGISTER messages.
        /// </summary>
        private static Task ProcessRegisterMessageAsync(StreamReader input, StreamWriter output, Logger log) {
            // Read message payload, terminated by an empty line. 
            // Each payload line has the following format
            // <filename>:<ipAddress>:<portNumber>
            return Task.Factory.StartNew(async () => {
                string line;
                while(( line = await input.ReadLineAsync() ) != null && line != string.Empty) {
                    string [] triple = line.Split(':');
                    if(triple.Length != 3) {
                        log.LogMessage("Handler - Invalid REGISTER message.");
                        return;
                    }
                    IPAddress ipAddress = IPAddress.Parse(triple [1]);
                    ushort port;
                    if(!ushort.TryParse(triple [2], out port)) {
                        log.LogMessage("Handler - Invalid REGISTER message.");
                        return;
                    }
                    Store.Instance.Register(triple [0], new IPEndPoint(ipAddress, port));
                }
            }, TaskCreationOptions.AttachedToParent);
        }

        /// <summary>
        /// Handles UNREGISTER messages.
        /// </summary>
        private static Task ProcessUnregisterMessageAsync(StreamReader input, StreamWriter output, Logger log) {
            // Read message payload, terminated by an empty line. 
            // Each payload line has the following format
            // <filename>:<ipAddress>:<portNumber>
            return Task.Factory.StartNew(async () => {
                string line;
                while(( line = await input.ReadLineAsync() ) != null && line != string.Empty) {
                    string [] triple = line.Split(':');
                    if(triple.Length != 3) {
                        log.LogMessage("Handler - Invalid UNREGISTER message.");
                        return;
                    }
                    IPAddress ipAddress = IPAddress.Parse(triple [1]);
                    ushort port;
                    if(!ushort.TryParse(triple [2], out port)) {
                        log.LogMessage("Handler - Invalid UNREGISTER message.");
                        return;
                    }
                    Store.Instance.Unregister(triple [0], new IPEndPoint(ipAddress, port));
                }
            }, TaskCreationOptions.AttachedToParent);

            // This request message does not have a corresponding response message, hence, 
            // nothing is sent to the client.
        }

        /// <summary>
        /// Handles LIST_FILES messages.
        /// </summary>
        private static Task ProcessListFilesMessageAsync(StreamReader input, StreamWriter output, Logger log) {
            // Request message does not have a payload.
            // Read end message mark (empty line)
            return Task.Factory.StartNew(async () => {
                await input.ReadLineAsync();

                string [] trackedFiles = Store.Instance.GetTrackedFiles();

                // Send response message. 
                // The message is composed of multiple lines and is terminated by an empty one.
                // Each line contains a name of a tracked file.
                foreach(string file in trackedFiles)
                    await output.WriteLineAsync(file);

                // End response and flush it.
                await output.WriteLineAsync();
                await output.FlushAsync();
            }, TaskCreationOptions.AttachedToParent);
        }

        /// <summary>
        /// Handles LIST_LOCATIONS messages.
        /// </summary>
        private static Task ProcessListLocationsMessageAsync(StreamReader input, StreamWriter output, Logger log) {
            // Request message payload is composed of a single line containing the file name.
            // The end of the message's payload is marked with an empty line
            return Task.Factory.StartNew(async () => {
                string line = await input.ReadLineAsync();
                await input.ReadLineAsync();

                IPEndPoint [] fileLocations = Store.Instance.GetFileLocations(line);

                // Send response message. 
                // The message is composed of multiple lines and is terminated by an empty one.
                // Each line has the following format
                // <ipAddress>:<portNumber>
                foreach(IPEndPoint endpoint in fileLocations)
                    await output.WriteLineAsync(string.Format("{0}:{1}", endpoint.Address, endpoint.Port));

                // End response and flush it.
                await output.WriteLineAsync();
                await output.FlushAsync();
            }, TaskCreationOptions.AttachedToParent);
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
        public Handler(Stream connection, Logger log) {
            this.log = log;
            output = new StreamWriter(connection);
            input = new StreamReader(connection);
        }

        /// <summary>
        /// Performs request servicing.
        /// </summary>
        public Task RunAsync() {
            return Task.Factory.StartNew(async () => {
                try {
                    string requestType;
                    // Read request type (the request's first line)
                    while(( requestType = await input.ReadLineAsync() ) != null && requestType != string.Empty) {
                        requestType = requestType.ToUpper();
                        if(!MESSAGE_HANDLERS.ContainsKey(requestType)) {
                            log.LogMessage("Handler - Unknown message type. Servicing ending.");
                            return;
                        }
                        // Dispatch request processing
                        await MESSAGE_HANDLERS [requestType](input, output, log);
                    }
                } catch(IOException ioe) {
                    // Connection closed by the client. Log it!
                    log.LogMessage(string.Format("Handler - Connection closed by client {0}", ioe));
                } finally {
                    input.Close();
                    output.Close();
                }
            }, TaskCreationOptions.AttachedToParent);
        }
    }
}
