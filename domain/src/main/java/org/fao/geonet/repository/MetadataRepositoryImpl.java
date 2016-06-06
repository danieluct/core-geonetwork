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

import com.google.common.collect.Maps;

import org.fao.geonet.domain.ISODate;
import org.fao.geonet.domain.Metadata;
import org.fao.geonet.domain.MetadataDataInfo_;
import org.fao.geonet.domain.MetadataSourceInfo;
import org.fao.geonet.domain.Metadata_;
import org.fao.geonet.domain.Pair;
import org.fao.geonet.repository.reports.MetadataReportsQueries;
import org.fao.geonet.repository.statistic.MetadataStatisticsQueries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

/**
 * Implementation for all {@link Metadata} queries that cannot be automatically generated by
 * Spring-data.
 *
 * @author Jesse
 */
public class MetadataRepositoryImpl implements MetadataRepositoryCustom {

    @PersistenceContext
    EntityManager _entityManager;

    @Override
    public MetadataStatisticsQueries getMetadataStatistics() {
        return new MetadataStatisticsQueries(_entityManager);
    }

    @Override
    public MetadataReportsQueries getMetadataReports() {
        return new MetadataReportsQueries(_entityManager);
    }

    @Override
    public Metadata findOne(String id) {
        try {
            return _entityManager.find(Metadata.class, Integer.parseInt(id));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id parameter of findByIdString must be parsable to an integer.  It was '" + id + "'");
        }
    }

    @Override
    public
    @Nonnull
    Page<Pair<Integer, ISODate>> findAllIdsAndChangeDates(@Nullable Pageable pageable) {
        CriteriaBuilder cb = _entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cbQuery = cb.createQuery(Tuple.class);
        Root<Metadata> root = cbQuery.from(Metadata.class);

        cbQuery.multiselect(cb.count(root));
        Long total = (Long) _entityManager.createQuery(cbQuery).getSingleResult().get(0);
        cbQuery.multiselect(root.get(Metadata_.id), root.get(Metadata_.dataInfo).get(MetadataDataInfo_.changeDate));

        if (pageable != null && pageable.getSort() != null) {
            final Sort sort = pageable.getSort();
            List<Order> orders = SortUtils.sortToJpaOrders(cb, sort, root);

            cbQuery.orderBy(orders);
        }

        TypedQuery<Tuple> query = _entityManager.createQuery(cbQuery);
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        ArrayList<Pair<Integer, ISODate>> finalResults = new ArrayList<Pair<Integer, ISODate>>();
        for (Tuple tuple : query.getResultList()) {
            final Integer mdId = (Integer) tuple.get(0);
            final ISODate changeDate = (ISODate) tuple.get(1);
            finalResults.add(Pair.read(mdId, changeDate));
        }
        return new PageImpl<Pair<Integer, ISODate>>(finalResults, pageable, total);
    }

    @Nonnull
    @Override
    public List<Integer> findAllIdsBy(@Nonnull Specification<Metadata> spec) {
        CriteriaBuilder cb = _entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> cbQuery = cb.createQuery(Integer.class);
        Root<Metadata> root = cbQuery.from(Metadata.class);
        cbQuery.select(root.get(Metadata_.id));

        cbQuery.where(spec.toPredicate(root, cbQuery, cb));
        return _entityManager.createQuery(cbQuery).getResultList();
    }


    @Override
    public Metadata findOneOldestByChangeDate() {
        final CriteriaBuilder cb = _entityManager.getCriteriaBuilder();
        final CriteriaQuery<Metadata> query = cb.createQuery(Metadata.class);
        final Root<Metadata> metadataRoot = query.from(Metadata.class);
        final Path<ISODate> changeDate = metadataRoot.get(Metadata_.dataInfo).get(MetadataDataInfo_.changeDate);
        query.orderBy(cb.asc(changeDate));
        return _entityManager.createQuery(query).setMaxResults(1).getSingleResult();
    }

    @Override
    public Map<Integer, MetadataSourceInfo> findAllSourceInfo(Specification<Metadata> spec) {
        CriteriaBuilder cb = _entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cbQuery = cb.createQuery(Object[].class);
        Root<Metadata> root = cbQuery.from(Metadata.class);
        cbQuery.select(cb.array(root.get(Metadata_.id), root.get(Metadata_.sourceInfo)));

        cbQuery.where(spec.toPredicate(root, cbQuery, cb));
        Map<Integer, MetadataSourceInfo> results = Maps.newHashMap();
        final List<Object[]> resultList = _entityManager.createQuery(cbQuery).getResultList();
        for (Object[] objects : resultList) {
            final Integer metadataId = (Integer) objects[0];
            final MetadataSourceInfo sourceInfo = (MetadataSourceInfo) objects[1];
            results.put(metadataId, sourceInfo);
        }

        return results;
    }

    /**
     * @see org.fao.geonet.repository.MetadataRepositoryCustom#findAllSimple(org.springframework.data.jpa.domain.Specification)
     */
    @Override
    public List<SimpleMetadata> findAllSimple(String id) {
        Query query = _entityManager.createQuery(
            "select new org.fao.geonet.repository.SimpleMetadata("
                + "id, uuid, dataInfo.changeDate, dataInfo.type_JPAWorkaround) "
                + "from Metadata where harvestInfo.uuid = :id").setParameter("id", id);

        //TODO paginate

        return query.getResultList();
    }

}
