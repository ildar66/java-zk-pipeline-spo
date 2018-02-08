package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.vtb.domain.ExtendText;
import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.ProjectTeamMember;
import com.vtb.domain.Supply4Rating;
import com.vtb.domain.Task;
import com.vtb.domain.Task4Rating;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskVersion;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.util.ApplProperties;
import com.vtb.util.EJBClientHelper;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.value.Page;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * JDBC-реализация интерфейса <code>TaskMapper</code>
 *
 * @author Andrey Pavlenko
 */
public class TaskMapper extends JDBCMapperExt<Task> implements com.vtb.mapping.TaskMapper {
    private static final Logger LOGGER = Logger.getLogger(TaskMapper.class.getName());

	private static final String INSERT_EXTEND_TEXT = "insert into "
            + " extend_text ("
            + "id, id_mdtask, description, context, id_author, signature, id_approved_author, approved_signature, create_date, approved_date, validto) "
            + "values (EXTEND_TEXT_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String DELETE_EXTEND_TEXT = "DELETE FROM "
            + " extend_text WHERE ID_MDTASK=?";

    private static final String SELECT_EXTEND_TEXT = "select id, description, context, id_author, signature, id_approved_author, " +
            "approved_signature, create_date, approved_date, validto from " + " extend_text where id_mdtask=?";

    /**
     * Fill Task data from the database
     */
    protected Task findByPrimaryKeyImpl(Connection conn, Task task, boolean full) throws SQLException, MappingException {
    	long tstart=System.currentTimeMillis();
        task = new Task(task.getId_task());

        LOGGER.info("*** total findByPrimaryKeyImpl1() time "+(System.currentTimeMillis()-tstart));
        // контрагенты
        TaskMapperReadHelper.readContragents(conn, task);

        // основные параметры
        TaskMapperReadHelper.readParameters(conn, task);
        LOGGER.info("*** total findByPrimaryKeyImpl2() time "+(System.currentTimeMillis()-tstart));
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //если получаем легкую заявку для списка, то информации достаточно
        if(!full){
            return task;
        }

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //для полной заявки получаем еще много чего.

        // читаем секцию 'Основные параметры'
        boolean isExist = TaskMapperReadHelper.readParametersFull(conn, task);
        LOGGER.info("*** total findByPrimaryKeyImpl3() time "+(System.currentTimeMillis()-tstart));
        // Добавил, чтобы бессмысленно не загружать данные. См. конец функции. -- MK
        if (!isExist) return null;
        // списки из других таблиц

        // Секция 'Проектная команда'.
        // TODO : MKuznetsov refactor!!!
        task.setProjectTeamStructurerList(readProjectTeam(conn, task, "Структуратор"));
        task.setProjectTeamClientManagerList(readProjectTeam(conn, task, "Клиентский менеджер"));
        task.setProjectTeamSPKZList(readProjectTeam(conn, task, "Сотрудник СПКЗ"));
        task.setProjectTeamStrucurerManagerList(readProjectTeam(conn, task, "Руководитель структуратора"));
        task.setProjectTeamCreditAnalyticList(readProjectTeam(conn, task, "Кредитный аналитик"));
        task.setProjectTeamProductManagerList(readProjectTeam(conn, task, "Продуктовый менеджер"));

        LOGGER.info("*** total findByPrimaryKeyImpl4() time "+(System.currentTimeMillis()-tstart));
        // Секция 'Транши'.
        TaskMapperReadHelper.readTranches(conn, task);
        LOGGER.info("*** total findByPrimaryKeyImpl 5.1() time "+(System.currentTimeMillis()-tstart));

        // Секция 'Основные параметры'. Основная часть
        TaskMapperReadHelper.readMainParameters(conn, this, task);
        LOGGER.info("*** total findByPrimaryKeyImpl 5.2() time "+(System.currentTimeMillis()-tstart));

        //Секция 'Стоимостные условия'
        TaskMapperReadHelper.readPriceConditions(conn, task);
        LOGGER.info("*** total findByPrimaryKeyImpl 5.3() time "+(System.currentTimeMillis()-tstart));
        // Секция 'Стоп-факторы'
        TaskMapperReadHelper.readStopFactors(conn, task);

        //  Секция 'Ответственные подразделения'
        TaskMapperReadHelper.readDepartments(conn, task);

        //  Секция 'Обеспечение'
        TaskMapperReadHelper.readSupply(conn, task);

        // комментарии
        TaskMapperReadHelper.readComments(conn, task);
        LOGGER.info("*** total findByPrimaryKeyImpl6() time "+(System.currentTimeMillis()-tstart));
        // специальные и другие УСЛОВИЯ
        TaskMapperReadHelper.readSpecialOtherConditions(conn, task);

        // Таблица контрактов
        TaskMapperReadHelper.readContract(conn, task);

        //  Секция 'Всякая хренотень'
        TaskMapperReadHelper.readAllOthers(conn, task);
        LOGGER.info("*** total findByPrimaryKeyImpl7() time "+(System.currentTimeMillis()-tstart));
        loadExtendText(conn, task);
        LOGGER.info("*** total findByPrimaryKeyImpl8() time "+(System.currentTimeMillis()-tstart));
        // а почему так долго грузим, а потом null возвращаем?
        return (isExist == true ? task : null);
    }

