package ru.md.spo.ejb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.compendium.domain.MDCalcHistory;
import ru.masterdm.flexworkflow.integration.list.EFlexWorkflowSDOProperty;
import ru.masterdm.flexworkflow.integration.list.EFlexWorkflowSDOType;
import ru.masterdm.flexworkflow.logic.ejb.IFlexWorkflowIntegrationLocal;
import ru.masterdm.flexworkflow.logic.ejb.IFlexWorkflowIntegrationRemote;
import ru.masterdm.flexworkflow.util.integration.FlexWorkflowSDOHelper;
import ru.masterdm.integration.CCStatus;
import ru.masterdm.spo.integration.FilialTask;
import ru.masterdm.spo.integration.FilialTaskList;
import ru.masterdm.spo.integration.FilialTaskListFilter;
import ru.md.crm.dbobjects.CRMRating;
import ru.md.pup.dbobjects.ProcessTypeJPA;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.TaskJPA;

import com.vtb.domain.Supply4Rating;
import com.vtb.domain.Task;
import com.vtb.domain.Task4Rating;
import com.vtb.exception.CreateSDOFlexWorkflowException;
import com.vtb.exception.InvalidPropertySDOFlexWorkflowException;
import com.vtb.exception.ValidateSDOFlexWorkflowException;
import com.vtb.exception.VtbException;
import com.vtb.mapping.MapperFactory;
import com.vtb.mapping.jdbc.TaskMapper;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import commonj.sdo.DataObject;

@Stateless
public class FlexWorkflowIntegration implements IFlexWorkflowIntegrationRemote,IFlexWorkflowIntegrationLocal {
	@Resource
	SessionContext cnx;
	
	@EJB
    private TaskFacadeLocal taskFacade;
	
	@EJB
    private PupFacadeLocal pupFacade;
	
