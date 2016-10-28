package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.cmp.machine.strains.EDXPattern;
import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.PatternsFolders;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PatternsDao  extends GenericDao<Pattern,Long>{
	
	private static final Logger LOGGER = LoggerUtil.getLogger(PatternsDao.class);

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
    
	public List<EDXPattern> getFromDatabase() throws SQLException, IOException {
		
		 long t1 = System.currentTimeMillis();

		ProjectionList projections = Projections.projectionList()
                .add(Projections.property("FOLDER."+Folder.FOLDER_NAME), "kind")
                .add(Projections.property("PATTERN."+Pattern.PATTERN_NAME), "name")
                .add(Projections.property("PATTERN."+Pattern.PATTERN_DESCRIPTION), "description")
                .add(Projections.property("PATTERN."+Pattern.PATTERN_UID), "fileName")
		 .add(Projections.property("PATTERNS_FOLDERS."+PatternsFolders.CORRECTORS), "correctingFolder");
		
		List list = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS,"PATTERNS_FOLDERS").createCriteria(PatternsFolders.PATTERNS,"PATTERN")
				.add(Restrictions.isNotNull("PATTERNS_FOLDERS."+PatternsFolders.CORRECTORS))
				.setProjection(projections)
        .setResultTransformer(Transformers.aliasToBean(EDXPattern.class)).list();
		
		LOGGER.info("Work with iterator took %d ms Result set is %d ", System.currentTimeMillis() - t1, list.size());
		
		return list;
        
/**		
		PreparedStatement ps = db.prepareStatement(
        		"SELECT IDFOLDER,"
        	            + "     IDPATTERN,"
        	            + "     PATTERNNAME,"
        	            + "     PATTERNDESCRIPTION,"
        	            + "     PATTERNUID,"
        	            + "     FOLDERNAME,"
        	            + "     FOLDER_ID,"
        	            + "     CORRECTORS,"
        	            + "     PATTERN_ID\n"
        	            + "FROM MAIN.FOLDER\n"
        	            + "JOIN MAIN.PATTERNS_FOLDERS"
        	            + "     ON FOLDER.ID = PATTERNS_FOLDERS.FOLDER_ID\n"
        	            + "JOIN MAIN.PATTERN"
        	            + "     ON PATTERN.ID = PATTERNS_FOLDERS.PATTERN_ID\n"
        	            + "WHERE CORRECTORS IS NOT NULL");
        
        List<EDXPattern> patterns = new ArrayList<>();
        ResultSet rs  = ps.executeQuery();
        while(rs.next()){
        	patterns.add(new EDXPattern(
                rs.getString("FOLDERNAME"),
                rs.getString("PATTERNNAME"),
                rs.getString("PATTERNDESCRIPTION"),
                "./data/edxfiles/" + rs.getString("PATTERNUID"),
                rs.getString("CORRECTORS")));
        }
        return patterns;
        **/
    }
    
	public List<EDXPattern> getFromDatabase(Long filter) throws SQLException, IOException {

        long t1 = System.currentTimeMillis();
		
        ProjectionList projections = Projections.projectionList()
                .add(Projections.property("FOLDER."+Folder.FOLDER_NAME), "kind")
                .add(Projections.property("PATTERN."+Pattern.PATTERN_NAME), "name")
                .add(Projections.property("PATTERN."+Pattern.PATTERN_DESCRIPTION), "desc")
                .add(Projections.property("PATTERN."+Pattern.PATTERN_UID), "filename")
        .add(Projections.property("PATTERNS_FOLDERS."+PatternsFolders.CORRECTORS), "correctingFolder");
        
        Criteria query = getSession().createCriteria(Folder.class, "FOLDER").setCacheable(false).createCriteria(Folder.PATTERNS_FOLDERS,"PATTERNS_FOLDERS").createCriteria(PatternsFolders.PATTERNS,"PATTERN")
        .add(Restrictions.eq("FOLDER."+Folder.ID_FOLDER, filter))
        .setProjection(projections).
        setResultTransformer(Transformers.aliasToBean(EDXPattern.class));
       
        List list = query.list();
        
        LOGGER.info("Work with iterator and filter took %d ms Result set is %d ", System.currentTimeMillis() - t1, list.size());
        
		return list;
		
		/**
        PreparedStatement ps = db.prepareStatement(
            "SELECT IDFOLDER,"
            + "     IDPATTERN,"
            + "     PATTERNNAME,"
            + "     PATTERNDESCRIPTION,"
            + "     PATTERNUID,"
            + "     FOLDERNAME,"
            + "     FOLDER_ID,"
            + "     CORRECTORS,"
            + "     PATTERN_ID\n"
            + "FROM MAIN.FOLDER\n"
            + "JOIN MAIN.PATTERNS_FOLDERS"
            + "     ON FOLDER.ID = PATTERNS_FOLDERS.FOLDER_ID\n"
            + "JOIN MAIN.PATTERN"
            + "     ON PATTERN.ID = PATTERNS_FOLDERS.PATTERN_ID\n"
            + "WHERE FOLDER_ID = ?");
        ps.setString(1, filter);
        
        List<EDXPattern> patterns = new ArrayList<>();
        ResultSet rs  = ps.executeQuery();
        rs.setFetchSize(1000);
        
        long t1 = System.currentTimeMillis();
        
        while(rs.next()){
        	patterns.add(new EDXPattern(
                rs.getString("FOLDERNAME"),
                rs.getString("PATTERNNAME"),
                rs.getString("PATTERNDESCRIPTION"),
                "./data/edxfiles/" + rs.getString("PATTERNUID"),
                rs.getString("CORRECTORS")));
        }
        
        LOGGER.info("Work with iterator took %d ms Result set is %d ", System.currentTimeMillis() - t1, patterns.size());
        
        return patterns;
        
        **/
    }



}
