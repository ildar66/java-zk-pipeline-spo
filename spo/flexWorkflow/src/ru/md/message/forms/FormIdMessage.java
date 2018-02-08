package ru.md.message.forms;

import org.apache.struts.action.ActionForm;

/**
 * Форма для просмотра одного сообщения
 * @author Какунин Константин Юрьевич
 *
 */
public class FormIdMessage extends ActionForm {

    private static final long serialVersionUID = 1L;

    private Long idMessage;

    public Long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }
}
