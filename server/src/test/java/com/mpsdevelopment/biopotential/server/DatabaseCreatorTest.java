package com.mpsdevelopment.biopotential.server;

import com.mpsdevelopment.biopotential.server.cmp.machine.dbs.arkdb.ArkDBException;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.dao.FoldersDao;
import com.mpsdevelopment.biopotential.server.db.dao.PatternsDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class DatabaseCreatorTest {

	private static final Logger LOGGER = LoggerUtil.getLogger(DatabaseCreatorTest.class);

	@Autowired
	public DatabaseCreator databaseCreator;

	@Autowired
	public FoldersDao foldersDao;
	@Autowired
	private PatternsDao patternsDao;

	@Test
	public void convertToH2Test() throws ArkDBException, IOException, SQLException {

		LOGGER.info("databaseCreator %s", databaseCreator);

		try {
			databaseCreator.convertToH2("test.arkdb");
		} catch (ArkDBException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(5, foldersDao.findAll().size());
		Assert.assertEquals(856, patternsDao.findAll().size());

		Folder folder = foldersDao.getById(4328);
		/*List<Pattern> patternses = folder.getPattern();

		for (Pattern patterns : patternses) {
			LOGGER.info("patterns %s", patterns);
		}*/
	}

	@Test
	public void createStorage() {
		try {
			databaseCreator.connect("test.arkdb");
		} catch (ArkDBException e) {
			LOGGER.printStackTrace(e);
		}

		try {
			databaseCreator.createStorage();
		} catch (SQLException | IOException e) {
			LOGGER.printStackTrace(e);
		}
	}



}
