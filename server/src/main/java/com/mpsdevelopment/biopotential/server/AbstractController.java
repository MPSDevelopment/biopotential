package com.mpsdevelopment.biopotential.server;

import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

/**
 * Created by Lena on 24.06.2015.
 */
public abstract class AbstractController implements Controller, Initializable {

	private Pane view;

	public Pane getView() {
		return view;
	}

	public void setView(Pane view) {
		this.view = view;
	}

	public void subscribe() {
	}

	public void unsubscribe() {
	}

	public void getUsers() {

	}

}
