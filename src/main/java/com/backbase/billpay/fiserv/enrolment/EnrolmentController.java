package com.backbase.billpay.fiserv.enrolment;

import com.backbase.billpay.fiserv.utils.AbstractController;
import com.backbase.billpay.integration.rest.spec.serviceapi.v2.billpay.enrolment.BillPayEnrolmentApi;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.MigrationGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.UserByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.UserByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.UserByIdPutResponseBody;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Bill Pay enrolment operations.
 */
@RestController
public class EnrolmentController extends AbstractController implements BillPayEnrolmentApi {
    
    private EnrolmentService enrolmentService;

    @Autowired
    public EnrolmentController(EnrolmentService enrolmentService) {
        this.enrolmentService = enrolmentService;
    }

    @Override
    public MigrationGetResponseBody getMigration(String user, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        MigrationGetResponseBody response = enrolmentService.getMigrationId(user);
        logger.debug("Checking migration for user: {}, found: {}", user, response.getSubscriberID());
        return response;
    }
    
    @Override
    public BillPayEnrolmentPostResponseBody postBillPayEnrolment(
                    @Valid BillPayEnrolmentPostRequestBody request,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String user = request.getUser();
        BillPayEnrolmentPostResponseBody response =
                        enrolmentService.enrol(fiservUtils.createHeader(httpServletRequest, null), user);
        logger.debug("Enrol user: {}, returned billpayID: {}", user, response.getId());
        return response;
    }
    
    @Override
    public UserByIdPutResponseBody putUserById(@Valid UserByIdPutRequestBody userByIdPutRequestBody, String userId,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserByIdGetResponseBody getUserById(String userId, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        UserByIdGetResponseBody response = enrolmentService.getSubscriberInfo(fiservUtils
                        .createHeader(httpServletRequest, userId), userId);
        logger.debug("Retrieving subscriber information for user: {}", userId);
        return response;
    }

}
