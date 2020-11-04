package org.wrkr.clb.common.mail;

import javax.mail.internet.AddressException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({ "classpath:qirk-services-test-root-ctx.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class DevOpsMailServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private DevOpsMailService mailService;

    @Test
    public void test_sendResourceFailedEmail() throws Exception {
        expectedException.expect(AddressException.class);

        mailService._sendResourceFailedEmail("test", new RuntimeException("Resource exception"));
    }
}
