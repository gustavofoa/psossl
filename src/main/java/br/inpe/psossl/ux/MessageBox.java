package br.inpe.psossl.ux;

/**
 *
 * @author Gustavo Furtado
 */

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * JfxMessageBox is free MessageBox API for JavaFX 2.
 * 
 * Using JfxMessageBox, you can use MessageBox in JavaFX 2 easily.
 * 
 * <h3>
 * Usage 1</h3>
 * 
 * <code>
 * <pre>
 * import jfx.messagebox.MessageBox;
 * 
 *     MessageBox.show(primaryStage,
 *         "Sample of error dialog.\n\nDialog option is below.\n[MessageBox.ICON_ERROR]",
 *         "Error dialog",
 *         MessageBox.ICON_ERROR);
 * </pre>
 * </code>
 * 
 * <h3>
 * Usage 2</h3>
 * 
 * <code>
 * <pre>
 * import jfx.messagebox.MessageBox;
 * 
 *     MessageBox.show(primaryStage,
 *         "Sample of information dialog.\n\nDialog option is below.\n[MessageBox.ICON_INFORMATION | MessageBox.OK | MessageBox.CANCEL]",
 *         "Information dialog",
 *         MessageBox.ICON_INFORMATION | MessageBox.OK | MessageBox.CANCEL);
 * </pre>
 * </code>
 * 
 * <h3>
 * System Require</h3>
 * <ul>
 * <li>JfxMessageBox require JavaFX 2.2 or later.</li>
 * </ul>
 * 
 * @author Toshiki Iga
 * @see <a href="http://sourceforge.jp/projects/jfxmessagebox/wiki/">Project
 *      Home</a>
 * @see <a
 *      href="http://sourceforge.jp/projects/jfxmessagebox/wiki/GettingStarted">Getting
 *      Started</a>
 */
public class MessageBox {
    // ///////////////////////////////////////////////////
    // Types of icon.

    /**
     * Error icon.
     */
    public static final int ICON_ERROR = 0x01000000;
    /**
     * Warning icon.
     */
    public static final int ICON_WARNING = 0x02000000;
    /**
     * Information icon.
     */
    public static final int ICON_INFORMATION = 0x04000000;
    /**
     * Question icon.
     */
    public static final int ICON_QUESTION = 0x08000000;

    // ///////////////////////////////////////////////////
    // Types of button.
    /**
     * OK button.
     */
    public static final int OK = 0x00010000;
    /**
     * Cancel button.
     */
    public static final int CANCEL = 0x00020000;
    /**
     * Yes button.
     */
    public static final int YES = 0x00040000;
    /**
     * No button.
     */
    public static final int NO = 0x00080000;
    /**
     * Abort button.
     */
    public static final int ABORT = 0x00100000;
    /**
     * Retry button.
     */
    public static final int RETRY = 0x00200000;
    /**
     * Ignore button.
     */
    public static final int IGNORE = 0x00400000;

    // ///////////////////////////////////////////////////
    // Control for default button.
    /**
     * Set first button as default button.
     */
    public static final int DEFAULT_BUTTON1 = 0x00000100;
    /**
     * Set second button as default button.
     */
    public static final int DEFAULT_BUTTON2 = 0x00000200;
    /**
     * Set third button as default button.
     */
    public static final int DEFAULT_BUTTON3 = 0x00000400;
    /**
     * Set fourth button as default button.
     */
    public static final int DEFAULT_BUTTON4 = 0x00000800;

    /**
     * Hide constructor.
     */
    protected MessageBox() {
        // Do nothing.
    }

