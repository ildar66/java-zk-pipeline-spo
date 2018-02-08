package ru.md.report.utils;

import org.apache.commons.codec.binary.Base64;
import ru.masterdm.integration.compendium.CompendiumService;
import ru.masterdm.spo.utils.Formatter;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Helper class that converts Task data to parameters for report building
 * Created by Andrey Pavlenko
 */
public class ReportHelper {
    protected final static Logger LOGGER = Logger.getLogger(ReportHelper.class.getName());

    public static byte[] generateAcceptReport(String taskNumber, String userName, Date dateOfAccept, byte[] srcFiledata) throws Exception {
        LOGGER.info("generateAcceptReport start");
        return Base64.decodeBase64(ru.masterdm.integration.ServiceFactory.getService(CompendiumService.class).
                generateAcceptReport(taskNumber, userName, Formatter.format(dateOfAccept),
                        Base64.encodeBase64String(srcFiledata)));
    }

}