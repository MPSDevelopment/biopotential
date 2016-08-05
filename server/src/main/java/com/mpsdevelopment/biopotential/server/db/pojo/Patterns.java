package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Patterns")
public class Patterns extends BaseObject {
    public static final String ID_PATTERN = "idPattern";
    public static final String ID_PATTERN_GS = "i";
    public static final String PATTERN_NAME = "patternName";
    public static final String PATTERN_NAME_GS = "pn";
    public static final String PATTERN_DESCRIPTION = "patternDescription";
    public static final String PATTERN_DESCRIPTION_GS = "pd";
    public static final String PATTERN_UID = "patternUid";
    public static final String PATTERN_UID_GS = "pu";
    public static final String SRC_HASH = "srcHash";
    public static final String SRC_HASH_GS = "sh";
    public static final String EDX_HASH = "edxHash";
    public static final String EDX_HASH_GS = "eh";
    public static final String DATABASE_ADDED = "dbdtsAdded";
    public static final String DATABASE_ADDED_GS = "a";
    public static final String IS_IN_USE = "isInUse";
    public static final String IS_IN_USE_GS = "u";
    public static final String PATTERN_SHORT_DESC = "patternShortDesc";
    public static final String PATTERN_SHORT_DESC_GS = "patDesc";
    public static final String PATTERN_SOURCE_FILE_NAME = "patternSourceFileName";
    public static final String PATTERN_SOURCE_FILE_NAME_GS = "patSourFileName";
    public static final String PATTERN_MAX_PLAYING_TIME = "patternMaxPlayingTime";
    public static final String PATTERN_MAX_PLAYING_TIME_GS = "patMaxPlayTime";
    public static final String PATTERN_TYPE = "patternType";
    public static final String PATTERN_TYPE_GS = "type";
    public static final String IS_CAN_BE_REPRODUCED = "isCanBeReproduced";
    public static final String IS_CAN_BE_REPRODUCED_GS = "isrep";
    public static final String SMULS = "smuls";
    public static final String SMULS_GS = "s";
    public static final String EDX_FILE_CREATION_DTS= "edxFileCreationDts";
    public static final String EDX_FILE_CREATION_DTS_GS = "eFileCre";
    public static final String EDX_FILE_CREATION_DTS_MSECS = "edxFileCreationDtsMsecs";
    public static final String EDX_FILE_CREATION_DTS_MSECS_GS = "eFilCreMs";
    public static final String EDX_FILE_LAST_MODIFIED_DTS= "edxFileLastModifiedDts";
    public static final String EDX_FILE_LAST_MODIFIED_DTS_GS = "eFileLastMod";
    public static final String EDX_FILE_LAST_MODIFIED_DTS_MSEC = "edxFileLastModifiedDtsMsecs";
    public static final String EDX_FILE_LAST_MODIFIED_DTS_MSEC_GS = "eFileLastModMs";
    public static final String LINKED_FOLDER_ID = "linkedFolderId";
    public static final String LINKED_FOLDER_ID_GS = "lFoldId";

    private Set<Folders> folderses = new HashSet<>();

    public Patterns() {

    }
    @Id
    @Expose
    @Column(name = ID_PATTERN)
    @SerializedName(ID_PATTERN_GS)
    private int idPattern;

    @Expose
    @Column(name = PATTERN_NAME)
    @SerializedName(PATTERN_NAME_GS)
    private String patternName;

    @Expose
    @Column(name = PATTERN_DESCRIPTION, length=5000)
    @SerializedName(PATTERN_DESCRIPTION_GS)
    private String patternDescription;

    @Expose
    @Column(name = PATTERN_UID)
    @SerializedName(PATTERN_UID_GS)
    private String patternUid;

    @Expose
    @Column(name = SRC_HASH)
    @SerializedName(SRC_HASH_GS)
    private String srcHash;

    @Expose
    @Column(name = EDX_HASH)
    @SerializedName(EDX_HASH_GS)
    private String edxHash;

    @Expose
    @Column(name = DATABASE_ADDED)
    @SerializedName(DATABASE_ADDED_GS)
    private String dbdtsAdded;

    @Expose
    @Column(name = IS_IN_USE)
    @SerializedName(IS_IN_USE_GS)
    private int isInUse;

    @Expose
    @Column(name = PATTERN_SHORT_DESC)
    @SerializedName(PATTERN_SHORT_DESC_GS)
    private String patternShortDesc;

    @Expose
    @Column(name = PATTERN_SOURCE_FILE_NAME)
    @SerializedName(PATTERN_SOURCE_FILE_NAME_GS)
    private String patternSourceFileName;

    @Expose
    @Column(name = PATTERN_MAX_PLAYING_TIME)
    @SerializedName(PATTERN_MAX_PLAYING_TIME_GS)
    private float patternMaxPlayingTime;

    @Expose
    @Column(name = PATTERN_TYPE)
    @SerializedName(PATTERN_TYPE_GS)
    private int patternType;

    @Expose
    @Column(name = IS_CAN_BE_REPRODUCED)
    @SerializedName(IS_CAN_BE_REPRODUCED_GS)
    private int isCanBeReproduced;

    @Expose
    @Column(name = SMULS)
    @SerializedName(SMULS_GS)
    private String smuls;

