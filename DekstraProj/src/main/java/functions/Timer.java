package functions;

public class Timer
{
    private static long start;
    //TimeUnit time = TimeUnit.NANOSECONDS;

    public static void start(){
        start = System.nanoTime();
    }

    public static long stop() {
        return (System.nanoTime() - start) / 1_000;
    }
}
