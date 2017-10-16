/*
 * INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA
 * Licenciatura em Engenharia Informática e de Computadores
 *
 * Programação Concorrente - Inverno de 2009-2010
 * João Trindade
 *
 * Código base para a 3ª Série de Exercícios.
 *
 */

using AsyncUtils;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading;

namespace TAPTracker
{
	// Logger single-threaded.
	public class Logger
	{
		private readonly TextWriter writer;
		private DateTime start_time;
		private int num_requests;
        private readonly ConcurrentQueue<string> messages;
        private Thread writerThread;
        private volatile bool terminated = false;

		public Logger() : this(Console.Out) {}
		public Logger(string logfile) : this(new StreamWriter(new FileStream(logfile, FileMode.Append, FileAccess.Write))) {}
		public Logger(TextWriter awriter)
		{
		    num_requests = 0;
		    writer = awriter;
            writerThread = new Thread(TakeMessageAndBeginWriting);
            writerThread.Priority = ThreadPriority.Lowest;
            messages = new ConcurrentQueue<string>();
        }

        public void TakeMessageAndBeginWriting() {
            for(;;) {
                if(terminated) return;
                if(!messages.IsEmpty()) {
                    string line;
                    if(messages.tryDequeue(out line)) {
                        writer.WriteLineAsync(line);
                    }
                }
                Thread.Yield();
            }
        }

	    public void Start()
		{
			start_time = DateTime.Now;
			writer.WriteLine();
			writer.WriteLine(String.Format("::- LOG STARTED @ {0} -::", DateTime.Now));
			writer.WriteLine();
            writerThread.Start();
        }

		public void LogMessage(string msg)
		{
			messages.tryEnqueue(String.Format("{0}: {1}", DateTime.Now, msg),5);
		}

		public void IncrementRequests()
		{
			++num_requests;
		}

		public void Stop()
		{
			long elapsed = DateTime.Now.Ticks - start_time.Ticks;
			writer.WriteLine();
			LogMessage(String.Format("Running for {0} second(s)", elapsed / 10000000L));
			LogMessage(String.Format("Number of request(s): {0}", num_requests));
			writer.WriteLine();
			writer.WriteLine(String.Format("::- LOG STOPPED @ {0} -::", DateTime.Now));
            terminated = true;
            writer.Close();
		}
	}
}
