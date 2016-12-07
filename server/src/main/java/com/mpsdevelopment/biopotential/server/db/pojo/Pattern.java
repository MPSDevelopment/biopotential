package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Pattern")
public class Pattern extends BaseObject {
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
    public static final String CHUNK_SUMMARY = "chunkSummary";
    public static final String CHUNK_SUMMARY_GS = "cS";

    /*@ManyToMany(fetch = FetchType.LAZY, mappedBy= "patterns")
    private List<Folder> folders = new ArrayList<>();*/
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pattern")
    private List<PatternsFolders> patternsFolders = new ArrayList<>();

    public Pattern() {

    }

    @Expose
    @Column(name = ID_PATTERN)
    @SerializedName(ID_PATTERN_GS)
    private int idPattern;

    @Expose
    @Column(name = PATTERN_NAME)
    @SerializedName(PATTERN_NAME_GS)
    private String patternName;

    @Expose
    @Column(name = PATTERN_DESCRIPTION, length=6000)
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

    @Expose
    @Column(name = CHUNK_SUMMARY, length = 2000)
    @SerializedName(CHUNK_SUMMARY_GS)
    private String chunkSummary;

    /*public List<Folder> getFolder() {
        return folders;
    }

    public void setFolder(List<Folder> folderses) {
        this.folders = folderses;
    }*/

    public List<PatternsFolders> getPatternsFolders() {
        return patternsFolders;
    }

    public void setPatternsFolders(List<PatternsFolders> patternseFolderses) {
        this.patternsFolders = patternseFolderses;
    }

    public int getIdPattern() {
        return idPattern;
    }

    public Pattern setIdPattern(int idPattern) {
        this.idPattern = idPattern;
        return this;
    }

    public String getPatternName() {
        return patternName;
    }

    public Pattern setPatternName(String patternName) {
        this.patternName = patternName;
        return this;
    }

    public String getPatternDescription() {
        return patternDescription;
    }

    public Pattern setPatternDescription(String patternDescription) {
        this.patternDescription = patternDescription;
        return this;
    }

    public String getPatternUid() {
        return patternUid;
    }

    public Pattern setPatternUid(String patternUid) {
        this.patternUid = patternUid;
        return this;
    }

    public String getSrcHash() {
        return srcHash;
    }

    public Pattern setSrcHash(String srcHash) {
        this.srcHash = srcHash;
        return this;
    }

    public String getEdxHash() {
        return edxHash;
    }

    public Pattern setEdxHash(String edxHash) {
        this.edxHash = edxHash;
        return this;
    }

    public String getDbdtsAdded() {
        return dbdtsAdded;
    }

    public Pattern setDbdtsAdded(String dbdtsAdded) {
        this.dbdtsAdded = dbdtsAdded;
        return this;
    }

    public int getIsInUse() {
        return isInUse;
    }

    public Pattern setIsInUse(int isInUse) {
        this.isInUse = isInUse;
        return this;
    }

    public String getPatternShortDesc() {
        return patternShortDesc;
    }

    public Pattern setPatternShortDesc(String patternShortDesc) {
        this.patternShortDesc = patternShortDesc;
        return this;
    }

    public String getPatternSourceFileName() {
        return patternSourceFileName;
    }

    public Pattern setPatternSourceFileName(String patternSourceFileName) {
        this.patternSourceFileName = patternSourceFileName;
        return this;
    }

    public float getPatternMaxPlayingTime() {
        return patternMaxPlayingTime;
    }

    public Pattern setPatternMaxPlayingTime(float patternMaxPlayingTime) {
        this.patternMaxPlayingTime = patternMaxPlayingTime;
        return this;
    }

    public int getPatternType() {
        return patternType;
    }

    public Pattern setPatternType(int patternType) {
        this.patternType = patternType;
        return this;
    }

    public int getIsCanBeReproduced() {
        return isCanBeReproduced;
    }

    public Pattern setIsCanBeReproduced(int isCanBeReproduced) {
        this.isCanBeReproduced = isCanBeReproduced;
        return this;
    }

    public String getSmuls() {
        return smuls;
    }

    public Pattern setSmuls(String smuls) {
        this.smuls = smuls;
        return this;
    }

    public String getEdxFileCreationDts() {
        return edxFileCreationDts;
    }

    public Pattern setEdxFileCreationDts(String edxFileCreationDts) {
        this.edxFileCreationDts = edxFileCreationDts;
        return this;
    }

    public int getEdxFileCreationDtsMsecs() {
        return edxFileCreationDtsMsecs;
    }

    public Pattern setEdxFileCreationDtsMsecs(int edxFileCreationDtsMsecs) {
        this.edxFileCreationDtsMsecs = edxFileCreationDtsMsecs;
        return this;
    }

    public String getEdxFileLastModifiedDts() {
        return edxFileLastModifiedDts;
    }

    public Pattern setEdxFileLastModifiedDts(String edxFileLastModifiedDts) {
        this.edxFileLastModifiedDts = edxFileLastModifiedDts;
        return this;
    }

    public int getEdxFileLastModifiedDtsMsecs() {
        return edxFileLastModifiedDtsMsecs;
    }

    public Pattern setEdxFileLastModifiedDtsMsecs(int edxFileLastModifiedDtsMsecs) {
        this.edxFileLastModifiedDtsMsecs = edxFileLastModifiedDtsMsecs;
        return this;
    }

    public int getLinkedFolderId() {
        return linkedFolderId;
    }

    public String getChunkSummary() {
        return chunkSummary;
    }

    public Pattern setChunkSummary(String chunkSummary) {
        this.chunkSummary = chunkSummary;
        return this;
    }

    public Pattern setLinkedFolderId(int linkedFolderId) {
        this.linkedFolderId = linkedFolderId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Pattern pattern = (Pattern) o;

        return patternName.equals(pattern.patternName);

    }

    @Override
    public int hashCode() {
        return patternName.hashCode();
    }
}