    @Override
    protected void updateImpl(Connection conn, Task task) throws SQLException, MappingException {
    	throw new NotImplementedException(); //перенесено в TaskActionProcessorFacadeBean.updateTask(Task)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Task> findByOperator(String operator, String orderBy) throws MappingException, SQLException {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> findAll() throws MappingException {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            return this.findAllImpl(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Task findByPrimaryKey(Task anObject,boolean full) {
        long tstart=System.currentTimeMillis();
        try {
            Connection conn = getConn();
            if (conn == null){
                LOGGER.severe("can`t connect to oracle");
                return null;
            }
            Task task = this.findByPrimaryKeyImpl(conn, anObject,full);
            conn.close();
            LOGGER.info("*** total TaskMapper.findByPrimaryKey() time "+(System.currentTimeMillis()-tstart));
            return task;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
            return null;
        } catch (MappingException e) {
            LOGGER.log(Level.WARNING, "Mapping Exception ", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task create(Task anObject) throws DuplicateKeyException, MappingException {
        Connection conn;
        try {
            conn = getConn();
            return create(conn, anObject);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
            throw new MappingException(e.getMessage());
        }
    }

	@Override
    public Task create(Connection conn, Task anObject) throws DuplicateKeyException, MappingException {
		throw new NotImplementedException(); //перенесено в TaskActionProcessorFacadeBean.createTask(Task)
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Task anObject) throws NoSuchObjectException {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            this.removeImpl(conn, anObject);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
        } catch (MappingException e) {
            LOGGER.log(Level.WARNING, "Mapping Exception ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Task anObject) throws MappingException {
        try {
            update(anObject, getConn());
        } catch (SQLException e) {
            throw new MappingException(e, e.getMessage());
        }
    }

    /**
     *
     * @param anObject
     * @param conn
     * @throws MappingException
     */
    public void update(Task anObject, Connection conn) throws MappingException {
        long tstart = System.currentTimeMillis();
        try {
            if (conn == null) {
                LOGGER.severe("can`t connect to oracle");
            }
            this.updateImpl(conn, anObject);
        } catch (SQLException e) {
            throw new  MappingException(e, e.getMessage());
        } catch (MappingException e) {
            throw new  MappingException(e, e.getMessage());
        }
        Long loadTime = System.currentTimeMillis()-tstart;
        LOGGER.warning("*** TaskMapper.update() time "+loadTime);
    }

    @Override
    protected List<Task> findAllImpl(Connection conn) throws SQLException, MappingException {
        throw new NotImplementedException();
    }

    @Override
    protected void insertImpl(Connection conn, Task anObject) throws SQLException, MappingException {
        this.createImpl(conn, anObject);
    }

    public Task renewTask(Task anObject) throws DuplicateKeyException, MappingException {
        Connection conn;
        try {
            conn = getConn();
            // создать пустую запись (только id, номер процессаПУП, номер заявки, parent и номер версии)
            Task task = anObject;
            String insertPipelineQuery = null;

            String updateMdTaskQuery = "UPDATE MDTASK M SET M.ID_PUP_PROCESS = ?, M.PARENTID = ? WHERE M.ID_MDTASK = ?";
            PreparedStatement stmn = conn.prepareStatement(updateMdTaskQuery);
            try {
                stmn.setObject(1, task.getId_pup_process());
                stmn.setObject(2, task.getParent());
                stmn.setObject(3, task.getId_task());
                LOGGER.info("execute query " + updateMdTaskQuery);
                stmn.executeUpdate();
            } finally {
                stmn.close();
            }

            insertPipelineQuery = "UPDATE PIPELINE P SET P.VTB_CONTRACTOR = ?,P.CONTRACTOR=? WHERE P.ID_MDTASK = ?";

            stmn = conn.prepareStatement(insertPipelineQuery);
            try {
                stmn.setObject(1, task.getMain().getIssuingBank());
                stmn.setObject(2, task.getMain().getIssuingBank());
                stmn.setObject(3, task.getId_task());
                stmn.executeUpdate();
                LOGGER.info("execute query " + insertPipelineQuery);
            } finally {
                stmn.close();
            }

            //updateImpl(conn, task);

            return task;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
            throw new MappingException(e.getMessage());
        }
    }

    @Override
	public Task createImpl(Connection conn, Task domainObject) throws SQLException,
			MappingException {
		// создать пустую запись (только id, номер процессаПУП, номер заявки, parent и номер версии)
		Task task = domainObject;
		Long version = task.getHeader().getVersion();
		String insertPipelineQuery = null;

        if (version==null) {
            // предполагая, что новая версия с инкрементным номер - версия СПО, устанавливаем ограничение
            // t.ID_PUP_PROCESS is not null
            PreparedStatement stmn = conn.prepareStatement("SELECT MAX(t.version)+1 ver FROM mdtask t "
                    + "WHERE t.mdtask_number=? and t.ID_PUP_PROCESS is not null");
            stmn.setObject(1, task.getHeader().getNumber());
            ResultSet r = stmn.executeQuery();
            while (r.next()) {
                version = r.getLong("ver");
            }
            if(version==null || version.equals(0L)) version = 1L;
        }
        String CMD_INSERT = "INSERT INTO mdtask (ID_MDTASK, ID_PUP_PROCESS,MDTASK_NUMBER,"
                + "PARENTID,VERSION) VALUES (MDTASK_SEQ.nextval,?,?,?,?)";
        PreparedStatement stmn = conn.prepareStatement(CMD_INSERT, new String[]{"ID_MDTASK"});
        try {
            stmn.setObject(1, task.getId_pup_process());
            stmn.setObject(2, task.getHeader().getNumber());
            stmn.setObject(3, task.getParent());
            stmn.setObject(4, version);
            LOGGER.info("execute query " + CMD_INSERT);
            int i = stmn.executeUpdate();
            if (i > 0) {
                ResultSet rs = stmn.getGeneratedKeys();
                if (rs.next())
                    task.setId_task(rs.getLong(1));
            }
        }
        finally {
            stmn.close();
        }

        insertPipelineQuery = "INSERT INTO PIPELINE(VTB_CONTRACTOR, ID_MDTASK, CONTRACTOR) VALUES(?, ?, ?)";

        stmn = conn.prepareStatement(insertPipelineQuery);
        try {
            stmn.setObject(1, task.getMain().getIssuingBank());
            stmn.setObject(3, task.getMain().getIssuingBank());
            stmn.setObject(2, task.getId_task());
            stmn.executeUpdate();
            LOGGER.info("execute query " + insertPipelineQuery);
        } finally {
            stmn.close();
        }

		return task;
	}

    private void loadExtendText(Connection conn, Task task) throws SQLException {
        PreparedStatement p = conn.prepareStatement(SELECT_EXTEND_TEXT);
        ResultSet r = null;
        try {
            p.setLong(1, task.getId_task());
            r = p.executeQuery();
            while (r.next()) {
                ExtendText text = new ExtendText();
                text.setId(r.getLong("id"));
                text.setDescription(r.getString("description"));
                text.setContext(r.getString("context"));
                text.setIdApprovedAuthor(r.getLong("id_approved_author"));
                text.setSignature(r.getString("signature"));
                text.setSignature(r.getString("approved_signature"));
                text.setCreateDate(r.getDate("create_date"));
                text.setApprovedDate(r.getDate("approved_date"));
                text.setValidTo(r.getDate("validto"));
                task.getExtendTexts().add(text);
            }
        } finally {
            if (r != null)
                r.close();
            p.close();
        }
    }

    @Override
    protected void removeImpl(Connection conn, Task domainObject) throws SQLException, MappingException {
    	throw new NotImplementedException();
    }

    /**
     * Добавление текстов большой длины
     *
     * @param conn
     * @param task
     * @throws SQLException
     */
    public void insertExtendText(Connection conn, Task task) throws SQLException {
        PreparedStatement p = conn.prepareStatement(INSERT_EXTEND_TEXT);
        try {
            for (ExtendText et : task.getExtendTexts()) {
                if (et != null)
                    if (et.getContext() != null) {
                        p.setLong(1, task.getId_task());
                        p.setString(2, et.getDescriptionWithPrefix());
                        p.setString(3, et.getContext());
                        p.setObject(4, et.getIdAuthor());
                        p.setString(5, et.getSignature());
                        p.setObject(6, et.getIdApprovedAuthor());
                        p.setString(7, et.getApprovedSignature());
                        p.setDate(8, et.getCreateDate());
                        p.setDate(9, et.getApprovedDate());
                        p.setDate(10, et.getValidTo());
                        int count = p.executeUpdate();
                        if (count != 1)
                            throw new SQLException("Добавлено " + count + " записей по sql: " + INSERT_EXTEND_TEXT);
                    }
            }
        } finally {
            p.close();
        }
    }

    /**
     * Удаление текстов больщой длины
     *
     * @param conn
     * @param task
     * @throws SQLException
     */
    public void deleteExtendText(Connection conn, Task task) throws SQLException {
        LOGGER.info("deleteExtendText");
        PreparedStatement p = conn.prepareStatement(DELETE_EXTEND_TEXT);
        try {
            p.setLong(1, task.getId_task());
            p.execute();
        } finally {
            p.close();
        }

    }

    /**
     * Найти заявку по номеру задачи ПУП
     * @param pupProcessID
     *            номер задачи ПУП
     * @return taskid
     * @throws SQLException
     * @throws MappingException
     */
    @Override
    public Long findByPupID(Long pupProcessID) throws MappingException, SQLException {
        Connection conn = getConn();
        if (conn == null)
            LOGGER.severe("can`t connect to oracle");
        try{
            PreparedStatement stmn = conn.prepareStatement("select ID_MDTASK from "
                    + " mdtask where ID_PUP_PROCESS=?");
            stmn.setObject(1, pupProcessID);
            ResultSet rs = stmn.executeQuery();
            Long task = null;
            if (rs.next()) {
                task = rs.getLong("ID_MDTASK");
            }
            rs.close();
            stmn.close();
            conn.close();
            return task;
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException("Ошибка поиска заявки по pupid");
        }
    }

    /**
     * Найти заявку по номеру задачи
     *
     * @param task_number
     *            номер задачи
     * @return taskid
     * @throws SQLException
     * @throws MappingException
     */
    public Long findByNumber(Long task_number, Long parentid) throws MappingException, SQLException {
        Connection conn = getConn();
        return findByNumberImpl(task_number,parentid,conn);
    }
    private Long findByNumberImpl(Long task_number, Long parentid,Connection conn) throws MappingException, SQLException {
    	if (conn == null)
            LOGGER.severe("can`t connect to oracle");
        PreparedStatement stmn;
        String sql = "select m1.ID_MDTASK from mdtask m1 "
        		+ " left outer join mdtask m2 on m1.MDTASK_NUMBER = m2.MDTASK_NUMBER and m1.VERSION < m2.VERSION "
        		+ " where m1.MDTASK_NUMBER=? and m2.ID_MDTASK is null ";
        if (parentid == null) {
            stmn = conn.prepareStatement(sql);
            stmn.setObject(1, task_number);
        } else {
            stmn = conn.prepareStatement(sql + " and m1.PARENTID=?");
            stmn.setObject(1, task_number);
            stmn.setObject(2, parentid);
        }
        ResultSet rs = stmn.executeQuery();
        Long task = null;
        if (rs.next()) {
            task = rs.getLong("ID_MDTASK");
        }
        stmn.close();
        return task;
    }

    /**
     * Найти заявки по номеру родительской задачи
     *
     * @param pupProcessID
     *            номер задачи
     * @param all вернуть все заявки, даже удаленные
     * @throws SQLException
     * @throws MappingException
     */
    public ArrayList<Long> findTaskByParent(Long mdtaskid, boolean all) throws MappingException, SQLException {
        Connection conn = getConn();
        if (conn == null) LOGGER.severe("can`t connect to oracle");
        String query = "select ID_MDTASK,DELETED from mdtask where PARENTID=? ";
        if(!all) query += " and DELETED like 'N'";
        query += " order by ID_MDTASK";
        LOGGER.info("findTaskByParent "+query);
        PreparedStatement stmn = conn.prepareStatement(query);
        stmn.setObject(1, mdtaskid);
        ResultSet rs = stmn.executeQuery();
        ArrayList<Long> tasks = new ArrayList<Long>();
        while (rs.next()) {
            if(all || rs.getString("DELETED").equals("N"))
                tasks.add(rs.getLong("ID_MDTASK"));
        }
        stmn.close();
        return tasks;
    }

    /**
     * Ищет назначенных пользователей для конкретного этапа конкретной заявки
     * @return HashMap список id,email пользователей
     */
    public HashMap<Long,String> findAssignUser(Long idStage, Long idProcess)
        throws MappingException,  SQLException{
        //--есть процесс и стадия. Какие пользователи назначены на эту стадию?
        Connection conn = getConn();
        if (conn == null)
            LOGGER.severe("can`t connect to oracle");
        String query="select u.all_emails,u.id_user from  " +" assign a"
                    +" inner join  " +" process_events pe on pe.id_process_event=a.id_process_event"
                    +" inner join  " +" user_in_role ur on ur.id_role=a.id_role  and lower(ur.status)='y'"
                    +" inner join  " +" stages_in_role sr on sr.id_role=a.id_role"
                    +" inner join  " +" users u on u.id_user=a.id_user_to and u.is_active=1"
                    +" where pe.id_process=? and a.id_user_to=ur.id_user"
                    +" and sr.id_stage=? and u.mail_user is not null";
        PreparedStatement stmn = conn.prepareStatement(query);
        stmn.setObject(1, idProcess);
        stmn.setObject(2, idStage);
        ResultSet rs = stmn.executeQuery();
        HashMap<Long,String> users = new HashMap<Long,String>();
        while (rs.next()) {
            users.put(rs.getLong("id_user"), rs.getString("all_emails"));
        }
        stmn.close();
        return users;
    }

    /**
     * Ищет пользователей для конкретного этапа конкретной заявки, у которых есть права взять её в работу
     * только в указанном подразделении
     * @return HashMap список id,email пользователей
     */
    public HashMap<Long,String> findUser(Long idStage, Long idDepartament)
        throws MappingException,  SQLException {
        HashMap<Long,String> users = new HashMap<Long,String>();
        Connection conn = getConn();
    	if (idDepartament==null)return users;
    	String query="select distinct u.id_user,u.all_emails from  users u  "+
        	"inner join user_in_role ur on ur.id_user=u.id_user "+
        	"inner join stages_in_role sr on sr.id_role=ur.id_role "+
        	"where sr.id_stage=? and lower(ur.status) = 'y' "+
        	"and u.id_department=? and u.mail_user is not null "
        	+ " and exists (select 1 from role_nodes rn where rn.role_parent=ur.id_role)";
    	PreparedStatement stmn = conn.prepareStatement(query);
    	stmn.setObject(1, idStage);
        stmn.setObject(2, idDepartament);
        ResultSet rs = stmn.executeQuery();
        while (rs.next()) {
            users.put(rs.getLong("id_user"), rs.getString("all_emails"));
        }
        stmn.close();
        return users;
    }

    @Override
    protected Task findByPrimaryKeyImpl(Connection conn, Task domainObjectWithKeyValues) throws SQLException, MappingException {
        return this.findByPrimaryKeyImpl(conn, domainObjectWithKeyValues, true);
    }

    @Override
    public boolean isCRMLimitLoaded(String crmid) throws MappingException, SQLException {
        Connection conn = getConn();
        if (conn == null)
            LOGGER.severe("can`t connect to oracle");
        PreparedStatement stmn = conn.prepareStatement("select crmid from mdtask where trim(crmid)=?");
        stmn.setObject(1,crmid.trim());
        ResultSet rs = stmn.executeQuery();
        if(rs.next()) {stmn.close();return true;}
        stmn.close();return false;
    }

    @Override
    public Long findByCRMid(String crmid) throws MappingException, SQLException {
        Connection conn = getConn();
        if (conn == null)
            LOGGER.severe("can`t connect to oracle");
        PreparedStatement stmn = conn.prepareStatement("select id_mdtask from mdtask where trim(crmid)=?");
        stmn.setObject(1,crmid.trim());
        ResultSet rs = stmn.executeQuery();
        if(rs.next()) {return rs.getLong("id_mdtask");}
        throw new MappingException("Нет такой заявки в спо с CRMID="+crmid);
    }

    @Override
    public List<Long> findChildrenOfCRMid(String crmid) throws MappingException, SQLException {
        List<Long> result = new ArrayList<Long>();
        if ((crmid == null) || (crmid.equals(""))) return result;

        Connection conn = getConn();
        if (conn == null) LOGGER.severe("can`t connect to oracle");
        PreparedStatement stmn = conn.prepareStatement("select id_mdtask from mdtask where trim(crminlimit)=?");
        stmn.setObject(1,crmid.trim());
        ResultSet rs = stmn.executeQuery();

        while (rs.next()) result.add(rs.getLong("id_mdtask"));
        stmn.close();
        return result;
    }

    @Override
    public byte[] getResolution(Long id_template,Long id_mdtask) {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            PreparedStatement stmn = conn.prepareStatement("select r.doc_report from cc_report r where r.id_template=? and r.id_question=?");
            stmn.setObject(1,id_template);
            stmn.setObject(2,id_mdtask);
            ResultSet rs = stmn.executeQuery();
            if(rs.next()) {
                return rs.getBytes("doc_report");
            } else {
                //прочитать инфу у клона
                stmn = conn.prepareStatement("select id_mdtask from mdtask where id_clone = ?");
                stmn.setObject(1,id_mdtask);
                rs = stmn.executeQuery();
                while (rs.next()) {
                    return getResolution(id_template, rs.getLong("id_mdtask"));
                }
            }
            stmn.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getXSLT(Integer templateid) {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            PreparedStatement stmn = conn.prepareStatement("select rt.template_data from "
                    +" report_template rt where rt.id_template="+templateid.toString());
            ResultSet rs = stmn.executeQuery();
            if(rs.next())
            {
                return rs.getString("template_data");
            }
            stmn.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isPermissionEdit(long idStage, String varname,Integer idTypeProcess)
            throws MappingException {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            PreparedStatement stmn = conn.prepareStatement("select v.name_var,sp.id_stage,sp.id_permission from  "
                    +" variables v inner join  "
                    +" stages_permissions sp on sp.id_var=v.id_var "
                    +"where v.id_type_process=? and v.name_var=? and sp.id_permission=3 and sp.id_stage=?");
            stmn.setObject(1, idTypeProcess);
            stmn.setObject(2, varname);
            stmn.setObject(3, idStage);
            ResultSet rs = stmn.executeQuery();
            if(rs.next())
            {
                stmn.close();
                return true;
            }
            stmn.close();
            return false;
        } catch (SQLException e) {
            throw new MappingException(e.getMessage());
        }
    }

    @Override
    public Page findRefusableTask(Long userid, Long start,Long count, ProcessSearchParam sp) throws ModelException {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            int totalCount=0;
            String query="  from mdtask mt " +
            		"inner join processes p on mt.ID_PUP_PROCESS=p.ID_PROCESS  " +
            		"left outer join DEPARTMENTS d on d.ID_DEPARTMENT=mt.INITDEPARTMENT " +
            		"where exists (select 1 from tasks t where t.id_status<3 and mt.id_pup_process=t.id_process) " +
            		"and (exists (select 1 from manager m where m.id_start_department is null and m.id_user=? and mt.id_mdtask=m.id_mdtask) " +
            		"and not exists (select 1 from roles r where r.id_type_process=p.id_type_process and r.name_role in ('Структуратор')) " +
            		"or exists (select 1 from assign a inner join roles r on r.id_role=a.id_role " +
            		"inner join process_events e on e.id_process_event=a.id_process_event " +
            		"where e.id_process=mt.id_pup_process and a.id_user_to=? and r.name_role in ('Структуратор','Структуратор (за МО)','Руководитель структуратора (за МО)','Руководитель структуратора')))";
          //фильтр
            if(sp.parseError){
            	return Page.EMPTY_PAGE;
            }
            if (sp.getNumber()!=null&&sp.getNumber().length()>0){
            	try{
            		new Integer(sp.getNumber());
            		query += " and (MDTASK_NUMBER="+sp.getNumber()+" or CRMCODE like '%"+sp.getNumber()+"%')";
            	}catch(Exception e){
            		query += " and CRMCODE like '%"+sp.getNumber()+"%'";
            	}
            }
            if (sp.getProcessTypeID()!=null)
            	query += " and p.ID_TYPE_PROCESS="+sp.getProcessTypeID().toString();
            if (sp.getCurrency()!=null)
            	query += " and lower(mt.CURRENCY)='"+sp.getCurrency().toLowerCase()+"'";
            if (sp.getSumFrom()!=null)
            	query += " and mt.MDTASK_SUM>="+sp.getSumFrom().toString();
            if (sp.getSumTo()!=null)
            	query += " and mt.MDTASK_SUM<="+sp.getSumTo().toString();
            if (sp.getContractor()!=null)
            	query += " and exists (select 1 from r_org_mdtask rom " +
            			"where rom.id_mdtask=mt.id_mdtask and rom.id_crmorg like'"
            	+sp.getContractor()+"') ";
            if(sp.getType()!=null)
            	query += " and exists (select 1 from  variables  v inner join " +
            	" attributes a on a.id_var=v.id_var where v.name_var='Тип кредитной заявки' and a.id_process=p.id_process and lower(a.value_var) like '%"+
            	sp.getType().toLowerCase()+"%')";
            if(sp.getStatus()!=null)
            	query += " and exists (select 1 from  variables  v inner join " +
            	" attributes a on a.id_var=v.id_var where v.name_var='Статус' and a.id_process=p.id_process and lower(a.value_var) like '%"+
            	sp.getStatus().toLowerCase()+"%')";
            if(sp.getPriority()!=null)
            	query += " and exists (select 1 from  variables  v inner join " +
            	" attributes a on a.id_var=v.id_var where v.name_var='Приоритет' and a.id_process=p.id_process and lower(a.value_var) like '%"+
            	sp.getPriority().toLowerCase()+"%')";
            if(sp.getInitDepartment()!=null)
            	query += " and lower(d.SHORTNAME) like '%"+sp.getInitDepartment().toLowerCase()+"%'";
            //считаем количество
            PreparedStatement stmn = conn.prepareStatement("select count(mt.id_mdtask)"+query);
            stmn.setObject(1, userid);
            stmn.setObject(2, userid);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                totalCount=rs.getInt(1);
            }
            stmn.close();
            rs.close();
            if(totalCount==0) return Page.EMPTY_PAGE;
            //сортировка
            query += " order by mt.id_pup_process desc";
            query = "select * from ( select /*+ FIRST_ROWS(n) */ "+
                "a.*, ROWNUM rnum from (select mt.id_mdtask"+query+") a "+
                "where ROWNUM <= "+Long.toString(start+count-1)+" ) where rnum  >= "+Long.toString(start);
            LOGGER.info(query);
            stmn = conn.prepareStatement(query);
            stmn.setObject(1, userid);
            stmn.setObject(2, userid);
            rs = stmn.executeQuery();
            ArrayList<Long> res = new ArrayList<Long>();
            while (rs.next()) {
                res.add(rs.getLong("id_mdtask"));
            }
            stmn.close();
            rs.close();
            Page returnPage = new Page(res, start.intValue(), (start.intValue() + res.size()) < totalCount);
            returnPage.setTotalCount(totalCount);
            return returnPage;
    } catch (SQLException e) {
        e.printStackTrace();
        LOGGER.severe(e.getMessage());
        return Page.EMPTY_PAGE;
    }
    }

    @Override
    public ArrayList<Long> findAffiliatedUsers(Long mdtaskid)
            throws ModelException {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            String query="select distinct u.id_user from users u where u.id_user in ("+
                    " select t.id_user from tasks t inner join mdtask mt on mt.id_pup_process=t.id_process"+
                    " where mt.id_mdtask=? and t.id_status < 3"+
                    " union select m.id_user from manager m where m.id_mdtask=?)";
            PreparedStatement stmn = conn.prepareStatement(query);
            stmn.setObject(1, mdtaskid);
            stmn.setObject(2, mdtaskid);
            ResultSet rs = stmn.executeQuery();
            ArrayList<Long> res = new ArrayList<Long>();
            while (rs.next()) {
                res.add(rs.getLong("id_user"));
            }
            rs.close();
            stmn.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ArrayList<Long>();
        }
    }
    @Override
    public ArrayList<Task> findTaskByOrganisation(String orgid, int startRow,
            int count) throws ModelException {
        try {
            Connection conn = getConn();
            String query="select * from ( select a.*, rownum r from (SELECT t.ID_MDTASK,t.MDTASK_NUMBER,t.CRMCODE,t.MDTASK_SUM,t.CURRENCY,t.EXCHANGE_RATE,t.PERIOD "+
                "FROM r_org_mdtask r INNER JOIN mdtask t ON t.id_mdtask = r.id_mdtask "+
                "WHERE r.id_crmorg = ? AND t.id_pup_process IS NOT NULL ORDER BY id_mdtask DESC"+
                ") a where rownum < ? ) where r > ?";
            PreparedStatement stmn = conn.prepareStatement(query);
            stmn.setObject(1, orgid);
            stmn.setObject(2, startRow+count+1);
            stmn.setObject(3, startRow);
            ResultSet rs = stmn.executeQuery();
            ArrayList<Task> res = new ArrayList<Task>();
            while (rs.next()) {
                Task task = new Task(rs.getLong("ID_MDTASK"));
                task.getHeader().setNumber(rs.getLong("MDTASK_NUMBER"));
                task.getHeader().setCrmcode(rs.getString("CRMCODE"));
                task.getMain().setCurrency(new Currency(rs.getString("CURRENCY")));
                task.getMain().setExchangeRate(rs.getBigDecimal("EXCHANGE_RATE"));
                task.getMain().setPeriod(rs.getInt("PERIOD"));
                res.add(task);
            }
            rs.close();
            stmn.close();
            conn.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ArrayList<Task>();
        }
    }

    @Override
    public ArrayList<Long> findTaskByOrganisation(String orgid)
            throws ModelException {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            String query="select r.id_mdtask from r_org_mdtask r inner join mdtask t on t.id_mdtask=r.id_mdtask " +
            		"where r.id_crmorg=? and t.id_pup_process is not null order by id_mdtask desc";
            PreparedStatement stmn = conn.prepareStatement(query);
            stmn.setObject(1, orgid);
            ResultSet rs = stmn.executeQuery();
            ArrayList<Long> res = new ArrayList<Long>();
            while (rs.next()) {
                res.add(rs.getLong("id_mdtask"));
            }
            rs.close();
            stmn.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ArrayList<Long>();
        }
    }

    @Override
    public void createVersion(Long idmdtask, TaskVersion version) {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            PreparedStatement stmn = conn.prepareStatement(
                    "insert into MDTASKVERSION (ROLE,USERNAME,VERSIONDATE,STAGE,"+
                    "VERSIONID,ID_MDTASK,REPORT) values(?,?,?,?,MDTASKVERSION_SEQ.nextval,?,?)");
            stmn.setObject(1, version.getRole());
            stmn.setObject(2, version.getUserName());
            stmn.setObject(3, new java.sql.Timestamp(version.getDate().getTime()));
            stmn.setObject(4, version.getStage());
            stmn.setObject(5, idmdtask);
            stmn.setObject(6, version.getReport());
            stmn.executeUpdate();
            stmn.close();
            conn.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<TaskVersion> findTaskVersion(Long mdtaskid) {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            ArrayList<TaskVersion> res = new ArrayList<TaskVersion>();
            PreparedStatement stmn = conn.prepareStatement(
                    "SELECT ROLE,  USERNAME,  VERSIONDATE,  STAGE,  VERSIONID FROM MDTASKVERSION where ID_MDTASK=? order by VERSIONID");
            stmn.setObject(1, mdtaskid);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                res.add(new TaskVersion(rs.getString("ROLE"),
                        rs.getString("USERNAME"),
                        rs.getLong("VERSIONID"),
                        rs.getDate("VERSIONDATE"),
                        rs.getString("STAGE")));
            }
            rs.close();
            stmn.close();
            return res;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return new ArrayList<TaskVersion>();
        }
    }

