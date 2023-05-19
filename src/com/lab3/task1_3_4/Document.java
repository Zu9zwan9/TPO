package com.lab3.task1_3_4;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Document(String fileName, ArrayList<String> lines) {

    /**
     * Завантаження документу з файлу, використовуючи FileStream
     *
     * @param fileName ім'я файлу
     * @return об'єкт типу Document
     */
    public static Document loadFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        ArrayList<String> fileLines = Files.lines(Paths.get(file.getAbsolutePath()))
                .flatMap(s -> Stream.of(s.split("/n")))
                .map(line -> line.replaceAll("[^A-Za-z/s ]+", ""))
                .collect(Collectors.toCollection(ArrayList::new));
        return new Document(fileName, fileLines);
    }

    /**
     * Рахує довжини слів в одному рядку
     *
     * @param line рядок
     * @return перелік довжин слів
     */
    static ArrayList<Integer> wordsCountInOneLine(String line) {
        return new ArrayList<>(List.of(line.split(" "))).stream()
                .map(String::length)
                .filter(s -> s != 0)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Рахує довжини слів в усьому документі, використовуючи паралелізм
     *
     * @param doc документ
     * @return перелік довжин слів, виключаючи нулі
     */
    public static ArrayList<Integer> wordsCountInDoc(Document doc) {
        CustomRecursiveTask customRecursiveTask = new CustomRecursiveTask(doc.lines().toArray(new String[0]));
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.execute(customRecursiveTask);
        return customRecursiveTask.join();
    }

    /**
     * Повертає кількість входжень слова в заданому тексті (String)
     *
     * @param line текст (рядок)
     * @param word слово
     * @return кількість входжень
     */
    static Integer lookForWordInLine(String line, String word) {
        ArrayList<String> filtered = new ArrayList<>(List.of(line.split(" "))).stream()
                .filter(s -> Objects.equals(s, word))
                .collect(Collectors.toCollection(ArrayList::new));
        return filtered.size();
    }

    /**
     * Шукає кількість входжень слова в документі (набір рядків)
     * Використовує ForkJoinPool
     *
     * @param doc  документ
     * @param word слово
     * @return кількість входжень
     */
    public static Integer lookForWordInDoc(Document doc, String word) {
        AnotherCustomRecursiveTask customRecursiveTask = new AnotherCustomRecursiveTask(
                doc.lines().toArray(new String[0]), word);
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.execute(customRecursiveTask);
        return customRecursiveTask.join();
    }

    /**
     * Обчислює кількість входжень кожного з заданих слів
     * Використовує ExecutorService (Threads)
     *
     * @param doc   документ, в якому йде пошук
     * @param words перелік слів
     * @return кількість входжень кожного з слів
     */
    public static ArrayList<Integer> lookForListOfWordsInDoc(Document doc, String[] words) throws InterruptedException {
        Collection<Callable<Integer>> callableTasks = Arrays.stream(words)
                .map(p -> (Callable<Integer>) () -> Document.lookForWordInDoc(doc, p))
                .collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(words.length);
        List<Future<Integer>> futures = executorService.invokeAll(callableTasks);
        executorService.shutdown();
        return futures.stream()
                .map(s -> {
                            try {
                                return s.get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                )
                .collect(Collectors.toCollection(ArrayList::new));
    }

    static <T> ArrayList<T> deleteDuplicates(ArrayList<T> data) {
        Set<T> set = new HashSet<>(data);
        data.clear();
        data.addAll(set);
        return data;
    }

    public String[] getAsArrayOfWords() {
        ArrayList<ArrayList<String>> words = this.lines.stream()
                .map(s -> Arrays.stream(s.split(" "))
                        .filter(word -> word.length() > 0)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(ArrayList::new));
        return deleteDuplicates(CustomRecursiveTask.flattenList(words)).toArray(String[]::new);

    }
}

/**
 * Рекурсивна задача для ForkJoinPool
 */
class CustomRecursiveTask extends RecursiveTask<ArrayList<Integer>> {

    private static final int THRESHOLD = 1;
    private final String[] arr;

    /**
     * Конструктов
     *
     * @param arr перелік рядків (масив)
     */

    CustomRecursiveTask(String[] arr) {
        this.arr = arr;
    }

    /**
     * Згладжування ArrayList<ArrayList<>> в одновимірний ArrayList<>
     */
    static <T> ArrayList<T> flattenList(ArrayList<ArrayList<T>> nestedList) {
        ArrayList<T> ls = new ArrayList<>();
        nestedList.forEach(ls::addAll);
        return ls;
    }

    /**
     * Рекурсивне створення задач
     *
     * @return Collection задач, на який була розподілена задача
     */
    static Collection<CustomRecursiveTask> createSubtasks(String[] arr) {
        List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
        dividedTasks.add(new CustomRecursiveTask(
                Arrays.copyOfRange(arr, 0, arr.length / 2)));
        dividedTasks.add(new CustomRecursiveTask(
                Arrays.copyOfRange(arr, arr.length / 2, arr.length)));
        return dividedTasks;
    }

    /**
     * Метод, що обчислює.
     * Якщо довжина масиву рядків, що переданий більше одиниці, то задача розподіляється
     * Інакше викликається розрахунок
     *
     * @return перелік довжин слів
     */
    @Override
    protected ArrayList<Integer> compute() {
        if (arr.length > THRESHOLD) {
            ArrayList<ArrayList<Integer>> result = ForkJoinTask.invokeAll(createSubtasks(arr)).stream()
                    .map(ForkJoinTask::join)
                    .collect(Collectors.toCollection(ArrayList::new));
            return flattenList(result);
        } else {
            return Document.wordsCountInOneLine(arr[0]);
        }
    }
}

class AnotherCustomRecursiveTask extends RecursiveTask<Integer> {

    private static final int THRESHOLD = 1;
    private final String[] arr;
    private final String word;

    /**
     * Конструктов
     *
     * @param arr перелік рядків (масив)
     */

    AnotherCustomRecursiveTask(String[] arr, String word) {
        this.arr = arr;
        this.word = word;
    }

    static Collection<AnotherCustomRecursiveTask> createSubtasks(String[] arr, String word) {
        List<AnotherCustomRecursiveTask> dividedTasks = new ArrayList<>();
        dividedTasks.add(new AnotherCustomRecursiveTask(
                Arrays.copyOfRange(arr, 0, arr.length / 2), word));
        dividedTasks.add(new AnotherCustomRecursiveTask(
                Arrays.copyOfRange(arr, arr.length / 2, arr.length), word));
        return dividedTasks;
    }

    /**
     * Метод, що обчислює.
     * Якщо довжина масиву рядків, що переданий більше одиниці, то задача розподіляється
     * Інакше викликається розрахунок
     *
     * @return перелік довжин слів
     */
    @Override
    protected Integer compute() {
        if (arr.length > THRESHOLD) {
            Collection<AnotherCustomRecursiveTask> subTasks = createSubtasks(arr, this.word);
            return ForkJoinTask.invokeAll(subTasks).stream()
                    .map(ForkJoinTask::join)
                    .reduce(0, Integer::sum);
        } else {
            return Document.lookForWordInLine(arr[0], word);
        }
    }


}
