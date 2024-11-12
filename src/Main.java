import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{

    public enum INT_CONSTANTS{
        WINDOW_WIDTH(1200), WINDOW_HEIGHT(WINDOW_WIDTH.value/2), BOUNDING_POS(15), BOUNDING_SIZE(BOUNDING_POS.value*5);
        public final int value;

        INT_CONSTANTS(int type) {this.value = type;}
    }

    public static void main(String[] args) {
        //changes l&f to windows classic because im a basic bitch like that
        /*try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows Classic".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {System.out.println("error with look and feel!\n------DETAILS------\n"+e.getMessage());}*/

        initUI();
    }

    private static void initUI(){
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


        //button to randomize color
        JButton randomButton = new JButton("Randomize");
        randomButton.setBounds(0,0,50,50);
        panel.add(randomButton);

        //the big square that shows ur color
        JTextField colorPreview = new JTextField();
        colorPreview.setBounds(boundingPos, boundingPos, windowWidth/3, windowHeight- boundingSize);
        colorPreview.setBackground(new Color(0,0,0));
        colorPreview.setEditable(false);
        panel.add(colorPreview);

        //runs when the randomizebutton is clicked
        randomButton.addActionListener(_ -> {
            System.out.println("test");
        });
    }
}