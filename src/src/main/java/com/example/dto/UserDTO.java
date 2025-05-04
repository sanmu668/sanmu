package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.UserTransaction;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Base64;

/**
 * @author wzs
 * @date 2025年05月01日 0:23
 */
@Data
public class UserDTO {
    private Integer id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] photo;

    private String photoBase64;
    private String username;
    private String email;
    private String number;
    private LocalDateTime createdAt;

    public UserDTO(Integer id,byte[] photo,String username,String email,String number,LocalDateTime createdAt){
        this.id=id;
        this.photo=photo;
        this.username=username;
        this.email=email;
        this.number=number;
        this.createdAt=createdAt;

        //转换photo为Base64
        if (photo != null){
            this.photoBase64 = Base64.getEncoder().encodeToString(photo);
        }
    }

    public UserDTO() {

    }
}
