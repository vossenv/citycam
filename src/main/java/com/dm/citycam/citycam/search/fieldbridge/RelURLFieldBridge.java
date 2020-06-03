package com.dm.citycam.citycam.search.fieldbridge;/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */


import org.hibernate.search.bridge.TwoWayStringBridge;
import org.hibernate.search.bridge.spi.IgnoreAnalyzerBridge;
import org.hibernate.search.exception.SearchException;
import org.hibernate.search.util.StringHelper;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Bridge for a {@link URL} to a {@link String}.
 *
 * @author Emmanuel Bernard
 */
public class RelURLFieldBridge implements TwoWayStringBridge, IgnoreAnalyzerBridge {

    private static final String URL_SEARCH = "https://|http://|ldap://|ldaps://|ftp://|tcp://";

    @Override
    public Object stringToObject(String stringValue) {
        return stringValue;
    }

    @Override
    public String objectToString(Object object) {
        return object == null ?
                null :
                object.toString().replaceAll(URL_SEARCH,"");
    }
}
