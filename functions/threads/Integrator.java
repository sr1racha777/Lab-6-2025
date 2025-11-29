package functions.threads;

import functions.Function;
import functions.Functions;
import java.util.concurrent.Semaphore;

public class Integrator extends Thread {
    private final Task task;
    private final Semaphore semaphore;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                if (isInterrupted()) {
                    System.out.println("Integrator was interrupted");
                    return;
                }

                Function function;
                double leftBorder, rightBorder, step;
                boolean shouldProcess = false;

                // Захватываем семафор для чтения
                semaphore.acquire();
                try {
                    // Получаем параметры задания
                    function = task.getFunction();
                    leftBorder = task.getLeftBorder();
                    rightBorder = task.getRightBorder();
                    step = task.getDiscretizationStep();

                    // Проверяем, что данные еще не обработаны
                    if (!task.isDataProcessed()) {
                        shouldProcess = true;
                        task.setDataProcessed(true); // Помечаем как обработанные
                    }
                } finally {
                    semaphore.release();
                }
                // После semaphore.release():
                synchronized (task) {
                    task.setDataProcessed(true);
                    task.notifyAll();
                }

                // Пропускаем, если данные уже обработаны
                if (!shouldProcess || function == null) {
                    continue;
                }

                try {
                    // Вычисляем интеграл
                    double result = Functions.Integrate(function, leftBorder, rightBorder, step);

                    // Выводим результат
                    System.out.printf("Result %.6f %.6f %.6f %.6f\n",
                            leftBorder, rightBorder, step, result);

                } catch (IllegalArgumentException e) {
                    System.out.printf("Error: %s for bounds [%.6f, %.6f] step %.6f\n",
                            e.getMessage(), leftBorder, rightBorder, step);
                }

                Thread.sleep(2);
            }
        } catch (InterruptedException e) {
            System.out.println("Integrator was interrupted during sleep or semaphore acquisition");
        }
    }
}