using System;
using System.Collections.Generic;
using Aenima.Commands;
using Aenima.Exceptions;
using System.Configuration;

namespace Aenima
{
    class Program
    {
        private static readonly string INSERT_PROMO = "1) Insert Promotion.";
        private static readonly string DELETE_PROMO = "2) Delete Promotion.";
        private static readonly string UPDATE_PROMO = "3) Update Promotion.";
        private static readonly string INSERT_RENT_CLIENT = "4) Insert rent without existing client.";
        private static readonly string INSERT_RENT = "5) Insert rent using existing client.";
        private static readonly string REMOVE_RENT = "6) Remove rent";
        private static readonly string UPDATE_RENTPRICE = "7) Update rentPrice.";
        private static readonly string LIST_FREE_EQUIP_DATE = "8) List free equipment for specific time.";
        private static readonly string LIST_RENT = "9) List Equipments without rent last week.";
        private static readonly string XML_EXPORT_RENT = "10) Export rent(s) between two dates (XML).";
        private static readonly string EXIT_APP = "11) Exit application.";

        public static void Main(string[] args)
        {
            bool isAdo;
            List<ICommand> cmds = SetupCommands();
            Console.WriteLine("Welcome to Aenima Console App!");

            Console.WriteLine("Insert letter A to ADO or E to Entity FrameWork approach.");
            string letter = Console.ReadLine();
            if (letter.Equals("A"))
                isAdo = true;
            else if (letter.Equals("E"))
                isAdo = false;
            else
            {
                Console.WriteLine("Invalid letter choosing ADO has default!");
                isAdo = true;
            }
            Run(cmds,isAdo);
        }

        private static List<ICommand> SetupCommands()
        {
            List<ICommand> cmds = new List<ICommand>();
            cmds.Add(new InsertPromoCmd(INSERT_PROMO));
            cmds.Add(new DeletePromoCmd(DELETE_PROMO));
            cmds.Add(new UpdatePromoCmd(UPDATE_PROMO));
            cmds.Add(new InsertRentFullClientCmd(INSERT_RENT_CLIENT));
            cmds.Add(new InsertRentCmd(INSERT_RENT));
            cmds.Add(new RemoveRentCmd(REMOVE_RENT)); 
            cmds.Add(new UpdateRentPriceCmd(UPDATE_RENTPRICE));
            cmds.Add(new ListFreeEquipDateCmd(LIST_FREE_EQUIP_DATE));
            cmds.Add(new ListEquipRentCmd(LIST_RENT));
            cmds.Add(new ExportRentCmd(XML_EXPORT_RENT));
            cmds.Add(new ExitCmd(EXIT_APP));
            return cmds;
        }

        private static void Run(List<ICommand> cmds,bool isAdo)
        {
            ICommand cmd;
            while (true)
            {
                PrintCommands(cmds);
                int value = 0;
                try
                {
                    value = Convert.ToInt32(Console.ReadLine());

                }
                catch (FormatException)
                {
                    Console.WriteLine("Insert a number...");
                    continue;
                }
                if (value > cmds.Count || value <= 0)
                {
                    try
                    {
                        throw new InvalidCommandException("Invalid Command try another one....");
                    }
                    catch (InvalidCommandException e)
                    {
                        Console.WriteLine(e.Message);
                        continue;
                    }
                }
                    
                cmd = cmds[value - 1];
              
                string connectionString = ConfigurationManager.ConnectionStrings["ConnectString"].ConnectionString;
                Console.Clear();

                if (isAdo)
                    cmd.Execute(connectionString);
                else
                    cmd.ExecuteEntity();

                Console.WriteLine("\nClick to continue.");
                Console.ReadKey();
                Console.Clear();
            }
        }

        private static void PrintCommands(List<ICommand> cmds)
        {
            Console.WriteLine("Choose one of the follow commands:");
            cmds.ForEach(Console.WriteLine);
        }
    }
}
