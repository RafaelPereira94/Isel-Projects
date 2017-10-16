using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;

namespace FileSearcher {
    public class Searcher {
        private readonly string root;
        private readonly string extension;
        private readonly string sequence;
        private readonly IProgress<string> filesProgress;

        public Searcher(string root, string ext, string seq) : this(root, ext, seq, null) {}

        public Searcher(string root, string ext, string seq, IProgress<string> fp) {
            this.root = root;
            extension = ext;
            sequence = seq;
            filesProgress = fp;
        }

        public Task<SearchResult> Search() {
            return Search((new CancellationTokenSource()).Token);
        }

        public Task<SearchResult> Search(CancellationToken token) {
            return Task.Factory.StartNew(()=>Search(new SearchResult(new List<string>(), 0, 0),new DirectoryInfo(root), token));
        }

        public SearchResult Search(SearchResult result,DirectoryInfo root, CancellationToken token) {
            try {
                token.ThrowIfCancellationRequested();
                foreach(DirectoryInfo di in root.EnumerateDirectories())
                    Task.Factory.StartNew(()=>Search(result,di, token),token,TaskCreationOptions.AttachedToParent,TaskScheduler.Current);
                foreach(FileInfo fi in root.EnumerateFiles())
                    Task.Factory.StartNew(async () => await SearchFile(result, fi, token), token, TaskCreationOptions.AttachedToParent, TaskScheduler.Current);
            } catch(Exception e) {
                if(!( e is UnauthorizedAccessException || e is IOException || e is PathTooLongException )) throw;
            }
            return result;
        }
        
        
        public async Task SearchFile(SearchResult res, FileInfo file, CancellationToken token) {
            Interlocked.Increment(ref res.totalFiles);
            if(!file.FullName.EndsWith(extension)) return;
            Interlocked.Increment(ref res.filesWExtention);
            byte [] buffer = new byte [32];
            string content = "", aux;
            try {
                using(StreamReader reader = file.OpenText()) {
                    while((aux = await reader.ReadLineAsync()) != null) {
                        content += aux;
                        token.ThrowIfCancellationRequested();
                        if(content.Contains(sequence)) {
                            if(filesProgress != null)
                                filesProgress.Report(file.FullName);
                            res.AddFile(file);
                            return;
                        }
                    }
                }
            } catch(IOException) {}
        }
    }

    

    public class SearchResult {
        public readonly List<string> files;
        public volatile int filesWExtention;
        public volatile int totalFiles;

        public SearchResult(List<string> files, int ext, int total) {
            this.files = files;
            filesWExtention = ext;
            totalFiles = total;
        }

        public void Add(SearchResult other) {
            lock (this) { files.AddRange(other.files); }
            Interlocked.Add(ref filesWExtention, other.filesWExtention);
            Interlocked.Add(ref totalFiles, other.totalFiles);
        }

        public void AddFile(FileInfo file) {
            lock (this) { files.Add(file.FullName); }
        }
    }
}