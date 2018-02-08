package com.vtb.util.report.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.aspose.words.DocumentBuilder;
import com.vtb.domain.Task;

/**
 * Helper class that converts Task data to parameters for report building. Fills report with TEST DATA 
 * @author Michael Kuznetsov
 */
public class Task2ReportDataHelperTest extends Task2ReportDataHelper {
    
    public Task2ReportDataHelperTest(DocumentBuilder builder, Task task, boolean viewSource) {
        super(builder, task, viewSource);
    }
    
    /**
     * Get data included in Task.header object.
     */
    @Override
    public TreeMap<String, String> getHeaderData() {
        TreeMap<String, String> map = new TreeMap<String, String>(); 
        map.put("Номер заявки", "3");  // "was header_combinedNumber"
        map.put("Номер_заявки", "2");  // "was header_combinedNumber"
        map.put("header_combinedNumber", "1");  // "was header_combinedNumber"
        map.put("isLimit", "True");        
        map.put("header_limitTypeName", "���������");
        map.put("header_operationtype_name", "�� ��� ����� ��� ���, ��");
        
        map.put("header_startDepartment_shortName", "�-� ���������");
        map.put("header_managersList_fio", "������ �.�.;\n�������� �.�.;\n�������� �.�."); // ���������� ������
        map.put("header_manager", "������� �.�.;\n�������� �.�.;\n�������� �.�."); 
        map.put("header_placesList_name", "�������������;\n����������;\n��� ���"); // ���������� ������
        map.put("header_place_shortName", "�������;\n�� � ������;\n������� �����");
        return map;
    }
        
    /**
     * Get data included in Task.main object. 
     */
    @Override
    public TreeMap<String, String> getMainData()  {
        TreeMap<String, String> map = new TreeMap<String, String>();
        // ��� ������� '�������� ���������'
        map.put("main_renewable", "��");   // �� BOOLEAN �������������
        map.put("main_projectFin", "��");   // �� BOOLEAN �������������

        map.put("main_sum", "3434,98");
        map.put("main_currency_code", "EUR");
        map.put("main_period_validTo", "99 ��.");     // ������ ���������, �����������
  
        map.put("main_redistribResidues", "��");   // �� BOOLEAN �������������
        map.put("main_validto", "12.12.2011");
        map.put("main_exchangeRate", "27,05");
        return map;        
    }