    @Expose
    @Column(name = EDX_FILE_CREATION_DTS)
    @SerializedName(EDX_FILE_CREATION_DTS_GS)
    private String edxFileCreationDts;

    @Expose
    @Column(name = EDX_FILE_CREATION_DTS_MSECS)
    @SerializedName(EDX_FILE_CREATION_DTS_MSECS_GS)
    private int edxFileCreationDtsMsecs;

    @Expose
    @Column(name = EDX_FILE_LAST_MODIFIED_DTS)
    @SerializedName(EDX_FILE_LAST_MODIFIED_DTS_GS)
    private String edxFileLastModifiedDts;

    @Expose
    @Column(name = EDX_FILE_LAST_MODIFIED_DTS_MSEC)
    @SerializedName(EDX_FILE_LAST_MODIFIED_DTS_MSEC_GS)
    private int edxFileLastModifiedDtsMsecs;

    @Expose
    @Column(name = LINKED_FOLDER_ID)
    @SerializedName(LINKED_FOLDER_ID_GS)
    private int linkedFolderId;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy="patternses")
    public Set<Folders> getFolderses() {
        return folderses;
    }

    public void setFolderses(Set<Folders> folderses) {
        this.folderses = folderses;
    }

    public int getIdPattern() {
        return idPattern;
    }

    public Patterns setIdPattern(int idPattern) {
        this.idPattern = idPattern;
        return this;
    }

    public String getPatternName() {
        return patternName;
    }

    public Patterns setPatternName(String patternName) {
        this.patternName = patternName;
        return this;
    }

    public String getPatternDescription() {
        return patternDescription;
    }

    public Patterns setPatternDescription(String patternDescription) {
        this.patternDescription = patternDescription;
        return this;
    }

    public String getPatternUid() {
        return patternUid;
    }

    public Patterns setPatternUid(String patternUid) {
        this.patternUid = patternUid;
        return this;
    }

    public String getSrcHash() {
        return srcHash;
    }

    public Patterns setSrcHash(String srcHash) {
        this.srcHash = srcHash;
        return this;
    }

    public String getEdxHash() {
        return edxHash;
    }

    public Patterns setEdxHash(String edxHash) {
        this.edxHash = edxHash;
        return this;
    }

    public String getDbdtsAdded() {
        return dbdtsAdded;
    }

    public Patterns setDbdtsAdded(String dbdtsAdded) {
        this.dbdtsAdded = dbdtsAdded;
        return this;
    }

    public int getIsInUse() {
        return isInUse;
    }

    public Patterns setIsInUse(int isInUse) {
        this.isInUse = isInUse;
        return this;
    }

    public String getPatternShortDesc() {
        return patternShortDesc;
    }

    public Patterns setPatternShortDesc(String patternShortDesc) {
        this.patternShortDesc = patternShortDesc;
        return this;
    }

    public String getPatternSourceFileName() {
        return patternSourceFileName;
    }

    public Patterns setPatternSourceFileName(String patternSourceFileName) {
        this.patternSourceFileName = patternSourceFileName;
        return this;
    }

    public float getPatternMaxPlayingTime() {
        return patternMaxPlayingTime;
    }

    public Patterns setPatternMaxPlayingTime(float patternMaxPlayingTime) {
        this.patternMaxPlayingTime = patternMaxPlayingTime;
        return this;
    }

    public int getPatternType() {
        return patternType;
    }

    public Patterns setPatternType(int patternType) {
        this.patternType = patternType;
        return this;
    }

    public int getIsCanBeReproduced() {
        return isCanBeReproduced;
    }

    public Patterns setIsCanBeReproduced(int isCanBeReproduced) {
        this.isCanBeReproduced = isCanBeReproduced;
        return this;
    }

    public String getSmuls() {
        return smuls;
    }

    public Patterns setSmuls(String smuls) {
        this.smuls = smuls;
        return this;
    }

    public String getEdxFileCreationDts() {
        return edxFileCreationDts;
    }

    public Patterns setEdxFileCreationDts(String edxFileCreationDts) {
        this.edxFileCreationDts = edxFileCreationDts;
        return this;
    }

    public int getEdxFileCreationDtsMsecs() {
        return edxFileCreationDtsMsecs;
    }

    public Patterns setEdxFileCreationDtsMsecs(int edxFileCreationDtsMsecs) {
        this.edxFileCreationDtsMsecs = edxFileCreationDtsMsecs;
        return this;
    }

    public String getEdxFileLastModifiedDts() {
        return edxFileLastModifiedDts;
    }

    public Patterns setEdxFileLastModifiedDts(String edxFileLastModifiedDts) {
        this.edxFileLastModifiedDts = edxFileLastModifiedDts;
        return this;
    }

    public int getEdxFileLastModifiedDtsMsecs() {
        return edxFileLastModifiedDtsMsecs;
    }

    public Patterns setEdxFileLastModifiedDtsMsecs(int edxFileLastModifiedDtsMsecs) {
        this.edxFileLastModifiedDtsMsecs = edxFileLastModifiedDtsMsecs;
        return this;
    }

    public int getLinkedFolderId() {
        return linkedFolderId;
    }

    public Patterns setLinkedFolderId(int linkedFolderId) {
        this.linkedFolderId = linkedFolderId;
        return this;
    }
}
