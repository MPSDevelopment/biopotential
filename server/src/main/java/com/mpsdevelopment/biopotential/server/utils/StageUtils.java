package com.mpsdevelopment.biopotential.server.utils;

import com.mpsdevelopment.biopotential.server.settings.StageSettings;
import com.mpsdevelopment.biopotential.server.stage.ScreenHelper;

import com.mpsdevelopment.biopotential.server.stage.SkinResourceManager;
import com.mpsdevelopment.biopotential.server.ui.dialogfx.DialogFX;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;

public class StageUtils {

	private static Rectangle2D screenBounds = ScreenHelper.getVisualScreenBounds(0);

	@SuppressWarnings("rawtypes")
	public static Stage createStage(Stage owner, Pane panel, double widthPanel, double heightPanel, String panelTitle, String panelName, double x, double y, Double width, Double height, Class clazz, String cssFileName, boolean decorated, boolean modal, boolean show) {
		return createStage(owner, panel, widthPanel, heightPanel, panelTitle, panelName, x, y, width, height, clazz, cssFileName, decorated, modal, Color.BLACK, show);
	}

	
	public static double getCenterX(){
		return screenBounds.getWidth()/4.0;
	}
	
	public static double getCenterY(){
		return screenBounds.getHeight()/4.0;
	}
	
	public static double getHeight(){
		return screenBounds.getHeight();
	}
	public static double getWidth(){
		return screenBounds.getWidth();
	}
		
	
	public static Stage createStage(Stage owner, Pane panel, StageSettings settings) {

		screenBounds = ScreenHelper.getVisualScreenBounds(0);
		if (settings.getWidth() == null) {
			settings.setWidth(screenBounds.getWidth());
		}
		if (settings.getHeight() == null) {
			settings.setHeight(screenBounds.getHeight());
		}
		StageStyle style;

		if (settings.isDecorated()) {
			style = StageStyle.DECORATED;
		} else {
			style = StageStyle.UNDECORATED;
		}
		Stage stage = new Stage(style);
		Group root = new Group();
		Scene scene = new Scene(root, settings.getWidth(), settings.getHeight(), settings.getColor());
		scene.getStylesheets().add(SkinResourceManager.getSkinResource());
		currentScene = scene;
		if (settings.getCssFileName() != null) {
			scene.getStylesheets().clear();
			scene.getStylesheets().add(settings.getClazz().getResource(settings.getCssFileName()).toExternalForm());
		}
		stage.setScene(scene);
		if (settings.getPanelTitle() != null) {
			stage.setTitle(settings.getPanelTitle());
		}
		stage.initOwner(owner);
		if (settings.isModal()) {
			stage.initModality(Modality.APPLICATION_MODAL);
		}
		// stage.sizeToScene();

		if (owner != null) {
			stage.setWidth(owner.getWidth());
			stage.setHeight(owner.getHeight());
			stage.setX(owner.getX());
			stage.setY(owner.getY());
		} else {
			if (settings.getWidth() == 0 || settings.getHeight() == 0) {
				stage.setWidth(screenBounds.getWidth());
				stage.setHeight(screenBounds.getHeight());
				stage.setX(0);
				stage.setY(0);
			} else {
				stage.setWidth(settings.getWidth());
				stage.setHeight(settings.getHeight());
				stage.setX(settings.getX());
				stage.setY(settings.getY());
			}
		}

		if (ScreenHelper.getDisplaysCount() != 1) {
			stage.setX(settings.getX());
			stage.setY(settings.getY());
		}

		if (settings.getWidth() == 0 || settings.getHeight() == 0) {
			settings.setWidthPanel(settings.getWidth());
			settings.setHeightPanel(settings.getHeight());
		}

		panel.scaleXProperty().bind(stage.getScene().widthProperty().divide(settings.getWidthPanel()));
		panel.scaleYProperty().bind(stage.getScene().heightProperty().divide(settings.getHeightPanel()));

		panel.setMaxHeight(0);
		panel.setMaxWidth(0);

		root.getChildren().add(panel);

		if (BooleanUtils.isTrue(settings.isShow())) {
			stage.show();
		}

		return stage;
	}

