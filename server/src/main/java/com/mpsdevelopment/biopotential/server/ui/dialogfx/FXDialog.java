package com.mpsdevelopment.biopotential.server.ui.dialogfx;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FXDialog {

	private Stage primaryStage;
	private Parent parent;
	private DialogFX dialog;

	public FXDialog() {
	}

	public FXDialog(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public FXDialog(Parent parent) {
		this.parent = parent;
	}

	public void getQuestionDialog(boolean removeFromParent) {
		List<String> buttonLabels = new ArrayList<>(3);
		buttonLabels.add("Сохранить");
		buttonLabels.add("Не сохранять");
		buttonLabels.add("Отменить");

		dialog = new DialogFX(DialogFX.Type.QUESTION);

		dialog.setTitleText("Подтверждение");
		dialog.setMessage("Есть несохраненные данные, сохранить?");
		dialog.addButtons(buttonLabels, 0, 1);

		int answer = dialog.showDialog();
		if (answer == 0) {
			dialog.close();
			setSaveAnswerAction();
		} else if (answer == 1) {
			dialog.close();
			setNoSaveAnswerAction(removeFromParent);
		} else {
			dialog.close();
			setCancelAnswerAction();
		}
	}

	public void setSaveAnswerAction() {
		getSaveDialog();
		primaryStage.close();
	}

	public void setNoSaveAnswerAction(boolean removeFromParent) {
		if (removeFromParent) {
			((Pane) parent.getParent()).getChildren().clear();
		} else {
			primaryStage.close();
		}
	}

	public void setCancelAnswerAction() {

	}

	public void getSaveDialog() {
		dialog = new DialogFX(DialogFX.Type.INFO);
		dialog.setTitleText("Информация");
		dialog.setMessage("Изменения сохранены успешно.");
		dialog.showDialog();
	}

	public void getErrorDialog(String messageString) {
		dialog = new DialogFX(DialogFX.Type.ERROR);
		dialog.setMessage(messageString);
		dialog.showDialog();
	}

	public void getInfoDialog(String messageString) {
		dialog = new DialogFX(DialogFX.Type.INFO);
		dialog.setMessage(messageString);
		dialog.showDialog();
	}

	public void getErrorDialog(String messageString, boolean closeStage) {
		dialog = new DialogFX(DialogFX.Type.ERROR);
		dialog.setMessage(messageString);
		dialog.showDialog();
		if (closeStage) {
			primaryStage.close();
		}
	}

	public void closeDialog() {
		if (dialog != null) {
			dialog.close();
		}
	}
}
