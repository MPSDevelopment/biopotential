package com.mpsdevelopment.biopotential.server.db;

import com.mps.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folders;
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

public class DatabaseCreator {
	
	private static final Logger LOGGER = LoggerUtil.getLogger(DatabaseCreator.class);

	public static final String ADMIN_LOGIN = "admin";
	
	public static final String ADMIN_PASSWORD = "234sgfweyewsgsf";

	@Autowired
	private UserDao userDao;	
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private FoldersDao foldersDao;

	private Folders folders;
	Connection db;
	ResultSet resultSet;
	

	public void initialization() throws IOException, URISyntaxException, DaoException {
		createUserIfNotExists(new User().setLogin(ADMIN_LOGIN).setPassword(passwordEncoder.encode(ADMIN_PASSWORD)).setRole(Role.ADMIN.name()));
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
			this.resultSet = this.db.createStatement().executeQuery(
					"SELECT * FROM folders");


		} catch (SQLException e) {
			throw new ArkDBException("SQLException: " + e.getMessage());
		}

		return resultSet;
	}

	public void convertToH2(String url) throws ArkDBException {
		resultSet = connect(url);
//		FoldersDao foldersDao = new FoldersDao();
		try {
			while(resultSet.next()) {
				folders = (Folders) new Folders().setId_folder(resultSet.getInt("id_folder")).setFolder_name(resultSet.getString("parent_folder_id")).setFolder_name(resultSet.getString("folder_name")).
						setFolder_description(resultSet.getString("folder_description")).setDbdts_added(resultSet.getString("dbdts_added")).setSort_priority(resultSet.getString("sort_priority")).
						setIs_in_use(resultSet.getInt("folder_description")).setFolder_type(resultSet.getString("folder_type"));

				LOGGER.info("foldersDao %s", folders);
				foldersDao.save(folders);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
