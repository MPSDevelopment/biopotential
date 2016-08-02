package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Patterns")
public class Patterns extends BaseObject {
    public static final String ID_PATTERN = "id_pattern";
    public static final String ID_PATTERN_GS = "i";
    public static final String PATTERN_NAME = "pattern_name";
    public static final String PATTERN_NAME_GS = "pn";
    public static final String PATTERN_DESCRIPTION = "pattern_description";
    public static final String PATTERN_DESCRIPTION_GS = "pd";
    public static final String PATTERN_UID = "pattern_uid";
    public static final String PATTERN_UID_GS = "pu";
    public static final String SRC_HASH = "src_hash";
    public static final String SRC_HASH_GS = "sh";
    public static final String EDX_HASH = "edx_hash";
    public static final String EDX_HASH_GS = "eh";
    public static final String DATABASE_ADDED = "dbdts_added";
    public static final String DATABASE_ADDED_GS = "add";
    public static final String IS_IN_USE = "is_in_use";
    public static final String IS_IN_USE_GS = "use";
    public static final String PATTERN_SHORT_DESC = "pattern_short_desc";
    public static final String PATTERN_SHORT_DESC_GS = "patdesc";
    public static final String PATTERN_SOURCE_FILE_NAME = "pattern_source_file_name";
    public static final String PATTERN_SOURCE_FILE_NAME_GS = "pattern_source_file_name";
    public static final String PATTERN_MAX_PLAYING_TIME = "pattern_max_playing_time";
    public static final String PATTERN_MAX_PLAYING_TIME_GS = "patmaxplattime";
    public static final String PATTERN_TYPE = "pattern_type";
    public static final String PATTERN_TYPE_GS = "pattype";
    public static final String IS_CAN_BE_REPRODUCED = "is_can_be_reproduced";
    public static final String IS_CAN_BE_REPRODUCED_GS = "isrep";
    public static final String SMULS = "smuls";
    public static final String SMULS_GS = "s";
    public static final String EDX_FILE_CREATION_DTS= "edx_file_creation_dts";
    public static final String EDX_FILE_CREATION_DTS_GS = "edxfilecre";
    public static final String EDX_FILE_CREATION_DTS_MSECS = "edx_file_creation_dts_msecs";
    public static final String EDX_FILE_CREATION_DTS_MSECS_GS = "edxfilcrems";
    public static final String EDX_FILE_LAST_MODIFIED_DTS= "edx_file_last_modified_dts";
    public static final String EDX_FILE_LAST_MODIFIED_DTS_GS = "edxfilelastmod";
    public static final String EDX_FILE_LAST_MODIFIED_DTS_MSEC = "edx_file_last_modified_dts_msecs";
    public static final String EDX_FILE_LAST_MODIFIED_DTS_MSEC_GS = "edxfilelastmodms";
    public static final String LINKED_FOLDER_ID = "linked_folder_id";
    public static final String LINKED_FOLDER_ID_GS = "linfoldid";

    public Patterns() {

    }

    @Expose
    @Column(name = ID_PATTERN)
    @SerializedName(ID_PATTERN_GS)
    private int id_pattern;

    @Expose
    @Column(name = PATTERN_NAME)
    @SerializedName(PATTERN_NAME_GS)
    private String pattern_name;

    @Expose
    @Column(name = PATTERN_DESCRIPTION)
    @SerializedName(PATTERN_DESCRIPTION_GS)
    private String pattern_description;

    @Expose
    @Column(name = PATTERN_UID)
    @SerializedName(PATTERN_UID_GS)
    private String pattern_uid;

    @Expose
    @Column(name = SRC_HASH)
    @SerializedName(SRC_HASH_GS)
    private String src_hash;

    @Expose
    @Column(name = EDX_HASH)
    @SerializedName(EDX_HASH_GS)
    private String edx_hash;

    @Expose
    @Column(name = DATABASE_ADDED)
    @SerializedName(DATABASE_ADDED_GS)
    private String dbdts_added;

    @Expose
    @Column(name = IS_IN_USE)
    @SerializedName(IS_IN_USE_GS)
    private int is_in_use;

    @Expose
    @Column(name = PATTERN_SHORT_DESC)
    @SerializedName(PATTERN_SHORT_DESC_GS)
    private String pattern_short_desc;

    @Expose
    @Column(name = PATTERN_SOURCE_FILE_NAME)
    @SerializedName(PATTERN_SOURCE_FILE_NAME_GS)
    private String pattern_source_file_name;

    @Expose
    @Column(name = PATTERN_MAX_PLAYING_TIME)
    @SerializedName(PATTERN_MAX_PLAYING_TIME_GS)
    private float pattern_max_playing_time;

    @Expose
    @Column(name = PATTERN_TYPE)
    @SerializedName(PATTERN_TYPE_GS)
    private int pattern_type;

    @Expose
    @Column(name = IS_CAN_BE_REPRODUCED)
    @SerializedName(IS_CAN_BE_REPRODUCED_GS)
    private int is_can_be_reproduced;

    @Expose
    @Column(name = SMULS)
    @SerializedName(SMULS_GS)
    private String smuls;

