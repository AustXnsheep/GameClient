package git.austxnsheep;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerThread {
    private final Thread thread;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public WorkerThread() {
        thread = new Thread(this::processTasks);
        thread.start();
    }

    private void processTasks() {
        while (running || !taskQueue.isEmpty()) {
            try {
                Runnable task = taskQueue.take(); // Blocks until a task is available
                task.run();
            } catch (InterruptedException e) {
                if (!running) { // Graceful shutdown on running = false
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void execute(Runnable task) {
        if (running) {
            taskQueue.add(task);
        }
    }

    public void shutdown() {
        running = false;
        thread.interrupt(); // Interrupt the thread to exit blocking state
    }
}
