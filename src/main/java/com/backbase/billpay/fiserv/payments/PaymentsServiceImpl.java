package com.backbase.billpay.fiserv.payments;

import static com.backbase.billpay.fiserv.utils.FiservUtils.fromFiservDate;
import static com.backbase.billpay.fiserv.utils.FiservUtils.toFiservDate;
import static com.backbase.billpay.fiserv.utils.PaginationUtils.paginateList;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payments.model.Payment;
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
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModel;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelAddRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelAddResponse;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelCancelRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelListRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelListResponse;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelModifyRequest;
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
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentsServiceImpl implements PaymentsService {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentsServiceImpl.class);
    
    protected static final int POSITIVE_MAX_DAYS = 360;
    protected static final int NEGATIVE_MAX_DAYS = -10000;
    private static final String PAYMENT_DETAIL_ACTION = "PaymentDetail";
    private static final String PAYMENT_ADD_ACTION = "PaymentAdd";
    private static final String PAYMENT_MODIFY_ACTION = "PaymentModify";
    private static final String PAYMENT_CANCEL_ACTION = "PaymentCancel";
    public static final String PAYMENT_LIST_ACTION = "PaymentList";
    private static final String RECURRING_ADD_ACTION = "RecurringModelAdd";
    private static final String RECURRING_MODIFY_ACTION = "RecurringModelModify";
    private static final String RECURRING_CANCEL_ACTION = "RecurringModelCancel";
    private static final String RECURRING_LIST_ACTION = "RecurringModelList";
    private static final BillPayPaymentsGetResponseBody EMPTY_PAYMENT_RESPONSE = new BillPayPaymentsGetResponseBody()
                                                                                    .withTotalCount(Long.valueOf(0));
    private static final String DIRECTION_ASC = "ASC";
    private static final String ORDER_BY_AMOUNT = "amount";
    
    private final PaymentsMapper mapper;
    private final PaymentMapper paymentMapper;
    private final FiservClient client;
    
    /**
     * Constructor for PaymentsServiceImpl.
     * @param mapper Maps between payment objects
     * @param paymentMapper Maps between payment objects
     * @param client Client to communicate with the provider
     */
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
                    Date endDate, String payeeId, Integer from, Integer size, String orderBy, String direction) {

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
        
        // filter payments by payee ID if supplied
        List<Payment> filteredPayments = StringUtils.isEmpty(payeeId) ? response.getPayments()
                        : response.getPayments().stream().filter(
                            payment -> String.valueOf(payment.getPayee().getPayeeId()).equals(payeeId))
                                        .collect(Collectors.toList());

        // return empty response if no payment found
        if (filteredPayments.isEmpty()) {
            return EMPTY_PAYMENT_RESPONSE;
        }
        
        sortPayments(filteredPayments, orderBy, direction);

        // paginate the data
        List<Payment> payments = paginateList(filteredPayments, from, size);
        response.setPayments(payments);
        return mapper.map(response)
                     .withTotalCount(Long.valueOf(filteredPayments.size()));
    }
    
    private void sortPayments(List<Payment> payments, String orderBy, String direction) {
        log.debug("Sorting payments by field: {}, direction: {}", orderBy, direction);
        Comparator<Payment> comparator;
        if (ORDER_BY_AMOUNT.equals(orderBy)) {
            comparator = (p1, p2) -> p2.getAmount().compareTo(p1.getAmount());
        } else {
            comparator = (p1, p2) -> fromFiservDate(p2.getPaymentDate()).compareTo(fromFiservDate(p1.getPaymentDate()));
        }
        
        if (DIRECTION_ASC.equals(direction)) {
            payments.sort(comparator.reversed());
        } else {
            payments.sort(comparator);
        }
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
        client.call(fiservRequest, PAYMENT_MODIFY_ACTION);
        return new PaymentByIdPutResponseBody().withId(id);
    }

    @Override
    public RecurringPaymentByIdGetResponseBody getRecurringPaymentById(Header header, String id) {
        RecurringModelListResponse response = client.call(RecurringModelListRequest.builder()
                                                    .header(header)
                                                    .build(), RECURRING_LIST_ACTION);
        Optional<RecurringModel> recurringPayment = 
                        response.getRecurringPayments().stream()
                        .filter(payment -> StringUtils.equals(id, payment.getRecurringModelId()))
                        .findFirst();
        if (recurringPayment.isPresent()) {
            return mapper.map(recurringPayment.get());
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public BillPayRecurringPaymentsPostResponseBody postBillPayRecurringPayments(
                    Header header, BillPayRecurringPaymentsPostRequestBody request) {
        RecurringModelAddRequest addRequest = mapper.toRecurringModelAddRequest(request);
        addRequest.setHeader(header);
        RecurringModelAddResponse addResponse = client.call(addRequest, RECURRING_ADD_ACTION);
        return new BillPayRecurringPaymentsPostResponseBody()
                        .withId(addResponse.getRecurringModelId());
    }

    @Override
    public void deleteRecurringPaymentById(Header header, String id) {
        RecurringModelCancelRequest cancelRequest = RecurringModelCancelRequest.builder()
                                                        .header(header)
                                                        .recurringModelId(id)
                                                        .cancelPendingPayments(true)
                                                        .build();
        client.call(cancelRequest, RECURRING_CANCEL_ACTION);
    }

    @Override
    public RecurringPaymentByIdPutResponseBody putBillPayRecurringPayments(Header header, String id,
                    RecurringPaymentByIdPutRequestBody request) {
        RecurringModelModifyRequest modifyRequest = mapper.toRecurringModelModifyRequest(id, request);
        modifyRequest.setHeader(header);
        client.call(modifyRequest, RECURRING_MODIFY_ACTION);
        return new RecurringPaymentByIdPutResponseBody().withId(id);
    }
}