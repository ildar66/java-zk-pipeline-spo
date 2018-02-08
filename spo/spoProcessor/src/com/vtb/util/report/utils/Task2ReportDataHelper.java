package com.vtb.util.report.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.crm.CompanyGroup;
import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.domain.spo.StandardPriceCondition;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.integration.rating.RatingService;
import ru.masterdm.integration.rating.ws.CalcHistoryInput;
import ru.masterdm.integration.rating.ws.CalcHistoryOutput;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.Currency;
import ru.md.domain.OtherGoal;
import ru.md.spo.util.Config;

import com.aspose.words.Body;
import com.aspose.words.Cell;
import com.aspose.words.CellMerge;
import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.FieldStart;
import com.aspose.words.FieldType;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.MailMerge;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeImporter;
import com.aspose.words.NodeList;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Row;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.SectionStart;
import com.aspose.words.Table;
import com.aspose.words.VisitorAction;
import com.vtb.domain.ApprovedRating;
import com.vtb.domain.Commission;
import com.vtb.domain.CommissionDeal;
import com.vtb.domain.Contract;
import com.vtb.domain.DepartmentAgreement;
import com.vtb.domain.Deposit;
import com.vtb.domain.EarlyPayment;
import com.vtb.domain.ExtendText;
import com.vtb.domain.FactPercent;
import com.vtb.domain.Fine;
import com.vtb.domain.Forbidden;
import com.vtb.domain.GeneralCondition;
import com.vtb.domain.Guarantee;
import com.vtb.domain.InterestPay;
import com.vtb.domain.LimitTree;
import com.vtb.domain.Main;
import com.vtb.domain.OtherCondition;
import com.vtb.domain.ParentData;
import com.vtb.domain.PaymentSchedule;
import com.vtb.domain.Premium;
import com.vtb.domain.PrincipalPay;
import com.vtb.domain.ProjectTeamMember;
import com.vtb.domain.PromissoryNote;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskCurrency;
import com.vtb.domain.TaskDepartment;
import com.vtb.domain.TaskHeader;
import com.vtb.domain.TaskManager;
import com.vtb.domain.TaskProcent;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskSupply;
import com.vtb.domain.Trance;
import com.vtb.domain.Warranty;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;

/**
 * Helper class that converts Task data to parameters for report building 
 * @author Michael Kuznetsov
 */
public class Task2ReportDataHelper {
	public final static String BLOCK_WARRANTY_PREFIX = "BlockWarranty";
	public final static String BLOCK_1OPPORTUNITY_PREFIX = "Block1Opportunity";    
	public final static String BLOCK_2OPPORTUNITY_PREFIX = "Block2Opportunity";    
	public final static String BLOCK_2LIMIT_PREFIX = "BlockLimit_part2";    
	public final static String BLOCK_1LIMIT_PREFIX = "BlockLimit_part1";    
    public final static String BLOCK_LIMIT_PREFIX = "BlockLimit";
    public final static String BLOCK_SUBLIMIT_PREFIX = "BlockSublimit";
    public final static String BLOCK_SUBLIMIT_CREDIT1_PREFIX = "BlockCreditSublimit1";
    public final static String BLOCK_SUBLIMIT_CREDIT2_PREFIX = "BlockCreditSublimit2";
    public final static String BLOCK_SUBLIMIT_DOCUMENT1_PREFIX = "BlockDocumentSublimit1";
    public final static String BLOCK_SUBLIMIT_DOCUMENT2_PREFIX = "BlockDocumentSublimit2";
    public final static String BLOCK_BOTH_LIMIT_SUBLIMIT_PREFIX = "BlockBothLimitSublimit";
    public final static String BLOCK_OPPORTUNITY_PREFIX = "BlockOpportunity";    
    public final static String BLOCK_OPPORTUNITY_INLIMIT_PREFIX = "BlockOpportunity_InLimit";
    public final static String BLOCK_OPPORTUNITY_NOT_INLIMIT_PREFIX = "BlockOpportunity_NOT_InLimit";
    public final static String BLOCK_HEADER_PREFIX = "BlockHeader";
    public final static String BLOCK_FOOTER_PREFIX = "BlockFooter";
    public final static String BLOCK_EMPTY_TABLE_PREFIX = "BlockEmptyTable";
    public final static String STARTING_BLOCK_SUFFIX = "start";
    public final static String ENDING_BLOCK_SUFFIX = "end";
    public final static String START_TABLE_PREFIX = "TableStart:";
    public final static String END_TABLE_PREFIX = "TableEnd:";

    
    protected final String LINE_DELIMITER = "\n";
    protected final boolean logOnlyNames = false;   // log only names, not values
    protected final static Logger LOGGER = Logger.getLogger(Task2ReportDataHelper.class.getName());
    
    protected  DocumentBuilder builder;
    private Task task;
    protected boolean log = true;   // log data    
    private Map<String, Integer> mergeFields;
    
    public Task2ReportDataHelper(DocumentBuilder builder, Task task, boolean viewSource) {
        this.builder = builder;
        this.task = task;
        this.log = viewSource;
    }
    
//  /**
//  * Returns list of MailMergeDataSource objects that object o contains.
//  * @param object object to analyse
//  * @return list of MailMergeDataSource objects that object o contains.
//  */
// List<MailMergeDataSource> getMailMergeList (VtbObject object) {
//     List<MailMergeDataSource> result = new ArrayList<MailMergeDataSource>(); 
//     List<List<VtbObject>> listOfLists = new ArrayList<List<VtbObject>>();
//     // Этап 1. При промощи reflection проанализировать структуру объекта и найти все списки 
//     // <рассмотрим пока списки сложных объектов, а не List<String>>
//     // listOfLists
//      
//     // Этап 2. Для каждого списка в цикле заполните объект MailMergeDataSource:
//     for(List<VtbObject> list : listOfLists) {
//         // пусть listу нас будет рассматриваемый список (вместо VtbObject подставтьте требуемый тип) 
//         String tableName = "найденное по правилам имя таблицы";   // полученное из reflection или аннотации имя таблицы
//         String tablePrefix = "найденный по правилам префикс таблицы"; // полученное из reflection или аннотации префикс таблицы
//         MailMergeDataSource ds = new MailMergeDataSource(tableName);
//         // Этап 2.1. Заполнение полей таблицы значениями
//         // тут надо вызвать метод, который рекурсивно проанализирует структуру VtbObject и сгенерирует имена колонок таблицы.
//         // Сережа такой метод написал уже.
//         ds.createColumns(tablePrefix, VtbObject.class);
// 
//         // а теперь для каждой строки таблицы положим значения в таблицу. Опять при помощи метода Сергея, обходящего структуру VtbObject
//         // и генерирующего значения типа <key, value>
//         for (int i = 0; i< list.size(); i++) {
//             VtbObject rowObject = list.get(i);
//             ds.addRow(tablePrefix, rowObject); 
//             // А теперь заполним все child tables для данной строки. Рекурсивно!!!
//             List<MailMergeDataSource> childTablesForRow = getMailMergeList(rowObject);
//             ds.setChildDataSources(i,childTablesForRow);
//         }
//         result.add(ds);
//     }
//     return result;
// }
// 
// 
// private void buildReport() {
//     VtbObject obj = new VtbObject();         // VtbObject obj -- это анализируемый объект, для которого строим отчет
//     Document doc = new Document();    // aspose doc, который модифицируем.
//     List<MailMergeDataSource> tablesInObject = getMailMergeList(obj);
//     for (MailMergeDataSource table : tablesInObject) { 
//         executeWithRegions(doc, table);
//     }
// }
    
    /***************************************************************************************************/
    /*                Methods that fills data from the java objects to be shown in the report          */
    /***************************************************************************************************/
    
    /**
     * Get data included in Task.header object. 
     */
    public TreeMap<String, String> getHeaderData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        TaskHeader header = task.getHeader();
        if (header == null) header = new TaskHeader();
        header.toFlatPairs("header_", pairs, true);

