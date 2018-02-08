/**
 * Created by Andrey Pavlenko.
 * Секция Решение уполномоченного органа
 */
var QuestionTable = React.createClass({
    addOnClick: function() {
        store.dispatch({type: 'ADD_QUESTION'});
        return false;
    },
    render: function() {
        const question = this.props.question;
        const trTemplate = question.map(function(item, index) {
            return (
                <QuestionRow question={item} index={index} />
            )
        });
        const addBtn = conclusionReadOnly() ? '' : <a className="button" id="conclusion_add" onClick={this.addOnClick} href="javascript:;">Добавить</a>;
        return (
            <div>
                {trTemplate}
                {addBtn}
            </div>
        );
    }
});
var CcQuestionType = React.createClass({
    render: function() {
        const qType = this.props.qType;
        if (conclusionReadOnly()) {
            const qTypeName = questionTypeList.map( (item, index) => {
                if(item.id == qType) return item.name;
                else return '';
            });
            return <span>{qTypeName}</span>;
        }
        const ccQuestionTypeOptions = questionTypeList.map(function(item) {
            return (
                <option value={item.id} selected={item.id==qType}>{item.name}</option>
            )
        });
        return (
            <select name="CcQuestionType" onchange="fieldChanged();">
                <option value=""></option>
                {ccQuestionTypeOptions}
            </select>
        );
    }
});
var CcPkr = React.createClass({
    render: function() {
        const pkr = this.props.pkr;
        if (conclusionReadOnly()) {
            const pkrName = pkrList.map( (item, index) => {
                if(item.id == pkr) return item.name;
                else return '';
            });
            return <span>{pkrName}</span>;
        }
        const options = pkrList.map(function(item) {
            return (
                <option value={item.id} selected={item.id==pkr}>{item.name}</option>
            )
        });
        return (
            <select name="creditDecisionProject" onchange="fieldChanged();">
                <option value=""></option>
                {options}
            </select>
        );
    }
});
var QuestionRow = React.createClass({
    delOnClick: function() {
        store.dispatch({type: 'DEL_QUESTION',payload:this.props.question.id});
        return false;
    },
    onChange: function() {
        conclusionButtons();fieldChanged();updateRelatedData();
        return false;
    },
    render: function() {
        const question = this.props.question;
        const assigneeAuthorityOptions = allowedCommittees.map(function(item) {
            return (
                <option value={item.id} selected={item.id==question.idDep}>{item.nominativeCaseName}</option>
            )
        });
        let delButton = conclusionReadOnly() ? '' :<a onClick={this.delOnClick} href="javascript:;"><img src="theme/img/minus.png" /></a>
        if (question.id==$('#mdtaskid').val()) delButton = '';
        return (
            <table className="regular leftPadd" style={{width: 99+'%'}}><tbody>
                <tr>
                    <th style={{width: 50+'%'}}>Уполномоченный орган {delButton}</th>
                    <td style={{width: 50+'%'}}>
                        <select name="assigneeAuthority" onChange={this.onChange} style={{width: 100+'%'}}
                                disabled={conclusionReadOnly()}>
                            <option value=""> </option>
                            {assigneeAuthorityOptions}
                        </select>
                        <input type="hidden" name="question_id" value={question.id} />
                        <span className="assigneeAuthoritySpan">assigneeAuthoritySpan</span>
                    </td>
                </tr>
                <tr>
                    <th>Дата заседания Комитета</th>
                    <td></td>
                </tr>
                <tr>
                    <th>Классификация вопроса для УО</th>
                    <td><CcQuestionType qType={question.ccQuestionType} /></td>
                </tr>
                <tr>
                    <th>Проект кредитного решения</th>
                    <td><CcPkr pkr={question.pkr} /></td>
                </tr>
                <tr>
                    <th>Статус</th>
                    <td>{question.status}</td>
                </tr>
                <tr>
                    <th>Номер протокола</th>
                    <td>{question.protocol}<br /><div className="CC_Resolution">{question.resolution}</div></td>
                </tr>
            </tbody></table>
        );
    }
});
const initialState = {
    question: questionInit
};
function conclusionReadOnly() {
    return $('#section_conclusion').size() == 0
}
function pageReducer(state = initialState, action) {
    switch (action.type) {
        case 'DEL_QUESTION':
            setTimeout(conclusionButtons,200);
            return { ...state, question: state.question.filter((item) => item.id != action.payload)};
        case 'ADD_QUESTION':
            const newQ = {id:'newquestion'+getNextId()};
            setTimeout(conclusionButtons,200);
            return { ...state, question: state.question.concat(newQ)};
        default:
            return state
    }
}
//init store
const store = Redux.createStore(pageReducer);
var Provider = ReactRedux.Provider;
const render = () => ReactDOM.render(
        <QuestionTable question={store.getState().question} />,
    document.getElementById('conclusion_root')
);
render();
store.subscribe(render);
conclusionButtons();