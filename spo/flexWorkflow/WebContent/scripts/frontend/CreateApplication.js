/**
 * Created by Andrey Pavlenko.
 * Область "Совпадения" на форме создания заявок
 */
import React from 'react';
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { createStore } from 'redux'

import AppTimed from './app'
//import pageReducer from './reducers'

const initialState = {
    tasks: [],
    total: 0,
    currPage: 0,
    isFetching: false,
    loadTime: 0,
    tab: 'kz'
};
export default function pageReducer(state = initialState, action) {
    switch (action.type) {
        case 'GET_PAGE_SUCCESS':
            const payload = JSON.parse(action.payload);
            return {...state, total: payload.total, currPage: payload.page, tasks: payload.tasks,
                isFetching: false,loadTime:payload.loadTime}
        case 'SET_PAGE':
            //console.log('SET_PAGE '+action.payload);
            $.post('ajax/create_app_task_list.html',
                {page: action.payload, orgid:$('#CRMcontractorID').val(), type:$('#limitSelect').val(),tab:state.tab},
                (ans) => store.dispatch({
                    type: 'GET_PAGE_SUCCESS',
                    payload: ans
                }));
            return { ...state, currPage: action.payload, isFetching: true}
        case 'SET_TAB':
            $.post('ajax/create_app_task_list.html',
                {page: 1, orgid:$('#CRMcontractorID').val(), type:$('#limitSelect').val(),tab:action.payload},
                (ans) => store.dispatch({
                    type: 'GET_PAGE_SUCCESS',
                    payload: ans
                }));
            return { ...state, tab: action.payload, currPage: 1, isFetching: true}
        default:
            return state;
    }
}

let store = createStore(pageReducer);
const drawMatchDiv = () => {
    render(
        <Provider store={store}>
            <AppTimed />
        </Provider>,
        document.getElementById('matchDivContent')
    );
    store.dispatch({type: 'SET_PAGE',payload: 1});
    //store.dispatch({type: 'SET_PAGE_GROUP',payload: 1});
};
window.drawMatchDiv = drawMatchDiv;
