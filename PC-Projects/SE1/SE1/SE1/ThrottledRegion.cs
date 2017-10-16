using System;
using System.Collections.Generic;
using System.Threading;

/*
O objectivo desta classe é controlar o número de threads que estão simultaneamente dentro de uma região de
código delimitada pelas chamadas aos métodos TryEnter e Leave, para cada valor particular de chave. Por
exemplo, pode ser usada para limitar o número de pedidos simultâneos que um servidor HTTP pode estar a
executar para o mesmo utilizador (usando o identificador de utilizador como chave). Além de controlar o 
número de threads dentro da região, limita também o número de threads que podem estar em espera. Mais
uma vez, no cenário de um servidor HTTP é por vezes preferível recusar imediatamente um pedido a colocá-lo
em espera (com o consequente consumo de recursos como memória ou ligações TCP).
Em qualquer momento do tempo e para qualquer chave: (a) não podem estar mais do que maxInside threads
dentro da zona protegida pela mesma chave; (b) não podem estar mais do que maxWaiting threads à espera
de entrar na zona protegida pela mesma chave; (c) uma thread não poderá esperar mais do que waitTimeout
milésimos de segundo para entrar na zona protegida. A política de entrada nas zonas protegidas é FIFO
(first-in-first-out). O método TryEnter retorna se a entrada foi feita com sucesso. Note-se que a entrada pode
não ter sucesso devido à ocorrência de timeout, a ter sido excedido o número máximo de threads em espera ou
ainda por ter sido interrompido o bloqueio da thread. Nas primeiras duas situações o método TryEnter retorna
false e na última termina com o lançamento de ThreadInterruptedException.*/

public class ThrottledRegion
{
    //Região
    class Region
    {
        //Numero de thread dentro da região
        public int inside = 0;
        //Lista de thread à espera para entrar na região
        public LinkedList<object> waiting = new LinkedList<object>();
    }
    //numero máximo de threads dentro de uma região
    //numero máximo de threads à espera para entrar numa região
    //numero máximo de tempo (em ms) que uma thread pode estar à espera para entrar na região
    private int maxInside, maxWaiting, waitTimeout;
    //mapeia key - região; é usado como objecto do lock
    private Dictionary<int, Region> map;

    public ThrottledRegion(int maxInside, int maxWaiting, int waitTimeout)
    {
        this.maxInside = maxInside;
        this.maxWaiting = maxWaiting;
        this.waitTimeout = waitTimeout;
        map = new Dictionary<int, Region>();
    }

    // throws ThreadInterruptedException
    public bool TryEnter(int key)
    {
        lock (map)
        {
            //Se não existir a key, adiciona-se essa key com uma nova região
            if (!map.ContainsKey(key))
                map.Add(key, new Region());

            //região associada à key
            Region r = map[key];
            //se a região não estiver cheia a thread pode entrar
            if(r.inside < maxInside)
            {
                //mais uma thread dentro da região
                r.inside++;
                //conseguiu entrar logo retorna true
                return true;
            }
            //se a lista de espera para entrar na região não estiver cheia a thread pode ficar à espera
            if(r.waiting.Count < maxWaiting)
            {
                //Momento inicial da espera
                int lastTime = (waitTimeout != Timeout.Infinite) ? Environment.TickCount : 0;
                //Nova thread à espera - adiciona-se ao fim para cumprir a disciplina FIFO
                LinkedListNode<object> myRequest = r.waiting.AddLast(new object());
                do
                {
                    try
                    {
                        //Espera por waitTimeout ms
                        SyncUtils.Wait(map, myRequest, waitTimeout);
                    }
                    catch(ThreadInterruptedException)
                    {
                        //Se saiu por excepção então removo a thread da lista de espera
                        r.waiting.Remove(myRequest);
                        //A expecção é relançada                 
                        throw;
                    }

                    //Se a thread saiu por timeOut mas há espaço para entrar então entra na mesma
                    if(r.inside < maxInside)
                    {
                        //Removo a thread da lista de espera
                        r.waiting.Remove(myRequest);
                        //Thread entra na região
                        r.inside++;
                        //Conseguiu entrar logo retorna true
                        return true;
                    }

                //enquanto não tiver passado timeOut. Isto porque podem ocorrer saidas espurias
                } while (SyncUtils.AdjustTimeout(ref lastTime, ref waitTimeout) != 0);

                //Se a thread saiu por timeOut e não houve espaço para entrar na região então removo da lista de espera
                r.waiting.Remove(myRequest);
                //Lanço timeOut exception
                throw new TimeoutException();

            }
            //Não conseguiu entrar na região nem na lista de espera então retorna false
            return false;      
        }
    }

    public void Leave(int key)
    {
        lock (map)
        {
            //Se não existe a key, retorna e não faz nada
            if (!map.ContainsKey(key))
                return;
            //Região associada à key
            Region r = map[key];
            //menos uma thread dentro da região
            r.inside--;
            //se houver threads à espera para entrar na região
            if (r.waiting.Count > 0)
                //É notificada a primeira que entrou para cumprir a disciplina FIFO
                SyncUtils.Notify(map, r.waiting.First);
        }
    }
}
