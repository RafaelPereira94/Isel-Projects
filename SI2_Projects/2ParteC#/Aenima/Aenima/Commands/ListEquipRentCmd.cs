using Aenima.Utils;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;

namespace Aenima.Commands
{
    class ListEquipRentCmd : ICommand
    {
        private string cmdDesct;

        public ListEquipRentCmd(string desc)
        {
            cmdDesct = desc;
        }

        public void Execute(string connectStr)
        {
            try
            {
                using (SqlConnection con = new SqlConnection(connectStr))
                {
                    con.Open();
                    try
                    {
                        using (SqlCommand cmd = con.CreateCommand())
                        {
                            cmd.CommandText = "SELECT * FROM SelectEquipmentsWithoutRentInTheLastWeek";
                            using (SqlDataReader dr = cmd.ExecuteReader())
                            {
                                List<string> columnNames = DataUtils.GetcolumnsNames(dr).ToList();
                                
                                DataUtils.PrintColumnNames(columnNames);

                                while (dr.Read())
                                {
                                    Console.Write(Convert.ToInt32(dr[columnNames[0]])+" - ");
                                    Console.Write(Convert.ToString(dr[columnNames[1]]) + " - ");
                                    Console.Write(Convert.ToString(dr[columnNames[2]]));
                                    Console.WriteLine();
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e.Message);
                    }
                    con.Close();
                }
            }
            catch (SqlException e)
            {
                Console.WriteLine(e.Message);
            }
        }

        public void ExecuteEntity()
        {
            using (var ctx = new AenimaEntities())
            {
                //LINQ Query syntax
                var result = from t in ctx.SelectEquipmentsWithoutRentInTheLastWeeks
                             select t;
                var names = typeof(SelectEquipmentsWithoutRentInTheLastWeek).GetProperties().Select(property => property.Name).ToList();

                DataUtils.PrintColumnNames(names);

                foreach (SelectEquipmentsWithoutRentInTheLastWeek t in result)
                    Console.WriteLine("{0} - {1} - {2}",t.code,t.equipment_name,t.descript);
            }
            Console.WriteLine("Sucessfull printed all rents");
        }

        public override string ToString()
        {
            return cmdDesct;
        }
    }
}
