package com.mpsdevelopment.biopotential.server;

import javafx.scene.layout.Pane;

/**
 * Created by Lena on 07.08.2015.
 */
public interface Controller {
    Pane getView();
    void setView(Pane view);
}
