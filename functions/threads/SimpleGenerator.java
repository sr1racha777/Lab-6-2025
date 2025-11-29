package functions.threads;

import functions.basic.Log;


public class SimpleGenerator implements Runnable {
    private final Task task;

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTasksCount(); i++) {
            // Генерируем случайные параметры
            double base = 1 + Math.random() * 9;
            if (Math.abs(base - 1.0) < 1e-10) {
                base = 1.1;
            }

            Log logFunction = new Log(base);
            double leftBorder = Math.random() * 100;
            double rightBorder = 100 + Math.random() * 100;
            double step = Math.random();

            // Синхронизация для устранения рассогласования данных
            synchronized (task) {
                // Устанавливаем параметры задания
                task.setFunction(logFunction);
                task.setLeftBorder(leftBorder);
                task.setRightBorder(rightBorder);
                task.setDiscretizationStep(step);

                // Выводим сообщение
                System.out.printf("Source %.6f %.6f %.6f\n", leftBorder, rightBorder, step);
            }

            try {
                // Небольшая задержка для наглядности
                Thread.sleep(2);
            } catch (InterruptedException e) {
                System.out.println("Generator was interrupted");
                return;
            }
        }
    }
}