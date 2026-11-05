package com.example;

import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** JavaFX interface for a human-versus-CPU Tic-Tac-Toe game. */
public class TicTacToeApp extends Application {
    private final Board board = new Board();
    private final HumanPlayer human = new HumanPlayer('X', new Scanner(System.in));
    private final CpuPlayer cpu = new CpuPlayer('O');
    private final Button[][] cells = new Button[3][3];
    private final Label status = new Label();
    private final Label score = new Label();
    private int xWins;
    private int oWins;
    private int draws;
    private Player currentPlayer = human;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        status.setAlignment(Pos.CENTER);
        status.setStyle("-fx-font-size: 18px;");
        score.setAlignment(Pos.CENTER);
        VBox header = new VBox(8, status, score);
        header.setAlignment(Pos.CENTER);
        root.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 16, 0));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(6);
        grid.setVgap(6);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Button cell = new Button();
                cell.setPrefSize(100, 100);
                cell.setStyle("-fx-font-size: 32px;");
                final int cellRow = row;
                final int cellColumn = column;
                cell.setOnAction(event -> playHumanMove(cellRow, cellColumn));
                cells[row][column] = cell;
                grid.add(cell, column, row);
            }
        }
        root.setCenter(grid);

        Button newGame = new Button("New Game");
        newGame.setOnAction(event -> startNewGame());
        BorderPane.setAlignment(newGame, Pos.CENTER);
        BorderPane.setMargin(newGame, new Insets(16, 0, 0, 0));
        root.setBottom(newGame);

        startNewGame();
        stage.setTitle("Tic-Tac-Toe");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void startNewGame() {
        board.initialize();
        currentPlayer = human;
        updateBoard();
        updateStatus();
        updateScore();
    }

    private void playHumanMove(int row, int column) {
        if (currentPlayer != human || !human.makeMove(board, row, column)) {
            return;
        }
        updateBoard();
        if (updateGameResult()) {
            return;
        }

        currentPlayer = cpu;
        updateStatus();
        cpu.makeMove(board);
        updateBoard();
        if (!updateGameResult()) {
            currentPlayer = human;
            updateBoard();
            updateStatus();
        }
    }

    private boolean updateGameResult() {
        if (board.checkWin()) {
            status.setText(currentPlayer.getName() + " (" + currentPlayer.getMarker() + ") wins!");
            if (currentPlayer.getMarker() == 'X') {
                xWins++;
            } else {
                oWins++;
            }
            updateScore();
            updateBoard();
            return true;
        }
        if (board.isFull()) {
            status.setText("It's a draw!");
            draws++;
            updateScore();
            updateBoard();
            return true;
        }
        return false;
    }

    private void updateBoard() {
        boolean gameOver = board.checkWin() || board.isFull();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                char marker = board.getCell(row, column);
                Button cell = cells[row][column];
                cell.setText(marker == ' ' ? "" : Character.toString(marker));
                cell.setDisable(gameOver || currentPlayer != human || marker != ' ');
            }
        }
    }

    private void updateStatus() {
        status.setText(currentPlayer.getName() + " (" + currentPlayer.getMarker() + ")'s turn");
    }

    private void updateScore() {
        score.setText("Wins X: " + xWins + " | Wins O: " + oWins + " | Draws: " + draws);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