    /**
     * Display MessageBox.
     * 
     * <h3>Requirement</h3>
     * <ul>
     * <li>Program should run as JavaFX Application.</li>
     * </ul>
     * 
     * @param parent
     *            Parent window object. If null was set, dialog may be modeless.
     * @param message
     *            Message string for dialog.
     * @param title
     *            Title string for dialog.
     * @param option
     *            Display option. The option value is either one of the option
     *            constants OR'ing together (using the int "|" operator) two or
     *            more of those MessageBox option constants. ex.
     *            MessageBox.ICON_INFORMATION | MessageBox.OK |
     *            MessageBox.CANCEL
     * @return Selected button value. Default value is MessageBox.CANCEL.
     *         Selected button value is one of MessageBox.OK, MessageBox.CANCEL,
     *         MessageBox.YES, MessageBox.NO, MessageBox.ABORT,
     *         MessageBox.RETRY, MessageBox.IGNORE.
     */
    public static int show(final Window parent, final String message, final String title, final int option) {
        // Default return value is CANCEL.
        final int[] result = new int[] { CANCEL };

        // Create stage without iconized button.
        final Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle(title);
        dialog.setResizable(false);
        dialog.initModality(Modality.WINDOW_MODAL);
        if (parent != null) {
            // Only set in case of not null.
            dialog.initOwner(parent);
        }

        final VBox totalPane = new VBox();
        dialog.setScene(new Scene(totalPane));
        totalPane.setAlignment(Pos.CENTER);
        totalPane.setSpacing(2);

        final HBox pane = new HBox();
        totalPane.getChildren().add(pane);

        pane.setSpacing(10);

        // Pad left space.
        pane.getChildren().add(new Label("")); //$NON-NLS-1$
        pane.getChildren().add(new Label("")); //$NON-NLS-1$

        {
            final VBox vbox = new VBox();
            pane.getChildren().add(vbox);
            vbox.setAlignment(Pos.CENTER);

            if ((option & ICON_ERROR) == ICON_ERROR) {
                final Group group = MessageIconBuilder.drawErrorIcon(3);
                vbox.getChildren().add(group);
            } else if ((option & ICON_WARNING) == ICON_WARNING) {
                final Group group = MessageIconBuilder.drawWarningIcon(3);
                vbox.getChildren().add(group);
            } else if ((option & ICON_INFORMATION) == ICON_INFORMATION) {
                final Group group = MessageIconBuilder.drawInformationIcon(3);
                vbox.getChildren().add(group);
            } else if ((option & ICON_QUESTION) == ICON_QUESTION) {
                final Group group = MessageIconBuilder.drawQuestionIcon(3);
                vbox.getChildren().add(group);
            }
        }

        {
            final VBox vbox = new VBox();
            pane.getChildren().add(vbox);

            vbox.setAlignment(Pos.CENTER);

            vbox.getChildren().add(new Label(""));//$NON-NLS-1$
            vbox.getChildren().add(new Label(message));

            // Pad right space.
            pane.getChildren().add(new Label("")); //$NON-NLS-1$
            pane.getChildren().add(new Label("")); //$NON-NLS-1$

            // Pad message and buttons.
            vbox.getChildren().add(new Label("")); //$NON-NLS-1$
            vbox.getChildren().add(new Label("")); //$NON-NLS-1$

            boolean isButtonExists = false;

            final int[] BUTTON_LIST = new int[] { OK, YES, NO, ABORT, RETRY, IGNORE, CANCEL };
            final String[] BUTTON_STRING_LIST = new String[] {
                    Messages.getString("MessageBox.OK"), Messages.getString("MessageBox.YES"), Messages.getString("MessageBox.NO"), Messages.getString("MessageBox.ABORT"), Messages.getString("MessageBox.RETRY"), Messages.getString("MessageBox.IGNORE"), Messages.getString("MessageBox.CANCEL") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

            final HBox hboxButtons = new HBox();
            totalPane.getChildren().add(hboxButtons);
            hboxButtons.setSpacing(10);
            hboxButtons.setAlignment(Pos.CENTER);
            hboxButtons.getChildren().add(new Label("")); //$NON-NLS-1$

            int buttonCounter = 0;
            for (int index = 0; index < BUTTON_LIST.length; index++) {
                if ((option & BUTTON_LIST[index]) == BUTTON_LIST[index]) {
                    final Button btnAdd = new Button();
                    btnAdd.setText(BUTTON_STRING_LIST[index]);
                    isButtonExists = true;
                    final int resultValue = BUTTON_LIST[index];
                    btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent e) {
                            dialog.close();
                            result[0] = resultValue;
                        }
                    });
                    hboxButtons.getChildren().add(btnAdd);
                    buttonCounter++;

                    if ((option & CANCEL) == CANCEL) {
                        btnAdd.setCancelButton(true);
                    }

                    setupDefaultButton(option, buttonCounter, btnAdd);
                }
            }

