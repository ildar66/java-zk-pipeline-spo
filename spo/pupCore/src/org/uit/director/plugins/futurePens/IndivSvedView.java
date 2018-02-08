package org.uit.director.plugins.futurePens;

//import org.PFR.EJB.dbkonvert.DBKonvert;
//import org.PFR.EJB.dbkonvert.DBKonvertHome;
//import org.PFR.EJB.dbkonvert.KonvertObject;
import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;

/**
 * Created by IntelliJ IDEA.
 * User: PD190379
 * Date: 07.06.2006
 * Time: 9:43:15
 * To change this template use File | Settings | File Templates.
 */
public class IndivSvedView implements PluginInterface {
    private List params;
    private WorkflowSessionContext wsc;

    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;
        this.params = params;
    }

    public String execute() {
        String res = "start";
        /*DBMgr dbMgr = null;
        try {
            StringBuffer pageCntx = new StringBuffer();


            String idTask = String.valueOf(wsc.getIdCurrTask());
            wsc.getTaskList().addTaskInfo(idTask);
            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());

            String insNmbr = taskInfo.getAttributes().getStringValueByName("Страховой номер");
            String fam = taskInfo.getAttributes().getStringValueByName("Фамилия");
            String nam = taskInfo.getAttributes().getStringValueByName("Имя");
            String ptr = taskInfo.getAttributes().getStringValueByName("Отчество");
            String brd = taskInfo.getAttributes().getStringValueByName("Дата рождения");
            InsuranceNumber number = new InsuranceNumber(insNmbr);

            String numbStr = "";
            StringTokenizer tokenizer = new StringTokenizer(insNmbr, "- .,_';:!@#$%^&*()+{}[]");
            while (tokenizer.hasMoreTokens()) {
                numbStr += tokenizer.nextToken();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

            dbMgr = wsc.getDbManager();

            List indivSved = dbMgr.getDbAnketa().getIndivSved(number.getMainNumber());

            Object konvertBean = EJBUtils.getRemouteEJBObject(Config.getProperty("JNDI_DB_KONVERT_HOST"),
                    Config.getProperty("JNDI_DB_KONVERT_EJB"));
            DBKonvertHome konvertHome = (DBKonvertHome)
                    PortableRemoteObject.narrow(konvertBean, DBKonvertHome.class);
            DBKonvert dbKonvert = konvertHome.create();

//            List stag = dbMgr.getDbKonvert().getStag( numbStr );
            List stagData = dbKonvert.getStag(numbStr, 1);

            Iterator iterIndivSved = indivSved.iterator();
            pageCntx.append("<left>");
            pageCntx.append("<strong style=\"font-size:12\">");
            pageCntx.append("<div class=\"tabledata\"> <table border=\"0\">");
            pageCntx.append("<tr><td>Страховой номер</td><td>" + insNmbr + "</td></tr>");
            pageCntx.append("<tr><td>Фамилия</td><td>" + fam + "</td></tr>");
            pageCntx.append("<tr><td>Имя</td><td>" + nam + "</td></tr>");
            pageCntx.append("<tr><td>Отчество</td><td>" + ptr + "</td></tr>");
            pageCntx.append("<tr><td>Дата рождения</td><td>" + brd + "</td></tr>");
            pageCntx.append("</table></div>");


            pageCntx.append("<div class=\"tabledata\"> <table border=\"1\">");
            pageCntx.append("<tr>\n" +
                    "    <th align=\"center\" style=\"font-size:10\">Рег. номер</th>\n" +
                    "    <th align=\"center\" style=\"font-size:10\">Наименование</th>\n" +
                    "    <th align=\"center\" style=\"font-size:10\">Начало периода</th>\n" +
                    "    <th align=\"center\" style=\"font-size:10\">Конец периода</th>\n" +
                    "\n" +
                    "</tr>");
            pageCntx.append("");
            EnterpriseNumber entNmbr = new EnterpriseNumber(0);
            while (iterIndivSved.hasNext()) {
                HashMap entrprseHashMap = (HashMap) iterIndivSved.next();
                entNmbr.set(Long.parseLong((String) entrprseHashMap.get("ENTNMB")));
                String entnmb = entNmbr.toString();
                String entnam = (String) entrprseHashMap.get("ENTNAM");
                String beg = (String) ((entrprseHashMap.get("PFWBEG") == null) ? "" : entrprseHashMap.get("PFWBEG"));
                String end = (String) ((entrprseHashMap.get("PFWEND") == null) ? "" : entrprseHashMap.get("PFWEND"));
                pageCntx.append("<tr>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(entnmb).
                        append(" </td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(entnam).
                        append(" </td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(beg).
                        append("</td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(end).
                        append("</td>\n" + "</tr>");
            }
            pageCntx.append("</table></div>");

            Iterator iterStag = stagData.iterator();
            pageCntx.append("<div class=\"tabledata\"> <table border=\"1\">");
            pageCntx.append("<tr>\n" +
                    "    <th align=\"center\" style=\"font-size:10\">Место работы</th>" +
                    "    <th align=\"center\" style=\"font-size:10\">Должность</th>" +
                    "    <th align=\"center\" style=\"font-size:10\">С</th>" +
                    "    <th align=\"center\" style=\"font-size:10\">По</th>" +
                    "    <th align=\"center\" style=\"font-size:10\">Выслуга</th>" +
                    "    <th align=\"center\" style=\"font-size:10\">Особые условия</th>" +
                    "</tr>");
            pageCntx.append("");
            float otnzar = 0;
            String chek = "";
            while (iterStag.hasNext()) {
                KonvertObject entrprseHashMap = (KonvertObject) iterStag.next();
//                String entnmb = entrprseHashMap.get( "O_NCOMP" );
                String entnmb = entrprseHashMap.getNCOMP();
//                String entnam = ( String ) entrprseHashMap.get( "O_PROF" );
                String entnam = (String) entrprseHashMap.getPROF();
//                String beg = formatter.format( ( java.util.Date ) entrprseHashMap.get( "O_DNACH" ) );
                String beg = entrprseHashMap.getDNACH();
//                String end = formatter.format( ( java.util.Date ) entrprseHashMap.get( "O_DKON" ) );
                String end = entrprseHashMap.getDKON();
//                String vis = ( ( ( String ) entrprseHashMap.get( "O_VIS" ) ) == null ) ? "" : ( ( String ) entrprseHashMap.get( "O_VIS" ) );
                String vis = ((entrprseHashMap.getVIS() == null) ? "" : (entrprseHashMap.getVIS()));
//                String spec = ( ( ( String ) entrprseHashMap.get( "O_SPEC" ) ) == null ) ? "" : ( ( String ) entrprseHashMap.get( "O_SPEC" ) );
                String spec = ((entrprseHashMap.getSPEC()) == null) ? "" : (entrprseHashMap.getSPEC());
//                otnzar = ( String ) entrprseHashMap.get( "O_OTNZAR" );
                otnzar = entrprseHashMap.getOTNZAR();
//                chek = ( String ) entrprseHashMap.get( "O_CHEK" );
                pageCntx.append("<tr>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(entnmb).
                        append(" </td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(entnam).
                        append(" </td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(beg).
                        append("</td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(end).
                        append("</td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(vis).
                        append("</td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(spec).
                        append("</td>\n" + "</tr>");
            }
            pageCntx.append("</table></div>");

            pageCntx.append("<div class=\"tabledata\"> <table border=\"0\">");
            pageCntx.append("<tr><td>Соотношение заработков</td><td>" + otnzar + "</td></tr>");
            pageCntx.append("<tr><td>Состояние</td><td>" + chek + "</td></tr>");
            pageCntx.append("</table></div>");

//            Calendar cal = Calendar.getInstance();
//            String date = new SimpleDateFormat( "dd.MM.yyyy" ).format( cal.getTime() );
//
//            int isDayPlus = Calendar.MONTH;
//            if ( is10Day ) isDayPlus = Calendar.DAY_OF_MONTH;
//            cal.add( isDayPlus, countDay );
//            String dateEnd = new SimpleDateFormat( "dd.MM.yyyy" ).format( cal.getTime() );


            wsc.getCacheManager().deleteCacheElement(idTask);
            wsc.getTaskList().addTaskInfo(idTask);
            wsc.setPageData(pageCntx.toString());

            res = "textPage";


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                dbMgr.closeDBAnketa();
            } catch (Exception e) {
            }

        }*/


        return res;
    }
}
