package service;

import model.UserModel;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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


    private UserService classUnderTest = new UserService();

    private UserModel userModel;

    @BeforeEach
    void createUser(){
        userModel = new UserModel("Dragon", "G", "Secret","token", "Bio","image", 10, null , null, 50, 10,10);
    }

    @Test
    void addWinTest() {
        classUnderTest.addWin(userModel);
        assertEquals(userModel.getWins(), 11);
        assertEquals(userModel.getElo(), 55);
    }

    @Test
    void addLoseTest() {
        classUnderTest.addLoss(userModel);
        assertEquals(userModel.getLooses(), 9);
        assertEquals(userModel.getElo(), 45);
    }
}