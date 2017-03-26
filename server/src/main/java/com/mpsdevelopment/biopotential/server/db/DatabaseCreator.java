package com.mpsdevelopment.biopotential.server.db;

import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.PcmDataSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.dao.*;
import com.mpsdevelopment.biopotential.server.db.pojo.*;
import com.mpsdevelopment.biopotential.server.eventbus.EventBus;
import com.mpsdevelopment.biopotential.server.eventbus.event.EnableButtonEvent;
import com.mpsdevelopment.biopotential.server.eventbus.event.ProgressBarEvent;
import com.mpsdevelopment.biopotential.server.gui.ConverterApplication;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Configuration
@ComponentScan(basePackages = {"com.mpsdevelopment.biopotential.server.db"})
public class DatabaseCreator {
    private static final Logger LOGGER = LoggerUtil.getLogger(DatabaseCreator.class);

    public DatabaseCreator() {
        EventBus.subscribe(this);
        LOGGER.info("Create constructor DatabaseCreator");
	}

	public static final String ADMIN_LOGIN = "admin";

	public static final String ADMIN_PASSWORD = "234sgfweyewsgsf";

	public static final String OPERATOR_LOGIN = "operator";

	public static final String OPERATOR_PASSWORD = "234sgfwesgsf";

	public static final String ADMIN = "ADMIN";

	public static final String OPERATOR = "OPERATOR";

	public static final String USER = "USER";

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private FoldersDao foldersDao;

	@Autowired
	private PatternsDao patternsDao;

	@Autowired
	private PatternsFoldersDao patternsFoldersDao;

	private Folder folder;
	private Pattern pattern;
	private PatternsFolders patternsFolders;
	private Connection db;
	private ResultSet foldersDb;
	private ResultSet patternsDb;
	private ResultSet patternsFoldersDb;

	public void initialization() throws IOException, URISyntaxException, DaoException, SQLException {
		userDao = ConverterApplication.APP_CONTEXT.getBean(UserDao.class);
        System.setErr(LoggerUtil.getRedirectedToLoggerErrPrintStream(System.err));
        System.setOut(LoggerUtil.getRedirectedToLoggerOutPrintStream(System.out));
		LOGGER.info("databaseCreator initialization ");
		List<User> users = userDao.findAll();
		if (users.size() < 10) {
			createAllUsers();
			LOGGER.info("databaseCreator created");

			try {
				convertToH2("./data/test.arkdb");
//				convertToH2("./data/db_cutted.db");
			} catch (ArkDBException e) {
				e.printStackTrace();
			}
		}

	}

