package main.java.managers;

import main.java.enums.TaskStatus;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTaskManager(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic\n");

        for (Task task : getAllTasks()) {
            sb.append(toString(task)).append("\n");
        }

        for (Epic epic : getAllEpics()) {
            sb.append(toString(epic)).append("\n");
            for (Subtask subtask : getSubtaskByEpicId(epic.getId())) {
                sb.append(toString(subtask)).append("\n");
            }
        }

        try {
            Files.writeString(filePath, sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving data", e);
        }
    }

    private String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,", task.getId(), task.getType(), task.getName(), task.getTaskStatus(), task.getDescription());
    }

    private String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,", epic.getId(), epic.getType(), epic.getName(), epic.getTaskStatus(), epic.getDescription());
    }

    private String toString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), subtask.getType(), subtask.getName(), subtask.getTaskStatus(), subtask.getDescription(), subtask.getEpic().getId());
    }

    public static FileBackedTaskManager loadFromFile(Path filePath) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(filePath);

        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filePath);
        }

        List<String> lines = Files.readAllLines(filePath);

        // Проверяем, есть ли строки после заголовка
        if (lines.size() > 1) { // Если есть хотя бы одна строка данных
            // Сначала создаем мапу для хранения загруженных эпиков
            Map<Integer, Epic> epicMap = new HashMap<>();

            for (String line : lines.subList(1, lines.size())) { // Пропускаем заголовок
                Task task = fromString(line, epicMap);
                switch (task.getType()) {
                    case TASK:
                        manager.createTask(task); // Создаем обычную задачу
                        break;
                    case EPIC:
                        Epic epic = (Epic) task; // Приводим к Epic
                        epicMap.put(epic.getId(), epic);
                        manager.createEpic(epic); // Создаем эпик в менеджере
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task; // Приводим к Subtask
                        Epic associatedEpic = epicMap.get(subtask.getEpicId());
                        if (associatedEpic != null) {
                            subtask.setEpic(associatedEpic); // Устанавливаем связь с эпиком
                            manager.createSubtask(subtask); // Создаем подзадачу в менеджере
                        } else {
                            throw new IllegalArgumentException("Epic not found for subtask: " + subtask);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown type: " + task.getType());
                }
            }
        }
        return manager;
    }

    private static Task fromString(String value, Map<Integer, Epic> epicMap) {
        String[] parts = value.split(",");

        if (parts.length < 5) {
            throw new IllegalArgumentException("Not enough data to create a task: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case "TASK":
                Task task = Task.createWithId(id, name, description);
                task.setTaskStatus(status);
                return task;
            case "EPIC":
                Epic epic = Epic.createWithId(id, name, description);
                epic.setTaskStatus(status);
                return epic;
            case "SUBTASK":
                if (parts.length < 6) {
                    throw new IllegalArgumentException("Not enough data to create a subtask: " + value);
                }
                int epicId = Integer.parseInt(parts[5]);
                Epic associatedEpic = epicMap.get(epicId); // Получаем связанный эпик из мапы
                if (associatedEpic == null) {
                    throw new IllegalArgumentException("Epic not found for subtask: " + value);
                }
                Subtask subtask = Subtask.createWithId(id, name, description, associatedEpic); // Используем существующий эпик
                subtask.setTaskStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public static void main(String[] args) {
        Path path = Path.of("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        Task task1 = Task.createWithId(1, "Task 1", "Description 1");
        Task task2 = Task.createWithId(2, "Task 2", "Description 2");

        Epic epic1 = Epic.createWithId(3, "Epic 1", "Description 1");

        Subtask subtask1 = Subtask.createWithId(4, "Subtask 1", "Description 1", epic1);
        Subtask subtask2 = Subtask.createWithId(5, "Subtask 2", "Description 2", epic1);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.save();

        try {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(path);

            System.out.println("Uploaded tasks:");
            for (Task task : loadedManager.getAllTasks()) {
                System.out.println(task);
            }

            System.out.println("Uploaded epics:");
            for (Epic epic : loadedManager.getAllEpics()) {
                System.out.println(epic);
            }

            System.out.println("Uploaded subtask:");
            for (Subtask subtask : loadedManager.getAllSubtasks()) {
                System.out.println(subtask);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
