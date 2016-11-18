package com.mpsdevelopment.biopotential.server.controller;

import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.PcmDataSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.PersistUtils;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsFoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.*;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequestMapping(ControllerAPI.CONVERT_DB)
@Controller
public class FoldersController {

    private static final Logger LOGGER = LoggerUtil.getLogger(FoldersController.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private FoldersDao foldersDao;

    @Autowired
    private PersistUtils persistUtils;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private PatternsDao patternsDao;

    @Autowired
    private PatternsFoldersDao patternsFoldersDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Folder folder;
    private Pattern pattern;
    private PatternsFolders patternsFolders;
    private Connection db;
    private ResultSet foldersDb;
    private ResultSet patternsDb;
    private ResultSet patternsFoldersDb;

    public static final String ADMIN_LOGIN = "admin";
    public static final String ADMIN_PASSWORD = "234sgfweyewsgsf";

    public FoldersController() {
    }

    @RequestMapping(value = "/convertDB", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> getDiseases(@RequestBody String url) {
        persistUtils.closeSessionFactory();
        persistUtils.setConfigurationDatabaseFilename(url);
        SessionFactory sessionFactory = persistUtils.configureSessionFactory();
        Session session = sessionFactory.openSession();
        sessionManager.setSession(session);
        try {
            convertToH2(url);
        } catch (ArkDBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("convert complete ");
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }

    public void connect(String url) throws ArkDBException {
        createUserIfNotExists(new User().setLogin(ADMIN_LOGIN).setPassword(passwordEncoder.encode(ADMIN_PASSWORD)).setSurname("Медков").setName("Игорь")
                .setPatronymic("Владимирович").setTel("0982359090").setEmail("igor@ukr.net").setGender("Man").setRole(Token.Role.ADMIN.name()));
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ArkDBException("ClassNotFoundException: " + e.getMessage());
        }

        try {
            this.db = DriverManager.getConnection("jdbc:sqlite:" + url);
            this.foldersDb = this.db.createStatement().executeQuery("SELECT * FROM folders");
            this.patternsDb = this.db.createStatement().executeQuery("SELECT * FROM patterns");
            this.patternsFoldersDb = this.db.createStatement().executeQuery("SELECT * FROM link_patterns_to_folders");

        } catch (SQLException e) {
            throw new ArkDBException("SQLException: " + e.getMessage());
        }

    }

    public void convertToH2(String url) throws ArkDBException, IOException {
        connect(url);
//        foldersDao = (FoldersDao) JettyServer.APP_CONTEXT.getBean("foldersDao");
        try {
            while (foldersDb.next()) {

                folder = foldersDao.getById(foldersDb.getInt("id_folder"));
                if (folder == null) {
                    folder = new Folder().setIdFolder(foldersDb.getInt("id_folder")).setFolderName(foldersDb.getString("parent_folder_id"))
                            .setFolderName(foldersDb.getString("folder_name")).setFolderDescription(foldersDb.getString("folder_description"))
                            .setDbdtsAdded(foldersDb.getString("dbdts_added")).setSortPriority(foldersDb.getString("sort_priority")).setIsInUse(foldersDb.getInt("is_in_use"))
                            .setFolderType(foldersDb.getString("folder_type"));

                    LOGGER.info("foldersDao %s", folder);
                    foldersDao.save(folder);
                }
            }
//            patternsDao = (PatternsDao) JettyServer.APP_CONTEXT.getBean("patternsDao");
            while (patternsDb.next()) {

                pattern = patternsDao.getById(patternsDb.getInt("id_pattern"));
                if (pattern == null) {
                    pattern = new com.mpsdevelopment.biopotential.server.db.pojo.Pattern().setIdPattern(patternsDb.getInt("id_pattern")).setPatternName(patternsDb.getString("pattern_name"))
                            .setPatternDescription(patternsDb.getString("pattern_description")).setPatternUid(patternsDb.getString("pattern_uid"))
                            .setSrcHash(patternsDb.getString("src_hash")).setEdxHash(patternsDb.getString("edx_hash")).setDbdtsAdded(patternsDb.getString("dbdts_added"))
                            .setIsInUse(patternsDb.getInt("is_in_use")).setPatternShortDesc(patternsDb.getString("pattern_short_desc"))
                            .setPatternSourceFileName(patternsDb.getString("pattern_source_file_name")).setPatternMaxPlayingTime(patternsDb.getFloat("pattern_max_playing_time"))
                            .setPatternType(patternsDb.getInt("pattern_type")).setIsCanBeReproduced(patternsDb.getInt("is_can_be_reproduced"))
                            .setSmuls(patternsDb.getString("smuls")).setEdxFileCreationDts(patternsDb.getString("edx_file_creation_dts"))
                            .setEdxFileCreationDtsMsecs(patternsDb.getInt("edx_file_creation_dts_msecs"))
                            .setEdxFileLastModifiedDts(patternsDb.getString("edx_file_last_modified_dts"))
                            .setEdxFileLastModifiedDtsMsecs(patternsDb.getInt("edx_file_last_modified_dts_msecs"));/*.setLinkedFolderId(patternsDb.getInt("linked_folder_id"));*/

                    LOGGER.info("%s", pattern.getPatternName());
                    patternsDao.save(pattern);
                }
            }
//            patternsFoldersDao = (PatternsFoldersDao) JettyServer.APP_CONTEXT.getBean("patternsFoldersDao");
            while (patternsFoldersDb.next()) {
				/*
				 * Long id_folder = patternsFoldersDb.getLong("id_folder"); LOGGER.info("id_folder %s", id_folder); Long id_pattern = patternsFoldersDb.getLong("id_pattern");
				 * LOGGER.info("id_pattern %s", id_pattern);
				 */

                folder = foldersDao.getById(patternsFoldersDb.getInt("id_folder"));
                pattern = patternsDao.getById(patternsFoldersDb.getInt("id_pattern"));

				/*
				 * folder.getPattern().add(pattern); pattern.getFolder().add(folder);
				 */

                patternsFolders = new PatternsFolders();
                patternsFolders.setFolder(folder);
                patternsFolders.setPattern(pattern);
                patternsFolders.setCorrectors(null);

                patternsFoldersDao.save(patternsFolders);

            }

            List<Folder> folderList = foldersDao.findAll();
            List<Pattern> patternList = patternsDao.findAll();
            Long bacId = 0L;
            Long mucId = 0L;
            Long virId = 0L;
            Long cardioId =0L, dermaId = 0L,endocrinId = 0L,gastroId=0L,immunId=0L,mentisId=0L,neuralId=0L
                    ,orthoId=0L,spiritusId=0L,stomatId=0L,urologId=0L,visionId = 0L;
            for (Folder folder : folderList) {
                if (folder.getFolderName().contains("BAC")){bacId = folder.getId();}
                if (folder.getFolderName().contains("Muc")){mucId = folder.getId();}
                if (folder.getFolderName().contains("VIR")){virId = folder.getId();}

                // add new condition

                if (folder.getFolderName().contains("CARDIO COR")){cardioId = folder.getId();}
                if (folder.getFolderName().contains("DERMA COR")){dermaId = folder.getId();}
                if (folder.getFolderName().contains("ENDOCRIN COR")){endocrinId = folder.getId();}
                if (folder.getFolderName().contains("GASTRO COR")){gastroId = folder.getId();}
                if (folder.getFolderName().contains("IMMUN COR")){immunId = folder.getId();}
                if (folder.getFolderName().contains("MENTIS COR")){mentisId = folder.getId();}

                if (folder.getFolderName().contains("NEURAL COR")){neuralId = folder.getId();}
                if (folder.getFolderName().contains("ORTHO COR")){orthoId = folder.getId();}
                if (folder.getFolderName().contains("SPIRITUS COR")){spiritusId = folder.getId();}
                if (folder.getFolderName().contains("STOMAT COR")){stomatId = folder.getId();}
                if (folder.getFolderName().contains("UROLOG COR")){urologId = folder.getId();}
                if (folder.getFolderName().contains("VISION COR")){visionId = folder.getId();}

            }

            Folder floraDissection = foldersDao.getById(4328);
            Folder analysis = foldersDao.getById(4550);

            for (Pattern pattern : patternList) {

				/*for (Folder folder : folderList) {

					if (folder.getIdFolder() == 4328) {*/

                patternsFolders = new PatternsFolders();
                if (floraDissection != null) {
                    patternsFolders.setFolder(floraDissection);
                }
                if (analysis != null) {
                    patternsFolders.setFolder(analysis);
                }
//                patternsFolders.setFolder(analysis);
                patternsFolders.setPattern(pattern);

                if (pattern.getPatternName().contains("BAC "))
                    patternsFolders.setCorrectors(bacId);
                else if (pattern.getPatternName().contains("Muc "))
                    patternsFolders.setCorrectors(mucId);
                else if (pattern.getPatternName().contains("VIR "))
                    patternsFolders.setCorrectors(virId);
                else if (pattern.getPatternName().contains("CARDIO♥"))
                {patternsFolders.setCorrectors(cardioId);}
                else if (pattern.getPatternName().contains("DERMAლ"))
                {patternsFolders.setCorrectors(dermaId);}
                else if (pattern.getPatternName().contains("Endocrinology♋"))
                {patternsFolders.setCorrectors(endocrinId);}
                else if (pattern.getPatternName().contains("GASTRO⌘"))
                {patternsFolders.setCorrectors(gastroId);}
                else if (pattern.getPatternName().contains("IMMUN☂"))
                {patternsFolders.setCorrectors(immunId);}
                else if (pattern.getPatternName().contains("MENTIS☺"))
                {patternsFolders.setCorrectors(mentisId);}
                else if (pattern.getPatternName().contains("NEURAL♕"))
                {patternsFolders.setCorrectors(neuralId);}
                else if (pattern.getPatternName().contains("ORTHO☤"))
                {patternsFolders.setCorrectors(orthoId);}
                else if (pattern.getPatternName().contains("SPIRITUS✽"))
                {patternsFolders.setCorrectors(spiritusId);}
                else if (pattern.getPatternName().contains("Stomat〲"))
                {patternsFolders.setCorrectors(stomatId);}
                else if (pattern.getPatternName().contains("UROLOGÜ"))
                {patternsFolders.setCorrectors(urologId);}
                else if (pattern.getPatternName().contains("VISION☄"))
                {patternsFolders.setCorrectors(visionId);}

                patternsFolders.getFolder().getPatternsFolders().add(patternsFolders);
                patternsFolders.getPattern().getPatternsFolders().add(patternsFolders);

                patternsFoldersDao.save(patternsFolders);
            }



            setChunkSummary();

            LOGGER.info("End");
			/*
			 * folder = foldersDao.getById(4328); LOGGER.info("Flora dissection size %s", folder.getPattern().size()); folder = foldersDao.getById(2483); LOGGER.info("BAC size %s",
			 * folder.getPattern().size()); folder = foldersDao.getById(959); LOGGER.info("Muc size %s", folder.getPattern().size()); folder = foldersDao.getById(490);
			 * LOGGER.info("VIR size %s", folder.getPattern().size());
			 */

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public User createUserIfNotExists(User user) {
        User loadedUser = userDao.getByLogin(user.getLogin());
        if (loadedUser == null) {
            LOGGER.info("User %s has been created", user.getLogin());
            loadedUser = userDao.save(user);
        }
        return loadedUser;
    }

    private void setChunkSummary() throws IOException, SQLException {
        List<Pattern> patternAll = patternsDao.getPatterns(null, null);
        LOGGER.info("List size %s", patternAll.size());

//		patternAll.forEach(new Consumer<Pattern>() {
//			@Override
//			public void accept(Pattern patternsum) {
//
//				try {
//					PcmDataSummary sum = Machine.getPcmData(patternsum.getPatternUid());
//					patternsum.setChunkSummary(JsonUtils.getJson(sum.getSummary()));
//					patternsDao.saveOrUpdate(patternsum);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//
//		});

        for (Pattern patternsum : patternAll) {
            try {
                PcmDataSummary sum = Machine.getPcmData(patternsum.getPatternUid());
                patternsum.setChunkSummary(JsonUtils.getJson(sum.getSummary()));
                patternsDao.saveOrUpdate(patternsum);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

