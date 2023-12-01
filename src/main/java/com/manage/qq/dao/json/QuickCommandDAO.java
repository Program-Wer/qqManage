package com.manage.qq.dao.json;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "quick_command")
@Data
public class QuickCommandDAO {
    @Id
    private String name;
    private String command;
}
