package ru.md.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ru.md.domain.User;

/**
 *
 * @author Andrey Pavlenko
 *
 */
public interface UserMapper {
	User getUserByLogin(String login);
	User getUserById(Long id);
	List<String> userAssignedAs(@Param("idUser") Long idUser, @Param("idProcess") Long idProcess);
	List<Long> userAssigned(@Param("idProcess") Long idProcess);
	List<String> userRoles(@Param("idUser") Long idUser, @Param("idTypeProcess") Long idTypeProcess);
	List<String> userAllRoles(@Param("idUser") Long idUser);
	List<String> userCpsRoles(@Param("idUser") Long idUser);

	/**
	 * Возвращает список идентификаторов получателей при отправки на акцепт изменения процентной ставки.
	 * @param currentUserId идентификатор текущего пользователя
	 * @param mdTaskId идентификатор заявки
	 * @return список идентификаторов получателей
	 */
	List<Long> getRecipientsOnInterestRateToAccept(@Param("currentUserId") Long currentUserId,
	                                               @Param("mdTaskId") Long mdTaskId);

	/**
     * Возвращает список идентификаторов получателей при отправки на доработку изменения процентной ставки.
     * @param currentUserId идентификатор текущего пользователя
     * @param mdTaskId идентификатор заявки
     * @return список идентификаторов получателей
     */
    List<Long> getRecipientsOnInterestRateReturn(@Param("currentUserId") Long currentUserId,
                                                 @Param("mdTaskId") Long mdTaskId);

    /**
     * Возвращает список идентификаторов получателей при акцепте изменения процентной ставки.
     * @param currentUserId идентификатор текущего пользователя
     * @param mdTaskId идентификатор заявки
     * @return список идентификаторов получателей
     */
    List<Long> getRecipientsOnInterestRateAccepted(@Param("currentUserId") Long currentUserId,
                                                   @Param("mdTaskId") Long mdTaskId);

	List<String> userRolesStage(@Param("idUser") Long idUser, @Param("idStage") Long idStage);

	List<String> getProjectTeamAssignedAs(@Param("mdTaskId") Long mdTaskId, @Param("roleName") String roleName);

	boolean isUserInRoleName(@Param("idUser") long idUser, @Param("roleNames") String ... roleNames);
}
