package com.dm.citycam.citycam.data.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class EntityBase<ID> {

//    @Id
//    @Type(type = "uuid-char")
//    @GeneratedValue(generator = "UUID", strategy = GenerationType.AUTO)
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "org.hibernate.id.UUIDGenerator"
//    )
//    private String id;
//
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date", nullable = false,  updatable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_modified_date", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @Column(name = "enabled", nullable = false, columnDefinition = "TINYINT default 1")
    private Boolean enabled = true;

    public ID getId(){
        throw new NotImplementedException();
    }

}