	@PersistenceUnit(unitName = "flexWorkflowEJBJPA")
    private EntityManagerFactory factory;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FlexWorkflowIntegration.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public DataObject getListOpportunity(DataObject input) {
		DataObject output = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			if (input == null)
				throw new VtbException("input data object is null");

			EFlexWorkflowSDOType dataObjectType = EFlexWorkflowSDOType.ORGANIZATION_ID_FILTER;
			Boolean isValid = FlexWorkflowSDOHelper.validateSDO(input, dataObjectType);
			if (isValid == null || !isValid) {
				baos = FlexWorkflowSDOHelper
						.getSDOAsByteArrayOutputStream(input);
				throw new ValidateSDOFlexWorkflowException(
						"can't validate input data object with type '"
								+ dataObjectType.toString()
								+ "'\ninput data object xml: '"
								+ baos.toString() + "'");
			}

			String organizationId = null;
			String xPath = EFlexWorkflowSDOProperty.ORGANIZATION_ID.getXPath();
			if (input.isSet(xPath)) {
				organizationId = input.getString(xPath);
			}

			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory
					.getActionProcessor("Task");
			//List<Task4Rating> task4RatingList = processor.getListOpportunity(organizationId);
			List<Task4Rating> task4RatingList = processor.getListOpportunity(organizationId,0,30);
			//FIXME точно здесь нужно без обеспечения

			if (task4RatingList == null) {
				return null;
			}

			List<DataObject> task4RatingListDataObject = new ArrayList<DataObject>();

			for (Task4Rating task4Rating : task4RatingList) {

				EFlexWorkflowSDOType task4RatingDataObjectType = EFlexWorkflowSDOType.TASK_4_RATING;

				DataObject task4RatingDataObject = FlexWorkflowSDOHelper
						.createSDO(task4RatingDataObjectType);

				if (task4RatingDataObject == null)
					throw new CreateSDOFlexWorkflowException(
							"can't create data object by type '"
									+ task4RatingDataObjectType.toString()
									+ "'");

				if (task4Rating.getIdMdTask() != null) {
					task4RatingDataObject.setLong(
							EFlexWorkflowSDOProperty.TASK_4_RATING_ID_MD_TASK
									.getXPath(), task4Rating.getIdMdTask());
				}
				if (task4Rating.getNumberDisplay() != null) {
					task4RatingDataObject
							.setString(
									EFlexWorkflowSDOProperty.TASK_4_RATING_NUMBER_DISPLAY
											.getXPath(), task4Rating
											.getNumberDisplay());
				}
				if (task4Rating.getSum() != null) {
					task4RatingDataObject.setBigDecimal(
							EFlexWorkflowSDOProperty.TASK_4_RATING_SUM
									.getXPath(), task4Rating.getSum());
				}
				if (task4Rating.getPeriod() != null) {
					task4RatingDataObject.setInt(
							EFlexWorkflowSDOProperty.TASK_4_RATING_PERIOD
									.getXPath(), task4Rating.getPeriod());
				}
				task4RatingDataObject.setBoolean(
						EFlexWorkflowSDOProperty.TASK_4_RATING_RATE_TYPE
								.getXPath(), task4Rating.isRateType());
				if (task4Rating.getOperationTypeCode() != null) {
					task4RatingDataObject
							.setLong(
									EFlexWorkflowSDOProperty.TASK_4_RATING_OPERATION_TYPE_CODE
											.getXPath(), task4Rating
											.getOperationTypeCode());
				}
				List<DataObject> supply4RatingList = new ArrayList<DataObject>();

				for (Supply4Rating s : task4Rating.getSupplyList()) {
					EFlexWorkflowSDOType supply4RatingDataObjectType = EFlexWorkflowSDOType.SUPPLY_4_RATING;

					DataObject supply4RatingDataObject = FlexWorkflowSDOHelper
							.createSDO(supply4RatingDataObjectType);

					if (supply4RatingDataObject == null)
						throw new CreateSDOFlexWorkflowException(
								"can't create data object by type '"
										+ supply4RatingDataObjectType
												.toString() + "'");

					if (s.getTypeCode() != null) {
						supply4RatingDataObject
								.setString(
										EFlexWorkflowSDOProperty.SUPPLY_4_RATING_TYPE_CODE
												.getXPath(), s.getTypeCode());
					}
					if (s.getSum() != null) {
						supply4RatingDataObject.setBigDecimal(
								EFlexWorkflowSDOProperty.SUPPLY_4_RATING_SUM
										.getXPath(), s.getSum());
					}
					if (s.getDepositorFinStatusCode() != null) {
						supply4RatingDataObject
								.setLong(
										EFlexWorkflowSDOProperty.SUPPLY_4_RATING_DEPOSITOR_FIN_STATUS_CODE
												.getXPath(), s
												.getDepositorFinStatusCode());
					}
					if (s.getLiquidityLevelCode() != null) {
						supply4RatingDataObject
								.setLong(
										EFlexWorkflowSDOProperty.SUPPLY_4_RATING_LIQUIDITY_LEVEL_CODE
												.getXPath(), s
												.getLiquidityLevelCode());
					}
					if (s.getSupplyTypeCode() != null) {
						supply4RatingDataObject
								.setLong(
										EFlexWorkflowSDOProperty.SUPPLY_4_RATING_SUPPLY_TYPE_CODE
												.getXPath(), s
												.getSupplyTypeCode());
					}
					isValid = FlexWorkflowSDOHelper.validateSDO(
							supply4RatingDataObject,
							supply4RatingDataObjectType);
					if (isValid == null || !isValid) {
						baos = FlexWorkflowSDOHelper
								.getSDOAsByteArrayOutputStream(supply4RatingDataObject);
						throw new ValidateSDOFlexWorkflowException(
								"can't validate output data object with type '"
										+ supply4RatingDataObjectType
												.toString()
										+ "'\ninstance data object xml: '"
										+ baos.toString() + "'");
					}

					supply4RatingList.add(supply4RatingDataObject);

				}
				if (supply4RatingList != null) {
					task4RatingDataObject.setList(
							EFlexWorkflowSDOProperty.TASK_4_RATING_SUPPLY_LIST
									.getXPath(), supply4RatingList);
				}
				isValid = FlexWorkflowSDOHelper.validateSDO(
						task4RatingDataObject, task4RatingDataObjectType);
				if (isValid == null || !isValid) {
					baos = FlexWorkflowSDOHelper
							.getSDOAsByteArrayOutputStream(task4RatingDataObject);
					throw new ValidateSDOFlexWorkflowException(
							"can't validate output data object with type '"
									+ task4RatingDataObjectType.toString()
									+ "'\ninstance data object xml: '"
									+ baos.toString() + "'");
				}

				task4RatingListDataObject.add(task4RatingDataObject);

			}

			dataObjectType = EFlexWorkflowSDOType.TASK_4_RATING_LIST;

			output = FlexWorkflowSDOHelper.createSDO(dataObjectType);

			if (output == null)
				throw new CreateSDOFlexWorkflowException(
						"can't create data object by type '"
								+ dataObjectType.toString() + "'");

			output.setList(EFlexWorkflowSDOProperty.TASK_4_RATING_LIST
					.getXPath(), task4RatingListDataObject);

			isValid = FlexWorkflowSDOHelper.validateSDO(output, dataObjectType);
			if (isValid == null || !isValid) {
				baos = FlexWorkflowSDOHelper
						.getSDOAsByteArrayOutputStream(output);
				throw new ValidateSDOFlexWorkflowException(
						"can't validate output data object with type '"
								+ dataObjectType.toString()
								+ "'\ninstance data object xml: '"
								+ baos.toString() + "'");
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			output = getFault(e);
		} finally {
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					LOGGER.warn( e.getMessage(), e);
				}
		}

