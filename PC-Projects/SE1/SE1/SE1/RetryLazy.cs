using System;
using System.Collections.Generic;
using System.Threading;

/*
Esta classe implementa uma versão da classe System.Lazy<T>, pertencente à plataforma .NET, thread-safe e
com tolerância a falhas esporádicas (e.g., a inexistência momentânea de ligações num pool de ligações a
bases de dados). Esta tolerância é conseguida através da retentativa de cálculo do valor em diferentes threads.
O acesso à propriedade Value deve ter o seguinte comportamento: (a) caso o valor já tenha sido calculado,
retorna esse valor; (b) caso o valor ainda não tenha sido calculado, e o número máximo de tentativas
(especificado com maxRetries) não tenha sido excedido, inicia esse cálculo chamando provider na própria
thread invocante e retorna o valor resultante; (c) caso já existe outra thread a realizar esse cálculo, espera até
que o valor esteja calculado ou o número de tentativas seja excedido; (d) lança ThreadInterruptedException
se a espera da thread for interrompida. No caso a chamada a provider resultar numa excepção: (a) a
chamada a Value nessa thread deve resultar no lançamento dessa excepção; (b) se o número de tentativas
ainda não tiver sido excedido e existirem threads em espera, deve ser seleccionada a mais antiga para a
retentativa do cálculo através da função provider; (c) quando o número de tentativas é excedido, todas as
threads à espera na propriedade Value, ou que chamem essa propriedade no futuro, devem retornar lançando
excepção InvalidOperationException.
*/

public class RetryLazy<T> where T: class
{
    private readonly object lockObj = new object();
    private Func<T> provider;
    private int maxRetries;

    private readonly LinkedList<bool> queue;
    private T val = null;
    public T Value { get { return Acquire(); } }

    public RetryLazy(Func<T> provider, int maxRetries)
    {
        this.provider = provider;
        this.maxRetries = maxRetries;
        queue = new LinkedList<bool>();
    }

    //throws InvalidOperationException, ThreadInterruptedException
    private T Acquire()
    {
        lock (lockObj)
        {
            //Se já tiver sido calculado retorna o valor
            if (val != null)
            {
                return val;
            }

            //Se não foi calculado e não tem mais tentativas lança excepção
            if (maxRetries == 0)
            {
                throw new InvalidOperationException();
            }

            //Adicionar o pedido de recalculo ao final da lista (FIFO)
            LinkedListNode<bool> myNode = queue.AddLast(false);

            //Se o pedido for o mais antigo - primeiro da lista então recalcula
            if (myNode == queue.First)
            {
                //Recalcula, por isso usa uma tentativa
                maxRetries--;
                //o pedido mais antigo vai recalcular o valor
                try
                {
                    //Não pode calcular o valor na posse do lock
                    Monitor.Exit(lockObj);
                    //Calcula o valor pelo provider
                    val = provider();
                }
                catch (Exception)
                {
                    Monitor.Enter(lockObj);
                    //Não conseguiu calcular pois a thread só tenta calcular 1 vez logo remove da lista
                    queue.RemoveFirst();
                    //Notifica todas as que estão à espera - o valor tem de ser recalculado
                    Monitor.PulseAll(lockObj);
                    //Lança a excepção
                    throw;
                }
                finally{
                    Monitor.Enter(lockObj);
                    //Notifica todas as que estão à espera - o valor já foi calculado
                    Monitor.PulseAll(lockObj);
                }
            }

            //Se o pedido não for o mais antigo então fica à espera pois já está a ser calculado
            while (myNode != queue.First)
            {
                try
                {
                    //Espera
                    Monitor.Wait(lockObj);
                }
                catch (ThreadInterruptedException)
                {
                    //Remover o pedido da lista
                    queue.Remove(myNode);

                    //Se o valor já estiver calculado retorna
                    if (val != null)
                    {
                        return val;
                    }

                    //Se não estiver calculado lança excepção
                    throw;
                }

                //Se o valor já estiver calculado retorna
                if (val != null)
                {
                    return val;
                }

                //Se o valor não tiver sido calculado e já tiver expirado o numero de tentativas
                if (maxRetries == 0)
                {
                    //Remover o pedido da lista
                    queue.Remove(myNode);
                    //Lançar excepção
                    throw new InvalidOperationException();
                }
            }
        }

        return val;
    }
}