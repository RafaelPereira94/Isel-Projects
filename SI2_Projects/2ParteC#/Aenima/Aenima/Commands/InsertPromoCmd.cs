using System;
using System.Data;
using System.Data.SqlClient;

namespace Aenima.Commands
{
    class InsertPromoCmd : ICommand
    {
        private string cmdDescr;

        public InsertPromoCmd(string desc)
        {
            cmdDescr = desc;
        }
        public void Execute(string connectStr)
        {
            DateTime? begin_date,end_date;

            try
            {
                Console.WriteLine("Insert begin date of the promotion.");
                begin_date = Convert.ToDateTime(Console.ReadLine());

                Console.WriteLine("Insert end date of the promotion.");
                end_date = Convert.ToDateTime(Console.ReadLine());
            }catch(FormatException)
            {
                Console.WriteLine("Invalid date, try YYYY/MM/DD OR DD/MM/YYYY");
                return;
            }

            Console.WriteLine("Insert the description of the promotion.");
            string description = Console.ReadLine();

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
                        cmd.CommandText = "InsertPromotion";
                        
                        cmd.Parameters.Add("@begin_date", SqlDbType.Date).Value = begin_date.Value.Date;
                        cmd.Parameters.Add("@end_date", SqlDbType.Date).Value = end_date.Value.Date;
                        cmd.Parameters.Add("@descript", SqlDbType.NChar,50).Value = description;
                        var returnParameter = cmd.Parameters.Add("@id", SqlDbType.Int);
                        returnParameter.Direction = ParameterDirection.Output;
                        cmd.ExecuteNonQuery();

                        int id = (int)returnParameter.Value;
                        Console.WriteLine("Successfully inserted promotion with id {0}",id);
                    }
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                    tran.Rollback();
                }
                tran.Commit();
                con.Close();
            }
        }

        public void ExecuteEntity()
        {
            DateTime? begin_date, end_date;

            try
            {
                Console.WriteLine("Insert begin date of the promotion.");
                begin_date = Convert.ToDateTime(Console.ReadLine());

                Console.WriteLine("Insert end date of the promotion.");
                end_date = Convert.ToDateTime(Console.ReadLine());
            }
            catch (FormatException)
            {
                Console.WriteLine("Invalid date, try YYYY/MM/DD OR DD/MM/YYYY");
                return;
            }

            Console.WriteLine("Insert the description of the promotion.");
            string description = Console.ReadLine();

            using (var ctx = new AenimaEntities())
            {
                var promo = new Promotion();
                promo.begin_date = begin_date;
                promo.end_date = end_date.Value;
                promo.descript = description;
                ctx.Promotions.Add(promo);

                ctx.SaveChanges();

                int id = promo.id;

                Console.WriteLine("Sucessfull inserted new Promotion with id = {0}",id);
            }
        }

        public override string ToString()
        {
            return cmdDescr;
        }
    }
}
