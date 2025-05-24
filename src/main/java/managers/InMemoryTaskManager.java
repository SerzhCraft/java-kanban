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
    private final HistoryManager historyManager = Managers.getDefaultHistory();
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
        tasks.put(newId, newTask);
        addToPrioritized(newTask);
        return newTask;
    }

    @Override
    public void updateTask(Task task) {
        if (hasOverlap(task)) {
            throw new IllegalArgumentException("The task overlaps in time with another task!");
        }
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            removeFromPrioritized(oldTask);
            tasks.put(task.getId(), task);
            addToPrioritized(task);
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
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
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
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
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
        if (hasOverlap(subtask)) {
            throw new IllegalArgumentException("The subtask overlaps in time with another subtask!");
        }
        int newId = generateId();
        int epicId = subtask.getEpic().getId();
        Epic epic = epics.get(epicId);

        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Subtask newSubtask = Subtask.createWithId(
                newId,
                subtask.getName(),
                subtask.getDescription(),
                epic,
                duration,
                startTime);

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
    public void updateSubtask(Subtask subtask) {
        if (hasOverlap(subtask)) {
            throw new IllegalArgumentException("The subtask overlaps in time with another subtask!");
        }
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask != null) {
            removeFromPrioritized(oldSubtask);
            subtasks.put(subtask.getId(), subtask);
            addToPrioritized(subtask);

            Epic epic = subtask.getEpic();
            updateEpicStatus(epic);
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
        return new ArrayList<>();
    }

    // Метод обновления статуса класса main.java.models.Epic
    private void updateEpicStatus(Epic epic) {
        epic.updateStatus();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // Вспомогательный метод для добавления задачи в приоритетный список
    private void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    // Вспомогательный метод для удаления задачи из приоритетного списка
    private void removeFromPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    // Метод проверки пересечения двух задач
    public static boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() ==null ||
            task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return !task1.equals(task2) &&
                task1.getStartTime().isBefore(task2.getEndTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime());
    }

    // Метод проверки пересечения задачи с любой другой
    public boolean hasOverlap(Task task) {
        return getPrioritizedTasks().stream()
                .anyMatch(other -> isTimeOverlap(task, other));
    }

}
