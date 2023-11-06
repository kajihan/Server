import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;

public class ServerAppTest {
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClientSentMessage() {
        ClientConnection mockClientConnection = Mockito.mock(ClientConnection.class);
        String testMessage = "Hello, server!";

        mockClientConnection.sendMessage(testMessage);
        Mockito.verify(mockClientConnection, Mockito.times(1)).sendMessage(testMessage);
    }

    @Test
    public void testAddConnection() {
        ClientConnection mockClientConnection = Mockito.mock(ClientConnection.class);
        Server.activeConnections.add(mockClientConnection);

        assertTrue(Server.activeConnections.contains(mockClientConnection));
    }

    @Test
    public void testRemoveConnection() {
        ClientConnection mockClientConnection = Mockito.mock(ClientConnection.class);
        Server.activeConnections.add(mockClientConnection);

        Server.removeConnection(mockClientConnection);
        assert (!Server.activeConnections.contains(mockClientConnection));
    }
}
