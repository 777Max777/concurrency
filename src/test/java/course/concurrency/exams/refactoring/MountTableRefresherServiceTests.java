package course.concurrency.exams.refactoring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;

public class MountTableRefresherServiceTests {

    private MountTableRefresherService service;

    private Others.RouterStore routerStore;
    private Others.MountTableManager manager;
    private Others.MountTableManager managerTimeout;
    private Others.MountTableManager managerException;
    private Others.LoadingCache routerClientsCache;

    private ManagerFactory remoteManager;
    private ManagerFactory localManager;

    @BeforeEach
    public void setUpStreams() {
        service = new MountTableRefresherService();
        service.setCacheUpdateTimeout(100L);
        routerStore = mock(Others.RouterStore.class);
        manager = mock(Others.MountTableManager.class);
        managerTimeout = mock(Others.MountTableManager.class);
        managerException = mock(Others.MountTableManager.class);
        remoteManager = mock(RemoteManagerFactory.class);
        localManager = mock(LocalManagerFactory.class);
        service.setRouterStore(routerStore);
        service.setLocalManagerCreator(localManager);
        service.setRemoteManagerCreator(remoteManager);
        routerClientsCache = mock(Others.LoadingCache.class);
        service.setRouterClientsCache(routerClientsCache);
        // service.serviceInit(); // needed for complex class testing, not for now
    }

    @AfterEach
    public void restoreStreams() {
        // service.serviceStop();
    }

    @Test
    @DisplayName("All tasks are completed successfully")
    public void allDone() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(manager.refresh()).thenReturn(true);

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more
        when(remoteManager.create(anyString())).thenReturn(manager);
        when(localManager.create(anyString())).thenReturn(manager);
        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=4,failureCount=0");
        verify(routerClientsCache, never()).invalidate(anyString());
    }

    @Test
    @DisplayName("All tasks failed")
    public void noSuccessfulTasks() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(manager.refresh()).thenReturn(false);

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more
        when(remoteManager.create(anyString())).thenReturn(manager);
        when(localManager.create(anyString())).thenReturn(manager);
        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=0,failureCount=4");
        verify(routerClientsCache, atLeast(addresses.size())).invalidate(anyString());
    }

    @Test
    @DisplayName("Some tasks failed")
    public void halfSuccessedTasks() {

        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(manager.refresh()).thenReturn(true, false, true, false);

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more
        when(remoteManager.create(anyString())).thenReturn(manager);
        when(localManager.create(anyString())).thenReturn(manager);
        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=2,failureCount=2");
        verify(routerClientsCache, atLeast(addresses.size()/2)).invalidate(anyString());
    }

    @Test
    @DisplayName("One task completed with exception")
    public void exceptionInOneTask() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "759", "789", "local");

        when(manager.refresh()).thenReturn(true, false, true, false);

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more
        when(remoteManager.create(anyString())).thenReturn(manager);
        when(localManager.create(anyString())).thenReturn(manager);
        // when
        mockedService.refresh();

        // then
//        verify(mockedService).log("Mount table cache refresher was interrupted");
        verify(routerClientsCache, atLeast(addresses.size()/2)).invalidate(anyString());
    }

    @Test
    @DisplayName("One task exceeds timeout")
    public void oneTaskExceedTimeout() {

    }

}
