package com.backbase.billpay.fiserv.payeessummary;

import static com.backbase.billpay.fiserv.autopay.AutopayServiceImpl.AUTOPAY_LIST_ACTION;
import static com.backbase.billpay.fiserv.payees.PayeesServiceImpl.PAYEE_LIST_ACTION;
import static com.backbase.billpay.fiserv.payments.PaymentsServiceImpl.PAYMENT_LIST_ACTION;
import static com.backbase.billpay.fiserv.utils.FiservUtils.fromFiservDate;

import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResponse;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResultInfo;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.PayeeListRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeListResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill.EbillStatus;
import com.backbase.billpay.fiserv.payeessummary.model.EbillFilter;
import com.backbase.billpay.fiserv.payeessummary.model.EbillFilter.BillTypeFilter;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListRequest;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListResponse;
import com.backbase.billpay.fiserv.payments.model.Payment;
import com.backbase.billpay.fiserv.payments.model.PaymentFilter;
import com.backbase.billpay.fiserv.payments.model.PaymentFilter.PaymentStatusFilter;
import com.backbase.billpay.fiserv.payments.model.PaymentListRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentListResponse;
import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.billpay.fiserv.utils.FiservUtils;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Payees Summary service, integrates with the Bill Pay provider.
 */
@Service
public class PayeesSummaryServiceImpl implements PayeesSummaryService {

    public static final String EBILL_LIST_ACTION = "EbillList";

    private PayeesSummaryMapper mapper;

    private FiservClient client;

    @Autowired
    public PayeesSummaryServiceImpl(PayeesSummaryMapper mapper, FiservClient client) {
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public BillPayPayeesSummaryGetResponseBody getBillPayPayeesSummary(Header header) {

        PayeeListRequest payeeRequest = PayeeListRequest.builder().header(header).build();
        PayeeListResponse payeeResponse = client.call(payeeRequest, PAYEE_LIST_ACTION);

        //Ebill
        EbillFilter ebillFilter = EbillFilter.builder()
                        .billType(BillTypeFilter.ALL)
                        .startingDate(FiservUtils.todayFiservDate())
                        .numberOfDays(Integer.valueOf(100))
                        .build();
        EbillListRequest ebillRequest = EbillListRequest.builder().header(header).filter(ebillFilter).build();
        EbillListResponse ebillResponse = client.call(ebillRequest, EBILL_LIST_ACTION);
        Map<Long, List<Ebill>> ebillMap =
                        ebillResponse.getEbillList().stream().filter(e -> e.getStatus() == EbillStatus.UNPAID)
                                        .collect(Collectors.groupingBy(ebill -> ebill.getPayee().getPayeeId()));
        //Autopay
        EbillAutoPayListRequest listRequest = EbillAutoPayListRequest.builder()
                        .header(header)
                        .build();
        EbillAutoPayListResponse autopayResponse = client.call(listRequest, AUTOPAY_LIST_ACTION);
        Map<Long, List<EbillAutoPayListResultInfo>> autopayMap =
                        autopayResponse.getEbillAutoPayList().stream()
                                        .collect(Collectors.groupingBy(EbillAutoPayListResultInfo::getPayeeId));
        //Payments
        PaymentFilter paymentFilter = PaymentFilter.builder()
                        .statusFilter(PaymentStatusFilter.PENDING)
                        .startingPaymentDate(FiservUtils.todayFiservDate())
                        .numberOfDays(Integer.valueOf(100))
                        .build();
        PaymentListRequest paymentRequest = PaymentListRequest.builder().header(header).filter(paymentFilter).build();
        PaymentListResponse paymentResponse = client.call(paymentRequest, PAYMENT_LIST_ACTION);
        Map<Long, List<Payment>> paymentMap = paymentResponse.getPayments().stream()
                        .collect(Collectors.groupingBy(payment -> payment.getPayee().getPayeeId()));

        List<com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PayeeSummary> summaries =
                        new ArrayList<>();
        if (payeeResponse.getPayees() != null) {
            for (PayeeSummary summary : payeeResponse.getPayees()) {
                Long payeeId = summary.getPayeeId();
                Ebill ebill = getLatestEbill(ebillMap.get(payeeId));
                EbillAutoPayListResultInfo autopay = getAutopay(autopayMap.get(payeeId));
                Payment payment = getNextPayment(paymentMap.get(payeeId));
                summaries.add(mapper.toPayeeSummary(summary, ebill, autopay, payment));
            }
        }

        return new BillPayPayeesSummaryGetResponseBody().withPayeeSummaries(summaries);
    }

    private Payment getNextPayment(List<Payment> payments) {
        if (payments != null) {
            Optional<Payment> optionalPayment =
                            payments.stream()
                                            .sorted(Comparator.comparing(payment -> fromFiservDate(
                                                            payment.getPaymentDate())))
                                            .findFirst();
            return optionalPayment.orElse(null);

        }
        return null;
    }

    private EbillAutoPayListResultInfo getAutopay(List<EbillAutoPayListResultInfo> autopays) {
        if (autopays != null) {
            Optional<EbillAutoPayListResultInfo> optionalAutopay = autopays.stream().findFirst();
            return optionalAutopay.orElse(null);
        }
        return null;
    }

    private Ebill getLatestEbill(List<Ebill> ebills) {
        if (ebills != null) {
            Optional<Ebill> optionalEbill = ebills.stream().sorted(Comparator.comparing(Ebill::getDueDate))
                            .findFirst();
            return optionalEbill.orElse(null);
        }
        return null;
    }
}
