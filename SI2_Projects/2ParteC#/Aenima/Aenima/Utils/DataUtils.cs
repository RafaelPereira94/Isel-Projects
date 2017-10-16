using System;
using System.Collections.Generic;
using System.Data;
using System.Text;

namespace Aenima.Utils
{
    public static class DataUtils
    {

        public static IEnumerable<string> GetcolumnsNames(this IDataReader reader)
        {
            for(int i = 0; i < reader.FieldCount; ++i)
            {
                yield return reader.GetName(i);
            }
        }

        public static void PrintColumnNames(List<string> list)
        {
            StringBuilder result = new StringBuilder();
            for(int i = 0; i < list.Count; ++i)
            {
                result.Append(list[i]);
                if (i != list.Count - 1)
                {
                    result.Append(" - ");
                } 
            }
            Console.WriteLine(result);
        }
    }
}
