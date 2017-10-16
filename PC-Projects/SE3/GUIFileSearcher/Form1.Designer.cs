namespace GUIFileSearcher {
    partial class Form1 {
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
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.directory = new System.Windows.Forms.TextBox();
            this.extension = new System.Windows.Forms.TextBox();
            this.sequence = new System.Windows.Forms.TextBox();
            this.panel1 = new System.Windows.Forms.Panel();
            this.panel2 = new System.Windows.Forms.Panel();
            this.clearBut = new System.Windows.Forms.Button();
            this.cancelBut = new System.Windows.Forms.Button();
            this.clearFormBut = new System.Windows.Forms.Button();
            this.searchButton = new System.Windows.Forms.Button();
            this.filesFound = new System.Windows.Forms.RichTextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.totalFilesSearched = new System.Windows.Forms.TextBox();
            this.filesWExtension = new System.Windows.Forms.TextBox();
            this.searchDuration = new System.Windows.Forms.TextBox();
            this.searchDir = new System.Windows.Forms.Button();
            this.folderBrowserDialog1 = new System.Windows.Forms.FolderBrowserDialog();
            this.panel1.SuspendLayout();
            this.panel2.SuspendLayout();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(21, 7);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(81, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Root Directory :";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(43, 33);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(59, 13);
            this.label2.TabIndex = 1;
            this.label2.Text = "Extension :";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(3, 57);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(99, 13);
            this.label3.TabIndex = 2;
            this.label3.Text = "Search Sequence :";
            // 
            // directory
            // 
            this.directory.Location = new System.Drawing.Point(108, 4);
            this.directory.Name = "directory";
            this.directory.Size = new System.Drawing.Size(100, 20);
            this.directory.TabIndex = 3;
            // 
            // extension
            // 
            this.extension.Location = new System.Drawing.Point(108, 30);
            this.extension.Name = "extension";
            this.extension.Size = new System.Drawing.Size(100, 20);
            this.extension.TabIndex = 4;
            // 
            // sequence
            // 
            this.sequence.Location = new System.Drawing.Point(108, 57);
            this.sequence.Name = "sequence";
            this.sequence.Size = new System.Drawing.Size(100, 20);
            this.sequence.TabIndex = 5;
            // 
            // panel1
            // 
            this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.panel1.Controls.Add(this.searchDir);
            this.panel1.Controls.Add(this.label3);
            this.panel1.Controls.Add(this.sequence);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Controls.Add(this.extension);
            this.panel1.Controls.Add(this.label2);
            this.panel1.Controls.Add(this.directory);
            this.panel1.Location = new System.Drawing.Point(12, 12);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(285, 85);
            this.panel1.TabIndex = 6;
            // 
            // panel2
            // 
            this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.panel2.Controls.Add(this.clearBut);
            this.panel2.Controls.Add(this.cancelBut);
            this.panel2.Controls.Add(this.clearFormBut);
            this.panel2.Controls.Add(this.searchButton);
            this.panel2.Location = new System.Drawing.Point(329, 12);
            this.panel2.Name = "panel2";
            this.panel2.Size = new System.Drawing.Size(177, 85);
            this.panel2.TabIndex = 7;
            // 
            // clearBut
            // 
            this.clearBut.Location = new System.Drawing.Point(95, 48);
            this.clearBut.Name = "clearBut";
            this.clearBut.Size = new System.Drawing.Size(75, 23);
            this.clearBut.TabIndex = 3;
            this.clearBut.Text = "Clear";
            this.clearBut.UseVisualStyleBackColor = true;
            this.clearBut.Click += new System.EventHandler(this.Clear);
            // 
            // cancelBut
            // 
            this.cancelBut.Location = new System.Drawing.Point(95, 9);
            this.cancelBut.Name = "cancelBut";
            this.cancelBut.Size = new System.Drawing.Size(75, 23);
            this.cancelBut.TabIndex = 2;
            this.cancelBut.Text = "Cancel";
            this.cancelBut.UseVisualStyleBackColor = true;
            this.cancelBut.Click += new System.EventHandler(this.Cancel);
            // 
            // clearFormBut
            // 
            this.clearFormBut.Location = new System.Drawing.Point(4, 48);
            this.clearFormBut.Name = "clearFormBut";
            this.clearFormBut.Size = new System.Drawing.Size(75, 23);
            this.clearFormBut.TabIndex = 1;
            this.clearFormBut.Text = "Clear Form";
            this.clearFormBut.UseVisualStyleBackColor = true;
            this.clearFormBut.Click += new System.EventHandler(this.ClearForm);
            // 
            // searchButton
            // 
            this.searchButton.Location = new System.Drawing.Point(4, 9);
            this.searchButton.Name = "searchButton";
            this.searchButton.Size = new System.Drawing.Size(75, 23);
            this.searchButton.TabIndex = 0;
            this.searchButton.Text = "Search";
            this.searchButton.UseVisualStyleBackColor = true;
            this.searchButton.Click += new System.EventHandler(this.Search);
            // 
            // filesFound
            // 
            this.filesFound.Location = new System.Drawing.Point(12, 130);
            this.filesFound.Name = "filesFound";
            this.filesFound.ReadOnly = true;
            this.filesFound.Size = new System.Drawing.Size(494, 178);
            this.filesFound.TabIndex = 8;
            this.filesFound.Text = "";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(231, 114);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(48, 13);
            this.label4.TabIndex = 9;
            this.label4.Text = "Results :";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(118, 326);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(110, 13);
            this.label5.TabIndex = 10;
            this.label5.Text = "Total Files Searched :";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(123, 352);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(105, 13);
            this.label6.TabIndex = 11;
            this.label6.Text = "Files with Extension :";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(140, 379);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(88, 13);
            this.label7.TabIndex = 12;
            this.label7.Text = "Search duration :";
            // 
            // totalFilesSearched
            // 
            this.totalFilesSearched.Location = new System.Drawing.Point(234, 323);
            this.totalFilesSearched.Name = "totalFilesSearched";
            this.totalFilesSearched.ReadOnly = true;
            this.totalFilesSearched.Size = new System.Drawing.Size(165, 20);
            this.totalFilesSearched.TabIndex = 13;
            // 
            // filesWExtension
            // 
            this.filesWExtension.Location = new System.Drawing.Point(234, 349);
            this.filesWExtension.Name = "filesWExtension";
            this.filesWExtension.ReadOnly = true;
            this.filesWExtension.Size = new System.Drawing.Size(165, 20);
            this.filesWExtension.TabIndex = 14;
            // 
            // searchDuration
            // 
            this.searchDuration.Location = new System.Drawing.Point(234, 376);
            this.searchDuration.Name = "searchDuration";
            this.searchDuration.ReadOnly = true;
            this.searchDuration.Size = new System.Drawing.Size(165, 20);
            this.searchDuration.TabIndex = 15;
            // 
            // searchDir
            // 
            this.searchDir.Location = new System.Drawing.Point(214, 4);
            this.searchDir.Name = "searchDir";
            this.searchDir.Size = new System.Drawing.Size(57, 20);
            this.searchDir.TabIndex = 6;
            this.searchDir.Text = "...";
            this.searchDir.TextAlign = System.Drawing.ContentAlignment.TopCenter;
            this.searchDir.UseVisualStyleBackColor = true;
            this.searchDir.Click += new System.EventHandler(this.button1_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(518, 423);
            this.Controls.Add(this.searchDuration);
            this.Controls.Add(this.filesWExtension);
            this.Controls.Add(this.totalFilesSearched);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.filesFound);
            this.Controls.Add(this.panel2);
            this.Controls.Add(this.panel1);
            this.Name = "Form1";
            this.Text = "Form1";
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.panel2.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox directory;
        private System.Windows.Forms.TextBox extension;
        private System.Windows.Forms.TextBox sequence;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Panel panel2;
        private System.Windows.Forms.Button clearBut;
        private System.Windows.Forms.Button cancelBut;
        private System.Windows.Forms.Button clearFormBut;
        private System.Windows.Forms.Button searchButton;
        private System.Windows.Forms.RichTextBox filesFound;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.TextBox totalFilesSearched;
        private System.Windows.Forms.TextBox filesWExtension;
        private System.Windows.Forms.TextBox searchDuration;
        private System.Windows.Forms.Button searchDir;
        private System.Windows.Forms.FolderBrowserDialog folderBrowserDialog1;
    }
}

