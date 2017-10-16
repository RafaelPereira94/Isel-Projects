using System;
using System.Data;
using System.Data.SqlClient;

namespace Aenima.Commands
{
    class RemoveRentCmd : ICommand
    {
        private string cmdDescr;

        public RemoveRentCmd(string desc)
        {
            cmdDescr = desc;
        }

        public void Execute(string connectStr)
        {
            Console.WriteLine("Delete Rent:");

            Console.WriteLine("Insert the rent id you want to delete:");
            int serial_nr = Convert.ToInt32(Console.ReadLine());
            
            using(SqlConnection con = new SqlConnection(connectStr))
            {
                con.Open();
                SqlTransaction tran = con.BeginTransaction();
                try
                {
                    using(SqlCommand cmd = con.CreateCommand())
                    {
                        cmd.Transaction = tran;
                        cmd.CommandType = CommandType.StoredProcedure;
                        cmd.CommandText = "DeleteRent";

                        cmd.Parameters.Add("@serial_nr", SqlDbType.Int).Value = serial_nr;
                        cmd.ExecuteNonQuery();

                        Console.WriteLine("Sucessfull deleted Rent with id {0}!!",serial_nr);
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
            Console.WriteLine("Delete Rent:");

            Console.WriteLine("Insert the rent id you want to delete:");
            int serial_nr = Convert.ToInt32(Console.ReadLine());

            using (var ctx = new AenimaEntities())
            {
                try
                {
                    ctx.DeleteRent(serial_nr);
                    ctx.SaveChanges();
                }catch(Exception e)
                {
                    Console.WriteLine(e.InnerException.Message);
                    return;
                }
                Console.WriteLine("Sucessfull deleted rent with id {0}", serial_nr);
            }
        }

        public override string ToString()
        {
            return cmdDescr;
        }
    }
}
