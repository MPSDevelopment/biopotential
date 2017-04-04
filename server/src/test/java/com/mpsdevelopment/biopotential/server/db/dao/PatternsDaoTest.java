package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.cmp.machine.Machine;
import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class PatternsDaoTest {

	private static final Logger LOGGER = LoggerUtil.getLogger(PatternsDaoTest.class);

	@Autowired
	private PatternsDao patternsDao;

	@Test
	public void getPatternsTest() {

		List<Pattern> list = patternsDao.getPatterns(1);
		Assert.assertEquals(605, list.size());
		LOGGER.info("List size %s ", list.size());

		list = patternsDao.getPatterns(0);
		Assert.assertEquals(856, list.size());
		LOGGER.info("List size %s ", list.size());

	}

	@Test
	public void getPatternsIsCanBeReproducedTest() {
		List<EDXPattern> list = null;
		try {
			list = patternsDao.getPatternsIsCanBeReproduced(1);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(603, list.size());

		LOGGER.info("getAllPatternsFromDataBaseTest");
	}

	@Test
	public void getPatternsWhereCorrectorsNotNull() {

		patternsDao.findAll();
		
		List<EDXPattern> list = null;
		try {
			list = patternsDao.getPatternsWhereCorrectorsNotNull(1L);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

	}

}
