package gui_parser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/*
 * File Name: GUI_Parser.java
 * Date: 02/09/2019
 * Author: Joon Park
 * Purpose: This program will parse text files in specific format and turn it into a GUI using recursive descent parsing.
 */

public class GUI_Parser {

    private String token;
    private String line;

    private int row;
    private int col;
    private int hgap;
    private int vgap;

    private File file;
    private Scanner fileIn;
    private Scanner scanLine;

    private JFrame frameA;
    private JPanel panel;
    private JButton button;
    private JTextField textField;
    private ButtonGroup radioGroup;
    private JRadioButton radioButton;
    private JLabel label;

    private String[] tok;

    private int isFrame;
    private final String DELIMITER = "(?<=[()\",.:;])|(?=[()\",.:;])| ";

    //Constructor
    public GUI_Parser() {
        loadFile();
        try {
            readFile(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GUI_Parser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SyntaxException e) {
            e.showMessage(token);
        }
    }

    //method for loading text file
    private void loadFile() {
        JFileChooser jfc = new JFileChooser(".");
        int returnVal = jfc.showOpenDialog(frameA);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();
        }
    }

    //read the first line in the file and call GUI building method
    private void readFile(File f) throws FileNotFoundException, SyntaxException {
        BufferedReader selectedFile = new BufferedReader(new FileReader(f));

        fileIn = new Scanner(selectedFile);
        if (fileIn.hasNextLine()) {
            line = fileIn.nextLine();
            scanLine = new Scanner(line);
            try {
                createWindow();
            } catch (SyntaxException e) {
                e.showMessage(token);
            }

        }

    }

    //This method will parse each word and split it based on the delimiter
    private void cleanToken() {
        token = scanLine.next().trim();
        tok = token.split(DELIMITER);
        //token = scanLine.next().replaceAll("[ \"();:,.]", "");
    }

