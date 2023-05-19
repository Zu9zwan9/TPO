package com.lab3.task1_3_4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 1.Побудувати алгоритм статистичного аналізу тексту та визначте характеристики випадкової величини
 * «довжина слова в символах» з використанням ForkJoinFramework. Тобто випадкова величина “довжина слова в символах”
 * (тобто в тексті зустрічаються слова різної довжини),
 * треба обчислити її характеристики: Мат. сподіванння, Дисперсію та Середнє кв. відхилення.
 * <p>
 * 3. Розробити та реалізувати алгоритм пошуку спільних слів в текстових документах з використанням ForkJoinFramework.
 * <p>
 * 4. Розробити та реалізувати алгоритм пошуку текстових документів, які відповідають заданим ключовим словам
 * (належать до області «Інформаційні технології»), з використанням ForkJoinFramework.
 */

public class TextProcessor {

    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<String> filesPaths = new ArrayList<>(List.of(new String[]{"Full Stack Web Developer.txt",
                "Full Stack Wordpress developer.txt", "Full Stack WordPress Engineer.txt", "Full Stack Yii2 Developer.txt",
                "Full Stack розробник (PHP JavaScript).txt", "Full Stack розробник з_ знанням англ_йської мови.txt",
                "Full-Stack  (Laravel, Vue)  developer.txt"}));

        Document singleDocument = TextProcessor.docLoad("Full Stack WordPress Engineer.txt");
        ArrayList<Document> severalDocuments = TextProcessor.docsLoad(filesPaths);

        TextProcessor.taskOne(singleDocument);
        TextProcessor.taskThree(severalDocuments.get(0), severalDocuments.get(1));
        TextProcessor.taskFour(severalDocuments);
    }

    private static Document docLoad(String fileName) {
        try {
            String filePath = System.getProperty("user.dir") + File.separator + "assets" + File.separator + fileName;
            return Document.loadFromFile(filePath);
        } catch (IOException exception) {
            return null;
        }
    }

    private static ArrayList<Document> docsLoad(ArrayList<String> filesPaths) {
        return filesPaths.stream()
                .map(TextProcessor::docLoad)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static void taskOne(Document doc) {
        ArrayList<Integer> wordsLengths = Document.wordsCountInDoc(doc);
        Integer total = wordsLengths.stream().reduce(0, Integer::sum);
        Double mean = (double) total / (double) wordsLengths.size();
        ArrayList<Double> deviations = wordsLengths.stream().map(s -> Math.pow(Math.abs(s - mean), 2))
                .collect(Collectors.toCollection(ArrayList::new));
        double variance = deviations.stream().reduce((double) 0, Double::sum) / (double) wordsLengths.size();
        System.out.printf("Mean = %6.3f\n", mean);
        System.out.printf("Variance = %6.3f\n", variance);
        System.out.printf("RMSE = %6.3f\n", Math.sqrt(variance));
    }

    private static void taskThree(Document docOne, Document docTwo) throws InterruptedException {
        // перетворення тексту на масив слів
        String[] wordsInFirst = docOne.getAsArrayOfWords();
        String[] wordsInSecond = docTwo.getAsArrayOfWords();

        // для ітеративного перебору в потоках вибираємо менший з текстів
        String[] words = (wordsInFirst.length < wordsInSecond.length) ? wordsInFirst : wordsInSecond;
        Document doc = (wordsInFirst.length < wordsInSecond.length) ? docTwo : docOne;

        // шукаємо кількість входження кожного слова
        Collection<Callable<WordFrequency>> callableTasks = Arrays.stream(words)
                .map(p -> (Callable<WordFrequency>) () -> new WordFrequency(p, Document.lookForWordInDoc(doc, p)))
                .collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(words.length);
        List<Future<WordFrequency>> futures = executorService.invokeAll(callableTasks);
        executorService.shutdown();

        // отримуємо результат
        List<WordFrequency> result = futures.stream()
                .map(s -> {
                    try {
                        return s.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        // виводимо в консоль усі слова, що є спільними
        result.stream()
                .filter(s -> s.getFrequency() > 0)
                .map(WordFrequency::getWord)
                .forEach(System.out::println);
    }

    private static void taskFour(ArrayList<Document> docs) throws InterruptedException {
        String[] wordsToLookFor = {"HTML", "CSS", "Angular"};
        for (Document doc : docs
        ) {
            // отримує кількість входжень кожного з заданих слів в документ
            ArrayList<Integer> value = Document.lookForListOfWordsInDoc(doc, wordsToLookFor);

            // визначає скільки отримано нулів
            int numberOfZeros = value.stream().filter(s -> s == 0)
                    .collect(Collectors.toCollection(ArrayList::new)).size();

            // дає відповідь в залежності від кількості нулів
            if (numberOfZeros == 0) {
                System.out.printf("All the words are in the document with name %s\n", doc.fileName());
            } else {
                System.out.printf("Not all the words are in the document with name %s\n", doc.fileName());
            }
        }
    }
}

class WordFrequency {

    private final String word;
    private final Integer frequency;

    WordFrequency(String word, Integer freq) {
        this.word = word;
        this.frequency = freq;
    }

    Integer getFrequency() {
        return this.frequency;
    }

    String getWord() {
        return this.word;
    }
}
