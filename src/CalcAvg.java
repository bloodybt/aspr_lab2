import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

/*
* Обраховує середнє значення для масиву цілих чисел
* */
public class CalcAvg  implements Callable<Double> {
    CopyOnWriteArrayList<Integer> list;
    private  int start;
    private int stop;

    public CalcAvg(CopyOnWriteArrayList list, int start, int stop) {

        // список чисел
        this.list = list;
        // початок
        this.start = start;
        // кінець
        this.stop = stop;
    }

    @Override
    public Double call() throws Exception {
        // якщо кінець більший чим розмір масиву чи початок <0 то викличемо виключення
        if (stop>list.size()|| start<0 ) throw new RuntimeException("Не коректний індекс");
        // якщо початок більший кінця теж викличемо виключення
        if (start>stop) throw new RuntimeException("Не коректний індекс");
        // повернемо середня значення для частини списку (масиву)
        // так як sublist не включає елемент з індексом stop - то передема на 1 більше
        // отримаємо потік, перетворимо його в потік цілих чисел і порахуємо середнє значення
        return list.subList(start,stop+1).stream().mapToInt(Integer::intValue).average().getAsDouble();

    }
}
