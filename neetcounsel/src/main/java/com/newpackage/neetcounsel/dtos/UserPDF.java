package com.newpackage.neetcounsel.dtos;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
//import lombok.NoArgsConstructor;


@Document(collection = "user_pdfs")
@Data

//@AllArgsConstructor
@RequiredArgsConstructor
public class UserPDF {
    @Id
    private String id;
    private final String userID;
    private final String filename;
    private final byte[] file;

}