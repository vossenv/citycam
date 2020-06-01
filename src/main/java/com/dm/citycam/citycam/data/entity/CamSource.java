package com.dm.citycam.citycam.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.bridge.builtin.DoubleBridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Indexed
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cam_source")
@SuppressWarnings("JpaDataSourceORMInspection")
public class CamSource extends EntityBase<String> {

    @Id
    @NotEmpty
    @NotNull
    @Column(name = "id", nullable = false)
    private String id;

    @Field
    @Column(name = "url", columnDefinition = "VARCHAR(500)")
    private String url;

    @Field
    @Column(name = "title", columnDefinition = "VARCHAR(500)")
    private String title;

    @Field
    @Column(name = "alive", columnDefinition = "TINYINT")
    private Boolean alive = true;

    @Field
    @Column(name = "description", columnDefinition = "VARCHAR(500)")
    private String description;

    @Field
    @Column(name = "location", columnDefinition = "VARCHAR(500)")
    private String location;

    @Field
    @FieldBridge(impl = DoubleBridge.class)
    @Column(name = "latitude", columnDefinition = "DECIMAL(10,5)")
    private double latitude;

    @Field
    @FieldBridge(impl = DoubleBridge.class)
    @Column(name = "longitude", columnDefinition = "DECIMAL(10,5)")
    private double longitude;

}
