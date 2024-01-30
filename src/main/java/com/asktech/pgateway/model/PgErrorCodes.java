package com.asktech.pgateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "pgName_index", columnList = "pgName"),
        @Index(name = "pgStatusCode_index", columnList = "pgStatusCode")
})
public class PgErrorCodes extends AbstractTimeStampAndId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "pg_id")
    private String pgId;
    private String pgName;
    private String pgStatusCode;
    private String pgStatusDetails;
    private String pgStatusNextStep;
    private String responseStatus;
}
