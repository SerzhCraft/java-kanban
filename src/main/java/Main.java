package main.java;

import main.java.enums.TaskStatus;
import main.java.managers.Managers;
import main.java.managers.TaskManager;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Task 1's description");
        Task task2 = new Task("Task 2", "Task 2's description");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        printAllTasks(taskManager);

        Epic epic1 = new Epic("Epic 1", "Epic 1's description");
        taskManager.createEpic(epic1);

        printAllTasks(taskManager);

        Subtask subtask1 = new Subtask("Subtask 1.1", "Subtask 1.1 's description", epic1);
        Subtask subtask2 = new Subtask("Subtask 1.2", "Subtask 1.2 's description", epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        printAllTasks(taskManager);

        Epic epic2 = new Epic("Epic 2", "Epic 2's description");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask 2.1", "Subtask 2.1's description", epic2);
        taskManager.createSubtask(subtask3);

        printAllTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        printAllTasks(taskManager);

        taskManager.getEpicById(epic1.getId());
        printAllTasks(taskManager);

        taskManager.getSubtaskById(subtask1.getId());
        printAllTasks(taskManager);

        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        task1.setDescription("New description");
        taskManager.updateTask(task1);
        taskManager.getTaskById(task1.getId());

        subtask1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        subtask3.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        printAllTasks(taskManager);

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        printAllTasks(taskManager);



    }


    private static void printAllTasks(TaskManager manager) {
        System.out.println("Tasks:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Epics:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Subtask subtask : manager.getSubtaskByEpicId(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Subtasks:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("History:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }

}
