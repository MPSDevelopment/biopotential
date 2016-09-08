package com.mpsdevelopment.biopotential.server.eventbus.event;


import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.eventbus.Event;

public class SelectUserEvent extends Event {

    private User user;

    public SelectUserEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
