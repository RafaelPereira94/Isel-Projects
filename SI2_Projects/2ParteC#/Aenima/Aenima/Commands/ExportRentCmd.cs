using System;
using System.Data;
using System.Data.SqlClient;
using System.Xml;
using System.Linq;
using System.Diagnostics;

namespace Aenima
{
    public class ExportRentCmd : ICommand
    {
        private string cmdDescr;

        public ExportRentCmd(string cmdDescr)
        {
            this.cmdDescr = cmdDescr;
        }

        public void Execute(string connectStr)
        {
            XmlDocument xDocWriter = new XmlDocument();
            xDocWriter.CreateXmlDeclaration("1.0", "UTF-8", null);

            XmlElement xml = xDocWriter.CreateElement("xml");
            xDocWriter.AppendChild(xml);
            XmlElement alugueres = xDocWriter.CreateElement("alugueres");

            Console.WriteLine("Insert begin date:");
            string begin = Console.ReadLine();
            DateTime begin_date = Convert.ToDateTime(begin);

            Console.WriteLine("Insert end date:");
            string end = Console.ReadLine();
            DateTime end_date = Convert.ToDateTime(end);
            
            alugueres.SetAttribute("dataInicio", begin_date.Date.ToString());
            alugueres.SetAttribute("dataFim", end_date.Date.ToString());
            xml.AppendChild(alugueres);

            try
            {
                using (SqlConnection con = new SqlConnection(connectStr))
                {
                    using (SqlCommand cmd = con.CreateCommand())
                    {
                        con.Open();
                        cmd.CommandText = "SELECT serial_nr," +
                            "rent_time,client_code,equipment_code " +
                            "FROM RENT " +
                            "WHERE date_time >= @begin_date " +
                            "AND end_date <= @end_date";

                        cmd.Parameters.Add("@begin_date",SqlDbType.DateTime).Value = begin_date;
                        cmd.Parameters.Add("@end_date",SqlDbType.DateTime).Value = end_date;

                        using (SqlDataReader dr = cmd.ExecuteReader())
                        {
                            while (dr.Read())
                            {
                                XmlElement aluguer = xDocWriter.CreateElement("aluguer");
                                aluguer.SetAttribute("id",dr["serial_nr"].ToString());
                                aluguer.SetAttribute("tipo",dr["rent_time"].ToString()+"m");

                                XmlElement client = xDocWriter.CreateElement("cliente");
                                client.InnerText= dr["client_code"].ToString();

                                XmlElement equipment = xDocWriter.CreateElement("equipamento");
                                equipment.InnerText = dr["equipment_code"].ToString();

                                aluguer.AppendChild(client);
                                aluguer.AppendChild(equipment);
                                alugueres.AppendChild(aluguer);
                            }
                        }
                        con.Close();
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            Console.WriteLine("XML sucessfully exported!");
            xDocWriter.Save("ExportedResult.xml");
            OpenFile("notepad.exe", "ExportedResultEF.xml");
        }

        public void ExecuteEntity()
        {
            XmlDocument xDocWriter = new XmlDocument();
            xDocWriter.CreateXmlDeclaration("1.0", "UTF-8", null);

            XmlElement xml = xDocWriter.CreateElement("xml");
            xDocWriter.AppendChild(xml);
            XmlElement alugueres = xDocWriter.CreateElement("alugueres");

            Console.WriteLine("Insert begin date:");
            string begin = Console.ReadLine();
            DateTime begin_date = Convert.ToDateTime(begin);

            Console.WriteLine("Insert end date:");
            string end = Console.ReadLine();
            DateTime end_date = Convert.ToDateTime(end);

            alugueres.SetAttribute("dataInicio", begin_date.Date.ToString());
            alugueres.SetAttribute("dataFim", end_date.Date.ToString());
            xml.AppendChild(alugueres);

            using (var ctx = new AenimaEntities())
            {
                var rents = ctx.Rents.Where(r => r.date_time >= begin_date && r.end_date <= end_date);

                foreach(var q in rents)
                {
                    XmlElement aluguer = xDocWriter.CreateElement("aluguer");
                    aluguer.SetAttribute("id", q.serial_nr.ToString());
                    aluguer.SetAttribute("tipo", q.rent_time.ToString()+"m");

                    XmlElement client = xDocWriter.CreateElement("cliente");
                    client.InnerText = q.client_code.ToString();

                    XmlElement equipment = xDocWriter.CreateElement("equipamento");
                    equipment.InnerText = q.equipment_code.ToString();

                    aluguer.AppendChild(client);
                    aluguer.AppendChild(equipment);
                    alugueres.AppendChild(aluguer);
                }
            }

            Console.WriteLine("XML sucessfully exported!");
            xDocWriter.Save("ExportedResultEF.xml");
            OpenFile("notepad.exe", "ExportedResultEF.xml");
        }

        public override string ToString()
        {
            return cmdDescr;
        }

        private void OpenFile(string prog, string path)
        {
            Process.Start(prog, path);
        }
    }
}

