using System;
using System.Collections.Generic;
using System.Linq;
using System.Data;
using System.Data.SqlClient;
using Aenima.Utils;

namespace Aenima.Commands
{
    class ListFreeEquipDateCmd : ICommand
    {
        private string cmdDesc;

        public ListFreeEquipDateCmd(string desc)
        {
            cmdDesc = desc;
        }

        public void Execute(string connectStr)
        {
            DateTime time;
            string equip;
            List<string> prms = new List<string>();
            prms = ListFreeEquipDateInfo(prms);
            try
            {
                time = Convert.ToDateTime(prms[0]);
                equip = prms[1];
            }

            catch (FormatException)
            {
                Console.WriteLine("Some parameters were invalid!!");
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
                        cmd.CommandType = CommandType.StoredProcedure;
                        cmd.Transaction = tran;
                        cmd.CommandText = "ListFreeEquipments";

                        cmd.Parameters.Add("@date", SqlDbType.Date).Value = time;
                        cmd.Parameters.Add("@equipType_Name", SqlDbType.VarChar,25).Value = equip;
                        using (SqlDataReader dr = cmd.ExecuteReader())
                        {
                            List<string> columnNames = DataUtils.GetcolumnsNames(dr).ToList();

                            DataUtils.PrintColumnNames(columnNames);
                            
                            while (dr.Read())
                            {
                                Console.Write(Convert.ToString(dr[columnNames[0]]) + " - ");
                                Console.Write(Convert.ToInt32(dr[columnNames[1]]));
                                Console.WriteLine();
                            }
                        }
                    }
                }
                catch (Exception e)
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
            DateTime time;
            string equip;
            List<string> prms = new List<string>();
            prms = ListFreeEquipDateInfo(prms);
            try
            {
                time = Convert.ToDateTime(prms[0]);
                equip = prms[1];
            }

            catch (FormatException)
            {
                Console.WriteLine("Some parameters were invalid!!");
                return;
            }

            using (var ctx = new AenimaEntities())
            {
                var equips = ctx.ListFreeEquipments(time, equip).ToList();
                ctx.SaveChanges();

                var columns = typeof(ListFreeEquipments_Result).GetProperties().Select(prop => prop.Name).ToList();

                DataUtils.PrintColumnNames(columns);

                equips.ForEach(eq => Console.WriteLine(eq.equipment_name +" - "+ eq.code));

                Console.WriteLine("Listed sucessfully the free equipments");
            }
        }

        public override string ToString()
        {
            return cmdDesc;
        }

        private List<string> ListFreeEquipDateInfo(List<string> prms)
        {
            Console.WriteLine("List free equipment for specific time:");

            Console.WriteLine("Insert a date (DD/MM/YYYY):");
            prms.Add(Console.ReadLine());

            Console.WriteLine("Insert Equipment name:");
            prms.Add(Console.ReadLine());

            return prms;
        }
    }
}
