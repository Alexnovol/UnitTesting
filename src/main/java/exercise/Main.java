package exercise;


import example.ExampleController;
import example.person.PersonRepositoryImpl;
import exercise.article.Article;
import exercise.article.LibraryImpl;
import exercise.worker.Worker;
import exercise.worker.WorkerImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        LibraryImpl library = new LibraryImpl();
        WorkerImpl worker = new WorkerImpl(library);
        List<Article> articleList = new ArrayList<>();
        articleList.add(new Article("Название2", "Контент1", "Автор1",
                 LocalDate.of(2024, 4, 15)));
        articleList.add(new Article("Название2", "Контент2", "Автор2",
                LocalDate.of(2024, 4, 15)));
        worker.addNewArticles(articleList);
        System.out.println(worker.getCatalog());

    }
}