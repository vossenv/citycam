package com.dm.citycam.citycam.data.entity;


import com.dm.citycam.citycam.search.fieldbridge.LocalDateFieldBridge;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
import org.apache.lucene.analysis.standard.ClassicTokenizerFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.hibernate.search.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@AnalyzerDef(
        name = "customanalyzer",
        tokenizer = @TokenizerDef(factory = ClassicTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = StopFilterFactory.class),
                @TokenFilterDef(factory = EnglishPossessiveFilterFactory.class),
                @TokenFilterDef(factory = SynonymFilterFactory.class, params = {
                        @Parameter(name = "synonyms", value = "synonyms.txt"),
                        @Parameter(name = "ignoreCase", value = "true")})
        }
)
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Analyzer(definition = "customanalyzer")
public abstract class EntityBase<ID> {

    @CreatedDate
    @Field(analyze = Analyze.NO)
//    @FieldBridge(impl = LocalDateFieldBridge.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedDate
    @Field(analyze = Analyze.NO)
    //@FieldBridge(impl = LocalDateFieldBridge.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_modified_date", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @Field
    @Column(name = "enabled", nullable = false, columnDefinition = "TINYINT default 1")
    private Boolean enabled = true;

    public ID getId() {
        throw new NotImplementedException();
    }

}




//    @Id
//    @Type(type = "uuid-char")
//    @GeneratedValue(generator = "UUID", strategy = GenerationType.AUTO)
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "org.hibernate.id.UUIDGenerator"
//    )
//    private String id;
//