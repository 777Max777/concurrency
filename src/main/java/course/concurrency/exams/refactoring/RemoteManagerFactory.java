package course.concurrency.exams.refactoring;

public class RemoteManagerFactory implements ManagerFactory {

    @Override
    public Others.MountTableManager create(String adminAddress) {
        return new Others.MountTableManager(adminAddress);
    }
}
