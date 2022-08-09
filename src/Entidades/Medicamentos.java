/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author APRENDIZ
 */
@Entity
@Table(name = "medicamentos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Medicamentos.findAll", query = "SELECT m FROM Medicamentos m")
    , @NamedQuery(name = "Medicamentos.findByCodigo", query = "SELECT m FROM Medicamentos m WHERE m.codigo = :codigo")
    , @NamedQuery(name = "Medicamentos.findByNombre", query = "SELECT m FROM Medicamentos m WHERE m.nombre = :nombre")
    , @NamedQuery(name = "Medicamentos.findByCantidad", query = "SELECT m FROM Medicamentos m WHERE m.cantidad = :cantidad")
    , @NamedQuery(name = "Medicamentos.findByPrecio", query = "SELECT m FROM Medicamentos m WHERE m.precio = :precio")
    , @NamedQuery(name = "Medicamentos.findByFechaCaducidad", query = "SELECT m FROM Medicamentos m WHERE m.fechaCaducidad = :fechaCaducidad")
    , @NamedQuery(name = "Medicamentos.findByDisponible", query = "SELECT m FROM Medicamentos m WHERE m.disponible = :disponible")})
public class Medicamentos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "cantidad")
    private String cantidad;
    @Basic(optional = false)
    @Column(name = "precio")
    private String precio;
    @Basic(optional = false)
    @Column(name = "fecha_caducidad")
    @Temporal(TemporalType.DATE)
    private Date fechaCaducidad;
    @Basic(optional = false)
    @Column(name = "disponible")
    private String disponible;
    @JoinColumn(name = "laboratorio_codigo", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Laboratorio laboratorioCodigo;
    @JoinColumn(name = "presentacion_codigo", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Presentacion presentacionCodigo;
    @JoinColumn(name = "sitio_codigo", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Sitio sitioCodigo;

    public Medicamentos() {
    }

    public Medicamentos(Integer codigo) {
        this.codigo = codigo;
    }

    public Medicamentos(Integer codigo, String nombre, String cantidad, String precio, Date fechaCaducidad, String disponible) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.fechaCaducidad = fechaCaducidad;
        this.disponible = disponible;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getDisponible() {
        return disponible;
    }

    public void setDisponible(String disponible) {
        this.disponible = disponible;
    }

    public Laboratorio getLaboratorioCodigo() {
        return laboratorioCodigo;
    }

    public void setLaboratorioCodigo(Laboratorio laboratorioCodigo) {
        this.laboratorioCodigo = laboratorioCodigo;
    }

    public Presentacion getPresentacionCodigo() {
        return presentacionCodigo;
    }

    public void setPresentacionCodigo(Presentacion presentacionCodigo) {
        this.presentacionCodigo = presentacionCodigo;
    }

    public Sitio getSitioCodigo() {
        return sitioCodigo;
    }

    public void setSitioCodigo(Sitio sitioCodigo) {
        this.sitioCodigo = sitioCodigo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Medicamentos)) {
            return false;
        }
        Medicamentos other = (Medicamentos) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Medicamentos[ codigo=" + codigo + " ]";
    }

    
    
}
