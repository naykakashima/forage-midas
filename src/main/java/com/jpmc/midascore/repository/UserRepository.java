package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserRecord, Long> {
    UserRecord findById(long id);
    UserRecord getBalanceById(long id);
    UserRecord getNameById(long id);
    Boolean existsById(long id);
    Balance getBalanceObjectById(long id);
}

