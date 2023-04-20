package Timer;

public class Timer {
    private final long startTime;
    private long oldTime;
    private long oldTimeTicks;

    private final double timePerSec;
    private long tickCounter;
    private double tps;

    private double globalTime;
    private double globalDeltaTime;

    public Timer() {
        timePerSec = 1000.0;
        startTime = oldTime = oldTimeTicks = System.currentTimeMillis();
        globalTime = globalDeltaTime = 0;
        tickCounter = 0;
        tps = 0;
    }

    public void tick() {
        long t = System.currentTimeMillis();

        globalTime = (double) (t - startTime) / timePerSec;
        globalDeltaTime = (double) (t - oldTime) / timePerSec;
        tickCounter++;

        if (t - oldTimeTicks > timePerSec) {
            tps = tickCounter * timePerSec / (double) (t - oldTimeTicks);
            oldTimeTicks = t;
            tickCounter = 0;
        }
        oldTime = t;
    }

    public double getTps() {
        return tps;
    }

    public double getGlobalTimeSeconds() {
        return globalTime;
    }

    public double getGlobalTimeMillis() {
        return globalTime * 1000;
    }

    public double getGlobalDeltaTimeSeconds() {
        return globalDeltaTime;
    }

    public double getGlobalDeltaTimeMillis() {
        return globalDeltaTime * 1000;
    }

    @Override
    public String toString() {
        return "Timer{" +
                "startTime=" + startTime +
                ", oldTime=" + oldTime +
                ", oldTimeTicks=" + oldTimeTicks +
                ", timePerSec=" + timePerSec +
                ", tickCounter=" + tickCounter +
                ", tps=" + tps +
                ", globalTime=" + globalTime +
                ", globalDeltaTime=" + globalDeltaTime +
                '}';
    }
}