            // In case of no button found.
            if (isButtonExists == false) {
                final Button btnAdd = new Button();
                hboxButtons.getChildren().add(btnAdd);
                btnAdd.setText(BUTTON_STRING_LIST[0]);
                btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        dialog.close();
                        result[0] = OK;
                    }
                });
                btnAdd.setCancelButton(true);
            }

            hboxButtons.getChildren().add(new Label("")); //$NON-NLS-1$

            totalPane.getChildren().add(new Label(""));//$NON-NLS-1$
        }

        // Below method is supported JavaFX 2.2 or lator.
        dialog.showAndWait();

        return result[0];
    }

    /**
     * Set up default button.
     * 
     * @param option
     * @param buttonCounter
     * @param btnAdd
     */
    private static void setupDefaultButton(final int option, final int buttonCounter, final Button btnAdd) {
        switch (buttonCounter) {
        case 1:
            if ((option & DEFAULT_BUTTON1) == DEFAULT_BUTTON1) {
                btnAdd.setDefaultButton(true);
            }
            break;
        case 2:
            if ((option & DEFAULT_BUTTON2) == DEFAULT_BUTTON2) {
                btnAdd.setDefaultButton(true);
            }
            break;
        case 3:
            if ((option & DEFAULT_BUTTON3) == DEFAULT_BUTTON3) {
                btnAdd.setDefaultButton(true);
            }
            break;
        case 4:
            if ((option & DEFAULT_BUTTON4) == DEFAULT_BUTTON4) {
                btnAdd.setDefaultButton(true);
            }
            break;
        }
    }
}

class MessageIconBuilder {
    /**
     * Draw error icon.
     * 
     * @param zoomRate
     * @param root
     */
    public static Group drawErrorIcon(final int zoomRate) {
        final Group root = new Group();

        final Circle circle = new Circle(5 * zoomRate, 5 * zoomRate, 5 * zoomRate);
        root.getChildren().add(circle);
        circle.setFill(Color.RED);

        {
            final Light.Spot light = new Light.Spot();
            light.setX(0);
            light.setY(0);
            light.setZ(10 * zoomRate);
            final Lighting lighting = new Lighting();
            lighting.setLight(light);
            circle.setEffect(lighting);
        }

        {
            final Line line = new Line(3 * zoomRate, 3 * zoomRate, 7 * zoomRate, 7 * zoomRate);
            line.setFill(null);
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(1.2 * zoomRate);
            root.getChildren().add(line);
        }

        {
            final Line line = new Line(7 * zoomRate, 3 * zoomRate, 3 * zoomRate, 7 * zoomRate);
            line.setFill(null);
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(1.2 * zoomRate);
            root.getChildren().add(line);
        }

        return root;
    }

