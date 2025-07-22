import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Main {

    public enum INT_CONSTANTS {
        WINDOW_WIDTH(1200), WINDOW_HEIGHT(WINDOW_WIDTH.value / 2), DARK_COLOR(167);
        public final int value;

        INT_CONSTANTS(int type) {
            this.value = type;
        }
    }

    public enum COLORS {
        RED, GREEN, BLUE, HEX
    }

    static boolean hexState = false; //is user in hex mode instead of rgb
    static boolean coloredBGs = true; //colored bgs for rgb/hex vals

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows Classic".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("[Error with look and feel!]\n------DETAILS------\n" + e.getMessage());
        }

        //INIT UI ELEMENTS ---------------------------------------------------------------------------


        //this ui is a little better but i shouldnt rely on magic nums for the x/y
        JFrame frame = new JFrame("Color Viewer [NACREOUS] ");
        frame.setSize(INT_CONSTANTS.WINDOW_WIDTH.value, INT_CONSTANTS.WINDOW_HEIGHT.value);
        frame.setResizable(false);
        frame.setVisible(true);

        MainPanel panel = new MainPanel();
        panel.setLayout(new GridBagLayout());
        frame.add(panel);


        //make this flow better

        //first col
        ColorPreview colorPreview = new ColorPreview("Color");
        new Column(new Component[]{
                colorPreview,
                new JLabel("Preview")
        });

        new Column(new Component[]{
                //user inputs for respective RGB values and hex
                new InputField(COLORS.RED),
                new InputField(COLORS.GREEN),
                new InputField(COLORS.BLUE),
                new InputField(COLORS.HEX),
                //buttons
                new JButton("Set Colors"),
                new JButton("Randomize")
        });

        //third col
        new Column(new Component[]{
                new JCheckBox("Enable Hex Input"),
                new JCheckBox("Enable Colored BGs")
        });
        panel.addAllItems(new Column.AllColumns());

        //ACTION LISTENERS-----------------------------


        //runs when the randomizebutton is clicked
        Objects.requireNonNull(Column.AllColumns.getButton("Randomize")).addActionListener(_ -> {
            Random rand = new Random();
            ((InputField)Objects.requireNonNull(Column.AllColumns.getTextField("R Value"))).setBG(rand.nextInt(256));
            ((InputField)Objects.requireNonNull(Column.AllColumns.getTextField("G Value"))).setBG(rand.nextInt(256));
            ((InputField)Objects.requireNonNull(Column.AllColumns.getTextField("B Value"))).setBG(rand.nextInt(256));
            //hexField.setText(String.format("#%02x%02x%02x", Integer.parseInt(RTextField.getText()), Integer.parseInt(GTextField.getText()), Integer.parseInt(BTextField.getText())).toUpperCase());

            updateAllFields(panel);
        });

        //runs when the RGB button is clicked
        Objects.requireNonNull(Column.AllColumns.getButton("Set Colors")).addActionListener(_ -> updateAllFields(panel));

        //runs when checkbox for color bgs is clicked
        Objects.requireNonNull(Column.AllColumns.getCheckBox("Enable Colored BGs")).addActionListener(_ -> {
            coloredBGs = Objects.requireNonNull(Column.AllColumns.getCheckBox("Enable Colored BGs")).isSelected();

            if(!coloredBGs){
                oneFieldUpdate(Color.WHITE, 255, Objects.requireNonNull(Column.AllColumns.getTextField("R Value")));
                oneFieldUpdate(Color.WHITE, 255, Objects.requireNonNull(Column.AllColumns.getTextField("G Value")));
                oneFieldUpdate(Color.WHITE, 255, Objects.requireNonNull(Column.AllColumns.getTextField("B Value")));
            }else{updateAllFields(panel);}
        });

        //runs when checkbox for hex override is clicked
        Objects.requireNonNull(Column.AllColumns.getCheckBox("EnableHexInput")).addActionListener(_ -> {
            hexState = Objects.requireNonNull(Column.AllColumns.getCheckBox("EnableHexInput")).isSelected();
            Objects.requireNonNull(Column.AllColumns.getTextField("HValue")).setVisible(hexState);
            Objects.requireNonNull(Column.AllColumns.getTextField("HValue")).setEnabled(hexState);

            panel.repaint();
            panel.revalidate();
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
            value = "#000000";
            valid = false;
        }
        if(valid){
            final boolean darkColor = hexColor.getRed()<INT_CONSTANTS.DARK_COLOR.value&&hexColor.getGreen()<INT_CONSTANTS.DARK_COLOR.value&&hexColor.getBlue()<INT_CONSTANTS.DARK_COLOR.value;
            if(darkColor){hexField.setForeground(Color.WHITE);
            }else{hexField.setForeground(Color.BLACK);}
            hexField.setText(value);
        }
    }

    private static void updateAllFields(JPanel panel){
        final JTextField preview = Column.AllColumns.getTextField("Color");
        final JLabel label = Column.AllColumns.getLabel("Preview");
        final InputField RField = (InputField)Column.AllColumns.getTextField("RValue");
        final InputField GField = (InputField)Column.AllColumns.getTextField("GValue");
        final InputField BField = (InputField)Column.AllColumns.getTextField("BValue");
        final InputField hexField = (InputField)Column.AllColumns.getTextField("HValue");
        int R;
        int G;
        int B;
        assert hexField != null;
        hexChecksum(hexField);

        if(!hexState) {
            assert RField != null;
            R = isValidInt(RField.getText());
            assert GField != null;
            G = isValidInt(GField.getText());
            assert BField != null;
            B = isValidInt(BField.getText());
            hexField.setText(String.format("#%02x%02x%02x", R, G, B).toUpperCase());
        }else{
            Color color = Color.decode(hexField.getText());
            R = color.getRed();
            G = color.getGreen();
            B = color.getBlue();
        }

        assert preview != null;
        preview.setBackground(new Color(R,G,B));

        oneFieldUpdate(new Color(R,0,0), R, RField);
        oneFieldUpdate(new Color(0,G,0), G, GField);
        oneFieldUpdate(new Color(0,0,B), B, BField);

        if(coloredBGs){
            assert BField != null;
            BField.setForeground(Color.WHITE); //this must be called afterwards because for some reason all blue colors have terrible contrast lol
            hexField.setBackground(preview.getBackground());
        }

        assert label != null;
        label.setText("RGB: "+R+", "+G+", "+B+" // HEX: "+hexField.getText());
        panel.repaint();
        panel.revalidate();
    }

    private static void oneFieldUpdate(Color color, int value, JTextField field) {

        if (coloredBGs) {
            field.setBackground(color);
            field.setForeground(isDarkColor(color));
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

        return out;
    }

    private static Color isDarkColor(Color color){
        int R = color.getRed();
        int G = color.getGreen();
        int B = color.getBlue();
        int darkThreshold = INT_CONSTANTS.DARK_COLOR.value;

        if(R<darkThreshold&&G<darkThreshold&&B<darkThreshold){return Color.WHITE;
        }else{return Color.BLACK;}
    }

    private static class MainPanel extends JPanel {
        final GridBagConstraints constraints = new GridBagConstraints();


        private MainPanel() {
            this.setLayout(new GridBagLayout());
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 0.5;
        }

        private void addObject(Component component, int X, int Y) {

            constraints.fill = GridBagConstraints.HORIZONTAL;
            component.setSize(new Dimension(500, 500));
            constraints.gridx = X;
            constraints.gridy = Y;
            this.add(component, constraints);

            this.repaint();
            this.revalidate();
        }

        public void addAllItems(Column.AllColumns allItems){
            int X = 0;
            int Y = 0;
            for(Column col:allItems){
                for(Component component:col){
                    this.addObject(component,X,Y);
                    Y++;
                }
                Y = 0;
                X++;
            }

        }
    }

    private static class ColorPreview extends JTextField {
        private ColorPreview(String name) {
            this.setText(name);
            this.setBackground(new Color(0, 0, 0));
            this.setEditable(false);
        }
    }

    private static class InputField extends JTextField {
        COLORS COLOR_TYPE;

        private InputField(COLORS type) {
            COLOR_TYPE = type;
            this.setText(COLOR_TYPE.toString().charAt(0) + " Value");

            if (COLOR_TYPE == COLORS.HEX) {
                this.setVisible(hexState);
                this.setEnabled(hexState);
            }
        }

        private void setBG(int value) {
            this.setText(String.valueOf(value));
            Color color = switch (COLOR_TYPE) {
                case RED -> new Color(value, 0, 0);
                case GREEN -> new Color(0, value, 0);
                case BLUE -> new Color(0, 0, value);
                case HEX -> new Color(value, value, value); //placeholder
            };

            this.setForeground(isDarkColor(color));
        }
    }

    private static class Column extends ArrayList<Component>{
        private static final ArrayList<Column> columnList = new ArrayList<>();


        private Column(Component[] items) {
            this.addAll(Arrays.asList(items));
            columnList.add(this);
        }

        private static class AllColumns extends ArrayList<Column>{
            private static final ArrayList<JButton> buttons = new ArrayList<>();
            private static final ArrayList<JTextField> fields = new ArrayList<>();
            private static final ArrayList<JLabel> labels = new ArrayList<>();
            private static final ArrayList<JCheckBox> checks = new ArrayList<>();

            private void sortAllItems(){
                for(Column col:this){
                    for(Component com:col){
                        if(com instanceof JButton){buttons.add((JButton) com);}
                        else if(com instanceof JTextField){fields.add((JTextField) com);}
                        else if(com instanceof JLabel){labels.add((JLabel) com);}
                        else if(com instanceof JCheckBox){checks.add((JCheckBox) com);}
                    }
                }
            }

            private AllColumns(){
                this.addAll(columnList);
                this.sortAllItems();
            }

            public static JLabel getLabel(String name){
                for(JLabel label:labels){
                    if(label.getText().equals(name))
                        return label;
                }
                return null;
            }

            public static JTextField getTextField(String name){
                for(JTextField field:fields){
                    if(field.getText().equals(name))
                        return field;
                }
                return null;
            }

            public static JButton getButton(String name){
                for(JButton button:buttons){
                    if(button.getText().equals(name))
                        return button;
                }
                return null;
            }

            public static JCheckBox getCheckBox(String name){
                for(JCheckBox check:checks){
                    if(check.getText().equals(name))
                        return check;
                }
                return null;
            }

        }
    }
}
