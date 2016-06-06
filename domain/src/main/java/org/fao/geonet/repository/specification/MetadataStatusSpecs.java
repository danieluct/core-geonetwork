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

import org.fao.geonet.domain.MetadataStatus;
import org.fao.geonet.domain.MetadataStatusId_;
import org.fao.geonet.domain.MetadataStatus_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Specifications for querying {@link org.fao.geonet.repository.UserRepository}.
 *
 * @author Jesse
 */
public final class MetadataStatusSpecs {
    private MetadataStatusSpecs() {
        // no instantiation
    }

    public static Specification<MetadataStatus> hasMetadataId(final int metadataId) {
        return new Specification<MetadataStatus>() {
            @Override
            public Predicate toPredicate(Root<MetadataStatus> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<Integer> userIdAttributePath = root.get(MetadataStatus_.id).get(MetadataStatusId_.metadataId);
                Predicate idEqualPredicate = cb.equal(userIdAttributePath, cb.literal(metadataId));
                return idEqualPredicate;
            }
        };
    }

    public static Specification<MetadataStatus> hasUserId(final int userId) {
        return new Specification<MetadataStatus>() {
            @Override
            public Predicate toPredicate(Root<MetadataStatus> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<Integer> userIdAttributePath = root.get(MetadataStatus_.id).get(MetadataStatusId_.userId);
                Predicate uuidEqualPredicate = cb.equal(userIdAttributePath, cb.literal(userId));
                return uuidEqualPredicate;
            }
        };
    }
}
