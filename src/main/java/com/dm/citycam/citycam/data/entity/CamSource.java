package com.dm.citycam.citycam.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "camera_source")
@SuppressWarnings("JpaDataSourceORMInspection")
public class CamSource extends EntityBase<String> {


    @NotNull
    @Column(name = "url", columnDefinition = "VARCHAR(500)")
    private String url;

    @NotNull
    @Column(name = "title", columnDefinition = "VARCHAR(500)")
    private String title;

    @NotNull
    @Column(name = "alive")
    private Boolean alive = true;

    @Column(name = "description", columnDefinition = "VARCHAR(500)")
    private String description;

    @Column(name = "location", columnDefinition = "VARCHAR(500)")
    private String location;

    @Column(name = "latitude", columnDefinition = "DECIMAL(10,5)")
    private double latitude;

    @Column(name = "longitude", columnDefinition = "DECIMAL(10,5)")
    private double longitude;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(name = "last_ping")
//    private LocalDateTime lastPing = LocalDateTime.now();


//    @JsonCreator
//    public CamSource(
//            @JsonProperty("url") String url,
//            @JsonProperty("title") String title,
//            @JsonProperty("coordinates") String coordinates,
//            @JsonProperty("alive") String alive
////            @JsonProperty("last_ping") String lastPing
//    ) {
//        this.url = url;
//        this.title = title;
//        this.coordinates = coordinates;
//        this.alive = Boolean.parseBoolean(alive);
////        this.lastPing = LocalDateTime.parse(lastPing,
////                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//    }


}
