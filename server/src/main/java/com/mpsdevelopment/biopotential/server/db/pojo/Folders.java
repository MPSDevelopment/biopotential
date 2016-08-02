package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Folders")
public class Folders extends BaseObject {
    public static final String ID_FOLDER = "id_folder";
    public static final String ID_FOLDER_GS = "i";
    public static final String PARENT_FOLDER_ID = "parent_folder_id";
    public static final String PARENT_FOLDER_ID_GS = "p";
    public static final String FOLDER_NAME = "folder_name";
    public static final String FOLDER_NAME_GS = "n";
    public static final String FOLDER_DESCRIPTION = "folder_description";
    public static final String FOLDER_DESCRIPTION_GS = "d";
    public static final String DATABASE_ADDED = "dbdts_added";
    public static final String DATABASE_ADDED_GS = "add";
    public static final String SORT_PRIORITY = "sort_priority";
    public static final String SORT_PRIORITY_GS = "s";
    public static final String IS_IN_USE = "is_in_use";
    public static final String IS_IN_USE_GS = "use";
    public static final String FOLDER_TYPE = "folder_type";
    public static final String FOLDER_TYPE_GS = "t";

    public Folders() {

    }

    @Expose
    @Column(name = ID_FOLDER)
    @SerializedName(ID_FOLDER_GS)
    private int id_folder;

    @Expose
    @Column(name = PARENT_FOLDER_ID)
    @SerializedName(PARENT_FOLDER_ID_GS)
    private String parent_folder_id;

    @Expose
    @Column(name = FOLDER_NAME)
    @SerializedName(FOLDER_NAME_GS)
    private String folder_name;

    @Expose
    @Column(name = FOLDER_DESCRIPTION)
    @SerializedName(FOLDER_DESCRIPTION_GS)
    private String folder_description;

    @Expose
    @Column(name = DATABASE_ADDED)
    @SerializedName(DATABASE_ADDED_GS)
    private String dbdts_added;

    @Expose
    @Column(name = SORT_PRIORITY)
    @SerializedName(SORT_PRIORITY_GS)
    private String sort_priority;

    @Expose
    @Column(name = IS_IN_USE)
    @SerializedName(IS_IN_USE_GS)
    private Integer is_in_use;

    @Expose
    @Column(name = FOLDER_TYPE)
    @SerializedName(FOLDER_TYPE_GS)
    private String folder_type;

    public int getId_folder() {
        return id_folder;
    }

    public Folders setId_folder(int id_folder) {
        this.id_folder = id_folder;
        return this;
    }

    public String getParent_folder_id() {
        return parent_folder_id;
    }

    public Folders setParent_folder_id(String parent_folder_id) {
        this.parent_folder_id = parent_folder_id;
        return this;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public Folders setFolder_name(String folder_name) {
        this.folder_name = folder_name;
        return this;
    }

    public String getFolder_description() {
        return folder_description;
    }

    public Folders setFolder_description(String folder_description) {
        this.folder_description = folder_description;
        return this;
    }

    public String getDbdts_added() {
        return dbdts_added;
    }

    public Folders setDbdts_added(String dbdts_added) {
        this.dbdts_added = dbdts_added;
        return this;
    }

    public String getSort_priority() {
        return sort_priority;
    }

    public Folders setSort_priority(String sort_priority) {
        this.sort_priority = sort_priority;
        return this;
    }

    public Integer getIs_in_use() {
        return is_in_use;
    }

    public Folders setIs_in_use(Integer is_in_use) {
        this.is_in_use = is_in_use;
        return this;
    }

    public String getFolder_type() {
        return folder_type;
    }

    public Folders setFolder_type(String folder_type) {
        this.folder_type = folder_type;
        return this;
    }
}
