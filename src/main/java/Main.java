package main.java;

import main.java.enums.TaskStatus;
import main.java.managers.Managers;
import main.java.managers.TaskManager;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Duration duration = Duration.ofMinutes(29);
        LocalDateTime startTime = LocalDateTime.now();

        System.out.println("=== 1. Create tasks and epics ===");

        Task task1 = new Task("Task 1", "Description 1");
        task1 = taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2");
        task2 = taskManager.createTask(task2);

        Epic epicWithSubtasks = new Epic("Epic with subtasks", "Description", duration, startTime);
        epicWithSubtasks = taskManager.createEpic(epicWithSubtasks);

        // Создание подзадач с установленным временем начала и продолжительностью
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicWithSubtasks);
        subtask1.setStartTime(startTime);
        subtask1.setDuration(duration);
        subtask1 = taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epicWithSubtasks);
        subtask2.setStartTime(startTime.plusMinutes(30)); // Начинается сразу после первой
        subtask2.setDuration(duration);
        subtask2 = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", epicWithSubtasks);
        subtask3.setStartTime(startTime.plusMinutes(60)); // Начинается сразу после второй
        subtask3.setDuration(duration);
        subtask3 = taskManager.createSubtask(subtask3);

        printAllTasks(taskManager);

        System.out.println("Let's check after changing subtask's status");

        // Обновление статуса подзадач
        updateAndPrintStatus(taskManager, subtask1);
        updateAndPrintStatus(taskManager, subtask2);
        updateAndPrintStatus(taskManager, subtask3);

        Epic epicWithoutSubtasks = new Epic("Epic without subtasks", "Description", duration, startTime);
        epicWithoutSubtasks = taskManager.createEpic(epicWithoutSubtasks);

        printAllTasks(taskManager);
        printHistory(taskManager);

        System.out.println("\n=== 2. Request tasks in different order ===");

        requestTasksInOrder(taskManager, epicWithSubtasks, task2, subtask1);

        System.out.println("\n=== 3. Delete task from the history (id=" + task2.getId() + ") ===");
        taskManager.deleteTaskById(task2.getId());

        printHistory(taskManager);

        System.out.println("\n=== 4. Delete epic with subtasks (id=" + epicWithSubtasks.getId() + ") ===");
        taskManager.deleteEpicById(epicWithSubtasks.getId());

        printHistory(taskManager);

        System.out.println("\n=== 5. Request remaining tasks again ===");

        requestRemainingTasks(taskManager, task1, epicWithoutSubtasks);

        System.out.println("\n=== 6. Change description of task 1 ===");

        changeTaskDescription(taskManager, task1);

    }

    private static void updateAndPrintStatus(TaskManager manager, Subtask subtask) {
        System.out.println(subtask.getName() + " is done:");
        subtask.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);
        printAllTasks(manager);
    }

    private static void requestTasksInOrder(TaskManager manager, Epic epicWithSubtasks, Task task2, Subtask subtask1) {
        System.out.println("Request epic with subtasks (id=" + epicWithSubtasks.getId() + ")");
        manager.getEpicById(epicWithSubtasks.getId());
        printHistory(manager);

        System.out.println("Request task 2 (id=" + task2.getId() + ")");
        manager.getTaskById(task2.getId());
        printHistory(manager);

        System.out.println("Request subtask 1 (id=" + subtask1.getId() + ")");
        manager.getSubtaskById(subtask1.getId());
        printHistory(manager);

        System.out.println("Repeat request epic with subtasks (id=" + epicWithSubtasks.getId() + ")");
        manager.getEpicById(epicWithSubtasks.getId());
        printHistory(manager);
    }

    private static void requestRemainingTasks(TaskManager manager, Task task1, Epic epicWithoutSubtasks) {
        System.out.println("Request remaining tasks:");

        System.out.println("Request task 1 (id=" + task1.getId() + ")");
        manager.getTaskById(task1.getId());
        printHistory(manager);

        System.out.println("Request epic without subtasks (id=" + epicWithoutSubtasks.getId() + ")");
        manager.getEpicById(epicWithoutSubtasks.getId());
        printHistory(manager);
    }

    private static void changeTaskDescription(TaskManager manager, Task task) {
        String newDescription = "Changed description";
        task.setDescription(newDescription); // Изменяем только описание
        manager.updateTask(task); // Обновляем задачу
        System.out.println("New description: " + newDescription);

        System.out.println("\n=== Request Changed task ===");
        manager.getTaskById(task.getId());
        printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("Current history (" + manager.getHistory().size() + " elements):");
        manager.getHistory().forEach(task -> System.out.println("- " + task));
        System.out.println();
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nAll tasks in system:");
        System.out.println("Tasks:");
        manager.getAllTasks().forEach(task -> System.out.println(" " + task));

        System.out.println("Epics:");
        manager.getAllEpics().forEach(epic -> {
            System.out.println("  " + epic);
            manager.getSubtaskByEpicId(epic.getId()).forEach(subtask ->
                    System.out.println("    -> " + subtask));
        });

        System.out.println("Subtasks:");
        manager.getAllSubtasks().forEach(subtask ->
                System.out.println("  " + subtask));
        System.out.println();
    }
}