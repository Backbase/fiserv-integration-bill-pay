package com.backbase.billpay.fiserv.payments;

import static com.backbase.billpay.fiserv.utils.FiservUtils.toFiservDate;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payments.model.PaymentAddRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentAddResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentCancelRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentDetailRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentDetailResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentFilter;
import com.backbase.billpay.fiserv.payments.model.PaymentFilter.PaymentStatusFilter;
import com.backbase.billpay.fiserv.payments.model.PaymentListRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentListResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentModifyRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentModifyResponse;
import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.OneOffPayment;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutResponseBody;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentsServiceImpl implements PaymentsService {
    
    public static final int POSITIVE_MAX_DAYS = 360;
    public static final int NEGATIVE_MAX_DAYS = -10000;
    public static final int DEFAULT_FROM = 0;
    public static final int DEFAULT_SIZE = 1000;
    
    private static final String PAYMENT_DETAIL_ACTION = "PaymentDetail";
    private static final String PAYMENT_ADD_ACTION = "PaymentAdd";
    private static final String PAYMENT_MODIFY_ACTION = "PaymentModify";
    private static final String PAYMENT_CANCEL_ACTION = "PaymentCancel";
    private static final String PAYMENT_LIST_ACTION = "PaymentList";
    
    
    private final PaymentsMapper mapper;
    private final PaymentMapper paymentMapper;
    private final FiservClient client;
    
    @Autowired
    public PaymentsServiceImpl(PaymentsMapper mapper, PaymentMapper paymentMapper, FiservClient client) {
        this.mapper = mapper;
        this.paymentMapper = paymentMapper;
        this.client = client;
    }

    @Override
    public void deletePaymentById(Header header, String id) {
        PaymentCancelRequest cancelRequest = PaymentCancelRequest.builder()
                                                                 .header(header)
                                                                 .paymentId(id)
                                                                 .build();
        client.call(cancelRequest, PAYMENT_CANCEL_ACTION);
    }

    @Override
    public BillPayPaymentsGetResponseBody getBillPayPayments(Header header, String status, Date startDate,
                    Date endDate, String payeeId, Integer from, Integer size) {

        int numberOfDays;
        Date calculatedStartDate = startDate;
        if (calculatedStartDate == null && endDate == null) {
            calculatedStartDate = Date.from(ZonedDateTime.now().plusDays(POSITIVE_MAX_DAYS).toInstant());
            numberOfDays = NEGATIVE_MAX_DAYS;
        } else if (calculatedStartDate == null) {
            calculatedStartDate = endDate;
            numberOfDays = NEGATIVE_MAX_DAYS;
        } else if (endDate == null) {
            numberOfDays = POSITIVE_MAX_DAYS;
        } else {
            numberOfDays = (int) ChronoUnit.DAYS.between(calculatedStartDate.toInstant(), endDate.toInstant());
        }
        
        
        BldrDate bldrStartDate = toFiservDate(calculatedStartDate);
        PaymentListResponse response = client.call(PaymentListRequest.builder()
                                              .filter(PaymentFilter.builder()
                                                                   .numberOfDays(numberOfDays)
                                                                   .startingPaymentDate(bldrStartDate)
                                                                   .statusFilter(PaymentStatusFilter.valueOf(status))
                                                                   .build())
                                              .header(header)
                                              .build(), PAYMENT_LIST_ACTION);
    
        // TODO implement pagination
        
        return mapper.map(response);
    }

    @Override
    public BillPayPaymentsPostResponseBody postBillPayPayments(Header header, BillPayPaymentsPostRequestBody request) {
        /**
         * Assuming only 1 payment is requested matching the new design of the product widgets.
         */
        PaymentAddRequest addRequest = mapper.toPaymentAddRequest(request);
        addRequest.setHeader(header);
        PaymentAddResponse addResponse = client.call(addRequest, PAYMENT_ADD_ACTION);
        return paymentMapper.toBillPayPaymentsPostResponseBody(request, addResponse);
    }

    @Override
    public PaymentByIdGetResponseBody getPaymentById(Header header, String id) {
        PaymentDetailRequest request = PaymentDetailRequest.builder()
                                                           .header(header)
                                                           .paymentId(id)
                                                           .build();
        PaymentDetailResponse response = client.call(request, PAYMENT_DETAIL_ACTION);
        OneOffPayment payment = mapper.toOneOffPayment(response.getPaymentDetailResult().get(0));
        return new PaymentByIdGetResponseBody().withPayment(payment);
    }

    @Override
    public PaymentByIdPutResponseBody putBillPayPayments(Header header, String id, PaymentByIdPutRequestBody request) {
        PaymentModifyRequest fiservRequest = mapper.toPaymentModifyRequest(id, request);
        fiservRequest.setHeader(header);
        PaymentModifyResponse fiservResponse = client.call(fiservRequest, PAYMENT_MODIFY_ACTION);
        return mapper.toPaymentByIdPutResponseBody(id, fiservResponse);
    }

    @Override
    public RecurringPaymentByIdGetResponseBody getRecurringPaymentById(Header header, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BillPayRecurringPaymentsPostResponseBody postBillPayRecurringPayments(
                    Header header, BillPayRecurringPaymentsPostRequestBody request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteRecurringPaymentById(Header header, String id) {
        deletePaymentById(header, id);
    }

    @Override
    public RecurringPaymentByIdPutResponseBody putBillPayRecurringPayments(Header header, String id,
                    RecurringPaymentByIdPutRequestBody request) {
        // TODO Auto-generated method stub
        return null;
    }
}