	private static Scene currentScene;

	public static Scene getScene() {
		return currentScene;
	}

	public static Stage createStage(Stage owner, Pane panel, double widthPanel, double heightPanel, String panelTitle, String panelName) {

		StageStyle style = StageStyle.DECORATED;

		Stage stage = new Stage(style);
		Group root = new Group();
		Scene scene = new Scene(root, widthPanel, heightPanel);
		scene.getStylesheets().add(SkinResourceManager.getSkinResource());

		stage.setScene(scene);
		stage.setTitle(panelTitle);
		stage.initOwner(owner);
		// stage.sizeToScene();

		if (owner != null) {
			stage.setWidth(owner.getWidth());
			stage.setHeight(owner.getHeight());
			stage.setX(owner.getX());
			stage.setY(owner.getY());
		} else {
			stage.setWidth(widthPanel);
			stage.setHeight(heightPanel);
			stage.setX(0);
			stage.setY(0);
		}

		root.getChildren().add(panel);

		stage.show();

		return stage;
	}

	@SuppressWarnings("rawtypes")
	public static Stage createStage(Stage owner, Pane panel, double widthPanel, double heightPanel, String panelTitle, String panelName, double x, double y, Double width, Double height, Class clazz, String cssFileName, boolean decorated, boolean modal, Color color,
			boolean show) {

		screenBounds = ScreenHelper.getVisualScreenBounds(0);

		if (width == null) {
			width = screenBounds.getWidth();
		}

		if (height == null) {
			height = screenBounds.getHeight();
		}

		StageStyle style;

		if (decorated) {
			style = StageStyle.DECORATED;
		} else {
			style = StageStyle.UNDECORATED;
		}

		Stage stage = new Stage(style);
		Group root = new Group();

		Scene scene = new Scene(root, width, height, color);
		scene.getStylesheets().add(SkinResourceManager.getSkinResource());

		currentScene = scene;

		if (cssFileName != null) {
			scene.getStylesheets().clear();
			scene.setFill(Color.BLACK); // TODO this change for scene.setFill(Color.TRANSPARENT);
			scene.getStylesheets().add(clazz.getResource(cssFileName).toExternalForm());
		}

		stage.setScene(scene);
		stage.setTitle(panelTitle);
		stage.initOwner(owner);
		if (modal) {
			stage.initModality(Modality.APPLICATION_MODAL);
		}
		// stage.sizeToScene();

		if (owner != null) {
			stage.setWidth(owner.getWidth());
			stage.setHeight(owner.getHeight());
			stage.setX(owner.getX());
			stage.setY(owner.getY());
		} else {
			if (width == 0 || height == 0) {
				stage.setWidth(screenBounds.getWidth());
				stage.setHeight(screenBounds.getHeight());
				stage.setX(0);
				stage.setY(0);
			} else {
				stage.setWidth(width);
				stage.setHeight(height);
				stage.setX(x);
				stage.setY(y);
			}
		}

		if (ScreenHelper.getDisplaysCount() != 1) {
			stage.setX(x);
			stage.setY(y);
		}

		if (widthPanel == 0 || heightPanel == 0) {
			widthPanel = width;
			heightPanel = height;
		}

		panel.scaleXProperty().bind(stage.getScene().widthProperty().divide(widthPanel));
		panel.scaleYProperty().bind(stage.getScene().heightProperty().divide(heightPanel));

		panel.setMaxHeight(0);
		panel.setMaxWidth(0);

		root.getChildren().add(panel);

		if (BooleanUtils.isTrue(show)) {
			stage.show();
		}

		return stage;
	}

	public static void scaleToFullScreen(Stage stage, Integer x, Integer y, boolean decorated) {
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX(x == null ? screenBounds.getMinX() : x);
		stage.setY(y == null ? screenBounds.getMinY() : y);
		stage.setWidth(screenBounds.getWidth());
		stage.setHeight(screenBounds.getHeight());
		stage.initStyle(decorated ? StageStyle.DECORATED : StageStyle.UNDECORATED);
		stage.initModality(Modality.APPLICATION_MODAL);
	}

