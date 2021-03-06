package com.xstd.pirvatephone.dao.demo;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig pMProtocalDaoConfig;
    private final DaoConfig pushUserInfoDaoConfig;

    private final PMProtocalDao pMProtocalDao;
    private final PushUserInfoDao pushUserInfoDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        pMProtocalDaoConfig = daoConfigMap.get(PMProtocalDao.class).clone();
        pMProtocalDaoConfig.initIdentityScope(type);

        pushUserInfoDaoConfig = daoConfigMap.get(PushUserInfoDao.class).clone();
        pushUserInfoDaoConfig.initIdentityScope(type);

        pMProtocalDao = new PMProtocalDao(pMProtocalDaoConfig, this);
        pushUserInfoDao = new PushUserInfoDao(pushUserInfoDaoConfig, this);

        registerDao(PMProtocal.class, pMProtocalDao);
        registerDao(PushUserInfo.class, pushUserInfoDao);
    }
    
    public void clear() {
        pMProtocalDaoConfig.getIdentityScope().clear();
        pushUserInfoDaoConfig.getIdentityScope().clear();
    }

    public PMProtocalDao getPMProtocalDao() {
        return pMProtocalDao;
    }

    public PushUserInfoDao getPushUserInfoDao() {
        return pushUserInfoDao;
    }

}
