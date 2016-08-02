package com.mpsdevelopment.biopotential.server.db;

import com.mps.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	private Folders folders;
	private Patterns patterns;
	Connection db;
	ResultSet folders_db;
	ResultSet patterns_db;


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

	public ResultSet connect (String url) throws ArkDBException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new ArkDBException(
					"ClassNotFoundException: " + e.getMessage());
		}

		try {
			this.db = DriverManager.getConnection("jdbc:sqlite:" + url);
			this.folders_db = this.db.createStatement().executeQuery(
					"SELECT * FROM folders");
			patterns_db = db.createStatement().executeQuery(
					"SELECT * FROM folders");

		} catch (SQLException e) {
			throw new ArkDBException("SQLException: " + e.getMessage());
		}

		return folders_db;
	}

	public void convertToH2(String url) throws ArkDBException {
		folders_db = connect(url);
		try {
			while(folders_db.next()) {
				folders = (Folders) new Folders().setId_folder(folders_db.getInt("id_folder")).setFolder_name(folders_db.getString("parent_folder_id")).setFolder_name(folders_db.getString("folder_name")).
						setFolder_description(folders_db.getString("folder_description")).setDbdts_added(folders_db.getString("dbdts_added")).setSort_priority(folders_db.getString("sort_priority")).
						setIs_in_use(folders_db.getInt("folder_description")).setFolder_type(folders_db.getString("folder_type"));

				LOGGER.info("foldersDao %s", folders);
				foldersDao.save(folders);
			}

			while (patterns_db.next()){
				patterns = (Patterns) new Patterns().setId_pattern(patterns_db.getInt("id_pattern")).setPattern_name(patterns_db.getString("pattern_name")).setPattern_description(patterns_db.getString("pattern_description")).
						setPattern_uid(patterns_db.getString("pattern_uid")).setSrc_hash(patterns_db.getString("src_hash")).setEdx_hash(patterns_db.getString("edx_hash")).setDbdts_added(patterns_db.getString("dbdts_added"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
