package edu.uchicago.mpcs53112.Recommender;
import javax.swing.*;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
public class Demo {

    JFrame frame = new JFrame("");
    AutoCompleteDecorator decorator;
    JComboBox combobox;
    private JTextField textField;

    public Demo(List<String> words) {
        combobox = new JComboBox(new Object[]{"","Ester", "Jordi",
            "Jordina", "Jorge", "Sergi", "aEster", "bJordi",
            "cJordina", "dJorge", "eSergi"});
        AutoCompleteDecorator.decorate(combobox);
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());
        
        textField = new JTextField();
        frame.getContentPane().add(textField);
        textField.setColumns(30);

        frame.getContentPane().add(combobox);
        frame.setVisible(true);
        
        
        //AutoSuggestor as = new AutoSuggestor(textField, frame, words, Color.WHITE, Color.black, Color.ORANGE, 0.5f );
       
    }
    
    

    /*
    public static void main(String[] args) {
        Demo d = new Demo();
    }
    */
}