import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class Window
extends JFrame
implements ActionListener, Runnable {
    private static final long serialVersionUID = 1;
    boolean inputBoxSelected = false;
    Solve solver;
    String outputFileName = "solution.txt";
    JTextField inputBox;
    JEditorPane outputBox;
    JButton printError;
    JButton inputFile;

    public static void main(String[] args) {
        new Window();
    }

    Window() {
        this.setTitle("Chemical Equation Balancer");
        this.setSize(500, 370);
        this.setResizable(false);
        this.setLayout(new GridLayout(2, 1, 0, 10));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(3);
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(2, 1));
        JPanel topTop = new JPanel();
        topTop.setLayout(new GridLayout(2, 1));
        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel("Chemical Equation Balancer");
        title.setFont(new Font("SansSerif", 1, 24));
        titlePanel.add(title);
        topTop.add(titlePanel);
        JPanel creditPanel = new JPanel();
        JLabel credit = new JLabel("by Raphael Koh");
        credit.setFont(new Font("SansSerif", 2, 15));
        creditPanel.add(credit);
        topTop.add(creditPanel);
        top.add(topTop);
        JPanel topBot1 = new JPanel();
        topBot1.setLayout(new GridLayout(2, 1));
        JPanel topBot = new JPanel();
        JPanel inputTitlePanel = new JPanel();
        JLabel inputTitle = new JLabel("Enter Chemical Equation Here:");
        inputTitlePanel.add(inputTitle);
        topBot.add(inputTitlePanel);
        JPanel inputBoxPanel = new JPanel();
        this.inputBox = new JTextField("e.g. \"H2+O2=H2O\" or \"H2 + O2 \u2192 H20\"", 20);
        inputBoxPanel.add(this.inputBox);
        this.inputBox.addMouseListener((MouseListener)new Window1());
        this.inputBox.addActionListener(this);
        topBot.add(inputBoxPanel);
        topBot1.add(topBot);
        JPanel topBotbot = new JPanel();
        topBotbot.setLayout(new FlowLayout(1, 15, 0));
        topBotbot.add(new JLabel("or"));
        this.inputFile = new JButton("Read From File");
        this.inputFile.addActionListener(this);
        topBotbot.add(this.inputFile);
        topBot1.add(topBotbot);
        top.add(topBot1);
        this.add(top);
        JPanel bot = new JPanel();
        bot.setLayout(new BorderLayout());
        JPanel botTop = new JPanel();
        botTop.setLayout(new BorderLayout());
        botTop.setPreferredSize(new Dimension(this.getWidth(), 130));
        botTop.setBorder(BorderFactory.createEtchedBorder(0));
        this.outputBox = new JEditorPane("text/html", "");
        this.outputBox.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(this.outputBox);
        botTop.add((Component)scrollPane, "Center");
        bot.add((Component)botTop, "North");
        JPanel botBot = new JPanel();
        this.printError = new JButton("Open Error Log");
        this.printError.addActionListener(this);
        botBot.add(this.printError);
        bot.add((Component)botBot, "South");
        this.add(bot);
        this.setVisible(true);
        run();
    }

    void fillOutput() {
        int fontSize = 6;
        String text = "<HTML><BODY> <div align=\"center\"> <font size=\"" + fontSize + "\">" + this.solver.printEquation(true) + "</font> </div> </BODY></HTML>";
        this.outputBox.setText(text);
    }

    void solve(String equation, boolean fromFile) {
        if (equation.trim().length() != 0) {
            try {
                this.solver = new Solve(equation);
                if (fromFile) {
                    this.writeToFile("Problem:\t" + this.solver.getEquation() + "\r\nSolution:\t" + this.solver.printEquation(false));
                } else {
                    this.fillOutput();
                }
                if (!this.solver.isBalanced()) {
                    this.outputBox.setBackground(Color.red);
                    JOptionPane.showMessageDialog(null, "FAILED...");
                    new ErrorFile(this.solver.getEquation(), "Failed", this.solver.showSolution(), this.solver.debug());
                }
            }
            catch (Exception e1) {
                try {
                    if (fromFile) {
                        this.writeToFile("Problem:\t" + equation + "\r\nSolution:\tError!!");
                    }
                    this.outputBox.setText("Error!!");
                    new ErrorFile(equation, "Error");
                }
                catch (IOException e2) {
                    System.out.println("Error Writing To File...");
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.inputBox) {
            this.outputBox.setBackground(Color.white);
            this.solve(this.inputBox.getText(), false);
        } else if (e.getSource() == this.printError) {
            try {
                Runtime.getRuntime().exec("notepad errorlog.txt");
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == this.inputFile) {
            boolean text = false;
            boolean chooserClose = false;
            File inputFile = null;
            while (!text) {
                JFileChooser chooseFile = new JFileChooser();
                if (chooseFile.showOpenDialog(null) == 0) {
                    inputFile = chooseFile.getSelectedFile();
                    text = this.checkExtension(inputFile);
                    continue;
                }
                chooserClose = true;
                break;
            }
            if (!chooserClose && inputFile.canRead()) {
                try {
                    this.solveFile(inputFile);
                }
                catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    void solveFile(File inputFile) throws FileNotFoundException {
        this.inputBox.setEnabled(false);
        Scanner read = new Scanner(inputFile);
        new File(this.outputFileName).delete();
        while (read.hasNextLine()) {
            this.solve(read.nextLine(), true);
        }
        read.close();
        try {
            Runtime.getRuntime().exec("notepad " + this.outputFileName);
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        this.inputBox.setEnabled(true);
    }

    boolean checkExtension(File inputFile) {
        String url = inputFile.getName();
        String extension = "";
        int dot = url.lastIndexOf(46);
        if (dot > 0) {
            extension = url.substring(dot + 1);
        }
        if (extension.equals("txt")) {
            return true;
        }
        JOptionPane.showMessageDialog(null, "Extension: " + extension + " - Please Select a .txt File!");
        return false;
    }

    void writeToFile(String line) throws IOException {
        FileWriter fw = new FileWriter(this.outputFileName, true);
        fw.write(String.valueOf(line) + "\r\n\r\n");
        fw.close();
    }
    
    @Override
    public void run() {
        try {
            do {
                if (Window.this.inputBox.getText().contains("e.g. \"H2+O2=H2O\" or \"H2 + O2 \u2192 H20\"") && !Window.this.inputBox.getText().equals("e.g. \"H2+O2=H2O\" or \"H2 + O2 \u2192 H20\"")) {
                    Window.this.inputBox.setText(Window.this.inputBox.getText().replace("e.g. \"H2+O2=H2O\" or \"H2 + O2 \u2192 H20\"", ""));
                    Window.this.inputBox.setCaretPosition(Window.this.inputBox.getText().length());
                }
                Thread.sleep(100);
            } while (true);
        }
        catch (Exception var1_1) {
            return;
        }
    }
    
    class Window1
    extends MouseAdapter {
        Window1() {
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (Window.this.inputBox.getText().equals("e.g. \"H2+O2=H2O\" or \"H2 + O2 \u2192 H20\"")) {
                Window.this.inputBox.setText(null);
            }
        }
    }
}