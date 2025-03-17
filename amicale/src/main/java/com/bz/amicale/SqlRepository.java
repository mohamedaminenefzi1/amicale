package com.bz.amicale;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SqlRepository extends JpaRepository<Sql, Long> {
    List<Sql> findByNameContaining(String name);
}
