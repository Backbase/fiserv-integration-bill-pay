package com.backbase.billpay.fiserv.enrolment;

import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.enrolment.BillPayEnrolmentListener;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.MigrationGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.UserByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.UserByIdPutResponseBody;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JMS consumer for Billpay enrolment.
 */
@Service
@RequestListener
public class EnrolmentListener extends AbstractListener implements BillPayEnrolmentListener {

    @Autowired
    private EnrolmentService enrolmentService;
    
    @Override
    public RequestProxyWrapper<MigrationGetResponseBody> getMigration(RequestProxyWrapper<Void> request, 
            Exchange exchange, String user) {
        MigrationGetResponseBody response = enrolmentService.getMigrationId(user);
        logger.debug("Checking migration for user: {}, found: {}", user, response.getSubscriberID());
        return createRequestProxyWrapper(request, response);
    }

    @Override
    public RequestProxyWrapper<BillPayEnrolmentPostResponseBody> postBillPayEnrolment(
            RequestProxyWrapper<BillPayEnrolmentPostRequestBody> request, Exchange exchange) {
        String user = request.getRequest().getData().getUser();
        BillPayEnrolmentPostResponseBody response = enrolmentService.enrol(createFiservHeader(request, null), user);
        logger.debug("Enrol user: {}, returned billpayID: {}", user, response.getId());
        return createRequestProxyWrapper(request, response);
    }

    @Override
    public RequestProxyWrapper<UserByIdPutResponseBody> putUserById(
                    RequestProxyWrapper<UserByIdPutRequestBody> userByIdPutRequestBody, Exchange exchange,
                    String userId) throws BadRequestException, InternalServerErrorException, NotFoundException {
        throw new UnsupportedOperationException();
    }

}
