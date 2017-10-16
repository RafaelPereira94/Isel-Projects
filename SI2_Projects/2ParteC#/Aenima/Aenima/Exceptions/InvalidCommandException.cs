using System;

namespace Aenima.Exceptions
{
    class InvalidCommandException : Exception
    {
        public InvalidCommandException() { }

        public InvalidCommandException(string msg) : base(msg)
        {
        }

        public InvalidCommandException(string message, Exception inner)
        : base(message, inner)
        {
        }
    }
}