		return output;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public DataObject getOpportunityInfo(DataObject input) {
		DataObject output = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			if (input == null)
				throw new VtbException("input data object is null");

			EFlexWorkflowSDOType dataObjectType = EFlexWorkflowSDOType.MD_TASK_ID_FILTER;
			Boolean isValid = FlexWorkflowSDOHelper.validateSDO(input,
					dataObjectType);
			if (isValid == null || !isValid) {
				baos = FlexWorkflowSDOHelper
						.getSDOAsByteArrayOutputStream(input);
				throw new ValidateSDOFlexWorkflowException(
						"can't validate input data object with type '"
								+ dataObjectType.toString()
								+ "'\ninput data object xml: '"
								+ baos.toString() + "'");
			}

			Long mdTaskId = null;
			String xPath = EFlexWorkflowSDOProperty.MD_TASK_ID.getXPath();
			if (input.isSet(xPath)) {
				mdTaskId = input.getLong(xPath);
			}

			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory
					.getActionProcessor("Task");
			Task4Rating task4Rating = processor.getOpportunityInfo(mdTaskId);

			if (task4Rating == null) {
				return null;
			}

			dataObjectType = EFlexWorkflowSDOType.TASK_4_RATING;

			output = FlexWorkflowSDOHelper.createSDO(dataObjectType);

			if (output == null)
				throw new CreateSDOFlexWorkflowException(
						"can't create data object by type '"
								+ dataObjectType.toString() + "'");

			if (task4Rating.getIdMdTask() != null) {
				output.setLong(
						EFlexWorkflowSDOProperty.TASK_4_RATING_ID_MD_TASK
								.getXPath(), task4Rating.getIdMdTask());
			}
			if (task4Rating.getNumberDisplay() != null) {
				output.setString(
						EFlexWorkflowSDOProperty.TASK_4_RATING_NUMBER_DISPLAY
								.getXPath(), task4Rating.getNumberDisplay());
			}
			if (task4Rating.getSum() != null) {
				output.setBigDecimal(EFlexWorkflowSDOProperty.TASK_4_RATING_SUM
						.getXPath(), task4Rating.getSum());
			}
			if (task4Rating.getPeriod() != null) {
				output.setInt(EFlexWorkflowSDOProperty.TASK_4_RATING_PERIOD
						.getXPath(), task4Rating.getPeriod());
			}
			output.setBoolean(EFlexWorkflowSDOProperty.TASK_4_RATING_RATE_TYPE
					.getXPath(), task4Rating.isRateType());
			if (task4Rating.getOperationTypeCode() != null) {
				output
						.setLong(
								EFlexWorkflowSDOProperty.TASK_4_RATING_OPERATION_TYPE_CODE
										.getXPath(), task4Rating
										.getOperationTypeCode());
			}
			List<DataObject> supply4RatingList = new ArrayList<DataObject>();

			for (Supply4Rating s : task4Rating.getSupplyList()) {
				EFlexWorkflowSDOType supply4RatingDataObjectType = EFlexWorkflowSDOType.SUPPLY_4_RATING;

				DataObject supply4RatingDataObject = FlexWorkflowSDOHelper
						.createSDO(supply4RatingDataObjectType);

				if (supply4RatingDataObject == null)
					throw new CreateSDOFlexWorkflowException(
							"can't create data object by type '"
									+ supply4RatingDataObjectType.toString()
									+ "'");
				if (s.getTypeCode() != null) {
					supply4RatingDataObject.setString(
							EFlexWorkflowSDOProperty.SUPPLY_4_RATING_TYPE_CODE
									.getXPath(), s.getTypeCode());
				}
				if (s.getSum() != null) {
					supply4RatingDataObject.setBigDecimal(
							EFlexWorkflowSDOProperty.SUPPLY_4_RATING_SUM
									.getXPath(), s.getSum());
				}
				if (s.getDepositorFinStatusCode() != null) {
					supply4RatingDataObject
							.setLong(
									EFlexWorkflowSDOProperty.SUPPLY_4_RATING_DEPOSITOR_FIN_STATUS_CODE
											.getXPath(), s
											.getDepositorFinStatusCode());
				}
				if (s.getLiquidityLevelCode() != null) {
					supply4RatingDataObject
							.setLong(
									EFlexWorkflowSDOProperty.SUPPLY_4_RATING_LIQUIDITY_LEVEL_CODE
											.getXPath(), s
											.getLiquidityLevelCode());
				}
				if (s.getSupplyTypeCode() != null) {
					supply4RatingDataObject
							.setLong(
									EFlexWorkflowSDOProperty.SUPPLY_4_RATING_SUPPLY_TYPE_CODE
											.getXPath(), s.getSupplyTypeCode());
				}
				isValid = FlexWorkflowSDOHelper.validateSDO(
						supply4RatingDataObject, supply4RatingDataObjectType);
				if (isValid == null || !isValid) {
					baos = FlexWorkflowSDOHelper
							.getSDOAsByteArrayOutputStream(supply4RatingDataObject);
					throw new ValidateSDOFlexWorkflowException(
							"can't validate output data object with type '"
									+ supply4RatingDataObjectType.toString()
									+ "'\ninstance data object xml: '"
									+ baos.toString() + "'");
				}

				supply4RatingList.add(supply4RatingDataObject);

			}
			if (supply4RatingList != null) {
				output.setList(
						EFlexWorkflowSDOProperty.TASK_4_RATING_SUPPLY_LIST
								.getXPath(), supply4RatingList);
			}
			isValid = FlexWorkflowSDOHelper.validateSDO(output, dataObjectType);
			if (isValid == null || !isValid) {
				baos = FlexWorkflowSDOHelper
						.getSDOAsByteArrayOutputStream(output);
				throw new ValidateSDOFlexWorkflowException(
						"can't validate output data object with type '"
								+ dataObjectType.toString()
								+ "'\ninstance data object xml: '"
								+ baos.toString() + "'");
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			output = getFault(e);
		} finally {
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					LOGGER.warn( e.getMessage(), e);
				}
		}

