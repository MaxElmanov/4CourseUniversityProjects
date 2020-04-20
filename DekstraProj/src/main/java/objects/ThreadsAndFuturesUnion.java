package objects;

import logics.DekstraBackPathsFinderThread_2_TEST;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class ThreadsAndFuturesUnion
{
    private Boolean threadsAndFuturesRun_1_Flag = true;
    private List<DekstraBackPathsFinderThread_2_TEST> threads_1;
    private List<Future<Integer>> futures_1;

    private Boolean threadsAndFuturesRun_2_Flag = false;
    private List<DekstraBackPathsFinderThread_2_TEST> threads_2;
    private List<Future<Integer>> futures_2;

    public ThreadsAndFuturesUnion()
    {
        threads_1 = new ArrayList<>();
        futures_1 = new ArrayList<>();
        threads_2 = new ArrayList<>();
        futures_2 = new ArrayList<>();
    }

    public List<DekstraBackPathsFinderThread_2_TEST> getThreads_1()
    {
        return threads_1;
    }
    public List<Future<Integer>> getFutures_1()
    {
        return futures_1;
    }
    public Boolean getThreadsAndFuturesRun_1_Flag()
    {
        return threadsAndFuturesRun_1_Flag;
    }
    public void setThreadsAndFuturesRun_1_Flag(Boolean threadsAndFuturesRun_1_Flag)
    {
        this.threadsAndFuturesRun_1_Flag = threadsAndFuturesRun_1_Flag;
    }

    public List<DekstraBackPathsFinderThread_2_TEST> getThreads_2()
    {
        return threads_2;
    }
    public List<Future<Integer>> getFutures_2()
    {
        return futures_2;
    }
    public Boolean getThreadsAndFuturesRun_2_Flag()
    {
        return threadsAndFuturesRun_2_Flag;
    }
    public void setThreadsAndFuturesRun_2_Flag(Boolean threadsAndFuturesRun_2_Flag)
    {
        this.threadsAndFuturesRun_2_Flag = threadsAndFuturesRun_2_Flag;
    }

    public void clearThreadsAndFutures_1()
    {
        threads_1.clear();
        futures_1.clear();
    }

    public void clearThreadsAndFutures_2()
    {
        threads_2.clear();
        futures_2.clear();
    }
}
