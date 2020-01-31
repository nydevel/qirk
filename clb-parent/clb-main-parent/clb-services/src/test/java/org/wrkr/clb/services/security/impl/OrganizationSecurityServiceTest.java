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
package org.wrkr.clb.services.security.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.organization.OrganizationMemberRepo;
import org.wrkr.clb.repo.organization.OrganizationRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.BaseServiceTest;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.security.OrganizationSecurityService;

public class OrganizationSecurityServiceTest extends BaseServiceTest {

    private static final String orgOwnerEmail = "org_owner@test.com";
    private static final String orgManagerEmail = "org_manager@test.com";
    private static final String orgMemberEmail = "org_member@test.com";
    private static final String nonMemberEmail = "non_member@test.com";

    private static final String privateOrganizationName = "Private Organization";
    private static final String privateOrganizationUiId = "private_organization";

    private static final String publicOrganizationName = "Public Organization";
    private static final String publicOrganizationUiId = "public_organization";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationMemberRepo organizationMemberRepo;

    @Autowired
    private OrganizationSecurityService orgSecurityService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void beforeTest() throws Exception {
        User orgOwnerUser = saveUser(orgOwnerEmail);
        User orgManagerUser = saveUser(orgManagerEmail);
        User orgMemberUser = saveUser(orgMemberEmail);
        saveUser(nonMemberEmail);

        Organization privateOrganization = saveOrganization(orgOwnerUser, privateOrganizationName, privateOrganizationUiId, true);
        saveOrganization(orgOwnerUser, publicOrganizationName, publicOrganizationUiId, false);

        saveOrganizationMember(orgManagerUser, privateOrganization, true);
        saveOrganizationMember(orgMemberUser, privateOrganization, false);
    }

    @After
    public void afterTest() {
        testRepo.clearTable(OrganizationMember.class);
        testRepo.clearTable(Organization.class);
        testRepo.clearTable(User.class);
    }

    @Test
    public void test_managerCanReadTheirOrganizationById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);

        orgSecurityService.authzCanReadOrganization(orgManager, organization1.getId());
    }

    @Test
    public void test_managerCanReadTheirOrganizationByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        orgSecurityService.authzCanReadOrganization(orgManager, privateOrganizationUiId);
    }

    @Test
    public void test_managerCanReadTheirOrganizationById2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);

        orgSecurityService.authzCanReadOrganization(orgManager, new IdOrUiIdDTO(organization1.getId()));
    }

    @Test
    public void test_managerCanReadTheirOrganizationByUiId2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        orgSecurityService.authzCanReadOrganization(orgManager, new IdOrUiIdDTO(privateOrganizationUiId));
    }

    @Test
    public void test_managerCanModifyTheirOrganization() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);

        orgSecurityService.authzCanModifyOrganization(orgManager, organization1.getId());
    }

    @Test
    public void test_managerCanReadTheirOrganizationMembersById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);

        orgSecurityService.authzCanReadOrganizationMembers(orgManager, organization1.getId());
    }

    @Test
    public void test_managerCanReadTheirOrganizationMembersByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        orgSecurityService.authzCanReadOrganizationMembers(orgManager, privateOrganizationUiId);
    }

    @Test
    public void test_managerCanReadTheirOrganizationMembersById2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);

        orgSecurityService.authzCanReadOrganizationMembers(orgManager, new IdOrUiIdDTO(organization1.getId()));
    }

    @Test
    public void test_managerCanReadTheirOrganizationMembersByUiId2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        orgSecurityService.authzCanReadOrganizationMembers(orgManager, new IdOrUiIdDTO(privateOrganizationUiId));
    }

    @Test
    public void test_managerCanModifyTheirOrganizationMembersById() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);

        orgSecurityService.authzCanModifyOrganizationMembers(orgManager, organization1.getId());
    }

    @Test
    public void test_managerCanModifyTheirOrganizationMembersById2() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);

        orgSecurityService.authzCanModifyOrganizationMembers(orgManager, new IdOrUiIdDTO(organization1.getId()));
    }

    @Test
    public void test_managerCanModifyTheirOrganizationMembersByUiId() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);

        orgSecurityService.authzCanModifyOrganizationMembers(orgManager, new IdOrUiIdDTO(privateOrganizationUiId));
    }

    @Test
    public void test_managerCanReadTheirOrganizationMember() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        User otherUser = userRepo.getByEmail(orgMemberEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);
        OrganizationMember otherMember = organizationMemberRepo.getByUserAndOrganization(otherUser, organization1);

        orgSecurityService.authzCanReadOrganizationMember(orgManager, otherMember.getId());
    }

    @Test
    public void test_managerCanModifyTheirOrganizationMember() {
        User orgManager = userRepo.getByEmail(orgManagerEmail);
        User otherUser = userRepo.getByEmail(orgMemberEmail);
        Organization organization1 = organizationRepo.getByUiId(privateOrganizationUiId);
        OrganizationMember otherMember = organizationMemberRepo.getByUserAndOrganization(otherUser, organization1);

        orgSecurityService.authzCanModifyOrganizationMember(orgManager, otherMember.getId());
    }

    @Test
    public void test_ownerCanModifyThemselves() {
        User orgOwner = userRepo.getByEmail(orgOwnerEmail);
        Organization organization = organizationRepo.getByUiId(privateOrganizationUiId);
        OrganizationMember orgOwnerMember = organizationMemberRepo.getByUserAndOrganization(orgOwner, organization);

        orgSecurityService.authzCanModifyOrganizationMember(orgOwner, orgOwnerMember.getId());
    }

    @Test
    public void test_managerCantModifyTheirOrganizationOwner() {
        expectedException.expect(SecurityException.class);

        User orgManager = userRepo.getByEmail(orgManagerEmail);
        User orgOwner = userRepo.getByEmail(orgOwnerEmail);
        Organization organization = organizationRepo.getByUiId(privateOrganizationUiId);
        OrganizationMember orgOwnerMember = organizationMemberRepo.getByUserAndOrganization(orgOwner, organization);

        orgSecurityService.authzCanModifyOrganizationMember(orgManager, orgOwnerMember.getId());
    }

    @Test
    public void test_nonMemberCantModifyOrganizationOwner() {
        expectedException.expect(SecurityException.class);

        User nonMember = userRepo.getByEmail(nonMemberEmail);
        User orgOwner = userRepo.getByEmail(orgOwnerEmail);
        Organization organization = organizationRepo.getByUiId(privateOrganizationUiId);
        OrganizationMember orgOwnerMember = organizationMemberRepo.getByUserAndOrganization(orgOwner, organization);

        orgSecurityService.authzCanModifyOrganizationMember(nonMember, orgOwnerMember.getId());
    }
}
