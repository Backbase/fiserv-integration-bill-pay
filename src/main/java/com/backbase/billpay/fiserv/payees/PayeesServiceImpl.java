package com.backbase.billpay.fiserv.payees;

import static com.backbase.billpay.fiserv.autopay.AutopayServiceImpl.AUTOPAY_LIST_ACTION;
import static com.backbase.billpay.fiserv.payeessummary.PayeesSummaryServiceImpl.EBILL_LIST_ACTION;

import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayFilter;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResponse;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResultInfo;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.PayeeAddRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeAddResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeCancelRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeListRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeListResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeModifyRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeModifyResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill.EbillStatus;
import com.backbase.billpay.fiserv.payeessummary.model.EbillFilter;
import com.backbase.billpay.fiserv.payeessummary.model.EbillFilter.BillTypeFilter;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListRequest;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListResponse;
import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.billpay.fiserv.utils.FiservUtils;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Payee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutResponseBody;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import java.util.Optional;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Payees service, integrates with the Bill Pay provider.
 */
@Service
public class PayeesServiceImpl implements PayeesService {
    
    public static final String PAYEE_LIST_ACTION = "PayeeList";
    private static final String PAYEE_ADD_ACTION = "PayeeAdd";
    private static final String PAYEE_MODIFY_ACTION = "PayeeModify";
    private static final String PAYEE_CANCEL_ACTION = "PayeeCancel";
    
    private PayeesMapper mapper;
    private FiservClient client;
    
    @Autowired
    public PayeesServiceImpl(PayeesMapper mapper, FiservClient client) {
        this.mapper = mapper;
        this.client = client;
    }
    
    @Override
    public BillPayPayeesPostResponseBody postBillPayPayees(Header header, BillPayPayeesPostRequestBody request) {
        PayeeAddRequest addRequest = mapper.toPayeeAddRequest(request);
        addRequest.setHeader(header);
        PayeeAddResponse addResponse = client.call(addRequest, PAYEE_ADD_ACTION);
        return mapper.toBillPayPayeesPostResponseBody(addResponse);
    }

    @Override
    public PayeeByIdGetResponseBody getBillPayPayee(Header header, String id) {
        PayeeSummary payeeSummary = getPayee(header, id);
        Payee payee = mapper.toPayee(payeeSummary);
        return new PayeeByIdGetResponseBody().withPayee(payee);
    }

    @Override
    public PayeeByIdPutResponseBody putBillPayPayees(Header header, String id, PayeeByIdPutRequestBody request) {
        PayeeModifyRequest modifyRequest = mapper.toPayeeModifyRequest(request);
        modifyRequest.setPayeeId(Long.valueOf(id));
        modifyRequest.setHeader(header);
        PayeeModifyResponse modifyResponse = client.call(modifyRequest, PAYEE_MODIFY_ACTION);
        return mapper.toPayeeByIdPutResponseBody(modifyResponse);
    }

    @Override
    public void deletePayeeById(Header header, String id, Boolean cancelPendingPayments) {
        PayeeCancelRequest cancelRequest = PayeeCancelRequest.builder()
                                                             .header(header)
                                                             .payeeId(Long.valueOf(id))
                                                             .cancelPayments(cancelPendingPayments)
                                                             .build();
        client.call(cancelRequest, PAYEE_CANCEL_ACTION);
    }

    @Override
    public BillPayElectronicPayeesPostResponseBody postBillPayElectronicPayees(
                    Header header, BillPayElectronicPayeesPostRequestBody request) {
        PayeeAddRequest addRequest = mapper.toPayeeAddRequest(request);
        addRequest.setHeader(header);
        PayeeAddResponse addResponse = client.call(addRequest, PAYEE_ADD_ACTION);
        return mapper.toBillPayElectronicPayeesPostResponseBody(addResponse);
    }

    @Override
    public ElectronicPayeeByIdGetResponseBody getBillPayElectronicPayee(Header header, String id) {
        PayeeSummary payeeSummary = getPayee(header, id);

        //Ebill
        EbillFilter ebillFilter = EbillFilter.builder()
                        .billType(BillTypeFilter.BILLER)
                        .startingDate(FiservUtils.todayFiservDate())
                        .numberOfDays(Integer.valueOf(100))
                        .build();
        EbillListRequest ebillRequest = EbillListRequest.builder()
                        .filter(ebillFilter)
                        .header(header)
                        .build();
        EbillListResponse ebillResponse = client.call(ebillRequest, EBILL_LIST_ACTION);
        Ebill ebill = ebillResponse.getEbillList().stream()
                                        .filter(e -> e.getStatus() == EbillStatus.UNPAID)
                                        .filter(e -> e.getPayee().getPayeeId().equals(Long.valueOf(id)))
                                        .findFirst().orElse(null);
        //Autopay
        EbillAutoPayFilter autoPayFilter = EbillAutoPayFilter.builder().payeeId(Long.valueOf(id)).build();
        EbillAutoPayListRequest listRequest = EbillAutoPayListRequest.builder()
                        .filter(autoPayFilter)
                        .header(header)
                        .build();
        EbillAutoPayListResponse autopayResponse = client.call(listRequest, AUTOPAY_LIST_ACTION);
        EbillAutoPayListResultInfo autopay =
                        autopayResponse.getEbillAutoPayList().stream()
                                        .filter(a -> a.getPayeeId().equals(Long.valueOf(id)))
                                        .findFirst().orElse(null);

        ElectronicPayee payee = mapper.toElectronicPayee(ebill, autopay, payeeSummary);
        return new ElectronicPayeeByIdGetResponseBody().withPayee(payee);
    }

    @Override
    public ElectronicPayeeByIdPutResponseBody putBillPayElectronicPayees(Header header, String id,
                    ElectronicPayeeByIdPutRequestBody request) {
        PayeeModifyRequest modifyRequest = mapper.toPayeeModifyRequest(request);
        modifyRequest.setHeader(header);
        modifyRequest.setPayeeId(Long.valueOf(id));
        PayeeModifyResponse modifyResponse = client.call(modifyRequest, PAYEE_MODIFY_ACTION);
        return mapper.toElectronicPayeeByIdPutResponseBody(modifyResponse);
    }

    @Override
    public void deleteElectronicPayeeById(Header header, String id, Boolean cancelPendingPayments) {
        PayeeCancelRequest cancelRequest = PayeeCancelRequest.builder()
                                                             .header(header)
                                                             .cancelPayments(cancelPendingPayments)
                                                             .payeeId(Long.valueOf(id))
                                                             .build();
        client.call(cancelRequest, PAYEE_CANCEL_ACTION);
    }
    
    private PayeeSummary getPayee(Header header, String id) {
        // TODO filter by manual / electronic
        PayeeListRequest listRequest = PayeeListRequest.builder().header(header).build();
        PayeeListResponse listResponse = client.call(listRequest, PAYEE_LIST_ACTION);
        Optional<PayeeSummary> optionalPayee = 
                        listResponse.getPayees()
                                    .stream()
                                    .filter(p -> StringUtils.equals(id, String.valueOf(p.getPayeeId())))
                                    .findFirst();
        if (optionalPayee.isPresent()) {
            return optionalPayee.get();
        } else {
            throw new NotFoundException().withMessage("Resource not found");
        }
    }
}