	// public static int createDialog(String question, Scene scene) {
	// DialogFX dialog = new DialogFX(DialogFX.Type.QUESTION);
	// dialog.setScene(scene);
	// dialog.addStylesheet(SkinResourceManager.getSkinResource());
	// dialog.setTitleText("Подтверждение");
	// dialog.setMessage(question);
	// dialog.addButtons(Arrays.asList("Да", "Нет"), 0, 1);
	//
	// return dialog.showDialog();
	// }

	public static int createDialog(String question, Scene scene, int width, int offSet) {
		int height = (int) (width / 2.5);
		DialogFX dialog = new DialogFX(DialogFX.Type.QUESTION);
		dialog.getStage().setWidth(width);
		dialog.getStage().setHeight(height);
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		// LoggerUtil.getLogger(StageUtils.class).info("X = %s", dialog.getStage().getX());
		dialog.getStage().setX(offSet + (screenBounds.getWidth() - width) / 2);
		dialog.getStage().setY((screenBounds.getHeight() - height) / 2);
		dialog.setTitleText("Подтверждение");
		dialog.setMessage(question);
		dialog.getMessage().setStyle(String.format("-fx-font-size: %spx; -fx-font-weight: bold; -fx-text-alignment: center;", width / 25));
		dialog.populateStage();
		dialog.addButtons(Arrays.asList("Да", "Нет"), 0, 1);
		for (Button button : dialog.getButons()) {
			button.setPrefHeight(height / 8);
			button.setPrefWidth(width / 4);
			button.setStyle(String.format("-fx-font-size: %spx; -fx-font-weight: bold; -fx-text-alignment: center;", width / 40));
		}
		return dialog.showBigDialog();
	}

	public static void createDoneDialog(String question, Scene scene) {
		DialogFX dialog = new DialogFX(DialogFX.Type.INFO);
		// dialog.setScene(scene);
		dialog.addStylesheet(SkinResourceManager.getSkinResource());
		dialog.setTitleText("");
		dialog.setMessage(question);
		dialog.showDialog();
	}

	public static void createDoneDialog(String question, Scene scene, int width, int offSet) {
		int height = (int) (width / 2.5);
		DialogFX dialog = new DialogFX(DialogFX.Type.INFO);
		dialog.getStage().setWidth(width);
		dialog.getStage().setHeight(height);
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		// LoggerUtil.getLogger(StageUtils.class).info("X = %s", dialog.getStage().getX());
		dialog.getStage().setX(offSet + (screenBounds.getWidth() - width) / 2);
		dialog.getStage().setY((screenBounds.getHeight() - height) / 2);
		dialog.setTitleText("");
		dialog.setMessage(question);
		dialog.getMessage().setStyle(String.format("-fx-font-size: %spx; -fx-font-weight: bold; -fx-text-alignment: center;", width / 25));
		dialog.addStylesheet(SkinResourceManager.getSkinResource());
		dialog.populateStage();
		for (Button button : dialog.getButons()) {
			button.setPrefHeight(height / 8);
			button.setPrefWidth(width / 4);
			button.setStyle(String.format("-fx-font-size: %spx; -fx-font-weight: bold; -fx-text-alignment: center;", width / 40));
		}
		dialog.showBigDialog();

	}

	public static void setScaledScene(Pane panel, Stage primaryStage, String title) {
		Pane root = new Pane();
		Scene scene = new Scene(root, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight(), Color.BLACK);
		scene.getStylesheets().add(SkinResourceManager.getSkinResource());

		panel.setMaxHeight(0);
		panel.setMaxWidth(0);

		primaryStage.setTitle(title);
		primaryStage.setScene(scene);

		root.getChildren().add(panel);

		panel.scaleXProperty().bind(primaryStage.getScene().widthProperty().divide(1920));
		panel.scaleYProperty().bind(primaryStage.getScene().heightProperty().divide(1080));
	}
}
