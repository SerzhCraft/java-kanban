import main.java.managers.FileBackedTaskManager;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private Path path;

    @BeforeEach
    public void setUp() throws IOException {
        path = Path.of("test_tasks.csv");
        // Создаем пустой файл перед каждым тестом
        Files.createFile(path);
        manager = new FileBackedTaskManager(path);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(path); // Удаляем файл после каждого теста
    }

    @Test
    public void testCreateAndLoadTasks() throws IOException {
        Task task1 = Task.createWithId(1, "Задача 1", "Описание задачи 1");
        Epic epic1 = Epic.createWithId(2, "Эпик 1", "Описание эпика 1");
        Subtask subtask1 = Subtask.createWithId(3, "Подзадача 1", "Описание подзадачи 1", epic1);

        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(path);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        assertEquals(task1.getName(), loadedManager.getAllTasks().getFirst().getName());
        assertEquals(epic1.getName(), loadedManager.getAllEpics().getFirst().getName());
        assertEquals(subtask1.getName(), loadedManager.getAllSubtasks().getFirst().getName());
    }

    @Test
    public void testUpdateTask() throws IOException {
        Task task = Task.createWithId(1, "Задача 1", "Описание задачи 1");
        manager.createTask(task);
        manager.save();

        task.setName("Обновленная Задача 1");
        manager.updateTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(path);

        assertEquals("Обновленная Задача 1", loadedManager.getAllTasks().getFirst().getName());
    }

    @Test
    public void testDeleteTaskById() throws IOException {
        Task task = Task.createWithId(1, "Задача 1", "Описание задачи 1");
        manager.createTask(task);
        manager.save();

        manager.deleteTaskById(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(path);

        assertTrue(loadedManager.getAllTasks().isEmpty());
    }

    @Test
    public void testLoadFromEmptyFile() throws IOException {
        // Поскольку файл уже создан в setUp и он пустой,
        // мы можем просто загрузить его и проверить.

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(path);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }
}