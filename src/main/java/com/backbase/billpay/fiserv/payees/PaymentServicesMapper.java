package com.backbase.billpay.fiserv.payees;

import static com.backbase.billpay.fiserv.utils.FiservUtils.fromFiservDate;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary;
import com.backbase.billpay.fiserv.payees.model.PaymentServices;
import com.backbase.billpay.fiserv.payees.model.PaymentServices.PaymentServiceType;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PaymentService;
import com.backbase.rest.spec.common.types.Currency;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PaymentServicesMapper {
    
    private static final String CURRENCY = "USD";

    public List<PaymentService> toPaymentServices(PayeeSummary source) {
        ArrayList<PaymentService> paymentServices = new ArrayList<>();
        PaymentService regularPaymentService = new PaymentService()
                                                    .withCutoffTime(source.getCutoffTime())
                                                    .withEarliestPaymentDate(fromFiservDate(source.getEarliestPaymentDate()))
                                                    .withNextPaymentDate(fromFiservDate(source.getNextPaymentDate()))
                                                    .withDeliveryDays(source.getLeadDays())
                                                    .withPaymentServiceType("REGULAR_PAYMENT");
        paymentServices.add(regularPaymentService);
        
        for (PaymentServices service : source.getPaymentServices()) {
            PaymentService paymentService = new PaymentService()
                            .withCutoffTime(service.getCutOffTime())
                            .withEarliestPaymentDate(fromFiservDate(service.getEarliestDate()))
                            .withNextPaymentDate(fromFiservDate(service.getNextDate()))
                            .withDeliveryDays(source.getLeadDays())
                            .withPaymentFee(service.getFee())
                            .withFee(new Currency().withAmount(service.getFee()).withCurrencyCode(CURRENCY));
            if (service.getPaymentService() == PaymentServiceType.OVERNIGHT_CHECK) {
                paymentService.setPaymentServiceType("OVERNIGHT_CHECK");
            } else if (service.getPaymentService() == PaymentServiceType.EXPEDITED_PAYMENT) {
                paymentService.setPaymentServiceType("EXPEDITED_PAYMENT");
            }
            paymentServices.add(paymentService);
        }
        return paymentServices;
    }
}