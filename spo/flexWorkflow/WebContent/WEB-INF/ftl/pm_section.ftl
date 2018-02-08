<div content="pm_section">
    <table class="prodManagerMainTable">
        <tr>
            <td class="prodManagerMainTd">
                <table class="prodManagerSubTable prodManagerSubTableSizeOne">
                    <tr class="lightGreenColor" style="display: none">
                        <th>Приоритет</th>
                        <td>
                            <#assign mdTask_priority = model["mdTask"].priority!"">
                            <#if model.readonly>
                                ${mdTask_priority!""}
                            <#else>
                                <#if model["priorities"]?? && model["priorities"]?first??>
                                    <#assign mdTask_priorities = model["priorities"]!''>
                                </#if>
    
                                <#if mdTask_priorities??>
                                    <select name="mdTask_priority">
                                        <option value="" />
                                        <#list mdTask_priorities as priority>
                                            <#if priority??>
                                                <option <#if mdTask_priority = priority.value>
                                                            selected="selected"
                                                        </#if> 
                                                        value="${priority.value!''}">
                                                    ${priority.value!''}
                                                </option>
                                            </#if> 
                                        </#list>
                                    </select>
                                <#else>
                                    ${mdTask_priority!''}
                                </#if>
                            </#if>
                        </td>
                    </tr>
                    <tr class="lightGreenColor">
                        <th>Дата начала работы над сделкой</th>
                        <td>
                            <#if model["mdTask"].creationDate??>
                                ${model["mdTask"].creationDate?string["dd.MM.yyyy"]}
                            </#if>
                        </td>
                    </tr>
                    <tr class="lightGreenColor">
                        <th>Плановая дата подписания КОД</th>
                        <td>
                            <#if model["mdTask"].proposedDtSigning??>
                                <#assign mdTask_proposedDtSigning = model["mdTask"].proposedDtSigning?string["dd.MM.yyyy"]>
                            </#if>
                            
                            <#if model.readonly>
                                ${mdTask_proposedDtSigning!''}
                            <#else>
                                <input id="mdTask_proposedDtSigning" 
                                       name="mdTask_proposedDtSigning"
                                       class="date"
                                       value="${mdTask_proposedDtSigning!''}"
                                       valueType="date" />
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="lightGreenColor">Плановая Дата Выборки</th>
                        <td>
                            <#if model["mdTask"].pipeline.planDate??>
                                <#assign mdTask_pipeline_planDate = model["mdTask"].pipeline.planDate?string["dd.MM.yyyy"]>
                            </#if>
                            
                            <#if model.readonly>
                                ${mdTask_pipeline_planDate!''}
                            <#else>
                                <input id="mdTask_pipeline_planDate" 
                                       name="mdTask_pipeline_planDate"
                                       class="date"
                                       value="${mdTask_pipeline_planDate!''}"
                                       valueType="date" />
                            </#if>
                        </td>
                    </tr>
                    <tr class="lightGreenColor">
                        <th>Срок использования, дней</th>
                        <td>
                        <#if model.readonly>
                            <#if model["mdTask"].usePeriod??>
                            ${model["mdTask"].usePeriod!''}
                            </#if>
                        <#else>
                            <input id="mdTask_drawdownDateInMonth"
                                   name="mdTask_usePeriod"
                                   class="digitsSpaces"
                                   value="${model["mdTask"].usePeriod!''}"
                                   valueType="digitsSpaces" />
                        </#if>
                        </td>
                    </tr>
                    <tr class="lightGreenColor">
                        <th>Кол-во недель в пайплайне</th>
                        <td>${model["mdTask"].pipeline.weeksNumber!""}</td>
                    </tr>
                    <tr style="display: none">
                        <th class="lightGreenColor">Приоритет менеджера</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_managerPriority"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.managerPriority>checked="checked"</#if> />
                        </td>
                    </tr>
                    <tr class="lightGreenColor">
                        <th>Дата последнего обновления</th>
                        <td>
                            <#if model["lastUpdateDate"]??>
                                ${model["lastUpdateDate"]?string["dd.MM.yyyy"]}
                            </#if>
                        </td>
                    </tr>
                </table>    
                <table class="prodManagerSubTable prodManagerSubTableSizeOne">   
                    <tr class="purpleColor">
                        <th>Отрасль</th>
                        <td>
                            <#if model["mdTask"].mainOrganization??>
                                ${model["mdTask"].mainOrganization.branch!''}
                            </#if>
                        </td>
                    </tr>
                    <tr class="purpleColor">
                        <th>Группа Компаний</th>
                        <td>
                            <#if model["mdTask"].mainOrganization??>
                                ${model["mdTask"].mainOrganization.groupname!''}
                            </#if>
                        </td>
                    </tr>
                    <tr class="purpleColor">
                        <th>Компания</th>
                        <td>
                            <#if model["ek_name"]??>
                                ${model["ek_name"]!''}
                            </#if>
                                ${model["mdTask"].projectName!''}
                        </td>
                    </tr>
                    <tr class="purpleColor">
                        <th>Поручители, гаранты и др. Участники сделки</th>
                        <td>
                            <#if model["mdTask"].otherOrganizations?? && model["mdTask"].otherOrganizations?first??>
                                <#list model["mdTask"].otherOrganizations as oo>
                                    ${oo.name!''}<#if oo_has_next>,<br /></#if>
                                </#list>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                </table>    
                <table class="prodManagerSubTable prodManagerSubTableSizeOne">    
                        <th class="yellowColor">Стадии сделки</th>
                        <td>
                            <#assign mdTask_pipeline_status = model["mdTask"].pipeline.status!''>
                                <input type="hidden" id="mdTask_pipeline_status" name="mdTask_pipeline_status" value="${mdTask_pipeline_status!''}">
                                <div id="mdTask_pipeline_status_div">${mdTask_pipeline_status!''}</div>
                                <#if !model.readonly>
                                <a href="javascript:;" class="dialogActivator" dialogId="mdTask_pipeline_statuses" id="mdTask_pipeline_statuses_change">
                                    <img src="style/dots.png" alt="выбрать из шаблона">
                                </a>
                                </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="yellowColor">Вероятность Закрытия</th>
                        <td>
                            <#assign mdTask_pipeline_closeProbability = model["mdTask"].pipeline.closeProbability!''>
                            <#if mdTask_pipeline_closeProbability?? && mdTask_pipeline_closeProbability?has_content>
                                <#assign mdTask_pipeline_closeProbability = (mdTask_pipeline_closeProbability?round)?string>
                            </#if>
                            
                            <#if model.readonly>
                                ${mdTask_pipeline_closeProbability!''}%
                            <#else>
                                <input id="mdTask_pipeline_closeProbability" name="mdTask_pipeline_closeProbability" 
                                       class="money3digits" readonly="readonly"
                                       value="${mdTask_pipeline_closeProbability!''}"
                                       valueType="money3digits" />%
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="yellowColor">Ручное управление стадиями</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_status_manual" id="mdTask_pipeline_status_manual"
                                   onclick="onPipelineStatusManualChange()"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.statusManual>checked="checked"</#if> />
                        </td>
                    </tr>
                </table>
            </td>
            <td class="prodManagerMainTd">
                <table class="prodManagerSubTable prodManagerSubTableSizeTwo">
                    <tr class="greyColor">
                        <th>Вид сделки</th>
                        <td>
                            <#if model["mdTask"].limit || model["mdTask"].sublimit>
                                <#if model["mdTask"].productGroups?? && model["mdTask"].productGroups?first??>
                                    <ul>
                                        <#list model["mdTask"].productGroups as pg>
                                            <li>${pg!''}</li>
                                        </#list>
                                    </ul>
                                </#if>
                            </#if>
                            <#if model["mdTask"].product>
                                <#assign mdTask_productType = model["mdTask"].productType!''>
                                <#if mdTask_productType?? && mdTask_productType?has_content>
                                    <#assign mdTask_productTypeId = mdTask_productType.productid!''>
                                    <#assign mdTask_productTypeName = mdTask_productType.name!''>
                                </#if>
                                <#if model.readonly>
                                    ${mdTask_productTypeName!''}
                                <#else>
                                    <textarea id="mdTask_productTypeName" name="mdTask_productTypeName"
                                              readonly="readonly">${mdTask_productTypeName!''}</textarea>
                                    <input id="mdTask_productTypeId" name="mdTask_productTypeId" 
                                           value="${mdTask_productTypeId!''}" type="hidden" />
                                    <br />
                                    <a href="javascript:;" class="dialogActivator" dialogId="mdTask_productTypes" 
                                        targetId="mdTask_productTypeName" hiddenTargetId="mdTask_productTypeId">
                                        <img src="style/dots.png" alt="выбрать из шаблона">
                                    </a>
                                </#if> 
                            </#if>
                            <#if model["mdTask"].crossSell>
                                <#if model.readonly>
                                    ${model["mdTask"].crossSellName!''}
                                <#else>
                                    <#assign mdTask_pipeline_supply_cross_sell_type = model["mdTask"].crossSellId!0>
                                    <select name="mdTask_pipeline_supply_cross_sell_type">
                                        <option value="" />
                                        <#list model["crossSellTypes"] as cs>
                                            <#if cs??>
                                                <option value="${cs.id!''}"
                                                    <#if mdTask_pipeline_supply_cross_sell_type == cs.id>
                                                        selected="selected"
                                                </#if> >
                                                ${cs.name!''}
                                                </option>
                                            </#if>
                                        </#list>
                                    </select>
                                </#if>
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="greyColor">Применимое Право</th>
                        <td>
                            <#assign mdTask_pipeline_law = model["mdTask"].pipeline.law!''>
                            <#if model.readonly>
                                ${mdTask_pipeline_law!''}
                            <#else>
                                <input id="mdTask_pipeline_law" name="mdTask_pipeline_law" 
                                           value="${mdTask_pipeline_law!''}" type="text" />
                                <br />
                                <a href="javascript:;" class="dialogActivator" dialogId="mdTask_pipeline_laws" 
                                    targetId="mdTask_pipeline_law">
                                    <img src="style/dots.png" alt="выбрать из шаблона">
                                </a>
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="greyColor">География</th>
                        <td>
                            <#assign mdTask_pipeline_geography = model["mdTask"].pipeline.geography!''>
                            <#if model.readonly>
                                ${mdTask_pipeline_geography!''}
                            <#else>
                                <input id="mdTask_pipeline_geography" name="mdTask_pipeline_geography" value="${mdTask_pipeline_geography!''}" />
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greyColor">Обеспечение</th>
                        <td>
                            <#assign mdTask_pipeline_supply = model["mdTask"].pipeline.supply!''>
                            <#if model.readonly>
                                ${mdTask_pipeline_supply!''}
                            <#else>
                                <#if model["supplies"]?? && model["supplies"]?first??>
                                    <#assign mdTask_pipeline_supplies = model["supplies"]!''>
                                </#if>
    
                                <#if mdTask_pipeline_supplies??>
                                    <select name="mdTask_pipeline_supply">
                                        <option value="" />
                                        <#list mdTask_pipeline_supplies as supply>
                                            <#if supply??>
                                                <option <#if mdTask_pipeline_supply = supply.value>
                                                            selected="selected"
                                                        </#if> 
                                                        value="${supply.value!''}">
                                                    ${supply.value!''}
                                                </option>
                                            </#if> 
                                        </#list>
                                    </select>
                                <#else>
                                    ${mdTask_pipeline_supply!''}
                                </#if>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greyColor">Предмет залога</th>
                        <td>
                            <#if model["mdTask"].pipeline.ensurings?? && model["mdTask"].pipeline.ensurings?first??>
                                <#list model["mdTask"].pipeline.ensurings as ens>
                                    ${ens.name!''}<#if ens_has_next>,<br /></#if>
                                </#list>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greyColor">Цель Финансирования</th>
                        <td>
                            <#if model["mdTask"].pipeline.financingObjectives?? && model["mdTask"].pipeline.financingObjectives?first??>
                                <#assign mdTask_pipeline_financingObjectives = model["mdTask"].pipeline.financingObjectives>
                            </#if>
                        
                            <#if model.readonly>
                                <#if mdTask_pipeline_financingObjectives??>
                                    <ul>
                                        <#list mdTask_pipeline_financingObjectives as fo>
                                            <li>${fo!''}</li>
                                        </#list>
                                    </ul>
                                </#if>
                            <#else>
                                <script id="mdTask_pipeline_financingObjectives_template" type="text/x-jquery-tmpl">
                                    <div>
                                        <textarea />
                                        <br />
                                        <a href="javascript:;" class="disable-decoration mdTask_pipeline_financingObjectives_select">
                                            <img src="style/dots.png" alt="выбрать из шаблона">
                                        </a>
                                        <a name="mdTask_pipeline_financingObjectives_removeLink" class="disable-decoration" 
                                           href="javascript:;" alt="удалить">
                                            <img src="theme/img/minus.png" />
                                        </a>
                                    </div>
                                </script>
                                
                                <a id="mdTask_pipeline_financingObjectives_addLink" href="javascript:;" alt="добавить"><img src="theme/img/plus.png" /></a>
                                <#if !mdTask_pipeline_financingObjectives??>
	                                <div id="hideFinancingObjectivesEmptyFieldDiv">
	                                	<textarea onfocus="hideFinOdjectiveEmptyField()"/>
	                                </div>
	                            </#if>    
                                <br />
                                
                                <#if mdTask_pipeline_financingObjectives??>
                                    <#list mdTask_pipeline_financingObjectives as fo>
                                        <div>
                                            <textarea id="mdTask_pipeline_financingObjectives${fo_index}" 
                                                      name="mdTask_pipeline_financingObjectives">${fo!''}</textarea>
                                            <br />
                                            <a href="javascript:;" class="disable-decoration" generatedId="true"
                                               onclick="$('#selectedID').val('mdTask_pipeline_financingObjectives${fo_index}');$('#mdTask_pipeline_financingObjectives').dialog({draggable:false, modal:true, width:800});">
                                                <img src="style/dots.png" alt="выбрать из шаблона">
                                            </a>
                                            <a name="mdTask_pipeline_financingObjectives_removeLink" class="disable-decoration"
                                               href="javascript:;" alt="удалить">
                                                <img src="theme/img/minus.png" alt="удалить" />
                                            </a>
                                        </div> 
                                    </#list>                         
                                </#if>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greyColor">Описание Сделки</th>
                        <td>
                            <#assign mdTask_pipeline_description = model["mdTask"].pipeline.description!''>
                            <#if model.readonly>
                                ${mdTask_pipeline_description!''}
                            <#else>
                                <textarea name="mdTask_pipeline_description">${mdTask_pipeline_description!''}</textarea>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greyColor">Комментарии по Статусу Сделки, Следующие Шаги</th>
                        <td>
                            <#assign mdTask_pipeline_note = model["mdTask"].pipeline.note!''>
                            <#if model.readonly>
                                ${mdTask_pipeline_note!''}
                            <#else>
                                <textarea name="mdTask_pipeline_note">${mdTask_pipeline_note!''}</textarea>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greyColor">Дополнительный Бизнес, Сроки, Примерный Объём в млн. дол. США</th>
                        <td>
                            <#assign mdTask_pipeline_additionalBusiness = model["mdTask"].pipeline.additionalBusiness!''>
                            <#if model.readonly>
                                ${mdTask_pipeline_additionalBusiness!''}
                            <#else>
                                <textarea name="mdTask_pipeline_additionalBusiness">${mdTask_pipeline_additionalBusiness!''}</textarea>
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="greyColor">Возможность Синдикации</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_syndication"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.syndication>checked="checked"</#if> />
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="greyColor">Комментарии по Синдикации</th>
                        <td>
                            <#assign mdTask_pipeline_syndicationNote = model["mdTask"].pipeline.syndicationNote!''>
                            <#if model.readonly>
                                ${mdTask_pipeline_syndicationNote!''}
                            <#else>
                                <textarea name="mdTask_pipeline_syndicationNote">${mdTask_pipeline_syndicationNote!''}</textarea>
                            </#if>
                        </td>
                    </tr>
                </table>    
                <table class="prodManagerSubTable prodManagerSubTableSizeTwo">    
                    <tr class="pinkColor">
                        <th>Срок сделки, мес.</th>
                        <td>
                            ${model["mdTask"].maturityInMonth!''}
                        </td>
                    </tr>
                    <tr>
                        <th class="pinkColor">Средневзвешенный Срок Погашения (WAL)</th>
                        <td>
                            <#assign mdTask_pipeline_wal = model["mdTask"].pipeline.wal!''>
                            <#if model.readonly>
                                <#if mdTask_pipeline_wal?? && mdTask_pipeline_wal?has_content>
                                    ${mdTask_pipeline_wal!''} мес.
                                </#if>
                            <#else>
                                <input id="mdTask_pipeline_wal" 
                                       name="mdTask_pipeline_wal"
                                       class="digitsSpaces"
                                       value="${mdTask_pipeline_wal!''}"
                                       valueType="money2digits" /> мес.
                            </#if>
                        </td>
                    </tr>
                </table>    
                <table class="prodManagerSubTable prodManagerSubTableSizeTwo">    
                    <tr class="blueColor">
                        <th>Валюта Сделки</th>
                        <td>
                            <#assign mdTask_currency = model["mdTask"].currency!''>
                            <#if model.readonly>
                                ${mdTask_currency!''}
                            <#else>
                                <#if model["currencies"]?? && model["currencies"]?first??>
                                    <#assign mdTask_currencies = model["currencies"]!''>
                                </#if>
    
                                <#if mdTask_currencies??>
                                    <select name="mdTask_currency">
                                        <#list mdTask_currencies as currency>
                                            <#if currency??>
                                                <option <#if mdTask_currency = currency>
                                                            selected="selected"
                                                        </#if> 
                                                        value="${currency!''}">
                                                    ${currency!''}
                                                </option>
                                            </#if> 
                                        </#list>
                                    </select>
                                <#else>
                                    ${mdTask_currency!''}
                                </#if>
                            </#if>
                        </td>
                    </tr>
                    <tr class="blueColor">
                        <th>Сумма Сделки в Валюте Сделки</th>
                        <td>
                            <#assign mdTask_mdTaskSumCalculated = model["mdTask"].mdTaskSumCalculated!''>
                            <#if mdTask_mdTaskSumCalculated?? && mdTask_mdTaskSumCalculated?has_content>
                                <#assign mdTask_mdTaskSumCalculated = ((model["mdTask"].mdTaskSumCalculated * 100)?round / 100)?string[",##0.00"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_mdTaskSumCalculated!''}
                            <#else>
                                <input id="mdTask_mdtaskSum" 
                                       name="mdTask_mdtaskSum"
                                       class="money"
                                       value="${mdTask_mdTaskSumCalculated!''}"
                                       valueType="money" />
                            </#if>
                        </td>
                    </tr>
                    <tr class="blueColor">
                        <th>Сумма Сделки в дол.США</th>
                        <td>
                            <#if model["mdTask"].mdTaskSumInUsd??>
                                ${((model["mdTask"].mdTaskSumInUsd * 100)?round / 100)?string[",##0.00"]}
                            </#if>
                        </td>
                    </tr>
                </table>
            </td>
            <td class="prodManagerMainTd">
                <table class="prodManagerSubTable prodManagerSubTableSizeThree">
                    <tr class="cyanColor">
                        <th>Фиксированная / Плавающая ставка</th>
                        <td>
                            <#assign mdTask_fixedRate = model["fixedRateDisplay"]!''>
                            <#assign interestRateFixed = model["interestRateFixed"]!''>
                            <#assign interestRateDerivative = model["interestRateDerivative"]!''>
                            <#if model.readonly>
                                <#if mdTask_fixedRate?has_content>
                                    ${mdTask_fixedRate}
                                </#if>
                            <#else>
                                <label>
                                    <input type="checkbox" id="mdTask_fixedRate_true" name="mdTask_fixedRate" value="true"
                                           <#if interestRateFixed>checked="checked"</#if> />
                                    Фиксированая
                                </label>
                                <br />
                                <label>
                                    <input type="checkbox" id="mdTask_fixedRate_false" name="mdTask_floatRate" value="true"
                                           <#if interestRateDerivative>checked="checked"</#if> />
                                    Плавающая
                                </label>
                            </#if>
                        </td>
                    </tr>
                    <tr class="cyanColor">
                        <th title="${model["mdtaskBaseRatesMessage"]!''}">Базовая Ставка (если плавающая)</th>
                        <td>
                            <div id="mdTask_baseRate_section">
                                <#assign mdTask_baseRate = model["mdtaskBaseRatesDisplay"]!''>
                                <#if model.readonly || true>
                                    <#if mdTask_baseRate?has_content>
                                        ${mdTask_baseRate}
                                    </#if>
                                <#else>
                                    <#if model["baseRates"]?? && model["baseRates"]?first??>
                                        <#assign mdTask_baseRates = model["baseRates"]!''>
                                    </#if>
        
                                    <#if mdTask_baseRates??>
                                        <select id="mdTask_baseRate" name="mdTask_baseRate">
                                            <#list mdTask_baseRates as br>
                                                <#if br?? && br.id?? && br.name??>
                                                    <option <#if mdTask_baseRate?? && mdTask_baseRate?has_content && mdTask_baseRate.id = br.id>
                                                                selected="selected"
                                                            </#if> 
                                                            value="${br.id!''}">
                                                        ${br.name!''}
                                                    </option>
                                                </#if> 
                                            </#list>
                                        </select>
                                    <#else>
                                        <#if mdTask_baseRate?has_content && mdTask_fixedRate?has_content>
                                            ${mdTask_fixedRate?string("", mdTask_baseRate.name!'')}
                                        </#if>
                                    </#if>
                                </#if>
                            </div>
                        </td>
                    </tr>
                    <tr class="cyanColor">
                        <th>Спред или Фиксированная Ставка, %</th>
                        <td>
                            <#assign mdTask_interestRates = model["mdTask"].interestRates!''>
                            <#if mdTask_interestRates?? && mdTask_interestRates?first?? && mdTask_interestRates?first?has_content>
                                <#assign mdTask_interestRates_id = mdTask_interestRates?first.id!''>
                                <#if mdTask_interestRates?first.loanRate??>
                                    <#assign mdTask_interestRates_loanRate = ((mdTask_interestRates?first.loanRate * 100)?round / 100)?string["0.00"]>
                                </#if>
                                <#if mdTask_interestRates?first.fundingRate??>
                                    <#assign mdTask_interestRates_fundingRate = ((mdTask_interestRates?first.fundingRate * 100)?round / 100)?string["0.00"]>
                                </#if>
                            </#if>
                            
                            <#if model.readonly>
                                ${mdTask_interestRates_loanRate!''}
                            <#else>
                                <input id="mdTask_interestRates_loanRate" 
                                       name="mdTask_interestRates_loanRate"
                                       class="money2digits"
                                       value="${mdTask_interestRates_loanRate!''}"
                                       valueType="money2digits" />
                                       
                                <input type="hidden"
                                       name="mdTask_interestRates_id"
                                       value="${mdTask_interestRates_id!''}" />
                            </#if>
                        </td>
                    </tr>
                    <tr class="cyanColor">
                        <th>Ставка Фондирования, %</th>
                        <td>
                            <#if model.readonly>
                                ${mdTask_interestRates_fundingRate!''}
                            <#else>
                                <input id="mdTask_interestRates_fundingRate" 
                                       name="mdTask_interestRates_fundingRate"
                                       class="money2digits"
                                       value="${mdTask_interestRates_fundingRate!''}"
                                       valueType="money2digits" />
                            </#if>
                        </td>
                    </tr>
                </table>    
                <table class="prodManagerSubTable prodManagerSubTableSizeThree">    
                    <tr class="lightPurpleColor">
                        <th>Комиссия за выдачу, %</th>
                        <td>
                            <#if model["managementFee"]?? && model["managementFee"]?has_content>
                                <#assign mdTask_managementFee = ((model["managementFee"] * 100)?round / 100)?string["0.00"]>
                            <#else>
                                Расчет не может быть произведен. Вероятно срок сделки не определен
                            </#if>
                            ${mdTask_managementFee!''}
                        </td>
                    </tr>
                    <tr class="lightPurpleColor" style="display: none">
                        <th>Компенсирующий спред за фиксацию ставки</th>
                        <td>
                            <#if model["mdTask"].fixingRateSpread?? && model["mdTask"].fixingRateSpread?has_content>
                                <#assign mdTask_fixingRateSpread = ((model["mdTask"].fixingRateSpread * 100)?round / 100)?string["0.00"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_fixingRateSpread!''}
                            <#else>
                                <input id="mdTask_fixingRateSpread" 
                                       name="mdTask_fixingRateSpread"
                                       class="money2digits"
                                       value="${mdTask_fixingRateSpread!''}"
                                       valueType="money2digits" />
                                       
                                <#if model["mdTask"].product> 
                                    <a href="javascript:;" class="dialogActivator disable-decoration"
                                       dialogId="mdTask_fixingRateSpreads"
                                       targetId="mdTask_fixingRateSpread">
                                        <img src="style/dots.png" alt="выбрать из шаблона">
                                    </a>
                                </#if>
                            </#if>
                        </td>
                    </tr>
                    <tr class="lightPurpleColor" style="display: none">
                        <th>Компенсирующий спред за досрочное погашение</th>
                        <td>
                            <#if model["mdTask"].earlyRepaymentSpread?? && model["mdTask"].earlyRepaymentSpread?has_content>
                                <#assign mdTask_earlyRepaymentSpread = ((model["mdTask"].earlyRepaymentSpread * 100)?round / 100)?string["0.00"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_earlyRepaymentSpread!''}
                            <#else>
                                <input id="mdTask_earlyRepaymentSpread" 
                                       name="mdTask_earlyRepaymentSpread"
                                       class="money2digits"
                                       value="${mdTask_earlyRepaymentSpread!''}"
                                       valueType="money2digits" />
                            </#if>
                            
                            <#if model["mdTask"].product> 
                                <a href="javascript:;" class="dialogActivator disable-decoration"
                                   dialogId="mdTask_earlyRepaymentSpreads"
                                   targetId="mdTask_earlyRepaymentSpread">
                                    <img src="style/dots.png" alt="выбрать из шаблона">
                                </a>
                            </#if>
                        </td>
                    </tr>
                    <tr class="lightPurpleColor">
                        <th>Маржа, %</th>
                        <td>
                            <#assign mdTask_pipeline_margin = model["mdTask"].pipeline.margin!''>
                            <#if model.readonly>
                            ${mdTask_pipeline_margin!''}
                            <#else>
                                <input id="mdTask_pipeline_margin" name="mdTask_pipeline_margin"
                                       value="${mdTask_pipeline_margin!''}"
                                       class="money2digits" valueType="money2digits" />
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="lightPurpleColor">Минимальная Ставка (Hurdle Rate)</th>
                        <td>
                            <#assign mdTask_pipeline_hurdleRate = model["mdTask"].pipeline.hurdleRate!''>
                            <#if mdTask_pipeline_hurdleRate?? && mdTask_pipeline_hurdleRate?has_content>
                                <#assign mdTask_pipeline_hurdleRate = ((mdTask_pipeline_hurdleRate * 1000)?round / 1000)?string["0.000"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_pipeline_hurdleRate!''}%
                            <#else>
                                <input id="mdTask_pipeline_hurdleRate" 
                                       name="mdTask_pipeline_hurdleRate"
                                       class="money3digits"
                                       value="${mdTask_pipeline_hurdleRate!''}"
                                       valueType="money3digits" />
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="lightPurpleColor">Маркап</th>
                        <td>
                            <#if model["mdTask"].product>
                                ${model["markup"]!''} %
                            <#else>
                                <#assign mdTask_pipeline_markup = model["mdTask"].pipeline.markup!''>
                                <#if mdTask_pipeline_markup?? && mdTask_pipeline_markup?has_content>
                                    <#assign mdTask_pipeline_markup = ((mdTask_pipeline_markup * 1000)?round / 1000)?string["0.000"]>
                                </#if>

                                <#if model.readonly>
                                ${mdTask_pipeline_markup!''}%
                                <#else>
                                    <#if model["mdTask"].product>
                                    <#else>
                                        <input id="mdTask_pipeline_markup"
                                               name="mdTask_pipeline_markup"
                                               class="money3digits"
                                               value="${mdTask_pipeline_markup!''}"
                                               valueType="money3digits" />
                                    </#if>
                                </#if>
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="lightPurpleColor">PCs: Кеш, млн. дол. США</th>
                        <td>
                            <#assign mdTask_pipeline_pcCash = model["mdTask"].pipeline.pcCash!''>
                            <#if mdTask_pipeline_pcCash?? && mdTask_pipeline_pcCash?has_content>
                                <#assign mdTask_pipeline_pcCash = ((mdTask_pipeline_pcCash * 10)?round / 10)?string[",##0.0"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_pipeline_pcCash!''}
                            <#else>
                                <input id="mdTask_pipeline_pcCash" 
                                       name="mdTask_pipeline_pcCash"
                                       class="money1digits"
                                       value="${mdTask_pipeline_pcCash!''}"
                                       valueType="money1digits" />
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="lightPurpleColor">PCs: Резервы, млн. дол. США</th>
                        <td>
                            <#assign mdTask_pipeline_pcReserve = model["mdTask"].pipeline.pcReserve!''>
                            <#if mdTask_pipeline_pcReserve?? && mdTask_pipeline_pcReserve?has_content>
                                <#assign mdTask_pipeline_pcReserve = ((mdTask_pipeline_pcReserve * 10)?round / 10)?string[",##0.0"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_pipeline_pcReserve!''}
                            <#else>
                                <input id="mdTask_pipeline_pcReserve" 
                                       name="mdTask_pipeline_pcReserve"
                                       class="money1digits"
                                       value="${mdTask_pipeline_pcReserve!''}"
                                       valueType="money1digits" />
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="lightPurpleColor">FX Rates, млн. дол. США</th>
                        <td>
                            <#assign mdTask_pipeline_pcDerivative = model["mdTask"].pipeline.pcDerivative!''>
                            <#if mdTask_pipeline_pcDerivative?? && mdTask_pipeline_pcDerivative?has_content>
                                <#assign mdTask_pipeline_pcDerivative = ((mdTask_pipeline_pcDerivative * 10)?round / 10)?string[",##0.0"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_pipeline_pcDerivative!''}
                            <#else>
                                <input id="mdTask_pipeline_pcDerivative" 
                                       name="mdTask_pipeline_pcDerivative"
                                       class="money1digits"
                                       value="${mdTask_pipeline_pcDerivative!''}"
                                       valueType="money1digits" />
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="lightPurpleColor">Commodities, млн. дол. США</th>
                        <td>
                            <#assign mdTask_pipeline_pcTotal = model["mdTask"].pipeline.pcTotal!''>
                            <#if mdTask_pipeline_pcTotal?? && mdTask_pipeline_pcTotal?has_content>
                                <#assign mdTask_pipeline_pcTotal = ((mdTask_pipeline_pcTotal * 10)?round / 10)?string[",##0.0"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_pipeline_pcTotal!''}
                            <#else>
                                <input id="mdTask_pipeline_pcTotal" 
                                       name="mdTask_pipeline_pcTotal"
                                       class="money1digits"
                                       value="${mdTask_pipeline_pcTotal!''}"
                                       valueType="money1digits" />
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="lightPurpleColor">Всего Ожидаемых млн. дол. США</th>
                        <td>
                            <#assign mdTask_pipeline_expectedValue = model["mdTask"].pipeline.expectedValue!''>
                            <#if mdTask_pipeline_expectedValue?? && mdTask_pipeline_expectedValue?has_content>
                                <#assign mdTask_pipeline_expectedValue = ((mdTask_pipeline_expectedValue * 10)?round / 10)?string[",##0.0"]>
                            </#if>
                            ${mdTask_pipeline_expectedValue!''}
                        </td>
                    </tr>
                </table>    
                <table class="prodManagerSubTable prodManagerSubTableSizeThree">    
                    <tr style="display: none">
                        <th class="cyanColor">Кредитная Линия млн. дол. США</th>
                        <td>
                            <#if model["mdTask"].pipeline.lineVolume??>
                                ${((model["mdTask"].pipeline.lineVolume * 10)?round / 10)?string[",##0.0"]}
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="cyanColor">Выбранный Объём Линии в Валюте Сделки</th>
                        <td>
                            <#assign mdTask_pipeline_selectedLineVolume = model["mdTask"].pipeline.selectedLineVolume!''>
                            <#if mdTask_pipeline_selectedLineVolume?? && mdTask_pipeline_selectedLineVolume?has_content>
                                <#assign mdTask_pipeline_selectedLineVolume = ((mdTask_pipeline_selectedLineVolume * 10)?round / 10)?string[",##0.0"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_pipeline_selectedLineVolume!''}
                            <#else>
                                <input id="mdTask_pipeline_selectedLineVolume" 
                                       name="mdTask_pipeline_selectedLineVolume"
                                       class="money1digits"
                                       value="${mdTask_pipeline_selectedLineVolume!''}"
                                       valueType="money1digits" />
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="cyanColor">Объём Линии, Доступный для Выборки в Валюте Сделки</th>
                        <td>
                            <#if model["mdTask"].pipeline.availibleLineVolume??>
                                ${((model["mdTask"].pipeline.availibleLineVolume * 10)?round / 10)?string[",##0.0"]}
                            </#if>
                        </td>
                    </tr>
                </table>
            </td>
            <td class="prodManagerMainTd">
                <table class="prodManagerSubTable prodManagerSubTableSizeFour">
                <#list model["sales"] as fo>
                    <tr class="yellowColor">
                        <#if fo?is_first>
                            <th rowspan="${model["sales"]?size}">Продуктовый менеджер</th>
                        </#if>
                        <td>
                            ${fo!''}
                        </td>
                    </tr>
                </#list>
                <#if model["sales"]?size == 0>
                    <tr class="yellowColor"><th>Продуктовый менеджер</th><td></td></tr>
                </#if>
                <#list model["credit"] as fo>
                    <tr class="yellowColor">
                    <#if fo?is_first>
                        <th rowspan="${model["credit"]?size}">Кредитный аналитик</th>
                    </#if>
                        <td>${fo!''}</td>
                    </tr>
                </#list>
                <#if model["credit"]?size == 0>
                    <tr class="yellowColor"><th>Кредитный аналитик</th><td></td></tr>
                </#if>
                    <tr class="yellowColor">
                        <th>Структуратор</th>
                        <td>
                        <#if model["mdTask"].pipeline.structureInspector??>
                                ${model["mdTask"].pipeline.structureInspector.fullName!''}
                            </#if>
                        </td>
                    </tr>
                    <tr class="yellowColor">
                        <th>Клиентский Менеджер</th>
                        <td>
                        <#if model["mdTask"].pipeline.clientManager??>
                                ${model["mdTask"].pipeline.clientManager.fullName!''}
                            </#if>
                        </td>
                    </tr>
                <#list model["gss"] as fo>
                    <tr class="yellowColor">
                    <#if fo?is_first>
                        <th rowspan="${model["gss"]?size}">Менеджер ГСС</th>
                    </#if>
                        <td>${fo!''}</td>
                    </tr>
                </#list>
                <#if model["gss"]?size == 0>
                    <tr class="yellowColor"><th>Менеджер ГСС</th><td></td></tr>
                </#if>
                    <tr>
                        <th class="yellowColor">Трейдинг Деск</th>
                        <td>
                        <#assign mdTask_pipeline_tradingDesk = model["mdTask"].pipeline.tradingDesk!''>
                        <#if model.readonly>
                        ${mdTask_pipeline_tradingDesk!''}
                        <#else>
                            <input id="mdTask_pipeline_tradingDesk"
                                   name="mdTask_pipeline_tradingDesk"
                                   value="${mdTask_pipeline_tradingDesk!''}" />
                            <br />
                            <a href="javascript:;" class="dialogActivator" dialogId="mdTask_pipeline_tradingDesks"
                               targetId="mdTask_pipeline_tradingDesk">
                                <img src="style/dots.png" alt="выбрать из шаблона">
                            </a>
                        </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greenColor">Выдающий Банк</th>
                        <td>
                        <#assign mdTask_pipeline_vtbContractor = model["mdTask"].pipeline.vtbContractor!''>
                        <#if model.readonly>
                        ${mdTask_pipeline_vtbContractor!''}
                        <#else>
                            <input id="mdTask_pipeline_vtbContractor" readonly
                                   name="mdTask_pipeline_vtbContractor"
                                   value="${mdTask_pipeline_vtbContractor!''}" />

                            <br />
                            <a href="javascript:;" class="dialogActivator" dialogId="mdTask_pipeline_fundCompanies"
                               targetId="mdTask_pipeline_vtbContractor">
                                <img src="style/dots.png" alt="выбрать из шаблона">
                            </a>
                        </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greenColor">Фондирующий Банк</th>
                        <td>
                        <#assign mdTask_pipeline_fundCompany = model["mdTask"].pipeline.fundCompany!''>
                        <#if model.readonly>
                        ${mdTask_pipeline_fundCompany!''}
                        <#else>
                            <input id="mdTask_pipeline_fundCompany"
                                   name="mdTask_pipeline_fundCompany"
                                   value="${mdTask_pipeline_fundCompany!''}" />

                            <br />
                            <a href="javascript:;" class="dialogActivator" dialogId="mdTask_pipeline_fundCompanies"
                               targetId="mdTask_pipeline_fundCompany">
                                <img src="style/dots.png" alt="выбрать из шаблона">
                            </a>
                        </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greenColor">ТЭФ Импорт/ Экспорт</th>
                        <td>
                        <#assign mdTask_pipeline_flowInvestment = model["mdTask"].pipeline.flowInvestment!''>
                        <#if model.readonly>
                        ${mdTask_pipeline_flowInvestment!''}
                        <#else>
                            <#if model["flowInvestmentValues"]?? && model["flowInvestmentValues"]?first??>
                                <#assign mdTask_pipeline_flowInvestmentValues = model["flowInvestmentValues"]!''>
                            </#if>

                            <#if mdTask_pipeline_flowInvestmentValues??>
                                <select name="mdTask_pipeline_flowInvestment">
                                    <option value="" />
                                    <#list mdTask_pipeline_flowInvestmentValues as fi>
                                        <#if mdTask_pipeline_flowInvestmentValues??>
                                            <option <#if mdTask_pipeline_flowInvestment?lower_case = fi?lower_case>
                                                    selected="selected"
                                            </#if>
                                                    value="${fi?capitalize!''}">
                                            ${fi?capitalize!''}
                                            </option>
                                        </#if>
                                    </#list>
                                </select>
                            <#else>
                            ${mdTask_pipeline_flowInvestment!''}
                            </#if>
                        </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greenColor">Тип сделки ТЭФ</th>
                        <td>
                            <input type="hidden" id="mdTask_trade_finance" name="mdTask_trade_finance" value="${model["mdTask"].pipeline.tradeFinance!''}">
                            <div id="mdTask_trade_finance_div">${model["mdTask"].pipeline.tradeFinanceName!''}</div>
                            <#if !model.readonly>
                                <a href="javascript:;" class="dialogActivator" dialogId="mdTask_trade_finance_dialog">
                                    <img src="style/dots.png" alt="выбрать">
                                </a>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <th class="greenColor">312 П</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_publicDeal"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.publicDeal>checked="checked"</#if> />
                        </td>
                    </tr>

                    <tr>
                        <th class="greenColor">Пролонгация</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_prolongation"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.prolongation>checked="checked"</#if> />
                        </td>
                    </tr>
                    <tr>
                        <th class="greenColor">Новый Клиент</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_newClient"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.newClient>checked="checked"</#if> />
                        </td>
                    </tr>
                    <tr>
                        <th class="greenColor">Приоритет Менеджмента</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_managementPriority"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.managementPriority>checked="checked"</#if> />
                        </td>
                    </tr>

                    <tr class="greenColor" style="display: none">
                        <th>Рейтинг клиента</th>
                        <td>
                            <#if model["mdTask"].mainOrganization??>
                                <#assign mdTask_mainOrganization_preliminaryRating = model["mdTask"].mainOrganization.preliminaryRating!''>
                            </#if>
                            
                            <#if model.readonly>
                                ${mdTask_mainOrganization_preliminaryRating!''}%
                            <#else>
                                <input id="mdTask_mainOrganization_preliminaryRating"
                                       name="mdTask_mainOrganization_preliminaryRating"
                                       value="${mdTask_mainOrganization_preliminaryRating!''}" />
                            </#if>
                        </td>
                    </tr>
                    <tr style="display: none">
                        <th class="greenColor">Коэффициент Типа Сделки</th>
                        <td>
                            <#assign mdTask_pipeline_productTypeFactor = model["mdTask"].pipeline.productTypeFactor!''>
                            <#if mdTask_pipeline_productTypeFactor?? && mdTask_pipeline_productTypeFactor?has_content>
                                <#assign mdTask_pipeline_productTypeFactor = ((mdTask_pipeline_productTypeFactor * 100)?round / 100)?string["0.00"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_pipeline_productTypeFactor!''}%
                            <#else>
                                <input id="mdTask_pipeline_productTypeFactor" 
                                       name="mdTask_pipeline_productTypeFactor"
                                       class="money2digits"
                                       value="${mdTask_pipeline_productTypeFactor!''}"
                                       valueType="money2digitsOrInt" />%
                                       
                                <br />
                                <a href="javascript:;" class="dialogActivator" dialogId="mdTask_pipeline_productTypeFactors" 
                                    targetId="mdTask_pipeline_productTypeFactor">
                                    <img src="style/dots.png" alt="выбрать из шаблона">
                                </a>
                            </#if>
                        </td>
                        
                    </tr>
                    <tr style="display: none">
                        <th class="greenColor">Коэффициент по Сроку Погашения</th>
                        <td>
                            <#assign mdTask_pipeline_periodFactor = model["mdTask"].pipeline.periodFactor!''>
                            <#if mdTask_pipeline_periodFactor?? && mdTask_pipeline_periodFactor?has_content>
                                <#assign mdTask_pipeline_periodFactor = ((mdTask_pipeline_periodFactor * 100)?round / 100)?string["0.00"]>
                            </#if>
                            <#if model.readonly>
                                ${mdTask_pipeline_periodFactor!''}
                            <#else>
                                <input id="mdTask_pipeline_periodFactor" 
                                       name="mdTask_pipeline_periodFactor"
                                       class="money2digits"
                                       value="${mdTask_pipeline_periodFactor!''}"
                                       valueType="money2digitsOrInt" />
                                       
                                <br />
                                <a href="javascript:;" class="dialogActivator" dialogId="mdTask_pipeline_periodFactors" 
                                    targetId="mdTask_pipeline_periodFactor">
                                    <img src="style/dots.png" alt="выбрать из шаблона">
                                </a>
                            </#if>
                        </td>
                    </tr>




                    <tr>
                        <th class="greenColor">Не показывать в отчетах</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_hideInReport"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.hideInReport>checked="checked"</#if> />
                        </td>
                    </tr>
                    <tr>
                        <th class="greenColor">Не показывать в пайплайне на трейдерс митинг</th>
                        <td style="text-align: center;">
                            <input type="checkbox" name="mdTask_pipeline_hideInReportTraders"
                                   <#if model.readonly>disabled="disabled"</#if>
                                   <#if model["mdTask"].pipeline.hideInReportTraders>checked="checked"</#if> />
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    
    <#if !model.readonly>
        <input type="hidden" name="updatePmSection" value="updatePmSection" />
    </#if>
    
    <div id="mdTask_productTypes" title="Тип Сделки" style="display: none" class="sectionPmDialog">
        <#if model["products"]?? && model["products"]?first??>
            <#assign mdTask_products = model["products"]>
        </#if>
    
        <#if mdTask_products??>
            <ul>
                <#list mdTask_products as product>
                    <li>
                        <#if product??>
                            <a href="javascript:;" returnValue="${product.productid!''}">${product.name!''}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
    
    <div id="mdTask_pipeline_statuses" title="Стадии сделки" style="display: none;">
        <#if model["statuses"]?? && model["statuses"]?first??>
            <#assign mdTask_pipeline_statuses = model["statuses"]>
        </#if>
    
        <#if mdTask_pipeline_statuses??>
        <table>
            <tr><th>Вероятность закрытия,%</th><th>Название стадии</th><th>Описание стадии</th></tr>
            <#list mdTask_pipeline_statuses as status>
            <tr><td>${status.value}</td><td>
                <a href="javascript:;"
                   onclick="$('#mdTask_pipeline_status').val('${status.name}');$('#mdTask_pipeline_closeProbability').val('${status.value}');$('#mdTask_pipeline_status_div').text('${status.name}')"
                >${status.name!''}</a>
            </td><td>${status.description!''}</td></tr>
            </#list>
        </table>
        </#if>
    </div>

    <div id="mdTask_trade_finance_dialog" title="Тип сделки ТЭФ" style="display: none;">
        <#if model["tradeFinanceList"]??>
            <ul>
            <#list model["tradeFinanceList"] as tf>
            <li>
                <a href="javascript:;"
                   onclick="$('#mdTask_trade_finance').val('${tf.id!''}');$('#mdTask_trade_finance_div').text('${tf.name}')"
                >${tf.name!''}</a>
            </li>
            </#list>
            </ul>
        </#if>
    </div>

    <div id="mdTask_pipeline_laws" title="Применимое Право" style="display: none;" class="sectionPmDialog">
        <#if model["laws"]?? && model["laws"]?first??>
            <#assign mdTask_pipeline_laws = model["laws"]>
        </#if>
    
        <#if mdTask_pipeline_laws??>
            <ul>
                <#list mdTask_pipeline_laws as law>
                    <li>
                        <a href="javascript:;">${law.value!''}</a>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
    
    <div id="mdTask_pipeline_financingObjectives" title="Цели финансирования" style="display: none;" class="sectionPmDialog">
        <#if model["financingObjectives"]?? && model["financingObjectives"]?first??>
            <#assign mdTask_pipeline_financingObjectives = model["financingObjectives"]>
        </#if>
    
        <#if mdTask_pipeline_financingObjectives??>
            <ul>
                <#list mdTask_pipeline_financingObjectives as fo>
                    <li>
                        <a href="javascript:;" onclick="$('#'+$('#selectedID').val()).text('${fo!''}');$('#mdTask_pipeline_financingObjectives').dialog('close');">${fo!''}</a>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
    
    <div id="mdTask_pipeline_productTypeFactors" title="Коэффициент Типа Сделки" style="display: none;" class="sectionPmDialog">
        <#if model["productTypeFactors"]?? && model["productTypeFactors"]?first??>
            <#assign mdTask_pipeline_productTypeFactors = model["productTypeFactors"]>
        </#if>
    
        <#if mdTask_pipeline_productTypeFactors??>
            <ul>
                <#list mdTask_pipeline_productTypeFactors as ptf>
                    <li>
                        <#if ptf?? && ptf?has_content>
                            <a href="javascript:;">${((ptf * 100)?round / 100)?string["0.00"]}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
    
    <div id="mdTask_pipeline_periodFactors" title="Коэффициент по Сроку Погашения" style="display: none;" class="sectionPmDialog">
        <#if model["periodFactors"]?? && model["periodFactors"]?first??>
            <#assign mdTask_pipeline_periodFactors = model["periodFactors"]>
        </#if>
    
        <#if mdTask_pipeline_periodFactors??>
            <ul>
                <#list mdTask_pipeline_periodFactors as pf>
                    <li>
                        <#if pf?? && pf?has_content>
                            <a href="javascript:;">${((pf * 100)?round / 100)?string["0.00"]}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
    
    <div id="mdTask_pipeline_fundCompanies" title="Фондирующие Компании" style="display: none;" class="sectionPmDialog">
        <#if model["fundCompanies"]?? && model["fundCompanies"]?first??>
            <#assign mdTask_pipeline_fundCompanies = model["fundCompanies"]>
        </#if>
    
        <#if mdTask_pipeline_fundCompanies??>
            <ul>
                <#list mdTask_pipeline_fundCompanies as fc>
                    <li>
                        <a href="javascript:;">${fc!''}</a>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
    
    <div id="mdTask_pipeline_tradingDesks" title="Трейдинг Деск" style="display: none;" class="sectionPmDialog">
        <#if model["tradingDesks"]?? && model["tradingDesks"]?first??>
            <#assign mdTask_pipeline_tradingDesks = model["tradingDesks"]>
        </#if>
    
        <#if mdTask_pipeline_tradingDesks??>
            <ul>
                <#list mdTask_pipeline_tradingDesks as td>
                    <li>
                        <a href="javascript:;">${td!''}</a>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
    
    <div id="mdTask_fixingRateSpreads" title="Компенсирующий спрэд за фиксацию процентной ставки" style="display: none;" class="sectionPmDialog">
        <#if model["fixingRateSpreads"]?? && model["fixingRateSpreads"]?first??>
            <#assign mdTask_fixingRateSpreads = model["fixingRateSpreads"]>
        </#if>
    
        <#if mdTask_fixingRateSpreads??>
            <ul>
                <#list mdTask_fixingRateSpreads as frs>
                    <li>
                        <#if frs?? && frs?has_content>
                            <a href="javascript:;">${((frs * 100 * 100)?round / 100)?string["0.00"]}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        <#else>
            Нет доступных значений
        </#if>
    </div>
    
    <div id="mdTask_earlyRepaymentSpreads" title="Компенсирующий спрэд за досрочное погашение" style="display: none;" class="sectionPmDialog">
        <#if model["earlyRepaymentSpreads"]?? && model["earlyRepaymentSpreads"]?first??>
            <#assign mdTask_earlyRepaymentSpreads = model["earlyRepaymentSpreads"]>
        </#if>
    
        <#if mdTask_earlyRepaymentSpreads??>
            <ul>
                <#list mdTask_earlyRepaymentSpreads as ers>
                    <li>
                        <#if ers?? && ers?has_content>
                            <a href="javascript:;">${((ers * 100 * 100)?round / 100)?string["0.00"]}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        <#else>
            Нет доступных значений
        </#if>
    </div>
</div>
<script>
    $(function () {
        $(document).tooltip({
            content: function () {
                return $(this).prop('title');
            }
        });
    });
    $(document).ready(function() {
        pipelineHandler();
    });
</script>