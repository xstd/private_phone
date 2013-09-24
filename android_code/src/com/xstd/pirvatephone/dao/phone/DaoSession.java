<<<<<<< HEAD
package com.xstd.pirvatephone.dao.phone;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.pirvatephone.dao.phone.PhoneDetail;
import com.xstd.pirvatephone.dao.phone.PhoneRecord;

import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig phoneDetailDaoConfig;
    private final DaoConfig phoneRecordDaoConfig;

    private final PhoneDetailDao phoneDetailDao;
    private final PhoneRecordDao phoneRecordDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        phoneDetailDaoConfig = daoConfigMap.get(PhoneDetailDao.class).clone();
        phoneDetailDaoConfig.initIdentityScope(type);

        phoneRecordDaoConfig = daoConfigMap.get(PhoneRecordDao.class).clone();
        phoneRecordDaoConfig.initIdentityScope(type);

        phoneDetailDao = new PhoneDetailDao(phoneDetailDaoConfig, this);
        phoneRecordDao = new PhoneRecordDao(phoneRecordDaoConfig, this);

        registerDao(PhoneDetail.class, phoneDetailDao);
        registerDao(PhoneRecord.class, phoneRecordDao);
    }
    
    public void clear() {
        phoneDetailDaoConfig.getIdentityScope().clear();
        phoneRecordDaoConfig.getIdentityScope().clear();
    }

    public PhoneDetailDao getPhoneDetailDao() {
        return phoneDetailDao;
    }

    public PhoneRecordDao getPhoneRecordDao() {
        return phoneRecordDao;
    }

}
=======
package com.xstd.pirvatephone.dao.phone;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.pirvatephone.dao.phone.PhoneRecord;

import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig phoneRecordDaoConfig;

    private final PhoneRecordDao phoneRecordDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        phoneRecordDaoConfig = daoConfigMap.get(PhoneRecordDao.class).clone();
        phoneRecordDaoConfig.initIdentityScope(type);

        phoneRecordDao = new PhoneRecordDao(phoneRecordDaoConfig, this);

        registerDao(PhoneRecord.class, phoneRecordDao);
    }
    
    public void clear() {
        phoneRecordDaoConfig.getIdentityScope().clear();
    }

    public PhoneRecordDao getPhoneRecordDao() {
        return phoneRecordDao;
    }

}
>>>>>>> cbac3c1946faccae702966f4e8e133fbb3997bf4
