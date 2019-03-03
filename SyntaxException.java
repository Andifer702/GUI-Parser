/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui_parser;

import javax.swing.JOptionPane;

/*
 * File Name: SyntaxException.java
 * Date: 02/09/2019
 * Author: Joon Park
 * Purpose: Exception class for catching syntex error and displaying at which token the error appeared
 */

public class SyntaxException extends Exception{
    public SyntaxException(){
    }
    public void showMessage(String message){
        JOptionPane.showMessageDialog(null, "Please check your syntax on token: " + message);
    }
    
}
