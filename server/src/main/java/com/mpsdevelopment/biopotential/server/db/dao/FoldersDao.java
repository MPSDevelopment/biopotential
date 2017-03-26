package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.PatternsFolders;
import com.mpsdevelopment.plasticine.commons.IdGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;


public class FoldersDao extends GenericDao<Folder, Long> {

    /*public List<Pattern> getAllPatternsByFolder(Folder folder) {
        Criteria query = getSession().createCriteria(PatternsFolders.class).setCacheable(false);
        query.add(Restrictions.eq(Pattern.ID_PATTERN, folder));
        return query.list();
    }*/

    public Folder getById(int value) {
        Criteria query = getSession().createCriteria(Folder.class).setCacheable(false);
        query.add(Restrictions.eq(Folder.ID_FOLDER, value));
        return (Folder) query.uniqueResult();
    }

    public List<Folder> getFolders() {
        Criteria query = getSession().createCriteria(Pattern.class).setCacheable(false);

        return query.list();
    }

    public Folder getByName(String value) {
        Folder uniqueResult = new Folder();
        Criteria query = getSession().createCriteria(Folder.class).setCacheable(false);
        query.add(Restrictions.eq(Folder.FOLDER_NAME, value));
        try {
            uniqueResult = (Folder) query.uniqueResult();
        } catch (NonUniqueResultException e) {
            List<Folder> list = query.list();
            for (int i = 0; i < query.list().size(); i++) {
                if(list.get(i).getParentFolderId() != null) {
                    uniqueResult = list.get(i);
                }
            }
        }
        return uniqueResult;
    }

    public List<Folder> getPatternsFolders(Collection<Integer> filter) {

//                SELECT  * FROM patterns JOIN link_patterns_to_folders ON patterns.id_pattern = link_patterns_to_folders.id_pattern
//                       JOIN folders
//                       ON folders.id_folder = link_patterns_to_folders.id_folder


        Criteria query = getSession().createCriteria(Folder.class).setCacheable(false);

        query = query.createCriteria(Folder.PATTERNS_FOLDERS)/*.createCriteria(PatternsFolders.PATTERNS)*/;
        if (CollectionUtils.isEmpty(filter)) {
            query.add(Restrictions.in(Folder.ID_FOLDER, filter));
        }
        return query.list();
//        return (Folder) query.uniqueResult();
    }

    public void insertNewFolder(Folder folder, boolean isNewObject) {

        Query query;
        if (isNewObject) {
            query = getSession().createSQLQuery("INSERT INTO MAIN.FOLDER (id,isJustCreated, dbdtsAdded, folderDescription, folderName, folderType, idFolder, isInUse, parentFolderId, sortPriority) VALUES(:id, true, :dbdtsAdded, :folderDescription, :folderName, :folderType, :idFolder, :isInUse, :parentFolderId, :sortPriority)");
            long idx = IdGenerator.nextId();
            query.setParameter("id", idx);
//            query.setParameter("time", folder.getTime());
        } else {
            query = getSession().createSQLQuery("UPDATE MAIN.FOLDER SET dbdtsAdded = :dbdtsAdded, folderDescription = :folderDescription, folderName = :folderName, folderType = :folderType, idFolder = :idFolder, isInUse = :isInUse, parentFolderId = :parentFolderId, sortPriority = :sortPriority WHERE id = :id");
            query.setParameter("id", folder.getId());
        }
        query.setParameter("dbdtsAdded", folder.getDbdtsAdded());
        query.setParameter("folderDescription", folder.getFolderDescription());
        query.setParameter("folderName", folder.getFolderName());
        query.setParameter("folderType", folder.getFolderType());
        query.setParameter("idFolder", folder.getIdFolder());
        query.setParameter("isInUse", folder.getIsInUse());
        query.setParameter("parentFolderId", folder.getParentFolderId());
        query.setParameter("sortPriority", folder.getSortPriority());
        query.executeUpdate();
    }


    /*public Faf loadByCoordinates(Double latitude, Double longitude, Long airfieldId) {
        Criteria query = getSession().createCriteria(Faf.class).setCacheable(false);
        query.add(Restrictions.eq(Faf.LATITUDE_FIELD, latitude));
        query.add(Restrictions.eq(Faf.LONGITUDE_FIELD, longitude));
        query.createCriteria(Faf.AIRFIELD_FIELD, AIRFIELD_CRITERIA_ALIAS);
        query.add(Restrictions.eq(AIRFIELD_CRITERIA_ALIAS + DOT_SIGN + Airfield.ID_FIELD, airfieldId));
        return (Faf) query.uniqueResult();
    }*/

}
