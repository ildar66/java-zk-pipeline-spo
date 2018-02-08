import React, { PropTypes } from 'react';
import { connect } from 'react-redux'

const PageLinkPassive = (props) => {
        const { page, currPage,txt,onPageClick } = props;
        if (page == currPage)
            return <b>{txt}</b>;
        return (
            <a href="#" onClick={() => onPageClick(page)}>{txt}</a>
        );
};
/*PageLinkPassive.propTypes = {
    onPageClick: PropTypes.func.isRequired,
    currPage: PropTypes.string.isRequired,
    page: PropTypes.string.isRequired,
    txt: PropTypes.string.isRequired
};*/
const PageLink = connect(
    (state) => {return {}},
    (dispatch) => {
        return {
            onPageClick: (page) => {
                dispatch({type: 'SET_PAGE',payload: page});
            }
        }
    }
)(PageLinkPassive);
const Pager = (props) => {
        const { total, currPage,isFetching } = props;
        const last = Math.floor((total-1)/10)+1;
        let back,next = '';
        if (currPage > 1)
            back = <span><PageLink currPage={currPage} page="1" txt="в начало" key="1e" />
                    <PageLink currPage={currPage} page={currPage-1} txt="назад"  key="back"/></span>;
        if (currPage < last)
            next = <span><PageLink currPage={currPage} page={currPage+1} txt="вперед" key="next" />
                <PageLink currPage={currPage} page={last} txt="в конец"  key="last"/></span>;
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
                <PageLink currPage={currPage} page={item} txt={item} key={item+'key'} />
            )
        });
        return (
            <div className="paging">
                Всего: {total}.
                {back}
                {numbersTemplate}
                {next}
            </div>
        );
};
Pager.propTypes = {
    isFetching: PropTypes.bool.isRequired,
    currPage: PropTypes.number.isRequired,
    total: PropTypes.number.isRequired
};
const PagerState = connect(
    (state) => {
        return {
            total: state.total,
            currPage: state.currPage,
            isFetching: state.isFetching
        }
    }
)(Pager);

export default PagerState