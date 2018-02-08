package ru.masterdm.spo.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.masterdm.spo.service.DictService;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.service.IPriceService;
import ru.masterdm.spo.service.IReporterService;
import ru.masterdm.spo.service.IStandardPeriodService;

import ru.md.persistence.CompendiumMapper;
import ru.md.persistence.CurrencyMapper;
import ru.md.persistence.DashboardMapper;
import ru.md.persistence.DepartmentMapper;
import ru.md.persistence.MdTaskMapper;
import ru.md.persistence.PlaceClientRecordMapper;
import ru.md.persistence.PlaceHistoryMapper;
import ru.md.persistence.DepartmentHistoryMapper;
import ru.md.persistence.PupMapper;

public class SBeanLocator {

    private static volatile SBeanLocator instance;
    private final ConfigurableApplicationContext context;

    public static SBeanLocator singleton() {
        SBeanLocator localInstance = instance;
        if (localInstance == null) {
            synchronized (SBeanLocator.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SBeanLocator();
                }
            }
        }
        return localInstance;
    }

    private SBeanLocator() {
		super();
		context = new ClassPathXmlApplicationContext(new String[] {"classpath:light-spring-servlet.xml"});
	}

    public Object getBean(String name) throws BeansException {
        return context.getBean(name);
    }

    public CurrencyMapper getCurrencyMapper() throws BeansException {
        return (CurrencyMapper) getBean("currencyMapper");
    }

    public DepartmentMapper getDepartmentMapper() throws BeansException {
        return (DepartmentMapper) getBean("departmentMapper");
    }

    public PlaceClientRecordMapper getPlaceClientRecordMapper() throws BeansException {
        return (PlaceClientRecordMapper) getBean("placeClientRecordMapper");
    }

    public PlaceHistoryMapper getPlaceHistoryMapper() throws BeansException {
        return (PlaceHistoryMapper) getBean("placeHistoryMapper");
    }

    public DepartmentHistoryMapper getDepartmentHistoryMapper() throws BeansException {
        return (DepartmentHistoryMapper) getBean("departmentHistoryMapper");
    }

    public CompendiumMapper getCompendiumMapper() throws BeansException {
        return (CompendiumMapper) getBean("compendiumMapper");
    }

    public IStandardPeriodService getStandardPeriodService() throws BeansException {
        return (IStandardPeriodService) getBean("standardPeriodService");
    }

    public DictService getDictService() throws BeansException {
        return (DictService) getBean("dictService");
    }

    public MdTaskMapper mdTaskMapper() throws BeansException {
        return (MdTaskMapper) getBean("mdTaskMapper");
    }

    public CompendiumMapper compendium() throws BeansException {
        return getCompendiumMapper();
    }

    public PupMapper getPupMapper() throws BeansException {
        return context.getBean(PupMapper.class);
    }

    public static IDashboardService getDashboardService() {
        return (IDashboardService) singleton().getBean("dashboardService");
    }
    public static IPriceService getPriceService() {
        return (IPriceService) singleton().getBean("priceService");
    }
    public static DashboardMapper getDashboardMapper() {
        return (DashboardMapper) singleton().getBean("dashboardMapper");
    }

    public IReporterService getReporterService() throws BeansException {
		return (IReporterService) getBean("reporterService");
	}
}
