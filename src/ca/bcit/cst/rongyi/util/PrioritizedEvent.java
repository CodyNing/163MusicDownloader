package ca.bcit.cst.rongyi.util;

/**
 * The higher prioritized event will get execute first.
 */
public class PrioritizedEvent implements Comparable<PrioritizedEvent> {
    private int priority = 0;
    private Runnable runnable;

    public PrioritizedEvent(Runnable runnable) {
        this.runnable = runnable;
    }

    public PrioritizedEvent(Runnable runnable, int priority) {
        this.priority = priority;
        this.runnable = runnable;
    }

    public void handle() {
        runnable.run();
    }

    @Override
    public int compareTo(PrioritizedEvent other) {
        return other.priority - this.priority;
    }
}
