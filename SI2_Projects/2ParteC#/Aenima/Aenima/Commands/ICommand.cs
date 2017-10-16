using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Aenima
{
    public interface ICommand
    {
        void Execute(string connectStr);

        void ExecuteEntity();
    }
}
