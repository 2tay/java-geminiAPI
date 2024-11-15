package com.example;

import javax.swing.*;
import java.io.File;

public class FileChooserExample {
    public static void main(String[] args) {
        // Create a JFileChooser instance
        JFileChooser fileChooser = new JFileChooser();

        // Set the dialog title
        fileChooser.setDialogTitle("Select a File");

        // Show the open file dialog
        int result = fileChooser.showOpenDialog(null);

        // Check if a file was selected
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("No file selected");
        }
    }
}


