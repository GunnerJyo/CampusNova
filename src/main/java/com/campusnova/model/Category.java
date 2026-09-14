package com.campusnova.model;
import javax.persistence.*;
@Entity public class Category { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(unique=true,nullable=false) private String name; private String icon;
 public Category(){} public Category(String n,String i){name=n;icon=i;} public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;} public String getIcon(){return icon;} public void setIcon(String i){icon=i;} }
