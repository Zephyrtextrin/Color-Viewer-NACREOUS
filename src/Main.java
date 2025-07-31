import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Main {

    private enum INT_CONSTANTS {
        WINDOW_WIDTH(1200), WINDOW_HEIGHT(WINDOW_WIDTH.value / 2), DARK_COLOR(167);
        public final int value;

        INT_CONSTANTS(int type) {
            this.value = type;
        }
    }

    public enum COLORS {
        RED, GREEN, BLUE, HEX
    }

    private enum UI_OBJECTS {
        COLOR_PREVIEW_BOX,
        COLOR_PREVIEW_TEXT,
        RED_INPUT,
        GREEN_INPUT,
        BLUE_INPUT,
        HEX_INPUT,
        RANDOMIZE_BUTTON,
        SET_COLORS_BUTTON,
        COLORED_BGS_CHECKBOX
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


        JFrame frame = new JFrame("Color Viewer [NACREOUS] ");
        frame.setSize(INT_CONSTANTS.WINDOW_WIDTH.value, INT_CONSTANTS.WINDOW_HEIGHT.value);
        frame.setResizable(false);
        frame.setVisible(true);

        MainPanel panel = new MainPanel();
        panel.setLayout(new GridBagLayout());
        frame.add(panel);

        final Column column1 = new Column();

        column1.put(UI_OBJECTS.COLOR_PREVIEW_BOX, new ColorPreview());
        column1.put(UI_OBJECTS.COLOR_PREVIEW_TEXT, new JLabel("Preview"));

        final Column column2 = new Column();

        column2.put(UI_OBJECTS.RED_INPUT, new InputField(COLORS.RED));
        column2.put(UI_OBJECTS.BLUE_INPUT, new InputField(COLORS.BLUE));
        column2.put(UI_OBJECTS.GREEN_INPUT, new InputField(COLORS.GREEN));
        column2.put(UI_OBJECTS.HEX_INPUT, new InputField(COLORS.HEX));

        column2.put(UI_OBJECTS.SET_COLORS_BUTTON, new JButton("Set Colors"));
        column2.put(UI_OBJECTS.RANDOMIZE_BUTTON, new JButton("Randomize"));


        final Column column3 = new Column();

        //column3.put(UI_OBJECTS.HEX_CHECKBOX, new JCheckBox("Enable/Disable hex input (This will override RGB values!)"));
        column3.put(UI_OBJECTS.COLORED_BGS_CHECKBOX, new JCheckBox("Enable/Disabled colored backgrounds"));


        Column.AllColumns allItems = new Column.AllColumns(panel);
        //ACTION LISTENERS-----------------------------


        ((JButton) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.RANDOMIZE_BUTTON))).addActionListener(_ -> {
            Random rand = new Random();
            ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.RED_INPUT))).setText(String.valueOf(rand.nextInt(256)));
            ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.GREEN_INPUT))).setText(String.valueOf(rand.nextInt(256)));
            ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.BLUE_INPUT))).setText(String.valueOf(rand.nextInt(256)));

            refresh(allItems);

        });

        ((JCheckBox) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.COLORED_BGS_CHECKBOX))).addActionListener(_ -> refresh(allItems));

        ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.RED_INPUT))).addActionListener(_->refresh(allItems));
        ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.GREEN_INPUT))).addActionListener(_->refresh(allItems));
        ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.BLUE_INPUT))).addActionListener(_->refresh(allItems));
        ((JButton) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.SET_COLORS_BUTTON))).addActionListener(_->refresh(allItems));



        panel.repaint();
        panel.revalidate();
    }

    //used to make sure the user did not input any words or negatives into the RGB input

    private static void refresh(Column.AllColumns allItems){
        final boolean coloredBGs = ((JCheckBox) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.COLORED_BGS_CHECKBOX))).isSelected();

        if(!coloredBGs){
            Objects.requireNonNull(allItems.getItem(UI_OBJECTS.RED_INPUT)).setBackground(Color.WHITE);
            Objects.requireNonNull(allItems.getItem(UI_OBJECTS.BLUE_INPUT)).setBackground(Color.WHITE);
            Objects.requireNonNull(allItems.getItem(UI_OBJECTS.GREEN_INPUT)).setBackground(Color.WHITE);
            Objects.requireNonNull(allItems.getItem(UI_OBJECTS.RED_INPUT)).setForeground(Color.BLACK);
            Objects.requireNonNull(allItems.getItem(UI_OBJECTS.GREEN_INPUT)).setForeground(Color.BLACK);
            Objects.requireNonNull(allItems.getItem(UI_OBJECTS.BLUE_INPUT)).setForeground(Color.BLACK);
        }else{
            ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.RED_INPUT))).update();
            ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.GREEN_INPUT))).update();
            ((InputField) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.BLUE_INPUT))).update();
        }

        ((ColorPreview) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.COLOR_PREVIEW_BOX))).setColor();
        ((JLabel) Objects.requireNonNull(allItems.getItem(UI_OBJECTS.COLOR_PREVIEW_TEXT))).setText("Color: "+InputField.getFullColor());

    }

    private static int intCheck(String input) {
        boolean valid = true;
        int out = 0;
        try {
            Integer.parseInt(input);
        } catch (Exception e) {
            valid = false;
        }

        if (valid) {
            out = Integer.parseInt(input);
            if (out >= 256) {
                out = 255;
            } else if (out < 0) {
                out = 0;
            }
        }

        return out;
    }

    private static Color isDarkColor(Color color) {
        int R = color.getRed();
        int G = color.getGreen();
        int B = color.getBlue();
        int darkThreshold = INT_CONSTANTS.DARK_COLOR.value;

        if (R < darkThreshold && G < darkThreshold && B < darkThreshold) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
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

        public void addAllItems(Column.AllColumns allItems) {
            int X = 0;
            int Y = 0;
            for (Column col : allItems){
                final Collection<Component> currentColItems = col.values();
                for (Component component : currentColItems) {
                    this.addObject(component, X, Y);
                    Y++;
                }
                Y = 0;
                X++;
            }

        }
    }

    private static class ColorPreview extends JTextField {
        private ColorPreview() {
            this.setBackground(new Color(0, 0, 0));
            this.setEditable(false);
        }

        public void setColor(){
            this.setBackground(InputField.getFullColor());
        }
    }

    private static class InputField extends JTextField {
        COLORS COLOR_TYPE;
        private final static HashMap<COLORS, InputField> colorInputs = new HashMap<>();

        private InputField(COLORS type) {
            COLOR_TYPE = type;
            this.setText(COLOR_TYPE.toString().charAt(0) + " Value");

            if (COLOR_TYPE == COLORS.HEX) {
                this.setVisible(hexState);
                this.setEnabled(hexState);
            }
            colorInputs.put(COLOR_TYPE, this);
        }

        private Color findColor(){
            int value = intCheck(this.getText());
            Color color = switch (COLOR_TYPE) {
                case RED -> new Color(value, 0, 0);
                case GREEN -> new Color(0, value, 0);
                case BLUE -> new Color(0, 0, value);
                case HEX -> new Color(value, value, value); //placeholder
            };

            this.setForeground(isDarkColor(color));
            return color;
        }

        public void update(){
            this.setText(Integer.toString(intCheck(this.getText())));

            if (coloredBGs){
                this.setForeground(isDarkColor(findColor()));
                this.setBackground(findColor());
            }
        }

        public static Color getFullColor(){
            final int R = intCheck(colorInputs.get(COLORS.RED).getText());
            final int G = intCheck(colorInputs.get(COLORS.GREEN).getText());
            final int B = intCheck(colorInputs.get(COLORS.BLUE).getText());
            return new Color(R,G,B);
        }
    }

        private static class Column extends HashMap<UI_OBJECTS, Component> {
            private static final ArrayList<Column> allColumnsTemp = new ArrayList<>();

            private Column() {
                allColumnsTemp.add(this);
            }

            final static class AllColumns extends ArrayList<Column> {

                public AllColumns(MainPanel panel) {
                    this.addAll(allColumnsTemp);

                    panel.addAllItems(this);
                }

                public Component getItem(UI_OBJECTS target) {
                    for (Column currentCol : this) {
                        if (currentCol.get(target) != null) {
                            return currentCol.get(target);
                        }
                    }
                    return null;
                }
            }
    }
}
