package com.xstd.pirvatephone.dao.use.demo;

import android.content.Context;
import android.text.TextUtils;
import com.xstd.pirvatephone.dao.demo.DaoSession;
import com.xstd.pirvatephone.dao.demo.PushUserInfo;
import com.xstd.pirvatephone.dao.demo.PushUserInfoDao;

import java.util.List;

class PushUser {

    private PushUserInfoDao mDataDao;

    private PushUserInfo mCurrentUserInfo;

    public PushUser(Context context) {
        DaoSession session = PushDaoUtils.getDaoSession(context);
        mDataDao = session.getPushUserInfoDao();

        List<PushUserInfo> userInfos = mDataDao.loadAll();
        if (userInfos != null && userInfos.size() > 0) {
            mCurrentUserInfo = userInfos.get(0);
        } else {
            mCurrentUserInfo = new PushUserInfo();
        }
        
        if (mCurrentUserInfo.getId() == null) {
            mCurrentUserInfo.setId(Long.valueOf(0));
        }

        /**
         *
         *   Demo 代码
         *
         */
        //demo代码，如果需要批量删除
//        mDataDao.insertInTx(null); //TX接口支持批量

        //demo代码，query
        mDataDao.queryBuilder().where(PushUserInfoDao.Properties.Id.eq(100)).build().forCurrentThread().list();
    }

    public synchronized PushUserInfo getCurrentLoginUser() {
        return mCurrentUserInfo;
    }

    public synchronized boolean updateCurrentUserId(long id) {
        if (mCurrentUserInfo != null && mCurrentUserInfo.getId() != id && id > 0) {
            mCurrentUserInfo = new PushUserInfo();
            mCurrentUserInfo.setId(id);
            // new user
            mDataDao.deleteAll();
            mDataDao.insert(mCurrentUserInfo);
            return true;
        }

        return false;
    }
    
    public synchronized boolean updateCurrentUserLastTime(long time) {
        if (mCurrentUserInfo != null) {
            mCurrentUserInfo.setLastLoginTime(time);
            if (mDataDao != null) {
                mDataDao.deleteAll();
            }
            mDataDao.insertOrReplace(mCurrentUserInfo);
            return true;
        }

        return false;
    }

    public synchronized boolean updateCurrentUserGenerateId(String id) {
        if (mCurrentUserInfo != null && !TextUtils.isEmpty(id) && !id.equals(mCurrentUserInfo.getGenerateId())) {
            mCurrentUserInfo.setGenerateId(id);
            mDataDao.update(mCurrentUserInfo);
            return true;
        }

        return false;
    }

    public synchronized boolean updateCurrentUserTicket(String ticket) {
        if (mCurrentUserInfo != null && !TextUtils.isEmpty(ticket) && !ticket.equals(mCurrentUserInfo.getTicket())) {
            mCurrentUserInfo.setTicket(ticket);
            mDataDao.update(mCurrentUserInfo);
            return true;
        }

        return false;
    }

    public synchronized boolean updateCurrentPushServer(String server) {
        if (mCurrentUserInfo != null && !TextUtils.isEmpty(server) && !server.equals(mCurrentUserInfo.getServer())) {
            mCurrentUserInfo.setServer(server);
            mDataDao.update(mCurrentUserInfo);
            return true;
        }

        return false;
    }
    
    public synchronized boolean updateCurrentLoginUser(PushUserInfo info) {
        if (info != null) {
            if (mCurrentUserInfo != null && mCurrentUserInfo.getId() == info.getId()) {
                mCurrentUserInfo = info;
                if (mDataDao != null) {
                    mDataDao.update(mCurrentUserInfo);
                }
            } else {
                mCurrentUserInfo = info;
                mDataDao.deleteAll();
                mDataDao.insert(mCurrentUserInfo);
            }

            return true;
        }

        return false;
    }

    public synchronized void clearInfo() {
        if (mDataDao != null) {
            mDataDao.deleteAll();
        }

        if (mCurrentUserInfo != null) {
            mCurrentUserInfo = new PushUserInfo();
        }
    }
}
