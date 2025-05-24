package main.java.managers;

import main.java.enums.TaskStatus;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
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
        sb.append("id,type,name,status,description,duration,startTime,epic\n");

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
        return String.format("%d,%s,%s,%s,%s,%d,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getTaskStatus(),
                task.getDescription(),
                task.getDuration() != null ? task.getDuration().toMinutes() : 0,
                task.getStartTime() != null ? task.getStartTime() : "null");
    }

    private String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,%d,%s",
                epic.getId(),
                epic.getType(),
                epic.getName(),
                epic.getTaskStatus(),
                epic.getDescription(),
                epic.getDuration() != null ? epic.getDuration().toMinutes() : 0,
                epic.getStartTime() != null ? epic.getStartTime() : "null");
    }

    private String toString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d,%d,%s",
                subtask.getId(),
                subtask.getType(),
                subtask.getName(),
                subtask.getTaskStatus(),
                subtask.getDescription(),
                subtask.getEpic().getId(),
                subtask.getDuration() != null ? subtask.getDuration().toMinutes() : 0,
                subtask.getStartTime() != null ? subtask.getStartTime() : "null");
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

        if (parts.length < 7) {
            throw new IllegalArgumentException("Not enough data to create a task: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[5]));

        LocalDateTime startTime = null;
        if (!parts[6].equals("null") && !parts[6].isEmpty() && !parts[6].equals("0")) {
            try {
                startTime = LocalDateTime.parse(parts[6]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        switch (type) {
            case "TASK":
                Task task = Task.createWithId(id, name, description);
                task.setTaskStatus(status);
                task.setDuration(duration);
                task.setStartTime(startTime);
                return task;
            case "EPIC":
                Epic epic = Epic.createWithId(id, name, description, duration, startTime);
                epic.setTaskStatus(status);
                return epic;
            case "SUBTASK":
                if (parts.length < 8) {
                    throw new IllegalArgumentException("Not enough data to create a subtask: " + value);
                }
                int epicId = Integer.parseInt(parts[5]);
                Duration subtaskDuration = Duration.ofMinutes(Long.parseLong(parts[6]));
                LocalDateTime subtaskStartTime = null;
                if (parts.length > 7 && !parts[7].equals("null") && !parts[7].isEmpty() && !parts[7].equals("0")) {
                    try {
                        subtaskStartTime = LocalDateTime.parse(parts[7]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                Epic associatedEpic = epicMap.get(epicId); // Получаем связанный эпик из мапы
                if (associatedEpic == null) {
                    throw new IllegalArgumentException("Epic not found for subtask: " + value);
                }
                Subtask subtask = Subtask.createWithId(id,
                        name,
                        description,
                        associatedEpic,
                        subtaskDuration,
                        subtaskStartTime);
                subtask.setTaskStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public static void main(String[] args) {
        Path path = Path.of("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        Duration duration = Duration.ofMinutes(30);
        LocalDateTime startTime = LocalDateTime.now();

        Task task1 = Task.createWithId(1, "Task 1", "Description 1");
        Task task2 = Task.createWithId(2, "Task 2", "Description 2");

        Epic epic1 = Epic.createWithId(3, "Epic 1", "Description 1", duration, startTime);

        Subtask subtask1 = Subtask.createWithId(4,
                "Subtask 1",
                "Description 1",
                epic1,
                duration,
                startTime);
        Subtask subtask2 = Subtask.createWithId(5,
                "Subtask 2",
                "Description 2",
                epic1,
                duration,
                startTime);

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