		return output;
	}

	/**
	 * ���������� {@link DataObject ������} ����
	 * {@link EFlexWorkflowSDOType#FAULT}
	 * 
	 * @param exception
	 *            {@link Exception ������}
	 * @return {@link DataObject ������} ���� {@link EFlexWorkflowSDOType#FAULT}
	 */
	private DataObject getFault(Exception exception) {
		DataObject fault = null;

		try {
			if (exception == null)
				throw new VtbException("exception is null");

			EFlexWorkflowSDOType faultType = EFlexWorkflowSDOType.FAULT;
			fault = FlexWorkflowSDOHelper.createSDO(faultType);

			if (fault == null)
				throw new CreateSDOFlexWorkflowException(
						"can't create data object by type '"
								+ faultType.toString() + "'");

			try {
				fault.setString(EFlexWorkflowSDOProperty.FAULT_EXCEPTION_CLASS
						.getXPath(), exception.getClass().getName());
				fault.setString(EFlexWorkflowSDOProperty.FAULT_MESSAGE
						.getXPath(), String.valueOf(exception.getMessage()));
				fault.setString(EFlexWorkflowSDOProperty.FAULT_STACK_TRACE
						.getXPath(), String.valueOf(ExceptionUtils
						.getFullStackTrace(exception)));
			} catch (IllegalArgumentException iae) {
				throw new InvalidPropertySDOFlexWorkflowException(iae);
			}

			Boolean isValid = FlexWorkflowSDOHelper.validateSDO(fault,
					faultType);
			if (isValid == null || !isValid)
				throw new ValidateSDOFlexWorkflowException(
						"can't validate fault data object with type '"
								+ faultType.toString() + "'");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			fault = null;
		}

		return fault;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public DataObject statusNotification(DataObject input) {
		LOGGER.info( "starting SPO statusNotification procedure...");
		
		DataObject output = null;
		ByteArrayOutputStream baos = null;
		try {
			if (input == null) throw new VtbException("input data object is null");

			EFlexWorkflowSDOType dataObjectType = EFlexWorkflowSDOType.CC_STATUS;
			Boolean isValid = FlexWorkflowSDOHelper.validateSDO(input, dataObjectType);
			if (isValid == null || !isValid) {
				baos = FlexWorkflowSDOHelper.getSDOAsByteArrayOutputStream(input);
				throw new ValidateSDOFlexWorkflowException(
						"can't validate input data object with type '" + dataObjectType.toString()
						+ "'\ninput data object xml: '" + baos.toString() + "'");
			}

			Long mdTaskId = null;
			String xPath = EFlexWorkflowSDOProperty.CC_STATUS_MD_TASK_ID.getXPath();
			if (input.isSet(xPath)) {
				mdTaskId = input.getLong(xPath);
			}

			Long ccResolutionStatusId = null;
			xPath = EFlexWorkflowSDOProperty.CC_STATUS_RESOLUTION_STATUS_ID.getXPath();
			if (input.isSet(xPath)) {
				ccResolutionStatusId = input.getLong(xPath);
			}

			Date meetingDate = null;
			xPath = EFlexWorkflowSDOProperty.CC_STATUS_MEETING_DATE.getXPath();
			if (input.isSet(xPath)) {
				meetingDate = FlexWorkflowSDOHelper.getDate(input, xPath);
			}

			String protocolId = null;
			xPath = EFlexWorkflowSDOProperty.CC_STATUS_PROTOCOL_ID.getXPath();
			if (input.isSet(xPath)) {
				protocolId = input.getString(xPath);
			}

			Long reportId = null;
			xPath = EFlexWorkflowSDOProperty.CC_STATUS_REPORT_ID.getXPath();
			if (input.isSet(xPath)) {
				reportId = input.getLong(xPath);
			}

			CCStatus status = new CCStatus();
			status.setId_report(reportId);
			status.setMeetingDate(meetingDate);
			status.setProtocol(protocolId);
			status.setStatus(ccResolutionStatusId);

			TaskMapper mapper = (TaskMapper) MapperFactory.getSystemMapperFactory().getMapper(Task.class);
			mapper.updateCCStatus(status, mdTaskId);

			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
			CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
			Task task = processor.getTask(new Task(mdTaskId));
			//решение пришло, можно разблокировать процесс
			processor.updateAttribute(task.getId_pup_process().longValue(), "Decision", task.getCcStatus().getStatus().getCategoryId().toString());
			
			try {
			    if (task.isLimit()){
			        crmFacadeLocal.exportLimit(task);
			    } else {
			        crmFacadeLocal.exportProduct(task);
			    }
			    CRMRating rating = new CRMRating();
			    MDCalcHistory h = processor.getMDCalcHistory(task.getContractors().get(0).getOrg().getAccountid(),
	                    new java.util.Date());
			    rating.setAccountid(task.getContractors().get(0).getOrg().getAccountid());
			    rating.setCount_rating(h.getTotalPoints());
			    rating.setCount_rating_date(h.getRDate());
			    rating.setCount_rating_val(h.getRating());
			    rating.setExpert_rating_val(h.getExpRating());
			    rating.setOpportunityid(task.getHeader().getCrmid());
		        rating.setCc_rating_val(h.getRatingCC());
			    crmFacadeLocal.exportRating(rating);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

			dataObjectType = EFlexWorkflowSDOType.VOID;

			output = FlexWorkflowSDOHelper.createSDO(dataObjectType);

			if (output == null)
				throw new CreateSDOFlexWorkflowException("can't create data object by type '" + dataObjectType.toString() + "'");

			isValid = FlexWorkflowSDOHelper.validateSDO(output, dataObjectType);
			if (isValid == null || !isValid) {
				baos = FlexWorkflowSDOHelper.getSDOAsByteArrayOutputStream(output);
				throw new ValidateSDOFlexWorkflowException(
						"can't validate output data object with type '" + dataObjectType.toString()
						+ "'\ninstance data object xml: '" + baos.toString() + "'");
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			output = getFault(e);
		} finally {
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					LOGGER.warn( e.getMessage(), e);
				}
		}
		LOGGER.info( "SPO statusNotification procedure finished.");
		return output;
	}

    /**
     * {@inheritDoc}
     */
    @Override @Deprecated
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EFlexWorkflowSDOType statusNotificationDTO(CCStatus status) {
        LOGGER.info( "starting SPO statusNotification procedure...");
        
        EFlexWorkflowSDOType output = null;

        try {
            if (status == null) throw new VtbException("input data object is null");
            Long mdTaskId = status.getQuestionId();
            TaskMapper mapper = (TaskMapper) MapperFactory.getSystemMapperFactory().getMapper(Task.class);
            mapper.updateCCStatus(status, mdTaskId);

            TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
            Task task = processor.getTask(new Task(mdTaskId));
            //решение пришло, можно разблокировать процесс
            processor.updateAttribute(task.getId_pup_process().longValue(), "Decision", 
                    task.getCcStatus().getStatus().getCategoryId().toString());
            
            try {
            	LOGGER.info("crmid="+task.getHeader().getCrmid());
                if (task.isLimit()){
                    crmFacadeLocal.exportLimit(task);
                } else {
                    crmFacadeLocal.exportProduct(task);
                }
                CRMRating rating = new CRMRating();
                MDCalcHistory h = processor.getMDCalcHistory(task.getContractors().get(0).getOrg().getAccountid(),
                        new java.util.Date());
                rating.setAccountid(task.getContractors().get(0).getOrg().getAccountid());
                rating.setCount_rating(h.getTotalPoints());
                rating.setCount_rating_date(h.getRDate());
                rating.setCount_rating_val(h.getRating());
                rating.setExpert_rating_val(h.getExpRating());
                rating.setOpportunityid(task.getHeader().getCrmid());
                rating.setCc_rating_val(h.getRatingCC());
                crmFacadeLocal.exportRating(rating);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            output = EFlexWorkflowSDOType.VOID;
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            output = EFlexWorkflowSDOType.FAULT;
        }
        LOGGER.info( "SPO statusNotification procedure finished.");
        return output;
    }
    
	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public FilialTaskList getFilialTaskList(FilialTaskListFilter filter) {
		FilialTaskList res = new FilialTaskList();
		try {
			res.setLogin(cnx.getCallerPrincipal().getName());
			if(res.getLogin().equals("UNAUTHENTICATED")) res.setLogin("adminwf");//отладка на сервере без аутентификации
			LOGGER.info("login: "+res.getLogin());
			//найти БП филиала
			EntityManager em = factory.createEntityManager();
			Query query = em.createQuery("SELECT u FROM UserJPA u where LOWER(u.login) = :login");
	        query.setParameter("login", res.getLogin().toLowerCase());
	        List<UserJPA> userlist = (List<UserJPA>) query.getResultList();
	        if (userlist.size() == 0) throw new Exception("нет пользователя с таким логином "+res.getLogin());
	        UserJPA user = userlist.get(0);
			String processTypeString = "0";
			List<ProcessTypeJPA> listProcessType = (List<ProcessTypeJPA>)em.createQuery(
					"SELECT u FROM ProcessTypeJPA u").getResultList();
			for(ProcessTypeJPA processType : listProcessType){
				if(processType.isPortalProcess()){
					processTypeString += ", " + processType.getIdTypeProcess().toString();
					if (user.hasRole(processType.getIdTypeProcess(), "Клиентский менеджер")) res.setShowCreateLink(true);
				}
			}
			//получить список сделок
			String sql = " from(" +
					" select t.id_task,p.id_process,p.id_type_process from tasks t inner join processes p on p.id_process=t.id_process where t.id_status<3  "+
					"union all "+
					"select null,p.id_process,p.id_type_process from processes p where p.id_status=4"+
					") q inner join mdtask m on m.id_pup_process=q.id_process " +
					"where q.id_type_process in(" + processTypeString + ") " +
					"and m.initdepartment in (select dp.id_department_child from departments_par dp " +
					"CONNECT BY PRIOR id_department_child=id_department_par START WITH dp.id_department_par ="+user.getDepartment().getIdDepartment()+
					" union select "+user.getDepartment().getIdDepartment()+" from dual)";
			//разбор фильтра
			if(filter.getTaskNumber()!=null) sql += " and m.mdtask_number="+filter.getTaskNumber();
			if(filter.getSumFrom()!=null) sql += " and m.mdtask_sum>=" + filter.getSumFrom();
			if(filter.getSumTo()!=null) sql += " and m.mdtask_sum<=" + filter.getSumTo();
			if(filter.getCur()!=null) sql += " and lower(currency) like '"+filter.getCur().toLowerCase()+"'";
			if(filter.getOrgName()!=null) sql+= " and m.id_mdtask in (select r.id_mdtask from r_org_mdtask r " +
					"inner join v_organisation o on o.crmid=r.id_crmorg where lower(o.organizationname) like '%"+
					filter.getOrgName().toLowerCase()+"%')";
			if(filter.isHideClosed()) sql += " and q.id_task is not null";
			/*
			private String productName;//	Вид сделки
			private String userName=null;//	ФИО инициатора сделки(пользователя создавшего сделку)
			*/
			
			res.setTotalCount(((BigDecimal) em.createNativeQuery(
					"select count(*) " + sql).getSingleResult()).longValue());
			sql = "select q.id_task,q.id_process " + sql + " order by q.id_process desc";
			if(filter.getPageNum()!=null && filter.getPageSize()!=null)
				sql = "select * from ( select /*+ FIRST_ROWS(n) */ a.*, ROWNUM rnum from (" + sql 
				    + ") a where ROWNUM <= "+filter.getMaxRowToTetch()+" ) where rnum  >= " + filter.getMinRowToTetch();
			List<Object[]> list = em.createNativeQuery(sql).getResultList();
			for(Object[] element : list){
				BigDecimal pupProcessId = (BigDecimal) element[1];
				TaskJPA task = taskFacade.getTaskByPupID(pupProcessId.longValue());
				FilialTask ft = task.toFilialTask((BigDecimal) element[0]);
				ft.setCanCloseTask(res.isShowCreateLink()&&ft.getIdPupTask()!=null);
				ft.setCanEditTask(res.isShowCreateLink()&&ft.getIdPupTask()!=null);
				//кто редактирует и тот ли это, кто запрашивает список
				if(ft.getIdPupTask()!=null && pupFacade.getTask(ft.getIdPupTask()).getExecutor()!=null){
					UserJPA executor = pupFacade.getTask(ft.getIdPupTask()).getExecutor();
					ft.setWhoWork(executor.getFullName());
					if(!user.equals(executor)) ft.setCanEditTask(false);
					if(user.equals(executor)) ft.setCanEditTask(true);
				}
				res.getList().add(ft);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return res;
	}
	
	@Override
	public void closeProcess(Long taskId) {
		try {
			TaskJPA task = taskFacade.getTask(taskId);
			UserJPA user = pupFacade.getCurrentUser();
			pupFacade.closeProcess(task.getProcess().getId(), user.getIdUser());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long whoWorkWithTask(Long mdtaskid) {
		for(TaskInfoJPA ti : pupFacade.getTaskInWork(taskFacade.getTask(mdtaskid).getProcess().getId())){
			if(ti.getExecutor()!=null) return ti.getExecutor().getIdUser();
		}
		return null;
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void acceptTask(Long mdtaskid) {
		EntityManager em = factory.createEntityManager();
		Query q = em.createQuery("SELECT u FROM TaskInfoJPA u where u.idStatus=1 and u.process.id= :processId");
		q.setParameter("processId", taskFacade.getTask(mdtaskid).getProcess().getId());
		for(Object tio : q.getResultList()){
			TaskInfoJPA ti = (TaskInfoJPA) tio;
			try {
				UserJPA user = pupFacade.getCurrentUser();
				pupFacade.acceptWork(ti.getIdTask(), user.getIdUser());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(),e);
			}
		}
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deAcceptTask(Long mdtaskid) {
        List<TaskInfoJPA> tasks = pupFacade.getTaskInWork(taskFacade.getTask(mdtaskid).getProcess().getId());
        for(TaskInfoJPA task : tasks){
        	try {
        		pupFacade.reacceptWork(task.getIdTask(), task.getExecutor().getIdUser());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(),e);
			}
        }
	}

}
