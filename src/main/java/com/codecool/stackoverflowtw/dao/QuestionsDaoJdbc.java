package com.codecool.stackoverflowtw.dao;

import com.codecool.stackoverflowtw.dao.model.Question;
import com.codecool.stackoverflowtw.database.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsDaoJdbc implements QuestionsDAO {
    private final ConnectionProvider connectionProvider;

    public QuestionsDaoJdbc(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void sayHi() {
        System.out.println("Hi DAO!");
    }

    @Override
    public List<Question> getAllQuestions() {
        String query = """
                SELECT question_id, votes, title, description, user_id, posted
                FROM questions;
                """;

        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            List<Question> allQuestions = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                allQuestions.add(new Question(resultSet.getInt("question_id"), resultSet.getInt("votes"),
                        resultSet.getString("title"), resultSet.getString("description"),
                        resultSet.getInt("user_id"), resultSet.getTimestamp("posted")));
            }
            return allQuestions;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Question getQuestionByQuestionId(int id) {
        String query = """
                SELECT question_id, votes, title, description, user_id, posted
                FROM questions
                WHERE questions.question_id = ?;
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return new Question(resultSet.getInt("question_id"), resultSet.getInt("votes"),
                    resultSet.getString("title"), resultSet.getString("description"),
                    resultSet.getInt("user_id"), resultSet.getTimestamp("posted"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean postNewQuestion(Question question) {
        return false;
    }

    @Override
    public boolean deleteQuestionByQuestionId(int questionId) {
        return false;
    }

    @Override
    public List<Question> getSortedQuestions(QuestionSortType sortBy) {
        String query = buildOrderByQuery(sortBy);

        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            List<Question> allQuestions = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                allQuestions.add(new Question(resultSet.getInt("question_id"), resultSet.getInt("votes"),
                        resultSet.getString("title"), resultSet.getString("description"),
                        resultSet.getInt("user_id"), resultSet.getTimestamp("posted")));
            }
            return allQuestions;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildOrderByQuery(QuestionSortType sortBy) {
        StringBuilder query = new StringBuilder("""
                SELECT Questions.question_id,
                                   Questions.votes,
                                   Questions.title,
                                   Questions.description,
                                   Questions.user_id,
                                   Questions.posted,
                                   COUNT(answer_id) AS answer_count
                            FROM questions
                                     LEFT JOIN Answers
                                               ON Questions.question_id = Answers.question_id
                            GROUP BY Questions.question_id, Questions.votes, Questions.title, Questions.description,
                                     Questions.user_id, Questions.posted
                            ORDER BY""");
        switch (sortBy) {
            case ALPHABET_ASC -> query.append(" Questions.title ASC;");
            case ALPHABET_DESC -> query.append(" Questions.title DESC;");
            case DATE_ASC -> query.append(" Questions.posted ASC;");
            case DATE_DESC -> query.append(" Questions.posted DESC;");
            case ANSWER_ASC -> query.append(" answer_count ASC;");
            case ANSWER_DESC -> query.append(" answer_count DESC;");
        }
        return query.toString();
    }

    @Override
    public int getNumberOfQuestionsByUserId(int userId) {
        return 0;
    }

    @Override
    public Map<Integer, Integer> getAnswerCountsByQuestionIds() {
        String query = """
                SELECT Questions.question_id AS id, COUNT(answer_id) AS count
                FROM questions
                         LEFT JOIN Answers
                                   ON Questions.question_id = Answers.question_id
                GROUP BY id;
                """;

        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            Map<Integer, Integer> answerCountsByQuestionIds = new HashMap<>();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                answerCountsByQuestionIds.put(resultSet.getInt("id"), resultSet.getInt("count"));
            }
            return answerCountsByQuestionIds;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getAnswerCountByQuestionId(int id) {
        String query = """
                SELECT COUNT(answer_id) AS count
                FROM questions
                         LEFT JOIN Answers
                                   ON Questions.question_id = Answers.question_id
                WHERE questions.question_id = ?;
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            System.out.println(resultSet.getInt("count"));
            return resultSet.getInt("count");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
