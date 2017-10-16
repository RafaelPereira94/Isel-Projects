using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileSearcher {
    public class Program {
        public static void Main(string [] args) {
            int start = Environment.TickCount;
            SearchResult result =( new Searcher(args[0], args [1], args [2]) ).Search().Result;
            Console.WriteLine("Total files : " + result.totalFiles);
            Console.WriteLine("Extension files : " + result.filesWExtention);
            Console.WriteLine("Files that match :");
            foreach(string file in result.files)
                Console.WriteLine(file);
            Console.WriteLine("Elapsed Time :" + ( Environment.TickCount - start ) + " ms.");
            Console.Read();
        }
    }
}
