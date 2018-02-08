package org.uit.director.report.mainreports;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.report.ComponentGenerator;
import org.uit.director.report.ComponentReport;
import org.uit.director.report.WorkflowReport;
import org.uit.director.tasks.ProcessInfo;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Отчет по запущенным процессам за период
 * Входные параметры для отчета:
 *   idTypeProcess   --  тип процесса
 *   ВСЕ ПРОЦЕССЫ выбирать нельзя. Только один из них (список атрибутов очень разный!!!)
 *   -- период с и по
 *   -- осуществлять поиск по активным процессам
 *   -- осуществлять поиск по завершенным процессам
 *   
 *   TODO: ХОРОШО бы показывать все текущие активные атрибуты, если они есть, а не все подряд.
 */


public class ProcessStartReport extends WorkflowReport {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(ProcessStartReport.class.getName());
	
	@Override
	public void init(WorkflowSessionContext wsc, List params) {

		// зададим набор параметров отчета 
        nameReport = "Запущенные процессы за период";
        super.init(wsc, params);
        componentList = new ArrayList<ComponentReport>();

        ComponentReport cR = ComponentGenerator.genTypeProcess();
        componentList.add(cR);

        cR = ComponentGenerator.genPeriod();
        componentList.add(cR);

        cR = new ComponentReport("check", "Поиск по активным процессам", Boolean.TRUE);
        componentList.add(cR);

        cR = new ComponentReport("check", "Поиск по завершенным процессам", Boolean.FALSE);
        componentList.add(cR);


    }

