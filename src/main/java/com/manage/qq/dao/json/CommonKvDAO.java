package com.manage.qq.dao.json;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "common_kv")
@Data
public class CommonKvDAO {
    @Id
    private String key;

    @Lob
    @Column(columnDefinition = "CLOB", length = 10000)
    private String value;
}
