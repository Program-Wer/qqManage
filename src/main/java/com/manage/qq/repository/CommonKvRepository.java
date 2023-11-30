package com.manage.qq.repository;

import com.manage.qq.dao.json.CommonKvDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonKvRepository extends JpaRepository<CommonKvDAO, String> {
}
