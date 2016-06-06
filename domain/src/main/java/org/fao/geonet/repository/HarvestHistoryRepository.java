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

package org.fao.geonet.repository;

import org.fao.geonet.domain.HarvestHistory;
import org.fao.geonet.domain.MetadataCategory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Data Access object for accessing {@link MetadataCategory} entities.
 *
 * @author Jesse
 */
public interface HarvestHistoryRepository extends GeonetRepository<HarvestHistory, Integer>, HarvestHistoryRepositoryCustom,
    JpaSpecificationExecutor<HarvestHistory> {
    /**
     * Find all the HarvestHistory objects of the given type.
     *
     * @param harvesterType the harvester type
     */
    @Nonnull
    List<HarvestHistory> findAllByHarvesterType(@Nonnull String harvesterType);

    /**
     * Look up a harvester by its uuid.
     *
     * @param uuid the uuid of the harvester
     */
    @Nonnull
    List<HarvestHistory> findAllByHarvesterUuid(@Nonnull String uuid);

}
