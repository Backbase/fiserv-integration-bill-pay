package com.backbase.billpay.fiserv.utils;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.buildingblocks.backend.api.utils.ApiUtils;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import com.backbase.buildingblocks.backend.internalrequest.InternalRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class for JMS consumers.
 */
public abstract class AbstractListener {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private FiservUtils fiservUtils;

    public AbstractListener() {
        super();
    }
    
    /**
     * Utility method for creating proxy wrapper classes.
     * @param request proxy wrapper request.
     * @param data request data.
     * @return updated request proxy wrapper.
     */
    protected <T> RequestProxyWrapper<T> createRequestProxyWrapper(RequestProxyWrapper<?> request, T data) {
        RequestProxyWrapper<T> resultWrapper = new RequestProxyWrapper<>();
        ApiUtils.copyRequestProxyWrapperValues(request, resultWrapper);
        resultWrapper.setRequest(new InternalRequest<>());
        resultWrapper.getRequest().setInternalRequestContext(request.getRequest().getInternalRequestContext());
        resultWrapper.getRequest().setData(data);
        return resultWrapper;
    }
    
    /**
     * Creates a Fiserv header.
     * @param request wrapper request.
     * @param subscriberId subscriber id
     * @return
     */
    protected Header createFiservHeader(RequestProxyWrapper<?> request, String subscriberId) {
        return fiservUtils.createHeader(request, subscriberId);
    }

}