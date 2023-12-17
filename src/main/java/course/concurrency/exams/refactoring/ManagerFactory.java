package course.concurrency.exams.refactoring;

public interface ManagerFactory {

    Others.MountTableManager create(String adminAddress);
}
