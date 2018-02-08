package ru.masterdm.spo.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.masterdm.spo.utils.Formatter;

@Component
public class DashScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashScheduler.class);
    @Autowired
    private IDashboardService dashboardService;

    @Scheduled(fixedDelay = 18000000) //раз в 5 часов
    public void recalculateOldTasks() {
        LOGGER.info("The time is now {}", Formatter.formatDateTime(new Date()));
        dashboardService.recalculateOldTasks();
    }
}
