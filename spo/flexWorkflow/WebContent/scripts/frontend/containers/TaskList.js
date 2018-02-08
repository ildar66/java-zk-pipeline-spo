import React from 'react';

const TaskNumber = (props) => {
        const { task } = props;
        if (task.userInProjectTeam)
            return (
                <td>
                    <a href={'form.jsp?mdtaskid='+task.idMdtask+'&from=CreateApplication'}
                       target="_blank">{task.mdtaskNumber} вер. {task.version}</a>
                </td>
            );
        return (
            <td>
                <a onClick={() => openProjectTeamDialog(task.idMdtask)} href="javascript:;">{task.mdtaskNumber} вер. {task.version}</a>
            </td>
        );
};
const TaskRow = (props) => {
        var task = props.task;
        return (
            <tr className={(props.index %2) > 0 ?'b':'a'} key={task.idMdtask}>
                <TaskNumber task={task} />
                <td>{task.ekname}</td>
                <td>{task.ekgroup}</td>
                <td className="numbertd">{task.mdtaskSum} {task.currency}</td>
                <td>{task.period} {task.periodDimension}</td>
                <td>{task.mapStatus}</td>
                <td>{task.initdep}</td>
            </tr>
        );
};
const TaskList = (props) => {
        const tasks = props.tasks;
        const trTemplate = tasks.map(function(item, index) {
            return (
                <TaskRow task={item} index={index} key={index+'TaskRow'}/>
            )
        });
        return (
            <tbody>
            {trTemplate}
            </tbody>
        );
};

export default TaskList