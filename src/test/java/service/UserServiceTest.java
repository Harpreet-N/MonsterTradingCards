package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserDtoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDtoRepository userDtoRepository;

    @InjectMocks
    private UserService classUnderTest;


    @Test
    void createNewUserWithM_shouldCreateDummyUsersToo() {
       // classUnderTest.createNewUser("KORGANA", "PASSWORD");

       // verify(userDtoRepository).createUser("KORGANA", "PASSWORD");
    }
}