package pt.isel.pc.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
O número máximo de worker threads (maxPoolSize) e o tempo máximo que uma worker thread pode estar
inativa antes de terminar (keepAliveTime) são passados com argumentos para o construtor da classe
SynchronousThreadPoolExecutor. A gestão, pelo sincronizador, das worker threads deve obedecer aos
seguintes critérios: (a) se o número total de worker threads for inferior ao limite máximo especificado, é criada
uma nova worker thread sempre que for submetida uma função para execução e não existir nenhuma worker
thread disponível; (b) as worker threads deverão terminar após decorrerem mais do que keepAliveTime
nanosegundos sem que sejam mobilizadas para executar uma função; (c) o número de worker threads
existentes no pool em cada momento depende da atividade deste e pode variar entre zero e maxPoolSize.
As threads que pretendem executar funções através do thread pool executor invocam o método execute,
especificando a função a executar através do parâmetro toCall. Este método bloqueia sempre a thread
invocante até que uma das worker threads conclua a execução da função especificada, e pode terminar: (a)
normalmente, devolvendo a instância do tipo T devolvida pela função, ou; (b) excepcionalmente, lançando a
mesma excepção que foi lançada aquando da chamada à função. Até ao momento em que a thread dedicada
considerar uma função para execução, é possível interromper a execução do método execute; contudo, se a
interrupção ocorrer depois da função ser aceite para execução, o método execute deve ser processado
normalmente, sendo a interrupção memorizada de forma a que possa vir a ser lançada pela thread mais tarde.
A chamada ao método shutdown coloca o executor em modo shutdown. Neste modo, todas as chamadas ao
método execute deverão lançar a excepção IllegalStateException. Contudo, todas as submissões para
execução feitas antes da chamada ao método shutdown devem ser processadas normalmente. O método
shutdown deverá bloquear a thread invocante até que sejam executados todos os itens de trabalho aceites
pelo executor e que tenham terminado todas as worker threads ativas.
A implementação do sincronizador deve optimizar o número de comutações de thread que ocorrem nas várias
circunstâncias.
 */

public class SynchronousThreadPoolExecutor<T> {

    //Representa o trabalho
    private class Work<R> {
        //trabalho a ser feito
        final Callable<R> work;
        //trabalho completo?
        final Condition complete;
        //está a trabalhar?
        boolean isWorking;
        //resultado do trabalho
        R result;
        //excepção do trabalho, se existir
        Exception exception;

        Work(Callable<R> work){
            this.work = work;
            complete = lockObj.newCondition();
            isWorking = false;
        }
    }

    //Lista de trabalhos a ser executados
    private final List<Work<T>> works = new ArrayList<>();
    //Representa o ThreadPool. Guarda a condição na qual a Thread está à espera por trabalho
    private final List<Condition> threadPool = new ArrayList<>();

    private final int maxPoolSize, keepAliveTime;

    //Objecto Lock
    private final Lock lockObj = new ReentrantLock();
    //Condição de shutdown
    private Condition shutdown;

    public SynchronousThreadPoolExecutor(int maxPoolSize, int keepAliveTime){
        if(maxPoolSize <= 0 || keepAliveTime <= 0)
            throw new IllegalArgumentException();
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
    }