    public static Group drawWarningIcon(final int zoomRate) {
        final Group root = new Group();

        final Path pathTriangle = new Path();
        {
            root.getChildren().add(pathTriangle);
            pathTriangle.getElements().addAll(
                    new MoveTo(4.5 * zoomRate, 0.5 * zoomRate),
                    new CubicCurveTo(4.5 * zoomRate, 0.5 * zoomRate, 5 * zoomRate, 0 * zoomRate, 5.5 * zoomRate,
                            0.5 * zoomRate),
                    new LineTo(10 * zoomRate, 9 * zoomRate),
                    new CubicCurveTo(10 * zoomRate, 9 * zoomRate, 10 * zoomRate, 10 * zoomRate, 9 * zoomRate,
                            10 * zoomRate),
                    new LineTo(1 * zoomRate, 10 * zoomRate),
                    new CubicCurveTo(1 * zoomRate, 10 * zoomRate, 0 * zoomRate, 10 * zoomRate, 0 * zoomRate,
                            9 * zoomRate), new ClosePath());
            pathTriangle.setStrokeWidth(0);
            pathTriangle.setFill(Color.YELLOW);
        }
        {
            final Light.Spot light = new Light.Spot();
            light.setX(0);
            light.setY(0);
            light.setZ(15 * zoomRate);
            final Lighting lighting = new Lighting();
            lighting.setLight(light);
            pathTriangle.setEffect(lighting);
        }

        {
            final Circle circle = new Circle(5 * zoomRate, 3.8 * zoomRate, 0.8 * zoomRate);
            root.getChildren().add(circle);
            circle.setFill(Color.BLACK);
        }

        {
            final Path path = new Path();
            root.getChildren().add(path);
            path.getElements().addAll(new MoveTo(5.8 * zoomRate, 3.8 * zoomRate),
                    new LineTo(5 * zoomRate, 7.5 * zoomRate), new LineTo(4.2 * zoomRate, 3.8 * zoomRate),
                    new ClosePath());
            path.setStrokeWidth(0);
            path.setFill(Color.BLACK);
        }

        {
            final Circle circle = new Circle(5 * zoomRate, 8.5 * zoomRate, 0.7 * zoomRate);
            root.getChildren().add(circle);
            circle.setFill(Color.BLACK);
        }

        return root;
    }

    public static Group drawInformationIcon(final int zoomRate) {
        final Group root = new Group();

        final Color baseColor = Color.rgb(30, 30, 255);
        final Color backColor = Color.rgb(120, 140, 255);

        final Path path = new Path();
        {
            root.getChildren().add(path);
            path.getElements().addAll(
                    new MoveTo(5 * zoomRate, 0),
                    new CubicCurveTo(5 * zoomRate, 0, 0, 0, 0, 4 * zoomRate),
                    new CubicCurveTo(0, 4 * zoomRate, 0, 8 * zoomRate, 4 * zoomRate, 8 * zoomRate),
                    new CubicCurveTo(4 * zoomRate, 8 * zoomRate, 4 * zoomRate, 10 * zoomRate, 6 * zoomRate,
                            10 * zoomRate),
                    new CubicCurveTo(6 * zoomRate, 10 * zoomRate, 5 * zoomRate, 10 * zoomRate, 5.5 * zoomRate,
                            8 * zoomRate),
                    new CubicCurveTo(5.5 * zoomRate, 8 * zoomRate, 10 * zoomRate, 8.6 * zoomRate, 10 * zoomRate,
                            4 * zoomRate),
                    new CubicCurveTo(10 * zoomRate, 4 * zoomRate, 10 * zoomRate, 0, 4 * zoomRate, 0));
            path.setStrokeWidth(0);
            path.setFill(backColor);
            path.setOpacity(0.6);
        }

        {
            final Light.Spot light = new Light.Spot();
            light.setX(0);
            light.setY(0);
            light.setZ(10 * zoomRate);
            final Lighting lighting = new Lighting();
            lighting.setLight(light);
            path.setEffect(lighting);
        }

        {
            Circle circle = new Circle(5. * zoomRate, 1.5 * zoomRate, 0.7 * zoomRate);
            root.getChildren().add(circle);
            circle.setFill(baseColor);
        }

        {
            final Line line = new Line(5 * zoomRate, 3.5 * zoomRate, 5 * zoomRate, 6 * zoomRate);
            root.getChildren().add(line);
            line.setStrokeWidth(1.2 * zoomRate);
            line.setStroke(baseColor);
        }

        {
            final Line line = new Line(3.8 * zoomRate, 3 * zoomRate, 5.4 * zoomRate, 3 * zoomRate);
            root.getChildren().add(line);
            line.setStrokeWidth(0.4 * zoomRate);
            line.setStroke(baseColor);
        }

        {
            final Line line = new Line(3.8 * zoomRate, 6.8 * zoomRate, 6.2 * zoomRate, 6.8 * zoomRate);
            root.getChildren().add(line);
            line.setStrokeWidth(0.4 * zoomRate);
            line.setStroke(baseColor);
        }

        return root;
    }

