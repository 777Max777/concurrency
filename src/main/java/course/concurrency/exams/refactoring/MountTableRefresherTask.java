package course.concurrency.exams.refactoring;

public class MountTableRefresherTask {
    Boolean succeed;
    String adminAddress;

    public MountTableRefresherTask(Boolean succeed, String adminAddress) {
        this.succeed = succeed;
        this.adminAddress = adminAddress;
    }

    public MountTableRefresherTask(Boolean succeed) {
        this.succeed = succeed;
    }

    public Boolean isSuccess() {
        return succeed;
    }

    public void setSucceed(Boolean succeed) {
        this.succeed = succeed;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }
}
