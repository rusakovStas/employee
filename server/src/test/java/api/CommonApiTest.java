package api;

import com.stasdev.backend.BackendApplication;
import common.ApiFunctions;
import common.PreConditionExecutor;
import common.impl.PreConditionExecutorSimpleImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/*
 * Главный класс от которого необходимо наследовать все остальные классы для тестирования
 * Он:
 * Запускает приложение с тестовым профайлом и настройками (рандомный порт, база H2 создается с нуля)
 * Предоставляет доступ к рест темплейту и основным методам его настройки (за счет этого можно не переживать за то что настройки прошлого теста повлияют на следующие)
 * */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {BackendApplication.class, ApiFunctions.class})
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")// переопределяем проперти для запуска
@TestInstance(TestInstance.Lifecycle.PER_CLASS)//Это необходимо что бы BeforeAll выполнялся после старта спринга (потому что будет выполняться только при создание инстанса тестового класса)
abstract class CommonApiTest {

    protected PreConditionExecutor preConditionExecutor = new PreConditionExecutorSimpleImpl();

    @Autowired
    protected ApiFunctions apiFunctions;

    @AfterEach
    public void afterEachTest(){
        preConditionExecutor.undoAll();
    }

}