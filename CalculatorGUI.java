import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorGUI {
    private JFrame frame;
    private JTextField textField;
    private JTextArea historyArea;
    private String operator;
    private double num1, num2, result;

    public CalculatorGUI() {
        frame = new JFrame("Calculator");
        frame.setSize(350, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.BOLD, 20));
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setEditable(false);
        topPanel.add(textField, BorderLayout.NORTH);

        historyArea = new JTextArea(5, 20);
        historyArea.setFont(new Font("Arial", Font.PLAIN, 14));
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 3, 3));

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "⌫", "(", ")"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.addActionListener(new ButtonClickListener());
            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                if ((c >= '0' && c <= '9') || c == '+' || c == '-' || c == '*' || c == '/' || c == '.') {
                    textField.setText(textField.getText() + c);
                } else if (c == KeyEvent.VK_ENTER) {
                    calculateResult();
                } else if (c == KeyEvent.VK_BACK_SPACE) {
                    String text = textField.getText();
                    if (!text.isEmpty()) {
                        textField.setText(text.substring(0, text.length() - 1));
                    }
                }
            }
        });
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("C")) {
                textField.setText("");
                num1 = num2 = result = 0;
                operator = "";
            } else if (command.equals("⌫")) {
                String text = textField.getText();
                if (!text.isEmpty()) {
                    textField.setText(text.substring(0, text.length() - 1));
                }
            } else if (command.equals("=")) {
                calculateResult();
            } else {
                textField.setText(textField.getText() + command);
            }
        }
    }

    private void calculateResult() {
        try {
            String expression = textField.getText();
            result = eval(expression);
            historyArea.append(expression + " = " + result + "\n");
            textField.setText(String.valueOf(result));
        } catch (Exception ex) {
            textField.setText("Error");
        }
    }

    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;
            
            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }
            
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
            
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            
            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            
            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        new CalculatorGUI();
    }
}
