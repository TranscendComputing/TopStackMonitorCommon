/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.monitor.common.model.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.model.monitor.DimensionBean;
import com.msi.tough.utils.CWUtil;

/**
 * @author jgardner
 *
 */
@Component
public class DimensionHelper {

    private static SessionFactory sessionFactory = null;

    @Transactional
    public DimensionBean getDimensionBean(
            final String key, final String value,
            final boolean addNew) {

        Session session = sessionFactory.getCurrentSession();

        try {
            return CWUtil.getDimensionBean(session, 0L, key, value, addNew);
        } catch (RuntimeException e) {
            // Not
            throw e;
        }
        catch (Exception e) {
            // No checked exceptions
            return null;
        }
    }

    @Autowired(required=true)
    public void setSessionFactory(SessionFactory sessionFactory) {
        DimensionHelper.sessionFactory = sessionFactory;
    }
}
