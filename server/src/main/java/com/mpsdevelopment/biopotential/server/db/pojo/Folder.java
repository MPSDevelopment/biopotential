package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Folder")
public class Folder extends BaseObject {
    public static final String ID_FOLDER = "idFolder";
    public static final String ID_FOLDER_GS = "i";
    public static final String PARENT_FOLDER_ID = "parentFolderId";
    public static final String PARENT_FOLDER_ID_GS = "p";
    public static final String FOLDER_NAME = "folderName";
    public static final String FOLDER_NAME_GS = "n";
    public static final String FOLDER_DESCRIPTION = "folderDescription";
    public static final String FOLDER_DESCRIPTION_GS = "d";
    public static final String DATABASE_ADDED = "dbdtsAdded";
    public static final String DATABASE_ADDED_GS = "a";
    public static final String SORT_PRIORITY = "sortPriority";
    public static final String SORT_PRIORITY_GS = "s";
    public static final String IS_IN_USE = "isInUse";
    public static final String IS_IN_USE_GS = "u";
    public static final String FOLDER_TYPE = "folderType";
    public static final String FOLDER_TYPE_GS = "t";

    public static final String PATTERNS_FOLDERS = "patternsFolders";

    /*@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Pattern.class)
    private List<Pattern> patterns = new ArrayList<>(0);*/
    /*@JoinTable(name="Folders_Patterns",
            joinColumns={@JoinColumn(name=Folder.ID_FIELD)},
            inverseJoinColumns={@JoinColumn(name=Pattern.ID_FIELD)})*/
    @OneToMany(mappedBy = "folder")
    private List<PatternsFolders> patternsFolders = new ArrayList<>();


    public Folder() {

    }

    @Expose
    @Column(name = ID_FOLDER)
    @SerializedName(ID_FOLDER_GS)
    private int idFolder;

    @Expose
    @Column(name = PARENT_FOLDER_ID)
    @SerializedName(PARENT_FOLDER_ID_GS)
    private String parentFolderId;

    @Expose
    @Column(name = FOLDER_NAME)
    @SerializedName(FOLDER_NAME_GS)
    private String folderName;

    @Expose
    @Column(name = FOLDER_DESCRIPTION, length = 2000)
    @SerializedName(FOLDER_DESCRIPTION_GS)
    private String folderDescription;

    @Expose
    @Column(name = DATABASE_ADDED)
    @SerializedName(DATABASE_ADDED_GS)
    private String dbdtsAdded;

    @Expose
    @Column(name = SORT_PRIORITY)
    @SerializedName(SORT_PRIORITY_GS)
    private String sortPriority;

    @Expose
    @Column(name = IS_IN_USE)
    @SerializedName(IS_IN_USE_GS)
    private int isInUse;

    @Expose
    @Column(name = FOLDER_TYPE)
    @SerializedName(FOLDER_TYPE_GS)
    private String folderType;


    /*public List<Pattern> getPattern() {
        return patterns;
    }
    public void setPattern(List<Pattern> patternses) {
        this.patterns = patternses;
    }*/

    public List<PatternsFolders> getPatternsFolders() {
        return patternsFolders;
    }

    public void setPatternsFolders(List<PatternsFolders> patternseFolderses) {
        this.patternsFolders = patternseFolderses;
    }

    public int getIdFolder() {
        return idFolder;
    }

    public Folder setIdFolder(int idFolder) {
        this.idFolder = idFolder;
        return this;
    }
    
    /*public Folder setIdFolder(Integer idFolder) {
        this.idFolder = Integer.toUnsignedLong(idFolder);
        return this;
    }*/

    public String getParentFolderId() {
        return parentFolderId;
    }

    public Folder setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
        return this;
    }

    public String getFolderName() {
        return folderName;
    }

    public Folder setFolderName(String folderName) {
        this.folderName = folderName;
        return this;
    }

    public String getFolderDescription() {
        return folderDescription;
    }

    public Folder setFolderDescription(String folderDescription) {
        this.folderDescription = folderDescription;
        return this;
    }

    public String getDbdtsAdded() {
        return dbdtsAdded;
    }

    public Folder setDbdtsAdded(String dbdtsAdded) {
        this.dbdtsAdded = dbdtsAdded;
        return this;
    }

    public String getSortPriority() {
        return sortPriority;
    }

    public Folder setSortPriority(String sortPriority) {
        this.sortPriority = sortPriority;
        return this;
    }

    public Integer getIsInUse() {
        return isInUse;
    }

    public Folder setIsInUse(Integer isInUse) {
        this.isInUse = isInUse;
        return this;
    }

    public String getFolderType() {
        return folderType;
    }

    public Folder setFolderType(String folderType) {
        this.folderType = folderType;
        return this;
    }
}
