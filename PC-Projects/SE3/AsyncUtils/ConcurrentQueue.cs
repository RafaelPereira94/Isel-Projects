using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace AsyncUtils
{
    public class ConcurrentQueue_<T>
    {
        class Node<T>
        {
            public volatile Node<T> next;
            public readonly T value;
            public Node(T value, Node<T> next)
            {
                this.value = value;
                this.next = next;
            }
        }

        private volatile Node<T> Qhead, Qtail;

        public ConcurrentQueue_()
        {
            Qhead = new Node<T>(default(T), null);
            Qtail = Qhead;
        }

        public void enqueue(T value)
        {
            Node<T> myNode = new Node<T>(value, null), tail, tailNext;
            while (true)
            {
                tail = Qtail;
                if ((tailNext = tail.next) == null)
                {
                    if (Interlocked.CompareExchange(ref tail.next, myNode, null) == null)
                    {
                        Interlocked.CompareExchange(ref Qtail, myNode, tail);
                        return;
                    }
                }
                else
                    Interlocked.CompareExchange(ref Qtail, tailNext, tail);
            }
        }

        public T tryDequeue()
        {
            while (true)
            {
                Node<T> head = Qhead, tail = Qtail, headNext = head.next;

                if (head == Qhead)
                    if (head == tail)
                    {
                        if (headNext == null) return default(T);
                        Interlocked.CompareExchange(ref Qtail, headNext, tail);
                    }
                    else if (Interlocked.CompareExchange(ref Qhead, headNext, head) == head)
                        return headNext.value;
            }
        }

        public T dequeue()
        {
            T value;
            while ((value = tryDequeue()) == null)
            {
                Thread.Sleep(0);
            }
            return value;
        }

        public bool isEmpty()
        {
            Node<T> head = Qhead;
            if (head.next == null)
                return true;
            return false;
        }


    }
}
