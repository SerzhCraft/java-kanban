package main.java;

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

        System.out.println("=== 1. Create tasks and epics ===");

        Duration duration = Duration.ofMinutes(30);
        LocalDateTime startTime = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Description 1");
        task1 = taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2");
        task2 = taskManager.createTask(task2);

        Epic epicWithSubtasks = new Epic("Epic with subtasks", "Description", duration, startTime);
        epicWithSubtasks = taskManager.createEpic(epicWithSubtasks);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicWithSubtasks);
        subtask1 = taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epicWithSubtasks);
        subtask2 = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", epicWithSubtasks);
        subtask3 = taskManager.createSubtask(subtask3);

        Epic epicWithoutSubtasks = new Epic("Epic without subtasks", "Description", duration, startTime);
        epicWithoutSubtasks = taskManager.createEpic(epicWithoutSubtasks);

        printAllTasks(taskManager);
        printHistory(taskManager);

        System.out.println("\n=== 2. Request tasks in different order ===");
        System.out.println("Request epic with subtasks (id=" + epicWithSubtasks.getId() + ")");
        taskManager.getEpicById(epicWithSubtasks.getId());
        printHistory(taskManager);

        System.out.println("Request task 2 (id=" + task2.getId() + ")");
        taskManager.getTaskById(task2.getId());
        printHistory(taskManager);

        System.out.println("Request subtask 1 (id=" + subtask1.getId() + ")");
        taskManager.getSubtaskById(subtask1.getId());
        printHistory(taskManager);

        System.out.println("Repeat request epic with subtasks (id=" + epicWithSubtasks.getId() + ")");
        taskManager.getEpicById(epicWithSubtasks.getId());
        printHistory(taskManager);

        System.out.println("\n=== 3. Delete task from the history (id=" + task2.getId() + ") ===");
        taskManager.deleteTaskById(task2.getId());
        printHistory(taskManager);

        System.out.println("\n=== 4.  Delete epic with subtask (id=" + epicWithSubtasks.getId() + ") ===");
        taskManager.deleteEpicById(epicWithSubtasks.getId());
        printHistory(taskManager);

        System.out.println("\n=== 5. Request remaining tasks again ===");
        System.out.println("Request task 2 (id=" + task1.getId() + ")");
        taskManager.getTaskById(task1.getId());
        printHistory(taskManager);

        System.out.println("Request epic without subtasks (id=" + epicWithoutSubtasks.getId() + ")");
        taskManager.getEpicById(epicWithoutSubtasks.getId());
        printHistory(taskManager);

        System.out.println("\n=== 6. Change description of task 1 ===");
        task1.setDescription("NEW ");
        task1.setDescription("Changed description");
        taskManager.updateTask(task1);
        System.out.println("New description: " + task1.getDescription());

        System.out.println("\n=== 7. Request Changed task ===");
        taskManager.getTaskById(task1.getId());
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("Current history (" + manager.getHistory().size() + " elements):");
        for (Task task : manager.getHistory()) {
            System.out.println("- " + task);
        }
        System.out.println();
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nAll tasks in system:");
        System.out.println("Tasks:");
        for (Task task : manager.getAllTasks()) {
            System.out.println("  " + task);
        }

        System.out.println("Epics:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println("  " + epic);
            for (Subtask subtask : manager.getSubtaskByEpicId(epic.getId())) {
                System.out.println("    -> " + subtask);
            }
        }

        System.out.println("Subtasks:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println("  " + subtask);
        }
        System.out.println();
    }
}
