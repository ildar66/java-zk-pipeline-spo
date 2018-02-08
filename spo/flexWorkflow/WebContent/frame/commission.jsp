<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.vtb.domain.CommissionDeal"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@ page import="ru.md.controller.PriceController" %>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
Logger LOGGER = Logger.getLogger("commission.jsp");
boolean readOnly = request.getParameter("readOnly").equals("true")  || request.getParameter("monitoringmode")!=null;
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
Task task=processor.getTask(new Task(Long.valueOf(request.getParameter("mdtaskid"))));
ArrayList<CommissionDeal> commissionList = task.getCommissionDealList();
boolean isCanEditFund = TaskHelper.isCanEditFund(request);
%>
<%if (readOnly && !isCanEditFund && commissionList.size()==0) { %>Нет комиссий.<% } %>
<%if (!readOnly || isCanEditFund) { %><input type="hidden" name="commission_section" value="YES" /><% } %>
<style>
 label {color: #888;}
<%if (readOnly) {%>
 label > div, label > input, label > textarea, label > span {color: black;}
<%} %>
</style>


<table id ="idCommissionTable" class="regular">
    <tbody>
    <%try{ %>
    <%
        for (int j=0; j<commissionList.size(); j++) {
        CommissionDeal commission = (CommissionDeal)commissionList.get(j);
        String htmlName = "compare_prodprice_commission" + commission.getId();
        %>
        <tr>
            <td>
                <table><tr><td>
                    <label id="<%=htmlName %>">Наименование комиссии<br>
                        <md:comissionType  readonly="<%=readOnly && !isCanEditFund%>" style="width:20em;" name="Наименование комиссии" value="<%=commission.getName().getId()%>" />
                    </label>
                    <label id="<%=htmlName %>_value">Величина комиссии<br>
                        <md:inputMoney  readonly="<%=readOnly && !isCanEditFund%>" name="Значение Комиссии" styleClass="money" value="<%=commission.getFormattedValue()%>" 
                            onBlur="input_autochange(this,'money')" onChange="calcCommission()" />
                    </label>
                    <label id="<%=htmlName %>_curr">Валюта&#47; &#37;<br>
                        <md:currency  withyearprocent="true" readonly="<%=readOnly && !isCanEditFund%>"  style="width:8em;" name="Валюта Комиссии"
                                      value="<%=commission.getCurrency2().getCode()%>" onChange="calcCommission()" />
                    </label>
                    <label>Величина комиссии % годовых<br />
                        <span id="calcCommission<%=j%>"><%=PriceController.getAnnualValue(commission.getFormattedValue(),
                                commission.getCurrency2().getCode(),commission.getProcent_order().getId(),task.getId_task())%></span></label>

                <%try{ %>
                    <label id="<%=htmlName %>_procentorder">Периодичность оплаты комиссии<br>
                        <md:PatternPaidPercent readonly="<%=readOnly && !isCanEditFund%>" name="Порядок уплаты процентов Комиссии" 
                            value="<%=commission.getProcent_order().getId()%>" onChange="calcCommission()"
                            />
                    </label>
                    <%String comm =  (commission.getCalcBase()==null||commission.getCalcBase().getId()==null)?"":commission.getCalcBase().getId().toString();%>
                    <label id="<%=htmlName %>_calcbase">База расчета<br>
                    <%try{ %>
                            <md:calcBase style="width:8em;"  readonly="<%=readOnly && !isCanEditFund%>" 
                            name="База расчета Комиссии"
                            value="<%=comm%>" />
                    <%}catch(Exception e){LOGGER.log(Level.SEVERE, e.getMessage(), e);} %>
                    </label>
                    <%String commSize= ((commission.getComissionSize()!=null) && (commission.getComissionSize().getId() != null)) ? commission.getComissionSize().getId().toString() : "";%>
                    <label id="<%=htmlName %>_size">Порядок расчета<br>
                    <%try{ %>
                            <md:comissionSize style="width:20em;"  readonly="<%=readOnly && !isCanEditFund%>" name="Порядок расчета Комиссии" value="<%=commSize%>" />
                    <%}catch(Exception e){LOGGER.log(Level.SEVERE, e.getMessage(), e);} %>
                    </label>
                    <label id="<%=htmlName %>_paydescr">Срок оплаты комиссии<br>
                    <%try{ %>
                        <%if(!readOnly || isCanEditFund){%>
                            <textarea name="Срок оплаты комиссии" onkeyup="fieldChanged(this)"
                                    ><%=(commission.getPayDescription()==null)?"":commission.getPayDescription()%></textarea>
                        <%} else {%>
                            <span><%=(commission.getPayDescription()==null)?"":commission.getPayDescription()%></span>
                        <%}%>
                    <%}catch(Exception e){LOGGER.log(Level.SEVERE, e.getMessage(), e);} %>
                <%}catch(Exception e){LOGGER.log(Level.SEVERE, e.getMessage(), e);} %>
                </label>
                </td></tr><tr><td id="<%=htmlName %>_descr">
                Комментарии<br>
                        <%if(!readOnly || isCanEditFund){%>
                        <textarea name="Описание Комиссии" onkeyup="fieldChanged(this)"
                                ><%=(commission.getDescription()==null)?"":commission.getDescription()%></textarea>
                        <%} else {%>
                        <span style="color:#888"><%=(commission.getDescription()==null)?"":commission.getDescription()%></span>
                        <%}%>
                </td></tr></table>
            </td>
            <%
            if (!readOnly || isCanEditFund) {
            %>
            <td class="delchk" style="width:auto;">
                <input type="checkbox" name="idCommissionTableChk"/>
            </td>
            <%
            }
            %>
        </tr>
        <%
        }
        %>
        <%}catch (Exception e) {out.println("Ошибка в секции  frame_priceCondition.jsp:" + e.getMessage());e.printStackTrace();} %>
    </tbody>
    <tfoot>
		    <tr>
		        <td class="compare-list-removed" id="compare_list_prodprice_commission" colspan=2></td>
		    </tr>
    <%
    if (!readOnly || isCanEditFund) {
    %>
        <tr>
            <td colspan=2 class="add">
                <button onmouseover="Tip(getToolTip('Добавить комиссию'))" onmouseout="UnTip()" onclick="insertCommissionTableTR();fieldChanged();return false;" class="add"></button>
                <button onmouseover="Tip(getToolTip('Удалить комиссию'))" onmouseout="UnTip()" onclick="DelRowWithLast('idCommissionTable', 'idCommissionTableChk'); return false;" class="del"></button>
            </td>
        </tr>
    <%
    }
    %>
    </tfoot>
</table>
<script type="text/javascript">
    $(document).ready(function() {
        calcCommission();
    });
	if ($('#lastApprovedVersion').val() != "" && prevApprovedDiffShown)
		displayPrevApprovedDiff();
</script>
<script id="newCommissionTableTemplate" type="text/x-jquery-tmpl">
<tr>
            <td>
                <table><tr><td>
                    <label>Наименование комиссии<br>
                        <md:comissionType  readonly="false" style="width:20em;" name="Наименование комиссии" value="" />
                    </label>
                    <label>Величина комиссии<br>
                        <md:inputMoney  readonly="false" name="Значение Комиссии" styleClass="money" value=""
                                        onBlur="input_autochange(this,'money')" onChange="calcCommission()" />
                    </label>
                    <label>Валюта&#47; &#37;<br>
                        <md:currency  withyearprocent="true" readonly="false"  style="width:8em;" name="Валюта Комиссии"
                                      value="" onChange="calcCommission()" />
                    </label>
                    <label>Величина комиссии % годовых<br />
                        <span id="calcCommission${nextid}"></span></label>

                    <label>Периодичность оплаты комиссии<br>
                        <md:PatternPaidPercent readonly="false" name="Порядок уплаты процентов Комиссии"
                                               value="" onChange="calcCommission()" />
                    </label>
                    <label>База расчета<br>
                            <md:calcBase style="width:8em;"  readonly="false"
                                         name="База расчета Комиссии"
                                         value="" />
                    </label>
                    <label>Порядок расчета<br>
                            <md:comissionSize style="width:20em;"  readonly="<%=readOnly && !isCanEditFund%>" name="Порядок расчета Комиссии" value="" />
                    </label>
                    <label>Срок оплаты комиссии<br>
                            <textarea name="Срок оплаты комиссии"></textarea>
                </label>
                </td></tr><tr><td>
                Комментарии<br>
                        <textarea name="Описание Комиссии"></textarea>
                </td></tr></table>
            </td>
            <td class="delchk" style="width:auto;">
                <input type="checkbox" name="idCommissionTableChk"/>
            </td>
        </tr>
</script>