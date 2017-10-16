using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;

namespace Aenima.Commands
{
    class UpdateRentPriceCmd : ICommand
    {
        private string cmdDescr;

        public UpdateRentPriceCmd(string desc)
        {
            cmdDescr = desc;
        }

        public void Execute(string connectStr)
        {
            List< string > prms = new List<string>();
            int serial_nr, price, time;
            DateTime validity;

            prms = UpdateRentPriceInfo(prms);
            string equipType = prms[0];
            try
            {
                serial_nr = Convert.ToInt32(prms[1]);
                validity = Convert.ToDateTime(prms[2]);
                price = Convert.ToInt32(prms[3]);
                time = Convert.ToInt32(prms[4]);
            }
            catch (FormatException)
            {
                Console.WriteLine("Invalid parameters !!");
                return;
            }

            using (SqlConnection con = new SqlConnection(connectStr))
            {
                con.Open();
                SqlTransaction tran = con.BeginTransaction();
                try
                {
                    using(SqlCommand cmd = con.CreateCommand())
                    {
                        cmd.Transaction = tran;
                        cmd.CommandType = CommandType.StoredProcedure;
                        cmd.CommandText = "UpdateRentPrice";

                        cmd.Parameters.Add("@name_type",SqlDbType.VarChar,25).Value = equipType;
                        cmd.Parameters.Add("@id",SqlDbType.Int).Value = serial_nr;
                        cmd.Parameters.Add("@validity",SqlDbType.Date).Value = validity;
                        cmd.Parameters.Add("@price",SqlDbType.Int).Value = price;
                        cmd.Parameters.Add("@_time",SqlDbType.Int).Value = time;
                        cmd.ExecuteNonQuery();

                        Console.WriteLine("Sucessfull updated Rent Price for id {0}",serial_nr);
                    }
                }catch(Exception e)
                {
                    tran.Rollback();
                    Console.WriteLine(e.Message);
                    return;
                }
                tran.Commit();
                con.Close();
            }
        }

        public void ExecuteEntity()
        {
            List<string> prms = new List<string>();
            int serial_nr, price, time;
            DateTime validity;

            prms = UpdateRentPriceInfo(prms);
            string equipType = prms[0];
            try
            {
                serial_nr = Convert.ToInt32(prms[1]);
                validity = Convert.ToDateTime(prms[2]);
                price = Convert.ToInt32(prms[3]);
                time = Convert.ToInt32(prms[4]);
            }
            catch (FormatException)
            {
                Console.WriteLine("Invalid parameters !!");
                return;
            }

            using(var ctx = new AenimaEntities())
            {
                ctx.UpdateRentPrice(equipType, serial_nr, validity, price, time);
                ctx.SaveChanges();
            }
            Console.WriteLine("Sucessfully updated rent Price!");
        }

        public override string ToString()
        {
            return cmdDescr;
        }

        private List<string> UpdateRentPriceInfo(List<string> prms)
        {
            Console.WriteLine("Update rent price:");

            Console.WriteLine("Insert Equipment Type:");
            prms.Add(Console.ReadLine());

            Console.WriteLine("Insert fare id:");
            prms.Add(Console.ReadLine());

            Console.WriteLine("Insert new validity date(DD/MM/YYYY):");
            prms.Add(Console.ReadLine());

            Console.WriteLine("Insert new price:");
            prms.Add(Console.ReadLine());

            Console.WriteLine("Insert new duration(minutes):");
            prms.Add(Console.ReadLine());

            return prms;
        }
    }
}
