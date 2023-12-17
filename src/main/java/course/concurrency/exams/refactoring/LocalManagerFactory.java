package course.concurrency.exams.refactoring;

public class LocalManagerFactory implements ManagerFactory {

    private String DEFAULT_ADDRESS = "local";

    @Override
    public Others.MountTableManager create(String adminAddress) {
        return new Others.MountTableManager(DEFAULT_ADDRESS);
    }
}
