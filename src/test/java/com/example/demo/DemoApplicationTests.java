package com.example.demo;

import com.example.demo.dto.QuestionsDTO;
import com.example.demo.dto.ReactionsDTO;
import com.example.demo.dto.TopicsDTO;
import com.example.demo.mapstruct.TopicsMapper;
import com.example.demo.model.Topics;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ActiveProfiles("test")
@Testcontainers
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
    @LocalServerPort
    Integer port;
    @Autowired
    TestRestTemplate restTemplate;

    @Nested
    @DisplayName("Тестирование контроллера топика")
    public class TopicsController {
        @Nested
        @DisplayName("Тестирование методов по возврату страницы со всеми топиками")
        public class getAllTopicsPage {
            @Test
            @DisplayName("Получение страницы для топиков")
            void getPageTopics() {
                ResponseEntity<?> response = restTemplate.getForEntity("/topics", String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                System.out.println(response);
            }

            @Test
            @DisplayName("Получение страницы для топиков по названию")
            void getPageTopicsTitle() {
                ResponseEntity<?> response = restTemplate.getForEntity("/topics?parentId=1", String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                System.out.println(response);
            }

            @Test
            @DisplayName("Получение страницы для топиков по не существующему названию")
            void getPageTopicsTitleNull() {
                ResponseEntity<?> response = restTemplate.getForEntity("/topics?title=павввввввввввввввввввввввввввв", String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                System.out.println(response);
            }
        }

        @Nested
        @DisplayName("Тестирование получения топиков по id")
        public class getTopicsById {
            @Test
            @DisplayName("Получение топика по существующему id")
            void getTopicById() {
                ResponseEntity<?> response = restTemplate.getForEntity("/topics/1", String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Получение топика по не существующему id")
            void getTopicByWrongId() {
                ResponseEntity<?> response = restTemplate.getForEntity("/topics/199", String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование расширенного получения топиков по id ")
        public class getTopicsByIdExtended {
            @Test
            @DisplayName("Получение топика по существующему id")
            void getTopicByIdExtended() {
                ResponseEntity<?> response = restTemplate.getForEntity("/topics/extended/2", String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Расширенное получение топика по не существующему id")
            void getTopicByWrongIdExtended() {
                ResponseEntity<?> response = restTemplate.getForEntity("/topics/extended/199", String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование создания топика")
        public class createTopics {
            @Test
            @DisplayName("Создание топика c parentId")
            void createTopicWithParentId() {
                TopicsDTO topicsDTO = new TopicsDTO("Литература с родителем","Топики на темы, связанные с книгами и чтением с родителями",9, null, null);
                ResponseEntity<?> response = restTemplate.postForEntity("/topics", topicsDTO, String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
                System.out.println(response.getBody());
            }
            @Test
            @DisplayName("Создание топика без parentId")
            void createTopicWithoutParentId() {
                TopicsDTO topicsDTO = new TopicsDTO("Литература без родителя","Топики на темы, связанные с книгами и чтением без родителей",null, null, null);
                ResponseEntity<?> response = restTemplate.postForEntity("/topics", topicsDTO, String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование обновление топика")
        public class updateTopics {
            @Test
            @DisplayName("Обновление топика с корректными данными")
            void updateTopic() {
                TopicsDTO topicsDTO = new TopicsDTO("Литература updated","Топики на темы, связанные с книгами и чтением updated", null, null, null);
                ResponseEntity<?> response = restTemplate.exchange("/topics/1", HttpMethod.PUT, new HttpEntity<>(topicsDTO), String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Поиск топика по несуществующего Id")
            void updateTopicNullId() {
                TopicsDTO topicsDTO = new TopicsDTO("Литература updated","Топики на темы, связанные с книгами и чтением updated", null, null, null);
                ResponseEntity<?> response = restTemplate.exchange("/topics/1222", HttpMethod.PUT, new HttpEntity<>(topicsDTO), String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Проверка ссылки parentId на само себя")
            void updateTopicRecursionId() {
                TopicsDTO topicsDTO = new TopicsDTO("Литература updated","Топики на темы, связанные с книгами и чтением updated",1, null,null);
                ResponseEntity<?> response = restTemplate.exchange("/topics/1", HttpMethod.PUT, new HttpEntity<>(topicsDTO), String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование выборочное обновление топика")
        public class patchTopics {
            @Test
            @DisplayName("Обновление названия топика с корректными данными")
            void patchTopicTitle() {
                HashMap<String, String> requestBody = new HashMap<>();
                requestBody.put("title", "Литература updated");
                requestBody.put("parentId",null);
                ResponseEntity<?> response = restTemplate.exchange("/topics/2", HttpMethod.PATCH, new HttpEntity<>(requestBody), String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Пустое тело запроса")
            void patchTopicNullBody() {
                HashMap<String, String> requestBody = new HashMap<>();
                ResponseEntity<?> response = restTemplate.exchange("/topics/1", HttpMethod.PATCH, new HttpEntity<>(requestBody), String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Проверка ссылки parentId на само себя")
            void patchTopicRecursionId() {
                TopicsDTO topicsDTO = new TopicsDTO("Литература updated","Топики на темы, связанные с книгами и чтением updated", 1, null, null);
                ResponseEntity<?> response = restTemplate.exchange("/topics/1", HttpMethod.PATCH, new HttpEntity<>(topicsDTO), String.class);
                Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование удаление топика")
        public class deleteTopics {
            @Test
            @DisplayName("Удаление существующего топика")
            void deleteTopic() {
                ResponseEntity<?> response = restTemplate.exchange("/topics/7", HttpMethod.DELETE, new HttpEntity<>(String.class), String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Ошибка удаления топика с несуществующим id")
            void deleteTopicId() {
                ResponseEntity<?> response = restTemplate.exchange("/topics/999", HttpMethod.DELETE, new HttpEntity<>(String.class), String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
                System.out.println(response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("Тестирование контроллера вопросов")
    public class QuestionsController {
        @Nested
        @DisplayName("Тестирование методов по возврату страницы со всеми вопросами")
        public class getAllQuestionsPage {
            @Test
            @DisplayName("Получение страницы для топиков")
            void getPageQuestions() {
                ResponseEntity<?> response = restTemplate.getForEntity("/questions", String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response);
            }

            @Test
            @DisplayName("Получение страницы для вопросов по тексту вопроса")
            void getPageQuestionsByText() {
                ResponseEntity<?> response = restTemplate.getForEntity("/questions?questions=какой", String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response);
            }

            @Test
            @DisplayName("Получение страницы для вопросов по не существующему тексту вопроса")
            void getPageQuestionsByTextNull() {
                ResponseEntity<?> response = restTemplate.getForEntity("/questions?questions=абвгдеёжзийклмнопрстуфхцчшщъыьэюя", String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response);
            }
        }

        @Nested
        @DisplayName("Тестирование получения вопроса по id")
        public class getQuestionsById {
            @Test
            @DisplayName("Получение вопроса по существующему id")
            void getQuestionById() {
                ResponseEntity<?> response = restTemplate.getForEntity("/questions/3", String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Получение вопроса по не существующему id")
            void getQuestionByWrongId() {
                ResponseEntity<?> response = restTemplate.getForEntity("/questions/999", String.class);
                Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование расширенного получения топиков по id вопроса")
        public class getTopicsByIdExtended {
            @Test
            @DisplayName("Получение топика по существующему id вопроса")
            void getTopicByIdExtended() {
                ResponseEntity<?> response = restTemplate.getForEntity("/questions/extended/6", String.class);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Расширенное получение топика по не существующему id вопроса")
            void getTopicByWrongIdExtended() {
                ResponseEntity<?> response = restTemplate.getForEntity("/questions/extended/199", String.class);
                Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование создания вопроса")
        public class createQuestions {
            @Test
            @DisplayName("Создание вопроса")
            void createQuestion() {
                QuestionsDTO questionsDTO = new QuestionsDTO("Какой самый высокий горный пик в мире?", "Эверест",false, 1, null, null);
                ResponseEntity<?> response = restTemplate.postForEntity("/questions", questionsDTO, String.class);
                Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование обновления вопроса")
        public class updateQuestions {
            @Test
            @DisplayName("Обновление вопроса с корректными данными")
            void updateQuestion() {
                QuestionsDTO questionsDTO = new QuestionsDTO("Какой самый высокий горный пик в мире? (обновлено)","Эверест (обновлено)", true, 2, null, null);
                ResponseEntity<?> response = restTemplate.exchange("/questions/4", HttpMethod.PUT, new HttpEntity<>(questionsDTO), String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Поиск вопроса по несуществующему Id")
            void updateQuestionNullId() {
                QuestionsDTO questionsDTO = new QuestionsDTO("Какой самый высокий горный пик в мире? (обновлено)", "Эверест (обновлено)", true, 2, null, null);
                ResponseEntity<?> response = restTemplate.exchange("/questions/12345", HttpMethod.PUT, new HttpEntity<>(questionsDTO), String.class);
                Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование выборочное обновление вопроса")
        public class patchQuestions {
            @Test
            @DisplayName("Обновление текста вопроса с корректными данными")
            void patchQuestionText() {
                HashMap<String, String> requestBody = new HashMap<>();
                requestBody.put("question", "Какой самый высокий вулкан в мире?");
                requestBody.put("is_popular", "true");
                requestBody.put("topicId", "1");
                ResponseEntity<?> response = restTemplate.exchange("/questions/5", HttpMethod.PATCH, new HttpEntity<>(requestBody), String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Пустое тело запроса")
            void patchQuestionNullBody() {
                HashMap<String, String> requestBody = new HashMap<>();
                ResponseEntity<?> response = restTemplate.exchange("/questions/7", HttpMethod.PATCH, new HttpEntity<>(requestBody), String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Попытка обновления несуществующего вопроса")
            void patchQuestionWrongId() {
                HashMap<String, String> requestBody = new HashMap<>();
                requestBody.put("answer", "Анды");
                ResponseEntity<?> response = restTemplate.exchange("/questions/54321", HttpMethod.PATCH, new HttpEntity<>(requestBody), String.class);
                Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование удаления вопроса")
        public class deleteQuestions {
            @Test
            @DisplayName("Удаление существующего вопроса")
            void deleteQuestion() {
                ResponseEntity<?> response = restTemplate.exchange("/questions/2", HttpMethod.DELETE, new HttpEntity<>(String.class), String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Ошибка удаления вопроса с несуществующим id")
            void deleteQuestionId() {
                ResponseEntity<?> response = restTemplate.exchange("/questions/888", HttpMethod.DELETE, new HttpEntity<>(String.class), String.class);
                Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("Тестирование контроллера реакций")
    public class ReactionsController {
        @Nested
        @DisplayName("Тестирование методов по возврату всех реакций")
        public class getAllReactions {
            @Test
            @DisplayName("Получение списка всех реакций")
            void getAllReaction() {
                ResponseEntity<?> response = restTemplate.getForEntity("/reactions", List.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование создания реакции")
        public class createReactions {
            @Test
            @DisplayName("Создание реакции с корректными данными")
            void createReaction() {
                ReactionsDTO reactionsDTO = new ReactionsDTO(UUID.fromString("a239a2cd-6e43-4fc6-b72a-073a6f3c0230"),"like", 8);
                ResponseEntity<?> response = restTemplate.postForEntity("/reactions", reactionsDTO, ReactionsDTO.class);
                Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
                Assertions.assertNotNull(response.getBody());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование обновления реакции")
        public class updateReactions {
            @Test
            @DisplayName("Обновление реакции с корректными данными")
            void updateReaction() {
                //Было user_id=37e68076-d94c-4cf0-ac7a-c16f7046451e, type=like, questionsId=6
                ReactionsDTO reactionsDTO = new ReactionsDTO(UUID.fromString("a239a2cd-6e43-4fc6-b72a-073a6f3c0230"), "dislike", 2);
                ResponseEntity<?> response = restTemplate.exchange("/reactions/6", HttpMethod.PUT, new HttpEntity<>(reactionsDTO), ReactionsDTO.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                Assertions.assertNotNull(response.getBody());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Обновление реакции по несуществующему Id")
            void updateReactionNullId() {
                ReactionsDTO reactionsDTO = new ReactionsDTO(UUID.fromString("a239a2cd-6e43-4fc6-b72a-073a6f3c0230"), "dislike", 2);
                ResponseEntity<?> response = restTemplate.exchange("/reactions/1333", HttpMethod.PUT, new HttpEntity<>(reactionsDTO), String.class);
                Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование частичного обновления реакции")
        public class patchReaction {
            @Test
            @DisplayName("Частичное обновление типа реакции")
            void patchReactionType() {
                HashMap<String, String> requestBody = new HashMap<>();
                requestBody.put("type", "dislike");
                ResponseEntity<?> response = restTemplate.exchange("/reactions/4", HttpMethod.PATCH, new HttpEntity<>(requestBody), ReactionsDTO.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                Assertions.assertNotNull(response.getBody());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Пустое тело запроса реакции")
            void patchReactionNullBody() {
                HashMap<String, String> requestBody = new HashMap<>();
                ResponseEntity<?> response = restTemplate.exchange("/reactions/5", HttpMethod.PATCH, new HttpEntity<>(requestBody), String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Попытка частичного обновления несуществующей реакции")
            void patchNonExistingReaction() {
                HashMap<String, String> requestBody = new HashMap<>();
                requestBody.put("type", "like");
                ResponseEntity<?> response = restTemplate.exchange("/reactions/999", HttpMethod.PATCH, new HttpEntity<>(requestBody), String.class);
                Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }

        @Nested
        @DisplayName("Тестирование удаления реакции")
        public class deleteReactions {
            @Test
            @DisplayName("Удаление существующей реакции")
            void deleteReaction() {
                ResponseEntity<?> response = restTemplate.exchange("/reactions/1", HttpMethod.DELETE, new HttpEntity<>(String.class), String.class);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                System.out.println(response.getBody());
            }

            @Test
            @DisplayName("Попытка удаления несуществующей реакции")
            void deleteNonExistingReaction() {
                ResponseEntity<?> response = restTemplate.exchange("/reactions/9999", HttpMethod.DELETE, new HttpEntity<>(String.class), String.class);
                Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                System.out.println(response.getBody());
            }
        }
    }

    @Nested
    @DisplayName("Валидация топиков")
    public class ValidationTopics {

        @Test
        @DisplayName("Валидация blank Title")
        void createTopicValidationNullTitle() {
            TopicsDTO topicsDTO = new TopicsDTO("        ","Топики на темы, связанные с книгами и чтением",null,null,null);
            ResponseEntity<?> response = restTemplate.postForEntity("/topics", topicsDTO, String.class);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
            System.out.println(response.getBody());
        }

        @Test
        @DisplayName("Валидация длины Title")
        void createTopicValidationTitle() {
            TopicsDTO topicsDTO = new TopicsDTO("Лит","Топики на темы, связанные с книгами и чтением",null,null,null);
            ResponseEntity<?> response = restTemplate.postForEntity("/topics", topicsDTO, String.class);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
            System.out.println(response.getBody());
        }

        @Test
        @DisplayName("Валидация null description")
        void updateTopicValidationNullDescription() {
            TopicsDTO topicsDTO = new TopicsDTO("Литература updated","     ",null,null,null);
            ResponseEntity<?> response = restTemplate.exchange("/topics/5", HttpMethod.PUT, new HttpEntity<>(topicsDTO), String.class);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
            System.out.println(response.getBody());
        }

        @Test
        @DisplayName("Валидация size description")
        void updateTopicValidationDescription() {
            TopicsDTO topicsDTO = new TopicsDTO("Литература updated","Топ",null,null,null);
            ResponseEntity<?> response = restTemplate.exchange("/topics/1", HttpMethod.PUT, new HttpEntity<>(topicsDTO), String.class);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
            System.out.println(response.getBody());
        }
    }

    @Nested
    @DisplayName("Валидация вопросов")
    public class ValidationQuestions {

        @Test
        @DisplayName("Валидация questions на длину от 5 до 1000")
        void createQuestionsValidationQuiestions() {
            QuestionsDTO questionsDTO = new QuestionsDTO("Как", "Эверест", false, 1, null, null);
            ResponseEntity<?> response = restTemplate.postForEntity("/questions", questionsDTO, String.class);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            System.out.println(response.getBody());
        }

        @Test
        @DisplayName("Валидация questions на пустой вопрос")
        void createQuestionsValidationBlankQuiestions() {
            QuestionsDTO questionsDTO = new QuestionsDTO("Эверест", "                   ", false, 1, null, null);
            ResponseEntity<?> response = restTemplate.postForEntity("/questions", questionsDTO, String.class);
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            System.out.println(response.getBody());
        }

        @Test
        @DisplayName("Валидация answer на размер ответа")
        void createQuestionsValidationSizeAnswer() {
            QuestionsDTO questionsDTO = new QuestionsDTO("Эве", "Какой самый высокий горный пик в мире?", false, 1, null, null);
            ResponseEntity<?> response = restTemplate.postForEntity("/questions", questionsDTO, String.class);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            System.out.println(response.getBody());
        }
    }

    @Nested
    @DisplayName("Валидация реакции")
    public class ValidationReactions {

        @Test
        @DisplayName("Валидация userId на пустой запрос")
        void createReactionValidationBlankId() {
            Map<String, String> map = new HashMap<>();
            map.put("user_id", "               ");
            map.put("type", "like");
            map.put("questionsId", "1");
            ResponseEntity<?> response = restTemplate.postForEntity("/reactions", map, String.class);
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            System.out.println(response.getBody());
        }

        @Test
        @DisplayName("Валидация type на размер реакции")
        void createReactionValidation() {
            ReactionsDTO reactionsDTO = new ReactionsDTO(UUID.fromString("a239a2cd-6e43-4fc6-b72a-073a6f3c0230"), "п", 1);
            ResponseEntity<?> response = restTemplate.postForEntity("/reactions", reactionsDTO, String.class);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            System.out.println(response.getBody());
        }
    }
}
