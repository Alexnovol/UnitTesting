package exercise.article;


import exercise.worker.WorkerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

class WorkerImplTest {
    private WorkerImpl worker;
    @Mock
    LibraryImpl library;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        worker = new WorkerImpl(library);
    }

    @Test
    public void getCatalogShouldReturnAllTitles() {
        Mockito.when(library.getAllTitles()).thenReturn((Arrays.asList("Статья1",
                "Статья2", "Статья3")));
        String expected = "Список доступных статей:\n" +
                "    Статья1\n" +
                "    Статья2\n" +
                "    Статья3\n";
        assertEquals(expected, worker.getCatalog());
    }

    @Test
    public void getCatalogShouldSortTitles() {
        Mockito.when(library.getAllTitles()).thenReturn((
                Arrays.asList("Сколько времени нужно, чтобы выучить Java",
                        "Как правильно изучать языки программирования",
                        "Почему важны soft skills?")));
        String expected = "Список доступных статей:\n" +
                "    Как правильно изучать языки программирования\n" +
                "    Почему важны soft skills?\n" +
                "    Сколько времени нужно, чтобы выучить Java\n";
        assertEquals(expected, worker.getCatalog());
    }

    @Test
    public void prepareArticlesShouldReturnCorrectList() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        assertEquals(articleList, worker.prepareArticles(articleList));
    }

    @Test
    public void prepareArticlesShouldDeleteArticlesWithoutTitle() {
        List<Article> articleList = Arrays.asList(new Article(null,
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        List<Article> expected = Arrays.asList(new Article("Название2", "Контент2", "Автор2",
                LocalDate.of(2024, 4, 14)));
        assertEquals(expected, worker.prepareArticles(articleList));
    }

    @Test
    public void prepareArticlesShouldDeleteArticlesWithoutContent() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        null, "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        List<Article> expected = Arrays.asList(new Article("Название2", "Контент2", "Автор2",
                LocalDate.of(2024, 4, 14)));
        assertEquals(expected, worker.prepareArticles(articleList));
    }

    @Test
    public void prepareArticlesShouldDeleteArticlesWithoutAuthor() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        "Контент1", null, LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        List<Article> expected = Arrays.asList(new Article("Название2", "Контент2", "Автор2",
                LocalDate.of(2024, 4, 14)));
        assertEquals(expected, worker.prepareArticles(articleList));
    }

    @Test
    public void prepareArticlesShouldCheckDate() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        null));
        List<Article> expected = Arrays.asList(new Article("Название1",
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.now()));
        assertEquals(expected.get(1).getCreationDate(), worker.prepareArticles(articleList)
                .get(1).getCreationDate());
    }

    @Test
    public void prepareArticlesShouldRemoveArticlesWithDublTitles() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название1", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 15)));
        assertEquals(1, worker.prepareArticles(articleList).size());

    }

    @Test
    public void addNewArticlesShouldSaveArticles() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        worker.addNewArticles(articleList);
        Mockito.verify(library).store(2024, articleList);
    }

    @Test
    public void addNewArticlesShouldUpdateDirAfterSaving() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        worker.addNewArticles(articleList);
        Mockito.verify(library).store(2024, articleList);
        Mockito.verify(library).updateCatalog();
    }

    @Test
    public void addNewArticlesShouldNotUpdateDirWithoutSaving() {
        List<Article> articleList = Arrays.asList(new Article(null,
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)));
        worker.addNewArticles(articleList);
        Mockito.verify(library, Mockito.never()).updateCatalog();
    }

    @Test
    public void addNewArticlesShouldSortArticles() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        "Контент1", "Автор1", LocalDate.of(2023, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        worker.addNewArticles(articleList);
        Mockito.verify(library).store(2023, Arrays.asList(articleList.get(0)));
        Mockito.verify(library).store(2024, Arrays.asList(articleList.get(1)));
    }
}