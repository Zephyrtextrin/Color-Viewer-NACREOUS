import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Main{

    public enum INT_CONSTANTS{
        WINDOW_WIDTH(1200), WINDOW_HEIGHT(WINDOW_WIDTH.value/2), BOUNDING_POS(15), BOUNDING_SIZE(BOUNDING_POS.value*5);
        public final int value;

        INT_CONSTANTS(int type) {this.value = type;}
    }

    boolean hexState;
    static boolean coloredBGs;
    public static void main(String[] args) {
        //changes l&f to windows classic because im a basic bitch like that
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows Classic".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {System.out.println("error with look and feel!\n------DETAILS------\n"+e.getMessage());}


        //TODO: add more comments and better organization


        //BEGIN TO INIT UI ---------------------------------------------------------------------------

        final int windowWidth =INT_CONSTANTS.WINDOW_WIDTH.value;
        final int windowHeight = INT_CONSTANTS.WINDOW_HEIGHT.value;
        final int boundingPos = INT_CONSTANTS.BOUNDING_POS.value;
        final int boundingSize = INT_CONSTANTS.BOUNDING_SIZE.value;

        //Jframe (the window)
        JFrame frame = new JFrame("Color Viewer [NACREOUS] ");
        frame.setSize(INT_CONSTANTS.WINDOW_WIDTH.value, INT_CONSTANTS.WINDOW_HEIGHT.value);
        frame.setResizable(false);
        frame.setVisible(true);

        //Jpanel (the panel that all elements are appended to)
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        panel.setLayout(null);
        frame.add(panel);

        //button to set R in RGB
        JTextField RTextField = new JTextField("R Value...");
        RTextField.setBounds(windowWidth/2, (boundingPos*5)+20,150,50);
        panel.add(RTextField);

        //button to set G in RGB
        JTextField GTextField = new JTextField("G Value...");
        GTextField.setBounds(windowWidth/2, RTextField.getY()+320/6,150,50);
        panel.add(GTextField);

        //button to set B in RGB
        JTextField BTextField = new JTextField("B Value...");
        BTextField.setBounds(windowWidth/2, GTextField.getY()+320/6,150,50);
        panel.add(BTextField);

        //button to set HEx
        JTextField hexField = new JTextField("Hex Value...");
        hexField.setBounds(windowWidth/2, BTextField.getY()+320/6,150,50);
        panel.add(hexField);

        //button to set rgb color
        JButton setRGBButton = new JButton("Set Colors!");
        setRGBButton.setBounds(windowWidth/2, hexField.getY()+320/6,150,50);
        panel.add(setRGBButton);

        //button to randomize color
        JButton randomButton = new JButton("Randomize");
        randomButton.setBounds(windowWidth/2, setRGBButton.getY()+320/6,150,50);
        panel.add(randomButton);

        //the big square that shows ur color
        JTextField colorPreview = new JTextField();
        colorPreview.setBounds(boundingPos, boundingPos, windowWidth/3, windowHeight-(boundingSize+boundingPos));
        colorPreview.setBackground(new Color(0,0,0));
        colorPreview.setEditable(false);
        panel.add(colorPreview);

        JLabel colorLabel = new JLabel("RGB and Hex data will show up here");
        colorLabel.setBounds((int)(colorPreview.getWidth()/3.5), windowHeight-(boundingSize+boundingPos), 500, 50);
        panel.add(colorLabel);

        JLabel hexWarningLabel = new JLabel("<html>The Hex value has a higher priority than RGB values.<br>It'll replace the RGB values when you update the color.</html>");
        hexWarningLabel.setBounds((int)(windowWidth*0.75)-boundingPos*4, RTextField.getY(), 200, 150);
        panel.add(hexWarningLabel);

        JCheckBox hexTargetCheckbox = new JCheckBox("Enable/Disable Hex overriding RGB values");
        hexTargetCheckbox.setBounds(hexWarningLabel.getX(), hexWarningLabel.getHeight()+boundingPos*4, 500, 20);
        panel.add(hexTargetCheckbox);

        JCheckBox colorBGCheckbox = new JCheckBox("Enable/Disable input field colored backgrounds");
        colorBGCheckbox.setBounds(hexTargetCheckbox.getX(), hexTargetCheckbox.getY()+boundingPos*2, 500, 20);
        panel.add(colorBGCheckbox);

        //runs when the randomizebutton is clicked
        randomButton.addActionListener(_ -> {
            Random rand = new Random();
            RTextField.setText(String.valueOf(rand.nextInt(256)));
            GTextField.setText(String.valueOf(rand.nextInt(256)));
            BTextField.setText(String.valueOf(rand.nextInt(256)));

            updateAllFields(colorPreview, RTextField, GTextField, BTextField, hexField, panel, colorLabel);
        });

        RTextField.addActionListener(_ -> {updateAllFields(colorPreview, RTextField, GTextField, BTextField, hexField, panel, colorLabel);});

        GTextField.addActionListener(_ -> {updateAllFields(colorPreview, RTextField,GTextField,BTextField,hexField,panel,colorLabel);});

        BTextField.addActionListener(_ -> {updateAllFields(colorPreview, RTextField,GTextField,BTextField,hexField,panel,colorLabel);});

        hexField.addActionListener(_ -> {
            hexChecksum(hexField);
            updateAllFields(colorPreview, RTextField, GTextField, BTextField, hexField, panel, colorLabel);
        });

        //runs when the RGB button is clicked
        setRGBButton.addActionListener(_ -> {updateAllFields(colorPreview, RTextField, GTextField, BTextField, hexField, panel, colorLabel);});

        colorBGCheckbox.addActionListener(_ -> {
            coloredBGs = colorBGCheckbox.isSelected();

            if(!coloredBGs){
                RTextField.setBackground(Color.WHITE);
                GTextField.setBackground(Color.WHITE);
                BTextField.setBackground(Color.WHITE);
                hexField.setBackground(Color.WHITE);
                RTextField.setForeground(Color.BLACK);
                GTextField.setForeground(Color.BLACK);
                BTextField.setForeground(Color.BLACK);
                hexField.setForeground(Color.BLACK);
            }else{updateAllFields(colorPreview,RTextField,GTextField,BTextField,hexField,panel,colorLabel);}
        });

        hexTargetCheckbox.addActionListener(_ -> {

        });

        panel.repaint();
        panel.revalidate();
    }

    private static void hexChecksum(JTextField hexField) {
        String value = hexField.getText();
        Color hexColor;
        boolean valid = true;
        try {hexColor = Color.decode(value);}
        catch (Exception e) {
            hexColor = Color.BLACK; //placeholder idgaf
            value = "000000";
            valid = false;
        }
        if(valid){
            if(hexColor.getRed() < 134 && hexColor.getGreen() < 134 && hexColor.getBlue() < 134){hexField.setForeground(Color.WHITE);
            }else{hexField.setForeground(Color.BLACK);}
            hexField.setText(value);
        }
    }

    private static void updateAllFields(JTextField preview, JTextField RField, JTextField GField, JTextField BField, JTextField hexField, JPanel panel, JLabel label){
        int R = isValidInt(RField.getText());
        int G = isValidInt(GField.getText());
        int B = isValidInt(BField.getText());
        hexField.setText(String.format("#%02x%02x%02x", R, G, B).toUpperCase());
        System.out.println(coloredBGs);
        preview.setBackground(new Color(R,G,B));

        oneFieldUpdate(new Color(R,0,0), R, RField);
        oneFieldUpdate(new Color(0,G,0), G, GField);
        oneFieldUpdate(new Color(0,0,B), B, BField);
        hexChecksum(hexField);
        if(coloredBGs){
            BField.setForeground(Color.WHITE); //this must be called afterwards because for some reason all blue colors have terrible contrast lol
            hexField.setBackground(preview.getBackground());
        }

        label.setText("RGB: "+R+", "+G+", "+B+" // HEX: "+hexField.getText());
        panel.repaint();
        panel.revalidate();
    }

    private static void oneFieldUpdate(Color color, int value, JTextField field){

        if(coloredBGs) {
            field.setBackground(color);
            Color textColor = Color.BLACK;
            if (value < 134) {textColor = Color.WHITE;} //this is a function so that way the text turns white on darker backgrounds
            field.setForeground(textColor);
        }
        field.setText(String.valueOf(value));
    }

    //used to make sure the user did not input any words or negatives into the RGB input | returns -1 if invalid
    private static int isValidInt(String input){
        int out = -1;
        boolean valid = true;
        try{Integer.parseInt(input);
        }catch (Exception e){
            out = 0;
            valid = false;
        }

        if(valid){
            out = Integer.parseInt(input);
            if(out>=256){out = 255;}
            else if(out<0){out = 0;}
        }

        if(out == -1) {System.out.println("BRO U FUCKED UP SOMEWHERE WITH VALIDITY CHECKS");} //err handlr if out somehow never gets changed
        return out;
    }
}
