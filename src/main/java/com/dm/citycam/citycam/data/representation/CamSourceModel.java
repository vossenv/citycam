package com.dm.citycam.citycam.data.representation;

import com.dm.citycam.citycam.data.entity.CamSource;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Data
@Setter
@Getter
@NoArgsConstructor
public class CamSourceModel extends RepresentationModel<CamSourceModel> {


    CamSource camSource;

    CamSourceModel(CamSource e) {
        this.camSource = e;
    }

}