    @Expose
    @Column(name = EDX_FILE_CREATION_DTS)
    @SerializedName(EDX_FILE_CREATION_DTS_GS)
    private String edx_file_creation_dts;

    @Expose
    @Column(name = EDX_FILE_CREATION_DTS_MSECS)
    @SerializedName(EDX_FILE_CREATION_DTS_MSECS_GS)
    private int edx_file_creation_dts_msecs;

    @Expose
    @Column(name = EDX_FILE_LAST_MODIFIED_DTS)
    @SerializedName(EDX_FILE_LAST_MODIFIED_DTS_GS)
    private String edx_file_last_modified_dts;

    @Expose
    @Column(name = EDX_FILE_LAST_MODIFIED_DTS_MSEC)
    @SerializedName(EDX_FILE_LAST_MODIFIED_DTS_MSEC_GS)
    private int edx_file_last_modified_dts_msecs;

    @Expose
    @Column(name = LINKED_FOLDER_ID)
    @SerializedName(LINKED_FOLDER_ID_GS)
    private int linked_folder_id;

    public int getId_pattern() {
        return id_pattern;
    }

    public Patterns setId_pattern(int id_pattern) {
        this.id_pattern = id_pattern;
        return this;
    }

    public String getPattern_name() {
        return pattern_name;
    }

    public Patterns setPattern_name(String pattern_name) {
        this.pattern_name = pattern_name;
        return this;
    }

    public String getPattern_description() {
        return pattern_description;
    }

    public Patterns setPattern_description(String pattern_description) {
        this.pattern_description = pattern_description;
        return this;
    }

    public String getPattern_uid() {
        return pattern_uid;
    }

    public Patterns setPattern_uid(String pattern_uid) {
        this.pattern_uid = pattern_uid;
        return this;
    }

    public String getSrc_hash() {
        return src_hash;
    }

    public Patterns setSrc_hash(String src_hash) {
        this.src_hash = src_hash;
        return this;
    }

    public String getEdx_hash() {
        return edx_hash;
    }

    public Patterns setEdx_hash(String edx_hash) {
        this.edx_hash = edx_hash;
        return this;
    }

    public String getDbdts_added() {
        return dbdts_added;
    }

    public Patterns setDbdts_added(String dbdts_added) {
        this.dbdts_added = dbdts_added;
        return this;
    }

    public int getIs_in_use() {
        return is_in_use;
    }

    public Patterns setIs_in_use(int is_in_use) {
        this.is_in_use = is_in_use;
        return this;
    }

    public String getPattern_short_desc() {
        return pattern_short_desc;
    }

    public Patterns setPattern_short_desc(String pattern_short_desc) {
        this.pattern_short_desc = pattern_short_desc;
        return this;
    }

    public String getPattern_source_file_name() {
        return pattern_source_file_name;
    }

    public Patterns setPattern_source_file_name(String pattern_source_file_name) {
        this.pattern_source_file_name = pattern_source_file_name;
        return this;
    }

    public float getPattern_max_playing_time() {
        return pattern_max_playing_time;
    }

    public Patterns setPattern_max_playing_time(int pattern_max_playing_time) {
        this.pattern_max_playing_time = pattern_max_playing_time;
        return this;
    }

    public int getPattern_type() {
        return pattern_type;
    }

    public Patterns setPattern_type(int pattern_type) {
        this.pattern_type = pattern_type;
        return this;
    }

    public int getIs_can_be_reproduced() {
        return is_can_be_reproduced;
    }

    public Patterns setIs_can_be_reproduced(int is_can_be_reproduced) {
        this.is_can_be_reproduced = is_can_be_reproduced;
        return this;
    }

    public String getSmuls() {
        return smuls;
    }

    public Patterns setSmuls(String smuls) {
        this.smuls = smuls;
        return this;
    }

    public String getEdx_file_creation_dts() {
        return edx_file_creation_dts;
    }

    public Patterns setEdx_file_creation_dts(String edx_file_creation_dts) {
        this.edx_file_creation_dts = edx_file_creation_dts;
        return this;
    }

    public int getEdx_file_creation_dts_msecs() {
        return edx_file_creation_dts_msecs;
    }

    public Patterns setEdx_file_creation_dts_msecs(int edx_file_creation_dts_msecs) {
        this.edx_file_creation_dts_msecs = edx_file_creation_dts_msecs;
        return this;
    }

    public String getEdx_file_last_modified_dts() {
        return edx_file_last_modified_dts;
    }

    public Patterns setEdx_file_last_modified_dts(String edx_file_last_modified_dts) {
        this.edx_file_last_modified_dts = edx_file_last_modified_dts;
        return this;
    }

    public int getEdx_file_last_modified_dts_msecs() {
        return edx_file_last_modified_dts_msecs;
    }

    public Patterns setEdx_file_last_modified_dts_msecs(int edx_file_last_modified_dts_msecs) {
        this.edx_file_last_modified_dts_msecs = edx_file_last_modified_dts_msecs;
        return this;
    }

    public int getLinked_folder_id() {
        return linked_folder_id;
    }

    public Patterns setLinked_folder_id(int linked_folder_id) {
        this.linked_folder_id = linked_folder_id;
        return this;
    }
}