    @Override
    public String getVersion(Long idversion) {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            String res = "";
            PreparedStatement stmn = conn.prepareStatement(
                    "SELECT REPORT FROM MDTASKVERSION where VERSIONID=?");
            stmn.setObject(1, idversion);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                res = rs.getString("REPORT");
            }
            rs.close();
            stmn.close();
            return res;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Task4Rating getTask4Rating(Long mdtaskid) {
        Task4Rating r = new Task4Rating();
        ArrayList<Supply4Rating> supply4RaingList = new ArrayList<Supply4Rating>();
        BigDecimal exchangeRate = null;

        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            PreparedStatement stmn = conn.prepareStatement(
                    "SELECT t.ID_MDTASK,t.MDTASK_NUMBER,t.CRMCODE,t.MDTASK_SUM,t.CURRENCY,t.EXCHANGE_RATE,t.PERIOD, " +
                    "t.ID_OPERATIONTYPE,pr.PR.IS_FIXED "+
                    "FROM MDTASK t LEFT OUTER JOIN PROCENT pr ON pr.ID_MDTASK = t.ID_MDTASK WHERE T.ID_MDTASK = ?");
            stmn.setObject(1, mdtaskid);
            ResultSet rs = stmn.executeQuery();
            if (rs.next()) {
                r.setIdMdTask(mdtaskid);
                exchangeRate = rs.getBigDecimal("EXCHANGE_RATE");
                r.setOperationTypeCode(rs.getLong("ID_OPERATIONTYPE"));
                r.setRateType(rs.getString("IS_FIXED")==null || rs.getString("IS_FIXED").equalsIgnoreCase("y"));
                r.setPeriod(rs.getInt("PERIOD"));
                BigDecimal sum = rs.getBigDecimal("MDTASK_SUM");
                if (rs.getString("CURRENCY")!=null && !rs.getString("CURRENCY").equalsIgnoreCase("RUR"))
                        sum = sum.multiply(exchangeRate==null?BigDecimal.valueOf(0):exchangeRate);
                r.setSum(sum);
                r.setNumberDisplay(String.valueOf(rs.getLong("MDTASK_NUMBER")));
                String crmcode = rs.getString("CRMCODE");
                if (crmcode != null && !crmcode.equals("") && !r.getNumberDisplay().equals(crmcode))
                    r.setNumberDisplay(crmcode + " (" + r.getNumberDisplay() + ")");
                rs.close();
                stmn.close();
            } else {
            	rs.close();
            	stmn.close();
            	return null;
            }
            //обеспечение
            stmn = conn.prepareStatement(
                    "select * from ( "+
                        "SELECT ID_MDTASK,'d' as stype,ZALOG as sum,'RUR' as currency,'n' as FULLSUM,ID_OB_KIND,LIQUIDITY_LEVEL,DEPOSITOR_FIN_STATUS FROM DEPOSIT "+
                        "union all "+
                        "SELECT ID_MDTASK,'g' as stype,SUM,CURRENCY,'n' as FULLSUM,ID_OB_KIND,LIQUIDITY_LEVEL,DEPOSITOR_FIN_STATUS FROM GARANT "+
                        "union all "+
                        "SELECT ID_MDTASK,'w' as stype,WSUM as sum,CURRENCY,FULLSUM,ID_OB_KIND,LIQUIDITY_LEVEL,DEPOSITOR_FIN_STATUS FROM WARRANTY) "+
                        "WHERE ID_MDTASK = ?");
            stmn.setObject(1, mdtaskid);
            rs = stmn.executeQuery();
            while (rs.next()) {
                Supply4Rating sr = new Supply4Rating();
                sr.setTypeCode(rs.getString("stype"));
                if (rs.getString("FULLSUM")!=null && rs.getString("FULLSUM").equalsIgnoreCase("y")){
                    sr.setSum(r.getSum());
                } else {
                    sr.setSum(rs.getBigDecimal("sum"));
                    if(rs.getString("currency")!=null && !rs.getString("currency").equalsIgnoreCase("RUR") && exchangeRate!=null && r.getSum()!=null)
                        sr.setSum(r.getSum().multiply(exchangeRate));
                }
                sr.setDepositorFinStatusCode(rs.getLong("DEPOSITOR_FIN_STATUS"));
                sr.setSupplyTypeCode(rs.getLong("ID_OB_KIND"));
                sr.setLiquidityLevelCode(rs.getLong("LIQUIDITY_LEVEL"));
                supply4RaingList.add(sr);
            }
            r.setSupplyList(supply4RaingList);
            rs.close();
            stmn.close();
            conn.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        return r;
    }

