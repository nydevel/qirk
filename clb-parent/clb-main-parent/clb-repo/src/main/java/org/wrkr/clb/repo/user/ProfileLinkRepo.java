/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.repo.user;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.user.ProfileLink;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.JPABaseIdRepo;

//@Repository
@SuppressWarnings("unused")
public class ProfileLinkRepo extends JPABaseIdRepo<ProfileLink> {

	@Override
	@Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
	public ProfileLink get(Long id) {
		return get(ProfileLink.class, id);
	}
	
	/*@Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
	public List<ProfileLink> getByUser(User user) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ProfileLink> query = criteriaBuilder.createQuery(ProfileLink.class);
		Root<ProfileLink> root = query.from(ProfileLink.class);
		query.where(criteriaBuilder.equal(root.get(ProfileLink_.user), user));

		return getResultList(query);
	}
	
	@Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
	public ProfileLink findByUserAndLink(User user, String link) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ProfileLink> query = criteriaBuilder.createQuery(ProfileLink.class);
		Root<ProfileLink> root = query.from(ProfileLink.class);
		query.where(criteriaBuilder.equal(root.get(ProfileLink_.user), user), criteriaBuilder.equal(root.get(ProfileLink_.link), link));

		return getSingleResultOrNull(query);
	}
	
	@Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
	public Integer deleteOldLinks(User user, List<String> preservedLinks) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaDelete<ProfileLink> criteria = criteriaBuilder.createCriteriaDelete(ProfileLink.class);
		Root<ProfileLink> root = criteria.from(ProfileLink.class);
		criteria.where(criteriaBuilder.equal(root.get(ProfileLink_.user), user), criteriaBuilder.not(root.get(ProfileLink_.link).in(preservedLinks)));
		
		return getEntityManager().createQuery(criteria).executeUpdate();
	}*/
}
