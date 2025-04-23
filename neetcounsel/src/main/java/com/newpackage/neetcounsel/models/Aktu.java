package com.newpackage.neetcounsel.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "aktu")
@Data
@NoArgsConstructor
public class Aktu {
    @Id
    private String id;
    @Field("Institute")
    private String Institute;
    @Field("Program")
    private String Program;
    @Field("Round")
    private String Round;
    @Field("Quota")
    private String Quota;
    @Field("Category")
    private String Category;
    @Field("Gender")
    private String Gender;
    @Field("OpeningRank")
    private Integer OpeningRank;
    @Field("ClosingRank")
    private Integer ClosingRank;

    // Getters and Setters
}