        // ��������� ������
        pairs.put("header_managersList_fio", getManagersList());
        //����� ���������� ������ (������)
        pairs.put("header_placesList_name", getPlacesList());
        return pairs;
    }

    /**
     * Get data included in Task.main object. 
     */
    public TreeMap<String, String> getMainData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        Main main = task.getMain();
        if (main == null) main = new Main();  
        main.toFlatPairs("main_", pairs, true);

        // period � validTo
        // pairs.put("main_period_validTo", getPeriodValidTo(main.getPeriod(), main.getValidto()));
        // list of currencies
        pairs.put("currencyList_currency", getCurrencyList());        
        return pairs;
    }

    /**
     * Get data included in Task.main object. 
     */
    public TreeMap<String, String> getParentData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        ParentData parentData = task.getParentData();
        if (parentData == null) parentData = new ParentData();  
        parentData.toFlatPairs("parentData_", pairs, true);

        // period � validTo
        // pairs.put("main_period_validTo", getPeriodValidTo(main.getPeriod(), main.getValidto()));
        // list of currencies
        pairs.put("currencyList_currency", getCurrencyList());        
        return pairs;
    }

    
    /**
     * Get data included in Task.mainBorrower object. 
     */
    public TreeMap<String, String> getMainBorrowerData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        TaskContractor mainBorrower = task.getMainBorrower();
        if (mainBorrower == null) mainBorrower = new TaskContractor();
        generateRatings("mainBorrower_", pairs, mainBorrower);
        mainBorrower.toFlatPairs("mainBorrower_", pairs, true);
        return pairs;
    }

    /**
     * Get data included in Task.principalPay object (������ ��������� ��������� �����). 
     */
    public TreeMap<String, String> getPrincipalPayData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        PrincipalPay prPay = task.getPrincipalPay();
        if (prPay == null) prPay = new PrincipalPay();
        prPay.toFlatPairs("principalPay_", pairs, true);
        return pairs;
    }
    

    /**
     * Get data included in Task.principalPay object (������ ��������� ��������� �����). 
     */
    public TreeMap<String, String> getInterestPayData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        InterestPay intPay = task.getInterestPay();
        if (intPay == null) intPay = new InterestPay();
        //if(intPay.getDescription()==null || intPay.getDescription().isEmpty())intPay.setDescription("не указан");
        //if(intPay.getPay_int()==null || intPay.getPay_int().isEmpty())intPay.setPay_int("не указана");
        intPay.toFlatPairs("interestPay_", pairs, true);
        return pairs;
    }

    /**
     * Get data included in Task.tranceComment field 
     */
    public TreeMap<String, String> getTranceCommentData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        pairs.put("tranceComment", Formatter.str(task.getTranceComment()));
        return pairs;
    }
    
    
    /**
     * Get data included in Task.tranceComment field 
     */
    public TreeMap<String, String> getInLimitForPrintFormData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        pairs.put("inLimitForPrintForm", (task.isInLimitForPrintForm() ? "True": "False"));
        return pairs;
    }

    
    
    /**
     * Get data included in Task.generalCondition object. 
     */
    public TreeMap<String, String> getGeneralConditionData()  {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        GeneralCondition condition = task.getGeneralCondition();
        if (condition == null) condition = new GeneralCondition();  
        condition.toFlatPairs("generalCondition_", pairs, true);
        return pairs;
    }
    
    /**
     * Get data included in Task.taskProcent object. 
     */
    public TreeMap<String, String> getTaskProcentData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        TaskProcent pr = task.getTaskProcent();
        if (pr == null) pr = new TaskProcent(); 
        pr.toFlatPairs("procent_", pairs, true);
        return pairs;
    }

    /**
     * Get data included in Task.supply object. 
     */
    public TreeMap<String, String> getSupplyData() {
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        TaskSupply supply = task.getSupply(); 
        if (supply == null) supply = new TaskSupply();  
        supply.toFlatPairs("supply_", pairs, true);
        return pairs;
    }

    /**
     * fills datasource with information about EarlyPayment  
     */
    public MailMergeDataSource getEarlyPaymentListData(TreeMap<String, String> map) {
        return getListData("earlyPaymentList", "ep_", EarlyPayment.class, task.getEarlyPaymentList(), true, map);
    }

    /**
     * fills datasource with information about contractors data of sublimits
     */
    public MailMergeDataSource getSublimitContractorsData(TreeMap<String, String> map) {
       return getListData("sublimit_contractors", "sl_", LimitTree.class, task.getMain().getLimitTreeList(), false, map);
    }

    
    /**
     * fills datasource with information about OTHER departments
     */
    public  MailMergeDataSource getOtherDepartmentsData(TreeMap<String, String> map) {
        final String TABLE_NAME = "header_otherDepartments";
        final String PREFIX = "od_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME); 
        try {
            boolean noRows = false; boolean firstElement = true;
            TreeMap<String, String> pairs = new TreeMap<String, String>();
            if (task.getHeader().getOtherDepartments().isEmpty()) {
                // according to businness requirements, we need at least one row in the list. 
                task.getHeader().getOtherDepartments().add(new TaskDepartment(null, new Department(null)));
                noRows = true;
            };                                
            
            for (TaskDepartment dep : task.getHeader().getOtherDepartments()) {
                pairs.clear();  
                dep.toFlatPairs(PREFIX, pairs, true);                        

                // generate list of Managers FIO data.
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (com.vtb.domain.TaskManager manager : dep.getManagers()) {
                    if (!first) sb.append(LINE_DELIMITER);
                    sb.append(Formatter.str(manager.getName()));    // NOT FIO ??? Check!!!
                    first = false;
                }
                pairs.put(PREFIX + "managersList_fio", sb.toString());
                
                // add column names
                if (firstElement) {                         
                    rs.addColumns(MailMergeDataSource.generateRow(pairs, 0).toArray());
                    firstElement = false;
                }
                rs.addRow(MailMergeDataSource.generateRow(pairs, 1));
            }
            // clearing after mix-in of empty row
            if (noRows) task.getHeader().getOtherDepartments().clear();                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in getOtherDepartmentsData() for the table " + TABLE_NAME + e.getMessage(), e); 
        }
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    
    /**
     * Generate sorted list of all Managers as String
     */
    public  String getManagersList() {
        try {
            TreeSet<String> groups = new TreeSet<String>();
            if (task.getHeader().getManagers() != null)  
                for (TaskManager manager : task.getHeader().getManagers()) 
                    groups.add(Formatter.str(manager.getName()));
           return listToString (groups, ";" + LINE_DELIMITER);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getManagersList. " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * Generate sorted list of all places as String
     */
    public  String getPlacesList() {
        try {
            TreeSet<String> groups = new TreeSet<String>();
            if (task.getHeader().getPlaces() != null)  
                for (Department dep : task.getHeader().getPlaces()) 
                    groups.add(Formatter.str(dep.getShortName()));
            return listToString (groups, ";" + LINE_DELIMITER);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getPlacesList. " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * Generate sorted list of all contractors groups as String
     */
    public  String getAllContractorsGroups() {
        try {
            TreeSet<String> groups = new TreeSet<String>();
            for (TaskContractor contractor : task.getContractors())
                if (contractor.getGroupList() != null) 
                    for (CompanyGroup group : contractor.getGroupList())
                        groups.add(Formatter.str(group.getName()));
           return listToString (groups, ";" + LINE_DELIMITER);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getAllContractorsGroups. " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * Generate sorted list of all currency list as String
     */
    public  String getCurrencyList() {
        try {
            TreeSet<String> groups = new TreeSet<String>();
            if (task.getCurrencyList() != null)
                for (TaskCurrency currency : task.getCurrencyList())
                    groups.add(getCurrencyName(currency.getCurrency().getCode()));
            return listToString (groups, ";" + LINE_DELIMITER);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getCurrencyList. " + e.getMessage(), e);
            return "";
        }
    }

    private String getCurrencyName(String code){
        if(code == null)
            return "";
        try{
            for(Currency cur : SBeanLocator.singleton().getCurrencyMapper().getCurrencyList())
                if(cur.getCode().equalsIgnoreCase(code))
                    return cur.getCurOne();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getCurrencyList. " + e.getMessage(), e);
        }
        if(code.equalsIgnoreCase("rur"))
            return "рубль";
        if(code.equalsIgnoreCase("usd"))
            return "доллар США";
        return code;
    }
    
    /**
     * Generate sorted list of all contractors as String
     */
    public  String getAllContractors() {
        try {
            TreeSet<String> groups = new TreeSet<String>();
            for (TaskContractor contractor : task.getContractors())
                if (contractor.getOrg() != null)
                    groups.add(Formatter.str(contractor.getOrg().getAccount_name()));
            return listToString (groups, ";" + LINE_DELIMITER);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getAllContractors. " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * 
     * @param cl Collection to iterate to
     * @param delimiter delimiter to use to delimite values
     * @return String with delimiter 
     */
    private  String listToString (Collection<String> cl, String delimiter) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = cl.iterator();
        while (it.hasNext()) {
            if (!first) sb.append(delimiter);
            sb.append(Formatter.str(it.next()));
            first = false;
        }
        return sb.toString(); 
    }
    
    /**
     * fills datasource with information about Contractors
     */
    public MailMergeDataSource getContractorsData(TreeMap<String, String> map) {
        final String TABLE_NAME = "contractors";
        final String PREFIX = "co_";
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME); 

        try {
            boolean noRows = false;
            TreeMap<String, String> pairs = new TreeMap<String, String>();
            if (task.getContractors().isEmpty()) {
                // according to businness requirements, we need at least one row (empty) in the list of contractors. 
                TaskContractor contractor = new TaskContractor(new Organization(), null, null);
                task.getContractors().add(contractor);
                noRows = true;
            }; 
            boolean firstElement = true;
            for (TaskContractor contractor : task.getContractors()) {
                pairs.clear();
                //сериализовать в pairs
                contractor.toFlatPairs(PREFIX, pairs, true);                        
                //начитать рейтинги и добавить их в pairs
                generateRatings(PREFIX, pairs, contractor);
                // add column names
                if (firstElement) {                         
                    rs.addColumns(MailMergeDataSource.generateRow(pairs, 0).toArray());
                    firstElement = false;
                }
                rs.addRow(MailMergeDataSource.generateRow(pairs, 1));
            }

            // clearing after mix-in of empty row
            if (noRows)  task.getContractors().clear();                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in getContractorsData(). " + TABLE_NAME + e.getMessage(), e); 
        }
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * Начитать рейтинги и добавить их в pairs для контрагента 
     * @param PREFIX -- используемый префикс
     * @param pairs  -- пары <ключ, значение>, куда рейтинги положим
     * @param contractor - сам контрагент, для которого 
     * Возвращаем изменную структуру pairs.
     */
    private void generateRatings(String PREFIX, TreeMap<String, String> pairs, TaskContractor contractor) {
        try {
            TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            //String branch = "";
            //String region = "";
            CalcHistoryInput input = new CalcHistoryInput();
    		input.setPartnerId(contractor.getOrg().getAccountid());
    		input.setRDate(new java.util.Date());
    		if(Config.enableIntegration()){
	    		CalcHistoryOutput output = ru.masterdm.integration.ServiceFactory.getService(RatingService.class).getKEKICalcHistory(input);
	    		if(output!=null){
	    			pairs.put(PREFIX + "rating_name1", "Рейтинг кредитного подразделения");
	    			pairs.put(PREFIX + "rating_value1", output.getRating());
	    			pairs.put(PREFIX + "rating_date1", Formatter.format(output.getRDate()));
	    			pairs.put(PREFIX + "branch", output.getBranch());
	    			pairs.put(PREFIX + "region", output.getRegion());
	    		} else {
	    			pairs.put(PREFIX + "rating_name1", "");
	    			pairs.put(PREFIX + "rating_value1", "");
	    			pairs.put(PREFIX + "rating_date1", "");
	    			pairs.put(PREFIX + "branch", "");
	    			pairs.put(PREFIX + "region", "");
	    		}
	            //подгрузить еще утвержденный рейтинг
	            ApprovedRating ar = processor.getApprovedRating(new Date(), contractor.getOrg().getAccountid());
	            if (ar != null) {
	                pairs.put(PREFIX + "rating_name5", ar.getName());
	                pairs.put(PREFIX + "rating_value5", ar.getRating());
	                pairs.put(PREFIX + "rating_date5", Formatter.format(ar.getDate()));
	            } else {
	                // already was set
	                pairs.put(PREFIX + "rating_name5", "Рейтинг КО");
	                pairs.put(PREFIX + "rating_value5", "");
	                pairs.put(PREFIX + "rating_date5", "");
	            }
    		}
            
            // add all lists as one element
            // generate list of OrgType descriptions data.
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            if (contractor.isMainBorrower()) {
                sb.append("Основной заемщик");
                first = false;                    
            }
            if (contractor.getOrgType() != null)
                for (ContractorType orgType : contractor.getOrgType()) {
                    if (!first) sb.append(LINE_DELIMITER);
                    if (!("Заемщик".equals(orgType.getDescription()) && contractor.isMainBorrower())) {
                        sb.append(Formatter.str(orgType.getDescription()));
                        first = false;
                    }
                }
            pairs.put(PREFIX + "orgType_description", sb.toString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            // set values manually
			pairs.put(PREFIX + "rating_name1", "");
			pairs.put(PREFIX + "rating_value1", "");
			pairs.put(PREFIX + "rating_date1", "");
			pairs.put(PREFIX + "branch", "");
			pairs.put(PREFIX + "region", "");

            pairs.put(PREFIX + "rating_name5", "Рейтинг КО");
            pairs.put(PREFIX + "rating_value5", "");
            pairs.put(PREFIX + "rating_date5", "");

            pairs.put(PREFIX + "orgType_description", "");
        }
    }

    /**
     * fills datasource with information about Opportunity Types
     */
    public  MailMergeDataSource getOpportunityTypesData(TreeMap<String, String> map) {
        return getListData("opportunityTypes", "op_", TaskProduct.class, task.getHeader().getOpportunityTypes(), true, map);
    }

    /**
     * fills datasource with information about Opportunity Types Dates
     */
    public MailMergeDataSource getOpportunityTypesDatesData(TreeMap<String, String> map) {
        return getListData("opportunityTypesDates", "opd_", TaskProduct.class, task.getHeader().getOpportunityTypes(), true, map);
    }

    /**
     * fills datasource with information about Other Goals
     */
    public MailMergeDataSource getOtherGoalsData(TreeMap<String, String> map) {
        MailMergeDataSource rs = new MailMergeDataSource("otherGoals");
        try {
            if (task.getMain().getOtherGoals() == null) {
                // according to businness requirements, we don't need rows in the list. 
                task.getMain().setOtherGoals(new ArrayList<OtherGoal>());
            };
            ArrayList<String> list = new ArrayList<String>();
            for (OtherGoal op : task.getMain().getOtherGoals()) list.add(Formatter.str(op.getGoal()));
            rs.addOneColumn("og_name", list.toArray());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in getOtherGoalsData. " + e.getMessage(), e); 
        }        
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }

    /**
     * fills datasource with information about StandardPriceCondition for Procent
     */
    public MailMergeDataSource getProcentStandardPriceConditionListData(TreeMap<String, String> map) {
        if (task.getTaskProcent() == null) task.setTaskProcent(new TaskProcent(null));
        return getListData("procent_standardPriceConditionList", "pspc_", StandardPriceCondition.class, task.getTaskProcent().getStandardPriceConditionList(), true, map);
    }

    /**
     * fills datasource with information about Commission for Procent (for Limit)
     */
    public MailMergeDataSource getCommissionListData(TreeMap<String, String> map) {
        if(task.getCommissionList() != null)
            for(Commission p : task.getCommissionList())
                if(p.getCurrency()!=null)
                    p.getCurrency().setCode(SBeanLocator.singleton().getDictService().moneyCurrencyDisplay(p.getValue(), p.getCurrency().getCode()));
        return getListData("commissionList", "com_", Commission.class, task.getCommissionList(), false, map);
    }

    /**
     * fills datasource with information about Commission for Procent (for Opportunity)
     */
    public MailMergeDataSource getFactPercentListData(TreeMap<String, String> map) {
    	ArrayList<FactPercent> list = new ArrayList<FactPercent>();
    	if ((task.getFactPercentList() != null) && (!task.getFactPercentList().isEmpty())) {
    		CompendiumCrmActionProcessor compenduimCrm =
    			(CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
    		// найдем индикативную ставку
    		String indicative = null;
    		for(FloatPartOfActiveRate fpar : compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(),null)){ 
		        if(fpar.getId().equals(task.getInd_rate())){
		        	indicative = fpar.getText();
		        }
    		}
    		for (FactPercent fp : task.getFactPercentList()) {
    			if (fp.getTranceId() == null) {
    				fp.setRate4Print( indicative);
    				list.add(fp);
    			}
    		}
    	}
    	return getListData("factList", "fact_", FactPercent.class, list, true, map);
    }

    /**
     * fills datasource with information about Tranches Fact data
     */
    public MailMergeDataSource getTrancheFactPercentListData(TreeMap<String, String> map) {
    	ArrayList<FactPercent> list = new ArrayList<FactPercent>();
    	if ((task.getFactPercentList() != null) && (!task.getFactPercentList().isEmpty())) {
    		for (FactPercent fp : task.getFactPercentList()) {
    			if (fp.getTranceId() != null) list.add(fp);
    		}
    	}
    	return getListData("trancheFactList", "trancheFact_", FactPercent.class, list, true, map);
    }

    /**
     * fills datasource with information about Tranches Fact data
     */
    public MailMergeDataSource getPremiumListData(TreeMap<String, String> map) {
    	return getListData("premiumList", "premium_", Premium.class, task.getPremiumList(), false, map);
    }

    
    /**
     * fills datasource with information about Commission for Procent (for Opportunity)
     */
    public MailMergeDataSource getFactOneLinePercentListData(TreeMap<String, String> map) {
    	ArrayList<FactPercent> list = new ArrayList<FactPercent>();
    	if ((task.getFactPercentList() != null) && (!task.getFactPercentList().isEmpty())) {
    		FactPercent first = task.getFactPercentList().get(0);
    		list.add(first);
    	}
    	return getListData("factShortList", "factShort_", FactPercent.class, list, true, map);
    }

    /**
     * fills datasource with information about Commission for Procent (for Opportunity)
     */
    public MailMergeDataSource getCommissionDealListData(TreeMap<String, String> map) {
        if(task.getCommissionDealList() != null)
            for(CommissionDeal p : task.getCommissionDealList())
                if(p.getCurrency()!=null)
                    p.getCurrency().setCode(SBeanLocator.singleton().getDictService().moneyCurrencyDisplay(p.getValue(),p.getCurrency().getCode()));
        return getListData("commissionDealList", "comD_", CommissionDeal.class, task.getCommissionDealList(), true, map);
    }

    /**
     * fills datasource with information about PaymentSchedule (for Opportunity)
     */
    public MailMergeDataSource getPaymentScheduleListData(TreeMap<String, String> map) {
        //DEBUG return getListData("paymentScheduleList", "paySch_", PaymentSchedule.class, task.getPaymentScheduleList(), true);
        if(task.getPaymentScheduleList() != null)
            for(PaymentSchedule p : task.getPaymentScheduleList())
                p.setCurrencyText(SBeanLocator.singleton().getDictService().moneyCurrencyDisplay(p.getAmount(),p.getCurrencyText()));
        return getListData("paymentScheduleList", "paySch_", PaymentSchedule.class, task.getPaymentScheduleList(), false, map);
    }
    
    
    /**
     * fills datasource with information about Fine for Procent
     */
    public MailMergeDataSource getFineListData(TreeMap<String, String> map) {
        ArrayList<Fine> fineList = task.getFineList();
        for(Fine fine :fineList){
            fine.generateColumn2();
            fine.generateColumn3();
        }
        return getListData("fineList", "fl_", Fine.class, fineList, true, map);
    }
    
    
    /**
     * fills datasource with information about otherCondition with type 1  
     */
    public  MailMergeDataSource getOtherConditionType1Data() {
        return getOtherConditionTypeData("otherConditionType1", "othCondType1_", 1, null);
    }
    
    
    /**
     * fills datasource with information about otherCondition with type 2  
     */
    public   MailMergeDataSource getOtherConditionType2Data() {
        return getOtherConditionTypeData("otherConditionType2", "othCondType2_", 2, null);
    }

    /**
     * fills datasource with information about otherCondition with type 3  
     */
    public   MailMergeDataSource getOtherConditionType3Data() {
        return getOtherConditionTypeData("otherConditionType3", "othCondType3_", 3, null);
    }

    /**
     * fills datasource with information about otherCondition with type 4  
     */
    public   MailMergeDataSource getOtherConditionType4Data() {
    	return getOtherConditionTypeData("otherConditionType4", "othCondType4_", 4, null);
    }
    public   MailMergeDataSource getOtherConditionType4DataW() {
    	return getOtherConditionTypeData("otherConditionType4W", "othCondType4W_", 4, "w");
    }
    public   MailMergeDataSource getOtherConditionType4DataG() {
    	return getOtherConditionTypeData("otherConditionType4G", "othCondType4G_", 4, "g");
    }
    public   MailMergeDataSource getOtherConditionType4DataD() {
    	return getOtherConditionTypeData("otherConditionType4D", "othCondType4D_", 4, "d");
    }

    /**
     * fills datasource with information about otherCondition with type 5  
     */
    public  MailMergeDataSource getOtherConditionType5Data() {
        return getOtherConditionTypeData("otherConditionType5", "othCondType5_", 5, null);
    }

    /**
     * fills datasource with information about otherCondition with type 6  
     */
    public  MailMergeDataSource getOtherConditionType6Data() {
        return getOtherConditionTypeData("otherConditionType6", "othCondType6_", 6, null);
    }

    /**
     * fills datasource with information about otherCondition with type 6  
     */
    public  MailMergeDataSource getOtherConditionType7Data() {
    	return getOtherConditionTypeData("otherConditionType7", "othCondType7_", 7, null);
    }
    public  MailMergeDataSource getOtherConditionType12Data() {
        return getOtherConditionTypeData("otherConditionType12", "othCondType12_", 12, null);
    }

    /**
     * Implementation method of gettting other condition with type = mode 
     */
    private  MailMergeDataSource getOtherConditionTypeData(String TABLE_NAME, String PREFIX, int mode, String supplyCode) {
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME);
        try {
            boolean noRows = false;  boolean firstElement = true;
            TreeMap<String, String> pairs = new TreeMap<String, String>();
            if (task.getOtherCondition().isEmpty()) {
                // according to businness requirements, we need at least one row (empty) in the list. 
                task.getOtherCondition().add(new OtherCondition(null, null));
                noRows = true;
            }; 
            for (OtherCondition cond : task.getOtherCondition()) {
            	if(supplyCode==null && cond.getSupplyCode()!=null || supplyCode!=null && !supplyCode.equals(cond.getSupplyCode()))
            		continue;
                pairs.clear();
                cond.toFlatPairs(PREFIX, pairs, true);
                if ((new Long(mode)).equals(cond.getType())) {
                    // add data of only type equals to MODE variable
                    rs.addRow(MailMergeDataSource.generateRow(pairs, 1));
                }
                // add column names
                if (firstElement) {                         
                    rs.addColumns(MailMergeDataSource.generateRow(pairs, 0).toArray());
                    firstElement = false;
                }
            }
            // clearing after mix-in of empty row
            if (noRows)  task.getOtherCondition().clear();                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in getOtherConditionTypeData() for table " + TABLE_NAME + e.getMessage(), e); 
        }
        if (log) rs.print(builder, logOnlyNames);
        return rs;        
    }

    /**
     * fills datasource with information about Deposit in Supply
     */
    public MailMergeDataSource getDepositListData(TreeMap<String, String> map) {
        return getListData("supplyDepositList", "dl_", Deposit.class, task.getSupply().getDeposit(), false, map);
    }

    /**
     * fills datasource with information of Additional Attributes sabout Deposit in Supply
     */
    public MailMergeDataSource getDepositAdditionalAttrsData() {
        return getMapData("supply_DepositAdditionalAttrs", "sdaa_", task.getSupply().getDepositKeyValue(), true);
    }
    
    /**
     * fills datasource with information about Warranty in Supply
     */
    public MailMergeDataSource getWarrantyListData(TreeMap<String, String> map) {
        return getListData("supplyWarrantyList", "wl_", Warranty.class, task.getSupply().getWarranty(), false, map);
    }

    /**
     * fills datasource with information about Guarantee in Supply
     */
    public MailMergeDataSource getGuaranteeListData(TreeMap<String, String> map) {
        return getListData("supplyGuaranteeList", "gl_", Guarantee.class, task.getSupply().getGuarantee(), false, map);
    }

    /**
     * fills datasource with information about Deposit in Supply
     */
    public MailMergeDataSource getPromissoryNoteListData(TreeMap<String, String> map) {
        if(task.getPromissoryNoteList() != null)
            for(PromissoryNote p : task.getPromissoryNoteList())
                p.setCurrency(SBeanLocator.singleton().getDictService().moneyCurrencyDisplay(p.getVal(),p.getCurrency()));
        return getListData("supplyPromissoryNoteList", "pn_", PromissoryNote.class, task.getPromissoryNoteList(), false, map);
    }
    
    /**
     * fills datasource with information about Tranches
     */
    public MailMergeDataSource getTranchesListData(TreeMap<String, String> map) {
        return getListData("tranches", "tra_", Trance.class, task.getTranceList(), true, map);
    }

    /**
     * fills datasource with information about Project Team Structurers
     */
    public MailMergeDataSource getProjectTeamStructurerListData(TreeMap<String, String> map) {
        return getListData("ptStructurers", "ptStr_", ProjectTeamMember.class, task.getProjectTeamStructurerList(), true, map);
    }

    /**
     * fills datasource with information about Project Team ClientManagers
     */
    public MailMergeDataSource getProjectTeamClientManagerListData(TreeMap<String, String> map) {
        return getListData("ptClientManagers", "ptCM_", ProjectTeamMember.class, task.getProjectTeamClientManagerList(), true, map);
    }

    /**
     * fills datasource with information about Project Team SPKZ
     */
    public MailMergeDataSource getProjectTeamSPKZListData(TreeMap<String, String> map) {
        return getListData("ptSPKZ", "ptSPKZ_", ProjectTeamMember.class, task.getProjectTeamSPKZList(), true, map);
    }

    /**
     * fills datasource with information about Project Team Strucurer Managers
     */
    public MailMergeDataSource getProjectTeamStrucurerManagerListData(TreeMap<String, String> map) {
        return getListData("ptStructurersManagers", "ptStrMgr_", ProjectTeamMember.class, task.getProjectTeamStrucurerManagerList(), true, map);
    }

    /**
     * fills datasource with information about Project Team Credit Analytics
     */
    public MailMergeDataSource getProjectTeamCreditAnalyticListData(TreeMap<String, String> map) {
        return getListData("ptCreditAnalytics", "ptCrAn_", ProjectTeamMember.class, task.getProjectTeamCreditAnalyticList(), true, map);
    }

    /**
     * fills datasource with information about Project Team Structurers
     */
    public MailMergeDataSource getProjectTeamProductManagerListData(TreeMap<String, String> map) {
        return getListData("ptProductManagers", "ptPrMgr_", ProjectTeamMember.class, task.getProjectTeamProductManagerList(), true, map);
    }

    /**
     * fills datasource with information about department Agreements
     */
    public MailMergeDataSource getDepartmentAgreementsListData(TreeMap<String, String> map) {
        return getListData("departmentAgreements", "depAgr_", DepartmentAgreement.class, task.getDepartmentAgreements(), true, map);
    }
    
    /**
     * fills datasource with information about contracts
     */
    public MailMergeDataSource getContractListData(TreeMap<String, String> map) {
        return getListData("contractsList", "ctrs_", Contract.class, task.getContractList(), true, map);
    }
    
    /**
     * Generate simple version of conclusions of given type (in HTML format). 
     */
    public String getConclusionSimpleData(String type) {
        try {
            StringBuilder conclusions = new StringBuilder("");
            for (ExtendText conclusion  : task.getExtendTexts()) {
                if (type.equals(conclusion.getDescription()))
                    conclusions.append("<p>" + conclusion.getContext() + "</p> ");
            }
            return conclusions.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getConclusionSimpleData for type " + type + " " +e.getMessage(), e);
            return "";
        }
    }

    /**
     * NOT IMPLEMENTED!
     */
    public MailMergeDataSource getVocConditionsData() {
        return null;
    }
    
    /***************************************************************************************************/
    /*                                        Service methods                                          */
    /***************************************************************************************************/

    /**
     * Get list<T> data of the gieven type T as a MailMergeDataSource object
     * @param <T> type of elements in the list
     * @param TABLE_NAME name of the table to use in report parameters
     * @param PREFIX prefix to use in report parameters
     * @param clazz Class of the object <T> 
     * @param collection collection of <T> objects
     * @param atLeastOneRow flag indicating whether to insert a row in the collection, if empty, or not.
     * According to businness rules, either the collection needs to have at least one element, or not.
     * @return MailMergeDataSource object sith filled data and columns.
     */
    public <T extends ru.masterdm.compendium.domain.VtbObject> MailMergeDataSource getListData(
            String TABLE_NAME, String PREFIX, Class<T> clazz, Collection<T> collection, boolean atLeastOneRow,
            TreeMap<String, String> map) {
        
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME);
        try {
            T instance = rs.createColumns(PREFIX, clazz);
            
            boolean noRows = false;
            if (collection == null) collection = new ArrayList<T>();
            if (atLeastOneRow && collection.isEmpty()) {
                // adds a row if needed by businness rules 
                collection.add(instance);
                noRows = true;
            };
            rs.fillRows(PREFIX, collection);
            if (map != null)
            	map.put(PREFIX + "_count", String.valueOf(collection.size()));
            // clearing after mix-in of empty row
            if (atLeastOneRow && noRows)  collection.clear();                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getListData() for " + TABLE_NAME 
                    + " and class " + clazz.getCanonicalName() + " " +  e.getMessage(), e); 
            return null;
        }
        if (log) rs.print(builder, logOnlyNames);
        return rs;
    }
    
    /**
     * Get map of the <String, String> as a MailMergeDataSource object
     * @param TABLE_NAME name of the table to use in report parameters
     * @param PREFIX prefix to use in report parameters
     * @param map<String, String> map of values 
     * @param atLeastOneRow flag indicating whether to insert a row in the collection, if empty, or not.
     * According to businness rules, either the collection needs to have at least one element, or not.
     * @return MailMergeDataSource object with filled data and columns.
     */
    public MailMergeDataSource getMapData(String TABLE_NAME, String PREFIX, Map<String, String> map, boolean atLeastOneRow) {
        MailMergeDataSource rs = new MailMergeDataSource(TABLE_NAME);
        try {
            rs.addColumn(PREFIX + "key");
            rs.addColumn(PREFIX + "value");
            boolean noRows = false;
            if (atLeastOneRow && map.isEmpty()) {
                // according to businness requirements, we need at least one row in the list.
                map.put(" "," ");
                noRows = true;
            }
            Iterator<Entry<String,String>> it = map.entrySet().iterator();
            while(it.hasNext()) {
                Entry<String, String> entry = it.next();
                ArrayList<String> list = new ArrayList<String>(); 
                list.add(Formatter.str(entry.getKey()));
                list.add(Formatter.str(entry.getValue()));
                rs.addRow(list.toArray());
            }
            // clear after self
            if (atLeastOneRow && noRows) map.clear();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in processing getMapData() for " + TABLE_NAME + " " +  e.getMessage(), e);
            return null;
        }
        if (log) rs.print(builder, logOnlyNames);
        return rs;        
    }
    

    /**
     * Prints map data. 
     */
    public void printMap(DocumentBuilder builder, TreeMap<String, String> pairs) {
        try {
            if (!log) return;
            Iterator<Entry<String, String>> it = pairs.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> pair = it.next();
                if (logOnlyNames) {
                    builder.writeln(pair.getKey().replaceAll("\n", " ").replaceAll("\r", " ") + ": ");
//                    System.out.println(pair.getKey().replaceAll("\n", " ").replaceAll("\r", " ") + ": ");
                }
                else {
//                    System.out.println(pair.getKey().replaceAll("\n", " ").replaceAll("\r", " ") +  ": " + pair.getValue().replaceAll("\n", " ").replaceAll("\r", " ") +"\t\t");
                    builder.writeln(pair.getKey().replaceAll("\n", " ").replaceAll("\r", " ") +  ": " + pair.getValue().replaceAll("\n", " ").replaceAll("\r", " ") +"\t\t"); 
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "error in printMap() method " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate document. Either a mailMerge, or just a print of prarameters to the file (look beneath). 
     * @throws Exception 
     */
    public void execWithRegions(MailMerge mm, MailMergeDataSource dataSource, Map<String, String> nameFields) {
        if (!log && (dataSource != null)) {
            if (mergeFields == null) 
                // initialize it once!
                mergeFields = getFieldsNames(builder.getDocument(), FieldType.FIELD_MERGE_FIELD);
            try { 
                int count = tableCountInDoc(dataSource.getTableName());
                if (count > 0)
                    for (int i = 1; i<= count; i++) {
                        dataSource.resetCursor();
                        // standard variant 
                        mm.executeWithRegions(dataSource);
                        // our version
                        //executeWithRegions(mm, dataSource);
                    }
            } catch (Exception e) {            
                String sourceName = (dataSource != null) ?  dataSource.getTableName() : "";
                LOGGER.log(Level.SEVERE,  "execWithRegions. Error in transformation dataSource: " + sourceName + " " + e.getMessage(), e);   
            }

            // create MailMergeDataSource with russian names
            MailMergeDataSource dataSourceRus = dataSource.copyWithChangeNames(nameFields);
            if (dataSourceRus != null)
                try { 
                    int count = tableCountInDoc(dataSourceRus.getTableName());
                    if (count > 0)
                        for (int i = 1; i<= count; i++) {
                            dataSourceRus.resetCursor();
                            // standard variant 
                            mm.executeWithRegions(dataSourceRus);
                            // our version
                            //executeWithRegions(mm,dataSourceRus);
                        }
                } catch (Exception e) {            
                String sourceName = (dataSourceRus != null) ?  dataSourceRus.getTableName() : "";
                LOGGER.log(Level.SEVERE,  "execWithRegions. Error in transformation dataSource: " + sourceName + " " + e.getMessage(), e);   
            }
        }
    }

    /**
     * Finds count of field in the document
     */
    private int tableCountInDoc(String tableName) {
        String fieldName = "TableStart:" + tableName;
        if ((mergeFields != null) && (mergeFields.containsKey(fieldName))) return mergeFields.get(fieldName).intValue();
        else return 0;
    }
    

    /**
     * Generate document. Either a mailMerge, or just a print of prarameters to the file (look beneath). 
     * @throws Exception 
     */
    public void exec(MailMerge mm, TreeMap<String, String> map) throws Exception {
        if (log) return;
        String[] fields = new String[1]; 
        Object[] fieldsValues  = new Object[1]; 
        List<String> simpleFieldsArray = new ArrayList<String>();
        List<Object> simpleFieldsValuesArray = new ArrayList<Object>();
        try {
            //String[] fields, Object[] values
            //�������� ���������� � ������
            Iterator<Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                // на всякий случай, проверим, что нет null
                if (entry.getKey() != null) {
	                simpleFieldsArray.add(entry.getKey());
	                // для null-строк кладем вместо них пустые строки ""
	                simpleFieldsValuesArray.add( entry.getValue() != null ? entry.getValue() : "");
                }
            }
            if (!simpleFieldsArray.isEmpty()) {
	            fields = simpleFieldsArray.toArray(fields);
	            fieldsValues = simpleFieldsValuesArray.toArray();
	            mm.execute(fields, fieldsValues);
            }
        } catch (Exception e) {            
        	StringBuilder sb = new StringBuilder();
        	for (int i = 0; i< fields.length; i++) {
        		sb.append(" " + fields[i] + " = " +  fieldsValues[i]);
        		if (i < fields.length - 1) sb.append(" ,"); 
        	}
        	LOGGER.log(Level.SEVERE,  "exec. Error in applying parameters: " + sb.toString() + "     " + e.getMessage(), e);
        	throw e;
        }
    }
    
    
    /**
     * visitor for Node. Helps to delete empty paragraphs.
     * @author Michail Kuznetsov
     */
    public class RemoveEmptyParagraphs extends DocumentVisitor {
        
        @Override
        public int visitParagraphStart(Paragraph paragraph)
        {
            // Check if string is empty
            if (!paragraph.hasChildNodes())
                try {
                    paragraph.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return VisitorAction.CONTINUE;
        }
    }
    
    /**
     * get inner class to remove empty paragraphs
     */
    public RemoveEmptyParagraphs getRemoveEmptyParagraphs() {
        return new RemoveEmptyParagraphs(); 
    }

    
    /**
     * @throws Exception 
     * 
     */
    public static void removeEmptyParagraphs(Document doc) throws Exception {
        Task2ReportDataHelper helper = new Task2ReportDataHelper(null, null, false); 
        RemoveEmptyParagraphs remEmptyPars = helper.getRemoveEmptyParagraphs();
        doc.accept(remEmptyPars);
    }
    
    /**
     * A useful function that you can use to easily append one document to another.
     * @param dstDoc The destination document where to append to.
     * @param srcDoc The source document (which is imported).
     * @throws Exception
     */
      public static void appendDoc(Document dstDoc, Document srcDoc) throws Exception
      {
          // Add to the same section in the destination document.
          srcDoc.getFirstSection().getPageSetup().setSectionStart(SectionStart.CONTINUOUS);

          // Loop through all sections in the source document.
          // Section nodes are immediate children of the Document node so we can just enumerate the Document.
          for (Section srcSection : srcDoc.getSections())
          {
              // Because we are copying a section from one document to another,
              // it is required to import the Section node into the destination document.
              // This adjusts any document-specific references to styles, lists, etc.
              //
              // Importing a node creates a copy of the original node, but the copy
              // is ready to be inserted into the destination document.
              Node dstSection = dstDoc.importNode(srcSection, true, ImportFormatMode.KEEP_SOURCE_FORMATTING);
    
              // Now the new section node can be appended to the destination document.
              dstDoc.appendChild(dstSection);
          }
      }
    
      
      /**
      * Inserts content of the external document after the specified node.
      * Section breaks and section formatting of the inserted document are ignored.
      * @param insertAfterNode Node in the destination document after which the content
      * should be inserted. This node should be a block level node (paragraph or table).
      * @param srcDoc The document to insert.
      * @return last inserted node (helps make consecutive inserts) 
      */
      public static Node insertDocument(Node insertAfterNode, Document srcDoc) throws Exception {
//          // Make sure that the node is either a paragraph or table.
          if (!((insertAfterNode.getNodeType() == NodeType.PARAGRAPH) || (insertAfterNode.getNodeType() == NodeType.TABLE)))
              throw new IllegalArgumentException("The destination node should be either a paragraph or table.");

          // We will be inserting into the parent of the destination paragraph.
          CompositeNode dstStory = insertAfterNode.getParentNode();

          // This object will be translating styles and lists during the import.
          NodeImporter importer = new NodeImporter(srcDoc, insertAfterNode.getDocument(), ImportFormatMode.KEEP_SOURCE_FORMATTING);

          // Loop through all sections in the source document.
          for (Section srcSection : srcDoc.getSections()) {
              // Loop through all block level nodes (paragraphs and tables) in the body of the section.
              for (Node srcNode : (Iterable<Node>) srcSection.getBody()) {
                  // Let's skip the node if it is a last empty paragraph in a section.
                  if (srcNode.getNodeType() == (NodeType.PARAGRAPH)) {
                      Paragraph para = (Paragraph)srcNode;
                      if (para.isEndOfSection() && !para.hasChildNodes()) continue;
                  }
                  // This creates a clone of the node, suitable for insertion into the destination document.
                  Node newNode = importer.importNode(srcNode, true);
                  // Insert new node after the reference node.
                  dstStory.insertAfter(newNode, insertAfterNode);
                  insertAfterNode = newNode;
              }
          }
          return insertAfterNode;
      }
       
    /***************************************************************************************************/
    /*                          Methods for search and removing fields and contents between them      */
    /***************************************************************************************************/

      /**
       * Returns map of fields of given type in the document in the form: key - fieldName, value - nomber of occurences
       * in the document
       *  
       */
      public static Map<String, Integer> getFieldsNames(Document doc, int type) {
          Map<String, Integer> fields = new HashMap<String, Integer>();
          NodeList fieldStarts = doc.selectNodes("//FieldStart");
          String[] tokens = new String[100];
          for (Node field : fieldStarts)
              if (((FieldStart) field).getFieldType() == type) {
                  String fieldName = findFieldName(field);
                  StringTokenizer st = new StringTokenizer(fieldName, " ");
                  int i = 0;
                  while (st.hasMoreTokens()) tokens[i++] = st.nextToken();
                  if (type == FieldType.FIELD_MERGE_FIELD) {
                      fieldName = tokens[1].trim();    
                  }
                  int count = 0;
                  if (fields.containsKey(fieldName)) count = fields.get(fieldName).intValue();
                  fields.put(fieldName, count + 1);
              }
          return fields; 
      }
      
      /**
       * Returns list of all mail merge field names in the document
       */
      public static String[] getMailMergeFiledNames(Document doc) throws Exception {
          return doc.getMailMerge().getFieldNames();
      }
      
      /**
       * Simple IF fields processing. Only =, and values to compute shouldn't include whitespaces and parenthesises.
       * The IF fiels is removed from the document (it's replaced by found condition) 
       * format is easy now:
       *    [0] [1]  [2] [3]   [4]     [5]
       *    if cond1 = cond2 "value1" "value2" \* mergeformat
       * If format is incorrect. just remove the field.
       */
      public static void updateIfFields(Document doc) {
          NodeList fieldStarts = doc.selectNodes("//FieldStart");
          String[] tokens = new String[100];
          for (Node field : fieldStarts)
              if (((FieldStart) field).getFieldType() == FieldType.FIELD_IF) {
                  // tokenize fields 
                  String ifStr = findFieldName(field);
                  StringTokenizer st = new StringTokenizer(ifStr, " ");
                  int i = 0;
                  while (st.hasMoreTokens()) tokens[i++] = st.nextToken();
                  String computedValue = "";
                  try {
                      // check format conditions
                      if ((i<6) 
                          || (!"if".equalsIgnoreCase(tokens[0])) // token[0] should be "if"
                          || (!"=".equalsIgnoreCase(tokens[2])) ) // token[2] should be "="
                          computedValue = "";
                      else {
                          // values can contain whitespaces. Find values in a different way. 
                          int firstParStart = ifStr.indexOf("\"", 0);
                          int firstParEnd = ifStr.indexOf("\"", firstParStart + 1);
                          int secondParStart = ifStr.indexOf("\"", firstParEnd + 1);
                          int secondParEnd = ifStr.indexOf("\"", secondParStart + 1);
                          if (  (firstParStart >=0) && (firstParStart <ifStr.length()) 
                              && (firstParEnd >=0) && (firstParEnd <ifStr.length())
                              && (secondParStart >=0) && (secondParStart <ifStr.length())
                              && (secondParEnd >=0) && (secondParEnd <ifStr.length())  
                              && (firstParEnd < secondParStart)) {
                              
                              if (firstParStart + 1 < firstParEnd) tokens[4] = ifStr.substring(firstParStart +1, firstParEnd);
                              else tokens[4] = "";
                              if (secondParStart + 1 < secondParEnd) tokens[5] = ifStr.substring(secondParStart + 1, secondParEnd);
                              else tokens[5] = "";
                              computedValue = (tokens[1].equalsIgnoreCase(tokens[3])) ? tokens[4] : tokens[5];
                          }
                      }
                  } catch (Exception e) {
                      LOGGER.log(Level.SEVERE, "Error in parsing IF statement " +  ifStr + " " +  e.getMessage(), e);
                      computedValue = "";
                  }
                  
                  try {
                      // replace IF field with found value.
                      field.getParentNode().insertBefore(new Run(doc, computedValue), field);
                      //can remove the IF field?
                      if (removeNodesWithSameParent(field, findNextSibling(field, NodeType.FIELD_END), false, true))
                          // remove IF field 
                          removeNodesWithSameParent(field, findNextSibling(field, NodeType.FIELD_END), true, true);
                  } catch (Exception e) {
                      LOGGER.log(Level.SEVERE, "Error in updateIfFields() " + e.getMessage(), e);                  
                  }
             }
      }

      /**
       * Finds nodes laying BETWEEN merge fields with names startFieldName and endFieldName
       * @param startFieldName starting field name
       * @param endFieldName end field name
       * @return Node[] { starting node (StartField), node AFTER starting node, previous to the end Node, end Node (Starting Field)} 
       */
      private static Node[] findNodesBetweenTwoMergeFields (Document doc, String startFieldName, String endFieldName) {
          // finds fields with names startFieldName and endFieldName.
          Node startField = findMergeField(doc, startFieldName);
          Node endField = findMergeField(doc, endFieldName);
          Node secondField = null; Node previousToLastField = null;
          
          if ((startField != null) && (endField != null)) {
              // finds nodes afer separator field. 
              Node fieldEnd = findNextSibling(startField, NodeType.FIELD_END);
              if ((fieldEnd != null) && (fieldEnd.getPreviousSibling() != null)) secondField =  fieldEnd.getNextSibling();
              previousToLastField = endField.getPreviousSibling();              
          }
          Node[] result = {startField, secondField, previousToLastField, endField };
          return result;
      }
      
      
    /**
     * Remove blocks from the document. Block is a piece of MSWord document, bracketed by merge fields with the names in the format:
     *      BLOCK_LIMIT_PREFIX<something>STARTING_BLOCK_SUFFIX, BLOCK_LIMIT_PREFIX<something>ENDING_BLOCK_SUFFIX,
     *      BLOCK_SUBLIMIT_PREFIX<something>STARTING_BLOCK_SUFFIX, BLOCK_SUBLIMIT_PREFIX<something>ENDING_BLOCK_SUFFIX,
     *      BLOCK_OPPORTUNITY_PREFIX<something>STARTING_BLOCK_SUFFIX, BLOCK_OPPORTUNITY_PREFIX<something>ENDING_BLOCK_SUFFIX,
     * Blocks should be well-formatted (minor deviations are permitted): the start of the block and the end of the block should 
     * be in the same level of the document (not allowed situations, where start of the block is placed BEFORE the table, 
     * and the end of the block is placed in the MIDDLE of the tables cell). 
     * @param doc document applied 
     * @param startWith starting symbols of the block field name.
     */
    public static void removeBlocks(Document doc, String startWith) {
        // generate map of fields to traverse to.
        NodeList fieldStarts = doc.selectNodes("//FieldStart");
        LinkedHashMap<String, Node> startingBlockFields = new LinkedHashMap<String, Node>();
        LinkedHashMap<String, Node> endingBlockFields = new LinkedHashMap<String, Node>();
        for (Node field : fieldStarts)
            if (((FieldStart) field).getFieldType() == FieldType.FIELD_MERGE_FIELD) {
                // is the field that starts a block?
                StringTokenizer st = new StringTokenizer(findFieldName(field).toLowerCase(), " ");
                while (st.hasMoreTokens()) {
                    String fieldName = st.nextToken();
                    if (fieldName.startsWith(startWith.toLowerCase()) && fieldName.endsWith(STARTING_BLOCK_SUFFIX.toLowerCase())) {
                        String withoutSuffix = fieldName.substring(0, fieldName.length() - STARTING_BLOCK_SUFFIX.length());
                        Node previous = startingBlockFields.put(withoutSuffix, field);
                        // only ONE occurence of field with this name in the document is allowed
                        if (previous != null) startingBlockFields.put(withoutSuffix, previous);
                        break;
                    }
                    if (fieldName.startsWith(startWith.toLowerCase()) && fieldName.endsWith(ENDING_BLOCK_SUFFIX.toLowerCase())) {
                        String withoutSuffix = fieldName.substring(0, fieldName.length() - ENDING_BLOCK_SUFFIX.length());
                        Node previous = endingBlockFields.put(withoutSuffix, field);
                        // only ONE occurence of field with this name in the document is allowed
                        if (previous != null) endingBlockFields.put(withoutSuffix, previous);
                        break;
                    }
                }
            }

        // traverse through fields.
        Iterator<Entry<String, Node>> it = startingBlockFields.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Node> entry = it.next();
            Node startingNode = startingBlockFields.get(entry.getKey());
            Node endingNode = endingBlockFields.get(entry.getKey());
            removeAllNodesBetweenTwoFields(doc, startingNode, endingNode);
        }
    }

    
    /**
     * Remove all occurences of the merge field with the name, starting with 'fieldName', in the document.  
     * @param doc document applied 
     * @param fieldName starting symbols of the field name.
     */
    public static void removeAllOccurencesOfMergeField(Document doc, String fieldName) {
        // finds fields with name fieldName and removes it from the document.
        NodeList fieldStarts = doc.selectNodes("//FieldStart");
        for (Node field : fieldStarts)
            if (((FieldStart) field).getFieldType() == FieldType.FIELD_MERGE_FIELD) {
                StringTokenizer st = new StringTokenizer(findFieldName(field).toLowerCase(), " ");
                while (st.hasMoreTokens()) {
                    String name = st.nextToken(); 
                    // compare with the first occurence of startField
                    if (name.startsWith(fieldName.toLowerCase())) {
                        // checks whether nodes are reachable (in the right order in the tree, have the same parent etc.)
                        if (removeNodesWithSameParent(field, findNextSibling(field, NodeType.FIELD_END), false, true)) 
                            // OK, the correctness is checked. Remove thr field.
                            removeNodesWithSameParent(field, findNextSibling(field, NodeType.FIELD_END), true, true);
                        break;
                    }
                }
            }
    }
    

    /**
     * Remove all nodes between two fields (nodes) in the document tree. the starting and final nodes are removed too.
     * The challenge is to find a nearest common parent of both nodes and his children in the subtrees of starting and final node,
     * remove the RIGHT subtrees from the common parent' child in the starting node subtree, remove all intermediate subtrees,
     * and remove the LEFT subtrees from the common parent' child in the final node subtree 
     * (not removing the tree to the final node itself).
     * @param doc document applied
     * @param startField starting block field
     * @param endField ending block field
     * @return true, if fields are found, false otherwise
     */
    private static boolean removeAllNodesBetweenTwoFields (Document doc, Node startField, Node endField) {
        // one or both fields are not found.
        if ((startField == null) || (endField == null)) return false; 

        // checks whether nodes are reachable (in the right order in the tree, have the same parent etc.)
        boolean canRemoveStartField = removeNodesWithSameParent(startField, findNextSibling(startField, NodeType.FIELD_END), false, true);
        boolean canRemoveEndField = removeNodesWithSameParent(endField, findNextSibling(endField, NodeType.FIELD_END), false, true);
        boolean canRemoveBetween = false;
        if (startField.getParentNode() == endField.getParentNode()) 
            // case 1. have the same parent. Remove all together (start, intermediate, end) 
            canRemoveBetween = removeNodesWithSameParent(startField, findNextSibling(endField, NodeType.FIELD_END), false, true);
        else {
            // check, whether can be removed, if in the different branches.
            canRemoveBetween = removeInDifferentBranches(startField, findNextSibling(endField, NodeType.FIELD_END), false);
        }

        if (canRemoveStartField && canRemoveEndField && canRemoveBetween) {
            if (startField.getParentNode() == endField.getParentNode()) {
                // case 1. Both fields have the same parent node.
                // remove data between end of the startField and the end of the endField. Remove the fields itselves.
                removeNodesWithSameParent(startField, findNextSibling(endField, NodeType.FIELD_END), true, true);
            } else {
                // case 2. Fields don't have the same parent.
                // remove data between fields
                removeInDifferentBranches(startField, findNextSibling(endField, NodeType.FIELD_END), true);                
            }
        }
        return true;
    }

    
    /**
     * Remove all nodes between two fields (nodes) in the document tree. the starting and final nodes are removed too.
     * The challenge is to find a nearest common parent of both nodes and his children in the subtrees of starting and final node,
     * remove the RIGHT subtrees from the common parent' child in the starting node subtree, remove all intermediate subtrees,
     * and remove the LEFT subtrees from the common parent' child in the final node subtree 
     * (not removing the tree to the final node itself).
     * @param doc document applied
     * @param startFieldName starting block field, that starts with these symbols
     * @param endFieldName ending block field, that starts with these symbols
     * @return true, if fielad are found, false otherwise
     */
    private static boolean removeAllNodesBetweenTwoMergeFields (Document doc, String startFieldName, String endFieldName) {
        // finds fields with names startFieldName and endFieldName.
        NodeList fieldStarts = doc.selectNodes("//FieldStart");
        Node startField = null; Node endField = null;
        String[] tokens = new String[100];
        for (Node field : fieldStarts) {
            FieldStart fl = (FieldStart) field;
            if (fl.getFieldType() == FieldType.FIELD_MERGE_FIELD) {
                String fieldName = findFieldName(field).toLowerCase();
                StringTokenizer st = new StringTokenizer(fieldName, " ");
                int i = 0;
                while (st.hasMoreTokens()) tokens[i++] = st.nextToken();
                fieldName = tokens[1].trim();    
                // compare with the first occurence of startField
                if ((startField == null) && fieldName.equals(startFieldName.toLowerCase())) startField = field;  //changed to equals  
                // compare with the first occurence of endField
                if ((endField == null) && fieldName.equals(endFieldName.toLowerCase())) endField = field;
                // finish the cycle
                if ((startField != null) && (endField != null)) break;
            }
        }
        return removeAllNodesBetweenTwoFields(doc, startField, endField);
    }
    
    
    /**
     * Remove all nodes between two fields (nodes) in the document tree. the starting and final nodes are removed too.
     * The challenge is to find a nearest common parent of both nodes and his children in the subtrees of starting and final node,
     * remove the RIGHT subtrees from the common parent' child in the starting node subtree, remove all intermediate subtrees,
     * and remove the LEFT subtrees from the common parent' child in the final node subtree 
     * (not removing the tree to the final node itself).
     * @param startNode node to start from 
     * @param endNode node to finish with
     * @param remove flag, if true, just checks for the correctness of document and parameters passed and possibility of removing  
     * @return whether the removal is successfull or whether the check for possibility of removal is passed well. 
     */
    private static boolean removeInDifferentBranches(Node startNode, Node endNode, boolean remove) {
        if ((startNode == null) ||(endNode == null)) return false;
        if (startNode.getParentNode() == endNode.getParentNode()) return false;
        try {
            Node[] parents = findSameParent(startNode, endNode);
            // don't have the same parent
            if (parents[0] == null) return false; 

            // remove all the rest of the first branch beginning from startNode.
            boolean canRemoveTheEndOfBranch = removeToTheEndOfBranch(parents[1], startNode, remove);
            // remove all intermediate branches between parents[1] and parents[2]
            boolean canRemoveIntermediateNodes = removeNodesWithSameParent(parents[1], parents[2], remove, false); 
            // remove the beginning of the branch parents[2] up to endNode.
            boolean canRemoveBeginningOfTheBranch = removeBeginningOfTheBranch(parents[2], endNode, remove);
            
            return (canRemoveTheEndOfBranch && canRemoveIntermediateNodes && canRemoveBeginningOfTheBranch);

        } catch (Exception e) {
            return false;
        }
    }
    

    /**
     * Removes all the RIGHT subBranches of the branch 'rootNode', starting from startFrom node. 
     * The branch leading to startFrom node, is preserved 
     */
    private static boolean removeToTheEndOfBranch(Node rootNode, Node startFrom, boolean remove) {
        if ((rootNode == null) ||(startFrom == null)) return false;
        try {
            Node node = startFrom; Node saved = null; 
            while (node != null) {
                saved = node.nextPreOrder(rootNode);
                if (remove) node.remove();
                node = saved;         
            }
            return true;
        } catch (Exception e) {
            return false;
        }        
    }
    
    /**
     * Removes all the LEFT subBranches of the branch 'rootNode', finishing with endTo. 
     * The branch, leading to the endTo, is preserved. The node endTo is removed too. 
     */
    private static boolean removeBeginningOfTheBranch(Node rootNode, Node endTo, boolean remove) {
        if ((rootNode == null) ||(endTo == null)) return false;
        try {
            Node node = endTo; Node saved = null;
            LinkedHashSet<Node> branch = findBranch(endTo);
            branch.remove(endTo);  // remove the node itself from the branch. It allows to remove it from the document.  
            while (node != null) {
                saved = node.previousPreOrder(rootNode);
                // remove, if not the nodes in the branch.
                if (remove && !(branch.contains(node))) 
                    node.remove();
                node = saved;         
            }
            return true;
        } catch (Exception e) {
            return false;
        }        
    }

    /**
     * Collects MSWord field name (may consists of different Runs).
     * @param  field Node of type NodeType.FIELD_START
     * @return collected field name  
     */
    private static String findFieldName(Node field) { 
        String result = "";
        Node separator = findNextSibling(field, NodeType.FIELD_SEPARATOR);
        if ((separator != null) && (separator.getPreviousSibling() != null)) {
            // search in the runs between start and field separators. Separator here exists and can be iterated to.
            for(Node curNode = field; curNode !=separator; curNode = curNode.getNextSibling())
               if (curNode.getNodeType() == NodeType.RUN) 
                   result += ((Run)curNode).getText();
        }
        return result.trim();
    }
    
    /**
     * Goes through siblings starting from the start node until it finds a node of the specified type or null. 
     * Both should have the same parent!
     * @return found node of specified type or null
     */
    private static Node findNextSibling(Node startNode, int nodeType) {
        if (startNode == null) return null;
        for (Node node = startNode; node != null; node = node.getNextSibling())
            if (node.getNodeType() == nodeType) return node;
        return null;
    }


    /**
     * Removes nodes from start up to the end node.
     * flag INCLUDE shows, whether to remove starting and end node or not. 
     * If remove = true, just check the correctness of removal porcedure instead of removing the contents.
     * Start and end are assumed to have the same parent.
     * @param startNode node to start from 
     * @param endNode node to finish with
     * @param remove flag, if true, just checks for the correctness of document and parameters passed and possibility of removing  
     * @param include flag, if true, remove not only the intermediate nodes, but the startNode and endNode too
     * @return whether the removal is successfull or whether the check for possibility of removal is passed well. 
     */
    private static boolean removeNodesWithSameParent(Node startNode, Node endNode, boolean remove, boolean include) {
        if ((startNode == null) ||(endNode == null)) return false;
        if (startNode.getParentNode() != endNode.getParentNode()) return false;
        try {
            Node curChild = startNode; boolean finished = false;
            if (!include) curChild =  curChild.getNextSibling();  // don't remove starting node
            while (curChild != null && (!finished))
                if (curChild == endNode) {
                    finished = true;
                    if (include && remove) curChild.remove();  // remove endNote
                } else {
                    Node nextChild = curChild.getNextSibling();
                    if (remove) curChild.remove();
                    curChild = nextChild;    
                }                

            // the final node is not found in document traversal.
            if (!finished) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Search for the nearest common parent of the two nodes and it's children to iterate through 
     * to get to the startNode and endNode  
     */
    private static Node[] findSameParent(Node startNode, Node endNode) {
        Node[] result = new Node[3];  // [0] -- Common parent
                                      // [1] -- child of common parent, which is in the branch leading to startNode
                                      // [2] -- child of common parent, which is in the branch leading to endNode
        if ((startNode == null) ||(endNode == null)) return result;

        // find branches
        LinkedHashSet<Node> branchToRoot1 = findBranch(startNode);
        LinkedHashSet<Node> branchToRoot2 = findBranch(endNode);
        // find all common parents of both branches by finding intersection of the sets.
        HashSet<Node> intersection = new HashSet<Node>(branchToRoot1);
        intersection.retainAll(branchToRoot2);
        // no intersection
        if (intersection.size() == 0) return result;
        
        // search for the nearest common parent. 
        // At the end of the cycle for startNode: parent == common parent, node.getParentNode(cur) = parent.
        Node parent = null; Node cur = null;
        Iterator<Node> it = branchToRoot1.iterator();
        while (it.hasNext()) {
            cur = parent;
            parent = it.next();
            if (intersection.contains(parent)) break;
        }
        result[0] = parent; result[1] = cur;

        // At the end of the cycle for endNode: parent == common parent, node.getParentNode(cur) = parent.
        parent = null;
        it = branchToRoot2.iterator();
        while (it.hasNext()) {
            cur = parent;
            parent = it.next();
            if (intersection.contains(parent)) break;
        }
        result[2] = cur;
        if (result[0] != parent);  // The impossible is possible. It's a bug. 
        return result;
    }

    /**
     * Fill in the branch from startNode to rootNode to the linked hashSet.
     * @param startNode
     * @return
     */
    private static LinkedHashSet<Node> findBranch(Node startNode) {
        LinkedHashSet<Node> branch =  new LinkedHashSet<Node>();
        Node cur = startNode;
        while (cur != null) {
            branch.add(cur);
            cur = cur.getParentNode();
        }
        return branch;
    }


    
    /*****************************************************************************************************/
    /*                                          Test methods                                             */ 
    /*****************************************************************************************************/
    
    private static String getNodeType(int nodeType) {
        switch (nodeType) {
            case NodeType.ANY : return "ANY";
            case NodeType.BODY : return "BODY";
            case NodeType.BOOKMARK_END : return "BODY";
            case NodeType.BOOKMARK_START : return "BOOKMARK_START";
            case NodeType.BUILDING_BLOCK : return "BUILDING_BLOCK";
            case NodeType.CELL : return "CELL";
            case NodeType.COMMENT : return "COMMENT";
            case NodeType.DOCUMENT : return "DOCUMENT";
            case NodeType.FIELD_END : return "FIELD_END";
            case NodeType.FIELD_SEPARATOR : return "FIELD_SEPARATOR";
            case NodeType.FIELD_START : return "FIELD_START";
            case NodeType.FOOTNOTE : return "FOOTNOTE";
            case NodeType.FORM_FIELD : return "FORM_FIELD";
            case NodeType.GLOSSARY_DOCUMENT : return "GLOSSARY_DOCUMENT";
            case NodeType.GROUP_SHAPE : return "GROUP_SHAPE";
            case NodeType.HEADER_FOOTER : return "HEADER_FOOTER";
            case NodeType.NULL : return "NULL";
            case NodeType.PARAGRAPH : return "PARAGRAPH";
            case NodeType.ROW : return "ROW";
            case NodeType.RUN : return "RUN";
            case NodeType.SECTION : return "SECTION";
            case NodeType.SHAPE : return "SHAPE";
            case NodeType.SMART_TAG : return "SMART_TAG";
            case NodeType.SPECIAL_CHAR : return "SPECIAL_CHAR";
            case NodeType.SYSTEM : return "SYSTEM";
            case NodeType.TABLE : return "TABLE";
        }
        return " ";
    }
    
    /**
     * Test method to remove fields
     * @param doc
     * @throws Exception
     */
    public static void testRemoveBlocks(Document doc, String startWith) throws Exception
    {
        traverseNode(doc, "");
        removeAllNodesBetweenTwoMergeFields(doc, 
                BLOCK_LIMIT_PREFIX + "_01_" + STARTING_BLOCK_SUFFIX, BLOCK_LIMIT_PREFIX + "_01_" + ENDING_BLOCK_SUFFIX);
        //removeAllNodesBetweenTwoFields(doc, 
        //      BLOCK_LIMIT_PREFIX + "_03_" + STARTING_BLOCK_SUFFIX, BLOCK_LIMIT_PREFIX + "_03_" + ENDING_BLOCK_SUFFIX);
        removeAllNodesBetweenTwoMergeFields(doc, 
                BLOCK_LIMIT_PREFIX + "_04_" + STARTING_BLOCK_SUFFIX, BLOCK_LIMIT_PREFIX + "_04_" + ENDING_BLOCK_SUFFIX);
        removeAllNodesBetweenTwoMergeFields(doc, 
                BLOCK_LIMIT_PREFIX + "_02_" + STARTING_BLOCK_SUFFIX, BLOCK_LIMIT_PREFIX + "_02_" + ENDING_BLOCK_SUFFIX);
        removeAllNodesBetweenTwoMergeFields(doc, 
                BLOCK_LIMIT_PREFIX + "_05_" + STARTING_BLOCK_SUFFIX, BLOCK_LIMIT_PREFIX + "_05_" + ENDING_BLOCK_SUFFIX);
        traverseNode(doc, "");
    }

    /**
     * Order: Parent, after that -- all children, next brother, all his children etc. This way. In the depth.
     * Father - Left Sibling - Right Sibling.
     */
    public static void traverseNode(Node node, String prefix)
    {
        boolean printIt = false;
        if (!printIt) return;
        System.out.println(prefix + getNodeType(node.getNodeType()) + " " + node.toString()); // + ": " + childNode.getText());
        if ((node.getNodeType() == NodeType.FIELD_END) || (node.getNodeType() == NodeType.FIELD_END) || (node.getNodeType() == NodeType.FIELD_SEPARATOR)
            || (node.getNodeType() == NodeType.RUN)) 
            System.out.println(prefix + ":" + node.getText());

        try {
        if (node.getNodeType() == NodeType.CELL) 
        	System.out.println("cell: " + ((Cell)node).getCellFormat().getVerticalMerge());
        } catch (Exception e) {}

        // Recurse into the node if it is a composite node.
        if (node.isComposite()) {
            int i = 100;
            for (Node childNode = ((CompositeNode)node).getFirstChild(); childNode != null; childNode = childNode.getNextSibling())
            {
                traverseNode(childNode, prefix + i + ".");
                i++;
            }
        }
    }

    /**
     * My own implementation of aspose.executeWithRegions(IMailMergeDataSource dataSource)
     * mail merge with regions only works when the start and end nodes are paragraphs 
     * inside one section or paragraphs inside cells of one table row.
     */
    public void executeWithRegions(MailMerge mm, MailMergeDataSource dataSource) throws Exception {
        executeWithRegions(builder.getDocument(), dataSource);
    }
    
    
    
    /**
     * Returns common parent of two nodes
     * @param startField
     * @param endField
     * @return found parent
     */
    public static Node findCommonParent (Node startField, Node endField) {
        if ((startField == null) || (endField == null)) return null;
        Node startFieldParent = startField.getParentNode();
        Node endFieldParent = endField.getParentNode();
        while (startFieldParent != endFieldParent) {    // POINTER equality, not by the contents (.equals())
            startFieldParent = startFieldParent.getParentNode();
            endFieldParent = endFieldParent.getParentNode();
        }
        return startFieldParent;
    }
    
    /**
     * Fills Tree with row values in the form {columnName: columnValue }
     */
    private TreeMap<String, String> getRowValues(MailMergeDataSource dataSource) {
        Object[] fieldValue = new Object[1]; 
        TreeMap<String, String> rowValues = new TreeMap<String, String>();
        Iterator<String> it = dataSource.getColumnNames().keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            if (dataSource.getValue(key, fieldValue)) 
                try {
                    rowValues.put(key, fieldValue[0].toString());
                } catch (Exception e) {}
        }
        return rowValues;
    }
    
    
    @SuppressWarnings("unused")
    private static void testSaveDoc(Document doc) {
        final String outputTaskFilePath = "C:/0/limit_decision.doc";
        // save document
        try {
            String fileName = outputTaskFilePath;
            File f = new File(fileName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream fos = new FileOutputStream(fileName);
            doc.save(fos, SaveFormat.DOC);
            fos.close();
            traverseNode(doc, "result ");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "TaskBasedReport couldn't be saved. The reason: " + e.getMessage(), e);
            return;
        }
    }
    
    /**
     * Removes first occurence of merge field sith a given name in the document 
     * @param fieldName name of the merge field
     * @param doc
     * @return true, if found and removed; false otherwise
     */
    public static boolean removeMergeField(Document doc, String fieldName) {
        Node startFieldNode = findMergeField(doc, fieldName);
        if (startFieldNode == null) return false;
        return removeMergeField(startFieldNode);
    }
    
    /**
     * Removes all nodes of the merge field.
     * @param startFieldNode node of the type START_FIELD od the given merge field
     * @return true, if removed; false otherwise
     */
    public static boolean removeMergeField(Node startFieldNode) {
        if (startFieldNode == null) return false;
        // checks whether nodes are reachable (in the right order in the tree, have the same parent etc.)
        if (removeNodesWithSameParent(startFieldNode, findNextSibling(startFieldNode, NodeType.FIELD_END), false, true)) { 
            // OK, the correctness is checked. Remove the field.
            removeNodesWithSameParent(startFieldNode, findNextSibling(startFieldNode, NodeType.FIELD_END), true, true);
            return true;    
        }
        else return false;
    }
    
    /**
     * Finds first occurence of merge field start node in the document
     * @param doc
     * @param fieldName name of the merge field
     * @return found start node of merge field. If not found, returns null
     */
    public static Node findMergeField(Document doc, String fieldName) {
        NodeList fieldStarts = doc.selectNodes("//FieldStart");
        String[] tokens = new String[100];
        for (Node field : fieldStarts) {
            FieldStart fl = (FieldStart) field;
            if (fl.getFieldType() == FieldType.FIELD_MERGE_FIELD) {
                String name = findFieldName(field).toLowerCase();
                StringTokenizer st = new StringTokenizer(name, " ");
                int i = 0;
                while (st.hasMoreTokens()) tokens[i++] = st.nextToken();
                name = tokens[1].trim();    
                if (name.equals(fieldName.toLowerCase())) return field;  // changed from contains to equals  
            }
        }
        return null;
    }
    
    /**
     * Reads zone from where the insertion took place. 
     */
    private Node readManipulationMark(Document doc, int type) {
        try {
            Paragraph para = doc.getFirstSection().getBody().getParagraphs().get(0);
            switch (type) {
               case NodeType.ROW : 
                   return doc.getFirstSection().getBody().getTables().get(0).getFirstRow();
               case NodeType.PARAGRAPH : 
                   if (para == null) para = insertParagraph(doc);
                   if (para.getFirstChild() == null) 
                       para.appendChild( new Run(doc));
                   return para.getFirstChild();
                   
               case NodeType.CELL :
                   return para; 
               
               case NodeType.BODY :
                   return para;                
               default:
                   LOGGER.severe("readManipulationMark switch error: unsupported case " + type);
                   return null;
           } 
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }
    }

    /**
     * эмулируем, вставляя пустой абзац
     */
    private static Paragraph insertParagraph (Document doc) throws Exception {
        Paragraph para = new Paragraph(doc);
        doc.getFirstSection().getBody().appendChild(para);
        return para;
    }
    
    /**
     * Create zone in a new document where all manipulations should take place.
     * @param type type of the node which should be the root node.  
     * Returns link to the node which will be the elder brother node to all of the nodes to manipulate with.  
     */
    private CompositeNode createManipulationZone(Document doc, int type) {
        try {
             doc.removeAllChildren();
             Section section = new Section(doc);
             doc.appendChild(section);
    
             // The section that we created is empty, lets populate it. The section needs at least the Body node.
             Body body = new Body(doc);
             section.appendChild(body);
    
             // The body needs to have at least one paragraph.
             Paragraph para = new Paragraph(doc);
             body.appendChild(para);
            
            switch (type) {
                case NodeType.ROW : 
                    // row can't be first element in the document. So, create table element and empty row element
                    Table table = new Table(doc);
                    body.appendChild(table);
                    return table;
                
                case NodeType.PARAGRAPH : 
                    // copy the paragraph. So, return run
                    return para;
                    
                case NodeType.CELL :
                    // copy from paragraph to paragraph. So, return paragraph
                    return body;
                
                case NodeType.BODY :
                    // copy from paragraph to paragraph. So, return paragraph
                    return body;
                
                default:
                    // let it be paragraph
                    return para;
            } 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Copy or check nodes from the startField to endField nodes as brothers of elderBrother in the different document
     * If startField and endField don't have the same parent, finds the common parent and copy it and all it's children   
     * Insert nodes as menor brothers of elderBrother, if it's not null. Otherwise iserts as first children.
     * @param parent node, to which the copying nodes will be appended as children  
     * @param elderBrother node, to which the copying nodes will be appended (as last children if isParent is true,
     * as menor brothers otherwise)  
     * @param startField node to start copy with
     * @param endField node to finish copying with. If endField is null, then copy all rest brothers   
     * @param doCopy whether to copy nodes on just check
     * @return last inserted node (for consecutive insertions), if copy is successful. Null otherwise
     */
    private Node copyNodes(CompositeNode parent, Node elderBrother, Node startField, Node endField, boolean doCopy) {
        if ((parent == null) || (startField == null)) return null;
        // elderBrother should be a son of the parent.
        if ((elderBrother != null) && (elderBrother.getParentNode() != parent)) return null;
        boolean success = true;
        boolean afterEndField = false;
        Node node = startField;
    
        // This object will be translating styles and lists during the import.
        NodeImporter importer;
        Node importedNode = null;
        try {
            importer = new NodeImporter(startField.getDocument(), parent.getDocument(), ImportFormatMode.KEEP_SOURCE_FORMATTING);
            Node startFieldParent = findCommonParent(startField, endField);
            if ((endField != null) && (startFieldParent != startField.getParentNode()) 
                // for section or cell copy list of parahrapsh (see below)
                && (!((startFieldParent.getNodeType() == NodeType.CELL) || (startFieldParent.getNodeType() == NodeType.BODY)))) {
                // copy the common parent and all it's children rather than  list of nodex from startField to endField
                importedNode = importer.importNode(startFieldParent, true);
                if (doCopy) {
                    if (elderBrother == null) parent.appendChild(importedNode);
                    else parent.insertAfter(importedNode, elderBrother);
                }
                return importedNode;
            } else {
                while (node != null && !afterEndField) {
                    importedNode = importer.importNode(node, true);
                    if (doCopy) { 
                        if (elderBrother == null) parent.appendChild(importedNode);
                        else parent.insertAfter(importedNode, elderBrother);
                        elderBrother = importedNode;
                    }
                    if (node == endField) afterEndField = true;
                    node = node.getNextSibling();
                }
                if (success) return importedNode;
                else return null;
            }        
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Removes all siblings of node to the beginning of siblings chain. The node passed as parameter is removed too. 
     */
    private static void trimBeginning(Node node) {
        trim(node, false);
    }
    
    /**
     * Removes all siblings of node to the end of siblings chain. The node passed as parameter is removed too. 
     */
    private static void trimEnd(Node node) {
        trim(node, true);
    }

    /**
     * Removes all siblings of node in the given direction. The node passed as parameter is removed too.
     * @param node node to start removal 
     * @param direction if true, removes in the nextSibling direction, false -- removes in the previousSibling direction 
     */
    private static void trim(Node node, boolean direction) {
        try {
            if (node == null) return;
            CompositeNode parent = node.getParentNode();
            while (node != null) {
                Node next = (direction) ? node.getNextSibling() : node.getPreviousSibling();
                parent.removeChild(node);
                node = next;    
            }
        } catch (Exception e) {
            LOGGER.severe("can't remove node");
        }
    }
    
    /**
     * Internal implementation of aspose.executeWithRegions(IMailMergeDataSource dataSource)
     * mail merge with regions only works when the start and end nodes are paragraphs 
     * inside one section or paragraphs inside cells of one table row.
     */
    private void executeWithRegions(Document doc, MailMergeDataSource dataSource) throws Exception {
        while (true) {
            // @TEST System.out.println("                     TABLE: " + dataSource.getTableName());
            // Finds all occurences of block TableStart - TableEnd in the cycle
            Node[] range = findNodesBetweenTwoMergeFields(
                                doc, 
                                START_TABLE_PREFIX + dataSource.getTableName(), 
                                END_TABLE_PREFIX + dataSource.getTableName());
            Node startField = range[1]; Node endField = range[2]; Node tableEndPrefixField = range[3];
            if (startField == null) startField = range[0];  // bad idea. But works anyway
            if (endField == null) endField = tableEndPrefixField;  // bad idea. But works anyway
            if ((startField == null) || (endField == null)) break;
    
            CompositeNode commonParent = (CompositeNode) findCommonParent(startField, endField);
            int parentType = commonParent.getNodeType();
            boolean theSameParent = (startField.getParentNode() == commonParent);
            
            // build the document by parts.
            traverseNode(commonParent.getParentNode(), "source: ");
            Document mergedDoc = new Document();
            CompositeNode mergedDocMark = createManipulationZone(mergedDoc, parentType);
            traverseNode(mergedDoc, "merged: ");
            
            if ((parentType == NodeType.BODY) || (parentType == NodeType.CELL)) {
                // to avoid work with trees and branches, just moves startField and endField from FieldStart level
                // to Paragraph level
                startField = startField.getParentNode();
                endField = endField.getParentNode();
                if ((startField.getNodeType() != NodeType.PARAGRAPH) || (endField.getNodeType() != NodeType.PARAGRAPH)) {
                    LOGGER.severe("Can't copy document. TableStart and TableEnd should be in paragraph level! Now they are not the same");
                }
            }
            
            // check for correctness of hierarchy and copy nodes there
            if (!theSameParent || (copyNodes(mergedDocMark, null, startField, endField, false) != null)) {
                dataSource.resetCursor();
                Node lastInsertedTemplateMark = null;
                //if (parentType == NodeType.PARAGRAPH) lastInsertedTemplateMark = mergedDocMark;
                CompositeNode whereInsertParent = mergedDocMark; 
    
                while (dataSource.moveNext()) {
                    // iterate through rows of the table
                    // copy template from the original document.
                    Document template = new Document();
                    CompositeNode templateMark = createManipulationZone(template, parentType);
                    if (copyNodes(templateMark, null, startField, endField, true) != null) {
                        traverseNode(template, "template: ");
                        // mail merge fields of the given row 
                        exec(template.getMailMerge(), getRowValues(dataSource));
                        traverseNode(template, "mailmerge: ");
                        // ATTENTION!: mailMerge поля документа (которые не в таблице) надо вызывать ПОСЛЕ всех mailmerge всех таблиц,
                        // потому что при mailMerge таблиц они размножаются
                        
                        if (!theSameParent) {
                            if ((parentType == NodeType.BODY) || (parentType == NodeType.CELL)) {
                                // remove the beginning of paragraph with start field mark
                                Node startFieldMark = findMergeField(template, START_TABLE_PREFIX + dataSource.getTableName());
                                startFieldMark = findNextSibling(startFieldMark, NodeType.FIELD_END);
                                trimBeginning(startFieldMark);
                                // remove the end of paragraph with start field mark
                                Node endFieldMark = findMergeField(template, END_TABLE_PREFIX + dataSource.getTableName());
                                trimEnd(endFieldMark);
                            } else {
                                // in this case merge remove only fields marks.
                                removeMergeField(template, START_TABLE_PREFIX + dataSource.getTableName());
                                removeMergeField(template, END_TABLE_PREFIX + dataSource.getTableName());
                            }
                        }
                        traverseNode(template, "removed: ");
                        
                        // делаем mailMerge для вложенных таблиц
                        List <MailMergeDataSource> children = dataSource.getChildDataSources(dataSource.getCurrentIndex());
                        if ((children != null) && (!children.isEmpty()))
                            for(MailMergeDataSource childTable : children) 
                                executeWithRegions(template, childTable);
                        
                        // append to the merge document.
                        Node from = readManipulationMark(template, parentType);
    
                        if (copyNodes(whereInsertParent, lastInsertedTemplateMark, from, null, false) != null)
                            lastInsertedTemplateMark = copyNodes(whereInsertParent, lastInsertedTemplateMark, from, null, true);
                        else LOGGER.severe("Can't copy document");
                        traverseNode(mergedDoc, "template added: ");
                    }
                }
            } else ; // TODO: error message&&&
    
            // replace them with the generated ones.
            Node from = readManipulationMark(mergedDoc, parentType);  
            if (parentType == NodeType.ROW) {
                // insert into the table one row
                CompositeNode table = commonParent.getParentNode();
                if (copyNodes(table, commonParent, from, null, false) != null)
                    copyNodes(table, commonParent, from, null, true);
                table.removeChild(commonParent);
            }
            if (parentType == NodeType.PARAGRAPH) {
                from = readManipulationMark(mergedDoc, parentType);
                
                // insert runs into the paragraph 
                Node fieldMarkEnd = findNextSibling(tableEndPrefixField, NodeType.FIELD_END);
                if (copyNodes(commonParent, fieldMarkEnd, from, null, false) != null)
                    copyNodes(commonParent, fieldMarkEnd, from, null, true);
                // remove merge fields
                removeAllNodesBetweenTwoMergeFields(
                        doc, 
                        START_TABLE_PREFIX + dataSource.getTableName(), 
                        END_TABLE_PREFIX + dataSource.getTableName()
                  );
            }
            if ((parentType == NodeType.BODY) || (parentType == NodeType.CELL)) {
                // in this case 1st parahraph is an empty one. We do not use it. Use always the second one
                CompositeNode parent = from.getParentNode(); 
                parent.removeChild(from);
                from = readManipulationMark(mergedDoc, parentType);
    
                // insert several paragraphs into the document section or cell after elderBrother Paragraph 
                Node elderBrother = tableEndPrefixField.getParentNode();
                if (copyNodes(commonParent, elderBrother, from, null, false) != null)
                    copyNodes(commonParent, elderBrother, from, null, true);
                traverseNode(doc, "before removal: ");
                // remove all children from startField to endField (here they are paragraphs or tables). 
                CompositeNode bodyOrCell = startField.getParentNode();
                Node iter = startField;
                while((iter != endField) && (iter != null)) {
                    Node next = iter.getNextSibling();
                    bodyOrCell.removeChild(iter);
                    iter = next;
                }
                bodyOrCell.removeChild(endField);                    
            }
            traverseNode(doc, "inserted: ");            
            // testSaveDoc(doc);
    
            //@TEST System.exit(0);
        } // end while
    }
    
    /**
     * Удалить пустые строки из таблицы согласно алгоритму.
     * @param doc
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeEmptyTableRows(Document doc, boolean delete) throws Exception {
    	for (Section section : doc.getSections()) {
	    	NodeCollection tables = section.getBody().getChildNodes(NodeType.TABLE, false);
	    	Iterator<Table> it = (Iterator<Table>)tables.iterator();
	    	while (it.hasNext()) {
	    		Table table = it.next();
	    		if (removeEmptyTable(doc, table, false, delete)) {
	    			// iterator is invalid!
	    			// try from the beginning
	    			tables = section.getBody().getChildNodes(NodeType.TABLE, false);
	    			it = (Iterator<Table>)tables.iterator();
	    		}
	    	}
    	}	
    }

    /**
     * Рассмотрим одну таблицу и удалим пустые строки в ней (либо саму таблицу, если все строки пустые)
     * @param table
     * @param boolean recursive показывает, работаем ли с таблицей САМОГО ВНЕШНЕГО, первого уровня (false) или внутреннего.
     * @throws Exception
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean removeEmptyTable(Document doc, Table table, boolean recursive, boolean delete) throws Exception {
		// такая ситуация возможна, т.к. NodeComposition включает ВСЕ таблицы, даже вложенные, а не только таблицы первого уровня
		// Т.е. мы можем обработать ее дважды, даже уже удаленную
		if (table.getParentNode() ==  null) return false;

		Row row = table.getFirstRow();
		while (row != null) {
		    // подходит ли в качестве кандидата на удаление?
		    boolean inPattern = true; 
		    
	    	// Вариант 1. Удаляем, если все  в данной строке колонки пустые, кроме первой.
		    // Iterate through all cells in the row
		    for (Cell cell : row.getCells()) {
	        	// Пробуем удалить содержимое ячейки. Ситуация 4.
	        	NodeCollection tablesInCell = cell.getChildNodes(NodeType.TABLE, true);
	        	if ((tablesInCell != null) && (tablesInCell.getCount() > 0)) {
	        		Iterator<Table> it = (Iterator<Table>)tablesInCell.iterator();
	    	    	while (it.hasNext()) {
	    	    		Table tableInCell = it.next();
	        			if (removeEmptyTable(doc, tableInCell, true, delete)) {
	        				// итератор невалиден. Заново.
	        				tablesInCell = cell.getChildNodes(NodeType.TABLE, true);
	        				it = (Iterator<Table>)tablesInCell.iterator();
	        			}
	        		}
	        	}
		    	
		    	int cellIndex = row.getCells().indexOf(cell);
		        // Get the content of this cell
		        //String cellText = cell.toTxt().trim();
		        if (cellIndex == 0) {
		        	if (row.getCells().getCount() == 1)
		        		inPattern = isCellEmpty(cell);  // если пустая ячейка, и она одна, то будем удалять
		        	else ; // больше одного столбца в строке. Первая ячейка может быть непустой или непустой. Это неважно
		        } else {
		        	// последующие ячейки в строке. Должны быть пусты.
		        	inPattern = isCellEmpty(cell);
		        }
		        if (!inPattern) // не подходит в кандидаты. Дальше проверять бессмысленно. 
		        	break;
		    }
		    if (delete && !recursive)
		    	inPattern |= isMarkedToDeleteRow(row);
		    // удаление строки.
		    if (inPattern) {
		    	Row next = (Row) row.getNextSibling();
		    	row.remove();
		    	row = next;
		    } else {
		    	row = (Row) row.getNextSibling();
		    }
		}
		
		// Проверяем случай 3. Только для самых внешних таблиц
	    if (!recursive) {
	    	mergedVerticalCellsCase(table);
	    }
		
		if (!table.hasChildNodes()) {
			// пустая таблица. Нам такая не нужна.
			table.remove();
			LOGGER.info("REMOVE TABLE!");
			return true; 
		}
		return false;
	}
	
	private static boolean isMarkedToDeleteRow(Row row) {
		boolean result = false;
		try {
			String rowText = row.getText();
			if (rowText != null && rowText.contains("DELETE"))
				result = true;
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	/**
	 * Отдельно рассмотрим случай case 3, когда первая ячейка ВИЗУАЛЬНО занимает несколько строк (в DOM это не так, на самом деле). В MSWord это хитрая вещь, регулируется как merge vertically cells.
	 * Не как в html.
	 * @param table таблица, над которой работаем 
	 * @throws Exception
	 */
	private static void mergedVerticalCellsCase(Table table) throws Exception {
		// Iterate through all rows in the table
		Row r = table.getFirstRow();
		while (r != null) {
			if (getFirstCellVerticalMerge(r) != CellMerge.FIRST) {
				r = (Row) r.getNextSibling();
				continue;
			}
			// нашли начало.
			Row merged = (Row) r.getNextSibling();
			if (getFirstCellVerticalMerge(merged) != CellMerge.PREVIOUS) merged = null;
			while (merged != null) {
				// пробуем удалить последующие записи (хотя бы часть) 
				if (canBeDeletedMergedRow(merged)) {
					Row previous = (Row) merged.getPreviousSibling();
					LOGGER.info("case 3. removed merged line. " + merged);
					merged.remove();
					merged = previous; 
				}
				// next iteration.
				merged = (Row) merged.getNextSibling();
				if (getFirstCellVerticalMerge(merged) != CellMerge.PREVIOUS) 
					merged = null;
			}
			// нужно удалить начальную строку либо убрать для нее VerticalMerge, если больше нет подчиненных строк.
			Row  next = (Row) r.getNextSibling();
			if ((next == null) || (getFirstCellVerticalMerge(next) == CellMerge.NONE)) 
				// либо строк в таблицу дальше нет, либо они -- не подчиненные.
				r.getCells().get(0).getCellFormat().setVerticalMerge(CellMerge.NONE);
			if (canBeDeletedMergedRow(r)) {
				// будем удалять строку.
				if (getFirstCellVerticalMerge(r) == CellMerge.FIRST) {
					// подчиненные строки есть. Нужно первую ячейку перенести в первую ячейку следующей строки
					Cell cellOfManyLines = r.getCells().get(0);
					cellOfManyLines.remove();
					Cell toRemove = next.getCells().get(0);
					toRemove.remove();
					next.prependChild(cellOfManyLines);
				} 
				next = (Row)r.getNextSibling();
				r.remove();
				// при удалении связь теряется...
				r = next;
			} else {
				r = (Row) r.getNextSibling();
			}
		}
	}
	
	
	/**
	 * Можем ли удалить строку в случае case 3, когда первая ячейка ВИЗУАЛЬНО занимает несколько строк?
	 * 
	 * @param r
	 * @return
	 */
	private static boolean canBeDeletedMergedRow(Row r) {
		if (r == null) return false;
		for (Cell cell : r.getCells()) {
			int cellIndex = r.getCells().indexOf(cell);
			if (cellIndex == 0) 
				continue;
			if (cellIndex == 1) {
				if (r.getCells().getCount() == 2) {
					// только одна ячейка подчиненная. Должна быть пуста
					if (!isCellEmpty(cell)) return false;
				} else {
					// вторая ячейка -- информативная. Содержательные ячейки -- следующие.
					continue;
				}
			} else {
				if (!isCellEmpty(cell)) return false;
			}
		}
		return true;
	}
	
	
	private static boolean isCellEmpty(Cell c) {
		try {
			if (!"".equals(c.toTxt().trim())) return false;	
		} catch(Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Какой признак VerticalMerge для первой ячейки строки?
	 * @param r
	 * @return
	 */
	private static int getFirstCellVerticalMerge(Row r) {
		if (r == null) return CellMerge.NONE; 
		try {
			if ((r.getCells() != null) && (r.getCells().getCount()>0)) 
				return r.getCells().get(0).getCellFormat().getVerticalMerge();
		} catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
			return CellMerge.NONE;
		}
	    return CellMerge.NONE;
	}
}


