import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class Main {
    static void withThreads() throws IOException { //
        int THREADS = 1900;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));//Позволяем пользователю взаимодействовать с клавиатурой
        System.out.println("Пожалуйста, введите ваше предложение: ");
        String st = br.readLine();//читка полученной строки
        String[] words = st.toLowerCase().split("[\\s,. :\"';%№!?]+");//переводим массиво слов в нижний регистр, при этом разделяем слова между собой по правилу регексов
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(words)); //лист всем элементов
        HashSet<String> hashSet = new HashSet<>(Arrays.asList(words));//лист содержащий уникальные слова (т е без повторов)
        System.out.println(arrayList);//вывод на консоль для удобства видимости
        System.out.println(hashSet);
        final Integer[] arr = new Integer[hashSet.size()];//создаем массив arr для итогового получения частот каждого слова (например, [1,3,4,5,5])
        for (int i = 0; i < hashSet.size(); i++) {
            arr[i] = 0;//заполняем массив нулями чтобы в дальнейшем корректировать его(инкрементировать если нужно)
        }
        long allTime = 0;//переменная, складывающая в себя всю сумму времени потраченного на программу
        AtomicInteger count = new AtomicInteger(); //переменная, считающая сумму чисел в массиве arr (в итоге она должна быть равна размеру общего массива)
        for (int i = 0; i < THREADS; i++) {
            long start = System.nanoTime();//фиксирование начала времени
            int finalI = i;
            Thread thread = new Thread(() -> {
                Iterator<String> p = hashSet.iterator();
                long time = allTime;
                for (int t = 0 ; t < hashSet.size(); t++) {//проход по уникальным элементам хэш сета
                    String word = p.next();
                    for (int j =  finalI; j < arrayList.size(); j += THREADS) {//делим на несколько кусков для одновременного выполнения тредами
                        if (words[j].equals(word)) {//условие, если слово == тому, которого ищем частоту
                            arr[t]++;//инкрементируем определенный индекс массива (он соответсвует частоте употребления определенного слова)
                            count.getAndIncrement();//увеличиваем общую сумму чисел в массиве arr
                            words[j] = "-";//заменяет данное слово на прочерк, чтобы в следющем проходу уже не учитывать данное слово
                        }
                    }
                    if (count.intValue() == arrayList.size()) {//при равенстве суммы чисел и размера массива,
                        long finish = System.nanoTime();//фиксируем время финиша
                        time += (finish - start);//прибавляем к общему времени данный промежуток
                        System.out.println("Время затраченное с потоками(нс) = " + time);//выводим
                        break;//выходим из данного цикла
                    }
                }

                if (count.intValue() == arrayList.size()){//при равенстве суммы чисел и размера массива,
                    Iterator<String> iter = hashSet.iterator();
                    for (int j = 0; j < hashSet.size(); j++) {//проход по уникальным элементам в предложении
                        String word = iter.next();
                        System.out.println("Частота употребления слова - " + word + " = " + arr[j]);//выводим на консоль  частоту каждого слова
                    }
                }
            });
            thread.start();//стартуем поток
        }
        }
//данная программа подобна прошлой, однако выполнена без использования потоков
    //данную программу можно было бы сократить, однако хотелось провести опыт выявления
// скорости работ, при котором программы были бы максимально идентичны.
    static void withoutThreads() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Пожалуйста, введите ваше предложение: ");
        String st = br.readLine();
        String[] words = st.toLowerCase().split("[\\s,. :\"';%№!?]+");//массив всех слов разделенных
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(words)); //лист в который мы будем пихать слова которые уже подсчитали
        HashSet<String> hashSet = new HashSet<>(Arrays.asList(words));
        System.out.println(arrayList);
        System.out.println(hashSet);
        final Integer[] arr = new Integer[hashSet.size()];
        for (int i = 0; i < hashSet.size(); i++) {
            arr[i] = 0;
        }
        long allTime = 0;
        AtomicInteger count = new AtomicInteger();
        long start = System.nanoTime();
            Iterator<String> p = hashSet.iterator();
            long time = allTime;
            for (int t = 0 ; t < hashSet.size(); t++) {
                String word = p.next();
                for (int j = 0; j < arrayList.size(); j ++) {
                    if (words[j].equals(word)) {
                        arr[t]++;
                        count.getAndIncrement();
                        words[j] = "-";
                    }
                }
                if (count.intValue() == arrayList.size()) {
                    long finish = System.nanoTime();
                    time += (finish - start);
                    System.out.println("Время затраченное без потоков(нс) = " + time);
                    break;
                }
            }
        if (count.intValue() == arrayList.size()){
            Iterator<String> iter = hashSet.iterator();
            for (int j = 0; j < hashSet.size(); j++) {
                String word = iter.next();
                System.out.println("Частота употребления слова - " + word + " = " + arr[j]);
            }
        }
    }
    }



