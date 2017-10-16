using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity.Core.Objects;
using System.Data.SqlClient;

namespace Aenima.Commands
{
    class InsertRentFullClientCmd : ICommand
    {
        private string cmdDescr;

        public InsertRentFullClientCmd(string desc)
        {
            cmdDescr = desc;
        }
        public void Execute(string connectStr)
        {
            List<string> prms = new List<string>();
            prms = InsertRentClientInfo(prms);

            int rentTime, rentPrice, equipCode, empNumber, nif;
            string clientName, address;
            try
            {
                rentTime = Convert.ToInt32(prms[0]);
                rentPrice = Convert.ToInt32(prms[1]);
                equipCode = Convert.ToInt32(prms[2]);
                empNumber = Convert.ToInt32(prms[3]);
                nif = Convert.ToInt32(prms[5]);
            }
            catch (FormatException e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine("Some parameters were wrong...");
                return;
            }
            clientName = prms[4];
            address = prms[6];

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
                        cmd.CommandText = "InsertRentAndClient";

                        cmd.Parameters.Add("@rent_time", SqlDbType.Int).Value = rentTime;
                        cmd.Parameters.Add("@price", SqlDbType.Int).Value = rentPrice;
                        cmd.Parameters.Add("@equipCode", SqlDbType.Int).Value = equipCode;
                        cmd.Parameters.Add("@employNr", SqlDbType.Int).Value = empNumber;
                        cmd.Parameters.Add("@clientName", SqlDbType.VarChar, 25).Value = clientName;
                        cmd.Parameters.Add("@clientNif", SqlDbType.Int).Value = nif;
                        cmd.Parameters.Add("@clientAddress", SqlDbType.VarChar, 50).Value = address;
                        var rentCode = cmd.Parameters.Add("@serial_nr", SqlDbType.Int);
                        var clientCode = cmd.Parameters.Add("@clientCode", SqlDbType.Int);
                        rentCode.Direction = ParameterDirection.Output;
                        clientCode.Direction = ParameterDirection.Output;
                        cmd.ExecuteNonQuery();

                        int RentCode = (int)rentCode.Value;
                        int ClientCode = (int)clientCode.Value;

                        Console.WriteLine("Successfully inserted Rent with Id {0} and Client with id {1}", RentCode, ClientCode);
                    }

                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                    tran.Rollback();
                    return;
                }
                tran.Commit();
                con.Close();
            }
        }

        public void ExecuteEntity()
        {

            List<string> prms = new List<string>();
            prms = InsertRentClientInfo(prms);

            int rentTime, rentPrice, equipCode, empNumber, nif;
            string clientName, address;
            try
            {
                rentTime = Convert.ToInt32(prms[0]);
                rentPrice = Convert.ToInt32(prms[1]);
                equipCode = Convert.ToInt32(prms[2]);
                empNumber = Convert.ToInt32(prms[3]);
                nif = Convert.ToInt32(prms[5]);
            }
            catch (FormatException e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine("Some parameters were wrong...");
                return;
            }
            clientName = prms[4];
            address = prms[6];

            var serial_nr = new ObjectParameter("serial_nr", typeof(Int32));
            var clientCode = new ObjectParameter("clientCode", typeof(Int32));

            using (var ctx = new AenimaEntities())
            {
                try
                {
                    ctx.InsertRentAndClient(rentTime, rentPrice, equipCode, empNumber, clientName, nif, address, serial_nr, clientCode);
                    ctx.SaveChanges();
                }catch(Exception e)
                {
                    Console.WriteLine(e.InnerException.Message);
                    return;
                }
                Console.WriteLine("Sucessfull insert Rent with id {0} and client with id {1}",serial_nr.Value,clientCode.Value);
                
                Console.WriteLine();
            }

        }

        public override string ToString()
        {
            return cmdDescr;
        }

        private List<string>  InsertRentClientInfo(List<string> info)
        {
            Console.WriteLine("Insert rent with full client information :");

            Console.WriteLine("Insert rent time(in minutes):");
            info.Add(Console.ReadLine());

            Console.WriteLine("Insert rent price:");
            info.Add(Console.ReadLine());

            Console.WriteLine("Insert equipment code:");
            info.Add(Console.ReadLine());

            Console.WriteLine("Insert employee number:");
            info.Add(Console.ReadLine());

            Console.WriteLine("Insert client Name:");
            info.Add(Console.ReadLine());

            Console.WriteLine("Insert client Nif:");
            info.Add(Console.ReadLine());

            Console.WriteLine("Insert client Address:");
            info.Add(Console.ReadLine());

            return info;
        }
    }
}
