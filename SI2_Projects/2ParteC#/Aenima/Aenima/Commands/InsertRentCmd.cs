using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity.Core.Objects;
using System.Data.SqlClient;

namespace Aenima
{
    public class InsertRentCmd : ICommand
    {
        private string cmdDescr;

        public InsertRentCmd(string desc)
        {
            cmdDescr = desc;
        }

        public void Execute(string connectStr)
        {
            int rentTime, rentPrice, equipCode, empNumber, clientCode;
            List<int> prms = new List<int>();
            prms = InsertRentInfo(prms);

            try
            {
                rentTime = prms[0];
                rentPrice = prms[1];
                equipCode = prms[2];
                empNumber = prms[3];
                clientCode = prms[4];
            }
            catch (FormatException)
            {
                Console.WriteLine("Some parameters were wrong...");
                return;
            }
            using (SqlConnection con = new SqlConnection(connectStr))
            {
                con.Open();
                SqlTransaction tran = con.BeginTransaction();
                try
                {
                    
                    using (SqlCommand cmd = con.CreateCommand())
                    {
                        cmd.Transaction = tran;
                        cmd.CommandType = CommandType.StoredProcedure;
                        cmd.CommandText = "InsertRent";

                        cmd.Parameters.Add("@rent_time", SqlDbType.Int).Value = rentTime;
                        cmd.Parameters.Add("@price", SqlDbType.Int).Value = rentPrice;
                        cmd.Parameters.Add("@EquipCode", SqlDbType.Int).Value = equipCode;
                        cmd.Parameters.Add("@clientCode", SqlDbType.Int).Value = clientCode;
                        cmd.Parameters.Add("@EmployNmb", SqlDbType.Int).Value = empNumber;

                        var serial_nr = cmd.Parameters.Add("@serial_nr", SqlDbType.Int);
                        serial_nr.Direction = ParameterDirection.Output;

                        cmd.ExecuteNonQuery();

                        int serial = (int)serial_nr.Value;

                        Console.WriteLine("Rent insert sucessfull with id {0}",serial);
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
            int rentTime, rentPrice, equipCode, empNumber, clientCode;
            List<int> prms = new List<int>();
            prms = InsertRentInfo(prms);

            try
            {
                rentTime = prms[0];
                rentPrice = prms[1];
                equipCode = prms[2];
                empNumber = prms[3];
                clientCode = prms[4]; 
            }
            catch (FormatException)
            {
                Console.WriteLine("Some parameters were wrong...");
                return;
            }

            var serial_nr = new ObjectParameter("serial_nr",typeof(Int32));
            using (var ctx = new AenimaEntities())
            {
                try
                {
                    ctx.InsertRent(rentTime, rentPrice, equipCode, empNumber, clientCode, serial_nr);
                    ctx.SaveChanges();
                }catch(Exception e)
                {
                    Console.WriteLine(e.InnerException.Message);
                    return;
                }
                
            }

            Console.WriteLine("Inserted Rent with id {0}",serial_nr.Value);
        }

        public override string ToString()
        {
            return cmdDescr;
        }

        private List<int> InsertRentInfo(List<int> prms)
        {
            Console.WriteLine("Insert Rent with Existing Client:");

            Console.WriteLine("Insert rent time(in minutes):");
            prms.Add(Convert.ToInt32(Console.ReadLine()));

            Console.WriteLine("Insert rent price:");
            prms.Add(Convert.ToInt32(Console.ReadLine()));

            Console.WriteLine("Insert equipment code:");
            prms.Add(Convert.ToInt32(Console.ReadLine()));

            Console.WriteLine("Insert employee number:");
            prms.Add(Convert.ToInt32(Console.ReadLine()));

            Console.WriteLine("Insert client id:");
            prms.Add(Convert.ToInt32(Console.ReadLine()));

            return prms;
        }
    }
}