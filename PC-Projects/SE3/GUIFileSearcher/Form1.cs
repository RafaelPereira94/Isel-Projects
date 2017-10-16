using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using FileSearcher;

namespace GUIFileSearcher {
    public partial class Form1 : Form {
        private CancellationTokenSource cts;
        private int start;
        public Form1() {
            cts = new CancellationTokenSource();
            InitializeComponent();
        }

        private async void Search(object sender, EventArgs e) {
            if(cts.IsCancellationRequested) cts = new CancellationTokenSource();
            ClearResults();
            Searcher fs = new Searcher(directory.Text,
                                        extension.Text,
                                        sequence.Text,
                                        new Progress<string>((file) => filesFound.Text += file + Environment.NewLine));
            IProgress<int> total = new Progress<int>((val) => totalFilesSearched.Text = "" + val),
                            ext = new Progress<int>((val) => filesWExtension.Text = "" + val),
                            time = new Progress<int>((val) => searchDuration.Text = "" + ( ( Environment.TickCount - start ) / 1000 ) + " s");
            start = Environment.TickCount;
            try {
                await fs.Search(cts.Token).ContinueWith((sresult) => {
                    SearchResult sr = sresult.Result;
                    total.Report(sr.totalFiles);
                    ext.Report(sr.filesWExtention);
                    time.Report(0);
                });
            } catch(OperationCanceledException) { }
            
        }

        private void Cancel(object sender, EventArgs e) {
            cts.Cancel();
            ClearResults();
        }

        private void ClearResults() {
            totalFilesSearched.Text = "";
            filesWExtension.Text = "";
            searchDuration.Text = "";
            filesFound.Text = "";
        }

        private void Clear(object sender, EventArgs e) {
            ClearResults();
            ClearForm(null, null);
        }

        private void ClearForm(object sender, EventArgs e) {
            directory.Text = "";
            sequence.Text = "";
            extension.Text = "";
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (folderBrowserDialog1.ShowDialog() == DialogResult.OK)
                directory.Text = folderBrowserDialog1.SelectedPath;
        }
    }
}
