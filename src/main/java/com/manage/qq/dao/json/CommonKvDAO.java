package com.manage.qq.dao.json;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "common_kv")
@Data
public class CommonKvDAO {
    @Id
    private String key;
    private String value;
}
