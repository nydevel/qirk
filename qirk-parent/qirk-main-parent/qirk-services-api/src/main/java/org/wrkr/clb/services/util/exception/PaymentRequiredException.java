package org.wrkr.clb.services.util.exception;

public class PaymentRequiredException extends SecurityException {

    private static final long serialVersionUID = -7786664583176514686L;

    public PaymentRequiredException(String message) {
        super(message);
    }
}
