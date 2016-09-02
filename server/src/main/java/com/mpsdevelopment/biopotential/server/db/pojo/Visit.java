package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Visit")
public class Visit extends BaseObject{

    @Expose
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "User")
    private User user;

    @Expose
    @Column(name = "Date")
    private Date date;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