    //method for building the GUI
    private void createWindow() throws SyntaxException {
        isFrame = 0; //tracking panel nesting. at 0, there are no sub panels
        if (scanLine.hasNext()) {
            cleanToken();
            try {
                if (token.equalsIgnoreCase("Window")) {
                    System.out.println("Adding mainframe...");
                    if (scanLine.hasNext()) {
                        cleanToken();
                        if (tok[0].equals("\"") && tok[2].equals("\"")) {
                            frameA = new JFrame((tok[1]));
                            if (scanLine.hasNext()) {
                                cleanToken();
                                if (tok[0].equals("(") && tok[2].equals(",")) {
                                    int width = Integer.parseInt(tok[1]);
                                    if (scanLine.hasNext()) {
                                        cleanToken();
                                        if (tok[1].equals(")")) {
                                            int height = Integer.parseInt(tok[0]);
                                            frameA.setSize(width, height);
                                            System.out.println("Main frame set with: " + "Width=" + width + " Height=" + height);
                                            if (scanLine.hasNext()) {
                                                setLayout();
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            throw new SyntaxException();
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                SyntaxException e = new SyntaxException();
                e.showMessage(token);
            }
        }
        if (token.equalsIgnoreCase("End.")) {
            System.out.println("Sucessfully parsed code");
            frameA.setVisible(true);
            frameA.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            throw new SyntaxException();
        }
    }

    //method for setting frame and panel layouts
    public void setLayout() throws SyntaxException {
        cleanToken();
        if (token.equalsIgnoreCase("Layout")) {
            if (scanLine.hasNext()) {
                tokenLayout();
            }
        }
        parseWidget();
    }

    private void tokenLayout() throws SyntaxException {
        char firstLetter = 'F';
        cleanToken();
        //setting Flow Layout
        if (token.charAt(0) == firstLetter) {
            if (tok[0].equalsIgnoreCase("Flow")) {
                if (isFrame == 0) {
                    frameA.setLayout(new FlowLayout());
                    System.out.println("Flow layout set for main frame");
                } else {
                    panel.setLayout(new FlowLayout());
                    System.out.println("Flow layout set for panel");
                }
            } else {
                throw new SyntaxException();
            }
            //setting up grid layout
        } else {
            if (tok[1].equals("(")) {
                if (tok[0].equalsIgnoreCase("Grid")) {
                    if (tok[3].equalsIgnoreCase(",")) {
                        row = Integer.parseInt(tok[2]);
                    }
                }
                if (scanLine.hasNext()) {
                    cleanToken();
                    if (tok[1].equals(",")) {
                        col = Integer.parseInt(tok[0]);
                        if (scanLine.hasNext()) {
                            cleanToken();
                            if (tok[1].equals(",")) {
                                hgap = Integer.parseInt(tok[0]);
                                if (scanLine.hasNext()) {
                                    cleanToken();
                                    if (tok[1].equals(")") && tok[2].equals(":")) {
                                        vgap = Integer.parseInt(tok[0]);
                                        if (isFrame == 0) {
                                            frameA.setLayout(new GridLayout(row, col, hgap, vgap));
                                            System.out.println("GridLayout set for main frame with: row=" + row + " col=" + col + " hpag= " + hgap + " vgap=" + vgap);
                                        } else {
                                            panel.setLayout(new GridLayout(row, col, hgap, vgap));
                                            System.out.println("GridLayout set for panel with: row=" + row + " col=" + col + " hpag= " + hgap + " vgap=" + vgap);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (isFrame == 0) {
                                frameA.setLayout(new GridLayout(row, col));
                                System.out.println("GridLayout set for main frame with: row=" + row + " col=" + col);
                            } else {
                                panel.setLayout(new GridLayout(row, col));
                                System.out.println("GridLayout set for panel with: row=" + row + " col=" + col);
                            }
                        }
                    }
                }
            } else {
                throw new SyntaxException();
            }

        }
    }

    //Parsing for widgets
    public void parseWidget() throws SyntaxException {
        while (fileIn.hasNextLine()) {
            line = fileIn.nextLine();
            scanLine = new Scanner(line);
            if (scanLine.hasNext()) {
                cleanToken();
                if (token.equalsIgnoreCase("Textfield")) {
                    int length;
                    if (scanLine.hasNext()) {
                        try {
                            cleanToken();
                            if (tok[1].equals(";")) {
                                length = Integer.parseInt(tok[0]);
                                if (isFrame == 0) {
                                    frameA.add(textField = new JTextField(length));
                                    System.out.println("Textfield added to main frame with length:" + length);
                                } else {
                                    panel.add(textField = new JTextField(length));
                                    System.out.println("Textfield added to panel with length:" + length);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Syntax Error." + token + " <- invalid Textfield length");
                        }
                    }
                } else if (token.equalsIgnoreCase("Panel")) {
                    if (isFrame == 0) {
                        frameA.add(panel = new JPanel());
                        System.out.println("Panel added to main frame");
                    } else {
                        panel.add(panel = new JPanel());
                        System.out.println("Panel nested to parent panel");
                    }
                    isFrame += 1;
                    setLayout();
                } else if (token.equalsIgnoreCase("Button")) {

                    if (scanLine.hasNext()) {
                        cleanToken();
                        if (tok[0].equals("\"") && tok[2].equals("\"") && tok[3].equals(";")) {
                            button = new JButton(tok[1]);
                        } else {
                            throw new SyntaxException();
                        }
                        if (isFrame == 0) {
                            frameA.add(button);
                            System.out.println("Button added to main frame");
                        } else {
                            panel.add(button);
                            System.out.println("Button added to panel");
                        }
                    }
                } else if (token.equalsIgnoreCase("Label")) {
                    if (scanLine.hasNext()) {
                        cleanToken();
                        if (tok[0].equals("\"") && tok[tok.length - 2].equals("\"") && tok[tok.length - 1].equals(";")) {
                            label = new JLabel(tok[1]);
                            if (tok[0].equals("\"") && tok[1].equals("\"")) {
                                label = new JLabel("");
                            }

                            if (isFrame == 0) {
                                frameA.add(label);
                                System.out.println("Label added to main frame");
                            } else {
                                panel.add(label);
                                System.out.println("Label added to panel");
                            }
                        }
                    }
                } else if (token.equalsIgnoreCase("Group")) {
                    radioGroup = new ButtonGroup();
                    isFrame += 1;
                    parseWidget();
                } else if (token.equalsIgnoreCase("Radio")) {
                    if (scanLine.hasNext()) {
                        cleanToken();
                        if (tok[0].equals("\"") && tok[2].equals("\"") && tok[3].equals(";")) {
                            radioButton = new JRadioButton(tok[1]);
                            radioGroup.add(radioButton);
                            if (isFrame == 0) {
                                frameA.add(radioButton);
                                System.out.println("Radio button added to main frame");
                            } else {
                                panel.add(radioButton);
                                System.out.println("Radio button added to panel");
                            }
                        }
                    }
                } else if (token.equals("End;")) {
                    isFrame -= 1;
                    break;
                } else if (token.equals("End.") && isFrame > 0) { //catching syntax error for incorrect End keyword usage.
                    throw new SyntaxException();
                } else if (token.equals("End.") && isFrame == 0) { //existing the while loop again for the final line of the input file
                    break;
                } else {
                    throw new SyntaxException();
                }
            }
        }
    }

    public static void main(String[] args) throws SyntaxException {
        
        GUI_Parser gui = new GUI_Parser();
    }

}