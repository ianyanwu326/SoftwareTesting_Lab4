
package Library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private EmailProvider emailProvider;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private LibraryService libraryService;

    private UUID resourceId;
    private String memberEmail;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();
        memberEmail = "member@example.com";
    }

    @Test
    void checkoutResource_NullResourceId_ReturnsFalse() throws Exception {
        boolean result =
                libraryService.checkoutResource(null, memberEmail);

        assertFalse(result);
        verifyNoInteractions(resourceRepository, emailProvider);
    }

    @Test
    void checkoutResource_ResourceUnavailable_ReturnsFalse()
            throws Exception {

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(false);

        boolean result =
                libraryService.checkoutResource(resourceId, memberEmail);

        assertFalse(result);

        verify(resourceRepository).isResourceAvailable(resourceId);
        verify(resourceRepository, never())
                .updateStatus(any(), anyBoolean());
        verifyNoInteractions(emailProvider);
    }

    @Test
    void checkoutResource_AvailableResource_ChecksOutAndSendsEmail()
            throws Exception {

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenReturn(true);

        when(emailProvider.sendEmail(eq(memberEmail), anyString()))
                .thenReturn(true);

        boolean result =
                libraryService.checkoutResource(resourceId, memberEmail);

        assertTrue(result);

        verify(resourceRepository).isResourceAvailable(resourceId);
        verify(resourceRepository).updateStatus(resourceId, false);

        verify(emailProvider).sendEmail(
                eq(memberEmail),
                eq("Resource ID: " + resourceId + " checked out.")
        );
    }

    @Test
    void checkoutResource_StatusUpdateFails_ThrowsDatabaseFailureException()
            throws Exception {

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenReturn(false);

        assertThrows(
                DatabaseFailureException.class,
                () -> libraryService.checkoutResource(resourceId, memberEmail)
        );

        verify(resourceRepository).updateStatus(resourceId, false);
        verifyNoInteractions(emailProvider);
    }

    @Test
    void checkoutResource_DatabaseThrowsException_PropagatesException()
            throws Exception {

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenThrow(
                        new DatabaseFailureException("Connection failed."));

        assertThrows(
                DatabaseFailureException.class,
                () -> libraryService.checkoutResource(resourceId, memberEmail)
        );

        verifyNoInteractions(emailProvider);
    }

    @Test
    void checkoutResource_EmailReturnsFalse_ThrowsEmailFailureException()
            throws Exception {

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenReturn(true);

        when(emailProvider.sendEmail(eq(memberEmail), anyString()))
                .thenReturn(false);

        assertThrows(
                EmailFailureException.class,
                () -> libraryService.checkoutResource(resourceId, memberEmail)
        );

        verify(resourceRepository).updateStatus(resourceId, false);
        verify(emailProvider).sendEmail(eq(memberEmail), anyString());
    }

    @Test
    void checkoutResource_EmailProviderThrowsException_PropagatesException()
            throws Exception {

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenReturn(true);

        when(emailProvider.sendEmail(eq(memberEmail), anyString()))
                .thenThrow(
                        new EmailFailureException("Server unavailable."));

        assertThrows(
                EmailFailureException.class,
                () -> libraryService.checkoutResource(resourceId, memberEmail)
        );
    }
}