    public static Group drawQuestionIcon(final int zoomRate) {
        final Group root = new Group();

        final Color baseColor = Color.rgb(0, 64, 0);
        final Color backColor = Color.rgb(120, 255, 140);

        final Path pathBalloon = new Path();
        {
            root.getChildren().add(pathBalloon);
            pathBalloon.getElements().addAll(
                    new MoveTo(5 * zoomRate, 0),
                    new CubicCurveTo(5 * zoomRate, 0, 0, 0, 0, 4 * zoomRate),
                    new CubicCurveTo(0, 4 * zoomRate, 0, 8 * zoomRate, 4 * zoomRate, 8 * zoomRate),
                    new CubicCurveTo(4 * zoomRate, 8 * zoomRate, 4 * zoomRate, 10 * zoomRate, 6 * zoomRate,
                            10 * zoomRate),
                    new CubicCurveTo(6 * zoomRate, 10 * zoomRate, 5 * zoomRate, 10 * zoomRate, 5.5 * zoomRate,
                            8 * zoomRate),
                    new CubicCurveTo(5.5 * zoomRate, 8 * zoomRate, 10 * zoomRate, 8.6 * zoomRate, 10 * zoomRate,
                            4 * zoomRate),
                    new CubicCurveTo(10 * zoomRate, 4 * zoomRate, 10 * zoomRate, 0, 4 * zoomRate, 0));
            pathBalloon.setStrokeWidth(0);
            pathBalloon.setFill(backColor);
            pathBalloon.setOpacity(0.6);
        }

        {
            final Light.Spot light = new Light.Spot();
            light.setX(0);
            light.setY(0);
            light.setZ(10 * zoomRate);
            final Lighting lighting = new Lighting();
            lighting.setLight(light);
            pathBalloon.setEffect(lighting);
        }

        {
            Circle circle = new Circle(5 * zoomRate, 6.5 * zoomRate, 0.7 * zoomRate);
            root.getChildren().add(circle);
            circle.setFill(baseColor);
        }

        {
            final Path path = new Path();
            root.getChildren().add(path);
            path.getElements()
                    .addAll(new MoveTo(3 * zoomRate, 3 * zoomRate),
                            new CubicCurveTo(3 * zoomRate, 3 * zoomRate, 3 * zoomRate, 1 * zoomRate, 5 * zoomRate,
                                    1 * zoomRate),
                            new CubicCurveTo(5 * zoomRate, 1 * zoomRate, 7 * zoomRate, 1 * zoomRate, 7 * zoomRate,
                                    3 * zoomRate),
                            new CubicCurveTo(7 * zoomRate, 3 * zoomRate, 6.5 * zoomRate, 4 * zoomRate, 5.5 * zoomRate,
                                    4 * zoomRate),
                            new CubicCurveTo(5.5 * zoomRate, 4 * zoomRate, 5 * zoomRate, 4 * zoomRate, 5 * zoomRate,
                                    5.5 * zoomRate),
                            new CubicCurveTo(5 * zoomRate, 5 * zoomRate, 4.5 * zoomRate, 4.5 * zoomRate, 5 * zoomRate,
                                    3.5 * zoomRate),
                            new CubicCurveTo(5 * zoomRate, 3.5 * zoomRate, 6.5 * zoomRate, 3.5 * zoomRate,
                                    6.2 * zoomRate, 2.5 * zoomRate),
                            new CubicCurveTo(6.2 * zoomRate, 2.5 * zoomRate, 6.6 * zoomRate, 2 * zoomRate,
                                    5 * zoomRate, 1.5 * zoomRate),
                            new CubicCurveTo(5 * zoomRate, 1.5 * zoomRate, 3 * zoomRate, 1.5 * zoomRate, 4 * zoomRate,
                                    3 * zoomRate),
                            new CubicCurveTo(4 * zoomRate, 3 * zoomRate, 3.5 * zoomRate, 4 * zoomRate, 3 * zoomRate,
                                    3 * zoomRate));
            path.setStrokeWidth(0);
            path.setFill(baseColor);
        }

        return root;
    }
}

class Messages {
    private static final String BUNDLE_NAME = "br.inpe.psossl.ux.messageBoxLocale.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