	private void createAllUsers() {
		createUserIfNotExists(new User().setLogin(ADMIN_LOGIN).setPassword(passwordEncoder.encode(ADMIN_PASSWORD)).setSurname("Медков").setName("Игорь")
				.setPatronymic("Владимирович").setTel("0982359090").setEmail("igor@ukr.net").setGender("Man").setRole(Token.Role.ADMIN.name()));
		createUserIfNotExists(new User().setLogin(OPERATOR_LOGIN).setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Медков").setName("Денис")
				.setPatronymic("Игоревич").setTel("0678940934").setEmail("denis@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("YavorD").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Яворский").setName("Дмитрий")
				.setPatronymic("Константинович").setTel("0675676511").setEmail("yd@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("YavorL").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Яворский").setName("Алексей")
				.setPatronymic("Константинович").setTel("0665940934").setEmail("yl@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("aurusd").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Назаренко").setName("Дмитрий")
				.setPatronymic("Владимирович").setTel("0936578944").setEmail("rebrov@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("Zep").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Цепляев").setName("Игорь").setPatronymic("Игоревич")
				.setTel("0634445566").setEmail("zep@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("Vova").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Вовненко").setName("Сергей").setPatronymic("Сергеевич")
				.setTel("0958899001").setEmail("sv@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("Serg").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Войтюк").setName("Сергей").setPatronymic("Денисович")
				.setTel("0687217645").setEmail("voytyk@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("pete").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Жирков").setName("Петр").setPatronymic("Петрович")
				.setTel("0992164589").setEmail("zhirkov@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("Vita").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Барановский").setName("Виталий")
				.setPatronymic("Витальевич").setTel("066758491").setEmail("server@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("Ivan").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Медяков").setName("Иван").setPatronymic("Григорьевич")
				.setTel("0951237890").setEmail("med@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("sheva").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Шевченко").setName("Андрей")
				.setPatronymic("Николаевич").setTel("0509872674").setEmail("shev@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("Fed").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Федченко").setName("Андрей").setPatronymic("Петрович")
				.setTel("0959872674").setEmail("fed@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("ray").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Рей").setName("Антон").setPatronymic("Федорович")
				.setTel("0506677453").setEmail("ray@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("gudz").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Гудзь").setName("Даниил").setPatronymic("Олегович")
				.setTel("09365489234").setEmail("gudz@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("bal").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Баленко").setName("Алексей").setPatronymic("Алексеевич")
				.setTel("0632587496").setEmail("bal@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("slava").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Черныш").setName("Слава")
				.setPatronymic("Владимирович").setTel("0662589641").setEmail("slava@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("sus").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Ютужанин").setName("Алексей")
				.setPatronymic("Алексеевич").setTel("0507895214").setEmail("yut@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
		createUserIfNotExists(new User().setLogin("shul").setPassword(passwordEncoder.encode(OPERATOR_PASSWORD)).setSurname("Шульга").setName("Ростислав").setPatronymic("Павлович")
				.setTel("09867546124").setEmail("shul@ukr.net").setGender("Man").setRole(Token.Role.OPERATOR.name()));
	}

	public User createUserIfNotExists(User user) {
		User loadedUser = userDao.getByLogin(user.getLogin());
		if (loadedUser == null) {
			LOGGER.info("User %s has been created", user.getLogin());
			loadedUser = userDao.save(user);
		}
		return loadedUser;
	}

	public void connect(String url) throws ArkDBException {
		LOGGER.info("Connect to %s", url);
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new ArkDBException("ClassNotFoundException: " + e.getMessage());
		}

        executeStatement(url);

    }

    private void executeStatement(String url) throws ArkDBException {
        try {
            this.db = DriverManager.getConnection("jdbc:sqlite:" + url);
            this.foldersDb = this.db.createStatement().executeQuery("SELECT * FROM folders");
            this.patternsDb = this.db.createStatement().executeQuery("SELECT * FROM patterns");
            this.patternsFoldersDb = this.db.createStatement().executeQuery("SELECT * FROM link_patterns_to_folders");

        } catch (SQLException e) {
            throw new ArkDBException("SQLException: " + e.getMessage());
        }
    }


    public void convertToH2(String url) throws ArkDBException, IOException, SQLException {
		long t1 = System.currentTimeMillis();
		connect(url);
        double clock = 0;
        double delta = 0;
        int foldersRows =0,  patternsRows = 0, patternsFoldersRows = 0;

        try {
             foldersRows = calcRows(foldersDb);
             patternsRows = calcRows(patternsDb);
             patternsFoldersRows = calcRows(patternsFoldersDb);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        executeStatement(url);
        delta = 0.2/(foldersRows+patternsRows+patternsFoldersRows);
        try {
			while (foldersDb.next()) {

				folder = foldersDao.getById(foldersDb.getInt("id_folder"));
				if (folder == null) {
					folder = new Folder().setIdFolder(foldersDb.getInt("id_folder")).setFolderName(foldersDb.getString("parent_folder_id"))
							.setFolderName(foldersDb.getString("folder_name")).setFolderDescription(foldersDb.getString("folder_description"))
							.setDbdtsAdded(foldersDb.getString("dbdts_added")).setSortPriority(foldersDb.getString("sort_priority")).setIsInUse(foldersDb.getInt("is_in_use"))
							.setFolderType(foldersDb.getString("folder_type"));

					LOGGER.info("foldersDao %s", folder);
					foldersDao.insertNewFolder(folder, true);
//					foldersDao.save(folder);

				}
                clock = delta * foldersDb.getRow();
                EventBus.publishEvent(new ProgressBarEvent(clock));

            }
			while (patternsDb.next()) {

				pattern = patternsDao.getById(patternsDb.getInt("id_pattern"));
				if (pattern == null) {
					pattern = new Pattern().setIdPattern(patternsDb.getInt("id_pattern")).setPatternName(patternsDb.getString("pattern_name"))
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
					patternsDao.insertNewPattern(pattern, true);
//					patternsDao.save(pattern);

//                    clock = clock + delta * patternsDb.getRow();
                    EventBus.publishEvent(new ProgressBarEvent(clock + delta * patternsDb.getRow()));
				}
			}
            clock = clock + delta * patternsRows;
            // patterns to Folders table
			while (patternsFoldersDb.next()) {

				folder = foldersDao.getById(patternsFoldersDb.getInt("id_folder"));
				pattern = patternsDao.getById(patternsFoldersDb.getInt("id_pattern"));

				patternsFolders = new PatternsFolders();
				patternsFolders.setFolder(folder);
				patternsFolders.setPattern(pattern);
				patternsFolders.setCorrectorsEn(null);

//				patternsFoldersDao.save(patternsFolders);
				patternsFoldersDao.insertNewPatternsFolders(patternsFolders, true);
                EventBus.publishEvent(new ProgressBarEvent(clock + delta * patternsFoldersDb.getRow()));

            }
            // patterns to Folders table
            clock = clock + delta * patternsFoldersRows;

			List<Folder> folderList = foldersDao.findAll();
			List<Pattern> patternList = patternsDao.findAll();
			Long bacId = 0L, mucId = 0L, virId = 0L;;
			Long cardioId =0L, dermaId = 0L,endocrinId = 0L,gastroId=0L,immunId=0L,mentisId=0L,neuralId=0L,orthoId=0L,spiritusId=0L,stomatId=0L,urologId=0L,visionId = 0L;
            Long DiId = 0L, BoEnId = 0L,AlId = 0L, DtId = 0L;
            Long HelmEn = 0L, HelmEx = 0L, AlEn = 0L, AlEx = 0L,AcEn = 0L, AcEx = 0L,BaEn = 0L, BaEx = 0L,CaEn = 0L, CaEx = 0L,DeEn = 0L, DeEx = 0L,ElEn = 0L, ElEx = 0L,EnEn = 0L, EnEx = 0L,GaEn = 0L, GaEx = 0L,
                    FeEn = 0L, FeEx = 0L,ImEn = 0L, ImEx = 0L,MaEn = 0L, MaEx = 0L,MyEn = 0L, MyEx = 0L,NeEn = 0L, NeEx = 0L,OrEn = 0L, OrEx = 0L,SpEn = 0L, SpEx = 0L,StEn = 0L, StEx = 0L,
                    UrEn = 0L, UrEx = 0L,DtEn = 0L, DtEx = 0L,FlViEn = 0L, FlViEx = 0L, ViEn = 0L, ViEx = 0L;

            // work with correctors
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
//712ms
                // add new condition
                if (folder.getFolderName().contains("Disrupt")){DiId = folder.getId();}
                if (folder.getFolderName().contains("Bо ex")){BoEnId = folder.getId();}
                if (folder.getFolderName().contains("AL en")){AlId = folder.getId();}
                if (folder.getFolderName().contains("Dt DETOKC")){DtId = folder.getId();}

                //add new condition
                if (folder.getFolderName().contains("He en")){HelmEn = folder.getId();}
                if (folder.getFolderName().contains("He ex")){HelmEx = folder.getId();}

                if (folder.getFolderName().contains("Al en")){AlEn = folder.getId();}
                if (folder.getFolderName().contains("Al ex")){AlEx = folder.getId();}

				if (folder.getFolderName().contains("Ac en")){AcEn = folder.getId();}
				if (folder.getFolderName().contains("Ac ex")){AcEx = folder.getId();}

                if (folder.getFolderName().contains("Ba en")){BaEn = folder.getId();}
                if (folder.getFolderName().contains("Ba ex")){BaEx = folder.getId();}

                if (folder.getFolderName().contains("Ca en")){CaEn = folder.getId();}
                if (folder.getFolderName().contains("Ca ex")){CaEx = folder.getId();}

                if (folder.getFolderName().contains("De en")){DeEn = folder.getId();}
                if (folder.getFolderName().contains("De ex")){DeEx = folder.getId();}

                if (folder.getFolderName().contains("El en")){ElEn = folder.getId();}
                if (folder.getFolderName().contains("El ex")){ElEx = folder.getId();}

                if (folder.getFolderName().contains("En en")){EnEn = folder.getId();}
                if (folder.getFolderName().contains("En ex")){EnEx = folder.getId();}

                if (folder.getFolderName().contains("Ga en")){GaEn = folder.getId();}
                if (folder.getFolderName().contains("Ga ex")){GaEx = folder.getId();}

                if (folder.getFolderName().contains("Fe en")){FeEn = folder.getId();}
                if (folder.getFolderName().contains("Fe ex")){FeEx = folder.getId();}

                if (folder.getFolderName().contains("Im en")){ImEn = folder.getId();}
                if (folder.getFolderName().contains("Im ex")){ImEx = folder.getId();}

                if (folder.getFolderName().contains("Ne en")){NeEn = folder.getId();}
                if (folder.getFolderName().contains("Ne ex")){NeEx = folder.getId();}

                if (folder.getFolderName().contains("Ma en")){MaEn = folder.getId();}
                if (folder.getFolderName().contains("Ma ex")){MaEx = folder.getId();}

                if (folder.getFolderName().contains("My en")){MyEn = folder.getId();}
                if (folder.getFolderName().contains("My ex")){MyEx = folder.getId();}

                if (folder.getFolderName().contains("Or en")){OrEn = folder.getId();}
                if (folder.getFolderName().contains("Or ex")){OrEx = folder.getId();}

                if (folder.getFolderName().contains("Sp en")){SpEn = folder.getId();}
                if (folder.getFolderName().contains("Sp ex")){SpEx = folder.getId();}

                if (folder.getFolderName().contains("St en")){StEn = folder.getId();}
                if (folder.getFolderName().contains("St ex")){StEx = folder.getId();}

                if (folder.getFolderName().contains("Ur en")){UrEn = folder.getId();}
                if (folder.getFolderName().contains("Ur ex")){UrEx = folder.getId();}

                if (folder.getFolderName().contains("Dt ex Fe")){DtEn = folder.getId();}
                if (folder.getFolderName().contains("Dt ex Ma")){DtEx = folder.getId();}

                if (folder.getFolderName().contains("FL Vi en")){FlViEn = folder.getId();}
                if (folder.getFolderName().contains("FL Vi ex")){FlViEx = folder.getId();}

                if (folder.getFolderName().contains("Vi en") && folder.getIsInUse() == 1){ViEn = folder.getId();}
                if (folder.getFolderName().contains("Vi ex") && folder.getIsInUse() == 1){ViEx = folder.getId();}


            }
            long t2 = System.currentTimeMillis();
            //  Folder stressAnalys = foldersDao.getById(4550);
            //  Folder allergy = foldersDao.getById(375);
//            Folder detokc = foldersDao.getById(492);
            Folder floraDissection = foldersDao.getById(4328);
            Folder actually = foldersDao.getById(4616);
            Folder body = foldersDao.getById(4553);

            Folder stressAnalys = foldersDao.getByName("Stress Analyze");
            Folder destruction = foldersDao.getByName("Di Деструкция");
            Folder metabolism = foldersDao.getByName("Me Метаболизм");
            Folder physCond = foldersDao.getByName("Bо Физ кодиции");
            Folder detokc = foldersDao.getByName("Dt DETOKC");
            Folder acariasis = foldersDao.getByName("FL Ac Acariasis");
            Folder bacteria = foldersDao.getByName("FL Ba Bacteria");
            Folder elementary = foldersDao.getByName("FL El Elementary");
            Folder helminths = foldersDao.getByName("FL He Helminths");
            Folder mycosis = foldersDao.getByName("FL My Mycosis");
            Folder virus = foldersDao.getByName("FL Vi Virus");
            Folder femely = foldersDao.getByName("Fe Femely");
            Folder man = foldersDao.getByName("Ma Man");
            Folder allergy = foldersDao.getByName("Al ALLERGY");
            Folder cardio = foldersDao.getByName("Ca CARDIO");
            Folder derma = foldersDao.getByName("De DERMA");
            Folder endocrin = foldersDao.getByName("En ENDOKRIN");
            Folder gastro = foldersDao.getByName("Ga GASTRO");
            Folder immun = foldersDao.getByName("Im IMMUN");
            Folder neural = foldersDao.getByName("Ne NEURAL");
            Folder ortho = foldersDao.getByName("Or ORTHO");
            Folder spiritus = foldersDao.getByName("Sp SPIRITUS");
            Folder stomat = foldersDao.getByName("St STOMAT");
            Folder urolog = foldersDao.getByName("Ur UROLOG");
            Folder vision = foldersDao.getByName("Vi VISION");

            handlePatternsFolders(stressAnalys, null,null);
            handlePatternsFolders(destruction, null, null);
            handlePatternsFolders(metabolism, null, null);
            handlePatternsFolders(physCond, BoEnId, null);
            handlePatternsFolders(detokc, DtEn, DtEx);
            handlePatternsFolders(acariasis, AcEn, AcEx);
            handlePatternsFolders(bacteria, BaEn, BaEx);
            handlePatternsFolders(elementary, ElEn, ElEx);
            handlePatternsFolders(helminths, HelmEn, HelmEx);
            handlePatternsFolders(mycosis, MyEn, MyEx);
            handlePatternsFolders(virus, FlViEn, FlViEx);
            handlePatternsFolders(femely, FeEn, FeEx);
            handlePatternsFolders(man, MaEn, MaEx);

            handlePatternsFolders(allergy, AlEn, AlEx);
            handlePatternsFolders(cardio, CaEn, CaEx);
            handlePatternsFolders(derma, DeEn, DeEx);
            handlePatternsFolders(endocrin, EnEn, EnEx);
            handlePatternsFolders(gastro, GaEn, GaEx);
            handlePatternsFolders(immun, ImEn, ImEx);
            handlePatternsFolders(neural, NeEn, NeEx);
            handlePatternsFolders(ortho, OrEn, OrEx);
            handlePatternsFolders(spiritus, SpEn, SpEx);
            handlePatternsFolders(stomat, SpEn, SpEx);
            handlePatternsFolders(urolog, UrEn, UrEx);
            handlePatternsFolders(vision, ViEn, ViEx);

            delta = 0.1/(patternList.size());


            /*for (Pattern pattern : patternList) {

				patternsFolders = new PatternsFolders();
//				patternsFolders = patternsFoldersDao.getByPattern(pattern);
				*//*if (floraDissection != null) {
					patternsFolders.setFolder(floraDissection);
				}
				if (stressAnalys != null) {
					patternsFolders.setFolder(stressAnalys);
				}
                if (actually != null) {
                    patternsFolders.setFolder(actually);
                }
                if (body != null) {
                    patternsFolders.setFolder(body);
                }
				if (body != null) {
					patternsFolders.setFolder(body);
				}
				if (allergy != null) {
					patternsFolders.setFolder(allergy);
				}
				if (detokc != null) {
					patternsFolders.setFolder(detokc);
				}*//*
//                patternsFolders.setFolder(stressAnalys);
                *//*patternsFolders.setFolder(patternsFolders.getFolder());
				patternsFolders.setPattern(pattern);*//*

				if (pattern.getPatternName().contains("BAC "))
					patternsFolders.setCorrectorsEn(bacId);
				else if (pattern.getPatternName().contains("Muc "))
					patternsFolders.setCorrectorsEn(mucId);
				else if (pattern.getPatternName().contains("VIR "))
					patternsFolders.setCorrectorsEn(virId);
				else if (pattern.getPatternName().contains("CARDIO♥"))
				{patternsFolders.setCorrectorsEn(cardioId);}
				else if (pattern.getPatternName().contains("DERMAლ"))
				{patternsFolders.setCorrectorsEn(dermaId);}
				else if (pattern.getPatternName().contains("Endocrinology♋"))
				{patternsFolders.setCorrectorsEn(endocrinId);}
				else if (pattern.getPatternName().contains("GASTRO⌘"))
				{patternsFolders.setCorrectorsEn(gastroId);}
				else if (pattern.getPatternName().contains("IMMUN☂"))
				{patternsFolders.setCorrectorsEn(immunId);}
				else if (pattern.getPatternName().contains("MENTIS☺"))
				{patternsFolders.setCorrectorsEn(mentisId);}
				else if (pattern.getPatternName().contains("NEURAL♕"))
				{patternsFolders.setCorrectorsEn(neuralId);}
				else if (pattern.getPatternName().contains("ORTHO☤"))
				{patternsFolders.setCorrectorsEn(orthoId);}
				else if (pattern.getPatternName().contains("SPIRITUS✽"))
				{patternsFolders.setCorrectorsEn(spiritusId);}
				else if (pattern.getPatternName().contains("Stomat〲"))
				{patternsFolders.setCorrectorsEn(stomatId);}
				else if (pattern.getPatternName().contains("UROLOGÜ"))
				{patternsFolders.setCorrectorsEn(urologId);}
				else if (pattern.getPatternName().contains("VISION☄"))
				{patternsFolders.setCorrectorsEn(visionId);}
                else if (pattern.getPatternName().contains("ACTUALLY") || pattern.getPatternName().contains("D\\") || pattern.getPatternName().contains("ENERGY"))
                {patternsFolders.setCorrectorsEn(DiId);}
                else if (pattern.getPatternName().contains("Bо"))
                {patternsFolders.setCorrectorsEn(BoEnId);}
				else if (pattern.getPatternName().contains("AL"))
				{patternsFolders.setCorrectorsEn(AlId);}
				else if (pattern.getPatternName().contains("Dt"))
				{patternsFolders.setCorrectorsEn(DtId);}

				patternsFolders.getFolder().getPatternsFolders().add(patternsFolders);
				patternsFolders.getPattern().getPatternsFolders().add(patternsFolders);

				patternsFoldersDao.saveOrUpdate(patternsFolders);

//                clock = clock + delta * patternsFoldersDb.getRow();
                EventBus.publishEvent(new ProgressBarEvent(clock + delta * patternList.indexOf(pattern)));

            }*/
            clock = clock + delta * patternList.size();

            LOGGER.info("patternsFolders took %s ms", System.currentTimeMillis() - t2);
            LOGGER.info("Convert table's took %s ms", System.currentTimeMillis() - t1);
			long t3 = System.currentTimeMillis();
			setChunkSummary();
			LOGGER.info("Chunk summary took %s ms", System.currentTimeMillis() - t3);
            LOGGER.info("Overall convert process took %s ms", System.currentTimeMillis() - t1);
            EventBus.publishEvent(new ProgressBarEvent(1));
            EventBus.publishEvent(new EnableButtonEvent(true, String.valueOf(System.currentTimeMillis() - t1)));
            LOGGER.info("End");

		} catch (SQLException e) {
			LOGGER.printStackTrace(e);
		}

	}

    private void handlePatternsFolders(Folder folder, Long correctorsEn, Long correctorsEx) {
        if (folder != null) {
                List<PatternsFolders> patternsFolderses = patternsFoldersDao.getPatternsByFolder(folder);
                for (PatternsFolders patternFolder : patternsFolderses) {
                /*patternsFolders = new PatternsFolders();
                patternsFolders.setFolder(patternFolder.getFolder());
                patternsFolders.setPattern(patternFolder.getPattern());*/
                    if (correctorsEn == null) {
                        patternFolder.setCorrectorsEn(null);
                    } else {
                        patternFolder.setCorrectorsEn(correctorsEn);
                    }
                    if (correctorsEx == null) {
                        patternFolder.setCorrectorsEx(null);
                    } else {
                        patternFolder.setCorrectorsEx(correctorsEx);
                    }
                    patternsFoldersDao.insertNewPatternsFolders(patternFolder, false);
//                    patternsFoldersDao.saveOrUpdate(patternFolder);

                }
            }
    }

    private int calcRows(ResultSet set) throws SQLException {
        long t1 = System.currentTimeMillis();

        int i = 0;
        while(set.next()) {
            i++;
        }
        LOGGER.info("Table size %s", String.valueOf(i));
        LOGGER.info("%d ms",System.currentTimeMillis() - t1);
        return i;
    }


    /**
	 *
	 * @throws IOException
	 * @throws SQLException
	 * setChunkSummary add to Pattern table column ChunkSummary pre-calculated lis of meandeviation and dispersion
	 */

	private void setChunkSummary() throws IOException, SQLException {
		List<Pattern> patternAll = patternsDao.getPatterns(null, null);
		LOGGER.info("List size %s", patternAll.size());
        double clock = 0.3;
        double delta = 0.7/(patternAll.size());

        for (Pattern patternsum : patternAll) {
			try {
                long t1 = System.currentTimeMillis();
				PcmDataSummary sum = Machine.getPcmData(patternsum.getPatternUid());
				patternsum.setChunkSummary(JsonUtils.getJson(sum.getSummary()));
//				patternsDao.saveOrUpdate(patternsum);
				patternsDao.insertNewPattern(patternsum, false);
                LOGGER.info("Time for get summarize %d ms", System.currentTimeMillis() - t1);

                EventBus.publishEvent(new ProgressBarEvent(clock + delta * patternAll.indexOf(patternsum)));


            } catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void createUsers() {
		createAllUsers();
	}

}
