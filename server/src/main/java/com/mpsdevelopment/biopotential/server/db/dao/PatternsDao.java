package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.PersistUtils;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.PatternsFolders;
import com.mpsdevelopment.plasticine.commons.IdGenerator;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.hibernate.*;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PatternsDao extends GenericDao<Pattern, Long> {

	private static final Logger LOGGER = LoggerUtil.getLogger(PatternsDao.class);

	@Autowired
	private PersistUtils persistUtils;

	@Autowired
	private SessionManager sessionManager;

	public Pattern getById(int value) {
		Criteria query = getSession().createCriteria(Pattern.class).setCacheable(false);
		query.add(Restrictions.eq(Pattern.ID_PATTERN, value));
		return (Pattern) query.uniqueResult();
	}

	public List<Pattern> getPatterns(Integer pageSize, Integer pageNumber) {
		Criteria query = getSession().createCriteria(Pattern.class).setCacheable(false);
		if (pageSize != null && pageNumber != null) {
			query.setFirstResult(pageNumber * pageSize);
		}
		if (pageSize != null) {
			query.setMaxResults(pageSize);
		}
		return query.list();
	}

	public List<Pattern> getPatterns(int value) {
		Criteria query = getSession().createCriteria(Pattern.class).setCacheable(false);
		query.add(Restrictions.eq(Pattern.IS_CAN_BE_REPRODUCED, value));
		return query.list();
	}

	public void insertNewPattern(Pattern pattern, boolean isNewObject) {

		Query query;
		if (isNewObject) {
			query = getSession().createSQLQuery("INSERT INTO MAIN.PATTERN (id,isJustCreated, idPattern, patternName, patternDescription, patternUid"
					+ ", srcHash, edxHash, dbdtsAdded, isInUse, patternShortDesc, patternSourceFileName, patternMaxPlayingTime, patternType, isCanBeReproduced "
					+ ", smuls, edxFileCreationDts, edxFileCreationDtsMsecs, edxFileLastModifiedDts, edxFileLastModifiedDtsMsecs, linkedFolderId, chunkSummary) "
					+ "VALUES(:id, true, :idPattern, :patternName, :patternDescription, :patternUid, :srcHash, :edxHash, :dbdtsAdded, :isInUse, :patternShortDesc"
					+ ", :patternSourceFileName, :patternMaxPlayingTime, :patternType, :isCanBeReproduced, :smuls, :edxFileCreationDts, :edxFileCreationDtsMsecs"
					+ ", :edxFileLastModifiedDts, :edxFileLastModifiedDtsMsecs, :linkedFolderId, :chunkSummary)");
			long idx = IdGenerator.nextId();
			query.setParameter("id", idx);
		} else {
			query = getSession()
					.createSQLQuery("UPDATE MAIN.PATTERN SET idPattern = :idPattern, patternName = :patternName, patternDescription = :patternDescription, patternUid = :patternUid"
							+ ", srcHash = :srcHash, edxHash = :edxHash, dbdtsAdded = :dbdtsAdded, isInUse = :isInUse, patternShortDesc = :patternShortDesc, patternSourceFileName = :patternSourceFileName"
							+ ", patternMaxPlayingTime = :patternMaxPlayingTime, patternType = :patternType, isCanBeReproduced = :isCanBeReproduced, smuls = :smuls"
							+ ", edxFileCreationDts = :edxFileCreationDts, edxFileCreationDtsMsecs = :edxFileCreationDtsMsecs, edxFileLastModifiedDts = :edxFileLastModifiedDts"
							+ ", edxFileLastModifiedDtsMsecs = :edxFileLastModifiedDtsMsecs, linkedFolderId = :linkedFolderId, chunkSummary = :chunkSummary WHERE id = :id");
			query.setParameter("id", pattern.getId());
		}
		query.setParameter("idPattern", pattern.getIdPattern());
		query.setParameter("patternName", pattern.getPatternName());
		query.setParameter("patternDescription", pattern.getPatternDescription());
		query.setParameter("patternUid", pattern.getPatternUid());
		query.setParameter("srcHash", pattern.getSrcHash());
		query.setParameter("edxHash", pattern.getEdxHash());
		query.setParameter("dbdtsAdded", pattern.getDbdtsAdded());
		query.setParameter("isInUse", pattern.getIsInUse());
		query.setParameter("patternShortDesc", pattern.getPatternShortDesc());
		query.setParameter("patternSourceFileName", pattern.getPatternSourceFileName());
		query.setParameter("patternMaxPlayingTime", pattern.getPatternMaxPlayingTime());
		query.setParameter("patternType", pattern.getPatternType());
		query.setParameter("isCanBeReproduced", pattern.getIsCanBeReproduced());
		query.setParameter("smuls", pattern.getSmuls());
		query.setParameter("edxFileCreationDts", pattern.getEdxFileCreationDts());
		query.setParameter("edxFileCreationDtsMsecs", pattern.getEdxFileCreationDtsMsecs());
		query.setParameter("edxFileLastModifiedDts", pattern.getEdxFileLastModifiedDts());
		query.setParameter("edxFileLastModifiedDtsMsecs", pattern.getEdxFileCreationDtsMsecs());
		query.setParameter("linkedFolderId", pattern.getLinkedFolderId());
		query.setParameter("chunkSummary", pattern.getChunkSummary());
		query.executeUpdate();
	}

	public List<EDXPattern> getPatternsIsCanBeReproduced(int value) throws SQLException, IOException {

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.distinct(Projections.property("PATTERN." + Pattern.PATTERN_UID)), "fileName");
//		projections.add(Projections.property("FOLDER." + Folder.FOLDER_NAME), "kind");
		projections.add(Projections.property("PATTERN." + Pattern.PATTERN_NAME), "name");
		projections.add(Projections.property("PATTERN." + Pattern.PATTERN_DESCRIPTION), "description");
		projections.add(Projections.property("PATTERN." + Pattern.CHUNK_SUMMARY), "summary");
		projections.add(Projections.property("PATTERN." + Pattern.IS_CAN_BE_REPRODUCED), "isCanBeReproduced");

		List list = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS, "PATTERNS_FOLDERS")
				.createCriteria(PatternsFolders.PATTERNS, "PATTERN").setProjection(projections).add(Restrictions.eq("PATTERN." + Pattern.IS_CAN_BE_REPRODUCED, value))
				.setResultTransformer(Transformers.aliasToBean(EDXPattern.class)).list();

		return list;
	}

	public List<EDXPattern> getPatternsWhereCorrectorsNotNull() throws SQLException, IOException {

		long t1 = System.currentTimeMillis();

		ProjectionList projections = Projections.projectionList().add(Projections.property("FOLDER." + Folder.FOLDER_NAME), "kind")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_NAME), "name").add(Projections.property("PATTERN." + Pattern.PATTERN_DESCRIPTION), "description")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_UID), "fileName").add(Projections.property("PATTERN." + Pattern.CHUNK_SUMMARY), "summary")
				.add(Projections.property("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN), "correctingFolderEn")
				.add(Projections.property("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EX), "correctingFolderEx");

		// TODO remove and fix this shit ------------------
		if (getSession().getSessionFactory().isClosed()) {
			while (getSession().getSessionFactory().isClosed()) {
				persistUtils.closeSessionFactory();
				// persistUtils.setConfigurationDatabaseFilename(name);
				SessionFactory sessionFactory = persistUtils.configureSessionFactory();
				Session session = sessionFactory.openSession();
				sessionManager.setSession(session);
			}
		}
		// TODO remove and fix this shit ------------------

		List list = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS, "PATTERNS_FOLDERS")
				.createCriteria(PatternsFolders.PATTERNS, "PATTERN").add(Restrictions.isNotNull("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN)).setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(EDXPattern.class)).list();

		// LOGGER.info("Work with iterator took %d ms Result set is %d ",
		// System.currentTimeMillis() - t1, list.size());

		return list;

		/**
		 * PreparedStatement ps = db.prepareStatement( "SELECT IDFOLDER," + "
		 * IDPATTERN," + " PATTERNNAME," + " PATTERNDESCRIPTION," + "
		 * PATTERNUID," + " FOLDERNAME," + " FOLDER_ID," + " CORRECTORS_EN," + "
		 * PATTERN_ID\n" + "FROM MAIN.FOLDER\n" + "JOIN MAIN.PATTERNS_FOLDERS" +
		 * " ON FOLDER.ID = PATTERNS_FOLDERS.FOLDER_ID\n" + "JOIN MAIN.PATTERN"
		 * + " ON PATTERN.ID = PATTERNS_FOLDERS.PATTERN_ID\n" + "WHERE
		 * CORRECTORS_EN IS NOT NULL");
		 * 
		 * List<EDXPattern> patterns = new ArrayList<>(); ResultSet rs =
		 * ps.executeQuery(); while(rs.next()){ patterns.add(new EDXPattern(
		 * rs.getString("FOLDERNAME"), rs.getString("PATTERNNAME"),
		 * rs.getString("PATTERNDESCRIPTION"), "./data/edxfiles/" +
		 * rs.getString("PATTERNUID"), rs.getString("CORRECTORS_EN"))); } return
		 * patterns;
		 **/
	}

	public List<EDXPattern> getPatternsWhereCorrectorsNotNull(Long filter) throws SQLException, IOException {

		long t1 = System.currentTimeMillis();

		ProjectionList projections = Projections.projectionList().add(Projections.property("FOLDER." + Folder.FOLDER_NAME), "kind")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_NAME), "name").add(Projections.property("PATTERN." + Pattern.PATTERN_DESCRIPTION), "description")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_UID), "fileName").add(Projections.property("PATTERN." + Pattern.CHUNK_SUMMARY), "summary")
				.add(Projections.property("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN), "correctingFolderEn");

		Criteria query = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS, "PATTERNS_FOLDERS")
				.createCriteria(PatternsFolders.PATTERNS, "PATTERN").add(Restrictions.eq("FOLDER." + Folder.ID_FIELD, filter)).setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(EDXPattern.class));

		List list = query.list();

		// LOGGER.info("Work with iterator and filter %s took %d ms Result set
		// is %d ", filter, System.currentTimeMillis() - t1, list.size());

		return list;

		/**
		 * PreparedStatement ps = db.prepareStatement( "SELECT IDFOLDER," + "
		 * IDPATTERN," + " PATTERNNAME," + " PATTERNDESCRIPTION," + "
		 * PATTERNUID," + " FOLDERNAME," + " FOLDER_ID," + " CORRECTORS_EN," + "
		 * PATTERN_ID\n" + "FROM MAIN.FOLDER\n" + "JOIN MAIN.PATTERNS_FOLDERS" +
		 * " ON FOLDER.ID = PATTERNS_FOLDERS.FOLDER_ID\n" + "JOIN MAIN.PATTERN"
		 * + " ON PATTERN.ID = PATTERNS_FOLDERS.PATTERN_ID\n" + "WHERE FOLDER_ID
		 * = ?"); ps.setString(1, filter);
		 * 
		 * List<EDXPattern> patterns = new ArrayList<>(); ResultSet rs =
		 * ps.executeQuery(); rs.setFetchSize(1000);
		 * 
		 * long t1 = System.currentTimeMillis();
		 * 
		 * while(rs.next()){ patterns.add(new EDXPattern(
		 * rs.getString("FOLDERNAME"), rs.getString("PATTERNNAME"),
		 * rs.getString("PATTERNDESCRIPTION"), "./data/edxfiles/" +
		 * rs.getString("PATTERNUID"), rs.getString("CORRECTORS_EN"))); }
		 * 
		 * LOGGER.info("Work with iterator took %d ms Result set is %d ",
		 * System.currentTimeMillis() - t1, patterns.size());
		 * 
		 * return patterns;
		 * 
		 **/
	}

	public List<EDXPattern> getPatternsWhereCorrectorsNotNull(int i) throws SQLException, IOException {

		long t1 = System.currentTimeMillis();

		ProjectionList projections = Projections.projectionList().add(Projections.property("FOLDER." + Folder.FOLDER_NAME), "kind")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_NAME), "name").add(Projections.property("PATTERN." + Pattern.PATTERN_DESCRIPTION), "description")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_UID), "fileName").add(Projections.property("PATTERN." + Pattern.CHUNK_SUMMARY), "summary")
				.add(Projections.property("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN), "correctingFolderEn");

		List list = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS, "PATTERNS_FOLDERS")
				.createCriteria(PatternsFolders.PATTERNS, "PATTERN").add(Restrictions.isNull("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN)).setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(EDXPattern.class)).list();

		// LOGGER.info("Work with iterator took %d ms Result set is %d ",
		// System.currentTimeMillis() - t1, list.size());

		return list;
	}

	public List<EDXPattern> getPatternsFromFoldersCorIsNull(Folder folder) throws SQLException, IOException {

		long t1 = System.currentTimeMillis();

		ProjectionList projections = Projections.projectionList().add(Projections.property("FOLDER." + Folder.FOLDER_NAME), "kind")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_NAME), "name").add(Projections.property("PATTERN." + Pattern.PATTERN_DESCRIPTION), "description")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_UID), "fileName").add(Projections.property("PATTERN." + Pattern.CHUNK_SUMMARY), "summary")
				.add(Projections.property("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN), "correctingFolderEn")
				.add(Projections.property("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EX), "correctingFolderEx");

		List list = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS, "PATTERNS_FOLDERS")
				.createCriteria(PatternsFolders.PATTERNS, "PATTERN").add(Restrictions.isNull("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN))
				.add(Restrictions.eq("PATTERNS_FOLDERS." + PatternsFolders.FOLDER, folder)).setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(EDXPattern.class)).list();

		// LOGGER.info("Work with iterator took %d ms Result set is %d ",
		// System.currentTimeMillis() - t1, list.size());

		return list;
	}

	public List<EDXPattern> getPatternsFromFoldersCorNotNull(Folder folder) throws SQLException, IOException {

		long t1 = System.currentTimeMillis();

		ProjectionList projections = Projections.projectionList().add(Projections.property("FOLDER." + Folder.FOLDER_NAME), "kind")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_NAME), "name").add(Projections.property("PATTERN." + Pattern.PATTERN_DESCRIPTION), "description")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_UID), "fileName").add(Projections.property("PATTERN." + Pattern.CHUNK_SUMMARY), "summary")
				.add(Projections.property("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN), "correctingFolderEn")
				.add(Projections.property("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EX), "correctingFolderEx");

		List list = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS, "PATTERNS_FOLDERS")
				.createCriteria(PatternsFolders.PATTERNS, "PATTERN").add(Restrictions.isNotNull("PATTERNS_FOLDERS." + PatternsFolders.CORRECTORS_EN))
				.add(Restrictions.eq("PATTERNS_FOLDERS." + PatternsFolders.FOLDER, folder)).setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(EDXPattern.class)).list();

		// LOGGER.info("Work with iterator took %d ms Result set is %d ",
		// System.currentTimeMillis() - t1, list.size());

		return list;
	}

	public int getPatternsFromFoldersToSystem(Folder folder) throws SQLException, IOException {

		long t1 = System.currentTimeMillis();

		ProjectionList projections = Projections.projectionList().add(Projections.property("FOLDER." + Folder.FOLDER_NAME), "kind");
		/*
		 * .add(Projections.property("PATTERN."+Pattern.PATTERN_NAME), "name")
		 * .add(Projections.property("PATTERN."+Pattern.PATTERN_DESCRIPTION),
		 * "description")
		 * .add(Projections.property("PATTERN."+Pattern.PATTERN_UID),
		 * "fileName")
		 * .add(Projections.property("PATTERN."+Pattern.CHUNK_SUMMARY),
		 * "summary")
		 * .add(Projections.property("PATTERNS_FOLDERS."+PatternsFolders.
		 * CORRECTORS_EN), "correctingFolderEn")
		 * .add(Projections.property("PATTERNS_FOLDERS."+PatternsFolders.
		 * CORRECTORS_EX), "correctingFolderEx");
		 */

		List list = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS, "PATTERNS_FOLDERS")
				.createCriteria(PatternsFolders.PATTERNS, "PATTERN").add(Restrictions.eq("PATTERNS_FOLDERS." + PatternsFolders.FOLDER, folder)).setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(EDXPattern.class)).list();

		// LOGGER.info("Work with iterator took %d ms Result set is %d ",
		// System.currentTimeMillis() - t1, list.size());

		return list.size();
	}

	// TODO method in this case is unusefull, make change or delete
	public List<EDXPattern> getPatternsWhereReproduced(int value) {

		ProjectionList projections = Projections.projectionList().add(Projections.property("FOLDER." + Folder.FOLDER_NAME), "kind")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_NAME), "name").add(Projections.property("PATTERN." + Pattern.PATTERN_DESCRIPTION), "description")
				.add(Projections.property("PATTERN." + Pattern.PATTERN_UID), "fileName").add(Projections.property("PATTERN." + Pattern.CHUNK_SUMMARY), "summary");

		List list = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS, "PATTERNS_FOLDERS")
				.createCriteria(PatternsFolders.PATTERNS, "PATTERN").add(Restrictions.eq(Pattern.IS_CAN_BE_REPRODUCED, value)).setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(EDXPattern.class)).list();

		return list;
	}

}
