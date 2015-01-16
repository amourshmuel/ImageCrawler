/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amour.imagecrawler.dal;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author amour
 */
@Entity
@Table(name = "IMAGES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Images.findAll", query = "SELECT i FROM Images i"),
    @NamedQuery(name = "Images.findById", query = "SELECT i FROM Images i WHERE i.id = :id"),
    @NamedQuery(name = "Images.findByDownloadtime", query = "SELECT i FROM Images i WHERE i.downloadtime = :downloadtime"),
    @NamedQuery(name = "Images.findByImagechecksum", query = "SELECT i FROM Images i WHERE i.imagechecksum = :imagechecksum")})
public class Images implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    @Basic(optional = false)
    @Column(name = "DOWNLOADTIME")
    @Temporal(TemporalType.DATE)
    private Date downloadtime;
    @Basic(optional = false)
    @Lob
    @Column(name = "IMAGEURL")
    private byte[] imageurl;
    @Basic(optional = false)
    @Lob
    @Column(name = "IMAGEONDISK")
    private byte[] imageondisk;
    @Basic(optional = false)
    @Column(name = "IMAGECHECKSUM")
    private String imagechecksum;

    public Images() {
        this.id =UUID.randomUUID().toString();
    }

    public Images(String id) {
        this.id = id;
    }

    public Images(String id, Date downloadtime, byte[] imageurl, byte[] imageondisk, String imagechecksum) {
        this.id = id;
        this.downloadtime = downloadtime;
        this.imageurl = imageurl;
        this.imageondisk = imageondisk;
        this.imagechecksum = imagechecksum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDownloadtime() {
        return downloadtime;
    }

    public void setDownloadtime(Date downloadtime) {
        this.downloadtime = downloadtime;
    }

    public byte[] getImageurl() {
        return imageurl;
    }

    public void setImageurl(byte[] imageurl) {
        this.imageurl = imageurl;
    }

    public byte[] getImageondisk() {
        return imageondisk;
    }

    public void setImageondisk(byte[] imageondisk) {
        this.imageondisk = imageondisk;
    }

    public String getImagechecksum() {
        return imagechecksum;
    }

    public void setImagechecksum(String imagechecksum) {
        this.imagechecksum = imagechecksum;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Images)) {
            return false;
        }
        Images other = (Images) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Images[ id=" + id + " ]";
    }
    
}
