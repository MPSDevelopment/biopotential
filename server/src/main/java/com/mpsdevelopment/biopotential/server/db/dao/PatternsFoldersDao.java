package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.PatternsFolders;
import com.mpsdevelopment.biopotential.server.gui.BioApplication;
import com.mpsdevelopment.plasticine.commons.IdGenerator;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class PatternsFoldersDao extends GenericDao<PatternsFolders, Long> {

	private static final Logger LOGGER = LoggerUtil.getLogger(PatternsFoldersDao.class);
    private PatternsFolders patternsFolders;

	public PatternsFolders getByPattern(Pattern pattern) {

		Criteria query = getSession().createCriteria(PatternsFolders.class).setCacheable(false);
		query.add(Restrictions.eq(PatternsFolders.PATTERNS, pattern));
        try{
            patternsFolders = (PatternsFolders) query.uniqueResult();

        }
        catch (NonUniqueResultException e) {
            LOGGER.printStackTrace(e);
        }
//		LOGGER.info("PatternsFolders %s", patternsFolders.getPattern().getPatternName());
        if (patternsFolders != null) {
            LOGGER.info("return patternsFolders");
            return patternsFolders;
        }
        return new PatternsFolders();
	}

	public List<PatternsFolders> getPatternsByFolder(Folder folder) {
        Criteria query = getSession().createCriteria(PatternsFolders.class).setCacheable(false);
        query.add(Restrictions.eq(PatternsFolders.FOLDER, folder));
        List<PatternsFolders> patterns = query.list();
        return patterns;
    }

    public void insertNewPatternsFolders(PatternsFolders patternsFolders, boolean isNewObject) {

        Query query;
        if (isNewObject) {
            query = getSession().createSQLQuery("INSERT INTO MAIN.PATTERNS_FOLDERS (id,isJustCreated, FOLDER_ID, PATTERN_ID, correctorsEn, correctorsEx) VALUES(:id, true, :FOLDER_ID, :PATTERN_ID, :correctorsEn, :correctorsEx)");
            long idx = IdGenerator.nextId();
            query.setParameter("id", idx);
//            query.setParameter("time", folder.getTime());
        } else {
            query = getSession().createSQLQuery("UPDATE MAIN.PATTERNS_FOLDERS SET FOLDER_ID = :FOLDER_ID, PATTERN_ID = :PATTERN_ID, correctorsEn = :correctorsEn, correctorsEx = :correctorsEx WHERE id = :id");
            query.setParameter("id", patternsFolders.getId());
        }
        query.setParameter("FOLDER_ID", patternsFolders.getFolder());
        query.setParameter("PATTERN_ID", patternsFolders.getPattern());
        query.setParameter("correctorsEn", patternsFolders.getCorrectorsEn());
        query.setParameter("correctorsEx", patternsFolders.getCorrectorsEx());
        query.executeUpdate();
    }
}
