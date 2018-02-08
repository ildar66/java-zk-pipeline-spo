package ru.md.spo.ejb;

import java.util.List;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.Pair;

import ru.md.dict.dbobjects.ConditionTypeJPA;
import ru.md.dict.dbobjects.DepositorFinStatusJPA;
import ru.md.dict.dbobjects.EarlyRepaymentJPA;
import ru.md.dict.dbobjects.OperationTypeJPA;
import ru.md.dict.dbobjects.PipelineCoeffsJPA;
import ru.md.dict.dbobjects.PipelineFinancialGoalJPA;
import ru.md.dict.dbobjects.PipelineFundingCompanyJPA;
import ru.md.dict.dbobjects.PipelineTradingDeskJPA;
import ru.md.dict.dbobjects.RiskStepupFactorJPA;
import ru.md.dict.dbobjects.SupplyTypeJPA;
import ru.md.dict.dbobjects.SystemModuleJPA;
import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.spo.dbobjects.CdAcredetivSourcePaymentJPA;
import ru.md.spo.dbobjects.CdCreditTurnoverCriteriumJPA;
import ru.md.spo.dbobjects.CdCreditTurnoverPremiumJPA;
import ru.md.spo.dbobjects.CdPremiumTypeJPA;
import ru.md.spo.dbobjects.CdRiskpremiumJPA;
import ru.md.spo.dbobjects.DependingLoanJPA;
import ru.md.spo.dbobjects.FundDownJPA;
import ru.md.spo.dbobjects.OrgJPA;
import ru.md.spo.dbobjects.ProductTypeJPA;
import ru.md.spo.dbobjects.PunitiveMeasureJPA;
import ru.md.spo.dbobjects.StatusReturnJPA;
import ru.md.spo.dbobjects.StavspredJPA;
import ru.md.spo.util.Page;

import com.vtb.domain.ProductGroup;
import com.vtb.domain.StavDefrayalExes;
import com.vtb.exception.FactoryException;
/**
 * Интерфейс для работы со справочниками.
 * @author Andrey Pavlenko
 *
 */
@Local
public interface DictionaryFacadeLocal {
	List<ConditionTypeJPA> findConditionTypes();
    List<DepositorFinStatusJPA> findDepositorFinStatus();
    List<OperationTypeJPA> findOperationType();
    List<SupplyTypeJPA> findSupplyType();
    /** Покрытие прямых расходов и Покрытие общебанковских расходов*/
    List<StavDefrayalExes> findStavDefrayalExes(StavDefrayalExes.StavDefrayalExesType type, String clientCategory);
    
    List<ProductTypeJPA> findProductType();
    List<FundDownJPA> findFundDown();
    List<CdAcredetivSourcePaymentJPA> findAcredetivSourcePayment();
    List<CdCreditTurnoverCriteriumJPA> findCdCreditTurnoverCriterium();
    List<CdCreditTurnoverPremiumJPA> findCdCreditTurnoverPremium();
    List<CdRiskpremiumJPA> findCdRiskpremium();
    List<CdPremiumTypeJPA> findRiskpremiumType(CdPremiumTypeJPA.Type type);
    /**Компенсирующий спрэд за фиксацию ставки*/
    List<StavspredJPA> findStavspred(String cur, Long period);
    /**Компенсирующий спрэд за досрочное погашение в зависимости от срока кредита*/
    List<EarlyRepaymentJPA> findEarlyRepayment(String cur, Long period);
    /**Срок запрета досрочного погашения*/
    List<DependingLoanJPA> findDependingLoan(String cur, Long period);
    /**штрафные санкции*/
    List<PunitiveMeasureJPA> findPunitiveMeasure(String sanction_type);
    List<RiskStepupFactorJPA> findRiskStepupFactor();
    
    Double findStavbase(String curr, Integer interval);
    Double findStavarsmargin (String curr, Integer interval,String indRate);
    
    List<PipelineFinancialGoalJPA> findPipelineFinancialGoal();
    List<PipelineCoeffsJPA> findPipelineCoeffs(Long type);
    List<PipelineTradingDeskJPA> findPipelineTradingDesk();
    List<PipelineFundingCompanyJPA> findPipelineFundingCompany();
    
    List<String> findProjectTeamRoles();
    List<String> findMiddleOfficeRoles();
    OrgJPA getOrg(String id);
    Page<OrgJPA> findOrganizationPage(OrgJPA org, int start, int count, String orderBy) throws Exception;
    Pair<List<OrgJPA>,List<DepartmentJPA>> findOrganization4EK(String crmDepName, String ek_id, String place2Name) throws Exception;
    
    String saveSpoRoute(HttpServletRequest request) throws FactoryException;
    Long findSpoRoute(String stageName, Long initDepId, Long processTypeId);
    /**
     * @return размерность срока сделки
     */
    List<String> getPeriodDimension();
    /**
     * Возвращает порядок уплаты процентов
     */
    List<String> getPayInt();
    List<String> getComBase();
    
    List<ProductGroup> getProductGroupList();
    DepartmentJPA findDepartmentByShortName(String name);
    DepartmentJPA findDepartmentByProduct(String productTypeId);
    /**
     * Места проведение сделки
     * @return
     */
    List<DepartmentJPA> getExecDepList();
    /**
     * Инициирующие подразделения
     * @return
     */
    List<DepartmentJPA> getInitialDepList();
    
    SystemModuleJPA getModuleInfo(String key);
    List<String> findCurrencyList();

    StatusReturnJPA getApprovedStatusReturn();
    List<String> getIllegalLendingTargets();
}
