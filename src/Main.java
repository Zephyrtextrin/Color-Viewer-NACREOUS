import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Main {

    private enum INT_CONSTANTS {
        WINDOW_WIDTH(1200), WINDOW_HEIGHT(WINDOW_WIDTH.value / 2), DARK_COLOR(190);
        public final int value;

        INT_CONSTANTS(int type) {
            this.value = type;
        }
    }

    public enum COLORS {
        RED, GREEN, BLUE, HEX
    }

    private enum UI_OBJECT {
        COLOR_PREVIEW_BOX,
        COLOR_PREVIEW_TEXT,
        RED_INPUT,
        GREEN_INPUT,
        BLUE_INPUT,
        HEX_INPUT,
        RANDOMIZE_BUTTON,
        SET_COLORS_BUTTON,
        COLORED_BGS_CHECKBOX,
        //HEX_TOGGLE
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



        new Column(new UIElement[]{
                new UIElement(UI_OBJECT.COLOR_PREVIEW_BOX, new ColorPreview()),
                new UIElement(UI_OBJECT.COLOR_PREVIEW_TEXT, new JLabel("Preview"))
        });

        new Column(new UIElement[]{
                new UIElement(UI_OBJECT.RED_INPUT, new InputField(COLORS.RED)),
                new UIElement(UI_OBJECT.BLUE_INPUT, new InputField(COLORS.BLUE)),
                new UIElement(UI_OBJECT.GREEN_INPUT, new InputField(COLORS.GREEN)),
                new UIElement(UI_OBJECT.HEX_INPUT, new InputField(COLORS.HEX)),

                new UIElement(UI_OBJECT.SET_COLORS_BUTTON, new JButton("Set Colors")),
                new UIElement(UI_OBJECT.RANDOMIZE_BUTTON, new JButton("Randomize"))
        });


        new Column(new UIElement[]{
                new UIElement(UI_OBJECT.COLORED_BGS_CHECKBOX, new JCheckBox("Enable/Disabled colored backgrounds"))
                //new UIElement(UI_OBJECT.HEX_CHECKBOX, new JCheckBox("Enable/Disable hex input (This will override RGB values!)"));
        });


        final Column.AllColumns allItems = new Column.AllColumns(panel);
        final Collection<InputField> fields = InputField.getAllFields().values();


        ((JButton)Objects.requireNonNull(allItems.getItem(UI_OBJECT.RANDOMIZE_BUTTON))).addActionListener(_ -> {
            final Random rand = new Random();
            for(InputField currentField:fields){
                currentField.setText(String.valueOf(rand.nextInt(256)));
            }

            refresh(allItems);

        });

        for(InputField currentField:fields){
            currentField.addActionListener(_->refresh(allItems));
        }

        ((JButton)Objects.requireNonNull(allItems.getItem(UI_OBJECT.SET_COLORS_BUTTON))).addActionListener(_->refresh(allItems));
        ((JCheckBox)Objects.requireNonNull(allItems.getItem(UI_OBJECT.COLORED_BGS_CHECKBOX))).addActionListener(_ -> refresh(allItems));

        panel.repaint();
        panel.revalidate();
    }

    //used to make sure the user did not input any words or negatives into the RGB input

    private static void refresh(Column.AllColumns allItems){
        final boolean coloredBGs = ((JCheckBox)Objects.requireNonNull(allItems.getItem(UI_OBJECT.COLORED_BGS_CHECKBOX))).isSelected();
        final Collection<InputField> fields = InputField.getAllFields().values();

        if(!coloredBGs){
            for(InputField currentField:fields){
                currentField.setBackground(Color.WHITE);
                currentField.setForeground(Color.BLACK);
            }
        }else{
            for(InputField currentField:fields){currentField.update();}
        }

        ((ColorPreview)Objects.requireNonNull(allItems.getItem(UI_OBJECT.COLOR_PREVIEW_BOX))).setColor();
        ((JLabel)Objects.requireNonNull(allItems.getItem(UI_OBJECT.COLOR_PREVIEW_TEXT))).setText("Color: "+InputField.getFullColor());

    }

    //checks to make sure user didnt include strings or negative ints
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
        //B value isnt included here because i found even with 255 blue it was still kinda hard to see with black text, so B should always be white
        int darkThreshold = INT_CONSTANTS.DARK_COLOR.value;

        if (R< darkThreshold && G <darkThreshold) {
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
            for (Column col : allItems){
                for(int i=0;i<col.size();i++){this.addObject(col.get(i).getBase(), X,i);} //use a regular for-loop here instead of an enchanced one because using an enchanced one overcomplicates things and requires you to make a seperate var for Y
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
                case HEX -> new Color(value, value, value); //placeholder hex is unfinished
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

        public static HashMap<COLORS, InputField> getAllFields(){return colorInputs;}
    }


    /*this is more or less an overcomplicated hashmap. the reason i made this instead of a hashmap is because hashmaps are an unsorted class,
    and it's rather important that the order of the items *stays sorted* because that determines the order that the items appear in on the ui.
    note to self: im really tired maybe theres a more obvious solution for this i should look at this entire codebase again in the morning -8/5/2025 00:18
     */
    private static class Column extends ArrayList<UIElement> {
        private static final ArrayList<Column> allColumnsTemp = new ArrayList<>();

        private Column(UIElement[] base){
            this.addAll(Arrays.asList(base));
            allColumnsTemp.add(this);
        }


        private Component get(UI_OBJECT value){
            for(UIElement component:this){
                if(component.getTag().equals(value)){return component.getBase();}
            }
            return null;
        }

        final private static class AllColumns extends ArrayList<Column> {

            public AllColumns(MainPanel panel) {
                this.addAll(allColumnsTemp);

                panel.addAllItems(this);
            }

            public Component getItem(UI_OBJECT target) {
                for (Column currentCol : this) {
                    if (currentCol.get(target) != null) {
                        return currentCol.get(target);
                    }
                }
                return null;
            }
        }
    }


    final private static class UIElement extends Component{
        final private Component base;
        final private UI_OBJECT tag;

        private UIElement(UI_OBJECT tag, Component base){
            this.base = base;
            this.tag = tag;
        }

        private Component getBase(){return base;}
        private UI_OBJECT getTag(){return tag;}
    }
}
