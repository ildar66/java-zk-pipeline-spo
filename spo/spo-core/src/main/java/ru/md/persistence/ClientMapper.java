package ru.md.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.ClientInfo;
import ru.md.domain.ClientInfoLimit;
import ru.md.domain.Decision;

/**
 * @author Andrey Pavlenko
 */
public interface ClientMapper {
	List<String> getDealStatusList();
	List<String> getDecisionMakerList();
	List<String> getBodydecisionList();

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	void saveClientInfo(@Param("id") String id, @Param("info") ClientInfo info);
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	void updateDecision(@Param("decision") Decision decision);
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	void updateDecisionData(@Param("decision") Decision decision);

	ClientInfo getClientInfo(@Param("id") String id);
	Decision getDocDecision(@Param("id") Long id);
	List<String> getDecisionBody(@Param("id") Long id);
	Decision getDocData(@Param("id") Long id);
}
