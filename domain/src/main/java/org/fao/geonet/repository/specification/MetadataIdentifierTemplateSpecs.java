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

package org.fao.geonet.repository.specification;

import org.fao.geonet.domain.Constants;
import org.fao.geonet.domain.MetadataIdentifierTemplate;
import org.fao.geonet.domain.MetadataIdentifierTemplate_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Specifications for querying MetadataIdentifierTemplate.
 *
 * @author Jose García
 */
public class MetadataIdentifierTemplateSpecs {
    private MetadataIdentifierTemplateSpecs() {
        // don't permit instantiation
    }

    public static Specification<MetadataIdentifierTemplate> isSystemProvided(final boolean isSystemProvided) {
        return new Specification<MetadataIdentifierTemplate>() {
            @Override
            public Predicate toPredicate(Root<MetadataIdentifierTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path<Character> defaultAttributePath = root.get(MetadataIdentifierTemplate_.systemProvided_JPAWorkaround);
                Predicate systemProvidedDefaultPredicate = cb.equal(defaultAttributePath, cb.literal(Constants.toYN_EnabledChar(isSystemProvided)));
                return systemProvidedDefaultPredicate;
            }
        };
    }
}