    @Override
	public void generateReport() {
        try {

            StringBuffer sb = new StringBuffer();
            StringBuffer line = new StringBuffer();
            // получим значения параметров из запроса и преобразуем их
            String idTypeProcess = ComponentGenerator.getSelectedItem(ComponentGenerator.getItemByName(componentList, "Тип процесса"));
            logger.info("idTypeProcess: " + idTypeProcess);
            if ((idTypeProcess == null) || (idTypeProcess.equals("")) || (idTypeProcess.equals(" "))
            	|| (idTypeProcess.equals("0")) || (idTypeProcess.equals("-1"))) {
                reportHTML = "<br><br> <center>Выберите тип процесса</center>";
                isReportGenerate = true;
                return;
            }

            
            String dateLeft = ComponentGenerator.getDateForPeriod(componentList, "Период", 0);
            String dateRight = ComponentGenerator.getDateForPeriod(componentList, "Период", 1);
            boolean isActive = ((Boolean)ComponentGenerator.getItemByName(componentList, "Поиск по активным процессам").getValue()).booleanValue();
            boolean isCompleted = ((Boolean)ComponentGenerator.getItemByName(componentList, "Поиск по завершенным процессам").getValue()).booleanValue();

            dateRight = ComponentGenerator.setRightDate(dateRight);
            SimpleDateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy");
    		SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
    		
    		// получим список id заявок, удовлетворяющих требованиям. 
    		String sql = "select p.id_process from processes p " +
    				" inner join process_events pe on (pe.id_process=p.id_process)  " +
    				" where p.ID_TYPE_PROCESS = " + idTypeProcess +
    				" and (pe.date_event between STR_TO_TIMESTAMP('" + toFormat.format((fromFormat.parse(dateLeft))) + "') " +
    						"and " + 
                    " STR_TO_TIMESTAMP('"+toFormat.format((fromFormat.parse(dateRight))) + "')) " +
    				" and pe.id_process_type_event=1 and p.id_status <> 5"; 

            // новый код
/*            String sql = "select p.ID_PROCESS from PROCESSES p  where p.ID_TYPE_PROCESS=" + idTypeProcess +
                    " and p.DATEOFCOMMING between STR_TO_TIMESTAMP('" + toFormat.format((fromFormat.parse(dateLeft))) + "') and " + 
                    "STR_TO_TIMESTAMP('"+toFormat.format((fromFormat.parse(dateRight))) + "') and p.ID_STATUS<>4 ";
*/            //
            if (isActive && !isCompleted) {
                sql += "and p.id_status = 1 ";
            }

            if (isCompleted && !isActive) {
                sql += "and p.id_status = 4 ";
            }

            // Самые свежие созданные процессы должны быть сверху. Тогда у нас -- наиболее актуальные представления процессов.
            sql += " order by  p.id_process DESC";
            	
            if (!isCompleted && !isActive) {
                reportHTML = "<br><br> <center>Укажите критерий поиска (по активным или по завершенным)</center>";
                isReportGenerate = true;
                return;
            }

            DBFlexWorkflowCommon dbFlex = getWsc().getDbManager().getDbFlexDirector();
            List res = dbFlex.execQuery(sql);
            String nameTypProcess = (String) WPC.getInstance().getData(Cnst.TBLS.typeProcesses, Long.valueOf(idTypeProcess), Cnst.TTypeProc.name);

            sb.append("<div class=\"tabledata\">\n").append("<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG> Запущенные процессы \"").
                    append(nameTypProcess).
                    append("\" в период с ").
                    append(dateLeft).
                    append(" по ").
                    append(dateRight);

            ArrayList <String> attrNames = new ArrayList <String>(); 
            // получим список атрибутов, используемых в последнем по счету процессе [наиболее свежая версия процесса]
            if (res.size() > 0)
            {
            	Map map = (Map) res.get(0);
            	String idProcess = (String) map.get("ID_PROCESS");                
                ProcessInfo info = new ProcessInfo();
                Long idUser = getWsc().getIdUser();
				info.init(getWsc(), Long.valueOf(idProcess), idUser, false);
    			info.execute();
                
    			// пробежимся по строке и вытащим значения.                                
                Iterator<BasicAttribute> it = info.getAttributes().getIterator();                
                while(it.hasNext()) attrNames.add(it.next().getAttribute().getNameVariable());                
    			            
	            // отсортируем его по имени 
                /*
                Collections.sort(attrNames, new Comparator<String>() {
                    public int compare(String object1, String object2) {
                        return object1.compareToIgnoreCase(object2);
                    }
                });
                */
                // будем использовать для отображения ТОЛЬКО эти атрибуты. [Если в более старых версиях
	            // процесса были другие атрибуты, то их значения отображаться не будут. Это нужно, чтобы не сбивались значения в таблице 
	            // не путались из-за того, что в одних экземплярах процессах есть такие атрибуты, а в других - нет.]
                                
                // Выводим строку заголовка                
                sb.append("<thead><tr><th>№</th>");                
                for (int i = 0; i < attrNames.size(); i++)
                	sb.append("<th>").append(attrNames.get(i)).append("</th>");                  
                sb.append("</tr></thead><tbody>");              
                
                // Выводим сам список процессов. List <Map <String, String>>
                for (int i = 0; i < res.size(); i++) {
                    map = (Map) res.get(i);
                    idProcess = (String) map.get("ID_PROCESS");
                    
                    info = new ProcessInfo();
                    idUser = getWsc().getIdUser();
    				info.init(getWsc(), Long.valueOf(idProcess), idUser, false);
        			info.execute();
                    
                    /*List attr = new AttributesStructList(dbFlex.getAttributes(Long.parseLong(idProcess), false)).
                            getMainAttributes(Integer.parseInt(idTypeProcess));*/
        			
                    // Обнулим строку, куда будем складывать результаты
                    line.delete(0, line.length());

                    // пробежимся по строке и вытащим значения.
                    for(int k=0; k<attrNames.size(); k++){
                        Attribute attribute = info.getAttributes().findAttributeByName(attrNames.get(k)).getAttribute();
                        line.append("<td>" + (attribute.getValueAttributeString()==null ? " " : attribute.getValueAttributeString()) + "</td>");    
                    }
                    	
                    // добавим полученную строку в результирующий список строк.  
                    sb.append("<tr>" + "<td>" + (i+1) + "</td>" + line + "</tr>");         
                }                
            }            
            sb.append("</tbody></table>").
            append("</div>");
            
            reportHTML += sb.toString();
            isReportGenerate = true;
        } catch (Exception e) {
            e.printStackTrace();
            reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
        }
    }
}
