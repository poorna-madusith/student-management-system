package com.example.student_managemnt;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModuleMarksController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField module1Field;
    @FXML
    private TextField module2Field;
    @FXML
    private TextField module3Field;
    @FXML
    private ImageView imageView;

    @FXML
    private void handleMouseEntered() {
        zoomIn();
    }

    @FXML
    private void handleMouseExited() {
        zoomOut();
    }

    private void zoomIn() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView);
        scaleTransition.setToX(1.6);  // Slightly zoom in (60% larger)
        scaleTransition.setToY(1.6);
        scaleTransition.play();
    }

    private void zoomOut() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), imageView);
        scaleTransition.setToX(1.0);  // Zoom out to original size
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }

    @FXML
    private void handleImageClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Stage stage = (Stage) imageView.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String studentId = studentIdField.getText().trim();
        String module1Text = module1Field.getText().trim();
        String module2Text = module2Field.getText().trim();
        String module3Text = module3Field.getText().trim();

        // Validate ID
        if (!validateStudentId(studentId)) {
            return;
        }

        // Validate Module Marks
        if (!validateModuleMarks(module1Text, module2Text, module3Text)) {
            return;
        }

        // Convert marks to integers
        int module1 = Integer.parseInt(module1Text);
        int module2 = Integer.parseInt(module2Text);
        int module3 = Integer.parseInt(module3Text);

        // Check if the student ID exists
        if (!isStudentIdExists(studentId)) {
            showAlert2("Invalid ID", "Student ID does not exist in the database.");
            return;
        }

        // Update the student's module marks
        String sql = "UPDATE st_details SET module_1 = ?, module_2 = ?, module_3 = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, module1);
            pstmt.setInt(2, module2);
            pstmt.setInt(3, module3);
            pstmt.setString(4, studentId);

            pstmt.executeUpdate();

            showAlert("Success", "Module marks updated successfully!");
            clearFields();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert2("Database Error", "Failed to update module marks.");
        }
    }

    private boolean validateStudentId(String studentId) {
        // Validate studentId: no symbols, no empty string
        if (studentId.isEmpty() || !studentId.matches("[a-zA-Z0-9]+")) {
            showAlert2("Invalid ID", "ID cannot contain symbols and cannot be empty.");
            return false;
        }
        return true;
    }

    private boolean validateModuleMarks(String... marks) {
        for (String mark : marks) {
            if (!mark.matches("\\d+")) {
                showAlert2("Invalid Marks", "Marks must be numbers.");
                return false;
            }
            int markInt = Integer.parseInt(mark);
            if (markInt >= 100) {
                showAlert2("Invalid Marks", "Marks must be less than 100.");
                return false;
            }
        }
        return true;
    }

    private boolean isStudentIdExists(String studentId) {
        String sql = "SELECT COUNT(*) FROM st_details WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showAlert2(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        studentIdField.clear();
        module1Field.clear();
        module2Field.clear();
        module3Field.clear();
    }
}
