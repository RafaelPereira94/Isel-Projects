using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GUIClient {

    public partial class GUIClient : Form {
        private const int PORT = 8888;

        public GUIClient() {
            InitializeComponent();
        }

        private async void Register(object sender, EventArgs e) {
            output.Text = "REGISTERING ...";
            using(TcpClient client = new TcpClient()) {
                await client.ConnectAsync(IPAddress.Loopback, PORT);

                StreamWriter output = new StreamWriter(client.GetStream());

                // Send request type line
                await output.WriteLineAsync("REGISTER");
                // Send message payload
                foreach(string file in files.Text.Split(','))
                    await output.WriteLineAsync(string.Format("{0}:{1}:{2}", file, address.Text, port.Text));

                // Send message end mark
                await output.WriteLineAsync();

                output.Close();
                client.Close();
            }
            output.Text = "REGISTERING ... DONE!!";
        }

        private async void Unregister(object sender, EventArgs e) {
            output.Text = "UNREGISTERING ...";
            using(TcpClient client = new TcpClient()) {
                await client.ConnectAsync(IPAddress.Loopback, PORT);

                StreamWriter output = new StreamWriter(client.GetStream());

                // Send request type line
                await output.WriteLineAsync("UNREGISTER");
                // Send message payload
                await output.WriteLineAsync(string.Format("{0}:{1}:{2}", files.Text, address.Text, port.Text));
                // Send message end mark
                await output.WriteLineAsync();

                output.Close();
                client.Close();
            }
            output.Text = "UNREGISTERING ... DONE!";
        }

        private async void ListFiles(object sender, EventArgs e) {
            output.Text = "Files :\n";
            using(TcpClient socket = new TcpClient()) {
                await socket.ConnectAsync(IPAddress.Loopback, PORT);

                StreamWriter soutput = new StreamWriter(socket.GetStream());

                // Send request type line
                await soutput.WriteLineAsync("LIST_FILES");
                // Send message end mark and flush it
                await soutput.WriteLineAsync();
                await soutput.FlushAsync();

                // Read response
                string line;
                StreamReader input = new StreamReader(socket.GetStream());
                while(( line = await input.ReadLineAsync() ) != null && line != string.Empty)
                    output.Text += line + "\n";

                soutput.Close();
                socket.Close();
            }
            output.Text += "DONE!";
        }

        private async void ListFileLocations(object sender, EventArgs e) {
            output.Text = files.Text + " Locations :\n";
            using(TcpClient socket = new TcpClient()) {
                await socket.ConnectAsync(IPAddress.Loopback, PORT);

                StreamWriter soutput = new StreamWriter(socket.GetStream());

                // Send request type line
                await soutput.WriteLineAsync("LIST_LOCATIONS");
                // Send message payload
                await soutput.WriteLineAsync(files.Text);
                // Send message end mark and flush it
                await soutput.WriteLineAsync();
                await soutput.FlushAsync();

                // Read response
                string line;
                StreamReader sinput = new StreamReader(socket.GetStream());

                while(( line = await sinput.ReadLineAsync() ) != null && line != string.Empty)
                    output.Text += line +"\n";
                
                soutput.Close();
                socket.Close();
            }
            output.Text += "DONE!";
        }
    }
}
