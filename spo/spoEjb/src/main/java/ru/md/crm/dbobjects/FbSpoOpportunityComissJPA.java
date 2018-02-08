package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="FB_SPO_OPPORTUNITY_COMISS",schema="sysdba")
@Deprecated//не нужно использовать этот класс. Вместо него используйте jdbc операции. 
public class FbSpoOpportunityComissJPA implements Serializable {
        private static final long serialVersionUID = 1L;
        @Id
        @Column(name="OPPORTUNITY_COMISSID")
        private String id;//id очереди
        private String SOURCE_NAME;
        private String COMISS_NAME;
        private BigDecimal COMISS_VALUE;
        private String COMISS_UNIT;
        private String COMISS_BASE;
        private String COMISS_PERIODICHNOST;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getSOURCE_NAME() {
            return SOURCE_NAME;
        }
        public void setSOURCE_NAME(String sOURCE_NAME) {
            SOURCE_NAME = sOURCE_NAME;
        }
        public String getCOMISS_NAME() {
            return COMISS_NAME;
        }
        public void setCOMISS_NAME(String cOMISS_NAME) {
            COMISS_NAME = cOMISS_NAME;
        }
        public BigDecimal getCOMISS_VALUE() {
            return COMISS_VALUE;
        }
        public void setCOMISS_VALUE(BigDecimal cOMISS_VALUE) {
            COMISS_VALUE = cOMISS_VALUE;
        }
        public String getCOMISS_UNIT() {
            return COMISS_UNIT;
        }
        public void setCOMISS_UNIT(String cOMISS_UNIT) {
            COMISS_UNIT = cOMISS_UNIT;
        }
        public String getCOMISS_BASE() {
            return COMISS_BASE;
        }
        public void setCOMISS_BASE(String cOMISS_BASE) {
            COMISS_BASE = cOMISS_BASE;
        }
        public String getCOMISS_PERIODICHNOST() {
            return COMISS_PERIODICHNOST;
        }
        public void setCOMISS_PERIODICHNOST(String cOMISS_PERIODICHNOST) {
            COMISS_PERIODICHNOST = cOMISS_PERIODICHNOST;
        }
        
}
