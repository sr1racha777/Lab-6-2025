package functions;

import java.io.*;
import java.io.StreamTokenizer;

public class TabulatedFunctions {

    private TabulatedFunctions() {} // запрет на создание объектов

    // Табулирование функции
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше 2");
        }

        double[] yValues = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            yValues[i] = function.getFunctionValue(x);
        }

        return new ArrayTabulatedFunction(leftX, rightX, yValues);
    }

    // БИНАРНЫЙ ВЫВОД
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        try {
            DataOutputStream dataOut = new DataOutputStream(out);
            int count = function.getPointsCount();
            dataOut.writeInt(count);
            for (int i = 0; i < count; i++) {
                FunctionPoint p = function.getPoint(i);
                dataOut.writeDouble(p.getX());
                dataOut.writeDouble(p.getY());
            }
            dataOut.flush();
        } catch (IOException e) {
            System.err.println("Ошибка записи табулированной функции: " + e.getMessage());
        }
    }

    // БИНАРНЫЙ ВВОД
    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        try {
            DataInputStream dataIn = new DataInputStream(in);
            int count = dataIn.readInt();
            double[] yValues = new double[count];
            double leftX = 0;
            double rightX = 0;
            for (int i = 0; i < count; i++) {
                double x = dataIn.readDouble();
                double y = dataIn.readDouble();
                yValues[i] = y;
                if (i == 0) leftX = x;
                if (i == count - 1) rightX = x;
            }
            return new ArrayTabulatedFunction(leftX, rightX, yValues);
        } catch (IOException e) {
            System.err.println("Ошибка чтения табулированной функции: " + e.getMessage());
            return null;
        }
    }

    // ТЕКСТОВЫЙ ВЫВОД
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        try {
            int count = function.getPointsCount();
            out.write(count + "\n");
            for (int i = 0; i < count; i++) {
                FunctionPoint p = function.getPoint(i);
                out.write(p.getX() + " " + p.getY() + "\n");
            }
            out.flush();
        } catch (IOException e) {
            System.err.println("Ошибка записи табулированной функции в текстовый поток: " + e.getMessage());
        }
    }

    // ТЕКСТОВЫЙ ВВОД (StreamTokenizer)
    public static TabulatedFunction readTabulatedFunction(Reader in) {
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(in);
            tokenizer.parseNumbers();
            tokenizer.eolIsSignificant(false);

            tokenizer.nextToken();
            int count = (int) tokenizer.nval;
            double[] yValues = new double[count];
            double leftX = 0;
            double rightX = 0;

            for (int i = 0; i < count; i++) {
                tokenizer.nextToken();
                double x = tokenizer.nval;
                tokenizer.nextToken();
                double y = tokenizer.nval;
                yValues[i] = y;
                if (i == 0) leftX = x;
                if (i == count - 1) rightX = x;
            }

            return new ArrayTabulatedFunction(leftX, rightX, yValues);
        } catch (IOException e) {
            System.err.println("Ошибка чтения табулированной функции из текстового потока: " + e.getMessage());
            return null;
        }
    }
}
