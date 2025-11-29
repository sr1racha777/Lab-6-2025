package functions.threads;

import functions.Function;
import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTasksCount(); i++) {
            Function function;
            double leftBorder, rightBorder, step;

            // Синхронизация для устранения рассогласования данных
            synchronized (task) {
                // Получаем параметры задания
                function = task.getFunction();
                leftBorder = task.getLeftBorder();
                rightBorder = task.getRightBorder();
                step = task.getDiscretizationStep();
            }

            // Простое решение для избежания NullPointerException
            if (function == null) {
                // Если функция еще не установлена, пропускаем итерацию
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

            try {
                // Небольшая задержка для наглядности
                Thread.sleep(2);
            } catch (InterruptedException e) {
                System.out.println("Integrator was interrupted");
                return;
            }
        }
    }
}