    public T execute(Callable<T> toCall) throws Exception{
        lockObj.lock();
        try{
            //Se shutdown mais nenhum trabalho é executado
            if(shutdown != null)
                throw new IllegalStateException();

            //Novo trabalho a ser executado
            Work<T> elem = new Work<>(toCall);
            //Adicionar à lista
            works.add(elem);

            //Se houver threads disponiveis no ThreadPool
            if(!threadPool.isEmpty()) {
                //Acorda a primeira thread do ThreadPool ao sinalizar a primeira condição da lista
                threadPool.get(0).signal();
            }
            //Se o numero de worker threads existentes não ultrapassar o maxPoolSize
            else if(getWorkerThreads() < maxPoolSize){
                //Cria uma nova thread
                new Thread(this::threadWork).start();
            }

            while (true){
                try{
                    //Aguardar pela conclusão do trabalho
                    elem.complete.await();
                }catch(InterruptedException e){
                    //Se o trabalho não está a ser executado então pode ser interrompido
                    if(!elem.isWorking) {
                        Thread.currentThread().interrupt();
                        continue;
                    }
                    throw e;
                }

                //Se não estiver a trabalhar
                if(!elem.isWorking) {
                    //Se deu excepção lança-a
                    if(elem.exception != null)
                        throw elem.exception;
                    //Se tiver o resultado retorna-o
                    if(elem.result != null)
                        return elem.result;
                }
            }
        }finally {
            lockObj.unlock();
        }
    }

    private void threadWork(){
        lockObj.lock();
        try{
            //Se shutdown mais nenhum trabalho é executado
            if (shutdown != null) {
                return;
            }

            while(true){
                //Trabalho a ser executado
                Work<T> elem;

                //Vai buscar trabalho para executar
                while ((elem = getLastWork()) != null) {
                    //trabalho está a ser executado
                    elem.isWorking = true;
                    //o trabalho em si não necessita ser executado dentro do lock
                    lockObj.unlock();
                    try {
                        //Executar o trabalho e guardar o resultado
                        elem.result = elem.work.call();
                    } catch (Exception e) {
                        //Se lançar excepção guarda-se
                        elem.exception = e;
                    }finally {
                        //já acabou de executar
                        elem.isWorking = false;
                        //Necessitamos novamente do lock para sinalizar a condição - COMPLETE
                        lockObj.lock();
                        //Sinalizamos a condição complete para acordar quem estava à espera da conclusão da execução do trabalho
                        elem.complete.signal();
                    }
                }

                //Se não houver shutdown e não houver trabalho
                if(shutdown != null && works.isEmpty()) {
                    //Sinaliza-se o shutdown pois o ThreadPool pode "desligar"
                    shutdown.signal();
                    return;
                }

                //Não existe trabalho para executar mas existe trabalho a ser executado ainda, logo:
                //A thread adiciona-se novamente ao ThreadPool - adicionando à lista a condição na qual vai esperar por trabalho
                Condition work = lockObj.newCondition();
                threadPool.add(work);

                long tm = keepAliveTime;
                while(true){
                    try{
                        //A thread fica à espera de trabalho no máximo até keepAliveTime
                        tm = work.awaitNanos(tm);
                    }catch (InterruptedException e){
                        //Abafador
                    }
                    //Se já ultrapassou keepAliveTime nanosegundos de espera por trabalho
                    if(tm <= 0){
                        //Remove-se da ThreadPool
                        threadPool.remove(work);
                        break;
                    }
                }
            }
        } finally {
            lockObj.unlock();
        }
    }

    public void shutdown(){
        lockObj.lock();
        try{
            //Activar o shutdown
            shutdown = lockObj.newCondition();
            //Se houver threads a trabalhar espera por estas
            if(getWorkingThreads() > 0)
                shutdown.awaitUninterruptibly();
        }finally {
            lockObj.unlock();
        }
    }

    //é acedido sempre dentro do lock
    //Retorna o numero de thread a trabalhar
    private int getWorkingThreads(){
        return (int) works.stream().filter(we -> we.isWorking).count();
    }

    //é acedido sempre dentro do lock
    //Retorna o numero de threads existentes
    private int getWorkerThreads(){
        //Threads que estão a trabalhar + Threads que estão "paradas" no ThreadPool
        return getWorkingThreads() + threadPool.size();
    }

    //é acedido sempre dentro do lock
    //Retorna o trabalho mais antigo para ser realizado
    private Work<T> getLastWork(){
        if(works.isEmpty()) return null;
        return works.remove(works.size() - 1);
    }
}