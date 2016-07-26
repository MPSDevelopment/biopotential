package com.mpsdevelopment.biopotential.server.handler;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandlerImpl extends AccessDeniedHandlerImpl {

    private static final Logger LOGGER = LoggerUtil.getLogger(CustomAccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOGGER.info("AccessDeniedException in call %s", request.getRequestURI());
        super.handle(request, response, accessDeniedException);

    }

}