    public Connection getConn() throws SQLException {
        //if(true) return getDataSource().getConnection();//Debug
        try{
            if (connection==null||connection.isClosed()){
                connection = getDataSource().getConnection();
            }
        }catch(Exception e){
            connection = getDataSource().getConnection();
        }
        return connection;
    }
    private Connection connection = null;

    @Override
    public HashMap<Long, Long> getProcessAssign(Long id_pup_process) {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
             HashMap<Long, Long> res = new LinkedHashMap<Long, Long>();
            PreparedStatement stmn = conn.prepareStatement(
                    "select a.id_user_to,a.id_role from assign a " +
                    "inner join process_events pe on a.id_process_event= pe.id_process_event " +
                    "where pe.id_process=?");
            stmn.setObject(1, id_pup_process);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                res.put(rs.getLong("id_role"), rs.getLong("id_user_to"));
            }
            rs.close();
            stmn.close();
            return res;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return new LinkedHashMap<Long, Long>();
        }
    }

    @Override
    public HashMap<Long, String> getRoles2Assign(Long idUser, Long idTask) {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
             HashMap<Long, String> res = new LinkedHashMap<Long, String>();
            PreparedStatement stmn = conn.prepareStatement(
                    "select ur.id_role, r.name_role "+
                    "from user_in_role ur inner join roles r on r.id_role=ur.id_role "+
                    "where ur.id_user=? and lower(ur.status) = 'y' and ur.id_role in  "+
                    "(select sr.id_role from stages_in_role sr inner join tasks t on t.id_stage_to=sr.id_stage "+
                    "where t.id_task=?)");
            stmn.setObject(1, idUser);
            stmn.setObject(2, idTask);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                res.put(rs.getLong("id_role"), rs.getString("name_role"));
            }
            rs.close();
            stmn.close();
            return res;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return new LinkedHashMap<Long, String>();
        }
    }

    @Override
    public void updateAttribute(long idProcess, String nameVar, String valueVar) {
        try {
           PreparedStatement st = getConn().prepareStatement("update attributes a set a.value_var=? " +
                   "where a.id_var in (select v.id_var from variables v where v.name_var =?) "+
                   "and a.id_process=?");
           st.setObject(1, valueVar);
           st.setObject(2, nameVar);
           st.setObject(3, idProcess);
           st.executeUpdate();
           st.close();
           connection.close();
       }  catch (Exception e) {
           LOGGER.log(Level.SEVERE, e.getMessage(), e);
           e.printStackTrace();
       }
    }

    @Override
    public String getAttributeValue(Long idProcess, String nameVar) {
        try {
            Connection conn = getConn();
            if (conn == null)
                LOGGER.severe("can`t connect to oracle");
            String res="";
            PreparedStatement stmn = conn.prepareStatement(
                    "select a.value_var from variables v inner join attributes a on a.id_var=v.id_var "+
                    "where v.name_var like ? and a.id_process=?");
            stmn.setObject(1, nameVar);
            stmn.setObject(2, idProcess);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                res = rs.getString("value_var");
            }
            rs.close();
            stmn.close();
            conn.close();
            return res;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void updateCCStatus(ru.masterdm.integration.CCStatus status, Long mdtaskid) throws SQLException {
        LOGGER.log(Level.INFO, "call TaskMapper.updateCCStatus mdtaskid="+mdtaskid);
        try {
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("delete from md_cc_cache where id_mdtask=?");
            st.setObject(1, mdtaskid);
            st.executeUpdate();
            st.close();
            st = conn.prepareStatement("insert into md_cc_cache (cc_cache_date, cc_cache_protocol,cc_cache_statusid,id_mdtask,id_report) "+
                    "values(?,?,?,?,?)");
            if (status.getMeetingDate() == null) {
                st.setNull(1, Types.TIMESTAMP);
            } else {
                st.setDate(1, new Date(status.getMeetingDate().getTime()));
            }
            st.setObject(3, status.getStatus());
            st.setObject(2, status.getProtocol());
            st.setObject(4, mdtaskid);
            st.setObject(5, status.getId_report());
            st.executeUpdate();
            st.close();
            //если очищаем секцию, то статус заявки не сбрасываеи. Если метод вызывается из КК, то статус заявки обновляем
            if (status.getStatus()==null || status.getStatus().equals(""))
                return;
            st = conn.prepareStatement("update attributes a set a.value_var=(select s.resolution_status from cc_resolution_status s where s.id_resolution_status=?) "+
                    "where a.id_var in (select v.id_var from variables v where v.name_var like 'Статус') "+
                    "and a.id_process in (select t.id_pup_process from mdtask t where t.id_mdtask=?)");
            st.setObject(1, status.getStatus());
            st.setObject(2,mdtaskid);
            st.executeUpdate();
            st.close();
        }  catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    public ru.masterdm.integration.CCStatus getStatus(Long mdtaskid) throws Exception {
        try {
        	ru.masterdm.integration.CCStatus status = new ru.masterdm.integration.CCStatus();
            Connection conn = getConn();
            PreparedStatement stmn = conn.prepareStatement(
                    "select c.cc_cache_date,c.cc_cache_protocol,c.cc_cache_statusid,c.id_report from md_cc_cache c where c.id_mdtask=?");
            stmn.setObject(1, mdtaskid);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                status.setId_report(rs.getLong("id_report"));
                status.setMeetingDate(rs.getDate("cc_cache_date"));
                status.setProtocol(rs.getString("cc_cache_protocol"));
                status.setStatus(rs.getLong("cc_cache_statusid"));
            }
            rs.close();
            stmn.close();
            return status;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    public TaskMapper(Connection c) {
        super();
        this.connection=c;
    }

    public TaskMapper() {
        super();
        this.connection=null;
    }

    @Override
    public ArrayList<ProjectTeamMember> readProjectTeam(Task task, String type) {
    	try {
            Connection conn = getConn();
            if (conn == null) LOGGER.severe("can`t connect to oracle");
            return readProjectTeam(conn, task, type);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
            return new ArrayList<ProjectTeamMember>();
        }
    }

    private ArrayList<ProjectTeamMember> readProjectTeam(Connection conn, Task task, String type) {
    	ArrayList<ProjectTeamMember> res = new ArrayList<ProjectTeamMember>();
    	try {
        	final String sql =
        		"select  u.surname||' '|| u.name||' '||u.patronymic as fio, h.path_short as department, r.name_role as role, "
        		+ " u.id_user as id_user, u.id_department as id_department "
        		+ " from project_team m "
        		+ " inner join mdtask md on m.id_mdtask = md.id_mdtask and m.id_mdtask = ? and m.teamtype = 'p'  "
        		+ " inner join processes p on p.id_process = md.id_pup_process  "
        		+ " left outer join users u on u.id_user=m.id_user "
        		+ " left outer join departments_hierarchy h on u.id_department = h.id_department "
        		+ " inner join user_in_role ur on m.id_user = ur.id_user and ur.status = 'Y' "
        		+ " inner join roles r on ur.id_role = r.id_role and r.id_type_process = p.id_type_process and r.active = 1 "

        		+ " left outer join "
        		+ "  ( select count(*) as cnt, a.id_user_to, a.id_role, pe.id_process  "
        		+ " from assign a inner join process_events pe on pe.id_process_event=a.id_process_event "
        		+ " group by a.id_user_to, a.id_role, pe.id_process "
        		+ " order by a.id_user_to, a.id_role, pe.id_process "
        		+ " ) asg on asg.id_role = r.id_role and asg.id_user_to = ur.id_user and asg.id_process = md.id_pup_process "
        		+ " where lower(r.name_role) like lower(?)  "
        		+ " and (asg.cnt is not null and asg.cnt > 0) ";

        	PreparedStatement st = conn.prepareStatement(sql);
            st.setObject(1, task.getId_task());
            st.setObject(2, type);
            ResultSet r = st.executeQuery();

            while (r.next()) {
            	res.add(
            		new ProjectTeamMember(r.getString("fio"), r.getString("department"), r.getString("role"),
                    		              r.getLong("id_user"), r.getLong("id_department"))
                );
            }
            r.close();
            st.close();

            return res;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
            return res;
        }
    }

    @Override
    public ArrayList<TaskProduct> readProductTypes(Task task) {
    	try {
            Connection conn = getConn();
            if (conn == null) LOGGER.severe("can`t connect to oracle");
    		return TaskMapperReadHelper.readProductTypes(conn, task);
    	} catch (Exception e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
            return new ArrayList<TaskProduct>();
        }
    }

    @Override
    public void saveProductTypes(Task task) {
    	try {
            Connection conn = getConn();
            if (conn == null) LOGGER.severe("can`t connect to oracle");
    		TaskMapperSaveHelper.saveProductTypes(conn, task);
    	} catch (Exception e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
        }
    }

    @Override
    public void saveCurrencyList(Task task) {
    	try {
            Connection conn = getConn();
            if (conn == null) LOGGER.severe("can`t connect to oracle");
    		TaskMapperSaveHelper.saveCurrencyList(conn, task);
    	} catch (Exception e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
        }
    }

    @Override
    public void saveSpecialOtherConditions(Task task) {
    	try {
            Connection conn = getConn();
            if (conn == null) LOGGER.severe("can`t connect to oracle");
    		TaskMapperSaveHelper.saveSpecialOtherConditions(conn, task);
    	} catch (Exception e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
        }
    }

    @Override
    public void saveParameters(Task task) {
    	try {
            Connection conn = getConn();
            if (conn == null) LOGGER.severe("can`t connect to oracle");
    		TaskMapperSaveHelper.saveParameters(conn, task);
    	} catch (Exception e) {
            LOGGER.log(Level.WARNING, "SQL Exception ", e);
        }
    }

}