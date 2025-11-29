package functions.threads;

import functions.Function;
import functions.basic.Log;
import java.util.concurrent.Semaphore;

public class Generator extends Thread {
    private final Task task;
    private final Semaphore semaphore;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                if (isInterrupted()) {
                    System.out.println("Generator was interrupted");
                    return;
                }

                // Ждем, пока предыдущие данные не будут обработаны
                synchronized (task) {
                    long waitStart = System.currentTimeMillis();
                    while (!task.isDataProcessed() && !isInterrupted()) {
                        long elapsed = System.currentTimeMillis() - waitStart;
                        if (elapsed > 100) { // Максимум 100ms ждем
                            break; // Выходим из ожидания
                        }
                        task.wait(10);
                    }
                    if (isInterrupted()) return;
                }

                // Генерируем случайные параметры
                double base = 1 + Math.random() * 9;
                if (Math.abs(base - 1.0) < 1e-10) {
                    base = 1.1;
                }

                Log logFunction = new Log(base);
                double leftBorder = Math.random() * 100;
                double rightBorder = 100 + Math.random() * 100;
                double step = Math.random();

                // Захватываем семафор для записи
                semaphore.acquire();
                try {
                    // Устанавливаем параметры задания
                    task.setFunction(logFunction);
                    task.setLeftBorder(leftBorder);
                    task.setRightBorder(rightBorder);
                    task.setDiscretizationStep(step);
                    task.setDataProcessed(false); // Помечаем как необработанные

                    // Выводим сообщение
                    System.out.printf("Source %.6f %.6f %.6f\n", leftBorder, rightBorder, step);
                } finally {
                    semaphore.release();
                }
                //после semaphore.release():
                synchronized (task) {
                    task.setDataProcessed(false);
                }

                Thread.sleep(2);
            }
        } catch (InterruptedException e) {
            System.out.println("Generator was interrupted during sleep or semaphore acquisition");
        }
    }
}