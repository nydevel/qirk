/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
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
package org.wrkr.clb.common.mail;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductOwnerMailService extends BaseMailService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductOwnerMailService.class);

    private static final String MESSAGE_TEMPLATES_DIR = "message-templates/to-product-owner/";

    private static final String CRDT_FAILURE_LOG_FILE_DELIMETER = "\n==========\n";

    // email subjects
    private static class Subject {
        private static final String DAILY_CHARGE_SUCCESS = "[QIRK INFO] Daily charge operation successful (%s environment)";
        private static final String NODE_INACTIVE = "[QIRK INFO] Daily charge operation performed by another node (%s environment)";
        private static final String SETTING_ACTIVE_NODE_FAILURE = "[QIRK WARNING] Setting active node failed";

        private static final String FINANCIAL_OPERATION_FAILURE = "[QIRK WARNING] Financial operation failed";
        private static final String ACCOUNT_NOT_FOUND = "[QIRK WARNING] User's account not found";
        private static final String BALANCE_HISTORY_NOT_SAVED = "[QIRK WARNING] User's balance history not saved to cassandra";
        private static final String CLOUDPAYMENTS_TRANSACTION_PROCESSING_ERROR = "[QIRK WARNING] Error during processing cloudpayments transaction";

        private static final String RARUS_CHECK_STATUS = "[QIRK INFO] Rarus check status";
        private static final String RARUS_REQUEST_FAILED = "[QIRK WARNING] Rarus request failed";
    }

    // config values
    private List<String> productOwnerEmails;
    private String environment;

    public void setProductOwnerEmails(String productOwnerEmails) {
        this.productOwnerEmails = Arrays.asList(productOwnerEmails.split(","));
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    private void sendEmailToProductOwners(String subject, String body) throws Exception {
        sendEmail(productOwnerEmails, subject, body);
    }

    private Path crdtFailureLogFilePath;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        String catalinaHomePath = System.getProperty("catalina.home");
        if (catalinaHomePath != null) {
            crdtFailureLogFilePath = Paths.get(catalinaHomePath, "logs", "crdt-failures.log");
            if (!Files.exists(crdtFailureLogFilePath)) {
                Files.createFile(crdtFailureLogFilePath);
            }
        }
    }

    private void logCrdtFailure(Exception crdtExceptione) {
        try {
            byte[] bytesToWrite = (CRDT_FAILURE_LOG_FILE_DELIMETER + ExceptionUtils.getFullStackTrace(crdtExceptione)).getBytes();
            Files.write(crdtFailureLogFilePath, bytesToWrite, StandardOpenOption.APPEND);
        } catch (Exception e) {
            LOG.error("Could not write clb-crdt failure to log file", e);
        }
    }

    void _sendDailyChargeSuccessEmail(String nodeId, int userCount, BigDecimal amount) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("environment", environment);
        velocityContext.put("nodeId", nodeId);
        velocityContext.put("userCount", userCount);
        velocityContext.put("amount", amount.stripTrailingZeros().toPlainString());

        String subject = String.format(Subject.DAILY_CHARGE_SUCCESS, environment);
        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "daily-charge-success-en.vm", velocityContext);

        sendEmailToProductOwners(subject, body);
    }

    public void sendDailyChargeSuccessEmail(String nodeId, int userCount, BigDecimal amount) {
        try {
            _sendDailyChargeSuccessEmail(nodeId, userCount, amount);
        } catch (Exception e) {
            LOG.error("Could not send 'setting active node failed' email", e);
        }
    }

    void _sendNodeInactiveEmail(String nodeId) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("environment", environment);
        velocityContext.put("nodeId", nodeId);

        String subject = String.format(Subject.NODE_INACTIVE, environment);
        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "node-inactive-en.vm", velocityContext);

        sendEmailToProductOwners(subject, body);
    }

    public void sendNodeInactiveEmail(String nodeId) {
        try {
            _sendNodeInactiveEmail(nodeId);
        } catch (Exception e) {
            LOG.error("Could not send 'setting active node failed' email", e);
        }
    }

    void _sendSettingActiveNodeFailureEmail(String nodeId, String jobName, Exception nodeException) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("environment", environment);
        velocityContext.put("nodeId", nodeId);
        velocityContext.put("jobName", jobName);
        velocityContext.put("exceptionMessage", ExceptionUtils.getFullStackTrace(nodeException));

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "set-active-node-failed-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.SETTING_ACTIVE_NODE_FAILURE, body);
    }

    public void sendSettingActiveNodeFailureEmail(String nodeId, String jobName, Exception nodeException) {
        try {
            _sendSettingActiveNodeFailureEmail(nodeId, jobName, nodeException);
        } catch (Exception e) {
            LOG.error("Could not send 'setting active node failed' email", e);
        }
        logCrdtFailure(nodeException);
    }

    void _sendChargeFailureEmail(long userId, long amount, String transactionType, String transactionId, String balanceType,
            Exception chargeException, Exception reverseChargeException) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("userId", userId);
        velocityContext.put("amount", amount);
        velocityContext.put("transactionType", transactionType);
        velocityContext.put("transactionId", transactionId);
        velocityContext.put("balanceType", balanceType);
        velocityContext.put("chargeExceptionMessage", ExceptionUtils.getFullStackTrace(chargeException));
        velocityContext.put("reverseChargeFailed", (reverseChargeException != null));
        if (reverseChargeException != null) {
            velocityContext.put("reverseChargeExceptionMessage", ExceptionUtils.getFullStackTrace(reverseChargeException));
        }

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "charge-failed-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.FINANCIAL_OPERATION_FAILURE, body);
    }

    public void sendChargeFailureEmail(long userId, long amount, String transactionType, String transactionId, String balanceType,
            Exception chargeException, Exception reverseChargeException) {
        try {
            _sendChargeFailureEmail(
                    userId, amount, transactionType, transactionId, balanceType, chargeException, reverseChargeException);
        } catch (Exception e) {
            LOG.error("Could not send 'charge failed' email", e);
        }
        logCrdtFailure(chargeException);
        if (reverseChargeException != null) {
            logCrdtFailure(reverseChargeException);
        }
    }

    void _sendFundFailureEmail(long userId, long amount, String transactionType, String transactionId, String balanceType,
            Exception fundException, Exception reverseFundException) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("userId", userId);
        velocityContext.put("amount", amount);
        velocityContext.put("transactionType", transactionType);
        velocityContext.put("transactionId", transactionId);
        velocityContext.put("balanceType", balanceType);
        velocityContext.put("fundExceptionMessage", ExceptionUtils.getFullStackTrace(fundException));
        velocityContext.put("reverseFundFailed", (reverseFundException != null));
        if (reverseFundException != null) {
            velocityContext.put("reverseFundExceptionMessage", ExceptionUtils.getFullStackTrace(reverseFundException));
        }

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "fund-failed-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.FINANCIAL_OPERATION_FAILURE, body);
    }

    public void sendFundFailureEmail(long userId, long amount, String transactionType, String transactionId, String balanceType,
            Exception fundException, Exception reverseFundException) {
        try {
            _sendFundFailureEmail(
                    userId, amount, transactionType, transactionId, balanceType, fundException, reverseFundException);
        } catch (Exception e) {
            LOG.error("Could not send 'fund failed' email", e);
        }
        logCrdtFailure(fundException);
        if (reverseFundException != null) {
            logCrdtFailure(reverseFundException);
        }
    }

    void _sendDailyChargeFailureEmail(long userId, Exception chargeException) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("userId", userId);
        velocityContext.put("chargeExceptionMessage", ExceptionUtils.getFullStackTrace(chargeException));

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "daily-charge-failed-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.FINANCIAL_OPERATION_FAILURE, body);
    }

    public void sendDailyChargeFailureEmail(long userId, Exception chargeException) {
        try {
            _sendDailyChargeFailureEmail(userId, chargeException);
        } catch (Exception e) {
            LOG.error("Could not send 'daily charge failed' email", e);
        }
        logCrdtFailure(chargeException);
    }

    void _sendChargeAfterFundFailureEmail(long userId, Exception chargeException) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("userId", userId);
        velocityContext.put("chargeExceptionMessage", ExceptionUtils.getFullStackTrace(chargeException));

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "charge-after-fund-failed-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.FINANCIAL_OPERATION_FAILURE, body);
    }

    public void sendChargeAfterFundFailureEmail(long userId, Exception chargeException) {
        try {
            _sendChargeAfterFundFailureEmail(userId, chargeException);
        } catch (Exception e) {
            LOG.error("Could not send 'charge after fund failed' email", e);
        }
        logCrdtFailure(chargeException);
    }

    void _sendAccountNotFoundEmail(long userId, long amount) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("userId", userId);
        velocityContext.put("amount", amount);

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "account-not-found-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.ACCOUNT_NOT_FOUND, body);
    }

    public void sendAccountNotFoundEmail(long userId, long amount) {
        try {
            _sendAccountNotFoundEmail(userId, amount);
        } catch (Exception e) {
            LOG.error("Could not send 'account not found' email", e);
        }
    }

    void _sendBalanceHistoryNotSavedEmail(long userId, long amount, String transactionId, String balanceType)
            throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("userId", userId);
        velocityContext.put("amount", amount);
        velocityContext.put("transactionId", transactionId);
        velocityContext.put("balanceType", balanceType);

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "balance-history-not-saved-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.BALANCE_HISTORY_NOT_SAVED, body);
    }

    public void sendBalanceHistoryNotSavedEmail(long userId, long amount, String transactionId, String balanceType) {
        try {
            _sendBalanceHistoryNotSavedEmail(userId, amount, transactionId, balanceType);
        } catch (Exception e) {
            LOG.error("Could not send 'balance history not saved' email", e);
        }
    }

    void _sendCloudpaymentsTransactionProcessingErrorEmail(Long transactionId, Exception cloudpaymentsTransactionProcessingError)
            throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("transactionId", transactionId);
        velocityContext.put("cloudpaymentsTransactionProcessingError",
                ExceptionUtils.getFullStackTrace(cloudpaymentsTransactionProcessingError));

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "cloudpayments-transaction-process-error-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.CLOUDPAYMENTS_TRANSACTION_PROCESSING_ERROR, body);
    }

    public void sendCloudpaymentsTransactionProcessingErrorEmail(
            Long transactionId, Exception cloudpaymentsTransactionProcessingError) {
        try {
            _sendCloudpaymentsTransactionProcessingErrorEmail(transactionId, cloudpaymentsTransactionProcessingError);
        } catch (Exception e) {
            LOG.error("Could not send 'cloudpayments transaction processing error' email", e);
        }
        logCrdtFailure(cloudpaymentsTransactionProcessingError);
    }

    void _sendCheckStatusEmail(String transactionId, String operationId, String status)
            throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("transactionId", transactionId);
        velocityContext.put("operationId", operationId);
        velocityContext.put("status", status);

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "check-status-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.RARUS_CHECK_STATUS, body);
    }

    public void sendCheckStatusEmail(String transactionId, String operationId, String status) {
        try {
            _sendCheckStatusEmail(transactionId, operationId, status);
        } catch (Exception e) {
            LOG.error("Could not send 'check status' email", e);
        }
    }

    void _sendRarusRequestFailedEmail(String transactionId, Exception rarusRequestException)
            throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("transactionId", transactionId);
        velocityContext.put("rarusRequestException",
                ExceptionUtils.getFullStackTrace(rarusRequestException));

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "rarus-request-failed-en.vm", velocityContext);

        sendEmailToProductOwners(Subject.RARUS_REQUEST_FAILED, body);
    }

    public void sendRarusRequestFailedEmail(String transactionId, Exception rarusRequestException) {
        try {
            _sendRarusRequestFailedEmail(transactionId, rarusRequestException);
        } catch (Exception e) {
            LOG.error("Could not send 'rarus request failed' email", e);
        }
        logCrdtFailure(rarusRequestException);
    }
}
