package com.mpsdevelopment.biopotential.server.db.pojo;

import javax.persistence.*;

@Entity
@Table(name = "Patterns_Folders")
public class PatternsFolders extends BaseObject{

    public static final String PATTERNS = "pattern";
    
    public static final String CORRECTORS = "correctors";

//    private long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "Folder_ID")
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "Pattern_ID")
    private Pattern pattern;

    // additional fields
    @Column(name = "correctors")
    private Long correctors;

    /*@Id
    @GeneratedValue
    @Column(name = "Patterns_Folders_ID")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }*/

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Long getCorrectors() {
        return correctors;
    }

    public void setCorrectors(Long paternal) {
        this.correctors = paternal;
    }
}