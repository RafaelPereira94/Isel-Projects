using AsyncUtils;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace APMTracker {
    /// <summary>
    /// Handles client requests.
    /// </summary>
    public sealed class Handler {
        #region Message handlers

        /// <summary>
        /// Data structure that supports message processing dispatch.
        /// </summary>
        private static readonly Dictionary<string, Action<string, byte [], Stream, Stream, Logger, GenericAsyncResult<Object>>> MESSAGE_HANDLERS;

        static Handler() {
            MESSAGE_HANDLERS = new Dictionary<string, Action<string, byte [], Stream, Stream, Logger, GenericAsyncResult<Object>>>();
            MESSAGE_HANDLERS ["RUN"] = ProcessRun;
            MESSAGE_HANDLERS ["REGISTER"] = ProcessRegisterMessage;
            MESSAGE_HANDLERS ["UNREGISTER"] = ProcessUnregisterMessage;
            MESSAGE_HANDLERS ["LIST_FILES"] = ProcessListFilesMessage;
            MESSAGE_HANDLERS ["LIST_LOCATIONS"] = ProcessListLocationsMessage;
        }
        /// <summary>
        /// Handles REGISTER messages.
        /// </summary>
        private static void ProcessRegisterMessage(string register, byte [] buffer, Stream input, Stream output, Logger log, GenericAsyncResult<object> ar) {
            // Read message payload, terminated by an empty line. 
            // Each payload line has the following format
            // <filename>:<ipAddress>:<portNumber>
            log.LogMessage(string.Format("::: REGISTER START IN THREAD {0}", Thread.CurrentThread.ManagedThreadId));
            foreach(String line in register.Split('\n')) {
                string [] triple = line.Split(':');
                if(triple.Length != 3) {
                    log.LogMessage("Handler - Invalid REGISTER message :" + line);
                    ar.OnComplete(null, null);
                    return;
                }
                IPAddress ipAddress = IPAddress.Parse(triple [1]);
                ushort port;
                if(!ushort.TryParse(triple [2], out port)) {
                    log.LogMessage("Handler - Invalid REGISTER message :" + line);
                    ar.OnComplete(null, null);
                    return;
                }
                Store.Instance.Register(triple [0], new IPEndPoint(ipAddress, port));
            }
            ar.OnComplete(null, null);
            log.LogMessage(string.Format("::: REGISTER END IN THREAD {0}", Thread.CurrentThread.ManagedThreadId));
        }

        /// <summary>
        /// Handles UNREGISTER messages.
        /// </summary>
        private static void ProcessUnregisterMessage(string unregister, byte [] buffer, Stream input, Stream output, Logger log, GenericAsyncResult<object> ar) {
            // Read message payload, terminated by an empty line. 
            // Each payload line has the following format
            // <filename>:<ipAddress>:<portNumber>
            log.LogMessage(string.Format("::: UNREGISTER START IN THREAD {0}", Thread.CurrentThread.ManagedThreadId));
            foreach(String line in unregister.Split('\n')) {
                string [] triple = line.Split(':');
                if(triple.Length != 3) {
                    log.LogMessage("Handler - Invalid UNREGISTER message:" + line);
                    ar.OnComplete(null, null);
                    return;
                }
                IPAddress ipAddress = IPAddress.Parse(triple [1]);
                ushort port;
                if(!ushort.TryParse(triple [2], out port)) {
                    log.LogMessage("Handler - Invalid UNREGISTER message:" + line);
                    ar.OnComplete(null, null);
                    return;
                }
                Store.Instance.Unregister(triple [0], new IPEndPoint(ipAddress, port));
            }
            ar.OnComplete(null, null);
            log.LogMessage(string.Format("::: UNREGISTER END IN THREAD {0}", Thread.CurrentThread.ManagedThreadId));
        }

        /// <summary>
        /// Handles LIST_FILES messages.
        /// </summary>
        private static void ProcessListFilesMessage(string commandBuffer, byte [] buffer, Stream input, Stream output, Logger log, GenericAsyncResult<object> ar) {
            // Request message does not have a payload.
            // Read end message mark (empty line)
            log.LogMessage(string.Format("::: LIST_FILES START IN THREAD {0}", Thread.CurrentThread.ManagedThreadId));
            string [] trackedFiles = Store.Instance.GetTrackedFiles();
            List<String> values = new List<string>(trackedFiles);
            string aggregatedValues = "";
            if(trackedFiles.Length > 0)
                aggregatedValues = values.Aggregate((a, b) => a + "\n" + b);
            aggregatedValues += EMPTY_LINE;
            byte [] message = System.Text.Encoding.ASCII.GetBytes(aggregatedValues);

            // Send response message. 
            // The message is composed of multiple lines and is terminated by an empty one.
            // Each line contains a name of a tracked file.
            output.BeginWrite(message, 0, message.Length, (res) => {
                output.EndWrite(res);
                // End response and flush it.
                output.Flush();
                ar.OnComplete(null, null);
                log.LogMessage(string.Format("::: LIST_FILES END IN THREAD {0}", Thread.CurrentThread.ManagedThreadId));
            }, null);
        }

        /// <summary>
        /// Handles LIST_LOCATIONS messages.
        /// </summary>
        private static void ProcessListLocationsMessage(string line, byte [] buffer, Stream input, Stream output, Logger log, GenericAsyncResult<object> ar) {
            log.LogMessage(string.Format("::: LIST_LOCATIONS START IN THREAD {0}", Thread.CurrentThread.ManagedThreadId));
            IPEndPoint [] fileLocations = Store.Instance.GetFileLocations(line);
            List<IPEndPoint> values = new List<IPEndPoint>(fileLocations);
            string aggregatedValues = "";
            if(fileLocations.Length > 0)
                aggregatedValues = values.Select((endpoint) => string.Format("{0}:{1}\n", endpoint.Address, endpoint.Port))
                                         .Aggregate((a, b) => a + b);
            aggregatedValues += EMPTY_LINE;
            byte [] message = System.Text.Encoding.ASCII.GetBytes(aggregatedValues);
            // Send response message. 
            // The message is composed of multiple lines and is terminated by an empty one.
            // Each line has the following format
            // <ipAddress>:<portNumber>
            output.BeginWrite(message, 0, message.Length, (res) => {
                output.EndWrite(res);
                // End response and flush it.
                output.Flush();
                ar.OnComplete(null, null);
                log.LogMessage(string.Format("::: LIST_LOCATIONS END IN THREAD {0}", Thread.CurrentThread.ManagedThreadId));
            }, null);
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

        public IAsyncResult BeginRun(String commandBuffer, AsyncCallback callback, Object state) {
            GenericAsyncResult<Object> asyncResult = new GenericAsyncResult<Object>(callback, state);
            byte [] buffer = new byte [32];
            Stream inputBase = input.BaseStream;
            inputBase.BeginRead(buffer, 0, buffer.Length,
                GenerateBeginReadCallback(commandBuffer, buffer, inputBase, output.BaseStream, log, asyncResult, "RUN", 0), null);
            return asyncResult;
        }

        private static readonly String EMPTY_LINE = "\r\n\r\n";
        private static readonly int RECURSION_LIMIT = 8;

        private static AsyncCallback GenerateBeginReadCallback(String commandBuffer, byte [] buffer, Stream input, Stream output,
                                                        Logger log, GenericAsyncResult<Object> asyncResult, string key, int count) {
            return (result) => {
                try {
                    int bytesRead = input.EndRead(result);
                    if(result.CompletedSynchronously) count++;
                    else count = 0;
                    commandBuffer += System.Text.Encoding.ASCII.GetString(buffer, 0, bytesRead);
                    if(commandBuffer.Contains(EMPTY_LINE))
                        MESSAGE_HANDLERS [key](commandBuffer, buffer, input, output, log, asyncResult);
                    else if(count < RECURSION_LIMIT)
                        input.BeginRead(buffer, 0, buffer.Length,
                                        GenerateBeginReadCallback(commandBuffer, buffer, input, output, log, asyncResult, key, count),
                                        null);

                } catch(IOException e) {
                    asyncResult.OnComplete(null, e);
                    log.LogMessage(String.Format("Handler - Connection closed by client {0}", e));
                }
            };
        }

        private static void ProcessRun(String commandBuffer, byte [] buffer, Stream input, Stream output,
                                                        Logger log, GenericAsyncResult<Object> asyncResult) {
            int commandDataLength = commandBuffer.IndexOf(EMPTY_LINE);
            int commandHeaderLength;
            String message = commandBuffer.Substring(0, commandDataLength);

            commandHeaderLength = message.IndexOf('\n');
            if(commandHeaderLength < 0)
                commandHeaderLength = message.Length;

            string command = message.Substring(0, commandHeaderLength).Trim();
            message = message.Substring(commandHeaderLength).TrimStart();
            commandBuffer = commandBuffer.Substring(commandDataLength + EMPTY_LINE.Length);
            log.LogMessage("Handler - " + command);
            if(!MESSAGE_HANDLERS.ContainsKey(command)) {
                log.LogMessage("Handler - Unknown message type. Servicing ending.");
                asyncResult.OnComplete(null, null);
                return;
            }
            MESSAGE_HANDLERS [command](message, buffer, input, output, log, asyncResult);
        }

        public void EndRun(IAsyncResult res) {
            GenericAsyncResult<object> ar = (GenericAsyncResult<object>) res;
            Object obj = ar.Result;
            input.Close();
            output.Close();
        }
    }
}
