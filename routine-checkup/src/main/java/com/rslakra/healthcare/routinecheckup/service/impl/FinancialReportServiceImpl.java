package com.rslakra.healthcare.routinecheckup.service.impl;

import com.rslakra.healthcare.routinecheckup.dto.response.DoctorPaymentDto;
import com.rslakra.healthcare.routinecheckup.service.DoctorService;
import com.rslakra.healthcare.routinecheckup.service.FinancialReportService;
import com.rslakra.healthcare.routinecheckup.service.XPathService;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.FileStorageConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;


/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:18 PM
 */
@Service
@RequiredArgsConstructor
public class FinancialReportServiceImpl implements FinancialReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinancialReportService.class);
    private static final String GET_FINANCIAL_BY_DOCTOR_ID_EXPRESSION_TEMPLATE
            = "/salary/staff/doctors/doc[@doc_id=\"%s\"]/payment";

    private final XPathService xPathService;
    private final DoctorService doctorService;
    private final FileStorageConstants fileStorageConstants;


    @Override
    public DoctorPaymentDto getCurrentMonthPayment(String userLogin, String doctorId) {
        String doctorIdStr = xPathService.sanitizeExpressionParam(doctorId);
        String formattedExpr = String.format(GET_FINANCIAL_BY_DOCTOR_ID_EXPRESSION_TEMPLATE, doctorIdStr);
        String financialReportPath = fileStorageConstants.getFinancialReportPath();

        NodeList nodeList;
        try {
            nodeList = xPathService.evaluateXpathExpression(formattedExpr, financialReportPath);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }

        if (nodeList.getLength() == 0) {
            return null;
        }

        Node item = nodeList.item(0);
        String nodeValue = item.getTextContent();
        int payment;
        try {
            payment = Integer.parseInt(nodeValue);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }

        DoctorPaymentDto result = new DoctorPaymentDto(doctorId, payment);
        return result;
    }

}
