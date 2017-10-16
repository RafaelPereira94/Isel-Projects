using System;
using System.Data;
using System.Data.SqlClient;
using System.Linq;

namespace Aenima.Commands
{
    class DeletePromoCmd : ICommand
    {
        private string cmdDescr;

        public DeletePromoCmd(string desc)
        {
            cmdDescr = desc;
        }
        public void Execute(string connectStr)
        {
            Console.WriteLine("Insert id of the promotion you want to remove");
            int id = Convert.ToInt32(Console.ReadLine());

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
                        cmd.CommandText = "DeletePromotion";

                        cmd.Parameters.Add("@id", SqlDbType.Int).Value = id;
                        cmd.ExecuteNonQuery();

                        tran.Commit();
                        Console.WriteLine("Sucessfull deleted Promotion!!");
                    }
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                    tran.Rollback();
                }
                finally
                {
                    con.Close();
                }
            }
        }

        public void ExecuteEntity()
        {
            Console.WriteLine("Insert id of the promotion you want to remove");
            int id = Convert.ToInt32(Console.ReadLine());

            using (var ctx = new  AenimaEntities())
            {
                var prom = ctx.Promotions.SingleOrDefault(x => x.id == id);

                ctx.Promotions.Remove(prom);
                ctx.SaveChanges();

                Console.WriteLine("Sucessfull remove promotion with id -> {0}",id);
            }
        }

        public override string ToString()
        {
            return cmdDescr;
        }
    }
}
