using System;
using System.Collections.Generic;
using System.Threading;

/*
Os métodos DownRead e DownWrite adquirem a posse do semáforo para leitura ou escrita, respectivamente.
Os métodos UpRead e UpWrite libertam o semáforo depois do mesmo ter sido adquirido para leitura ou escrita,
respectivamente. O método DowngradeWriter, que apenas pode ser invocado pelas threads que tenham
adquirido o semáforo para escrita, liberta o acesso para escrita e, atomicamente, adquire acesso para leitura.
Se o método UpRead for invocado por threads que não tenham previamente adquirido o semáforo para leitura,
ou os métodos UpWrite e DowngradeWriter forem invocados por threads que não tenham previamente
adquirido o semáforo para escrita, deve ser lançada a excepção InvalidOperationException. O acesso para
leitura deve ser concedido às threads leitoras que se encontrem no início da fila de espera (ou de imediato, se
a fila de espera estiver vazia quando é invocado o método DownRead) desde que o não tenha sido concedido
acesso para escrita a outra thread; para ser concedido acesso para escrita à thread que se encontra à cabeça
da fila (ou de imediato, se a fila estiver vazia quando é invocado o método DownWrite), é necessário que
nenhuma outra thread tenha adquirido acesso para escrita ou para leitura.
Para que o semáforo seja equitativo na atribuição dos dois tipos de acesso (leitura e escrita), deverá ser
utilizada uma única fila de espera, com disciplina FIFO, onde são inseridas por ordem de chegada as
solicitações de aquisição pendentes. A implementação deve suportar o cancelamento dos métodos
bloqueantes quando são interrompidas as threads bloqueadas (lançando ThreadInterruptedException) e
deve optimizar o número de comutações de thread que ocorrem nas várias circunstâncias.*/

public class RwSemaphore
{
    //Objecto usado no lock
    private readonly object lockObj;

    //Lista de leitores e escritores à espera
    private readonly LinkedList<Rw> rwq;
    //Lista de ids de threads leitoras a ler
    private readonly LinkedList<int> readersInside;
    //Id da thread escritora a escrever 
    private int writerInside;
    private readonly int READER = 0, WRITER = 1;
    //if a writer is writing
    private bool isWriting = false;
    //how many readers reading
    private int readersReading = 0;

    class Rw
    {
        //leitor ou escritor?
        public int rw;

        public Rw(int rw)
        {
            this.rw = rw;
        }
    }

    public RwSemaphore()
    {
        rwq = new LinkedList<Rw>();
        readersInside = new LinkedList<int>();
        lockObj = new object();
    }

    // throws ThreadInterruptedException
    public void DownRead()
    {
        lock(lockObj){

            //Se não houver escritores à espera, nem estiver nenhum escritor a escrever
            if(!isWriting && rwq.First.Value.rw != WRITER)
            {
                //mais um leitor a ler
                readersReading++;
                readersInside.AddLast(Thread.CurrentThread.ManagedThreadId);
                return;
            }
            //Adicionar um novo leitor à lista de espera
            LinkedListNode<Rw> rd = rwq.AddLast(new Rw(READER));
            do
            {
                try
                {
                    //Esperar
                    SyncUtils.Wait(lockObj, rd);
                }
                catch (ThreadInterruptedException)
                {
                    //Remover da lista de espera
                    rwq.Remove(rd);
                    //Lançar excepção
                    throw;
                }
                //Pode ler se ninguem estiver a escrever e se for o primeiro da lista de espera
                if (!isWriting && rwq.First == rd)
                {
                    //Remover da lista de espera
                    rwq.Remove(rd);
                    //Mais um leitor a ler
                    readersReading++;
                    readersInside.AddLast(Thread.CurrentThread.ManagedThreadId);
                    return;
                }
            } while (true);
        }
    }

    // throws ThreadInterruptedException
    public void DownWrite()
    {
        lock (lockObj)
        {
            //Se não houver leitores/escritores à espera e não houver nenhum escritor a escrever e leitores a ler
            if (rwq.Count == 0 && !isWriting && readersReading == 0)
            {
                //Um escritor a escrever
                isWriting = true;
                writerInside = Thread.CurrentThread.ManagedThreadId;
                return;
            }
            //Adicionar um novo escritor à lista de espera
            LinkedListNode<Rw> wr = rwq.AddLast(new Rw(WRITER));
            do
            {
                try
                {
                    //Espera
                    SyncUtils.Wait(lockObj, wr);
                }
                catch (ThreadInterruptedException)
                {
                    //Remover da lista de espera
                    rwq.Remove(wr);
                    //Lançar excepção
                    throw;
                }
                //Se for o primeiro da lista e não houver ninguém a escrever nem a ler
                if (rwq.First == wr && !isWriting && readersReading == 0)
                {
                    //Remover da lista de espera
                    rwq.Remove(wr);
                    //Escritor a escrever
                    isWriting = true;
                    writerInside = Thread.CurrentThread.ManagedThreadId;
                    return;
                }
            } while (true);
        }
    }

    // throws InvalidOperationException
    public void UpRead()
    {
        lock (lockObj)
        {
            if (!readersInside.Contains(Thread.CurrentThread.ManagedThreadId))
            {
                throw new InvalidOperationException();
            }

            //Menos um leitor a ler
            readersReading--;
            readersInside.Remove(Thread.CurrentThread.ManagedThreadId);

            //Se não esta nenhum leitor a ler e houver escritores à espera
            //Não pode haver leitores à espera no inicio da fila neste momento!!
            if (readersReading == 0 && rwq.Count > 0)
            {
                //Notifica o primeiro escritor da lista
                SyncUtils.Notify(lockObj, rwq.First);
            }
        }
    }

    // throws InvalidOperationException
    public void UpWrite()
    {
        lock (lockObj)
        {
            //se n tiver sido invocado por uma thread que invocou o DownWrite
            //lança excepção
            if (writerInside != Thread.CurrentThread.ManagedThreadId)
            {
                throw new InvalidOperationException();
            }

            //Nenhum escritor a escrever
            isWriting = false;

            //Se houver leitores/escritores à espera
            if (rwq.Count > 0)
            {
                //Se houver leitores à espera
                if (rwq.First.Value.rw == READER)
                {
                    //Notificar todos os leitores que estão no inicio da fila, antes de uma escrita
                    NotifyAllReaders();
                }
                //Se houver escritores à espera
                else
                {
                    //Notifica o primeiro escritor da lista
                    SyncUtils.Notify(lockObj, rwq.First);
                }
            }
        }
    }

    // throws InvalidOperationException
    public void DowngradeWriter()
    {
        lock (lockObj)
        {
            //se n tiver sido invocado por uma thread que invocou o DownWrite
            //lança excepção
            if (writerInside != Thread.CurrentThread.ManagedThreadId)
            {
                throw new InvalidOperationException();
            }

            //Nenhum escritor a escrever
            isWriting = false;

            //Adquire imediatamente para leitura
            readersReading++;
            readersInside.AddLast(Thread.CurrentThread.ManagedThreadId);
            return;
        }
    }

    //Acedido na posse do lock - lockObj
    private void NotifyAllReaders()
    {
        for (LinkedListNode<Rw> curr = rwq.First; ; curr = curr.Next)
        {
            if (curr.Value.rw == WRITER)
                return;
            SyncUtils.Notify(lockObj, curr);
        }
    }

    //Usado nos testes
    public int getWaitingCount()
    {
        return rwq.Count;
    }
}
