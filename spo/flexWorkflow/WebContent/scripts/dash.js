/**
 * Created by Andrey Pavlenko.
 * Список заявок для отчёта dashboard
 */
var Pager = React.createClass({
    render: function() {
        const { total, currPage,isFetching } = this.props;
        const last = Math.floor((total-1)/10)+1;
        let back,next = '';
        if (currPage > 1)
            back = <span><PageLink currPage={currPage} page="1" txt="в начало" />
                    <PageLink currPage={currPage} page={currPage-1} txt="назад" /></span>;
        if (currPage < last)
            next = <span><PageLink currPage={currPage} page={currPage+1} txt="вперед" />
                <PageLink currPage={currPage} page={last} txt="в конец" /></span>;
        if (isFetching) {
            return <div className="paging">идёт загрузка...</div>
        }
        let numbers = [];
        if (currPage > 2) numbers.push(currPage-2);
        if (currPage > 1) numbers.push(currPage-1);
        numbers.push(currPage);
        if (currPage < last) numbers.push(currPage+1);
        if (currPage < last -1) numbers.push(currPage+2);
        const numbersTemplate = numbers.map(function(item) {
            return (
                <PageLink currPage={currPage} page={item} txt={item} />
            )
        })
        return (
            <div className="paging">
                Всего: {total}.
                {back}
                {numbersTemplate}
                {next}
            </div>
        );
    }
});
var PageLink = React.createClass({
    render: function() {
        const { page, currPage,txt } = this.props;
        if (page == currPage)
            return <b>{txt}</b>
        return (
            <a href="#" onClick={() => store.dispatch(setPage(page))}>{txt}</a>
        );
    }
});
var Header = React.createClass({
    getInitialState: function() {
        if ($('#initFilter').val() == '')
            return {searchNumber: '',searchSumTo:'',searchSumFrom:''};
        let initFilter = JSON.parse($('#initFilter').val());
        //console.log(initFilter.searchCurrency);
        setTimeout("$('#searchCurrency').val('"+initFilter.searchCurrency+"')",50);
        setTimeout("$('#searchPriority').val('"+initFilter.searchPriority+"')",50);
        setTimeout("$('#searchProcessType').val('"+initFilter.searchProcessType+"')",50);
        setTimeout("$('#searchContractor').val('"+initFilter.searchContractor+"')",50);
        $.post('ajax/orgname.html',{id: initFilter.searchContractor},
            function(data){$('#selectedName').val(data);});
        return initFilter;
    },
    searchNumberChange: function(event) {
        this.setState({searchNumber: event.target.value.replace(/\D+/,'')});
    },
    clearOnClick: function() {
        this.setState({searchNumber: '',searchSumTo:'',searchSumFrom:'',searchStatus:'',searchType:'',searchInitDepartment:''},
            () => {
                $('#searchCurrency').val('');$('#searchPriority').val('');$('#searchProcessType').val('');
                $('#searchContractor').val('');$('#selectedName').val('');
                store.dispatch(setPage(1));
            })
    },
    orgOnClick: function() {
        var wnd = window.open("popup_org.jsp?ek=only&mode=inprocess&fieldNames=SPOcontractorID|selectedName|searchContractor", "organizationLookupList", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");
        wnd.focus();
        return false;
    },
    render: function() {
        const priorityOptions = ['высокий','средний','низкий'].map(function(item) {
            return (
                <option  value={item}>{item}</option>
            )
        });
        const currencyOptions = currencyList.map(function(item) {
            return (
                <option  value={item}>{item}</option>
            )
        });
        const bpOptions = processTypeList.map(function(item) {
            return (
                <option  value={item.id}>{item.name}</option>
            )
        });
        return (
            <thead>
            <tr>
                <th>поиск</th>
                <th>№</th>
                <th>Версия</th>
                <th>Контрагент</th>
                <th>Группа компаний</th>
                <th>Сумма</th>
                <th title="Валюта">Вал.</th>
                <th title="Тип заявки">Тип</th>
                <th title="Приоритет">Приор.</th>
                <th>Статус</th>
                <th>Инициирующее подразделение</th>
                <th>Тип процесса</th>
            </tr>
            <tr id="fullSearchForm">
                <td>
                    <button className="button" onClick={() => store.dispatch(setPage(1))}>найти</button>
                    <button className="button clear"  onClick={this.clearOnClick}>очистить</button>
                </td>
                <td><input id="searchNumber" type="text" value={this.state.searchNumber} onChange={this.searchNumberChange} /></td>
                <td></td>
                <td><input id="searchContractor" type="hidden" />
                    <input type="hidden" id="SPOcontractorID" />
                    <input onClick={this.orgOnClick} type="text" class="text" readonly="true" id="selectedName" />
                </td>
                <td></td>
                <td>
                    от<input id="searchSumFrom" type="text" value={this.state.searchSumFrom}
                             onChange={(event) => this.setState({searchSumFrom: event.target.value.replace(/\D+/,'')})} />
                    <br />до<input id="searchSumTo" type="text" value={this.state.searchSumTo}
                             onChange={(event) => this.setState({searchSumTo: event.target.value.replace(/\D+/,'')})} /></td>
                <td><select id="searchCurrency">
                    <option value=""></option>
                    {currencyOptions}
                </select></td>
                <td><input id="searchType" type="text" value={this.state.searchType} onChange={(event) => this.setState({searchType: event.target.value})} />
                </td>
                <td><select id="searchPriority">
                    <option value=""></option>
                    {priorityOptions}
                </select
                ></td>
                <td><input id="searchStatus" type="text" value={this.state.searchStatus} onChange={(event) => this.setState({searchStatus: event.target.value})} /><br />
                </td>
                <td><input id="searchInitDepartment" type="text" value={this.state.searchInitDepartment} onChange={(event) => this.setState({searchInitDepartment: event.target.value})} /></td>
                <td><select id="searchProcessType">
                    <option value=""></option>
                    {bpOptions}
                </select></td>
            </tr>
            </thead>
        );
    }
});
var TaskList = React.createClass({
    render: function() {
        const tasks = this.props.tasks;
        const trTemplate = tasks.map(function(item, index) {
            return (
                <TaskRow task={item} index={index} />
            )
        })
        return (
            <tbody>
                {trTemplate}
            </tbody>
        );
    }
});
var TaskRow = React.createClass({
    render: function() {
        var task = this.props.task;
        const url = '/ProdflexWorkflow/form.jsp?mdtaskid='+task.idMdtask+'&viewtype=all&dash=true'+
            '&from='+$('#from_param').val()+'&to='+$('#to_param').val()+
            ($('#taskType').val()=='' ? ('&statusid='+$('#statusids').val()) : ('&taskType='+$('#taskType').val()) )+
            '&listParam='+stringifyListParam()+'&filter='+stringifyFilter()+'&page='+$('#page').val();
        return (
            <tr className={(this.props.index %2) > 0 ?'b':'a'} key={task.idMdtask}>
                <td className="linker">
                    <a target="_blank" href={"rprt.do?rp=as&id="+task.pupid+"&mdtaskId="+task.idMdtask} title="Посмотреть активные операции"
                    ><img src="style/in_progress.png" alt="Активные операции" /></a>
                    <a href={"rprt.do?rp=hr&p="+task.pupid} target="_blank"
                       title="Посмотреть хронологию выполнения операций по этой заявке"><img src="style/time.png" alt="хронология" /></a>
                    <a target="_blank" href={"rprt.do?rp=sh&id="+task.pupid} title="Посмотреть путь заявки по всем операциям"><img src="style/shema.png" alt="схема" /></a>
                </td>
                <td>
                    <a href={url} title="Посмотреть заявку">{task.number}</a>
                </td>
                <td align="center">{task.version}</td>
                <td>{task.ekname}</td>
                <td>{task.ekgroup}</td>
                <td className="numbertd">{task.mdtaskSum}</td>
                <td>{task.currency}</td>
                <td>{task.tasktype}</td>
                <td>{task.priority}</td>
                <td>{task.status}</td>
                <td>{task.initdep}</td>
                <td>{task.processname}</td>
            </tr>
        );
    }
});
var App = React.createClass({
    render: function() {
        const state = store.getState();
        return (
            <div>
                <Pager total={state.total} currPage={state.currPage} isFetching={state.isFetching} />
                <table className="regular">
                    <Header />
                    <TaskList tasks={state.tasks} />
                </table>
                <div className="Copyright">Время формирования страницы (секунд): {state.loadTime}</div>
            </div>
        );
    }
});
const initialState = {
    tasks: [],
    total: 0,
    currPage: 0,
    isFetching: false,
    loadTime: 0
};
function stringifyListParam() {
    return JSON.stringify({creditDocumentary:$('#creditDocumentary').val(),branch:$('#branch').val(),
        isTradingDeskOthers: $('#isTradingDeskOthers').val(),
        tradingDeskSelected: $('#tradingDeskSelected').val(),departments: $('#departments').val()});
}
function stringifyFilter() {
    let filter = {searchCurrency: $('#searchCurrency').val(),searchType: $('#searchType').val(),searchStatus: $('#searchStatus').val(),
        searchInitDepartment: $('#searchInitDepartment').val(), searchPriority: $('#searchPriority').val(),searchProcessType: $('#searchProcessType').val(),
        searchContractor: $('#searchContractor').val()};
    if($('#searchNumber').val() != '') filter.searchNumber = $('#searchNumber').val();
    if($('#searchSumFrom').val() != '') filter.searchSumFrom = $('#searchSumFrom').val();
    if($('#searchSumTo').val() != '') filter.searchSumTo = $('#searchSumTo').val();
    return JSON.stringify(filter);
}
function pageReducer(state = initialState, action) {
    switch (action.type) {
        case 'GET_PAGE_SUCCESS':
            const payload = JSON.parse(action.payload);
            $('#page').val(payload.currPage);
            return {...state, total: payload.total, currPage: payload.currPage, tasks: payload.tasks,
                isFetching: false,loadTime:payload.loadTime}
        case 'SET_PAGE':
            //отправить запрос
            $.post('dash_list.html',
                {statusids:$('#statusids').val(),page: action.payload, from:$('#from_param').val(), to:$('#to_param').val(),
                    filter:stringifyFilter(),listParam:stringifyListParam()
                },
                (ans) => store.dispatch({
                    type: 'GET_PAGE_SUCCESS',
                    payload: ans
                }));
            return { ...state, currPage: action.payload, isFetching: true}
        default:
            return state
    }
}
function setPage(pagenum) {
    return {
        type: 'SET_PAGE',
        payload: pagenum
    }
}
//init store
const store = Redux.createStore( pageReducer);
var Provider = ReactRedux.Provider;
const render = () => ReactDOM.render(
        <App />,
    document.getElementById('root')
);
render();
store.subscribe(render);
//store.dispatch(setPage($('#initPage').val()));
setTimeout(() => store.dispatch(setPage($('#initPage').val())), 200);