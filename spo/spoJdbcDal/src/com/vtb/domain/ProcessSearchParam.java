package com.vtb.domain;

import com.vtb.util.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ProcessSearchParam implements Serializable {
    private static final long serialVersionUID = 1L;


    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessSearchParam.class);

    public boolean parseError = false;
    private String number;
    private boolean projectTeam = false;
    private boolean expertTeam = false;
    private boolean paused = false;
    private boolean favorite;
    private boolean closed;
    private Integer ProcessTypeID;
    private String currency;
    private String search;//универсальный поиск
    private BigDecimal sumFrom;
    private BigDecimal sumTo;
    private String initDepartment;
    private String currOperation;
    private String priority;
    private String status;
    private String type;
    private String contractor;
    private String executor;   // user, who executes the current operation
    private Long executorId;   // id of the user, who executes the current operation
    private boolean hideAssigned = false;// hide assigned tasks from list
    private boolean hideApproved = false;// hide assigned tasks from list
    private boolean showOnlyLastVersionTask = false;//show only last version mdtask
    private String showImportOnly;

    private String currentList = "clientRefuse";
    private Long pageNumber;

    /**
     * конструктор из реквеста
     *
     * @param req
     * @param closed
     * @return
     */
    public ProcessSearchParam(HttpServletRequest req, boolean closed) {
        try {
            currentList = "clientRefuse";
            if (req.getParameter("typeList") != null) currentList = req.getParameter("typeList");
            if (req.getParameter("closed") != null) currentList += "closed";
            //учитывать параметры гет и куки. Параметры в приоритет.
            setClosed(closed);
            setProjectTeam(req.getParameter("projectteam") != null);
            setExpertTeam(req.getParameter("expertteam") != null);
            setFavorite(req.getParameter("favorite") != null);
            if (isProjectTeam()) setHideApproved(true);
            setPaused(req.getParameter("paused") != null);
            boolean resetFilter = req.getParameter("resetFilter") != null;
            boolean reqExist = false;//есть ли хоть один параметр в реквесте
            if (req.getParameter("searchHideAssigned") != null) {
                setHideAssigned(true);
                reqExist = true;
            }
            if (req.getParameter("searchHideApproved") != null) {
                setHideApproved(req.getParameter("searchHideApproved").equals("y"));
                reqExist = true;
            }
            if (req.getParameter("searchNumber") != null) {
                setNumber(req.getParameter("searchNumber").trim());
                reqExist = true;
            }
            if (req.getParameter("searchProcessType") != null && !req.getParameter("searchProcessType").equals("all")) {
                setProcessTypeID(new Integer(req.getParameter("searchProcessType")));
                reqExist = true;
            }
            if (req.getParameter("searchCurrency") != null && !req.getParameter("searchCurrency").equals("all") && req.getParameter("searchCurrency").length() > 0) {
                setCurrency(req.getParameter("searchCurrency"));
                reqExist = true;
            }
            if (req.getParameter("searchSumFrom") != null && req.getParameter("searchSumFrom").trim().length() > 0) {
                try {
                    setSumFrom(Formatter.parseBigDecimal(req.getParameter("searchSumFrom").trim()));
                } catch (Exception e) {
                    parseError = true;
                }
                reqExist = true;
            }
            if (req.getParameter("searchSumTo") != null && req.getParameter("searchSumTo").trim().length() > 0) {
                try {
                    setSumTo(Formatter.parseBigDecimal(req.getParameter("searchSumTo").trim()));
                } catch (Exception e) {
                    parseError = true;
                }
                reqExist = true;
            }
            if (req.getParameter("searchInitDepartment") != null && req.getParameter("searchInitDepartment").trim().length() > 0) {
                setInitDepartment(req.getParameter("searchInitDepartment").trim());
                reqExist = true;
            }
            if (req.getParameter("searchStatus") != null && req.getParameter("searchStatus").trim().length() > 0) {
                setStatus(req.getParameter("searchStatus").trim());
                reqExist = true;
            }
            if (req.getParameter("searchType") != null && req.getParameter("searchType").trim().length() > 0) {
                setType(req.getParameter("searchType").trim());
                reqExist = true;
            }
            if (req.getParameter("searchContractor") != null && req.getParameter("searchContractor").trim().length() > 0) {
                setContractor(req.getParameter("searchContractor").trim());
                reqExist = true;
            }
            if (req.getParameter("searchPriority") != null && !req.getParameter("searchPriority").equals("all")) {
                setPriority(req.getParameter("searchPriority"));
                reqExist = true;
            }
            if (req.getParameter("searchShowImportOnly") != null) {
                setShowImportOnly(req.getParameter("searchShowImportOnly"));
                reqExist = true;
            }
            if (req.getParameter("searchCurrOperation") != null && req.getParameter("searchCurrOperation").trim().length() > 0) {
                setCurrOperation(req.getParameter("searchCurrOperation").trim());
                reqExist = true;
            }
            if (req.getParameter("searchExecutor") != null && req.getParameter("searchExecutor").trim().length() > 0) {
                setExecutor(req.getParameter("searchExecutor").trim());
                reqExist = true;
            }
            if (req.getParameter("searchExecutorId") != null && req.getParameter("searchExecutorId").trim().length() > 0) {
                try {
                    setExecutorId(Formatter.parseLong(req.getParameter("searchExecutorId").trim()));
                } catch (Exception e) {
                    parseError = true;
                }
                reqExist = true;
            }
            if (req.getParameter("searchLastVersion") != null) {
                setShowOnlyLastVersionTask(req.getParameter("searchLastVersion").equals("y"));
                reqExist = true;
            }
            if (req.getParameter("search") != null && !req.getParameter("search").isEmpty()) {// Поиск производится по колонкам №, Контрагент, Сумма, Валюта
                String search = req.getParameter("search");
                reqExist = true;
                setSearch(search);
            }
            if (req.getParameter("navigation") != null) {
                LOGGER.trace("============ProcessSearchParam constructor. navigation get parameter is '" + req.getParameter("navigation") + "'. setPageNumber to '" + req.getParameter("navigation") + "'");

                setPageNumber(Formatter.parseLong(req.getParameter("navigation")));
            } else {
                LOGGER.trace("============ProcessSearchParam constructor. navigation parameter is null. set page number to 0");

                setPageNumber(0L);
            }
            if (req.getParameter("resetFilter") != null)
                resetFilter = true;

            LOGGER.info("===================ProcessSearchParam reqExist '" + reqExist + "', resetFilter '" +  resetFilter + "'");

            //выбираем режим параметров или куки
            if (!reqExist && !resetFilter) {
                //если хоть один параметр пришел гетом, значит это сознательный поиск или переключение страницы(пейджинг)
                //если гетом ничего не пришло, значит мы вернулись к списку заявок или впервые зашли в список. Тогда читаем из печенек
                Cookie[] cookies = req.getCookies();
                if (cookies != null) {
                    //читаем из куки только если текущий список тот же, что и в прошлый раз
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("typeList") && !currentList.equals(cookie.getValue()))
                            return;
                    }

                    for (Cookie cookie : cookies) {
                        String name = cookie.getName();
                        String value = URLDecoder.decode(cookie.getValue(), "UTF-8");
                        if (name.equals("searchNumber")) {
                            setNumber(value.trim());
                        }
                        if (name.equals("searchProcessType") && value != null && !value.equals("null"))
                            try {
                                setProcessTypeID(new Integer(value));
                            } catch (Exception e) {
                            }
                        if (name.equals("searchCurrency")) {
                            setCurrency(value);
                        }
                        if (name.equals("searchSumFrom")) {
                            try {
                                setSumFrom(Formatter.parseBigDecimal(value.trim()));
                            } catch (Exception e) {
                            }
                        }
                        if (name.equals("searchSumTo")) {
                            try {
                                setSumTo(Formatter.parseBigDecimal(value.trim()));
                            } catch (Exception e) {
                            }
                        }
                        if (name.equals("searchInitDepartment")) {
                            setInitDepartment(value.trim());
                        }
                        if (name.equals("searchStatus")) {
                            setStatus(value.trim());
                        }
                        if (name.equals("searchType")) {
                            setType(value.trim());
                        }
                        if (name.equals("searchContractor")) {
                            setContractor(value.trim());
                        }
                        if (name.equals("searchPriority")) {
                            setPriority(value);
                        }
                        if (name.equals("searchCurrOperation")) {
                            setCurrOperation(value.trim());
                        }
                        if (name.equals("searchExecutor")) {
                            setExecutor(value.trim());
                        }
                        if (name.equals("searchExecutorId")) {
                            try {
                                setExecutorId(Formatter.parseLong(value.trim()));
                            } catch (Exception e) {
                            }
                        }
                        if (name.equals("searchHideAssigned")) {
                            try {
                                setHideAssigned(value.equals("t"));
                            } catch (Exception e) {
                            }
                        }
                        if (name.equals("searchLastVersion")) {
                            try {
                                setShowOnlyLastVersionTask(value.equals("y"));
                            } catch (Exception e) {
                            }
                        }
                        if (name.equals("searchpageNumber")) {
                            try {
                                LOGGER.info("============ProcessSearchParam constructor. searchpageNumber param from cookie. setPageNumber value '" + value + "'");

                                setPageNumber(Formatter.parseLong(value));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * сохранить куки
     */
    public void saveCookies(HttpServletResponse resp) {
        LOGGER.info("=================ProcessSearchParam.saveCookies");

        resp.addCookie(prepareCookie("searchNumber", number));
        resp.addCookie(prepareCookie("searchProcessType", ProcessTypeID == null ? null : String.valueOf(ProcessTypeID)));
        resp.addCookie(prepareCookie("searchCurrency", currency));
        resp.addCookie(prepareCookie("searchSumFrom", sumFrom == null ? null : Formatter.format(sumFrom)));
        resp.addCookie(prepareCookie("searchSumTo", sumTo == null ? null : Formatter.format(sumTo)));
        resp.addCookie(prepareCookie("searchInitDepartment", initDepartment));
        resp.addCookie(prepareCookie("searchCurrOperation", currOperation));
        resp.addCookie(prepareCookie("searchPriority", priority));
        resp.addCookie(prepareCookie("searchStatus", status));
        resp.addCookie(prepareCookie("searchType", type));
        resp.addCookie(prepareCookie("searchContractor", contractor));
        resp.addCookie(prepareCookie("typeList", currentList));
        resp.addCookie(prepareCookie("searchExecutor", executor));
        resp.addCookie(prepareCookie("searchExecutorId", Formatter.str(executorId)));
        resp.addCookie(prepareCookie("searchHideAssigned", hideAssigned ? "t" : "f"));
        resp.addCookie(prepareCookie("searchLastVersion", showOnlyLastVersionTask ? "y" : "n"));

        LOGGER.trace("============ProcessSearchParam.saveCookies searchpageNumber '" + getPageNumber() + "'");

        resp.addCookie(prepareCookie("searchpageNumber", Formatter.format(getPageNumber())));
    }

    private Cookie prepareCookie(String name, String value) {
        try {
            Cookie c = new Cookie(name, value == null ? "" : URLEncoder.encode(value, "UTF-8"));
            if (value == null) c.setMaxAge(0);
            return c;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Cookie("URLEncoder.encodeerror", e.getMessage());
        }
    }

    public boolean showFilter() {
        boolean showFilter = number != null && number.length() > 0;
        showFilter = showFilter || contractor != null && contractor.length() > 0;
        showFilter = showFilter || sumFrom != null;
        showFilter = showFilter || sumTo != null;
        showFilter = showFilter || currency != null && !currency.equals("all");
        showFilter = showFilter || type != null && type.length() > 0;
        showFilter = showFilter || priority != null && !priority.equals("all");
        showFilter = showFilter || status != null && status.length() > 0;
        showFilter = showFilter || initDepartment != null && initDepartment.length() > 0;
        showFilter = showFilter || ProcessTypeID != null;
        showFilter = showFilter || executor != null && executor.length() > 0;
        showFilter = showFilter || executorId != null;
        showFilter = showFilter || hideAssigned;
        showFilter = showFilter || showOnlyLastVersionTask;
        showFilter = showFilter || !Formatter.str(showImportOnly).isEmpty();
        if (currentList.equals("all")) {
            showFilter = showFilter || !hideApproved && projectTeam || !projectTeam && hideApproved;
        }
        return showFilter;
    }

    public ProcessSearchParam() {
        super();
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the closed
     */
    public boolean isClosed() {
        return closed;
    }

    public String getProcessStatus() {
        if (closed)
            return "4";
        if (paused)
            return "2";
        if (favorite)
            return "1,2,4";
        return "1,2";
    }

    /**
     * @param closed the closed to set
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * @return the processTypeID
     */
    public Integer getProcessTypeID() {
        return ProcessTypeID;
    }

    /**
     * @param processTypeID the processTypeID to set
     */
    public void setProcessTypeID(Integer processTypeID) {
        ProcessTypeID = processTypeID;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the sumFrom
     */
    public BigDecimal getSumFrom() {
        return sumFrom;
    }

    /**
     * @param sumFrom the sumFrom to set
     */
    public void setSumFrom(BigDecimal sumFrom) {
        this.sumFrom = sumFrom;
    }

    /**
     * @return the sumTo
     */
    public BigDecimal getSumTo() {
        return sumTo;
    }

    /**
     * @param sumTo the sumTo to set
     */
    public void setSumTo(BigDecimal sumTo) {
        this.sumTo = sumTo;
    }

    /**
     * @return the initDepartment
     */
    public String getInitDepartment() {
        return initDepartment;
    }

    /**
     * @param initDepartment the initDepartment to set
     */
    public void setInitDepartment(String initDepartment) {
        this.initDepartment = initDepartment;
    }

    /**
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the contractor
     */
    public String getContractor() {
        return contractor;
    }

    /**
     * @param contractor the contractor to set
     */
    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    /**
     * @return the currOperation
     */
    public String getCurrOperation() {
        return currOperation;
    }

    /**
     * @param currOperation the currOperation to set
     */
    public void setCurrOperation(String currOperation) {
        this.currOperation = currOperation;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }

    /**
     * @return hide assigned tasks from list
     */
    public boolean isHideAssigned() {
        return hideAssigned;
    }

    /**
     * @param hideAssigned hide assigned tasks from list
     */
    public void setHideAssigned(boolean hideAssigned) {
        this.hideAssigned = hideAssigned;
    }

    public boolean isProjectTeam() {
        return projectTeam;
    }

    public void setProjectTeam(boolean projectTeam) {
        this.projectTeam = projectTeam;
    }

    public boolean isExpertTeam() {
        return expertTeam;
    }

    public void setExpertTeam(boolean expertTeam) {
        this.expertTeam = expertTeam;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isHideApproved() {
        return hideApproved;
    }

    public void setHideApproved(boolean hideApproved) {
        this.hideApproved = hideApproved;
    }

    public boolean isShowOnlyLastVersionTask() {
        return showOnlyLastVersionTask;
    }

    public void setShowOnlyLastVersionTask(boolean showOnlyLastVersionTask) {
        this.showOnlyLastVersionTask = showOnlyLastVersionTask;
    }

    public String getShowImportOnly() {
        return Formatter.str(showImportOnly);
    }

    public void setShowImportOnly(String showAccessOnly) {
        this.showImportOnly = showAccessOnly;
    }

    /**
     * Возвращает признак выборки избранных заявок
     *
     * @return <code><b>true</b></code> если необходимо выбрать избранные заявки
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * Установка признака выборки избранных заявок
     *
     * @param favorite признак выборки избранных заявок
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Long pageNumber) {
        this.pageNumber = pageNumber;
    }
}
