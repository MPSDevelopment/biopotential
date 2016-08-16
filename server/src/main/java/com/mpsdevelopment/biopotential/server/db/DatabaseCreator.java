package com.mpsdevelopment.biopotential.server.db;

import com.mps.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folders;
import com.mpsdevelopment.biopotential.server.db.pojo.Patterns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mpsdevelopment.biopotential.server.db.dao.DaoException;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Role;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DatabaseCreator {
	
	private static final Logger LOGGER = LoggerUtil.getLogger(DatabaseCreator.class);

	public static final String ADMIN_LOGIN = "admin";
	
	public static final String ADMIN_PASSWORD = "234sgfweyewsgsf";

    public static final String OPERATOR_LOGIN = "operator";

    public static final String OPERATOR_PASSWORD = "234sgfwesgsf";

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private FoldersDao foldersDao;

	@Autowired
    private PatternsDao patternsDao;

    private Folders folders;
    private Patterns patterns;
    Connection db;
    ResultSet foldersDb;
    ResultSet patternsDb;
    ResultSet patternsFolders;

    public void initialization() throws IOException, URISyntaxException, DaoException {
        createUserIfNotExists(new User().setLogin(ADMIN_LOGIN).setPassword(passwordEncoder.encode(ADMIN_PASSWORD)).setRole(Role.ADMIN.name()));
        createUserIfNotExists(new User().setLogin(OPERATOR_LOGIN).setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setRole(Role.OPERATOR.name()));
    }

    public User createUserIfNotExists(User user) {
        User loadedUser = userDao.getByLogin(user.getLogin());
        if (loadedUser == null) {
            LOGGER.info("User %s has been created", user.getLogin());
            loadedUser = userDao.save(user);
        }
        return loadedUser;
    }

    public void connect (String url) throws ArkDBException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ArkDBException(
                    "ClassNotFoundException: " + e.getMessage());
        }

        try {
            this.db = DriverManager.getConnection("jdbc:sqlite:" + url);
            this.foldersDb = this.db.createStatement().executeQuery(
                    "SELECT * FROM folders");
            this.patternsDb = this.db.createStatement().executeQuery(
                    "SELECT * FROM patterns");
            this.patternsFolders = this.db.createStatement().executeQuery(
                    "SELECT * FROM link_patterns_to_folders");

        } catch (SQLException e) {
            throw new ArkDBException("SQLException: " + e.getMessage());
        }

    }

    public void convertToH2(String url) throws ArkDBException {
        connect(url);

        try {
            while(foldersDb.next()) {

                folders = foldersDao.getById(foldersDb.getInt("id_folder"));
                if (folders == null) {
                    folders = (Folders) new Folders().setIdFolder(foldersDb.getInt("id_folder")).setFolderName(foldersDb.getString("parent_folder_id")).setFolderName(foldersDb.getString("folder_name")).
                            setFolderDescription(foldersDb.getString("folder_description")).setDbdtsAdded(foldersDb.getString("dbdts_added")).setSortPriority(foldersDb.getString("sort_priority")).
                            setIsInUse(foldersDb.getInt("is_in_use")).setFolderType(foldersDb.getString("folder_type"));

                    LOGGER.info("id_folder %s", foldersDb.getInt("id_folder"));
                    LOGGER.info("foldersDao %s", folders);
                    foldersDao.save(folders);
                }
            }

            while (patternsDb.next()){

                patterns = patternsDao.getById(patternsDb.getInt("id_pattern"));
                if (patterns == null) {
                    patterns = (Patterns) new Patterns().setIdPattern(patternsDb.getInt("id_pattern")).setPatternName(patternsDb.getString("pattern_name")).setPatternDescription(patternsDb.getString("pattern_description")).
                            setPatternUid(patternsDb.getString("pattern_uid")).setSrcHash(patternsDb.getString("src_hash")).setEdxHash(patternsDb.getString("edx_hash")).setDbdtsAdded(patternsDb.getString("dbdts_added")).
                            setIsInUse(patternsDb.getInt("is_in_use")).setPatternShortDesc(patternsDb.getString("pattern_short_desc")).setPatternSourceFileName(patternsDb.getString("pattern_source_file_name")).
                            setPatternMaxPlayingTime(patternsDb.getFloat("pattern_max_playing_time")).setPatternType(patternsDb.getInt("pattern_type")).setIsCanBeReproduced(patternsDb.getInt("is_can_be_reproduced")).
                            setSmuls(patternsDb.getString("smuls")).setEdxFileCreationDts(patternsDb.getString("edx_file_creation_dts")).setEdxFileCreationDtsMsecs(patternsDb.getInt("edx_file_creation_dts_msecs")).
                            setEdxFileLastModifiedDts(patternsDb.getString("edx_file_last_modified_dts")).setEdxFileLastModifiedDtsMsecs(patternsDb.getInt("edx_file_last_modified_dts_msecs"))
                            .setLinkedFolderId(patternsDb.getInt("linked_folder_id"));

                    LOGGER.info("patternsDao %s", patterns);
                    patternsDao.save(patterns);
                }
            }

            while (patternsFolders.next()){
                Long id_folder = patternsFolders.getLong("id_folder");
                LOGGER.info("id_folder %s", id_folder);
                Long id_pattern = patternsFolders.getLong("id_pattern");
                LOGGER.info("id_pattern %s", id_pattern);

                Folders folders = foldersDao.getById(patternsFolders.getInt("id_folder"));
                Patterns patterns = patternsDao.getById(patternsFolders.getInt("id_pattern"));

//                folders = foldersDao.load(patternsFolders.getLong("id_folder"));
//                Patterns patterns = patternsDao.load(patternsFolders.getLong("id_pattern"));
                folders.getPatternses().add(patterns);
                patterns.getFolderses().add(folders);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
