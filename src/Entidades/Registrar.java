/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author APRENDIZ
 */
@Entity
@Table(name = "registrar")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Registrar.findAll", query = "SELECT r FROM Registrar r")
    , @NamedQuery(name = "Registrar.findByCedula", query = "SELECT r FROM Registrar r WHERE r.cedula = :cedula")
    , @NamedQuery(name = "Registrar.findByNombre", query = "SELECT r FROM Registrar r WHERE r.nombre = :nombre")
    , @NamedQuery(name = "Registrar.findByDireccion", query = "SELECT r FROM Registrar r WHERE r.direccion = :direccion")
    , @NamedQuery(name = "Registrar.findByClave", query = "SELECT r FROM Registrar r WHERE r.clave = :clave")})
public class Registrar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "cedula")
    private Integer cedula;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "direccion")
    private String direccion;
    @Basic(optional = false)
    @Column(name = "clave")
    private String clave;

    public Registrar() {
    }

    public Registrar(Integer cedula) {
        this.cedula = cedula;
    }

    public Registrar(Integer cedula, String nombre, String direccion, String clave) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.direccion = direccion;
        this.clave = clave;
    }

    public Integer getCedula() {
        return cedula;
    }

    public void setCedula(Integer cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cedula != null ? cedula.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Registrar)) {
            return false;
        }
        Registrar other = (Registrar) object;
        if ((this.cedula == null && other.cedula != null) || (this.cedula != null && !this.cedula.equals(other.cedula))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Registrar[ cedula=" + cedula + " ]";
    }
    
}
