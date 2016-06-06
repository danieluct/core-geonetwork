/*
 * Copyright (C) 2001-2016 Food and Agriculture Organization of the
 * United Nations (FAO-UN), United Nations World Food Programme (WFP)
 * and United Nations Environment Programme (UNEP)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 *
 * Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
 * Rome - Italy. email: geonetwork@osgeo.org
 */

package org.fao.geonet.kernel.schema;

import com.google.common.collect.ImmutableSet;

import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by francois on 6/16/14.
 */
public abstract class SchemaPlugin implements CSWPlugin {
    public static final String LOGGER_NAME = "geonetwork.schema-plugin";
    public final String identifier;
    private List<SavedQuery> savedQueries = new ArrayList<>();
    private ImmutableSet<Namespace> allNamespaces;

    protected SchemaPlugin(String identifier,
                           ImmutableSet<Namespace> allNamespaces) {
        this.identifier = identifier;
        this.allNamespaces = allNamespaces;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<SavedQuery> getSavedQueries() {
        return savedQueries;
    }

    public void setSavedQueries(List<SavedQuery> savedQueries) {
        this.savedQueries = savedQueries;
    }

    public
    @Nullable
    SavedQuery getSavedQuery(@Nonnull String queryKey) {
        Iterator<SavedQuery> iterator = this.getSavedQueries().iterator();
        while (iterator.hasNext()) {
            SavedQuery query = iterator.next();
            if (queryKey.equals(query.getId())) {
                return query;
            }
        }
        return null;
    }

    public Set<Namespace> getNamespaces() {
        return allNamespaces;
    }
}
