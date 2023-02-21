package com.codecool.stackoverflowtw.dao;

import com.codecool.stackoverflowtw.dao.model.Question;
import com.codecool.stackoverflowtw.database.ConnectionProvider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        
        try(Connection connection = connectionProvider.getConnection();
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
}
