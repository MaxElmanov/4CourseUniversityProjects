package launcher;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class test
{
    public static void main(String[] args) throws InterruptedException
    {
//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
//
//        for (int i = 1; i <= 5; i++)
//        {
//            Task task = new Task("Task " + i);
//            System.out.println("Created : " + task.getName());
//
//            executor.execute(task);
//        }
//        executor.shutdown();

//        long timeInSec = new Date().getTime();
    }
}

class Task implements Runnable {
    private String name;

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void run() {
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.println("Executing : " + name);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}