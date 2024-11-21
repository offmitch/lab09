import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class QuizApp extends Application
{

    public static final int QUESTION_ANSWER_LENGTH = 2;
    public static final int MAX_QUESTIONS = 10;
    public static final int LAYOUT_SIZE = 15;
    public static final int SCENE_WIDTH = 600;
    public static final int SCENE_LENGTH = 400;
    public static final int STARTING_SCORE = 0;
    public static final int STARTING_QUESTION_COUNT = 0;

    private final List<String[]> questionsAndAnswers = new ArrayList<>();
    private String currentAnswer;
    private int questionCount;
    private int score;

    @Override
    public void start(final Stage primaryStage)
    {

        final Label questionLabel;
        final Label statusLabel;
        final TextField answerField;
        final Button submitButton;
        final Button startQuizButton;
        final VBox layout;
        final Scene scene;

        layout = new VBox(LAYOUT_SIZE);
        questionLabel = new Label("Press 'Start Quiz' to begin!");
        statusLabel = new Label("Status: ");
        answerField = new TextField();
        submitButton = new Button("Submit");
        startQuizButton = new Button("Start Quiz");

        initializeQuestionsFromFile();

        primaryStage.setTitle("Quiz");


        submitButton.setOnAction(e -> handleAnswer(answerField, questionLabel, statusLabel, startQuizButton));

        answerField.setOnKeyPressed(e ->
        {
            if(e.getCode() == KeyCode.ENTER)
            {
                handleAnswer(answerField, questionLabel, statusLabel, startQuizButton);
            }
        });

        startQuizButton.setOnAction(e ->
        {
            resetQuiz(questionLabel, statusLabel, answerField);
            startQuizButton.setDisable(true);
        });

        layout.getChildren().addAll(questionLabel, answerField, submitButton, statusLabel, startQuizButton);
        scene = new Scene(layout, SCENE_WIDTH, SCENE_LENGTH);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void handleAnswer(final TextField answerField,
                              final Label questionLabel,
                              final Label statusLabel,
                              final Button startQuizButton)
    {
        if(questionCount < MAX_QUESTIONS)
        {
            if(answerField.getText().equalsIgnoreCase(currentAnswer))
            {
                score++;
                statusLabel.setText("Status: CORRECT! Your score: " + score);
            }
            else
            {
                statusLabel.setText("Status: INCORRECT! The answer was: " + currentAnswer);
            }
            answerField.clear();

            if(++questionCount < MAX_QUESTIONS)
            {
                nextQuestion(questionLabel);
            }
            else
            {
                statusLabel.setText("Quiz Complete! Final Score: " + score + "/" + MAX_QUESTIONS);
                questionLabel.setText("Press 'Start Quiz' to play again!");
                startQuizButton.setDisable(false);
            }
        }
    }

    private void resetQuiz(final Label questionLabel,
                           final Label statusLabel,
                           final TextField answerField)
    {
        questionCount = STARTING_QUESTION_COUNT;
        score = STARTING_SCORE;

        answerField.clear();
        statusLabel.setText("Status: ");
        nextQuestion(questionLabel);
    }

    private final void nextQuestion(final Label questionLabel)
    {
        String question;

        question = getRandomQuestion();
        questionLabel.setText(question);
    }

    private final void initializeQuestionsFromFile()
    {

        questionsAndAnswers.clear();
        try(final BufferedReader br = new BufferedReader(new FileReader("src/quiz.txt")))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                final String[] qAndA;
                qAndA = line.split("\\|");
                if(qAndA.length == QUESTION_ANSWER_LENGTH)
                {
                    questionsAndAnswers.add(qAndA);
                }
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    private final String getRandomQuestion()
    {
        final Random random;
        final int index;

        random = new Random();

        if(questionsAndAnswers.isEmpty())
        {
            return "No questions available!";
        }

        index = random.nextInt(questionsAndAnswers.size());
        currentAnswer = questionsAndAnswers.get(index)[1];

        return questionsAndAnswers.get(index)[STARTING_SCORE];
    }

    public static void main(final String[] args)
    {
        launch(args);
    }
}
