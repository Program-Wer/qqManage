package com.manage.qq.dao.json;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "quick_command")
@Data
public class QuickCommandDAO {
    @Id
    private String name;

    @Lob
    @Column(columnDefinition = "CLOB", length = 10000)
    private String command;
}
