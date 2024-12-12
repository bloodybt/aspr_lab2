import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        // кількість елементів
        int size = 60;
        CopyOnWriteArrayList<Integer> list = generate(size);




        // кількість частин
        int part = 4;
        // кількість елементів в одній частині
        int partSize = size/part;

        // список задач
        List<Future<Double>> tasks = new ArrayList<>();
        // створимо асинхронні задачі і додамо їх в список
        for (int i = 0; i < (size); i=i+partSize) {
            tasks.add( calculateAsync(list,i,i+partSize-1));
        }

        double sum = 0.0;
        // пройдемось по задачам
        for (Future<Double> task:tasks){
            // якщо задача була прервана - то сенсу продовжувати задачу несма
            if (task.isCancelled()) throw new RuntimeException("Обчислення не може бути завершиним, бо було відмінено чи перервано");
            // якщо задача виконанан
            if (task.isDone()) sum=sum+task.get();
            // якщо ні - то зачекаємо 1 секунду
            else
                 {
                try {

                    sum = sum+ task.get(1000, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                   // якщо не виконалась за 1 секунду - то сенсу продовжувати немає
                    task.cancel(true);
                    new RuntimeException("Обчислення  не вклалось у відведений час");
                    return;
                }
            }
        }
        // порахуємо середнє
        double avg  = sum/part;
        System.out.println("Результат "+avg);
        // виведемо час виконання
        System.out.println("Код виконувався - " + (System.currentTimeMillis() - start) + " мілісекунд(и)");
        // код для перевірки правильності виконання, можна прибрати
        System.out.println("Перевірка  черещ stream "+list.stream().mapToInt(Integer::intValue).average().getAsDouble());

    }
    /*
    * повертає згенерований потоково-безпечний список( масив ) цілих чисел
    * */
    public static CopyOnWriteArrayList generate(int size){
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        for (int i = 0;i<size;i++) list.add(new Random().nextInt(1001));
        return list;
    }

    // повертає запущений поток для обрахування середнього значення з частини списку
    public static Future<Double> calculateAsync(CopyOnWriteArrayList list, int start,int stop) throws Exception {
        CalcAvg calc = new CalcAvg(list,start,stop);
        FutureTask<Double> future = new FutureTask<>(calc);
        Thread thread = new Thread(future);
        thread.start();
        return future;
    }
}