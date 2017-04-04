package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Folder;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class FolderDaoTest {

    private static final Logger LOGGER = LoggerUtil.getLogger(FolderDaoTest.class);

    @Autowired
    private FoldersDao foldersDao;
    private List<Folder> folders;

    @Test
    public void checkPatternsFolders() {

//        folders = new ArrayList<Folder>();
        folders = foldersDao.getPatternsFolders(Arrays.asList(490, 959, 2483));
        LOGGER.info("folders size %s", folders.size());

    }

    @Test
    public void getFolders() {
        List<Folder> folderList = foldersDao.getFolders();
        LOGGER.info("folderList size %s", folderList.size());

    }

}
