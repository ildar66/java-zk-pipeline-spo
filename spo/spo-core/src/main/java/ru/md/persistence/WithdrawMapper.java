package ru.md.persistence;

import java.util.List;
import ru.md.domain.Withdraw;

/**
 * 
 * @author Andrey Pavlenko
 *
 */
public interface WithdrawMapper {
	List<Withdraw> findByMdtask(Long mdtask);
	List<Withdraw> findByTrance(Long tranceid);
}
