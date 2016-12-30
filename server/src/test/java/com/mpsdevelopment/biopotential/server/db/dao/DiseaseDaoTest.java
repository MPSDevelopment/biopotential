package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.cmp.analyzer.AnalysisSummary;
import com.mpsdevelopment.biopotential.server.cmp.analyzer.TestSummary;
import com.mpsdevelopment.biopotential.server.cmp.machine.Pattern;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class DiseaseDaoTest {

	@Autowired
	private DiseaseDao diseaseDao;

	private static final Logger LOGGER = LoggerUtil.getLogger(DiseaseDaoTest.class);
	Map<Pattern, AnalysisSummary> allHealings;
	Map<Pattern, AnalysisSummary> diseases;
	private File file;

	public DiseaseDaoTest() {
		diseases = new HashMap<>();
		allHealings = new HashMap<>();
		file = new File("files/test3.wav");
	}

	@Test
	public void getHealingsTest() throws IOException, UnsupportedAudioFileException {
		try {
			diseases = diseaseDao.getDeseases(file,0,"corNotNull", "M");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		long t1 = System.currentTimeMillis();
		Map<Pattern, AnalysisSummary> healings = diseaseDao.getHealings(diseases, file,0);
		LOGGER.info("Total time to get calculate healings %d ms", System.currentTimeMillis() - t1);

		Set<Pattern> keys = new HashSet<>(healings.keySet());
		List<TestSummary> testSummaries = new ArrayList<>();

		for (Pattern temp : keys) {
			testSummaries.add(new TestSummary(temp.getKind(), temp.getName(), temp.getDescription(), healings.get(temp).getDispersion()));
		}

		testSummaries.sort(new Comparator<TestSummary>() {
			@Override
			public int compare(TestSummary o1, TestSummary o2) {
				return o2.getDispersion().toString().compareTo(o1.getDispersion().toString());
			}
		});

//		Assert.assertEquals(105, healings.size()); // with duplicate patterns filename
		Assert.assertEquals(104, healings.size()); // without duplicate patterns filename

		Assert.assertEquals(0.9428119761134071, testSummaries.get(0).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9403581859325427, testSummaries.get(1).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9402918941295554, testSummaries.get(2).getDispersion(), 0.000000001);
		Assert.assertEquals(0.940230391850338, testSummaries.get(3).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9401602749365161, testSummaries.get(4).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9400048256478736, testSummaries.get(5).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9396519675275893, testSummaries.get(6).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9395446663374385, testSummaries.get(7).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9394064078893043, testSummaries.get(8).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9391722653463764, testSummaries.get(9).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9380269027016215, testSummaries.get(10).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9374881081074107, testSummaries.get(11).getDispersion(), 0.000000001);
		Assert.assertEquals(0.93712043818981, testSummaries.get(12).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9369130363249805, testSummaries.get(13).getDispersion(), 0.000000001);
		Assert.assertEquals(0.9367212979067614, testSummaries.get(14).getDispersion(), 0.000000001);

	}

}
