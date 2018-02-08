import React from 'react';
import { connect } from 'react-redux'

import PagerState from './containers/Pager'
import TaskList from './containers/TaskList'

const Header =() => (
    <thead>
    <tr>
        <th>№, версия</th>
        <th>Контрагент</th>
        <th>Группа компаний</th>
        <th>Сумма, валюта</th>
        <th>Срок</th>
        <th>Статус</th>
        <th>Инициирующее подразделение</th>
    </tr>
    </thead>
);

class App extends React.Component {
    render() {
        return (
            <div>
                <ul className="nav nav-tabs">
                    <li className={this.props.tab == 'kz' ?'active':''}><a href="#" onClick={() => this.props.onTabClick('kz')}>По контрагенту</a></li>
                    <li className={this.props.tab == 'group' ?'active':''}><a href="#" onClick={() => this.props.onTabClick('group')}>По Группе Компаний</a></li>
                </ul>
                <PagerState />
                <table className="regular">
                    <Header />
                    <TaskList tasks={this.props.tasks} />
                </table>
                <div className="Copyright">Время формирования страницы (секунд): {this.props.loadTime}</div>
            </div>
        );
    }
};

const AppTimed = connect(
    (state) => {
        return {
            loadTime: state.loadTime,
            tasks: state.tasks,
            tab: state.tab
        }
    },
    (dispatch) => {
        return {
            onTabClick: (code) => {
                dispatch({type: 'SET_TAB',payload: code});
            }
        }
    }
)(App);

export default AppTimed