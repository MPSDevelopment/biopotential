package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.PatternsFolders;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.Collection;
import java.util.List;


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

    public Folder getByName(String value) {
        Criteria query = getSession().createCriteria(Folder.class).setCacheable(false);
        query.add(Restrictions.eq(Folder.FOLDER_NAME, value));
        return (Folder) query.uniqueResult();
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

    /*public Faf loadByCoordinates(Double latitude, Double longitude, Long airfieldId) {
        Criteria query = getSession().createCriteria(Faf.class).setCacheable(false);
        query.add(Restrictions.eq(Faf.LATITUDE_FIELD, latitude));
        query.add(Restrictions.eq(Faf.LONGITUDE_FIELD, longitude));
        query.createCriteria(Faf.AIRFIELD_FIELD, AIRFIELD_CRITERIA_ALIAS);
        query.add(Restrictions.eq(AIRFIELD_CRITERIA_ALIAS + DOT_SIGN + Airfield.ID_FIELD, airfieldId));
        return (Faf) query.uniqueResult();
    }*/

}
