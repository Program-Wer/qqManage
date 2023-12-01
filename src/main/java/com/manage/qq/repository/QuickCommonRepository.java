package com.manage.qq.repository;

import com.manage.qq.dao.json.QuickCommandDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuickCommonRepository extends JpaRepository<QuickCommandDAO, String> {
}
