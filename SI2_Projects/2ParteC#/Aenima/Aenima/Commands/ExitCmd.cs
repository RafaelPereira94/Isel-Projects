using System;

namespace Aenima.Commands
{
    class ExitCmd : ICommand
    {
        private string cmdDescr;

        public ExitCmd(string desc)
        {
            this.cmdDescr = desc;
        }

        public void Execute(string connectStr)
        {
            Environment.Exit(0);
        }

        public void ExecuteEntity()
        {
            Environment.Exit(0);
        }

        public override string ToString()
        {
            return cmdDescr;
        }

    }
}