    /**
     * Get data included in Task.tranceComment field 
     */
    @Override
    public TreeMap<String, String> getTranceCommentData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        pairs.put("tranceComment", "����� ��� ����������� �� ������ �������");
        return pairs;
    }

    @Override
    public TreeMap<String, String> getInLimitForPrintFormData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        pairs.put("inLimitForPrintForm", "True");
        return pairs;
    }

    /**
     * Get data included in Task.principalPay object (������ ��������� ��������� �����). 
     */
    @Override
    public TreeMap<String, String> getPrincipalPayData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        pairs.put("principalPay_periodOrder_name","Always");
        pairs.put("principalPay_firstPayDate","12.12.2010");
        pairs.put("principalPay_amount","123,89");
        pairs.put("principalPay_firstPay","YES");
        pairs.put("principalPay_finalPayDate","12.03.2020");
        pairs.put("principalPay_depended","NO");
        pairs.put("principalPay_description","Comments to schedule");
        return pairs;
    }
    

    /**
     * Get data included in Task.principalPay object (������ ��������� ��������� �����). 
     */
    @Override
    public TreeMap<String, String> getInterestPayData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        pairs.put("interestPay_firstPayDate","01.01.2009");
        pairs.put("interestPay_numDay","12");
        pairs.put("interestPay_finalPayDate","12.09.2019");
        pairs.put("interestPay_finalPay","03.08.2019");
        pairs.put("interestPay_description","Comments to interest pay");
        return pairs;
    }

    @Override
    public MailMergeDataSource getPaymentScheduleListData(TreeMap<String, String> map) {
        MailMergeDataSource rs = new MailMergeDataSource("paymentScheduleList");
        rs.addColumn("paySch_amount");
        rs.addColumn("paySch_fromDate");
        rs.addColumn("paySch_toDate");

        List<Object> row = new ArrayList<Object>();
        row.add("123 123,10");
        row.add("10.09.2012");
        row.add("08.09.2015");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("2 343,10");
        row2.add("01.10.2018");
        row2.add("12.03.2019");
        rs.addRow(row2);
        return rs;
    }

    @Override
    public MailMergeDataSource getCommissionDealListData(TreeMap<String, String> map) {
        MailMergeDataSource rs = new MailMergeDataSource("commissionDealList");
        rs.addColumn("comD_name_name");
        rs.addColumn("comD_value");
        rs.addColumn("comD_currency_code");
        rs.addColumn("comD_description");
        rs.addColumn("comD_procent_order_name");
        rs.addColumn("comD_payDescription");
        rs.addColumn("comD_calcBase_name");
        rs.addColumn("comD_comissionSize_name");
        
        List<Object> row = new ArrayList<Object>();
        row.add("name of the comission");
        row.add("123,45");
        row.add("EUR");
        row.add("Description of comission");
        row.add("procent order name");
        row.add("pay it for this goods");
        row.add("LIBOR M");
        row.add("comissionSize_name");
        rs.addRow(row);
        return rs;
    }

    @Override
    public MailMergeDataSource getTranchesListData(TreeMap<String, String> map) {
        MailMergeDataSource rs = new MailMergeDataSource("tranches");
        rs.addColumn("tra_id");
        rs.addColumn("tra_sum");
        rs.addColumn("tra_currency_code");
        rs.addColumn("tra_usedatefrom");
        rs.addColumn("tra_usedate");
        
        List<Object> row = new ArrayList<Object>();
        row.add("1");
        row.add("1 234,45");
        row.add("EUR");
        row.add("01.01.2011");
        row.add("12.12.2012");
        rs.addRow(row);

        List<Object> row2 = new ArrayList<Object>();
        row2.add("2");
        row2.add("224,45");
        row2.add("RUR");
        row2.add("11.11.2011");
        row2.add("22.12.2012");
        rs.addRow(row2);
        return rs;
    }

    
    
    /**
     * Get data included in AllContractorsGroups object. 
     */
    @Override
    public String getAllContractorsGroups() {
        return "������ 1;\n������ 2; ������ 3"; // ���������� ������
    }
    
    /**
     * Generate sorted list of all currency list as String
     */
    @Override
    public  String getCurrencyList() {
        return "RUR\nEUR\nUSD\nJAE";  // ���������� ������
    }
    
    /**
     * Generate sorted list of all contractors as String
     */
    @Override
    public  String getAllContractors() {
        return "���������� ���������� �����, ���;\n ��������-������, ���"; // ������� ������
    }

    
    /**
     * Get data included in Task.generalCondition object. 
     */
    @Override
    public TreeMap<String, String> getGeneralConditionData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("generalCondition_quality_category", "II");
        map.put("generalCondition_quality_category_desc", "������������� � ������� �������� ������� ���������� ��������");
        return map;
    }
    
    /**
     * Get data included in Task.taskProcent object. 
     */
    @Override
    public TreeMap<String, String> getTaskProcentData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        // ��� ������� ����������� �������
        // �������. ����� �� ������� �������
        map.put("procent_riskpremium", "30,25");
        map.put("procent_description", "�������� �������� ��� ��� ���");
        map.put("procent_limitPayPattern_text", "3 ���.");
        map.put("procent_trRiskC1", "0,76");
        map.put("procent_trRiskC1", "0,43");
        map.put("procent_computeDate", "12.12.2023");
        map.put("procent_additionalDescription", "��� ����� ��� �������������� ��������");
        return map;
    }
    
    /**
     * Get data included in Task.supply object. 
     */
    @Override
    public TreeMap<String, String> getSupplyData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        // ����� ���� ��� �����������
        map.put("supply_exist", "�������������");  // �� BOOLEAN ������������� 
        map.put("supply_additionSupply", "��� ����� ��� �����������");
        map.put("supply_cfact", "0,78");
        
        // ��� �������
        map.put("supply_dCondition", "��� ����� ��� �������������� ������� ��������� ������");
        // ��� �������������
        map.put("supply_wCondition", "��� ����� ��� �������������� ������� ��� �������������");
        // ��� ��������
        map.put("supply_guaranteeCondition", "��� ����� ��� �������������� ������� ��� ��������");
        return map;
    }
    
    /**
     * fills datasource with information about EarlyPayment  
     */
    @Override
    public  MailMergeDataSource getEarlyPaymentListData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("earlyPaymentList");
        rs.addColumn("ep_condition");
        rs.addColumn("ep_permissionValue");
        rs.addColumn("ep_commission");
        
        List<Object> row = new ArrayList<Object>();
        row.add("����������� ������� ���������� ��������� � ������������ � ����������� ����� �����");
        row.add("� ������������");
        row.add("�������� �� ���������"); 
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("��� �������� ��������");
        row2.add("�� ������������ � ������");
        row2.add("�������� ���������");
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about contractors data of sublimits
     */
    @Override
    public MailMergeDataSource getSublimitContractorsData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("sublimit_contractors");
        rs.addColumn("sl_number");
        rs.addColumn("sl_companiesGroup");
        rs.addColumn("sl_organization");
        rs.addColumn("sl_limitVid");
        rs.addColumn("sl_sum");
        rs.addColumn("sl_currency");
        rs.addColumn("sl_period_validTo");

        List<Object> row = new ArrayList<Object>();
        row.add("1");
        row.add("СуперГруппа");
        row.add("АвиаНова, Просто Компания; \nNAFTOS TERMINALAS");
        row.add("Кредитный");
        row.add("999 999,44");
        row.add("EUR");
        row.add("12.12.2012");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("2");
        row2.add("СверхГруппа;\nИ еще одна");
        row2.add("S7, ОАО;\nТрансаэро, ООО");
        row2.add("Лимитный");
        row2.add("123,44");
        row2.add("USD");
        row2.add("123 РУБ.");
        rs.addRow(row2);
        
        List<Object> row3 = new ArrayList<Object>();
        row3.add("3");
        row3.add("Группа печенегов;\n");
        row3.add("Просто товарищ, ООО");
        row3.add("Кредитный");
        row3.add("123 232,54");
        row3.add("USD");
        row3.add("343,00 РУБ.");
        rs.addRow(row3);
        
        MailMergeDataSource targetsOne = new MailMergeDataSource("targetInternalList");
        targetsOne.addOneColumn("ta_name", new String[]{"Для первого Одна цель", "Для первого вторая цель"});

        MailMergeDataSource targetsTwo = new MailMergeDataSource("targetInternalList");
        targetsTwo.addOneColumn("ta_name", new String[]{});

        MailMergeDataSource targetsThree = new MailMergeDataSource("targetInternalList");
        targetsThree.addOneColumn("ta_name", new String[]{"Для третьего Одна цель", "Для третьего вторая цель", "Для третьего третья цель"});

        List<MailMergeDataSource> arrayForOne = new ArrayList<MailMergeDataSource>();
        arrayForOne.add(targetsOne);
        List<MailMergeDataSource> arrayForTwo = new ArrayList<MailMergeDataSource>();
        arrayForTwo.add(targetsTwo);
        List<MailMergeDataSource> arrayForThree = new ArrayList<MailMergeDataSource>();
        arrayForThree.add(targetsThree);
        List<List<MailMergeDataSource>> children = new ArrayList<List<MailMergeDataSource>>();
        children.add(arrayForOne);
        children.add(arrayForTwo);
        children.add(arrayForThree);
        rs.setChildDataSources(children);
        
        
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }
    
    /**
     * fills datasource with information about OTHER departments
     */
    @Override
    public MailMergeDataSource getOtherDepartmentsData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("header_otherDepartments");
        rs.addColumn("od_dep_shortName");
        rs.addColumn("od_managersList_fio");
    
        List<Object> row = new ArrayList<Object>();
        row.add("������ � �. �������");
        row.add("��������� �������� ���������.;\n�������� ������� �������������.");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("000 - �������� �����������");
        row2.add("");
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about Contractors
     */
    @Override
    public MailMergeDataSource getContractorsData(TreeMap<String, String> map)     {
        final String TABLE_NAME = "contractors";
        final String PREFIX = "co_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME); 
        rs.addColumn(PREFIX + "orgType_description");
        rs.addColumn(PREFIX + "org_account_name");
        rs.addColumn(PREFIX + "org_category");
        rs.addColumn(PREFIX + "org_calcHistory_branch");        
        rs.addColumn(PREFIX + "org_calcHistory_region");
        
        rs.addColumn(PREFIX + "org_calcHistory_ratingCC");
        rs.addColumn(PREFIX + "org_calcHistory_expRating");
        rs.addColumn(PREFIX + "org_calcHistory_rating");
        rs.addColumn(PREFIX + "org_calcHistory_totalPoints");
        rs.addColumn(PREFIX + "org_calcHistory_rDate");

        List<Object> row = new ArrayList<Object>();
        row.add("Заемщик;\nЗалогодатель");
        row.add("Завод Восход, ОАО");
        row.add("Промышленность");
        row.add("такой вот");
        row.add("Москва");
        row.add("A1");
        row.add("B2");
        row.add("E3");
        row.add("63,20");
        row.add("09.02.2011");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("Гарантор;\nКредитор;\nЗаемщик");
        row2.add("Дельта, ООО");
        row2.add("ПищеПром");
        row2.add("так-то");
        row2.add("Москва");
        row2.add("B1");
        row2.add("E");
        row2.add("B3");
        row2.add("343,00");
        row2.add("10.02.2011");
        rs.addRow(row2);

        row = new ArrayList<Object>();
        row.add("Гарантор;\n");
        row.add("Просто товарищ, ООО");
        row.add("Молочный");
        row.add("так-то вот");
        row.add("СПб");
        row.add("B1");
        row.add("E");
        row.add("B3");
        row.add("343,00");
        row.add("10.02.2011");
        rs.addRow(row);
        
        MailMergeDataSource targetsOne = new MailMergeDataSource("targetInternalList");
        targetsOne.addOneColumn("ta_name", new String[]{"Для первого Одна цель", "Для первого вторая цель"});

        MailMergeDataSource targetsTwo = new MailMergeDataSource("targetInternalList");
        targetsTwo.addOneColumn("ta_name", new String[]{});

        MailMergeDataSource targetsThree = new MailMergeDataSource("targetInternalList");
        targetsThree.addOneColumn("ta_name", new String[]{"Для третьего Одна цель", "Для третьего вторая цель", "Для третьего третья цель"});

        List<MailMergeDataSource> arrayForOne = new ArrayList<MailMergeDataSource>();
        arrayForOne.add(targetsOne);
        List<MailMergeDataSource> arrayForTwo = new ArrayList<MailMergeDataSource>();
        arrayForTwo.add(targetsTwo);
        List<MailMergeDataSource> arrayForThree = new ArrayList<MailMergeDataSource>();
        arrayForThree.add(targetsThree);
        List<List<MailMergeDataSource>> children = new ArrayList<List<MailMergeDataSource>>();
        children.add(arrayForOne);
        children.add(arrayForTwo);
        children.add(arrayForThree);
        rs.setChildDataSources(children);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about Opportunity Types
     */
    @Override
    public MailMergeDataSource getOpportunityTypesData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("opportunityTypes");
        rs.addColumn("op_family");
        rs.addColumn("op_name");

        List<Object> row = new ArrayList<Object>();
        row.add("������������");
        row.add("������");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("������������");
        row2.add("������ � ������������ ���������");                        
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about Opportunity Types Dates
     */
    @Override
    public  MailMergeDataSource getOpportunityTypesDatesData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("opportunityTypesDates");
        rs.addColumn("opd_name");
        rs.addColumn("opd_period");
        rs.addColumn("opd_usedate");
        rs.addColumn("opd_description");
        
        List<Object> row = new ArrayList<Object>();
        row.add("������");
        row.add("150");
        row.add("12.12.2020");
        row.add("�� ��� ����� ��� �����������");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("������ � ������������ ���������");
        row2.add("90");                        
        row2.add("21.12.2011");
        row2.add("� ��� ���� �����������");
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

//    /**
//     * fills datasource with information about Other Goals
//     */
//    @Override
//    public  MailMergeDataSource getOtherGoalsData() {
//        MailMergeDataSource rs = new MailMergeDataSource("otherGoals");
//        rs.addOneColumn("og_name", new String[]{"��� ���� ����", "��� ���� ���", "��� ���� ���"});          
//        if (log) rs.print(builder, logOnlyNames);
//        return rs;
//    }

    /**
     * fills datasource with information about StandardPriceCondition for Procent
     */
    @Override
    public MailMergeDataSource getProcentStandardPriceConditionListData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("procent_standardPriceConditionList");
        rs.addOneColumn("pspc_name", new String[]{"�������� �� ������ ������� ����������������� � ������������ �������, ���� ���������� ��������� �������� ����������� ���... ", 
                                               "���������������� ��������� �������� � ������������ � ��������� ������ ����������� ��������� ����������� ��� ����� ������������ ��������, ������������ � �����"});          
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about Commission for Procent (for Limit)
     */
    @Override
    public MailMergeDataSource getCommissionListData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("commissionList");
        rs.addColumn("com_name_name");
        rs.addColumn("com_description");
        rs.addColumn("com_commissionLimitPayPattern_name");

        List<Object> row = new ArrayList<Object>();
        row.add("�������� �� ���������� ��������� �������");
        row.add("�������� �� ���������� ��������� �������");
        row.add("����������");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("�������� �� ������");
        row2.add("�������� �� ������ ����");                        
        row2.add("����������");
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about Fine for Procent
     */
    @Override
    public MailMergeDataSource getFineListData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("fineList");
        rs.addColumn("fl_punitiveMeasure");
        rs.addColumn("fl_description");

        List<Object> row = new ArrayList<Object>();
        row.add("���������, ����������� �� ����� ������������ ������������� �� ��������� �����");
        row.add(" �� ����� ������������ ������������� (���� �� ���������)");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("��������� �� ������������ ������������� �� ��������� ��");
        row2.add("�������� ����� �� ���������");                        
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }
    
    
    /**
     * fills datasource with information about otherCondition with type 1  
     */
    @Override
    public MailMergeDataSource getOtherConditionType1Data()     {
        final String TABLE_NAME = "otherConditionType1";
        final String PREFIX = "othCondType1_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME);
        rs.addOneColumn(PREFIX + "body", new String[]{"���� �������� ��������� �������� ������ ������������ �� ��������� ������ ������������ �������� � �������������� ����������� ��� ����� ������ �� ������ �������� ������.", 
                "��, ��� �������"});
        // ������� ������ ��� 1
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }
    
    
    /**
     * fills datasource with information about otherCondition with type 2  
     */
    @Override
    public MailMergeDataSource getOtherConditionType2Data()     {
        final String TABLE_NAME = "otherConditionType2";
        final String PREFIX = "othCondType2_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME);
        rs.addOneColumn(PREFIX + "body", new String[]{"2.  ������������� ������� ������������� ��������� ������� 2", 
                                               "222.. ��, ��� �������"});
        // ������� ������ ��� 2
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about otherCondition with type 3  
     */
    @Override
    public MailMergeDataSource getOtherConditionType3Data()     {
        final String TABLE_NAME = "otherConditionType3";
        final String PREFIX = "othCondType3_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME);
        rs.addOneColumn(PREFIX + "body", new String[]{"3.  �������", 
                "333.. �������22", "3333 ��, ��� �������"});
        // ������� ������ ��� 3
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about otherCondition with type 4  
     */
    @Override
    public MailMergeDataSource getOtherConditionType4Data()     {
        final String TABLE_NAME = "otherConditionType4";
        final String PREFIX = "othCondType4_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME);
        rs.addOneColumn(PREFIX + "body", new String[]{"4.  �������", 
                "444.. �������", "4444 ��, ��� �������"});
        // ������� ������ ��� 3
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about otherCondition with type 5  
     */
    @Override
    public MailMergeDataSource getOtherConditionType5Data()     {
        final String TABLE_NAME = "otherConditionType5";
        final String PREFIX = "othCondType5_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME);
        rs.addOneColumn(PREFIX + "body", new String[]{"5.  �������", 
                "555.. �������", "5555 ��, ��� �������"});
        // ������� ������ ��� 3
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about Deposit in Supply
     */
    @Override
    public MailMergeDataSource getDepositListData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("supplyDepositList");
        rs.addColumn("dl_org_account_name");
        rs.addColumn("dl_org_calcHistory_rating");   // �����: ������� + ��� �������. ���� �������� � if � ��������� �� null 
        rs.addColumn("dl_main");      // ������ ����. ��� ����������� ��������� �� ��� ���. ���� if ��������.  
        rs.addColumn("dl_type");
        rs.addColumn("dl_zalogObject_text");
        rs.addColumn("dl_issuer_account_name");
        rs.addColumn("dl_issuer_calcHistory_rating"); // ���� ����� ������� ����:�(�������: <xsl:value-of select="issuer/calcHistory/rating"/>) 
                                                      // ���� if � ��������� �� null  
        rs.addColumn("dl_zalogDescription");
        rs.addColumn("dl_zalogMarket");
        rs.addColumn("dl_orderDescription");
        rs.addColumn("dl_discount");
        rs.addColumn("dl_oppDescription");
        rs.addColumn("dl_zalogTerminate");
        rs.addColumn("dl_zalog");
        rs.addColumn("dl_liquidityLevel_name");
        rs.addColumn("dl_depositorFinStatus_name");
        rs.addColumn("dl_ob_name");
        rs.addColumn("dl_transRisk");
        
        List<Object> row = new ArrayList<Object>();
        row.add("����������������� ( ���� ), ���");
        row.add("D1");
        row.add("���");
        row.add("�����");  
        row.add("������ ������");
        row.add("������������, ��� E  1");
        row.add("���������� E2)");
        row.add("������� ��������� ��� ���");
        row.add("715 000,00");
        row.add("�� ������������))");
        row.add("0,70");
        row.add("�� ����� ������");
        row.add("500 000,00");
        row.add("200 000,00");  
        row.add("������");
        row.add("�������");
        row.add("����� ������ �����, ���������� � ���������� ������ ����� ������ (����� ���������� � 1,2 � 3 ������ ������������� ��������������� �����) � �������� �������������� ������ �� ���������� ��������� ��������");
        row.add("0,20");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("��������-������, ���");
        row2.add(" E");
        row2.add("��");
        row2.add("�����");
        row2.add("������ ������");
        row2.add("");
        row2.add("��������-2");
        row2.add("");
        row2.add("400 000,00");
        row2.add("��� ��� � ����������");
        row2.add("0,80");
        row2.add("�� ����� ���������");
        row2.add("370 000,00");
        row2.add("1 200,00");
        row2.add("������");
        row2.add("�������");
        row2.add("����� ����� ��� ���� ���");
        row2.add("0,10");
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information of Additional Attributes sabout Deposit in Supply
     */
    @Override
    public MailMergeDataSource getDepositAdditionalAttrsData()     {
        final String TABLE_NAME = "supply_DepositAdditionalAttrs";
        final String PREFIX = "sdaa_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME); 
        rs.addColumn(PREFIX + "key");
        rs.addColumn(PREFIX + "value");

        List<Object> row = new ArrayList<Object>();
        row.add("������� 1");
        row.add("�������� 1");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("������� 2");
        row2.add("�������� 2");
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about Warranty in Supply
     */
    @Override
    public MailMergeDataSource getWarrantyListData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("supplyWarrantyList");
        rs.addColumn("wl_org_account_name");
        rs.addColumn("wl_org_calcHistory_rating");   // �����: ������� + ��� �������. ���� �������� � if � ��������� �� null 
        // ���� ��������� ���. If �������!!!
        rs.addColumn("wl_person_lastName");
        rs.addColumn("wl_person_name");
        rs.addColumn("wl_person_middleName");
        
        rs.addColumn("wl_main");      // ������ ����. ��� ����������� ��������� �� ��� ���. ���� if ��������.  
        rs.addColumn("wl_fullSum");   // ������ ����. ��� ����������� ��������� �� ��� ���. ���� if ��������.
        rs.addColumn("wl_sum");
        rs.addColumn("wl_currency_code");
        rs.addColumn("wl_responsibilityString");
        rs.addColumn("wl_kind");  
        
        rs.addColumn("wl_fine");
        rs.addColumn("wl_add");
        rs.addColumn("wl_liquidityLevel_name");
        rs.addColumn("wl_depositorFinStatus_name");
        rs.addColumn("wl_ob_name");
        rs.addColumn("wl_transRisk");
        rs.addColumn("wl_description");
        
        List<Object> row = new ArrayList<Object>();
        row.add("NAFTOS TERMINALAS");
        row.add("������� E1");
        row.add("");
        row.add("");
        row.add("");           
        
        row.add("���");
        row.add("���");  
        
        row.add("423 433,98");
        row.add("RUR");
        row.add("������, ��� �������������, % ��������, �������� ����");
        row.add("������������");
        row.add("����� ��� ����");
        row.add("�� ���� ��");
        row.add("������");
        row.add("�������");
        row.add("�������� �������� � ������ �������� �� ��������������� ����� (�� ���������� ���������)");
        row.add("0,10");  
        row.add("������ �� ������� �������� ��������������?");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("��������������� �.������, ���  ");
        row2.add("");
        row2.add("");
        row2.add("");
        row2.add("");
        row2.add("��");
        row2.add("��");
        row2.add("407 300,00");
        row2.add("EUR");
        row2.add("������, ��� �������������, % ��������, �������� ����");
        row2.add("������������");
        row2.add("������");
        row2.add("��� ��");
        row2.add("������");
        row2.add("�������");
        row2.add("����� ������������, ������������ �������, ���������� �������");
        row2.add("0,13");
        row2.add("�������� �������� �����");
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about Guarantee in Supply
     */
    @Override
    public MailMergeDataSource getGuaranteeListData(TreeMap<String, String> map)     {
        MailMergeDataSource rs = new MailMergeDataSource("supplyGuaranteeList");
        
        rs.addColumn("gl_org_account_name");
        rs.addColumn("gl_org_calcHistory_rating");   // �����: ������� + ��� �������. ���� �������� � if � ��������� �� null 
        // ���� ��������� ���. If �������!!!
        rs.addColumn("gl_person_lastName");
        rs.addColumn("gl_person_name");
        rs.addColumn("gl_person_middleName");
        
        rs.addColumn("gl_main");      // ������ ����. ��� ����������� ��������� �� ��� ���. ���� if ��������.  
        rs.addColumn("gl_sum");
        rs.addColumn("gl_currency_code");
        rs.addColumn("gl_liquidityLevel_name");
        rs.addColumn("gl_depositorFinStatus_name");
        rs.addColumn("gl_ob_name");
        rs.addColumn("gl_transRisk");
        rs.addColumn("gl_description");

        List<Object> row = new ArrayList<Object>();
        row.add("RUSSIAN-AMERICAN CORPORATION (RAFCO), N YK");
        row.add("������� D1");
        row.add("");
        row.add("");
        row.add("");           
        
        row.add("���");
        row.add("7 000,00");
        row.add("RUR");
        row.add("������");
        row.add("c������");
        row.add("����� ������������, ��������� �������� ��������������������� ����������");
        row.add("0,60");  
        row.add("������� ���");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("��� ��� ����");
        row2.add("������� A1");
        row2.add("");
        row2.add("");
        row2.add("");            
        row2.add("���");
        row2.add("107 300,00");
        row2.add("EUR");
        row2.add("������");
        row2.add("c������");
        row2.add("����� ������ � ������� ������������");
        row2.add("0,10");
        row2.add("�������� ��� �����");
        rs.addRow(row2);
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }
    
    @Override
    public TreeMap<String, String> getMainBorrowerData() {
        return new TreeMap<String, String>();
    }
    
    /**
     * fills datasource with information about contractors data of sublimits
     */
    @Override
    public MailMergeDataSource getVocConditionsData()     {
        MailMergeDataSource rs = new MailMergeDataSource("СПРАВОЧНИК_УСЛОВИЯ");
        rs.addColumn("УСЛ_ИМЯ_УСЛОВИЯ");
        rs.addColumn("УСЛ_СРОК_ВЫПОЛНЕНИЯ");

        List<Object> row = new ArrayList<Object>();
        row.add("Условие номер 1");
        row.add("25.12.2012");
        rs.addRow(row);
        
        List<Object> row2 = new ArrayList<Object>();
        row2.add("Условие номер 2");
        row2.add("01.01.2013");
        rs.addRow(row2);
        
        List<Object> row3 = new ArrayList<Object>();
        row3.add("Условие номер 3");
        row3.add("12.02.2020");
        rs.addRow(row3);
        
        MailMergeDataSource docsOne = new MailMergeDataSource("ТИП_ДОКУМЕНТА");
        docsOne.addColumn("ТД_ИМЯ_ТИПА");
        docsOne.addColumn("ТД_АВТОР");
        row = new ArrayList<Object>();
        row.add("Тип документа номер 1");
        row.add("Кузнецов М.Л");
        docsOne.addRow(row);
        row = new ArrayList<Object>();
        row.add("Тип документа номер 2");
        row.add("Мяктинова Н.Н");
        docsOne.addRow(row);
        row = new ArrayList<Object>();
        row.add("Тип документа номер 3");
        row.add("Валиев С.С");
        docsOne.addRow(row);
        
        MailMergeDataSource docsTwo = new MailMergeDataSource("ТИП_ДОКУМЕНТА");
        docsTwo.addColumn("ТД_ИМЯ_ТИПА");
        docsTwo.addColumn("ТД_АВТОР");
        row = new ArrayList<Object>();
        row.add("Тип документа номер 4");
        row.add("Павленко А.Ю.");
        docsTwo.addRow(row);
        row = new ArrayList<Object>();
        row.add("Тип документа номер 5");
        row.add("Рясиченко О.В.");
        docsTwo.addRow(row);

        MailMergeDataSource docsThree = new MailMergeDataSource("ТИП_ДОКУМЕНТА");
        docsThree.addColumn("ТД_ИМЯ_ТИПА");
        docsThree.addColumn("ТД_АВТОР");
        row = new ArrayList<Object>();
        row.add("Тип документа номер 6");
        row.add("Кузнецов М.Л");
        docsThree.addRow(row);
        row = new ArrayList<Object>();
        row.add("Тип документа номер 7");
        row.add("Мяктинова Н.Н");
        docsThree.addRow(row);
        row = new ArrayList<Object>();
        row.add("Тип документа номер 8");
        row.add("Валиев С.С");
        docsThree.addRow(row);

        List<MailMergeDataSource> arrayForOne = new ArrayList<MailMergeDataSource>();
        arrayForOne.add(docsOne);
        List<MailMergeDataSource> arrayForTwo = new ArrayList<MailMergeDataSource>();
        arrayForTwo.add(docsTwo);
        List<MailMergeDataSource> arrayForThree = new ArrayList<MailMergeDataSource>();
        arrayForThree.add(docsThree);
        List<List<MailMergeDataSource>> children = new ArrayList<List<MailMergeDataSource>>();
        children.add(arrayForOne);
        children.add(arrayForTwo);
        children.add(arrayForThree);
        rs.setChildDataSources(children);
        
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }
}
