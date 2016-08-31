package com.mpsdevelopment.biopotential.server.db.pojo;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Patterns_Folders")
public class PatternsFolderses {
    private long id;
    private Folders folders;
    private Patterns patterns;

    // additional fields
    private boolean paternal;

    @Id
    @GeneratedValue
    @Column(name = "Patterns_Folders_ID")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Folders_ID")
    public Folders getFolders() {
        return folders;
    }

    public void setFolders(Folders folders) {
        this.folders = folders;
    }
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Patterns_ID")
    public Patterns getPatterns() {
        return patterns;
    }

    public void setPatterns(Patterns patterns) {
        this.patterns = patterns;
    }

    @Column(name = "paternal")
    public boolean isPaternal() {
        return paternal;
    }

    public void setPaternal(boolean paternal) {
        this.paternal = paternal;
    }


}