package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fb_spo_log_status",schema="sysdba")
public class FbSpoLogStatusJPA implements Serializable, Comparable<FbSpoLogStatusJPA> {
        private static final long serialVersionUID = 1L;
        private String CRMID;
        private Long STATUS;
        private String LOG;
        @Id
        private Date ERR_DATE;
        /**
         * @return cRMID
         */
        public String getCRMID() {
            return CRMID;
        }
        /**
         * @param cRMID cRMID
         */
        public void setCRMID(String cRMID) {
            CRMID = cRMID;
        }
        /**
         * @return sTATUS
         */
        public Long getSTATUS() {
            return STATUS;
        }
        /**
         * @param sTATUS sTATUS
         */
        public void setSTATUS(Long sTATUS) {
            STATUS = sTATUS;
        }
        /**
         * @return lOG
         */
        public String getLOG() {
            return LOG;
        }
        /**
         * @param lOG lOG
         */
        public void setLOG(String lOG) {
            LOG = lOG;
        }
        /**
         * @return eRR_DATE
         */
        public Date getERR_DATE() {
            return ERR_DATE;
        }
        /**
         * @param eRR_DATE eRR_DATE
         */
        public void setERR_DATE(Date eRR_DATE) {
            ERR_DATE = eRR_DATE;
        }
        @Override
        public int compareTo(FbSpoLogStatusJPA o) {
            return ERR_DATE.compareTo(o.getERR_DATE());
        }
        
}
