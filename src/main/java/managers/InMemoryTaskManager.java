package main.java.managers;

import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int idNumber = 1;
    private final HistoryManager<Task> historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));

    private int generateId() {
        if (idNumber == Integer.MAX_VALUE) {
            idNumber = 1;
        }
        return idNumber++;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Методы для класса main.java.models.Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        if (hasOverlap(task)) {
            throw new IllegalArgumentException("The task overlaps in time with another task!");
        }

        int newId = generateId();
        Task newTask = Task.createWithId(newId, task.getName(), task.getDescription());
        newTask.setTaskStatus(task.getTaskStatus());
        newTask.setDuration(task.getDuration());
        newTask.setStartTime(task.getStartTime());

        tasks.put(newId, newTask);
        addToPrioritized(newTask);

        return newTask;
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            // Удаляем старую задачу из приоритетного списка
            removeFromPrioritized(oldTask);

            // Обновляем данные задачи
            oldTask.setName(task.getName());
            oldTask.setDescription(task.getDescription());
            oldTask.setDuration(task.getDuration());
            oldTask.setStartTime(task.getStartTime()); // Если нужно обновить время

            // Добавляем обновленную задачу обратно в приоритетный список
            addToPrioritized(oldTask);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task oldTask = tasks.remove(id);
        if (oldTask != null) {
            removeFromPrioritized(oldTask);
            historyManager.remove(id);
        }
    }

    // Методы для класса Epics
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int newId = generateId();

        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic newEpic = Epic.createWithId(newId,
                epic.getName(),
                epic.getDescription(),
                duration,
                startTime);

        epics.put(newId, newEpic);
        return newEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtasks().forEach(subtask -> {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            });
            historyManager.remove(id);
        }
    }

    // Методы для класса main.java.models.Subtask
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        });
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (hasOverlapWithEpicSubtasks(subtask)) {
            throw new IllegalArgumentException("The subtask overlaps in time with another subtask in the same epic!");
        }

        int newId = generateId();
        int epicId = subtask.getEpic().getId();
        Epic epic = epics.get(epicId);

        Subtask newSubtask = Subtask.createWithId(
                newId,
                subtask.getName(),
                subtask.getDescription(),
                epic,
                subtask.getDuration(),
                subtask.getStartTime());

        newSubtask.setTaskStatus(subtask.getTaskStatus());

        subtasks.put(newId, newSubtask);

        if (epic != null) {
            epic.addSubtask(newSubtask);
            updateEpicStatus(epic);
        } else {
            System.out.println("Epic not found for ID: " + epicId);
        }

        addToPrioritized(newSubtask);
        return newSubtask;
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }

        subtasks.put(newSubtask.getId(), newSubtask);

        Epic epic = getEpicById(newSubtask.getEpicId());
        if (epic != null) {
            epic.updateStatus();
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask oldSubtask = subtasks.remove(id);
        if (oldSubtask != null) {
            removeFromPrioritized(oldSubtask);
            Epic epic = oldSubtask.getEpic();
            epic.removeSubtask(oldSubtask);
            updateEpicStatus(epic);
            historyManager.remove(id);
        }
    }

    // Метод получения списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getSubtaskByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return new ArrayList<>(epic.getSubtasks());
        }
        return Collections.emptyList();
    }

    // Метод обновления статуса класса main.java.models.Epic
    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks();

        epic.updateStatus();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // Вспомогательный метод для добавления задачи в приоритетный список
    private void addToPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    // Вспомогательный метод для удаления задачи из приоритетного списка
    private void removeFromPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    // Метод проверки пересечения двух задач
    public static boolean isTimeOverlap(Task task1, Task task2) {
        Objects.requireNonNull(task1, "Task1 can not be null");
        Objects.requireNonNull(task2, "Task2 can not be null");

        if (task1.equals(task2)) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (end1 == null || end2 == null || start1 == null || start2 == null) {
            return false;
        }

        return !start1.isAfter(end2) && !end1.isBefore(start2);

    }

    // Метод проверки пересечения задачи с любой другой
    public boolean hasOverlap(Task task) {
        if (task == null || task.getStartTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .anyMatch(other -> isTimeOverlap(task, other));
    }

    // Новый метод для проверки пересечения с подзадачами эпика
    private boolean hasOverlapWithEpicSubtasks(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpic().getId());
        if (epic == null || subtask.getStartTime() == null) {
            return false;
        }

        return epic.getSubtasks().stream()
                .anyMatch(existingSub -> isTimeOverlap(subtask, existingSub));
    }

    private boolean isTimeOverlap(Subtask newSubtask, Subtask existingSub) {
        LocalDateTime newStart = newSubtask.getStartTime();
        LocalDateTime newEnd = newStart.plus(newSubtask.getDuration());
        LocalDateTime existingStart = existingSub.getStartTime();
        LocalDateTime existingEnd = existingStart.plus(existingSub.getDuration());

        return (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart));
    }

    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic == null) {
            return Collections.emptyList(); // Возвращаем пустой список, если эпик не найден
        }
        return epic.getSubtasks();
    }

}