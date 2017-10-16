namespace GUIClient {
    partial class GUIClient {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing) {
            if(disposing && ( components != null )) {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent() {
            this.registerBut = new System.Windows.Forms.Button();
            this.unregisterBut = new System.Windows.Forms.Button();
            this.listFilesBut = new System.Windows.Forms.Button();
            this.listFileLocBut = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.output = new System.Windows.Forms.RichTextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.backgroundWorker = new System.ComponentModel.BackgroundWorker();
            this.addressLabel = new System.Windows.Forms.Label();
            this.portLabel = new System.Windows.Forms.Label();
            this.address = new System.Windows.Forms.TextBox();
            this.port = new System.Windows.Forms.TextBox();
            this.files = new System.Windows.Forms.TextBox();
            this.panel1 = new System.Windows.Forms.Panel();
            this.label3 = new System.Windows.Forms.Label();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // registerBut
            // 
            this.registerBut.Location = new System.Drawing.Point(37, 13);
            this.registerBut.Name = "registerBut";
            this.registerBut.Size = new System.Drawing.Size(75, 23);
            this.registerBut.TabIndex = 0;
            this.registerBut.Text = "Register";
            this.registerBut.UseVisualStyleBackColor = true;
            this.registerBut.Click += new System.EventHandler(this.Register);
            // 
            // unregisterBut
            // 
            this.unregisterBut.Location = new System.Drawing.Point(119, 12);
            this.unregisterBut.Name = "unregisterBut";
            this.unregisterBut.Size = new System.Drawing.Size(75, 23);
            this.unregisterBut.TabIndex = 1;
            this.unregisterBut.Text = "Unregister";
            this.unregisterBut.UseVisualStyleBackColor = true;
            this.unregisterBut.Click += new System.EventHandler(this.Unregister);
            // 
            // listFilesBut
            // 
            this.listFilesBut.Location = new System.Drawing.Point(201, 12);
            this.listFilesBut.Name = "listFilesBut";
            this.listFilesBut.Size = new System.Drawing.Size(75, 23);
            this.listFilesBut.TabIndex = 2;
            this.listFilesBut.Text = "List Files";
            this.listFilesBut.UseVisualStyleBackColor = true;
            this.listFilesBut.Click += new System.EventHandler(this.ListFiles);
            // 
            // listFileLocBut
            // 
            this.listFileLocBut.Location = new System.Drawing.Point(283, 12);
            this.listFileLocBut.Name = "listFileLocBut";
            this.listFileLocBut.Size = new System.Drawing.Size(99, 23);
            this.listFileLocBut.TabIndex = 3;
            this.listFileLocBut.Text = "List File Locations";
            this.listFileLocBut.UseVisualStyleBackColor = true;
            this.listFileLocBut.Click += new System.EventHandler(this.ListFileLocations);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(25, 67);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(29, 13);
            this.label1.TabIndex = 5;
            this.label1.Text = "File :";
            // 
            // output
            // 
            this.output.Location = new System.Drawing.Point(188, 69);
            this.output.Name = "output";
            this.output.Size = new System.Drawing.Size(239, 195);
            this.output.TabIndex = 6;
            this.output.Text = "";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(185, 53);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(45, 13);
            this.label2.TabIndex = 7;
            this.label2.Text = "Output :";
            // 
            // addressLabel
            // 
            this.addressLabel.AutoSize = true;
            this.addressLabel.Location = new System.Drawing.Point(3, 11);
            this.addressLabel.Name = "addressLabel";
            this.addressLabel.Size = new System.Drawing.Size(51, 13);
            this.addressLabel.TabIndex = 8;
            this.addressLabel.Text = "Address :";
            // 
            // portLabel
            // 
            this.portLabel.AutoSize = true;
            this.portLabel.Location = new System.Drawing.Point(22, 39);
            this.portLabel.Name = "portLabel";
            this.portLabel.Size = new System.Drawing.Size(32, 13);
            this.portLabel.TabIndex = 9;
            this.portLabel.Text = "Port :";
            // 
            // address
            // 
            this.address.Location = new System.Drawing.Point(53, 8);
            this.address.Name = "address";
            this.address.Size = new System.Drawing.Size(100, 20);
            this.address.TabIndex = 10;
            // 
            // port
            // 
            this.port.Location = new System.Drawing.Point(53, 36);
            this.port.Name = "port";
            this.port.Size = new System.Drawing.Size(100, 20);
            this.port.TabIndex = 11;
            // 
            // files
            // 
            this.files.Location = new System.Drawing.Point(53, 64);
            this.files.Name = "files";
            this.files.Size = new System.Drawing.Size(100, 20);
            this.files.TabIndex = 12;
            this.files.MouseHover += null;
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.addressLabel);
            this.panel1.Controls.Add(this.files);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Controls.Add(this.port);
            this.panel1.Controls.Add(this.portLabel);
            this.panel1.Controls.Add(this.address);
            this.panel1.Location = new System.Drawing.Point(9, 69);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(170, 94);
            this.panel1.TabIndex = 13;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(12, 53);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(42, 13);
            this.label3.TabIndex = 14;
            this.label3.Text = "Inputs :";
            // 
            // GUIClient
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(436, 276);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.output);
            this.Controls.Add(this.listFileLocBut);
            this.Controls.Add(this.listFilesBut);
            this.Controls.Add(this.unregisterBut);
            this.Controls.Add(this.registerBut);
            this.Name = "GUIClient";
            this.Text = "GUIClient";
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button registerBut;
        private System.Windows.Forms.Button unregisterBut;
        private System.Windows.Forms.Button listFilesBut;
        private System.Windows.Forms.Button listFileLocBut;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.RichTextBox output;
        private System.Windows.Forms.Label label2;
        private System.ComponentModel.BackgroundWorker backgroundWorker;
        private System.Windows.Forms.Label addressLabel;
        private System.Windows.Forms.Label portLabel;
        private System.Windows.Forms.TextBox address;
        private System.Windows.Forms.TextBox port;
        private System.Windows.Forms.TextBox files;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Label label3;
    }
}

