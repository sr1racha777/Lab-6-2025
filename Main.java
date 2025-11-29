package functions;

import functions.basic.*;
import functions.threads.Task;
import functions.threads.SimpleIntegrator;
import functions.threads.SimpleGenerator;
import functions.threads.Generator;
import functions.threads.Integrator;
import java.util.concurrent.Semaphore;


public class Main {
    public static void main(String[] args) {
        testIntegration();
        nonThread();
        simpleThreads();
        complicatedThreads();
    }

    private static void testIntegration() {
        System.out.println("=== TEST INTEGRATION METHOD ===");

        // Создаем экспоненциальную функцию
        Exp expFunction = new Exp();

        // Теоретическое значение интеграла exp(x) от 0 до 1
        double theoreticalValue = Math.E - 1; // ∫exp(x)dx от 0 до 1 = exp(1) - exp(0) = e - 1

        System.out.println("Теоретическое значение интеграла exp(x) от 0 до 1: " + theoreticalValue);
        System.out.printf("Точное значение: %.10f\n", theoreticalValue);

        // Тестируем с разными шагами дискретизации
        double[] steps = {0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001};

        System.out.println("\nРезультаты интегрирования с разными шагами:");
        System.out.println("Шаг\t\tЗначение интеграла\t\tПогрешность");

        for (double step : steps) {
            try {
                double integralValue = Functions.Integrate(expFunction, 0, 1, step);
                double error = Math.abs(integralValue - theoreticalValue);
                System.out.printf("%.6f\t%.10f\t%.10f\n", step, integralValue, error);

                // Проверяем точность до 7 знака после запятой
                if (error < 1e-7) {
                    System.out.println("\n✓ Точность до 7 знака после запятой достигнута при шаге: " + step);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Ошибка при шаге " + step + ": " + e.getMessage());
            }
        }

        // Дополнительный тест с очень маленьким шагом для демонстрации
        System.out.println("\n--- Проверка предельной точности ---");
        double fineStep = 1e-6;
        double fineIntegral = Functions.Integrate(expFunction, 0, 1, fineStep);
        double fineError = Math.abs(fineIntegral - theoreticalValue);
        System.out.printf("Шаг %.2e: интеграл = %.10f, погрешность = %.2e\n",
                fineStep, fineIntegral, fineError);

        // Тестирование обработки ошибок
        System.out.println("\n=== TEST ERROR HANDLING ===");

        try {
            // Границы вне области определения
            Functions.Integrate(expFunction, -2, 1, 0.1);
        } catch (IllegalArgumentException e) {
            System.out.println("Поймано ожидаемое исключение: " + e.getMessage());
        }

        try {
            // Неправильный порядок границ
            Functions.Integrate(expFunction, 1, 0, 0.1);
        } catch (IllegalArgumentException e) {
            System.out.println("Поймано ожидаемое исключение: " + e.getMessage());
        }

        try {
            // Некорректный шаг
            Functions.Integrate(expFunction, 0, 1, -0.1);
        } catch (IllegalArgumentException e) {
            System.out.println("Поймано ожидаемое исключение: " + e.getMessage());
        }
    }

    public static void nonThread() {
        System.out.println("=== NON-THREADED ===");

        // Создаем объект задания
        Task task = new Task();
        task.setTasksCount(100); // минимум 100 заданий

        for (int i = 0; i < task.getTasksCount(); i++) {
            // Случайное основание логарифма от 1 до 10 (исключая 1)
            double base = 1 + Math.random() * 9; // от 1.000... до 10.000...
            if (Math.abs(base - 1.0) < 1e-10) {
                base = 1.1; // гарантируем, что основание != 1
            }

            // Создаем логарифмическую функцию
            Log logFunction = new Log(base);

            // Устанавливаем параметры задания
            task.setFunction(logFunction);
            task.setLeftBorder(Math.random() * 100); // от 0 до 100
            task.setRightBorder(100 + Math.random() * 100); // от 100 до 200
            task.setDiscretizationStep(Math.random()); // от 0 до 1

            // Выводим исходные данные
            System.out.printf("Source %.6f %.6f %.6f\n",
                    task.getLeftBorder(),
                    task.getRightBorder(),
                    task.getDiscretizationStep());

            try {
                // Вычисляем интеграл
                double result = Functions.Integrate(
                        task.getFunction(),
                        task.getLeftBorder(),
                        task.getRightBorder(),
                        task.getDiscretizationStep()
                );

                // Выводим результат
                System.out.printf("Result %.6f %.6f %.6f %.6f\n",
                        task.getLeftBorder(),
                        task.getRightBorder(),
                        task.getDiscretizationStep(),
                        result);

            } catch (IllegalArgumentException e) {
                System.out.printf("Error: %s for bounds [%.6f, %.6f] step %.6f\n",
                        e.getMessage(),
                        task.getLeftBorder(),
                        task.getRightBorder(),
                        task.getDiscretizationStep());
            }
        }
    }

    public static void simpleThreads() {
        System.out.println("=== SIMPLE THREADS EXECUTION ===");

        // Создаем объект задания
        Task task = new Task();
        task.setTasksCount(100); // минимум 100 заданий

        // Создаем потоки
        Thread generatorThread = new Thread(new SimpleGenerator(task));
        Thread integratorThread = new Thread(new SimpleIntegrator(task));

        // Можно экспериментировать с приоритетами:
         //generatorThread.setPriority(Thread.MAX_PRIORITY);
         //integratorThread.setPriority(Thread.MIN_PRIORITY);

        // Запускаем потоки
        generatorThread.start();
        integratorThread.start();

        // Ожидаем завершения потоков
        try {
            generatorThread.join();
            integratorThread.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread was interrupted");
        }

        System.out.println("=== SIMPLE THREADS EXECUTION COMPLETED ===");
    }

    public static void complicatedThreads() {
        System.out.println("=== COMPLICATED THREADS EXECUTION ===");

        // Создаем объект задания
        Task task = new Task();
        task.setTasksCount(100);

        // Создаем семафор (1 разрешение = взаимное исключение)
        Semaphore semaphore = new Semaphore(1);

        // Создаем потоки
        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        // Устанавливаем приоритеты (можно экспериментировать)
        generator.setPriority(Thread.MAX_PRIORITY);
        integrator.setPriority(Thread.MIN_PRIORITY);

        // Запускаем потоки
        generator.start();
        integrator.start();

        // Ждем 50ms и прерываем потоки
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.println("Main thread was interrupted");
        }

        // Прерываем потоки
        generator.interrupt();
        integrator.interrupt();

        // Ожидаем завершения потоков
        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread was interrupted while joining");
        }

        System.out.println("=== COMPLICATED THREADS EXECUTION COMPLETED ===");
    }
}
