package com.mpsdevelopment.helicopter.license.dialogfx;

import java.util.ArrayList;
import java.util.List;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Mark Heckler (mark.heckler@gmail.com, @HecklerMark)
 */
public final class DialogFX extends Stage {

	private static final Logger LOGGER = LoggerUtil.getLogger(DialogFX.class);

	/**
	 * Type of dialog box is one of the following, each with a distinct icon:
	 * <p/>
	 * ACCEPT = check mark icon
	 * <p/>
	 * ERROR = red 'X' icon
	 * <p/>
	 * INFO = blue 'i' (information) icon
	 * <p/>
	 * QUESTION = blue question mark icon
	 * <p/>
	 * If no type is specified in the constructor, the default is INFO.
	 */
	public enum Type {
		ACCEPT, ERROR, INFO, QUESTION
	}

	private Type type;
	private Stage stage;
	private BorderPane pane = new BorderPane();
	private ImageView icon = new ImageView();
	private Label message = new Label();
	private HBox buttonBox = new HBox(10);
	private List<String> buttonLabels;
	private int buttonCancel = -1;
	private int buttonCount = 0;
	private int buttonSelected = -1;
	private List<String> stylesheets = new ArrayList<>();
	private List<Button> buttons = new ArrayList<>();

	/**
	 * Default constructor for a DialogFX dialog box. Creates an INFO box.
	 * 
	 * @see Type
	 */
	public DialogFX() {
		initDialog(Type.INFO);
	}

	/**
	 * Constructor for a DialogFX dialog box that accepts one of the enumerated types listed above.
	 * 
	 * @param t
	 *            The type of DialogFX dialog box to create.
	 * @see Type
	 */
	public DialogFX(Type t) {
		initDialog(t);
	}

	/**
	 * Public method used to add custom buttons to a DialogFX dialog.
	 * 
	 * @param labels
	 *            A list of String variables. While technically unlimited, usability makes the practical limit around three.
	 */
	public void addButtons(List<String> labels) {
		addButtons(labels, -1, -1);
	}

