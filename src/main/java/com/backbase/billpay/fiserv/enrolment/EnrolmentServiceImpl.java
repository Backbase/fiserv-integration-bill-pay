package com.backbase.billpay.fiserv.enrolment;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.enrolment.model.BankAccountInformation;
import com.backbase.billpay.fiserv.enrolment.model.IndividualName;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberEnrollRequest;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberEnrollResponse;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberInformation;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payees.model.UsAddress;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.fiserv.payments.model.BankAccountId.BankAccountType;
import com.backbase.billpay.fiserv.search.SearchServiceImpl;
import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.MigrationGetResponseBody;
import com.backbase.buildingblocks.logging.api.Logger;
import com.backbase.buildingblocks.logging.api.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Enrolment service for enrolment into Fiserv.
 */
@Service
public class EnrolmentServiceImpl implements EnrolmentService {

    private static final String SOAP_ACTION = "SubscriberEnroll";
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final FiservClient client;
    
    /**
     * Setup client.
     * @param client provider client service
     */
    @Autowired
    public EnrolmentServiceImpl(FiservClient client) {
        this.client = client;
    }
    
    @Override
    public MigrationGetResponseBody getMigrationId(String user) {
        /*
         * This service currently returns null for the subscriber ID, meaning an enrolment should be attempted
         * with Fiserv.  If Fiserv accounts have previously been enrolled this service can be used to supply the 
         * existing subscriber ID to allow for migrations from an external system into Backbase Bill Pay product.
         * This will allow an existing user to continue using their existing Bill Pay provider account.
         */
        return new MigrationGetResponseBody()
                        .withSubscriberID(null);
    }

    @Override
    public BillPayEnrolmentPostResponseBody enrol(Header header, String user) {
        SubscriberEnrollRequest request = createEnrolmentRequest(header);
        LOGGER.debug("Request: {}", request);
        SubscriberEnrollResponse response = client.call(request, SOAP_ACTION);
        LOGGER.debug("Received response: {}", response);
        return new BillPayEnrolmentPostResponseBody()
                        .withId(request.getHeader().getSubscriberId());
    }
    
    /**
     * This method creates a sample enrolment request.  Typically this service would integrate with the bank to
     * retrieve the customer and bank data.
     * @return
     */
    private SubscriberEnrollRequest createEnrolmentRequest(Header header) {
        
        // Set the bank designated Bill Pay subscriber ID, note Fiserv advise this should NOT be a social
        // security number.
        header.setSubscriberId("UniqueCustomerNumber"); 
        
        return SubscriberEnrollRequest.builder()
            .header(header)
            .subscriberInformation(SubscriberInformation.builder()
                                                        .birthDate(BldrDate.builder()
                                                                           .date("")
                                                                           .build())
                                                        .dayPhone("0123456789")
                                                        .emailAddress("me@bankA.com")
                                                        .name(IndividualName.builder()
                                                                            .first("firstName")
                                                                            .last("lastName")
                                                                            .build())
                                                        .taxId("0001")
                                                        .usAddress(UsAddress.builder()
                                                                            .address1("address1")
                                                                            .address2("address2")
                                                                            .city("Miami")
                                                                            .state("FL")
                                                                            .zip5("90210")
                                                                            .build())
                                                        .build())
            .bankAccountInformation(BankAccountInformation.builder()
                                                          .accountId(BankAccountId.builder()
                                                                                  .accountNumber("1234")
                                                                                  .accountType(BankAccountType.DDA)
                                                                                  .routingTransitNumber("4321")
                                                                                  .build())
                                                          .build())
            .build();
    }
}