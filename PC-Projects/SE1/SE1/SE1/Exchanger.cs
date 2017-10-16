using System;
using System.Threading;


/*
O unico método, Exchange, é invocado pelas threads para oferecer uma mensagem (mine) e receber, através
do valor de retorno do método, a mensagem oferecida pela thread com que emparelharam. Quando a troca de
mensagens não pode ser realizada de imediato (porque não existe ainda outra thread bloqueada), a thread que
invova o método Exchange fica bloqueada até que: (a) outra thread invoque o método Exchange, devolvendo o
método a mensagem oferecida pela outra thread; (b) expire o limite do tempo de espera especificado, situação
em que o método devolve null, ou; (c) a espera seja interrompida, terminado o método com o lançamento de
ThreadInterruptedException.
*/


//IMPORTANTE - Em cada momento só poderá estar um request (thread) à espera; 
public class Exchanger<T>
{
    //Objecto usado no lock
    private readonly object lockObj = new object();
    //Representa o pedido de exchange de uma thread
    private ThreadRequest request;

    private class ThreadRequest
    {
        //mensagem a ser trocada
        public T message;
        //indica se a o pedido já foi emparelhado com outro
        public bool isExchanging;

        public ThreadRequest(T message)
        {
            this.message = message;
            isExchanging = false;
        }
    }

    public T Exchange(T mine, int timeout)
    {
        lock (lockObj)
        {
            //Se já existir um request então este request pode emparelhar com ele
            if (request != null)
            {
                //dois requests estão a emparelhar
                request.isExchanging = true;
                //Trocar mensagens
                T myMessage = request.message;
                request.message = mine;
                //Acorda a thread à espera de emparelhamento
                Monitor.Pulse(lockObj);
                //Retorna a mensagem
                return myMessage;
            }

            //Ainda não existe nenhum request em espera de emparelhamento, logo tem de esperar por outro
            ThreadRequest req = new ThreadRequest(mine);
            request = req;

            //Momento inicial da espera
            int lastTime = (timeout != Timeout.Infinite) ? Environment.TickCount : 0;
            do
            {
                try
                {
                    //Esperar por outro emparelhamento
                    Monitor.Wait(lockObj, timeout);
                }
                catch (ThreadInterruptedException)
                {
                    //Se sair por excepção e estiver a emparelhar: 
                    if (req.isExchanging)
                    {
                        //sinalizar esta thread como interrompida
                        Thread.CurrentThread.Interrupt();
                        //retornar a mensagem
                        return req.message;
                    }
                    //Se sair por excepção e não estiver a emparelhar elimina o request e lança excepção
                    request = null;
                    throw;
                }

                //Se a thread acordou e estiver a emparelhar:
                if (req.isExchanging)
                {
                    //Apagar o request
                    request = null;
                    //Retornar a mensagem
                    return req.message;
                }

            //enquanto não tiver passado timeOut. Isto porque podem ocorrer saidas espurias
            } while (SyncUtils.AdjustTimeout(ref lastTime, ref timeout) != 0);

            //Se a thread saiu por timeOut e está a emparelhar então continua e retorna 
            if (req.isExchanging)
            {
                //Apagar o request
                request = null;
                //Retornar a mensagem
                return req.message;
            }

            //Se a thread saiu por timeOut e não está a emparelhar então apaga o request e lança excepção
            request = null;
            throw new TimeoutException();
        }
    }
}