	/**
	 * Public method used to add custom buttons to a DialogFX dialog. Additionally, default and cancel buttons are identified so user can trigger them with the ENTER key (default)
	 * and ESCAPE (cancel).
	 * 
	 * @param labels
	 *            A list of String variables. While technically unlimited, usability makes the practical limit around three.
	 * @param defaultBtn
	 *            Position within the list of labels of the button to designate as the default button.
	 * @param cancelBtn
	 *            Position within the list of labels of the button to designate as the cancel button.
	 */
	public void addButtons(List<String> labels, int defaultBtn, int cancelBtn) {
		buttonLabels = labels;

		for (int i = 0; i < labels.size(); i++) {
			final Button btn = new Button(labels.get(i));

			btn.setDefaultButton(i == defaultBtn);
			btn.setCancelButton(i == cancelBtn);
			buttons.add(btn);

			if (i == defaultBtn) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						btn.requestFocus();
					}
				});
			}

			buttonCancel = cancelBtn;

			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent evt) {
					buttonSelected = buttonLabels.indexOf(((Button) evt.getSource()).getText());
					stage.close();
				}
			});
			buttonBox.getChildren().add(btn);
		}

		buttonBox.setAlignment(Pos.CENTER);

		BorderPane.setAlignment(buttonBox, Pos.CENTER);
		BorderPane.setMargin(buttonBox, new Insets(5, 5, 5, 5));
		pane.setBottom(buttonBox);
		buttonCount = labels.size();
	}

	public List<Button> getButons() {
		return buttons;
	}

	public void addOKButton() {
		List<String> labels = new ArrayList<>(1);
		labels.add("OK");

		addButtons(labels, 0, 0);
	}

	private void addYesNoButtons() {
		/*
		 * No default or cancel buttons designated, by design. Some cases would require the Yes button to be default & No to cancel, while others would require the opposite. You as
		 * the developer can assign default/cancel Yes/No buttons using the full addButtons() method if required. You have the power!
		 */
		List<String> labels = new ArrayList<>(2);
		labels.add("Yes");
		labels.add("No");

		addButtons(labels);
	}

	/**
	 * Allows developer to add stylesheet for DialogFX dialog, supplementing or overriding existing styling.
	 * 
	 * @param stylesheet
	 *            String variable containing the name or path/name of the stylesheet to add to the dialog's scene and contained controls.
	 */
	public void addStylesheet(String stylesheet) {
		try {
			// String newStyle = this.getClass().getResource(stylesheet).toExternalForm();
			stylesheets.add(stylesheet);
		} catch (Exception ex) {
			System.err.println("Unable to find specified stylesheet: " + stylesheet);
			System.err.println("Error message: " + ex.getMessage());
		}
	}

	private void initDialog(Type t) {
		stage = new Stage();

		setType(t);
		stage.initModality(Modality.APPLICATION_MODAL);
		// stage.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
	}

	private void loadIconFromResource(String fileName) {
		Image imgIcon = new Image(getClass().getResourceAsStream(fileName));
		icon.setPreserveRatio(true);
		icon.setFitHeight(48);
		icon.setImage(imgIcon);
	}

	private void loadIconFromResource(String fileName, Integer iconSize) {
		Image imgIcon = new Image(getClass().getResourceAsStream(fileName));
		icon.setPreserveRatio(true);
		icon.setFitHeight(iconSize == 0 ? 48 : iconSize);
		icon.setImage(imgIcon);
	}

	/**
	 * Sets the text displayed within the DialogFX dialog box. Word wrap ensures that all text is displayed.
	 * 
	 * @param msg
	 *            String variable containing the text to display.
	 */
	public void setMessage(String msg) {
		message.setText(msg);
		message.setWrapText(true);
	}

	/**
	 * Sets the modality of the DialogFX dialog box.
	 * 
	 * @param isModal
	 *            Boolean. A true value = APPLICATION_MODAL, false = NONE.
	 */
	public void setModal(boolean isModal) {
		stage.initModality((isModal ? Modality.APPLICATION_MODAL : Modality.NONE));
	}

	/**
	 * Sets the text diplayed in the title bar of the DialogFX dialog box.
	 * 
	 * @param title
	 *            String containing the text to place in the title bar.
	 */
	public void setTitleText(String title) {
		stage.setTitle(title);
	}

	/**
	 * Sets the Type of DialogFX dialog box to display.
	 * 
	 * @param typeToSet
	 *            One of the supported types of dialogs.
	 * @see Type
	 */
	public void setType(Type typeToSet) {
		type = typeToSet;
	}

	public void populateStage() {
		String iconFile;

		switch (type) {
		case ACCEPT:
			iconFile = "Dialog-accept2.png";
			addOKButton();
			break;
		case ERROR:
			iconFile = "Dialog-error2.png";
			addOKButton();
			break;
		case INFO:
			iconFile = "Dialog-info.png";
			addOKButton();
			break;
		case QUESTION:
			iconFile = "Dialog-question2.png";
			break;
		default:
			iconFile = "Dialog-info.png";
			break;
		}

		try {
			loadIconFromResource(iconFile, (int) getStage().getWidth() / 10);
		} catch (Exception ex) {
			LOGGER.error("Exception trying to load icon file %s: %s", iconFile, ex.getMessage());
		}

		BorderPane.setAlignment(icon, Pos.CENTER);
		BorderPane.setMargin(icon, new Insets(5, 5, 5, 5));
		pane.setLeft(icon);

		BorderPane.setAlignment(message, Pos.CENTER);
		BorderPane.setMargin(message, new Insets(5, 5, 5, 5));
		pane.setCenter(message);

		Scene scene = new Scene(pane);
		for (String stylesheet : stylesheets) {
			try {
				scene.getStylesheets().add(stylesheet);
			} catch (Exception ex) {
				System.err.println("Unable to load specified stylesheet: " + stylesheet);
				System.err.println(ex.getMessage());
			}
		}
		stage.setScene(scene);
	}

	public Stage getStage() {
		return stage;
	}

	/**
	 * Displays the DialogFX dialog box and waits for user input.
	 * 
	 * @return The index of the button pressed.
	 */
	public int showBigDialog() {
		// populateStage();
		if (type == Type.QUESTION) {
			if (buttonCount == 0) {
				addYesNoButtons();
			}
		}
		stage.setResizable(false);
		stage.sizeToScene();
		// stage.centerOnScreen();
		stage.showAndWait();
		return (buttonSelected == -1 ? buttonCancel : buttonSelected);
	}

	public int showDialog() {
		populateStage();
		if (type == Type.QUESTION) {
			if (buttonCount == 0) {
				addYesNoButtons();
			}
		}
		stage.setResizable(false);
		stage.sizeToScene();
		stage.centerOnScreen();
		stage.showAndWait();
		return (buttonSelected == -1 ? buttonCancel : buttonSelected);
	}

	public Label getMessage() {
		return message;
	}

	public int getButtonCount() {
		return buttonCount;
	}
}
