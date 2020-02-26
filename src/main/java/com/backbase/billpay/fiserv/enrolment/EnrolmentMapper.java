package com.backbase.billpay.fiserv.enrolment;

import com.backbase.billpay.integration.enrolment.PhoneNumber;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberInfoResponse;
import com.backbase.billpay.integration.enrolment.Subscriber;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.UserByIdGetResponseBody;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

/**
 * Map between Backbase and Fiserv enrolment objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EnrolmentMapper {
    
    @Mappings({
        @Mapping(target = "user", ignore = true),
        @Mapping(target = "subscriberId", ignore = true),
        @Mapping(target = "type", ignore = true),
        @Mapping(target = "prefix", source = "subscriberInformation.name.prefix"),
        @Mapping(target = "middleNameOrInitial", source = "subscriberInformation.name.middle"),
        @Mapping(target = "lastName", source = "subscriberInformation.name.last"),
        @Mapping(target = "suffix", ignore = true),
        @Mapping(target = "taxIdentifier", source = "subscriberInformation.taxId"),
        @Mapping(target = "email", source = "subscriberInformation.emailAddress"),
        @Mapping(target = "address.address1", source = "subscriberInformation.usAddress.address1"),
        @Mapping(target = "address.address2", source = "subscriberInformation.usAddress.address2"),
        @Mapping(target = "address.city", source = "subscriberInformation.usAddress.city"),
        @Mapping(target = "address.state", source = "subscriberInformation.usAddress.state"),
        @Mapping(target = "address.postalCode", source = "subscriberInformation.usAddress.zip5"),
        @Mapping(target = "address.country", ignore = true),
        @Mapping(target = "address.additions", ignore = true),
        @Mapping(target = "phoneNumbers", source = "subscriberInformation.dayPhone"),
        @Mapping(target = "accounts", ignore = true),
        @Mapping(target = "business", ignore = true),
        @Mapping(target = "language", ignore = true),
        @Mapping(target = "additions", ignore = true),
        @Mapping(target = "firstName", source = "subscriberInformation.name.first")
    })
    Subscriber toSubscriber(SubscriberInfoResponse subscriberInfoResponse);
    
    /**
     * Map the phone number to the phone numbers list.
     * @param dayPhone daytime phone number.
     * @return list of PhoneNumbers.
     */
    default List<PhoneNumber> mapPhoneNumber(String dayPhone) {
        
        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        phoneNumbers.add(new PhoneNumber().withNumber(dayPhone));
        return phoneNumbers;
        
    }
}
