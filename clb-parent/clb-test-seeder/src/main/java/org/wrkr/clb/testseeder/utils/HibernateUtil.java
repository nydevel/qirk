package org.wrkr.clb.testseeder.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.wrkr.clb.model.Attachment;
import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Organization;
import org.wrkr.clb.model.OrganizationMember;
import org.wrkr.clb.model.OrganizationMemberGroup;
import org.wrkr.clb.model.OrganizationMemberGroupPermissions;
import org.wrkr.clb.model.Project;
import org.wrkr.clb.model.TechnologyTag;
import org.wrkr.clb.model.UploadingAttachment;
import org.wrkr.clb.model.Version;
import org.wrkr.clb.model.task.Task;
import org.wrkr.clb.model.task.TaskPriority;
import org.wrkr.clb.model.task.TaskStatus;
import org.wrkr.clb.model.task.TaskType;
import org.wrkr.clb.model.user.ActivationToken;
import org.wrkr.clb.model.user.LoginStatistics;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserSocialAuth;

/**
 * @author Denis Bilenko
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory = null;

    static {
        Configuration cfg = new Configuration().configure();

        cfg.addAnnotatedClass(Language.class);
        cfg.addAnnotatedClass(Organization.class);
        cfg.addAnnotatedClass(OrganizationMember.class);
        cfg.addAnnotatedClass(OrganizationMemberGroup.class);
        cfg.addAnnotatedClass(OrganizationMemberGroupPermissions.class);
        cfg.addAnnotatedClass(Project.class);
        cfg.addAnnotatedClass(TechnologyTag.class);
        cfg.addAnnotatedClass(Version.class);

        cfg.addAnnotatedClass(TaskPriority.class);
        cfg.addAnnotatedClass(TaskStatus.class);
        cfg.addAnnotatedClass(TaskType.class);
        cfg.addAnnotatedClass(Task.class);

        cfg.addAnnotatedClass(ActivationToken.class);
        cfg.addAnnotatedClass(LoginStatistics.class);
        cfg.addAnnotatedClass(User.class);
        cfg.addAnnotatedClass(UserSocialAuth.class);
        
        cfg.addAnnotatedClass(Attachment.class);
        cfg.addAnnotatedClass(UploadingAttachment.class);

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties());

        sessionFactory = cfg.buildSessionFactory(builder.build());
    }

    public static SessionFactory getSessionfactory() {
        return sessionFactory;
    }
}
