using System;
using System.Data.SqlClient;
using System.Data;
using System.Linq;
using System.Collections.Generic;

namespace Aenima.Commands
{
    class UpdatePromoCmd : ICommand
    {
        private string cmdDescr;

        public UpdatePromoCmd(string desc)
        {
            cmdDescr = desc;
        }

        public void Execute(string connectStr)
        {
            DateTime begin_date, end_date;
            int id;
            List<string> prms = new List<string>();
            prms = UpdatePromoInfo(prms);

            try
            {
                id = Convert.ToInt32(prms[0]);
                begin_date = Convert.ToDateTime(prms[1]);
                end_date = Convert.ToDateTime(prms[2]);
            }
            catch (FormatException)
            {
                Console.WriteLine("Invalid parameters inserted ...");
                return;
            }
            string descript = prms[3];


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
                        cmd.CommandText = "UpdatePromotion";

                        cmd.Parameters.Add("@id", SqlDbType.Int).Value = id;
                        cmd.Parameters.Add("@begin_date", SqlDbType.Date).Value = begin_date.Date;
                        cmd.Parameters.Add("@end_date", SqlDbType.Date).Value = end_date.Date;
                        cmd.Parameters.Add("@descript", SqlDbType.VarChar,50).Value = descript.Length != 0 ? descript : null;
                        cmd.ExecuteNonQuery();
                        tran.Commit();
                        Console.WriteLine("sucessfull update to promotion.");
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
            DateTime begin_date, end_date;
            int id;
            List<string> prms = new List<string>();
            prms = UpdatePromoInfo(prms);

            try
            {
                id = Convert.ToInt32(prms[0]);
                begin_date = Convert.ToDateTime(prms[1]);
                end_date = Convert.ToDateTime(prms[2]);
            }
            catch (FormatException)
            {
                Console.WriteLine("Invalid parameters inserted ...");
                return;
            }
            string descript = prms[3];

            using (var ctx = new AenimaEntities())
            {
                var promo = ctx.Promotions.SingleOrDefault(pr => pr.id == id);
                if (promo != null)
                {
                    promo.begin_date = begin_date;
                    promo.end_date = end_date;
                    promo.descript = descript;
                    ctx.SaveChanges();

                    Console.WriteLine("Sucessfull update promotion!");
                }
                else Console.WriteLine("No promotion found with that id!");
            }
        }

        private List<string> UpdatePromoInfo(List<string> prms)
        {
            Console.WriteLine("Update Promotion:");

            Console.WriteLine("Choose promotion you want to update:");
            prms.Add(Console.ReadLine());

            Console.WriteLine("Choose new begin date:");
            prms.Add(Console.ReadLine());

            Console.WriteLine("Choose new end date:");
            prms.Add(Console.ReadLine());

            Console.WriteLine("Choose new description if you want otherwise leave it blank:");
            prms.Add(Console.ReadLine());

            return prms;
        }

        public override string ToString()
        {
            return cmdDescr;
        }
    }
}
