package exercise.article;


import exercise.worker.WorkerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

class WorkerImplTest {
    @InjectMocks
    private WorkerImpl worker;
    @Mock
    private LibraryImpl library;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Проверка, что метод getCatalog возвращает все названия статей")
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
    @DisplayName("Проверка, что метод getCatalog сортирует названия статей")
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
    @DisplayName("Проверка, что метод prepareArticles не изменяет корректный список")
    public void prepareArticlesShouldReturnCorrectList() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        assertEquals(articleList, worker.prepareArticles(articleList));
    }

    @Test
    @DisplayName("Проверка, что метод prepareArticles удаляет статьи без названия")
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
    @DisplayName("Проверка, что метод prepareArticles удаляет статьи без контента")
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
    @DisplayName("Проверка, что метод prepareArticles удаляет статьи без автора")
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
    @DisplayName("Проверка, что метод prepareArticles устанавливает текущую дату, если дата отсутствует")
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
    @DisplayName("Проверка, что метод prepareArticles удаляет дубликаты статей с повторяющимися названиями")
    public void prepareArticlesShouldRemoveArticlesWithDublTitles() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название1", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 15)));
        assertEquals(1, worker.prepareArticles(articleList).size());

    }

    @Test
    @DisplayName("Проверка, что метод addNewArticles сохраняет статьи в библиотеке")
    public void addNewArticlesShouldSaveArticles() {
        List<Article> articleList = Arrays.asList(new Article("Название1",
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)),
                new Article("Название2", "Контент2", "Автор2",
                        LocalDate.of(2024, 4, 14)));
        worker.addNewArticles(articleList);
        Mockito.verify(library).store(2024, articleList);
    }

    @Test
    @DisplayName("Проверка, что метод addNewArticles обновляет каталог только после сохранения")
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
    @DisplayName("Проверка, что метод addNewArticles не обновляет каталог, если сохраненных статей нет")
    public void addNewArticlesShouldNotUpdateDirWithoutSaving() {
        List<Article> articleList = Arrays.asList(new Article(null,
                        "Контент1", "Автор1", LocalDate.of(2024, 4, 15)));
        worker.addNewArticles(articleList);
        Mockito.verify(library, Mockito.never()).updateCatalog();
    }

    @Test
    @DisplayName("Проверка, что метод addNewArticles производит сортировку статей по годам")
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