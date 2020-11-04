package org.wrkr.clb.model.project;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.wrkr.clb.model.user.User;

/**
 * @author Evgeny Poreykin
 *
 */
@Entity
@Table(name = ProjectInviteMeta.TABLE_NAME)
public class ProjectInvite extends BaseProjectInvite {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
