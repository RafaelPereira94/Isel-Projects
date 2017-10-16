using System.Threading;

/*
* There are various volatiles in this code. But there are no risk if the order changes.
* So theres no need to implement Barriers.
*/

public class ConcurrentQueue_<T>
{
    class Node
    {
        public volatile Node next;
        public readonly T value;
		public Node(T value, Node next)
        {
            this.value = value;
            this.next = next;
        }
    }

    private volatile Node Qhead, Qtail;

    public ConcurrentQueue_()
    {
        Qhead = new Node(default(T), null);
        Qtail = Qhead;
    }

    //Enqueue a datum
    public void enqueue(T value)
    {
        Node myNode = new Node(value, null), tail, tailNext;
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

    //Try to dequeue a datum
    public T tryDequeue()
    {
        while(true)
        {
            Node head = Qhead, tail = Qtail, headNext = head.next;

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

    //Dequeue a datum
    public T dequeue()
    {
        T value;
		while ((value = tryDequeue()) == null) {
            Thread.Sleep(0);
        }
		return value;
    }

    public bool isEmpty()
    {
        Node head = Qhead;
        if (head.next == null)
            return true;
        return false;
    }

   
}




